package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.DBConfig;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class DBInit {
	private static DBInit instance = null;
	protected static DBConfig dbConfig = null;
	protected static final String tmp_sign_server = "tmp_signServer";
	protected static final String tmp_sign_client = "tmp_signClient";

	protected DBInit() throws DBException {
		try {
			Class.forName(dbConfig.getDriverClass()).newInstance();
			try {
				Connection localConnection = DriverManager.getConnection(
						dbConfig.getURL().trim(), dbConfig.getUser().trim(), dbConfig
								.getPassword().trim());
				localConnection.close();
			} catch (SQLException localSQLException) {
				throw new DBException("8062", "连接数据库失败", localSQLException);
			}
		} catch (DBException localDBException) {
			throw localDBException;
		} catch (ClassNotFoundException localClassNotFoundException) {
			throw new DBException("8088", "数据库驱动类找不到",
					localClassNotFoundException);
		} catch (Exception localException) {
			throw new DBException("8089", "获取配置信息失败", localException);
		}
	}

	public static synchronized DBInit getInstance() throws DBException {
		if (instance == null)
			try {
				try {
					dbConfig = DBConfig.getInstance();
				} catch (IDAException localIDAException) {
					throw new DBException("8001", "数据库连接信息不正确",
							localIDAException);
				}
				String str1 = dbConfig.getURL();
				int i = str1.indexOf(":");
				if (i < 1)
					throw new DBException("8052", "连接数据库的URL不正确");
				str1 = str1.substring(i + 1);
				int j = str1.indexOf(":");
				if (j < 1)
					throw new DBException("8052", "连接数据库的URL不正确");
				String str2 = str1.substring(0, j);
				if (str2.equalsIgnoreCase("oracle"))
					instance = new OracleInit();
				else if (str2.equalsIgnoreCase("mysql"))
					instance = new MySQLInit();
				else if (str2.equalsIgnoreCase("microsoft"))
					instance = new MSSQLInit();
				else if (str2.equals("db2"))
					instance = new DB2Init();
				else if (str2.equals("sybase"))
					instance = new SybaseInit();
				else
					instance = new DB2Init();
			} catch (DBException localDBException) {
				instance = null;
				throw localDBException;
			}
		return instance;
	}

	public static void close() {
		instance = null;
	}

	public void createDB(String paramString1, String paramString2)
			throws DBException {
	}

	public void setConfig(String paramString1, String paramString2,
			String paramString3, String paramString4) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		try {
			String str = "select count(modulename) from config where modulename=? and property=?";
			localConnection = DriverManager.getConnection(dbConfig.getURL(),
					dbConfig.getUser(), dbConfig.getPassword());
			localPreparedStatement = localConnection.prepareStatement(str);
			localPreparedStatement.setString(1, paramString1);
			localPreparedStatement.setString(2, paramString2);
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			if (localResultSet.getInt(1) == 0) {
				str = "insert into config values(?,?,?,?,?,?)";
				localPreparedStatement = localConnection.prepareStatement(str);
				localPreparedStatement.setString(1, paramString1);
				localPreparedStatement.setString(2, paramString2);
				localPreparedStatement.setString(3, paramString3);
				localPreparedStatement.setString(4, paramString4);
				localPreparedStatement.setString(5, "tmp_signServer");
				localPreparedStatement.setString(6, "tmp_signClient");
				localPreparedStatement.execute();
			} else if (localResultSet.getInt(1) == 1) {
				str = "update config set value=?,isencrypted=? where modulename=? and property=?";
				localPreparedStatement = localConnection.prepareStatement(str);
				localPreparedStatement.setString(1, paramString3);
				localPreparedStatement.setString(2, paramString4);
				localPreparedStatement.setString(3, paramString1);
				localPreparedStatement.setString(4, paramString2);
				localPreparedStatement.execute();
			}
		} catch (SQLException localSQLException1) {
			throw new DBException("8084", "数据库异常："
					+ localSQLException1.getMessage(), localSQLException1);
		} finally {
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException2) {
				}
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException3) {
				}
		}
	}

	public void saveCACert(CACertInfo paramCACertInfo) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		Object localObject1 = null;
		try {
			localConnection = DriverManager.getConnection(dbConfig.getURL(),
					dbConfig.getUser(), dbConfig.getPassword());
			String str = "insert into rootcert values(?,?,?,?,?,?,?,?,?,?,?)";
			localPreparedStatement = localConnection.prepareStatement(str);
			localPreparedStatement.setString(1, paramCACertInfo.getSN());
			localPreparedStatement.setInt(2, paramCACertInfo.getCA_ID());
			localPreparedStatement.setString(3, paramCACertInfo.getCA_DESC());
			localPreparedStatement.setString(4, paramCACertInfo.getSubject()
					.toUpperCase());
			localPreparedStatement
					.setString(5, paramCACertInfo.getPrivateKey());
			localPreparedStatement
					.setString(6, paramCACertInfo.getCertStatus());
			ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(
					paramCACertInfo.getCertEntity());
			localPreparedStatement.setBinaryStream(7,
					localByteArrayInputStream,
					paramCACertInfo.getCertEntity().length);
			localPreparedStatement.setString(8, paramCACertInfo.getDeviceID());
			localPreparedStatement.setString(9, paramCACertInfo.getRemark());
			localPreparedStatement.setString(10, "tmp_signServer");
			localPreparedStatement.setString(11, "tmp_signClient");
			localPreparedStatement.execute();
		} catch (Exception localException) {
			throw new DBException("8082", "数据库异常："
					+ localException.getMessage(), localException);
		} finally {
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException1) {
				}
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException2) {
				}
		}
	}

	public Vector getCACert() throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		CACertInfo localCACertInfo = null;
		Vector localVector1 = null;
		try {
			localConnection = DriverManager.getConnection(dbConfig.getURL(),
					dbConfig.getUser(), dbConfig.getPassword());
			String str = "select * from rootcert";
			localPreparedStatement = localConnection.prepareStatement(str);
			localResultSet = localPreparedStatement.executeQuery();
			localVector1 = new Vector();
			while (localResultSet.next()) {
				localCACertInfo = new CACertInfo();
				localCACertInfo.setSN(localResultSet.getString(1));
				localCACertInfo.setCA_ID(localResultSet.getInt(2));
				localCACertInfo.setCA_DESC(localResultSet.getString(3));
				localCACertInfo.setSubjectREF(localResultSet.getString(4));
				localCACertInfo.setPrivateKey(localResultSet.getString(5));
				localCACertInfo.setCertStatus(localResultSet.getString(6));
				localCACertInfo.setCertEntity(localResultSet.getBytes(7));
				localCACertInfo.setDeviceID(localResultSet.getString(8));
				localCACertInfo.setRemark(localResultSet.getString(9));
				localVector1.add(localCACertInfo);
			}
			return localVector1;
		} catch (Exception localException) {
			throw new DBException("8083", "数据库异常："
					+ localException.getMessage(), localException);
		} finally {
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException1) {
				}
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException2) {
				}
		}
	}


	public void initPrivilege() throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		int i = 0;
		String str = "";
		try {
			String[][] arrayOfString = { { "CERTREQUST", "申请证书" },
					{ "CERTDOWNLOAD", "下载证书" }, { "CERTREQDOWN", "申请并下载证书" },
					{ "CERTHOLD", "冻结证书" }, { "CERTUNHOLD", "解冻证书" },
					{ "CERTCARECOVERKEY", "恢复密钥" },
					{ "CERTUPDATERETAINKEY", "更新证书保持密钥" },
					{ "CERTUPDATE", "更新证书" }, { "CERTUPDDOWN", "更新并下载证书" },
					{ "CERTREVOKE", "注销证书" }, { "AUTHCODEUPDATE", "更新授权码" },
					{ "CERTQUERY", "查询证书" }, { "CERTUPDATEQUERY", "查询更新证书" },
					{ "CERTENTITYQUERY", "查询证书实体" }, { "CTMLGET", "浏览模板" },
					{ "CTMLCREATE", "添加模板" }, { "CTMLMODIFY", "修改模板" },
					{ "CTMLDELETE", "删除模板" }, { "CTMLREVOKE", "注销模板" },
					{ "CTMLSELFEXTGET", "浏览自定义扩展" },
					{ "CTMLSELFEXTCREATE", "添加自定义扩展" },
					{ "CTMLSELFEXTMODIFY", "修改自定义扩展" },
					{ "CTMLSELFEXTDELETE", "删除自定义扩展" },
					{ "CTMLSELFEXTREVOKE", "注销自定义扩展" },
					{ "PRIVILEGEGETADMINLIST", "查询管理员证书列表" },
					{ "PRIVILEGEGETROLELIST", "查询角色列表" },
					{ "PRIVILEGESETADMIN", "修改管理员角色权限" },
					{ "PRIVILEGESETTEMPLATEADMIN", "修改管理员证书业务权限" },
					{ "PRIVILEGEGETADMINROLES", "查询管理员角色权限" },
					{ "PRIVILEGEGETTEMPLATEADMIN", "查询管理员证书业务权限" },
					{ "AUDITQUERYLOG", "日志查询" },
					{ "AUDITCOUNTCERT", "证书数量统计" },
					{ "AUDITARCHIVELOG", "日志归档" },
					{ "AUDITQUERYARCHIVELOG", "查询归档日志" },
					{ "SUPERARCHIVECERT", "证书归档" },
					{ "SUPERQUERYARCHIVECERT", "查询归档证书" } };
			str = "insert into privilege values(?,?,?,?)";
			localConnection = DriverManager.getConnection(dbConfig.getURL(),
					dbConfig.getUser(), dbConfig.getPassword());
			localPreparedStatement = localConnection.prepareStatement(str);
			for (int j = 0; j < arrayOfString.length; j++) {
				localPreparedStatement.setString(1, arrayOfString[j][0]);
				localPreparedStatement.setString(2, arrayOfString[j][1]);
				localPreparedStatement.setString(3, "tmp_signServer");
				localPreparedStatement.setString(4, "tmp_signClient");
				localPreparedStatement.execute();
			}
			i = arrayOfString.length;
		} catch (SQLException localSQLException1) {
			throw new DBException("8085", "数据库异常："
					+ localSQLException1.getMessage(), localSQLException1);
		} finally {
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException2) {
				}
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException3) {
				}
		}
	}

	public void initRole() throws DBException {
		Vector localVector = null;
		DBManager localDBManager = null;
		try {
			localDBManager = DBManager.getInstance();
			String str = "证书管理角色";
			localVector = new Vector();
			localVector.add("CERTREQUST");
			localVector.add("CERTDOWNLOAD");
			localVector.add("CERTREQDOWN");
			localVector.add("CERTHOLD");
			localVector.add("CERTUNHOLD");
			localVector.add("CERTCARECOVERKEY");
			localVector.add("CERTUPDATERETAINKEY");
			localVector.add("CERTUPDATE");
			localVector.add("CERTUPDDOWN");
			localVector.add("CERTREVOKE");
			localVector.add("AUTHCODEUPDATE");
			localVector.add("CERTQUERY");
			localVector.add("CERTUPDATEQUERY");
			localVector.add("CERTENTITYQUERY");
			localDBManager.setRoleInfo(str, localVector);
			str = "模板管理角色";
			localVector = new Vector();
			localVector.add("CTMLGET");
			localVector.add("CTMLCREATE");
			localVector.add("CTMLMODIFY");
			localVector.add("CTMLDELETE");
			localVector.add("CTMLREVOKE");
			localVector.add("CTMLSELFEXTGET");
			localVector.add("CTMLSELFEXTCREATE");
			localVector.add("CTMLSELFEXTMODIFY");
			localVector.add("CTMLSELFEXTDELETE");
			localVector.add("CTMLSELFEXTREVOKE");
			localDBManager.setRoleInfo(str, localVector);
			str = "授权管理角色";
			localVector = new Vector();
			localVector.add("PRIVILEGESETADMIN");
			localVector.add("PRIVILEGESETTEMPLATEADMIN");
			localVector.add("PRIVILEGEGETTEMPLATEADMIN");
			localVector.add("PRIVILEGEGETADMINLIST");
			localVector.add("PRIVILEGEGETROLELIST");
			localVector.add("PRIVILEGEGETADMINROLES");
			localDBManager.setRoleInfo(str, localVector);
			str = "审计管理角色";
			localVector = new Vector();
			localVector.add("AUDITQUERYLOG");
			localVector.add("AUDITCOUNTCERT");
			localVector.add("AUDITARCHIVELOG");
			localVector.add("AUDITQUERYARCHIVELOG");
			localDBManager.setRoleInfo(str, localVector);
			str = "超级管理员角色";
			localVector = new Vector();
			localVector.add("SUPERARCHIVECERT");
			localVector.add("SUPERQUERYARCHIVECERT");
			localDBManager.setRoleInfo(str, localVector);
		} catch (DBException localDBException) {
			throw localDBException;
		}
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.initserver.DBInit JD-Core Version: 0.6.0
 */