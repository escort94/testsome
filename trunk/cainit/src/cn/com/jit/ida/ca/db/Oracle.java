package cn.com.jit.ida.ca.db;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import oracle.jdbc.OracleResultSet;
import oracle.sql.BLOB;
import oracle.sql.CLOB;
import cn.com.jit.ida.ca.certmanager.reqinfo.CRLInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertExtensions;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertRevokeInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.Extension;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTMLPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.StandardExtension;
import cn.com.jit.ida.log.Operation;

public class Oracle extends DBManager {
	Oracle() throws DBException {
	}

	public byte[] getTemplateInfo() {
		Connection localConnection = null;
		Statement localStatement = null;
		ResultSet localResultSet = null;
		Blob blob = null;
		byte[] bytes = null;
		BufferedInputStream is = null;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			String str1 = "select CT_XML from TCA_CERT_TEMPLATE where CT_ID = 1";
			localStatement = localConnection.createStatement();
			localResultSet = localStatement.executeQuery(str1);
			if (localResultSet.next()) {
				blob = localResultSet.getBlob(1);
			}
			try {
				is = new BufferedInputStream(blob.getBinaryStream());
				bytes = new byte[(int) blob.length()];
				int len = bytes.length;
				int offset = 0;
				int read = 0;
				while (offset < len
						&& (read = is.read(bytes, offset, len - offset)) >= 0) {
					offset += read;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (SQLException localSQLException1) {
			localSQLException1.printStackTrace();
		} finally {
			try {
				is.close();
				is = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (localStatement != null)
				try {
					localStatement.close();
				} catch (SQLException localSQLException2) {
				}
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException3) {
				}
		}
		return bytes;
	}

	public String getDBVersion() {
		Connection localConnection = null;
		Statement localStatement = null;
		ResultSet localResultSet = null;
		String str1 = "";
		String str2 = "";
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			str1 = "select * from v$version";
			localStatement = localConnection.createStatement();
			localResultSet = localStatement.executeQuery(str1);
			if (localResultSet.next())
				str2 = localResultSet.getString(1);
		} catch (SQLException localSQLException1) {
			str2 = "返回数据库版本失败:数据库访问异常";
		} finally {
			if (localStatement != null)
				try {
					localStatement.close();
				} catch (SQLException localSQLException2) {
				}
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException3) {
				}
		}
		return str2;
	}

	public void refrushCertAccount() throws DBException {
		Connection localConnection = null;
		Statement localStatement = null;
		ResultSet localResultSet = null;
		long l2 = 0L;
		String str = null;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			str = "select /*+index(cert pk_cert)*/count(certsn) from cert";
			localStatement = localConnection.createStatement();
			localResultSet = localStatement.executeQuery(str);
			localResultSet.next();
			long l1 = localResultSet.getLong(1);
			str = "select /*+index(cert pk_cert)*/count(certsn) from certarc";
			localResultSet = localStatement.executeQuery(str);
			localResultSet.next();
			l2 = localResultSet.getLong(1);
			setCertSUM(l1 + l2);
		} catch (SQLException localSQLException1) {
			throw new DBException("8009", "获取证书总数失败", localSQLException1);
		} finally {
			if (localStatement != null)
				try {
					localStatement.close();
				} catch (SQLException localSQLException2) {
				}
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException3) {
				}
		}
	}

	public int saveCTML(Properties paramProperties) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		Statement localStatement = null;
		ResultSet localResultSet = null;
		Writer localWriter = null;
		int str2 = -1;
		try {
			int i;
			if (paramProperties == null) {
				return str2;
			}
			localConnection = DriverManager.getConnection("proxool.ida");
			localConnection.setAutoCommit(false);
			String str1 = "insert into ctml values(?,?,?,?,?,empty_clob(),?,?,?,?)";
			localPreparedStatement = localConnection.prepareStatement(str1);
			localPreparedStatement.setString(1, paramProperties.getProperty(
					"ctml_name", null));
			localPreparedStatement.setString(2, paramProperties.getProperty(
					"ctml_id", null));
			localPreparedStatement.setString(3, paramProperties.getProperty(
					"ctml_type", null));
			localPreparedStatement.setString(4, paramProperties.getProperty(
					"ctml_status", null));
			localPreparedStatement.setString(5, paramProperties.getProperty(
					"ctml_description", null));
			localPreparedStatement.setString(6, paramProperties.getProperty(
					"reserve", null));
			localPreparedStatement.setLong(7, getTime());
			localPreparedStatement.setString(8, "tmp_signServer");
			localPreparedStatement.setString(9, "tmp_signClient");
			str2 = localPreparedStatement.executeUpdate();
			str1 = "select ctml_policyinfo from ctml where ctml_name=?";
			localPreparedStatement = localConnection.prepareStatement(str1);
			localPreparedStatement.setString(1, paramProperties.getProperty(
					"ctml_name", null));
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			CLOB localCLOB = ((OracleResultSet) localResultSet).getCLOB(1);
			localWriter = localCLOB.getCharacterOutputStream();
			char[] arrayOfChar = paramProperties.getProperty("ctml_policyinfo",
					"null").toCharArray();
			localWriter.write(arrayOfChar);
			localWriter.flush();
			localWriter.close();
			if (!paramProperties.getProperty("ctml_name").equals("0")) {
				String str3 = null;
				String str4 = null;
				str1 = "select value from config where modulename='CAConfig' and property='CAAdminSN'";
				localStatement = localConnection.createStatement();
				localResultSet = localStatement.executeQuery(str1);
				if (localResultSet.next())
					str3 = localResultSet.getString(1);
				X509V3CTMLPolicy localX509V3CTMLPolicy = new X509V3CTMLPolicy(
						paramProperties.getProperty("ctml_policyinfo")
								.getBytes());
				if ((localX509V3CTMLPolicy.getAttribute().attribute & 1L) != 0L) {
					str4 = null;
				} else {
					str1 = "select value from config where modulename='CAConfig' and property='BaseDN'";
					localStatement = localConnection.createStatement();
					localResultSet = localStatement.executeQuery(str1);
					if (localResultSet.next())
						str4 = localResultSet.getString(1);
				}
				if (str3 != null)
					str2 = setTemplateAdmin(localConnection, str3,
							paramProperties.getProperty("ctml_name", null),
							str4, null);
			}
			localConnection.commit();
			return str2;
		} catch (DBException localDBException) {
			String str3;
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException1) {
				}
			throw localDBException;
		} catch (Exception localException) {
			syslogger.info("数据库异常：" + localException.getMessage());
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException2) {
				}
			throw new DBException("8004", "增加模板失败", localException);
		} finally {
			if (localWriter != null)
				try {
					localWriter.close();
				} catch (IOException localIOException) {
				}
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException3) {
				}
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException4) {
				}
		}
	}

	public int modifyCTML(Properties paramProperties1,
			Properties paramProperties2) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		Writer localWriter = null;
		Enumeration localEnumeration = null;
		int i = 0;
		int localCLOB1 = -1;
		String[] arrayOfString = null;
		String str3 = null;
		try {
			if ((paramProperties1 == null) || (paramProperties2 == null)) {
				return localCLOB1;
			}
			localConnection = DriverManager.getConnection("proxool.ida");
			localConnection.setAutoCommit(false);
			String str1 = "update ctml set ";
			localEnumeration = paramProperties2.propertyNames();
			arrayOfString = new String[paramProperties1.size()
					+ paramProperties2.size()];
			String str2;
			while (localEnumeration.hasMoreElements()) {
				str2 = localEnumeration.nextElement().toString();
				if (str2.equalsIgnoreCase("ctml_policyinfo")) {
					str3 = paramProperties2.getProperty(str2);
					str1 = str1 + "ctml_policyinfo=empty_clob(),";
					continue;
				}
				arrayOfString[(i++)] = paramProperties2.getProperty(str2,
						"null");
				str1 = str1 + str2 + "=?,";
			}
			str1 = str1.substring(0, str1.length() - 1);
			localEnumeration = paramProperties1.propertyNames();
			if (localEnumeration.hasMoreElements())
				;
			for (str1 = str1 + " where "; localEnumeration.hasMoreElements(); str1 = str1
					+ str2 + "=? and ") {
				str2 = localEnumeration.nextElement().toString();
				arrayOfString[(i++)] = paramProperties1.getProperty(str2,
						"null");
			}
			str1 = str1.substring(0, str1.length() - 5);
			localPreparedStatement = localConnection.prepareStatement(str1
					.toLowerCase());
			for (int j = 0; j < i; j++)
				localPreparedStatement.setString(j + 1, arrayOfString[j]);
			localCLOB1 = localPreparedStatement.executeUpdate();
			if (localCLOB1 == 1) {
				if (str3 != null) {
					str1 = "select ctml_policyinfo from ctml";
					localEnumeration = paramProperties1.propertyNames();
					if (localEnumeration.hasMoreElements())
						str1 = str1 + " where ";
					i = 0;
					while (localEnumeration.hasMoreElements()) {
						str2 = localEnumeration.nextElement().toString();
						arrayOfString[(i++)] = paramProperties1.getProperty(
								str2, "null");
						str1 = str1 + str2 + "=? and ";
					}
					str1 = str1.substring(0, str1.length() - 5);
					localPreparedStatement = localConnection
							.prepareStatement(str1.toLowerCase());
					int j = 0;
					for (j = 0; j < i; j++)
						localPreparedStatement.setString(j + 1,
								arrayOfString[j]);
					localResultSet = localPreparedStatement.executeQuery();
					if (localResultSet.next()) {
						CLOB localCLOB2 = ((OracleResultSet) localResultSet)
								.getCLOB(1);
						localWriter = localCLOB2.getCharacterOutputStream();
						localWriter.write(str3.toCharArray());
						localWriter.flush();
						localWriter.close();
						if (localResultSet.next()) {
							localConnection.rollback();
							localCLOB1 = 0;
						} else {
							localConnection.commit();
							localCLOB1 = 1;
						}
					} else {
						localConnection.rollback();
						localCLOB1 = 0;
					}
				} else {
					localConnection.commit();
				}
			} else if (localCLOB1 > 1)
				localConnection.rollback();
			else
				localCLOB1 = 0;
			return localCLOB1;
		} catch (Exception localException) {
			CLOB localCLOB2;
			syslogger.info("数据库异常：" + localException.getMessage());
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException1) {
				}
			throw new DBException("8016", "修改模板失败", localException);
		} finally {
			if (localWriter != null)
				try {
					localWriter.close();
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

	public Properties[] getCTML(Properties paramProperties) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		Enumeration localEnumeration = null;
		CLOB localCLOB = null;
		BufferedReader localBufferedReader = null;
		Properties localProperties = null;
		Properties[] arrayOfProperties1 = null;
		String[] arrayOfString = null;
		String str1 = "";
		StringBuffer localStringBuffer = new StringBuffer();
		int i = 0;
		try {
			if (paramProperties == null) {
				return arrayOfProperties1;
			}
			str1 = "select ctml_name,ctml_id,ctml_type,ctml_status,ctml_description,ctml_policyinfo,reserve from ctml";
			localEnumeration = paramProperties.propertyNames();
			arrayOfString = new String[paramProperties.size()];
			if (localEnumeration.hasMoreElements())
				localStringBuffer.append(" where ");
			while (localEnumeration.hasMoreElements()) {
				String str2 = localEnumeration.nextElement().toString();
				arrayOfString[i] = paramProperties.getProperty(str2);
				if (str2.equalsIgnoreCase("ctml_name")) {
					if (arrayOfString[i].indexOf("*") >= 0) {
						arrayOfString[i] = arrayOfString[i].replace('*', '%');
						localStringBuffer.append("ctml_name like ? and ");
					} else {
						localStringBuffer.append("ctml_name=? and ");
					}
				} else
					localStringBuffer.append(str2 + "=? and ");
				i++;
			}
			if (i > 0)
				str1 = str1
						+ localStringBuffer.substring(0, localStringBuffer
								.length() - 5);
			str1 = str1 + " order by createtime desc";
			localConnection = DriverManager.getConnection("proxool.ida");
			localPreparedStatement = localConnection.prepareStatement(str1
					.toLowerCase());
			for (int j = 0; j < arrayOfString.length; j++)
				localPreparedStatement.setString(j + 1, arrayOfString[j]);
			localResultSet = localPreparedStatement.executeQuery();
			i = 0;
			Vector localVector = new Vector();
			while (localResultSet.next()) {
				localProperties = new Properties();
				String str3 = localResultSet.getString(1);
				if (str3 != null)
					localProperties.setProperty("ctml_name", str3);
				str3 = localResultSet.getString(2);
				if (str3 != null)
					localProperties.setProperty("ctml_id", str3);
				str3 = localResultSet.getString(3);
				if (str3 != null)
					localProperties.setProperty("ctml_type", str3);
				str3 = localResultSet.getString(4);
				if (str3 != null)
					localProperties.setProperty("ctml_status", str3);
				str3 = localResultSet.getString(5);
				if (str3 != null)
					localProperties.setProperty("ctml_description", str3);
				localCLOB = ((OracleResultSet) localResultSet).getCLOB(6);
				localBufferedReader = new BufferedReader(localCLOB
						.getCharacterStream());
				char[] arrayOfChar = new char[(int) localCLOB.length()];
				localBufferedReader.read(arrayOfChar);
				localBufferedReader.close();
				if (arrayOfChar != null)
					localProperties.setProperty("ctml_policyinfo", new String(
							arrayOfChar));
				str3 = localResultSet.getString(7);
				if (str3 != null)
					localProperties.setProperty("reserve", str3);
				localVector.add(localProperties);
			}
			arrayOfProperties1 = new Properties[localVector.size()];
			for (int k = 0; k < localVector.size(); k++)
				arrayOfProperties1[k] = ((Properties) localVector.get(k));
			return arrayOfProperties1;
		} catch (Exception localException) {
			Properties[] arrayOfProperties2;
			syslogger.info("数据库异常：" + localException.getMessage());
			throw new DBException("8008", "获取模板失败", localException);
		} finally {
			if (localCLOB != null)
				try {
					localCLOB.close();
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

	public int saveCertEntity(CertInfo paramCertInfo) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		OutputStream localOutputStream = null;
		int localBLOB1 = -1;
		try {
			int i;
			if (paramCertInfo == null) {
				return localBLOB1;
			}
			localConnection = DriverManager.getConnection("proxool.ida");
			localConnection.setAutoCommit(false);
			if (paramCertInfo.getApplicant() == null) {
				String str = "update cert set certstatus=?,notbefore=?,notafter=?,certentity=empty_blob() where certsn=?";
				localPreparedStatement = localConnection.prepareStatement(str);
				localPreparedStatement.setString(1, paramCertInfo
						.getCertStatus());
				localPreparedStatement.setLong(2, paramCertInfo.getNotBefore());
				localPreparedStatement.setLong(3, paramCertInfo.getNotAfter());
				localPreparedStatement.setString(4, paramCertInfo.getCertSN());
			} else {
				String str = "update cert set certstatus=?,notbefore=?,notafter=?,applicant=?,certentity=empty_blob() where certsn=?";
				localPreparedStatement = localConnection.prepareStatement(str);
				localPreparedStatement.setString(1, paramCertInfo
						.getCertStatus());
				localPreparedStatement.setLong(2, paramCertInfo.getNotBefore());
				localPreparedStatement.setLong(3, paramCertInfo.getNotAfter());
				localPreparedStatement.setString(4, paramCertInfo
						.getApplicant());
				localPreparedStatement.setString(5, paramCertInfo.getCertSN());
			}
			localBLOB1 = localPreparedStatement.executeUpdate();
			String str = "select certentity from cert where certsn=? for update of certentity";
			localPreparedStatement = localConnection.prepareStatement(str);
			localPreparedStatement.setString(1, paramCertInfo.getCertSN());
			localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next()) {
				BLOB localBLOB2 = ((OracleResultSet) localResultSet).getBLOB(1);
				localOutputStream = localBLOB2.getBinaryOutputStream();
				localOutputStream.write(paramCertInfo.getCertEntity());
				localOutputStream.flush();
				localOutputStream.close();
				localConnection.commit();
			} else {
				localConnection.rollback();
			}
			return localBLOB1;
		} catch (Exception localException) {
			BLOB localBLOB2;
			syslogger.info("数据库异常：" + localException.getMessage());
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException1) {
				}
			throw new DBException("8006", "保存证书实体失败", localException);
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

	public int reqAndDownCert(CertInfo paramCertInfo) throws DBException {
		Connection localConnection = null;
		int i = -1;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			localConnection.setAutoCommit(false);
			i = reqAndDownCert(localConnection, paramCertInfo);
			if (i == 1)
				localConnection.commit();
			else
				localConnection.rollback();
			return i;
		} catch (DBException localDBException) {
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException2) {
				}
			throw localDBException;
		} catch (SQLException localSQLException1) {
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException3) {
				}
			throw new DBException("8035", "申请并下载证书失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException4) {
				}
		}
	}

	private int reqAndDownCert(Connection paramConnection,
			CertInfo paramCertInfo) throws DBException {
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		OutputStream localOutputStream = null;
		int i = -1;
		try {
			int j;
			if (paramCertInfo == null) {
				return i;
			}
			String str = "insert into cert values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,empty_blob(),?,?,?,?,?,?,?,?)";
			localPreparedStatement = paramConnection.prepareStatement(str);
			localPreparedStatement.setString(1, paramCertInfo.getCertSN());
			localPreparedStatement.setString(2, paramCertInfo.getSubject()
					.toUpperCase());
			localPreparedStatement.setString(3, paramCertInfo.getSubject());
			localPreparedStatement.setLong(4, paramCertInfo.getNotBefore());
			localPreparedStatement.setLong(5, paramCertInfo.getNotAfter());
			localPreparedStatement.setInt(6, paramCertInfo.getValidity());
			localPreparedStatement.setString(7, paramCertInfo.getAuthCode());
			localPreparedStatement.setLong(8, paramCertInfo.getCdpid());
			localPreparedStatement.setString(9, paramCertInfo.getCtmlName());
			localPreparedStatement.setString(10, paramCertInfo.getCertStatus());
			localPreparedStatement.setLong(11, 1L);
			localPreparedStatement.setLong(12, getTime());
			localPreparedStatement.setString(13, paramCertInfo.getApplicant()
					.toUpperCase());
			localPreparedStatement.setString(14, paramCertInfo.getApplicant());
			localPreparedStatement.setString(15, paramCertInfo.getEmail());
			localPreparedStatement.setString(16, paramCertInfo.getRemark());
			localPreparedStatement.setLong(17, paramCertInfo
					.getAuthCodeUpdateTime());
			localPreparedStatement.setString(18, "tmp_signServer");
			localPreparedStatement.setString(19, "tmp_signClient");
			localPreparedStatement
					.setString(20, paramCertInfo.getIsRetainKey());
			localPreparedStatement.setString(21, "0");
			localPreparedStatement.setString(22, paramCertInfo.getOldSN());
			i = localPreparedStatement.executeUpdate();
			str = "select certentity from cert where certsn=? for update of certentity";
			localPreparedStatement = paramConnection.prepareStatement(str);
			localPreparedStatement.setString(1, paramCertInfo.getCertSN());
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			BLOB localBLOB = ((OracleResultSet) localResultSet).getBLOB(1);
			localOutputStream = localBLOB.getBinaryOutputStream();
			localOutputStream.write(paramCertInfo.getCertEntity());
			localOutputStream.flush();
			localOutputStream.close();
			Vector localVector = paramCertInfo.getStandardExtensions();
			if (localVector != null)
				for (int k = 0; k < localVector.size(); k++) {
					str = "insert into cert_standard_ext values(?,?,?,?,?,?,?,?)";
					StandardExtension localStandardExtension = (StandardExtension) localVector
							.get(k);
					localPreparedStatement = paramConnection
							.prepareStatement(str);
					localPreparedStatement.setString(1, paramCertInfo
							.getCertSN());
					localPreparedStatement.setString(2, localStandardExtension
							.getParentOID());
					localPreparedStatement.setString(3, localStandardExtension
							.getParentName());
					localPreparedStatement.setString(4, localStandardExtension
							.getChildName());
					localPreparedStatement.setString(5, localStandardExtension
							.getOtherNameOid());
					localPreparedStatement.setString(6, localStandardExtension
							.getStandardValue());
					localPreparedStatement.setString(7, "tmp_signServer");
					localPreparedStatement.setString(8, "tmp_signClient");
					localPreparedStatement.executeUpdate();
				}
			CertExtensions localCertExtensions = paramCertInfo
					.getCertExtensions();
			if (localCertExtensions != null)
				for (int m = 0; m < localCertExtensions.getExtensionsCount(); m++) {
					str = "insert into cert_selfext values(?,?,?,?,?,?)";
					Extension localExtension = localCertExtensions
							.getExtension(m);
					localPreparedStatement = paramConnection
							.prepareStatement(str);
					localPreparedStatement.setString(1, paramCertInfo
							.getCertSN());
					localPreparedStatement
							.setString(2, localExtension.getOid());
					localPreparedStatement.setString(3, localExtension
							.getName());
					localPreparedStatement.setString(4, localExtension
							.getValue());
					localPreparedStatement.setString(5, "tmp_signServer");
					localPreparedStatement.setString(6, "tmp_signClient");
					i = localPreparedStatement.executeUpdate();
				}
			addCertSUM();
			return i;
		} catch (Exception localException) {
			int m;
			syslogger.info("数据库异常：" + localException.getMessage());
			throw new DBException("8035", "申请并下载证书失败", localException);
		} finally {
			if (localOutputStream != null)
				try {
					localOutputStream.close();
				} catch (IOException localIOException) {
				}
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException) {
				}
		}
	}

	public int updAndDownCert(CertRevokeInfo paramCertRevokeInfo,
			CertInfo paramCertInfo) throws DBException {
		Connection localConnection = null;
		int i = -1;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			localConnection.setAutoCommit(false);
			String str = revokeCert(localConnection, paramCertRevokeInfo);
			if (str != null) {
				paramCertInfo.setOldSN(str);
				i = reqAndDownCert(localConnection, paramCertInfo);
				if (i == 1) {
					updateAdmin(localConnection, str, paramCertInfo.getCertSN());
					updateTemplateAdmin(localConnection, str, paramCertInfo
							.getCertSN());
					localConnection.commit();
				} else {
					localConnection.rollback();
				}
			} else {
				localConnection.rollback();
			}
			return i;
		} catch (DBException localDBException) {
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException2) {
				}
			throw localDBException;
		} catch (SQLException localSQLException1) {
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException3) {
				}
			throw new DBException("8036", "更新并下载证书失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException4) {
				}
		}
	}

	public int updAndDownAdminCert(CertRevokeInfo paramCertRevokeInfo,
			CertInfo paramCertInfo) throws DBException {
		Connection localConnection = null;
		int i = -1;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			localConnection.setAutoCommit(false);
			String str = revokeCert(localConnection, paramCertRevokeInfo);
			if (str != null) {
				paramCertInfo.setOldSN(str);
				i = reqAndDownCert(localConnection, paramCertInfo);
				if (i != -1) {
					i = updateAdmin(localConnection, str, paramCertInfo
							.getCertSN());
					if (i != -1)
						i = updateTemplateAdmin(localConnection, str,
								paramCertInfo.getCertSN());
					if (i != -1)
						localConnection.commit();
					else
						localConnection.rollback();
				} else {
					localConnection.rollback();
				}
			} else {
				localConnection.rollback();
			}
			return i;
		} catch (DBException localDBException) {
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException2) {
				}
			throw localDBException;
		} catch (SQLException localSQLException1) {
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException3) {
				}
			throw new DBException("8044", "更新并下载管理员证书失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException4) {
				}
		}
	}

	public byte[] getCertEntity(String paramString) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		BLOB localBLOB = null;
		byte[] arrayOfByte = null;
		try {
			if (paramString == null) {
				return arrayOfByte;
			}
			localConnection = DriverManager.getConnection("proxool.ida");
			String str = "select certentity from cert where certsn=?";
			localPreparedStatement = localConnection.prepareStatement(str);
			localPreparedStatement.setString(1, paramString);
			localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next()) {
				localBLOB = ((OracleResultSet) localResultSet).getBLOB(1);
				BufferedInputStream localObject1 = new BufferedInputStream(
						localBLOB.getBinaryStream());
				arrayOfByte = new byte[(int) localBLOB.length()];
				((BufferedInputStream) localObject1).read(arrayOfByte);
				((BufferedInputStream) localObject1).close();
			}
			return arrayOfByte;
		} catch (Exception localException) {
			Object localObject1;
			syslogger.info("数据库异常：" + localException.getMessage());
			throw new DBException("8010", "获取证书实体失败", localException);
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

	public Vector getCertInfo(Properties paramProperties, int paramInt1,
			int paramInt2, boolean paramBoolean) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		int i = 0;
		CertInfo[] arrayOfCertInfo = null;
		Vector localVector1 = new Vector();
		Vector localVector2 = new Vector();
		String str3 = "";
		Enumeration localEnumeration = null;
		String[][] arrayOfString = (String[][]) null;
		try {
			if (paramProperties == null) {
				return localVector1;
			}
			String str2 = "select /*+first_rows*/ count(ctmlname) from cert";
			int k = paramProperties.size();
			Object localObject1 = paramProperties.getProperty("ctmlName");
			if ((localObject1 != null)
					&& (((String) localObject1).indexOf("|") >= 0)) {
				StringTokenizer localObject2 = new StringTokenizer(
						(String) localObject1, "|");
				k += ((StringTokenizer) localObject2).countTokens();
			}
			Object localObject2 = paramProperties.getProperty("certStatus");
			StringTokenizer localStringTokenizer;
			if ((localObject2 != null)
					&& (((String) localObject2).indexOf("|") >= 0)) {
				localStringTokenizer = new StringTokenizer(
						(String) localObject2, "|");
				k += localStringTokenizer.countTokens();
			}
			arrayOfString = new String[k][2];
			localEnumeration = paramProperties.propertyNames();
			if (localEnumeration.hasMoreElements()) {
				str3 = " where ";
				while (localEnumeration.hasMoreElements()) {
					String str1 = localEnumeration.nextElement().toString();
					arrayOfString[i][0] = paramProperties.getProperty(str1);
					if (str1.equals("createTimeStart")) {
						str3 = str3 + "createTime" + ">=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("createTimeEnd")) {
						str3 = str3 + "createTime" + "<=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("ctmlName")) {
						if (arrayOfString[i][0].indexOf("|") >= 0) {
							localStringTokenizer = new StringTokenizer(
									arrayOfString[i][0], "|");
							k = 0;
							for (str3 = str3 + str1 + " in ("; localStringTokenizer
									.hasMoreTokens(); str3 = str3 + "?,") {
								arrayOfString[i][0] = localStringTokenizer
										.nextToken();
								arrayOfString[i][1] = "S";
								i++;
							}
							i--;
							str3 = str3.substring(0, str3.length() - 1)
									+ ") and ";
						} else {
							str3 = str3 + str1 + "=? and ";
							arrayOfString[i][1] = "S";
						}
					} else if (str1.equals("certStatus")) {
						if (arrayOfString[i][0].indexOf("|") >= 0) {
							localStringTokenizer = new StringTokenizer(
									arrayOfString[i][0], "|");
							for (str3 = str3 + str1 + " in ("; localStringTokenizer
									.hasMoreTokens(); str3 = str3 + "?,") {
								arrayOfString[i][0] = localStringTokenizer
										.nextToken();
								arrayOfString[i][1] = "S";
								i++;
							}
							i--;
							str3 = str3.substring(0, str3.length() - 1)
									+ ") and ";
						} else {
							str3 = str3 + str1 + "=? and ";
							arrayOfString[i][1] = "S";
						}
					} else if (str1.equals("subject")) {
						if (!paramBoolean) {
							arrayOfString[i][0] = ("%"
									+ setEscape2(arrayOfString[i][0]) + "%");
							str3 = str3 + "subjectuppercase"
									+ " like ? escape '\\' and ";
						} else {
							str3 = str3 + "subjectuppercase" + "=? and ";
						}
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else if (str1.equals("applicant")) {
						str3 = str3 + "applicantuppercase" + "=? and ";
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else if (str1.equals("certSN")) {
						str3 = str3 + "cert." + "certSN" + "=? and ";
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else if (str1.equals("cdpid")) {
						str3 = str3 + "cert." + "cdpid" + "=? and ";
						arrayOfString[i][1] = "S";
					} else if ((str1.equals("notBefore"))
							|| (str1.equals("notAfter"))
							|| (str1.equals("validity"))
							|| (str1.equals("cdpid"))
							|| (str1.equals("isvalid"))
							|| (str1.equals("createTime"))) {
						str3 = str3 + str1 + "=? and ";
						arrayOfString[i][1] = "L";
					} else {
						str3 = str3 + str1 + "=? and ";
						arrayOfString[i][1] = "S";
					}
					i++;
				}
				str3 = str3.substring(0, str3.length() - 5);
			}
			localConnection = DriverManager.getConnection("proxool.ida");
			str2 = str2 + str3;
			localPreparedStatement = localConnection.prepareStatement(str2
					.toLowerCase());
			for (int j = 0; j < i; j++)
				if (arrayOfString[j][1].equals("L"))
					localPreparedStatement.setLong(j + 1, Long
							.parseLong(arrayOfString[j][0]));
				else
					localPreparedStatement
							.setString(j + 1, arrayOfString[j][0]);
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			long l = localResultSet.getLong(1);
			localVector1.add(Long.toString(l));
			if (paramInt1 < 1)
				paramInt1 = 1;
			if (orderby)
				str3 = str3 + " order by createtime desc";
			str2 = "select /*+first_rows*/ cert.certsn,subject,notbefore,notafter,validity,authcode,cert.cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark,reason,reasondesc,oldsn from cert left outer join revokedcert on (cert.certsn=revokedcert.certsn) ";
			if (paramInt2 < 1)
				str2 = str2 + str3;
			else
				str2 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark,reason,reasondesc,oldsn from (select rownum as my_rownum,table_a.* from ("
						+ str2
						+ str3
						+ ") table_a where rownum<?) where my_rownum>=?";
			localPreparedStatement = localConnection.prepareStatement(str2
					.toLowerCase());
			int j = 0;
			for (j = 0; j < i; j++)
				if (arrayOfString[j][1].equals("L"))
					localPreparedStatement.setLong(j + 1, Long
							.parseLong(arrayOfString[j][0]));
				else
					localPreparedStatement
							.setString(j + 1, arrayOfString[j][0]);
			if (paramInt2 > 0) {
				localPreparedStatement.setInt(j + 1, paramInt1 + paramInt2);
				localPreparedStatement.setInt(j + 2, paramInt1);
			}
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next())
				localVector2.add(getCertinfoCondition(localResultSet));
			arrayOfCertInfo = new CertInfo[localVector2.size()];
			localVector2.toArray(arrayOfCertInfo);
			localVector1.add(arrayOfCertInfo);
			return localVector1;
		} catch (SQLException localSQLException1) {
			Vector localVector3;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8014", "获取证书信息失败", localSQLException1);
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

	public Vector getCertInfoForIsnotWaiting(Properties paramProperties,
			int paramInt1, int paramInt2, boolean paramBoolean)
			throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		int i = 0;
		CertInfo[] arrayOfCertInfo = null;
		Vector localVector1 = new Vector();
		Vector localVector2 = new Vector();
		String str3 = "";
		Enumeration localEnumeration = null;
		String[][] arrayOfString = (String[][]) null;
		try {
			if (paramProperties == null) {
				return localVector1;
			}
			String str2 = "select /*+first_rows*/ count(ctmlname) from cert";
			int k = paramProperties.size();
			Object localObject1 = paramProperties.getProperty("ctmlName");
			if ((localObject1 != null)
					&& (((String) localObject1).indexOf("|") >= 0)) {
				StringTokenizer localObject2 = new StringTokenizer(
						(String) localObject1, "|");
				k += ((StringTokenizer) localObject2).countTokens();
			}
			Object localObject2 = paramProperties.getProperty("certStatus");
			if ((localObject2 != null)
					&& (((String) localObject2).indexOf("|") >= 0)) {
				StringTokenizer localObject3 = new StringTokenizer(
						(String) localObject2, "|");
				k += ((StringTokenizer) localObject3).countTokens();
			}
			arrayOfString = new String[k][2];
			localEnumeration = paramProperties.propertyNames();
			if (localEnumeration.hasMoreElements()) {
				str3 = " where ";
				while (localEnumeration.hasMoreElements()) {
					String str1 = localEnumeration.nextElement().toString();
					arrayOfString[i][0] = paramProperties.getProperty(str1);
					if (str1.equals("createTimeStart")) {
						str3 = str3 + "createTime" + ">=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("createTimeEnd")) {
						str3 = str3 + "createTime" + "<=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("ctmlName")) {
						if (arrayOfString[i][0].indexOf("|") >= 0) {
							StringTokenizer localObject3 = new StringTokenizer(
									arrayOfString[i][0], "|");
							k = 0;
							for (str3 = str3 + str1 + " in ("; ((StringTokenizer) localObject3)
									.hasMoreTokens(); str3 = str3 + "?,") {
								arrayOfString[i][0] = ((StringTokenizer) localObject3)
										.nextToken();
								arrayOfString[i][1] = "S";
								i++;
							}
							i--;
							str3 = str3.substring(0, str3.length() - 1)
									+ ") and ";
						} else {
							str3 = str3 + str1 + "=? and ";
							arrayOfString[i][1] = "S";
						}
					} else if (str1.equals("certStatus")) {
						if (arrayOfString[i][0].indexOf("|") >= 0) {
							StringTokenizer localObject3 = new StringTokenizer(
									arrayOfString[i][0], "|");
							for (str3 = str3 + str1 + " in ("; ((StringTokenizer) localObject3)
									.hasMoreTokens(); str3 = str3 + "?,") {
								arrayOfString[i][0] = ((StringTokenizer) localObject3)
										.nextToken();
								arrayOfString[i][1] = "S";
								i++;
							}
							i--;
							str3 = str3.substring(0, str3.length() - 1)
									+ ") and ";
						} else {
							str3 = str3 + str1 + "=? and ";
							arrayOfString[i][1] = "S";
						}
					} else if (str1.equals("subject")) {
						if (!paramBoolean) {
							arrayOfString[i][0] = ("%"
									+ setEscape2(arrayOfString[i][0]) + "%");
							str3 = str3 + "subjectuppercase"
									+ " like ? escape '\\' and ";
						} else {
							str3 = str3 + "subjectuppercase" + "=? and ";
						}
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else if (str1.equals("applicant")) {
						str3 = str3 + "applicantuppercase" + "=? and ";
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else if (str1.equals("certSN")) {
						str3 = str3 + "cert." + "certSN" + "=? and ";
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else if (str1.equals("cdpid")) {
						str3 = str3 + "cert." + "cdpid" + "=? and ";
						arrayOfString[i][1] = "S";
					} else if ((str1.equals("notBefore"))
							|| (str1.equals("notAfter"))
							|| (str1.equals("validity"))
							|| (str1.equals("cdpid"))
							|| (str1.equals("isvalid"))
							|| (str1.equals("createTime"))) {
						str3 = str3 + str1 + "=? and ";
						arrayOfString[i][1] = "L";
					} else {
						str3 = str3 + str1 + "=? and ";
						arrayOfString[i][1] = "S";
					}
					i++;
				}
				str3 = str3.substring(0, str3.length() - 5);
			}
			localConnection = DriverManager.getConnection("proxool.ida");
			Object localObject3 = "";
			if (str3.indexOf("where") >= 0)
				localObject3 = " and iswaiting = '0'";
			else
				localObject3 = " where iswaiting = '0'";
			str3 = str3 + (String) localObject3;
			str2 = str2 + str3;
			localPreparedStatement = localConnection.prepareStatement(str2
					.toLowerCase());
			for (int j = 0; j < i; j++)
				if (arrayOfString[j][1].equals("L"))
					localPreparedStatement.setLong(j + 1, Long
							.parseLong(arrayOfString[j][0]));
				else
					localPreparedStatement
							.setString(j + 1, arrayOfString[j][0]);
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			long l = localResultSet.getLong(1);
			localVector1.add(Long.toString(l));
			if (paramInt1 < 1)
				paramInt1 = 1;
			if (orderby)
				str3 = str3 + " order by createtime desc";
			str2 = "select /*+first_rows*/ cert.certsn,subject,notbefore,notafter,validity,authcode,cert.cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark,reason,reasondesc,oldsn from cert left outer join revokedcert on (cert.certsn=revokedcert.certsn) ";
			if (paramInt2 < 1)
				str2 = str2 + str3;
			else
				str2 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark,reason,reasondesc,oldsn from (select rownum as my_rownum,table_a.* from ("
						+ str2
						+ str3
						+ ") table_a where rownum<?) where my_rownum>=?";
			localPreparedStatement = localConnection.prepareStatement(str2
					.toLowerCase());
			int j = 0;
			for (j = 0; j < i; j++)
				if (arrayOfString[j][1].equals("L"))
					localPreparedStatement.setLong(j + 1, Long
							.parseLong(arrayOfString[j][0]));
				else
					localPreparedStatement
							.setString(j + 1, arrayOfString[j][0]);
			if (paramInt2 > 0) {
				localPreparedStatement.setInt(j + 1, paramInt1 + paramInt2);
				localPreparedStatement.setInt(j + 2, paramInt1);
			}
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next())
				localVector2.add(getCertinfoCondition(localResultSet));
			arrayOfCertInfo = new CertInfo[localVector2.size()];
			localVector2.toArray(arrayOfCertInfo);
			localVector1.add(arrayOfCertInfo);
			return localVector1;
		} catch (SQLException localSQLException1) {
			Vector localVector3;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8077", "获得非待操作状态证书信息失败", localSQLException1);
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

	public CertInfo[] getCertTBPInfo() throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		BLOB localBLOB = null;
		CertInfo localCertInfo = null;
		Vector localVector = new Vector();
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			String str = "select t1.certsn,t1.subject,t1.ctmlname,t1.certstatus,t1.certentity from cert t1,certtbp t2 where t1.certsn=t2.certsn";
			localPreparedStatement = localConnection.prepareStatement(str);
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next()) {
				localCertInfo = new CertInfo();
				localCertInfo.setCertSN(localResultSet.getString(1));
				localCertInfo.setSubject(localResultSet.getString(2));
				localCertInfo.setCtmlName(localResultSet.getString(3));
				localCertInfo.setCertStatus(localResultSet.getString(4));
				localBLOB = ((OracleResultSet) localResultSet).getBLOB(5);
				BufferedInputStream localObject1 = new BufferedInputStream(
						localBLOB.getBinaryStream());
				ByteArrayOutputStream localObject2 = new ByteArrayOutputStream();
				byte[] arrayOfByte = new byte[1];
				int i = -1;
				while ((i = ((BufferedInputStream) localObject1)
						.read(arrayOfByte)) != -1)
					((ByteArrayOutputStream) localObject2).write(arrayOfByte);
				localCertInfo
						.setCertEntity(((ByteArrayOutputStream) localObject2)
								.toByteArray());
				localVector.add(localCertInfo);
			}
			CertInfo[] localObject1 = new CertInfo[localVector.size()];
			localVector.toArray(localObject1);
			return localObject1;
		} catch (Exception localException) {
			Object localObject2;
			syslogger.info("数据库异常：" + localException.getMessage());
			throw new DBException("8056", "获取待发布证书信息失败", localException);
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

	public Vector getOperationLogInfo(Properties paramProperties,
			int paramInt1, int paramInt2, boolean paramBoolean)
			throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		int i = 0;
		int k = -1;
		Operation localOperation = null;
		Operation[] arrayOfOperation = null;
		Vector localVector1 = new Vector();
		Vector localVector2 = new Vector();
		String str3 = "";
		Enumeration localEnumeration = null;
		String[][] arrayOfString = (String[][]) null;
		Object localObject1 = null;
		try {
			Vector localVector3;
			if (paramProperties == null) {
				return localVector1;
			}
			String str2 = "select /*+first_rows*/ count(operatorsubjectuppercase) from operationlog";
			arrayOfString = new String[paramProperties.size()][2];
			localEnumeration = paramProperties.propertyNames();
			if (localEnumeration.hasMoreElements()) {
				str3 = " where ";
				while (localEnumeration.hasMoreElements()) {
					String str1 = localEnumeration.nextElement().toString();
					arrayOfString[i][0] = paramProperties.getProperty(str1);
					if (str1.equals("optTimeBegin")) {
						str3 = str3 + "optTime" + ">=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("optTimeEnd")) {
						str3 = str3 + "optTime" + "<=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("result")) {
						str3 = str3 + str1 + "=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("objectSubject")) {
						if (!paramBoolean) {
							arrayOfString[i][0] = ("%"
									+ setEscape2(arrayOfString[i][0]) + "%");
							str3 = str3 + "objectsubjectuppercase"
									+ " like ? escape '\\' and ";
						} else {
							str3 = str3 + "objectsubjectuppercase" + "=? and ";
						}
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else if (str1.equals("operatorSubject")) {
						str3 = str3 + "operatorsubjectuppercase" + "=? and ";
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else {
						str3 = str3 + str1 + "=? and ";
						arrayOfString[i][1] = "S";
					}
					i++;
				}
				str3 = str3.substring(0, str3.length() - 5);
			}
			localConnection = DriverManager.getConnection("proxool.ida");
			str2 = str2 + str3;
			localPreparedStatement = localConnection.prepareStatement(str2
					.toLowerCase());
			for (int j = 0; j < i; j++)
				if (arrayOfString[j][1].equals("L"))
					localPreparedStatement.setLong(j + 1, Long
							.parseLong(arrayOfString[j][0]));
				else
					localPreparedStatement
							.setString(j + 1, arrayOfString[j][0]);
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			long l = localResultSet.getLong(1);
			localVector1.add(Long.toString(l));
			if (paramInt1 < 1)
				paramInt1 = 1;
			str2 = "select /*+first_rows*/operatorsn,operatorsubject,objectcertsn,objectsubject,objectctmlname,opttype,opttime,result from operationlog ";
			if (orderby)
				str3 = str3 + " order by opttime desc";
			if (paramInt2 < 1)
				str2 = str2 + str3;
			else
				str2 = "select operatorsn,operatorsubject,objectcertsn,objectsubject,objectctmlname,opttype,opttime,result from (select rownum as my_rownum,table_a.* from ("
						+ str2
						+ str3
						+ ") table_a where rownum<?) where my_rownum>=?";
			localPreparedStatement = localConnection.prepareStatement(str2
					.toLowerCase());
			int j = 0;
			for (j = 0; j < i; j++)
				if (arrayOfString[j][1].equals("L"))
					localPreparedStatement.setLong(j + 1, Long
							.parseLong(arrayOfString[j][0]));
				else
					localPreparedStatement
							.setString(j + 1, arrayOfString[j][0]);
			if (paramInt2 > 0) {
				localPreparedStatement.setInt(j + 1, paramInt1 + paramInt2);
				localPreparedStatement.setInt(j + 2, paramInt1);
			}
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next()) {
				localOperation = new Operation();
				localOperation.setOperatorSN(localResultSet.getString(1));
				localOperation.setOperatorDN(localResultSet.getString(2));
				localOperation.setObjCertSN(localResultSet.getString(3));
				localOperation.setObjSubject(localResultSet.getString(4));
				localOperation.setObjCTMLName(localResultSet.getString(5));
				localOperation.setOptType(localResultSet.getString(6));
				localOperation.setOptTime(localResultSet.getLong(7));
				localOperation.setResult(localResultSet.getInt(8));
				localVector2.add(localOperation);
			}
			arrayOfOperation = new Operation[localVector2.size()];
			localVector2.toArray(arrayOfOperation);
			localVector1.add(arrayOfOperation);
			return localVector1;
		} catch (SQLException localSQLException1) {
			Vector localVector4;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8050", "获取操作日志信息失败", localSQLException1);
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

	public Vector getCertStatistic(Properties paramProperties,
			String paramString, boolean paramBoolean) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		int i = 0;
		Properties localProperties = new Properties();
		Vector localVector1 = new Vector();
		String str3 = "";
		Enumeration localEnumeration = null;
		String[][] arrayOfString = (String[][]) null;
		Object localObject1 = null;
		try {
			if (paramProperties == null) {
				return localVector1;
			}
			int k = paramProperties.size();
			Object localObject2 = paramProperties.getProperty("ctmlName");
			StringTokenizer localStringTokenizer;
			if ((localObject2 != null)
					&& (((String) localObject2).indexOf("|") >= 0)) {
				localStringTokenizer = new StringTokenizer(
						(String) localObject2, "|");
				k += localStringTokenizer.countTokens();
			}
			localObject2 = paramProperties.getProperty("certStatus");
			if ((localObject2 != null)
					&& (((String) localObject2).indexOf("|") >= 0)) {
				localStringTokenizer = new StringTokenizer(
						(String) localObject2, "|");
				k += localStringTokenizer.countTokens();
			}
			arrayOfString = new String[k][2];
			localEnumeration = paramProperties.propertyNames();
			if (localEnumeration.hasMoreElements()) {
				str3 = " where ";
				while (localEnumeration.hasMoreElements()) {
					String str1 = localEnumeration.nextElement().toString();
					arrayOfString[i][0] = paramProperties.getProperty(str1);
					if (str1.equals("createTimeStart")) {
						str3 = str3 + "createTime" + ">=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("createTimeEnd")) {
						str3 = str3 + "createTime" + "<=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("ctmlName")) {
						if (arrayOfString[i][0].indexOf("|") >= 0) {
							localStringTokenizer = new StringTokenizer(
									arrayOfString[i][0], "|");
							k = 0;
							for (str3 = str3 + str1 + " in ("; localStringTokenizer
									.hasMoreTokens(); str3 = str3 + "?,") {
								arrayOfString[i][0] = localStringTokenizer
										.nextToken();
								arrayOfString[i][1] = "S";
								i++;
							}
							i--;
							str3 = str3.substring(0, str3.length() - 1)
									+ ") and ";
						} else {
							str3 = str3 + str1 + "=? and ";
							arrayOfString[i][1] = "S";
						}
					} else if (str1.equals("certStatus")) {
						if (arrayOfString[i][0].indexOf("|") >= 0) {
							localStringTokenizer = new StringTokenizer(
									arrayOfString[i][0], "|");
							for (str3 = str3 + str1 + " in ("; localStringTokenizer
									.hasMoreTokens(); str3 = str3 + "?,") {
								arrayOfString[i][0] = localStringTokenizer
										.nextToken();
								arrayOfString[i][1] = "S";
								i++;
							}
							i--;
							str3 = str3.substring(0, str3.length() - 1)
									+ ") and ";
						} else {
							str3 = str3 + str1 + "=? and ";
							arrayOfString[i][1] = "S";
						}
					} else if (str1.equals("subject")) {
						if (!paramBoolean) {
							arrayOfString[i][0] = ("%"
									+ setEscape2(arrayOfString[i][0]) + "%");
							str3 = str3 + "subjectuppercase"
									+ " like ? escape '\\' and ";
						} else {
							str3 = str3 + "subjectuppercase" + "=? and ";
						}
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else if (str1.equals("applicant")) {
						str3 = str3 + "applicantuppercase" + "=? and ";
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else if ((str1.equals("notBefore"))
							|| (str1.equals("notAfter"))
							|| (str1.equals("validity"))
							|| (str1.equals("createTime"))) {
						str3 = str3 + str1 + "=? and ";
						arrayOfString[i][1] = "L";
					} else {
						str3 = str3 + str1 + "=? and ";
						arrayOfString[i][1] = "S";
					}
					i++;
				}
				str3 = str3.substring(0, str3.length() - 5);
			}
			if (paramString.equalsIgnoreCase("applicant"))
				paramString = "applicantuppercase";
			String str2 = "select " + paramString + ",count(" + paramString
					+ ") from cert ";
			str2 = str2 + str3 + " group by " + paramString + " order by "
					+ paramString;
			localConnection = DriverManager.getConnection("proxool.ida");
			localPreparedStatement = localConnection.prepareStatement(str2
					.toLowerCase());
			for (int j = 0; j < i; j++)
				if (arrayOfString[j][1].equals("L"))
					localPreparedStatement.setLong(j + 1, Long
							.parseLong(arrayOfString[j][0]));
				else
					localPreparedStatement
							.setString(j + 1, arrayOfString[j][0]);
			localResultSet = localPreparedStatement.executeQuery();
			long l1 = 0L;
			long l2 = 0L;
			while (localResultSet.next()) {
				String str4 = localResultSet.getString(1);
				l2 = localResultSet.getLong(2);
				l1 += l2;
				localProperties.put(str4, Long.toString(l2));
			}
			localVector1.add(Long.toString(l1));
			localVector1.add(localProperties);
			return localVector1;
		} catch (SQLException localSQLException1) {
			Vector localVector2;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8051", "获取证书统计信息失败", localSQLException1);
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

	public Vector getCRLInfo() throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		CRLInfo[] arrayOfCRLInfo = null;
		Vector localVector1 = new Vector();
		Vector localVector2 = new Vector();
		String str1 = "select crl_name,crl_entity from crl";
		BLOB localBLOB = null;
		long l1 = 0L;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			localPreparedStatement = localConnection.prepareStatement(str1,
					1004, 1007);
			localResultSet = localPreparedStatement.executeQuery();
			long l2 = 0L;
			while (localResultSet.next()) {
				CRLInfo localObject1 = new CRLInfo();
				((CRLInfo) localObject1)
						.setCRLName(localResultSet.getString(1));
				localBLOB = ((OracleResultSet) localResultSet).getBLOB(2);
				if (localBLOB != null) {
					BufferedInputStream localBufferedInputStream = new BufferedInputStream(
							localBLOB.getBinaryStream());
					ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
					int i = -1;
					while ((i = localBufferedInputStream.read()) != -1)
						localByteArrayOutputStream.write(i);
					String str2 = new String(localByteArrayOutputStream
							.toByteArray());
					((CRLInfo) localObject1).setCRLEntity(str2);
				}
				localVector2.add(localObject1);
				l1 += l2;
			}
			arrayOfCRLInfo = new CRLInfo[localVector2.size()];
			localVector2.toArray(arrayOfCRLInfo);
			localVector1.add(Long.toString(l1));
			localVector1.add(arrayOfCRLInfo);
			return localVector1;
		} catch (Exception localException) {
			Object localObject1;
			syslogger.info("数据库异常：" + localException.getMessage());
			throw new DBException("8065", "获取证书注销列表信息失败", localException);
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

	public int saveCRL(CRLInfo[] paramArrayOfCRLInfo) throws DBException {
		int localSQLException1 = -1;
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		OutputStream localOutputStream = null;
		String str1 = "insert into crl values (?,empty_blob(),?,?)";
		String str2 = "select crl_entity from crl where crl_name=? for update of crl_entity";
		try {
			int i;
			if (paramArrayOfCRLInfo == null) {
				return localSQLException1;
			}
			localConnection = DriverManager.getConnection("proxool.ida");
			localConnection.setAutoCommit(false);
			deleteCRL(localConnection);
			String str3 = null;
			String str4 = null;
			int j = 0;
			for (j = 0; j < paramArrayOfCRLInfo.length; j++) {
				str3 = paramArrayOfCRLInfo[j].getCRLName();
				str4 = paramArrayOfCRLInfo[j].getCRLEntity();
				if ((str3 == null) || (str4 == null)) {
					if (localConnection != null)
						try {
							localConnection.rollback();
						} catch (SQLException localSQLException3) {
						}
					return localSQLException1;
				}
				localPreparedStatement = localConnection.prepareStatement(str1);
				localPreparedStatement.setString(1, str3);
				localPreparedStatement.setString(2, "tmp_signServer");
				localPreparedStatement.setString(3, "tmp_signClient");
				localSQLException1 = localPreparedStatement.executeUpdate();
				localPreparedStatement = localConnection.prepareStatement(str2);
				localPreparedStatement.setString(1, str3);
				localResultSet = localPreparedStatement.executeQuery();
				localResultSet.next();
				BLOB localBLOB = ((OracleResultSet) localResultSet).getBLOB(1);
				localOutputStream = localBLOB.getBinaryOutputStream();
				localOutputStream.write(str4.getBytes());
				localOutputStream.flush();
				localOutputStream.close();
			}
			localConnection.commit();
			localSQLException1 = paramArrayOfCRLInfo.length;
			return localSQLException1;
		} catch (Exception localException) {
			int j;
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException2) {
				}
			syslogger.info("数据库异常：" + localException.getMessage());
			throw new DBException("8066", "批量增加证书注销列表失败", localException);
		} finally {
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException4) {
				}
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException5) {
				}
		}
	}

	public long getCertCountByCtmlNameCertStatus(String paramString)
			throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		long l1 = -1L;
		if (paramString == null)
			return l1;
		try {
			String str = "select /*+index(cert pk_cert)*/count(certsn) from cert  where ctmlname=?  and (certstatus = 'Use'   or certstatus = 'Revoke'   or certstatus = 'Hold')";
			localConnection = DriverManager.getConnection("proxool.ida");
			localPreparedStatement = localConnection.prepareStatement(str);
			localPreparedStatement.setString(1, paramString);
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			l1 = localResultSet.getLong(1);
			return l1;
		} catch (SQLException localSQLException1) {
			long l2;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8068", "依据模板获取证书总数失败", localSQLException1);
		} finally {
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException2) {
				}
			if (localConnection != null)
				try {
					localConnection.close();
					localConnection = null;
				} catch (SQLException localSQLException3) {
				}
		}
	}

	public CertInfo[] getCertsToPublish(String paramString, int paramInt1,
			int paramInt2) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		CertInfo[] arrayOfCertInfo1 = null;
		Vector localVector = new Vector();
		String str1 = "";
		String str2 = "";
		try {
			if (paramString == null) {
				return arrayOfCertInfo1;
			}
			localConnection = DriverManager.getConnection("proxool.ida");
			if (paramInt1 < 1)
				paramInt1 = 1;
			str1 = "select /*+first_rows*/ certsn,subject,certstatus,certentity from cert  where ctmlname=?  and (certstatus = 'Use'   or certstatus = 'Revoke'   or certstatus = 'Hold')";
			if (orderby)
				str2 = " order by createtime desc";
			if (paramInt2 < 1)
				str1 = str1 + str2;
			else
				str1 = "select certsn,subject,certstatus,certentity from     (select rownum as my_rownum,table_a.* from ("
						+ str1
						+ str2
						+ ") table_a "
						+ "     where rownum<?) "
						+ "where my_rownum>=? ";
			localPreparedStatement = localConnection.prepareStatement(str1);
			localPreparedStatement.setString(1, paramString);
			if (paramInt2 > 0) {
				localPreparedStatement.setInt(2, paramInt1 + paramInt2);
				localPreparedStatement.setInt(3, paramInt1);
			}
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next())
				localVector.add(getCertinfoForCertsToPublish(localResultSet));
			arrayOfCertInfo1 = new CertInfo[localVector.size()];
			localVector.toArray(arrayOfCertInfo1);
			return arrayOfCertInfo1;
		} catch (SQLException localSQLException1) {
			CertInfo[] arrayOfCertInfo2;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8069", "依据模板获取证书失败", localSQLException1);
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

	protected CertInfo getCertinfoForCertsToPublish(ResultSet paramResultSet)
			throws SQLException {
		BLOB localBLOB = null;
		byte[] arrayOfByte = null;
		CertInfo localCertInfo = new CertInfo();
		localCertInfo.setCertSN(paramResultSet.getString(1));
		localCertInfo.setSubject(paramResultSet.getString(2));
		localCertInfo.setCertStatus(paramResultSet.getString(3));
		try {
			localBLOB = ((OracleResultSet) paramResultSet).getBLOB(4);
			BufferedInputStream localBufferedInputStream = new BufferedInputStream(
					localBLOB.getBinaryStream());
			arrayOfByte = new byte[(int) localBLOB.length()];
			localBufferedInputStream.read(arrayOfByte);
			localBufferedInputStream.close();
			localCertInfo.setCertEntity(arrayOfByte);
		} catch (Exception localException) {
			localException.printStackTrace();
		} finally {
			if (localBLOB != null)
				try {
					localBLOB.close();
				} catch (SQLException localSQLException) {
				}
		}
		return localCertInfo;
	}

	public Vector getAdminDN() throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		Vector localVector1 = new Vector();
		try {
			String str = "select /*+first_rows*/ distinct subject from cert t1  where exists (select t2.certsn from admin t2 where t2.certsn=t1.certsn)";
			localConnection = DriverManager.getConnection("proxool.ida");
			localPreparedStatement = localConnection.prepareStatement(str);
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next())
				localVector1.add(localResultSet.getString(1));
			if (localVector1 == null)
				throw new DBException("8070", "获取DN信息失败");
			return localVector1;
		} catch (SQLException localSQLException1) {
			Vector localVector2;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8070", "获取DN信息失败", localSQLException1);
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

	protected ArrayList getCerts(Connection paramConnection,
			String paramString, Vector paramVector, int paramInt)
			throws SQLException {
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		ArrayList localArrayList1 = new ArrayList();
		String str1 = "";
		ArrayList localArrayList2 = null;
		try {
			if ((paramConnection == null) || (paramString == null)
					|| (paramInt < 0)) {
				return localArrayList1;
			}
			Object localObject1 = getCertsCondition(paramString);
			if (localObject1 == null) {
				return localArrayList1;
			}
			str1 = ((Vector) localObject1).get(0).toString();
			localArrayList2 = (ArrayList) ((Vector) localObject1).get(1);
			Object localObject2 = "select /*+first_rows*/ certsn,subjectuppercase,subject, notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid, createtime, applicantuppercase,applicant,certentity, email,remark,authcode_updatetime,sign_server,sign_client,isretainkey,iswaiting,oldsn from cert";
			localPreparedStatement = paramConnection.prepareStatement(
					(String) localObject2 + str1, 1003, 1008);
			localPreparedStatement.setMaxRows(paramInt);
			localPreparedStatement.setFetchSize(paramInt);
			ValuePair localValuePair = null;
			for (int i = 0; i < localArrayList2.size(); i++) {
				localValuePair = (ValuePair) localArrayList2.get(i);
				if (localValuePair.getType().equals("I"))
					localPreparedStatement.setInt(i + 1, Integer
							.parseInt(localValuePair.getValue()));
				else if (localValuePair.getType().equals("L"))
					localPreparedStatement.setLong(i + 1, Long
							.parseLong(localValuePair.getValue()));
				else
					localPreparedStatement.setString(i + 1, localValuePair
							.getValue());
			}
			localResultSet = localPreparedStatement.executeQuery();
			CertInfo localCertInfo = null;
			String str2 = null;
			String str3 = null;
			String str4 = null;
			while (localResultSet.next()) {
				localCertInfo = getCert(localResultSet);
				str2 = localCertInfo.getCertSN();
				str3 = localCertInfo.getCertStatus();
				str4 = localCertInfo.getCtmlName();
				localArrayList1.add(localCertInfo);
				try {
					if ((str4 != null)
							&& (paramVector != null)
							&& (paramVector.contains(str4))
							&& (str3 != null)
							&& (!str3.equals("Undown"))
							&& (!str3.equals("UndownRevoke"))
							&& (insertCertArcForKMC(paramConnection, str2) == -1))
						paramConnection.rollback();
					if (deleteFKDataforCert(paramConnection, str2) == -1)
						paramConnection.rollback();
				} catch (Exception localException) {
					paramConnection.rollback();
					localException.printStackTrace();
				}
				localResultSet.deleteRow();
			}
			return localArrayList1;
		} finally {
			ArrayList localArrayList3;
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException) {
					localSQLException.printStackTrace();
				}
		}
	}

	protected CertInfo getCert(ResultSet paramResultSet) throws SQLException {
		BLOB localBLOB = null;
		byte[] arrayOfByte = null;
		CertInfo localCertInfo = new CertInfo();
		localCertInfo.setCertSN(paramResultSet.getString(1));
		localCertInfo.setSubjectUppercase(paramResultSet.getString(2));
		localCertInfo.setSubject(paramResultSet.getString(3));
		localCertInfo.setNotBefore(paramResultSet.getLong(4));
		localCertInfo.setNotAfter(paramResultSet.getLong(5));
		localCertInfo.setValidity(paramResultSet.getInt(6));
		localCertInfo.setAuthCode(paramResultSet.getString(7));
		localCertInfo.setCdpid(paramResultSet.getLong(8));
		localCertInfo.setCtmlName(paramResultSet.getString(9));
		localCertInfo.setCertStatus(paramResultSet.getString(10));
		localCertInfo.setIsValid(paramResultSet.getLong(11));
		localCertInfo.setCreateTime(paramResultSet.getLong(12));
		localCertInfo.setApplicantUppercase(paramResultSet.getString(13));
		localCertInfo.setApplicant(paramResultSet.getString(14));
		try {
			localBLOB = ((OracleResultSet) paramResultSet).getBLOB(15);
			if (localBLOB != null) {
				BufferedInputStream localBufferedInputStream = new BufferedInputStream(
						localBLOB.getBinaryStream());
				arrayOfByte = new byte[(int) localBLOB.length()];
				localBufferedInputStream.read(arrayOfByte);
				localBufferedInputStream.close();
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		} finally {
			if (localBLOB != null)
				try {
					localBLOB.close();
				} catch (SQLException localSQLException) {
				}
		}
		localCertInfo.setCertEntity(arrayOfByte);
		localCertInfo.setEmail(paramResultSet.getString(16));
		localCertInfo.setRemark(paramResultSet.getString(17));
		localCertInfo.setAuthCodeUpdateTime(paramResultSet.getLong(18));
		localCertInfo.setSignServer(paramResultSet.getString(19));
		localCertInfo.setSignClient(paramResultSet.getString(20));
		localCertInfo.setIsRetainKey(paramResultSet.getString(21));
		localCertInfo.setIswaiting(paramResultSet.getString(22));
		localCertInfo.setOldSN(paramResultSet.getString(23));
		return localCertInfo;
	}

	protected int insertCertArc(Connection paramConnection,
			ArrayList paramArrayList) throws SQLException {
		PreparedStatement localPreparedStatement1 = null;
		PreparedStatement localPreparedStatement2 = null;
		CertInfo localCertInfo = null;
		ResultSet localResultSet = null;
		OutputStream localOutputStream = null;
		int i = -1;
		try {
			int j;
			if ((paramConnection == null) || (paramArrayList == null)) {
				return i;
			}
			String str1 = "insert into certarc values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,empty_blob(),?,?,?,?,?,?,?,?)";
			localPreparedStatement1 = paramConnection.prepareStatement(str1);
			for (int k = 0; k < paramArrayList.size(); k++) {
				localCertInfo = (CertInfo) paramArrayList.get(k);
				localPreparedStatement1.setString(1, localCertInfo.getCertSN());
				localPreparedStatement1.setString(2, localCertInfo
						.getSubjectUppercase());
				localPreparedStatement1
						.setString(3, localCertInfo.getSubject());
				localPreparedStatement1
						.setLong(4, localCertInfo.getNotBefore());
				localPreparedStatement1.setLong(5, localCertInfo.getNotAfter());
				localPreparedStatement1.setLong(6, localCertInfo.getValidity());
				localPreparedStatement1.setString(7, localCertInfo
						.getAuthCode());
				localPreparedStatement1.setLong(8, localCertInfo.getCdpid());
				localPreparedStatement1.setString(9, localCertInfo
						.getCtmlName());
				localPreparedStatement1.setString(10, localCertInfo
						.getCertStatus());
				localPreparedStatement1.setLong(11, localCertInfo.getIsValid());
				localPreparedStatement1.setLong(12, localCertInfo
						.getCreateTime());
				localPreparedStatement1.setString(13, localCertInfo
						.getApplicantUppercase());
				localPreparedStatement1.setString(14, localCertInfo
						.getApplicant());
				localPreparedStatement1.setString(15, localCertInfo.getEmail());
				localPreparedStatement1
						.setString(16, localCertInfo.getRemark());
				localPreparedStatement1.setLong(17, localCertInfo
						.getAuthCodeUpdateTime());
				localPreparedStatement1.setString(18, localCertInfo
						.getSignServer());
				localPreparedStatement1.setString(19, localCertInfo
						.getSignClient());
				localPreparedStatement1.setString(20, localCertInfo
						.getIsRetainKey());
				localPreparedStatement1.setString(21, localCertInfo
						.getIswaiting());
				localPreparedStatement1.setString(22, localCertInfo.getOldSN());
				localPreparedStatement1.executeUpdate();
				String str2 = "select certentity from certarc where certsn=? for update of certentity";
				localPreparedStatement2 = paramConnection
						.prepareStatement(str2);
				localPreparedStatement2.setString(1, localCertInfo.getCertSN());
				localResultSet = localPreparedStatement2.executeQuery();
				localResultSet.next();
				BLOB localBLOB = ((OracleResultSet) localResultSet).getBLOB(1);
				if (localCertInfo.getCertEntity() == null)
					continue;
				localOutputStream = localBLOB.getBinaryOutputStream();
				localOutputStream.write(localCertInfo.getCertEntity());
				localOutputStream.flush();
				localOutputStream.close();
			}
			localPreparedStatement1.executeBatch();
			i = paramArrayList.size();
		} catch (Exception localException) {
			localException.printStackTrace();
		} finally {
			if (localOutputStream != null)
				try {
					localOutputStream.close();
				} catch (IOException localIOException) {
					localIOException.printStackTrace();
				}
			if (localPreparedStatement1 != null)
				try {
					localPreparedStatement1.close();
				} catch (SQLException localSQLException) {
					localSQLException.printStackTrace();
				}
		}
		return i;
	}

	public Vector getCertArcInfo(Properties paramProperties, int paramInt1,
			int paramInt2, boolean paramBoolean) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		int i = 0;
		CertInfo[] arrayOfCertInfo = null;
		Vector localVector1 = new Vector();
		Vector localVector2 = new Vector();
		String str3 = "";
		Enumeration localEnumeration = null;
		String[][] arrayOfString = (String[][]) null;
		try {
			if (paramProperties == null) {
				return localVector1;
			}
			String str2 = "select /*+first_rows*/ count(ctmlname) from certarc";
			int k = paramProperties.size();
			Object localObject1 = paramProperties.getProperty("ctmlName");
			if ((localObject1 != null)
					&& (((String) localObject1).indexOf("|") >= 0)) {
				StringTokenizer localObject2 = new StringTokenizer(
						(String) localObject1, "|");
				k += ((StringTokenizer) localObject2).countTokens();
			}
			Object localObject2 = paramProperties.getProperty("certStatus");
			StringTokenizer localStringTokenizer;
			if ((localObject2 != null)
					&& (((String) localObject2).indexOf("|") >= 0)) {
				localStringTokenizer = new StringTokenizer(
						(String) localObject2, "|");
				k += localStringTokenizer.countTokens();
			}
			arrayOfString = new String[k][2];
			localEnumeration = paramProperties.propertyNames();
			if (localEnumeration.hasMoreElements()) {
				str3 = " where ";
				while (localEnumeration.hasMoreElements()) {
					String str1 = localEnumeration.nextElement().toString();
					arrayOfString[i][0] = paramProperties.getProperty(str1);
					if (str1.equals("createTimeStart")) {
						str3 = str3 + "createTime" + ">=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("createTimeEnd")) {
						str3 = str3 + "createTime" + "<=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("ctmlName")) {
						if (arrayOfString[i][0].indexOf("|") >= 0) {
							localStringTokenizer = new StringTokenizer(
									arrayOfString[i][0], "|");
							k = 0;
							for (str3 = str3 + str1 + " in ("; localStringTokenizer
									.hasMoreTokens(); str3 = str3 + "?,") {
								arrayOfString[i][0] = localStringTokenizer
										.nextToken();
								arrayOfString[i][1] = "S";
								i++;
							}
							i--;
							str3 = str3.substring(0, str3.length() - 1)
									+ ") and ";
						} else {
							str3 = str3 + str1 + "=? and ";
							arrayOfString[i][1] = "S";
						}
					} else if (str1.equals("certStatus")) {
						if (arrayOfString[i][0].indexOf("|") >= 0) {
							localStringTokenizer = new StringTokenizer(
									arrayOfString[i][0], "|");
							for (str3 = str3 + str1 + " in ("; localStringTokenizer
									.hasMoreTokens(); str3 = str3 + "?,") {
								arrayOfString[i][0] = localStringTokenizer
										.nextToken();
								arrayOfString[i][1] = "S";
								i++;
							}
							i--;
							str3 = str3.substring(0, str3.length() - 1)
									+ ") and ";
						} else {
							str3 = str3 + str1 + "=? and ";
							arrayOfString[i][1] = "S";
						}
					} else if (str1.equals("subject")) {
						if (!paramBoolean) {
							arrayOfString[i][0] = ("%"
									+ setEscape2(arrayOfString[i][0]) + "%");
							str3 = str3 + "subjectuppercase"
									+ " like ? escape '\\' and ";
						} else {
							str3 = str3 + "subjectuppercase" + "=? and ";
						}
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else if (str1.equals("applicant")) {
						str3 = str3 + "applicantuppercase" + "=? and ";
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else if (str1.equals("certSN")) {
						str3 = str3 + "certarc." + "certSN" + "=? and ";
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else if (str1.equals("cdpid")) {
						str3 = str3 + "certarc." + "cdpid" + "=? and ";
						arrayOfString[i][1] = "S";
					} else if ((str1.equals("notBefore"))
							|| (str1.equals("notAfter"))
							|| (str1.equals("validity"))
							|| (str1.equals("cdpid"))
							|| (str1.equals("isvalid"))
							|| (str1.equals("createTime"))) {
						str3 = str3 + str1 + "=? and ";
						arrayOfString[i][1] = "L";
					} else {
						str3 = str3 + str1 + "=? and ";
						arrayOfString[i][1] = "S";
					}
					i++;
				}
				str3 = str3.substring(0, str3.length() - 5);
			}
			localConnection = DriverManager.getConnection("proxool.ida");
			str2 = str2 + str3;
			localPreparedStatement = localConnection.prepareStatement(str2
					.toLowerCase());
			for (int j = 0; j < i; j++)
				if (arrayOfString[j][1].equals("L"))
					localPreparedStatement.setLong(j + 1, Long
							.parseLong(arrayOfString[j][0]));
				else
					localPreparedStatement
							.setString(j + 1, arrayOfString[j][0]);
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			long l = localResultSet.getLong(1);
			localVector1.add(Long.toString(l));
			if (paramInt1 < 1)
				paramInt1 = 1;
			if (orderby)
				str3 = str3 + " order by createtime desc";
			str2 = "select /*+first_rows*/ certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark from certarc ";
			if (paramInt2 < 1)
				str2 = str2 + str3;
			else
				str2 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark from (select rownum as my_rownum,table_a.* from ("
						+ str2
						+ str3
						+ ") table_a where rownum<?) where my_rownum>=?";
			localPreparedStatement = localConnection.prepareStatement(str2
					.toLowerCase());
			int j = 0;
			for (j = 0; j < i; j++)
				if (arrayOfString[j][1].equals("L"))
					localPreparedStatement.setLong(j + 1, Long
							.parseLong(arrayOfString[j][0]));
				else
					localPreparedStatement
							.setString(j + 1, arrayOfString[j][0]);
			if (paramInt2 > 0) {
				localPreparedStatement.setInt(j + 1, paramInt1 + paramInt2);
				localPreparedStatement.setInt(j + 2, paramInt1);
			}
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next())
				localVector2.add(getCertinfoArcCondition(localResultSet));
			arrayOfCertInfo = new CertInfo[localVector2.size()];
			localVector2.toArray(arrayOfCertInfo);
			localVector1.add(arrayOfCertInfo);
			return localVector1;
		} catch (SQLException localSQLException1) {
			Vector localVector3;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8075", "获取归档证书信息失败", localSQLException1);
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

	public Vector getOperationLogArcInfo(Properties paramProperties,
			int paramInt1, int paramInt2, boolean paramBoolean)
			throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		int i = 0;
		int k = -1;
		Operation localOperation = null;
		Operation[] arrayOfOperation = null;
		Vector localVector1 = new Vector();
		Vector localVector2 = new Vector();
		String str3 = "";
		Enumeration localEnumeration = null;
		String[][] arrayOfString = (String[][]) null;
		Object localObject1 = null;
		try {
			Vector localVector3;
			if (paramProperties == null) {
				return localVector1;
			}
			String str2 = "select /*+first_rows*/ count(operatorsubjectuppercase) from operationlogarc";
			arrayOfString = new String[paramProperties.size()][2];
			localEnumeration = paramProperties.propertyNames();
			if (localEnumeration.hasMoreElements()) {
				str3 = " where ";
				while (localEnumeration.hasMoreElements()) {
					String str1 = localEnumeration.nextElement().toString();
					arrayOfString[i][0] = paramProperties.getProperty(str1);
					if (str1.equals("optTimeBegin")) {
						str3 = str3 + "optTime" + ">=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("optTimeEnd")) {
						str3 = str3 + "optTime" + "<=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("result")) {
						str3 = str3 + str1 + "=? and ";
						arrayOfString[i][1] = "L";
					} else if (str1.equals("objectSubject")) {
						if (!paramBoolean) {
							arrayOfString[i][0] = ("%"
									+ setEscape2(arrayOfString[i][0]) + "%");
							str3 = str3 + "objectsubjectuppercase"
									+ " like ? escape '\\' and ";
						} else {
							str3 = str3 + "objectsubjectuppercase" + "=? and ";
						}
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else if (str1.equals("operatorSubject")) {
						str3 = str3 + "operatorsubjectuppercase" + "=? and ";
						arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
						arrayOfString[i][1] = "S";
					} else {
						str3 = str3 + str1 + "=? and ";
						arrayOfString[i][1] = "S";
					}
					i++;
				}
				str3 = str3.substring(0, str3.length() - 5);
			}
			localConnection = DriverManager.getConnection("proxool.ida");
			str2 = str2 + str3;
			localPreparedStatement = localConnection.prepareStatement(str2
					.toLowerCase());
			for (int j = 0; j < i; j++)
				if (arrayOfString[j][1].equals("L"))
					localPreparedStatement.setLong(j + 1, Long
							.parseLong(arrayOfString[j][0]));
				else
					localPreparedStatement
							.setString(j + 1, arrayOfString[j][0]);
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			long l = localResultSet.getLong(1);
			localVector1.add(Long.toString(l));
			if (paramInt1 < 1)
				paramInt1 = 1;
			str2 = "select /*+first_rows*/operatorsn,operatorsubject,objectcertsn,objectsubject,objectctmlname,opttype,opttime,result from operationlogarc ";
			if (orderby)
				str3 = str3 + " order by opttime desc";
			if (paramInt2 < 1)
				str2 = str2 + str3;
			else
				str2 = "select operatorsn,operatorsubject,objectcertsn,objectsubject,objectctmlname,opttype,opttime,result from (select rownum as my_rownum,table_a.* from ("
						+ str2
						+ str3
						+ ") table_a where rownum<?) where my_rownum>=?";
			localPreparedStatement = localConnection.prepareStatement(str2
					.toLowerCase());
			int j = 0;
			for (j = 0; j < i; j++)
				if (arrayOfString[j][1].equals("L"))
					localPreparedStatement.setLong(j + 1, Long
							.parseLong(arrayOfString[j][0]));
				else
					localPreparedStatement
							.setString(j + 1, arrayOfString[j][0]);
			if (paramInt2 > 0) {
				localPreparedStatement.setInt(j + 1, paramInt1 + paramInt2);
				localPreparedStatement.setInt(j + 2, paramInt1);
			}
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next()) {
				localOperation = new Operation();
				localOperation.setOperatorSN(localResultSet.getString(1));
				localOperation.setOperatorDN(localResultSet.getString(2));
				localOperation.setObjCertSN(localResultSet.getString(3));
				localOperation.setObjSubject(localResultSet.getString(4));
				localOperation.setObjCTMLName(localResultSet.getString(5));
				localOperation.setOptType(localResultSet.getString(6));
				localOperation.setOptTime(localResultSet.getLong(7));
				localOperation.setResult(localResultSet.getInt(8));
				localVector2.add(localOperation);
			}
			arrayOfOperation = new Operation[localVector2.size()];
			localVector2.toArray(arrayOfOperation);
			localVector1.add(arrayOfOperation);
			return localVector1;
		} catch (SQLException localSQLException1) {
			Vector localVector4;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8076", "获取归档操作日志信息失败", localSQLException1);
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

	public long getCertCountForIsvaild() throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		long l1 = -1L;
		try {
			String str = "select /*+index(cert pk_cert)*/ count(certsn) from cert  where isvalid = 1";
			localConnection = DriverManager.getConnection("proxool.ida");
			localPreparedStatement = localConnection.prepareStatement(str);
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			l1 = localResultSet.getLong(1);
			return l1;
		} catch (SQLException localSQLException1) {
			long l2;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8606", "依据证书是否有效获取证书总数失败",
					localSQLException1);
		} finally {
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException2) {
				}
			if (localConnection != null)
				try {
					localConnection.close();
					localConnection = null;
				} catch (SQLException localSQLException3) {
				}
		}
	}
}
