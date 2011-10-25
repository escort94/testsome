package cn.com.jit.ida.ca.dbutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.DBConfig;
import cn.com.jit.ida.privilege.Admin;

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
		if(null != state){
			try {
				state.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * the method is called update but actually is insert means
	 * @param sn
	 * @param dn
	 * @param adminType
	 * @throws IDAException
	 * @throws SQLException
	 */
	public static void updateConfig(String sn, String dn, int adminType)
			throws IDAException, SQLException {
		Connection conn = getConnection();
		String sql = "insert into config values(?,?,?,?,?,?)";
		PreparedStatement localPreparedStatement = conn.prepareStatement(sql);
		//wether ,delete the update admin,because system is not allow one person hava two identity
		String delsql = "delete from config where value = ?";
		localPreparedStatement.setString(1, sn);
		localPreparedStatement.execute();
		localPreparedStatement.setString(1, dn);
		localPreparedStatement.execute();
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
		this.closeStatement(localPreparedStatement);
		this.closeConnection(conn);
	}
}
