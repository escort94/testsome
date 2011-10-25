package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.ca.config.DBConfig;
import cn.com.jit.ida.ca.db.DBException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import oracle.jdbc.OracleResultSet;
import oracle.sql.BLOB;

public class OracleInit extends DBInit {
	OracleInit() throws DBException {
	}

	public void createDB(String paramString1, String paramString2)
			throws DBException {
		Connection localConnection = null;
		Statement localStatement = null;
		String str1 = "";
		String str2 = "";
		FileReader localFileReader = null;
		BufferedReader localBufferedReader = null;
		try {
			String str3 = "./config/db/oracle/tables.sql";
			File localFile = new File(str3);
			try {
				localFileReader = new FileReader(localFile);
				localBufferedReader = new BufferedReader(localFileReader);
			} catch (FileNotFoundException localFileNotFoundException) {
				throw new DBException("8086", "下列文件不存在：" + str3);
			}
			localConnection = DriverManager.getConnection(dbConfig.getURL(),
					dbConfig.getUser(), dbConfig.getPassword());
			localStatement = localConnection.createStatement();
			while ((str2 = localBufferedReader.readLine()) != null) {
				str2 = str2.trim();
				if ((str2.equals("")) || (str2.substring(0, 1).equals("#")))
					continue;
				if (str2.equals("/")) {
					try {
						localStatement.execute(str1);
					} catch (SQLException localSQLException1) {
						if (localSQLException1.getErrorCode() != 942)
							throw new DBException("8081", "数据库异常："
									+ localSQLException1.getMessage());
					}
					str1 = "";
					continue;
				}
				str1 = str1 + " " + str2;
			}
			// 初始化 权限与角色
			// initPrivilege();
			// initRole();
		} catch (IOException localIOException1) {
			throw new DBException("8081", "建库脚本文件读取失败", localIOException1);
		} catch (SQLException localSQLException2) {
			throw new DBException("8081", "数据库异常："
					+ localSQLException2.getMessage(), localSQLException2);
		} finally {
			if (localFileReader != null)
				try {
					localFileReader.close();
				} catch (IOException localIOException2) {
				}
			if (localBufferedReader != null)
				try {
					localBufferedReader.close();
				} catch (IOException localIOException3) {
				}
			if (localStatement != null)
				try {
					localStatement.close();
				} catch (SQLException localSQLException3) {
				}
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException4) {
				}
		}
	}

	public void saveCACert(CACertInfo paramCACertInfo) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		OutputStream localOutputStream = null;
		try {
			localConnection = DriverManager.getConnection(dbConfig.getURL(),
					dbConfig.getUser(), dbConfig.getPassword());
			localConnection.setAutoCommit(false);
			String str = "insert into rootcert values(?,?,?,?,?,?,empty_blob(),?,?,?,?)";
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
			localPreparedStatement.setString(7, paramCACertInfo.getDeviceID());
			localPreparedStatement.setString(8, paramCACertInfo.getRemark());
			localPreparedStatement.setString(9, "tmp_signServer");
			localPreparedStatement.setString(10, "tmp_signClient");
			localPreparedStatement.execute();
			str = "select certentity from rootcert where sn=?";
			localPreparedStatement = localConnection.prepareStatement(str);
			localPreparedStatement.setString(1, paramCACertInfo.getSN());
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			BLOB localBLOB = ((OracleResultSet) localResultSet).getBLOB(1);
			localOutputStream = localBLOB.getBinaryOutputStream();
			byte[] arrayOfByte = paramCACertInfo.getCertEntity();
			localOutputStream.write(arrayOfByte);
			localOutputStream.flush();
			localOutputStream.close();
			localConnection.commit();
		} catch (Exception localException) {
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException1) {
				}
			throw new DBException("8082", "数据库异常："
					+ localException.getMessage(), localException);
		} finally {
			if (localOutputStream != null)
				try {
					localOutputStream.close();
				} catch (IOException localIOException) {
				}
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

	public Vector getCACert() throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		BLOB localBLOB = null;
		CACertInfo localCACertInfo = null;
		Vector localVector = null;
		try {
			localConnection = DriverManager.getConnection(dbConfig.getURL(),
					dbConfig.getUser(), dbConfig.getPassword());
			String str = "select * from rootcert";
			localPreparedStatement = localConnection.prepareStatement(str);
			localResultSet = localPreparedStatement.executeQuery();
			localVector = new Vector();
			while (localResultSet.next()) {
				localCACertInfo = new CACertInfo();
				if (localResultSet.getString(1) != null)
					localCACertInfo.setSN(localResultSet.getString(1));
				if (localResultSet.getInt(2) > 0)
					localCACertInfo.setCA_ID(localResultSet.getInt(2));
				if (localResultSet.getString(3) != null)
					localCACertInfo.setCA_DESC(localResultSet.getString(3));
				if (localResultSet.getString(4) != null)
					localCACertInfo.setSubjectREF(localResultSet.getString(4));
				if (localResultSet.getString(5) != null)
					localCACertInfo.setPrivateKey(localResultSet.getString(5));
				if (localResultSet.getString(6) != null)
					localCACertInfo.setCertStatus(localResultSet.getString(6));
				localBLOB = ((OracleResultSet) localResultSet).getBLOB(7);
				localObject1 = new BufferedInputStream(localBLOB
						.getBinaryStream());
				byte[] arrayOfByte = new byte[(int) localBLOB.length()];
				((BufferedInputStream) localObject1).read(arrayOfByte);
				((BufferedInputStream) localObject1).close();
				if (arrayOfByte != null)
					localCACertInfo.setCertEntity(arrayOfByte);
				if (localResultSet.getString(8) != null)
					localCACertInfo.setDeviceID(localResultSet.getString(8));
				if (localResultSet.getString(9) != null)
					localCACertInfo.setRemark(localResultSet.getString(9));
				localVector.add(localCACertInfo);
			}
			localObject1 = localVector;
		} catch (Exception localException) {
			Object localObject1;
			throw new DBException("8083", "数据库异常："
					+ localException.getMessage(), localException);
		} finally {
			if (localBLOB != null)
				try {
					localBLOB.close();
				} catch (SQLException localSQLException1) {
				}
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
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.initserver.OracleInit JD-Core Version: 0.6.0
 */