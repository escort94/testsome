package cn.com.jit.ida.ca.displayrelated;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.DBConfig;
import cn.com.jit.ida.ca.exception.OperateException;
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
	 * @throws SQLException
	 */
	public static void updateConfig(String sn, String dn, int adminType)
			throws IDAException {
		sn = sn.toUpperCase();
		dn = dn.toUpperCase();
		Connection conn = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);

			// wether ,delete the update admin,because system is not allow one
			// person hava two identity
			String delconfigsql = "delete from config where value = ?";
			PreparedStatement delconfigPStatement = conn
					.prepareStatement(delconfigsql);
			delconfigPStatement.setString(1, sn);
			delconfigPStatement.execute();
			delconfigPStatement.setString(1, dn);
			delconfigPStatement.execute();
			if (null != delconfigPStatement) {
				delconfigPStatement.close();
			}

			String deladminsql = "delete from admin where certsn = ?";
			PreparedStatement delAdminPStatement = conn
					.prepareStatement(deladminsql);
			delAdminPStatement.setString(1, sn);
			delAdminPStatement.execute();
			if (null != delAdminPStatement) {
				delAdminPStatement.close();
			}
			//TODO
			//★★★must be modify in the futher,role_id and tablename ★★★
			String insertAdminsql = "insert into admin values(?,?,?,?)";
			PreparedStatement insertAdminPStatement = conn
					.prepareStatement(insertAdminsql);
			insertAdminPStatement.setString(1, sn);
			insertAdminPStatement.setString(2, String.valueOf(adminType));
			insertAdminPStatement.setString(3, "temp_client");
			insertAdminPStatement.setString(4, "temp_server");
			insertAdminPStatement.execute();
			if (null != insertAdminPStatement) {
				insertAdminPStatement.close();
			}
			
			String insertConfigsql = "insert into config values(?,?,?,?,?,?)";
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
