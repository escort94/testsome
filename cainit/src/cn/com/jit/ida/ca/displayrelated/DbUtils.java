package cn.com.jit.ida.ca.displayrelated;

import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.DBConfig;
import cn.com.jit.ida.ca.db.TableNames;
import cn.com.jit.ida.ca.exception.OperateException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.privilege.Admin;

/**
 * 主要用于除初始化功能外，提供数据库操作的力所能及的方法 由于DBManager类过于庞大臃肿与混乱，所以在这里增加一些目的清晰的
 * 重要的是写了我就知道他的存在了的方法
 * 
 * @author kmc
 * 
 */
public class DbUtils {
	public static Connection getConnection() throws IDAException, SQLException {
		DBConfig dbConfig = DBConfig.getInstance();
		return DriverManager.getConnection(dbConfig.getURL(), dbConfig
				.getUser(), dbConfig.getPassword());
	}

	public String encode(String pwd) {
		BASE64Encoder encode = new BASE64Encoder();
		return encode.encode(pwd.getBytes());
	}

	public String decode(String pwd) throws IOException {
		BASE64Decoder decode = new BASE64Decoder();
		return new String(decode.decodeBuffer(pwd));
	}

	public static void insertSysAdminPwd() throws IDAException {
		String pwd = ConfigTool.getNewPassword("请设置系统管理员密码", 6, 16, true);
		try {
			Connection conn = getConnection();
			String insertAdminsql = "insert into " + TableNames.TCA_CONFIG
					+ " values(?,?,?,?,?,?)";
			PreparedStatement statement = conn.prepareStatement(insertAdminsql);
			statement.setString(1, "CAConfig");
			statement.setString(2, "SysAdminPwd");
			statement.setString(3, pwd);
			statement.setString(4, "N");
			statement.setString(5, "tmp_signServer");
			statement.setString(6, "tmp_signClient");
			statement.execute();
		} catch (SQLException e) {
			OperateException oexception = new OperateException(
					OperateException.INSERT_SYSADMINPWD_ERROR,
					OperateException.INSERT_SYSADMINPWD_ERROR_DES);
			throw oexception;
		}
	}

	public static String getSysPwd() throws OperateException {
		try {
			Connection conn = getConnection();
			String insertAdminsql = "select value from "
					+ TableNames.TCA_CONFIG + " where property = 'SysAdminPwd'";
			Statement statement = conn.createStatement();
			ResultSet set = statement.executeQuery(insertAdminsql);
			while (set.next()) {
				String pwd = set.getString(1);
				return pwd;
			}
		} catch (Exception e) {
			OperateException oexception = new OperateException(
					OperateException.GET_SYSADMINPWD_ERROR,
					OperateException.GET_SYSADMINPWD_ERROR_DES);
			throw oexception;
		}
		return null;
	}

	public static boolean updateSysAdminPwd() throws IDAException {
		String pwd_old = ConfigTool.getPassword("请输入系统管理员密码", 6, 16);
		if (pwd_old == null) {
			return false;
		}
		while (!pwd_old.equals(getSysPwd())) {
			System.out.println("系统管理员密码错误...");
			pwd_old = ConfigTool.getPassword("请输入系统管理员密码", 6, 16);
			if (pwd_old == null) {
				return false;
			}
		}
		String pwd_new = ConfigTool.getNewPassword("请输入新的系统管理员密码", 6, 16);
		if (pwd_new == null) {
			return false;
		}
		try {
			Connection conn = getConnection();
			String insertAdminsql = "update " + TableNames.TCA_CONFIG
					+ " set value = ? where property ='SysAdminPwd'";
			PreparedStatement statement = conn.prepareStatement(insertAdminsql);
			statement.setString(1, pwd_new);
			statement.execute();
			return true;
		} catch (SQLException e) {
			OperateException oexception = new OperateException(
					OperateException.UPDATE_SYSADMINPWD_ERROR,
					OperateException.UPDATE_SYSADMINPWD_ERROR_DES);
			throw oexception;
		}
	}

	public static void closeConnection(Connection conn) {
		if (null != conn) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeStatement(Statement state) {
		if (null != state) {
			try {
				state.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * the method is called update but actually is insert means
	 * 
	 * @param sn
	 * @param dn
	 * @param adminType
	 * @throws IDAException
	 * @throws CertificateEncodingException 
	 * @throws  
	 * @throws SQLException
	 */
	public static void updateConfig(String sn, String dn, int adminType,
			X509Certificate cert) throws IDAException, CertificateEncodingException {
		String keytype = null;
		int keylength = 0;
		Timestamp after = null;
		Timestamp before = null;
		try {
			after = new Timestamp(cert.getNotAfter().getTime());
			before = new Timestamp(cert.getNotBefore().getTime());
			Key publicKey = cert.getPublicKey();
			keytype = publicKey.getAlgorithm();
			if (keytype.equalsIgnoreCase("RSA")) {
				KeyFactory keyFact;

				keyFact = KeyFactory.getInstance(keytype);

				RSAPublicKeySpec keySpec = (RSAPublicKeySpec) keyFact
						.getKeySpec(publicKey, RSAPublicKeySpec.class);
				BigInteger modulus = keySpec.getModulus();
				keylength = modulus.toString(2).length();
			} else {
				keylength = 256;
			}
		} catch (Exception e) {
			OperateException kException = new OperateException(
					OperateException.SET_ADMIN_AUTHENTICATION_ERROR,
					OperateException.SET_ADMIN_AUTHENTICATION_ERROR, e);
			throw kException;
		}
		sn = sn.toUpperCase();
		Connection conn = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);

			// wether ,delete the update admin,because system is not allow one
			// person hava two identity
			String delconfigsql = "delete from " + TableNames.TCA_CONFIG
					+ " where value = ?";
			PreparedStatement delconfigPStatement = conn
					.prepareStatement(delconfigsql);
			delconfigPStatement.setString(1, sn);
			delconfigPStatement.execute();
			delconfigPStatement.setString(1, dn);
			delconfigPStatement.execute();
			if (null != delconfigPStatement) {
				delconfigPStatement.close();
			}

			String deladminsql = "delete from " + TableNames.TCA_ADMIN
					+ " where cert_sn = ?";
			PreparedStatement delAdminPStatement = conn
					.prepareStatement(deladminsql);
			delAdminPStatement.setString(1, sn);
			delAdminPStatement.execute();
			if (null != delAdminPStatement) {
				delAdminPStatement.close();
			}
			// TODO
			// ★★★must be modify in the futher,role_id and tablename ★★★
			String insertAdminsql = "insert into " + TableNames.TCA_ADMIN
					+ " values(?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement insertAdminPStatement = conn
					.prepareStatement(insertAdminsql);
			insertAdminPStatement.setString(1, sn);
			insertAdminPStatement.setString(2, dn);
			insertAdminPStatement.setObject(3, before);
			insertAdminPStatement.setObject(4, after);
			insertAdminPStatement.setObject(5, new Timestamp(new Date().getTime()));
			insertAdminPStatement.setString(6,
					new BASE64Encoder().encode(cert.getEncoded()));
			insertAdminPStatement.setString(7, keytype);
			insertAdminPStatement.setInt(8, keylength);
			insertAdminPStatement.setNull(9, Types.NUMERIC);
			insertAdminPStatement.setInt(10, adminType);
			insertAdminPStatement.execute();
			if (null != insertAdminPStatement) {
				insertAdminPStatement.close();
			}

			String insertConfigsql = "insert into " + TableNames.TCA_CONFIG
					+ " values(?,?,?,?,?,?)";
			PreparedStatement localPreparedStatement = conn
					.prepareStatement(insertConfigsql);
			if (adminType == Admin.AUDIT_ADMIN) {
				localPreparedStatement.setString(1, "CAConfig");
				localPreparedStatement.setString(2, "AuditAdminSN");
				localPreparedStatement.setString(3, sn);
				localPreparedStatement.setString(4, "N");
				localPreparedStatement.setString(5, "tmp_signServer");
				localPreparedStatement.setString(6, "tmp_signClient");
				localPreparedStatement.execute();
				localPreparedStatement.setString(1, "CAConfig");
				localPreparedStatement.setString(2, "AuditAdminDN");
				localPreparedStatement.setString(3, dn);
				localPreparedStatement.setString(4, "N");
				localPreparedStatement.setString(5, "tmp_signServer");
				localPreparedStatement.setString(6, "tmp_signClient");
				localPreparedStatement.execute();
			} else if (adminType == Admin.SUPER_ADMIN) {
				localPreparedStatement.setString(1, "CAConfig");
				localPreparedStatement.setString(2, "CAAdminSN");
				localPreparedStatement.setString(3, sn);
				localPreparedStatement.setString(4, "N");
				localPreparedStatement.setString(5, "tmp_signServer");
				localPreparedStatement.setString(6, "tmp_signClient");
				localPreparedStatement.execute();
				localPreparedStatement.setString(1, "CAConfig");
				localPreparedStatement.setString(2, "CAAdminDN");
				localPreparedStatement.setString(3, dn);
				localPreparedStatement.setString(4, "N");
				localPreparedStatement.setString(5, "tmp_signServer");
				localPreparedStatement.setString(6, "tmp_signClient");
				localPreparedStatement.execute();
			}
			conn.commit();
			closeStatement(localPreparedStatement);
			closeConnection(conn);
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				OperateException oexception = new OperateException(
						OperateException.UPDATE_ADMIN_TO_DATABASE_ROLLBACK_ERROR,
						OperateException.UPDATE_ADMIN_TO_DATABASE_ROLLBACK_ERROR_DES);
				throw oexception;
			}
			OperateException oexception = new OperateException(
					OperateException.UPDATE_ADMIN_TO_DATABASE_ERROR,
					OperateException.UPDATE_ADMIN_TO_DATABASE_ERROR_DES);
			throw oexception;
		}
	}
}
