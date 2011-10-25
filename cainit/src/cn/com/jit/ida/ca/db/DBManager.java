package cn.com.jit.ida.ca.db;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CRLInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertExtensions;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertPendingRevokeInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertRevokeInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertSelfExt;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertStandardExt;
import cn.com.jit.ida.ca.certmanager.reqinfo.Extension;
import cn.com.jit.ida.ca.certmanager.reqinfo.RevokeCert;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.DBConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTMLPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTMLPolicy.Attribute;
import cn.com.jit.ida.ca.ctml.x509v3.extension.StandardExtension;
import cn.com.jit.ida.ca.updataRootCert.RevokeCertInfo;
import cn.com.jit.ida.globalconfig.DBInterface;
import cn.com.jit.ida.globalconfig.Information;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.SysLogger;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.TemplateAdmin;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;

public class DBManager implements DBInterface {
	private static DBManager instance = null;
	private static DBConfig dbConfig = null;
	private static String dbtype = null;
	private static String testSQL = null;
	protected static SysLogger syslogger = LogManager.getSysLogger();
	protected static long certsum = 0L;
	protected static boolean orderby = true;
	protected static int maxCount = 1000;
	private static final String select_crl = "select crl_name,crl_entity from crl";
	protected static final String tmp_sign_server = "tmp_signServer";
	protected static final String tmp_sign_client = "tmp_signClient";
	protected static final String select_certs = "select certsn,subjectuppercase,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicantuppercase,applicant,certentity,email,remark,authcode_updatetime,sign_server,sign_client,isretainkey,iswaiting,oldsn from cert";
	protected static final String select_operationlog = "select id,operatorsn,operatorsubject,objectcertsn,objectsubject,objectctmlname,opttype,opttime,result,sign_server,sign_client from operationlog";
	protected static final String insert_certarc = "insert into certarc values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	protected static final String insert_operationlogarc = "insert into operationlogarc values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public static synchronized DBManager getInstance() throws DBException {
		if (instance == null)
			try {
				try {
					dbConfig = DBConfig.getInstance();
				} catch (IDAException localIDAException) {
					throw new DBException("8001", "数据库连接信息不正确",
							localIDAException);
				}
				String str = dbConfig.getURL();
				int i = str.indexOf(":");
				if (i < 1)
					throw new DBException("8052", "连接数据库的URL不正确");
				str = str.substring(i + 1);
				int j = str.indexOf(":");
				if (j < 1)
					throw new DBException("8052", "连接数据库的URL不正确");
				dbtype = str.substring(0, j);
				if (dbtype.equalsIgnoreCase("oracle")) {
//					testSQL = "select sysdate from dual";
					instance = new Oracle();
				} else if (dbtype.equalsIgnoreCase("mysql")) {
					testSQL = "select 1";
					instance = new MySQL();
				} else if (dbtype.equalsIgnoreCase("microsoft")) {
					testSQL = "select day(0)";
					instance = new MSSQL();
				} else if (dbtype.equals("db2")) {
					testSQL = "values current date";
					instance = new DB2();
				} else if (dbtype.equals("sybase")) {
					testSQL = "select getdate()";
					instance = new Sybase();
				} else {
					testSQL = " ";
					instance = new DBManager();
				}
			} catch (DBException localDBException) {
				close();
				throw localDBException;
			}
		return instance;
	}
	public void updateAdminConfig(String sn, String dn){
		
	}
	public Object byteToObject(byte[] bytes) {
        java.lang.Object obj = new java.lang.Object();
        try {
            // bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);
            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }
	public byte[] getTemplateInfo(){
		return null;
	}
	private void clearLog() {
		String str = "commons-logging";
		Appender localAppender = Logger.getRootLogger().getAppender(str);
		if (localAppender != null)
			Logger.getRootLogger().removeAppender(str);
	}

	protected DBManager() throws DBException {
		try {
			Properties localProperties = new Properties();
			localProperties.setProperty("proxool.maximum-connection-count",
					Integer.toString(20));
//			localProperties.setProperty("proxool.house-keeping-test-sql",
//					testSQL);
			localProperties.setProperty("user", dbConfig.getUser());
			localProperties.setProperty("password", dbConfig.getPassword());
			String str1 = "ida";
			String str2 = dbConfig.getDriverClass();
			String str3 = dbConfig.getURL();
			String str4 = "proxool." + str1 + ":" + str2 + ":" + str3;
			Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
			clearLog();
			ProxoolFacade.registerConnectionPool(str4, localProperties);
			try {
				Connection localConnection = DriverManager
						.getConnection("proxool.ida");
				localConnection.close();
			} catch (SQLException localSQLException) {
				throw new DBException("8062", "连接数据库失败", localSQLException);
			}
//			refrushCertAccount();
		} catch (DBException localDBException) {
			throw localDBException;
		} catch (ClassNotFoundException localClassNotFoundException) {
			throw new DBException("8002", "连接池类找不到",
					localClassNotFoundException);
		} catch (ProxoolException localProxoolException) {
			throw new DBException("8003", "连接池初始化失败", localProxoolException);
		}
	}

	public static void close() {
		ProxoolFacade.shutdown(0);
		instance = null;
	}

	public void refreshConfig() throws DBException {
		try {
			InternalConfig localInternalConfig = InternalConfig.getInstance();
			orderby = localInternalConfig.getIsOrderDB();
			CAConfig localCAConfig = CAConfig.getInstance();
			maxCount = localCAConfig.getMaxCountPerPage();
		} catch (IDAException localIDAException) {
			localIDAException.printStackTrace();
			throw new DBException("8063", "获取全局配置信息失败", localIDAException);
		}
	}

	public String getDBVersion() {
		return "";
	}

	public final long getTime() {
		Date localDate = new Date();
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyyMMddHHmmssSSS");
		return Long.parseLong(localSimpleDateFormat.format(localDate));
	}

	protected final String getRandom(int paramInt) {
		char[] arrayOfChar = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				.toCharArray();
		StringBuffer localStringBuffer = new StringBuffer();
		int i = arrayOfChar.length;
		if (paramInt < 10)
			paramInt = 10;
		for (int j = 0; j < paramInt; j++) {
			int k = (int) (Math.random() * i);
			if ((j == 0) && (k == 0))
				j--;
			else
				localStringBuffer.append(arrayOfChar[k]);
		}
		return localStringBuffer.toString();
	}

	public final long getRandom() {
		char[] arrayOfChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		int i = 16;
		StringBuffer localStringBuffer = new StringBuffer();
		int j = arrayOfChar.length;
		for (int k = 0; k < i; k++) {
			int m = (int) (Math.random() * j);
			if ((k == 0) && (m == 0))
				k--;
			else
				localStringBuffer.append(arrayOfChar[m]);
		}
		return Long.parseLong(localStringBuffer.toString());
	}

	protected String setEscape4(String paramString) {
		if (paramString.indexOf("%") >= 0)
			paramString = paramString.replaceAll("%", "\\\\%");
		if (paramString.indexOf("_") >= 0)
			paramString = paramString.replaceAll("_", "\\\\_");
		if (paramString.indexOf("[") >= 0)
			paramString = paramString.replaceAll("[", "\\\\[");
		if (paramString.indexOf("]") >= 0)
			paramString = paramString.replaceAll("]", "\\\\]");
		return paramString;
	}

	protected String setEscape2(String paramString) {
		if (paramString.indexOf("%") >= 0)
			paramString = paramString.replaceAll("%", "\\\\%");
		if (paramString.indexOf("％") >= 0)
			paramString = paramString.replaceAll("％", "\\\\％");
		if (paramString.indexOf("_") >= 0)
			paramString = paramString.replaceAll("_", "\\\\_");
		if (paramString.indexOf("＿") >= 0)
			paramString = paramString.replaceAll("＿", "\\\\＿");
		return paramString;
	}

	protected synchronized void addCertSUM() {
		certsum += 1L;
	}

	protected synchronized void decCertSUM() {
		certsum -= 1L;
	}

	protected synchronized void setCertSUM(long paramLong) {
		certsum = paramLong;
	}

	public synchronized long getCertAccount() {
		return certsum;
	}

	public void refrushCertAccount() throws DBException {
		Connection localConnection = null;
		Statement localStatement = null;
		ResultSet localResultSet = null;
		long l2 = 0L;
		String str = null;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			str = "select count(certsn) from cert";
			localStatement = localConnection.createStatement();
			localResultSet = localStatement.executeQuery(str);
			localResultSet.next();
			long l1 = localResultSet.getLong(1);
			str = "select count(certsn) from certarc";
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

	public int saveCTML(Properties paramProperties)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    Statement localStatement = null;
    ResultSet localResultSet = null;
    String str2 = -1;
    try
    {
      int i;
      if (paramProperties == null)
      {
        i = str2;
        jsr 525;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      localConnection.setAutoCommit(false);
      String str1 = "insert into ctml values(?,?,?,?,?,?,?,?,?,?)";
      localPreparedStatement = localConnection.prepareStatement(str1);
      localPreparedStatement.setString(1, paramProperties.getProperty("ctml_name", null));
      localPreparedStatement.setString(2, paramProperties.getProperty("ctml_id", null));
      localPreparedStatement.setString(3, paramProperties.getProperty("ctml_type", null));
      localPreparedStatement.setString(4, paramProperties.getProperty("ctml_status", null));
      localPreparedStatement.setString(5, paramProperties.getProperty("ctml_description", null));
      String str3 = paramProperties.getProperty("ctml_policyinfo", "");
      localPreparedStatement.setCharacterStream(6, new CharArrayReader(str3.toCharArray()), str3.length());
      localPreparedStatement.setString(7, paramProperties.getProperty("reserve", null));
      localPreparedStatement.setLong(8, getTime());
      localPreparedStatement.setString(9, "tmp_signServer");
      localPreparedStatement.setString(10, "tmp_signClient");
      str2 = localPreparedStatement.executeUpdate();
      if (!paramProperties.getProperty("ctml_name").equals("0"))
      {
        str4 = null;
        String str5 = null;
        str1 = "select value from config where modulename='CAConfig' and property='CAAdminSN'";
        localStatement = localConnection.createStatement();
        localResultSet = localStatement.executeQuery(str1);
        if (localResultSet.next())
          str4 = localResultSet.getString(1);
        X509V3CTMLPolicy localX509V3CTMLPolicy = new X509V3CTMLPolicy(paramProperties.getProperty("ctml_policyinfo").getBytes());
        if ((localX509V3CTMLPolicy.getAttribute().attribute & 1L) != 0L)
        {
          str5 = null;
        }
        else
        {
          str1 = "select value from config where modulename='CAConfig' and property='BaseDN'";
          localStatement = localConnection.createStatement();
          localResultSet = localStatement.executeQuery(str1);
          if (localResultSet.next())
            str5 = localResultSet.getString(1);
        }
        if (str4 != null)
          str2 = setTemplateAdmin(localConnection, str4, paramProperties.getProperty("ctml_name", null), str5, null);
      }
      localConnection.commit();
      str4 = str2;
    }
    catch (DBException localDBException)
    {
      String str4;
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException2)
        {
        }
      throw localDBException;
    }
    catch (IDAException localIDAException)
    {
      syslogger.info("数据库异常：" + localIDAException.getMessage());
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException3)
        {
        }
      throw new DBException("8004", "调用X509转换失败", localIDAException);
    }
    catch (SQLException localSQLException1)
    {
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException4)
        {
        }
      throw new DBException("8004", "增加模板失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException5)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException6)
        {
        }
    }
  }

	public int modifyCTML(Properties paramProperties1, Properties paramProperties2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    Enumeration localEnumeration = null;
    int i = 0;
    int j = -1;
    String[] arrayOfString = null;
    try
    {
      if ((paramProperties1 == null) || (paramProperties2 == null))
      {
        k = j;
        jsr 409;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      String str1 = "update ctml set ";
      localEnumeration = paramProperties2.propertyNames();
      arrayOfString = new String[paramProperties1.size() + paramProperties2.size()];
      String str2;
      while (localEnumeration.hasMoreElements())
      {
        str2 = localEnumeration.nextElement().toString();
        arrayOfString[(i++)] = paramProperties2.getProperty(str2, "null");
        str1 = str1 + str2 + "=?,";
      }
      str1 = str1.substring(0, str1.length() - 1);
      localEnumeration = paramProperties1.propertyNames();
      if (localEnumeration.hasMoreElements());
      for (str1 = str1 + " where "; localEnumeration.hasMoreElements(); str1 = str1 + str2 + "=? and ")
      {
        str2 = localEnumeration.nextElement().toString();
        arrayOfString[(i++)] = paramProperties1.getProperty(str2, "null");
      }
      str1 = str1.substring(0, str1.length() - 5);
      localPreparedStatement = localConnection.prepareStatement(str1.toLowerCase());
      for (k = 0; k < i; k++)
        if (arrayOfString[k].getBytes().length > 1000)
          localPreparedStatement.setCharacterStream(k + 1, new CharArrayReader(arrayOfString[k].toCharArray()), arrayOfString[k].length());
        else
          localPreparedStatement.setString(k + 1, arrayOfString[k]);
      j = localPreparedStatement.executeUpdate();
      k = j;
    }
    catch (SQLException localSQLException1)
    {
      int k;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8016", "修改模板失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public Properties[] getCTML(Properties paramProperties)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    Enumeration localEnumeration = null;
    Properties localProperties = null;
    Properties[] arrayOfProperties1 = null;
    String[] arrayOfString = null;
    String str1 = "";
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    try
    {
      Object localObject1;
      if (paramProperties == null)
      {
        localObject1 = null;
        jsr 614;
      }
      str1 = "select ctml_name,ctml_id,ctml_type,ctml_status,ctml_description,ctml_policyinfo,reserve from ctml";
      localEnumeration = paramProperties.propertyNames();
      arrayOfString = new String[paramProperties.size()];
      if (localEnumeration.hasMoreElements())
        localStringBuffer.append(" where ");
      while (localEnumeration.hasMoreElements())
      {
        String str2 = localEnumeration.nextElement().toString();
        arrayOfString[i] = paramProperties.getProperty(str2);
        if (str2.equalsIgnoreCase("ctml_name"))
        {
          if (arrayOfString[i].indexOf("*") >= 0)
          {
            arrayOfString[i] = arrayOfString[i].replace('*', '%');
            localStringBuffer.append("ctml_name like ? and ");
          }
          else
          {
            localStringBuffer.append("ctml_name=? and ");
          }
        }
        else
          localStringBuffer.append(str2 + "=? and ");
        i++;
      }
      if (i > 0)
        str1 = str1 + localStringBuffer.substring(0, localStringBuffer.length() - 5);
      str1 = str1 + " order by createtime desc";
      localConnection = DriverManager.getConnection("proxool.ida");
      localPreparedStatement = localConnection.prepareStatement(str1.toLowerCase());
      for (int j = 0; j < arrayOfString.length; j++)
        localPreparedStatement.setString(j + 1, arrayOfString[j]);
      localResultSet = localPreparedStatement.executeQuery();
      i = 0;
      Vector localVector = new Vector();
      while (localResultSet.next())
      {
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
        str3 = localResultSet.getString(6);
        if (str3 != null)
          localProperties.setProperty("ctml_policyinfo", str3);
        str3 = localResultSet.getString(7);
        if (str3 != null)
          localProperties.setProperty("reserve", str3);
        localVector.add(localProperties);
      }
      arrayOfProperties1 = new Properties[localVector.size()];
      for (int k = 0; k < localVector.size(); k++)
        arrayOfProperties1[k] = ((Properties)localVector.get(k));
      arrayOfProperties2 = arrayOfProperties1;
    }
    catch (SQLException localSQLException1)
    {
      Properties[] arrayOfProperties2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8008", "获取模板失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int deleteCTML(Properties paramProperties)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement1 = null;
    PreparedStatement localPreparedStatement2 = null;
    ResultSet localResultSet1 = null;
    ResultSet localResultSet2 = null;
    int i = 0;
    String str4 = null;
    String[] arrayOfString = null;
    int j = -1;
    try
    {
      int k;
      if (paramProperties == null)
      {
        k = j;
        jsr 451;
      }
      String str3 = "select ctml_name from ctml";
      Enumeration localEnumeration = paramProperties.propertyNames();
      arrayOfString = new String[paramProperties.size()];
      if (localEnumeration.hasMoreElements());
      String str1;
      for (str4 = " where "; localEnumeration.hasMoreElements(); str4 = str4 + str1 + "=? and ")
      {
        str1 = localEnumeration.nextElement().toString();
        arrayOfString[(i++)] = paramProperties.getProperty(str1, null);
      }
      str4 = str4.substring(0, str4.length() - 5);
      str3 = str3 + str4.toLowerCase();
      localConnection = DriverManager.getConnection("proxool.ida");
      localPreparedStatement1 = localConnection.prepareStatement(str3);
      for (m = 0; m < i; m++)
        localPreparedStatement1.setString(m + 1, arrayOfString[m]);
      localResultSet2 = localPreparedStatement1.executeQuery();
      while (localResultSet2.next())
      {
        String str2 = localResultSet2.getString(1);
        str3 = "select ctmlname from cert where ctmlname=?";
        localPreparedStatement2 = localConnection.prepareStatement(str3);
        localPreparedStatement2.setString(1, str2);
        localResultSet1 = localPreparedStatement2.executeQuery();
        if (localResultSet1.next())
          throw new DBException("8042", "删除模板失败：模板已被使用");
        str3 = "delete from ba_privilege where ctmlname=?";
        localPreparedStatement2 = localConnection.prepareStatement(str3);
        localPreparedStatement2.setString(1, str2);
        localPreparedStatement2.execute();
      }
      str3 = "delete from ctml" + str4;
      localPreparedStatement1 = localConnection.prepareStatement(str3.toLowerCase());
      for (m = 0; m < i; m++)
        localPreparedStatement1.setString(m + 1, arrayOfString[m]);
      j = localPreparedStatement1.executeUpdate();
      m = j;
    }
    catch (DBException localDBException)
    {
      int m;
      throw localDBException;
    }
    catch (SQLException localSQLException1)
    {
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8041", "删除模板失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement1 != null)
        try
        {
          localPreparedStatement1.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int saveSelfExt(Properties paramProperties)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    int i = -1;
    try
    {
      int j;
      if (paramProperties == null)
      {
        j = i;
        jsr 213;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      String str = "insert into selfext values(?,?,?,?,?,?,?,?,?)";
      localPreparedStatement = localConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramProperties.getProperty("selfext_name", null));
      localPreparedStatement.setString(2, paramProperties.getProperty("selfext_oid", null));
      localPreparedStatement.setString(3, paramProperties.getProperty("selfext_status", null));
      localPreparedStatement.setString(4, paramProperties.getProperty("selfext_encodetype", null));
      localPreparedStatement.setString(5, paramProperties.getProperty("selfext_description", null));
      localPreparedStatement.setString(6, paramProperties.getProperty("reserve", null));
      localPreparedStatement.setLong(7, getTime());
      localPreparedStatement.setString(8, "tmp_signServer");
      localPreparedStatement.setString(9, "tmp_signClient");
      i = localPreparedStatement.executeUpdate();
      k = i;
    }
    catch (SQLException localSQLException1)
    {
      int k;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8007", "保存自定义扩展失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int modifySelfExt(Properties paramProperties1, Properties paramProperties2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    int i = 0;
    int j = -1;
    String[] arrayOfString = null;
    try
    {
      int k;
      if ((paramProperties1 == null) || (paramProperties2 == null))
      {
        k = j;
        jsr 358;
      }
      String str1 = "update selfext set ";
      Enumeration localEnumeration1 = paramProperties2.propertyNames();
      arrayOfString = new String[paramProperties1.size() + paramProperties2.size()];
      String str2;
      while (localEnumeration1.hasMoreElements())
      {
        str2 = localEnumeration1.nextElement().toString();
        arrayOfString[(i++)] = paramProperties2.getProperty(str2);
        str1 = str1 + str2 + "=?,";
      }
      str1 = str1.substring(0, str1.length() - 1);
      Enumeration localEnumeration2 = paramProperties1.propertyNames();
      if (localEnumeration2.hasMoreElements());
      for (str1 = str1 + " where "; localEnumeration2.hasMoreElements(); str1 = str1 + str2 + "=? and ")
      {
        str2 = localEnumeration2.nextElement().toString();
        arrayOfString[(i++)] = paramProperties1.getProperty(str2);
      }
      if (i > 0)
        str1 = str1.substring(0, str1.length() - 5).toLowerCase();
      localConnection = DriverManager.getConnection("proxool.ida");
      localPreparedStatement = localConnection.prepareStatement(str1);
      for (m = 0; m < i; m++)
        localPreparedStatement.setString(m + 1, arrayOfString[m]);
      j = localPreparedStatement.executeUpdate();
      m = j;
    }
    catch (SQLException localSQLException1)
    {
      int m;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8017", "修改自定义扩展失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public Properties[] getSelfExt(Properties paramProperties)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    int i = 0;
    Properties localProperties = null;
    Properties[] arrayOfProperties1 = null;
    String str2 = null;
    StringBuffer localStringBuffer = new StringBuffer();
    String[] arrayOfString = null;
    try
    {
      if (paramProperties == null)
      {
        localEnumeration = null;
        jsr 537;
      }
      localStringBuffer.append("select selfext_name,selfext_oid,selfext_status,");
      localStringBuffer.append("selfext_encodetype,selfext_description,reserve from selfext");
      Enumeration localEnumeration = paramProperties.propertyNames();
      arrayOfString = new String[paramProperties.size()];
      if (localEnumeration.hasMoreElements())
        localStringBuffer.append(" where ");
      while (localEnumeration.hasMoreElements())
      {
        String str1 = localEnumeration.nextElement().toString();
        arrayOfString[i] = paramProperties.getProperty(str1);
        localStringBuffer.append(str1 + "=? and ");
        i++;
      }
      if (i > 0)
        str2 = new String(localStringBuffer.substring(0, localStringBuffer.length() - 5));
      else
        str2 = new String(localStringBuffer);
      str2 = str2.toLowerCase() + " order by createtime desc";
      localConnection = DriverManager.getConnection("proxool.ida");
      localPreparedStatement = localConnection.prepareStatement(str2);
      for (int j = 0; j < i; j++)
        localPreparedStatement.setString(j + 1, arrayOfString[j]);
      localResultSet = localPreparedStatement.executeQuery();
      Vector localVector = new Vector();
      while (localResultSet.next())
      {
        localProperties = new Properties();
        String str3 = localResultSet.getString(1);
        if (str3 != null)
          localProperties.setProperty("selfext_name", str3);
        str3 = localResultSet.getString(2);
        if (str3 != null)
          localProperties.setProperty("selfext_oid", str3);
        str3 = localResultSet.getString(3);
        if (str3 != null)
          localProperties.setProperty("selfext_status", str3);
        str3 = localResultSet.getString(4);
        if (str3 != null)
          localProperties.setProperty("selfext_encodetype", str3);
        str3 = localResultSet.getString(5);
        if (str3 != null)
          localProperties.setProperty("selfext_description", str3);
        str3 = localResultSet.getString(6);
        if (str3 != null)
          localProperties.setProperty("reserve", str3);
        localVector.add(localProperties);
      }
      arrayOfProperties1 = new Properties[localVector.size()];
      for (int k = 0; k < localVector.size(); k++)
        arrayOfProperties1[k] = ((Properties)localVector.get(k));
      arrayOfProperties2 = arrayOfProperties1;
    }
    catch (SQLException localSQLException1)
    {
      Properties[] arrayOfProperties2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8015", "获取自定义扩展失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int deleteSelfExt(Properties paramProperties)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement1 = null;
    PreparedStatement localPreparedStatement2 = null;
    ResultSet localResultSet1 = null;
    ResultSet localResultSet2 = null;
    int i = 0;
    String str3 = null;
    String[] arrayOfString = null;
    int j = -1;
    try
    {
      int k;
      if (paramProperties == null)
      {
        k = j;
        jsr 416;
      }
      String str2 = "select selfext_name from selfext";
      Enumeration localEnumeration = paramProperties.propertyNames();
      arrayOfString = new String[paramProperties.size()];
      if (localEnumeration.hasMoreElements());
      String str1;
      for (str3 = " where "; localEnumeration.hasMoreElements(); str3 = str3 + str1 + "=? and ")
      {
        str1 = localEnumeration.nextElement().toString();
        arrayOfString[(i++)] = paramProperties.getProperty(str1, null);
      }
      str3 = str3.substring(0, str3.length() - 5);
      str2 = str2 + str3.toLowerCase();
      localConnection = DriverManager.getConnection("proxool.ida");
      localPreparedStatement1 = localConnection.prepareStatement(str2);
      for (m = 0; m < i; m++)
        localPreparedStatement1.setString(m + 1, arrayOfString[m]);
      localResultSet2 = localPreparedStatement1.executeQuery();
      while (localResultSet2.next())
      {
        str2 = "select selfext_name from cert_selfext where selfext_name=?";
        localPreparedStatement2 = localConnection.prepareStatement(str2);
        localPreparedStatement2.setString(1, localResultSet2.getString(1));
        localResultSet1 = localPreparedStatement2.executeQuery();
        if (!localResultSet1.next())
          continue;
        throw new DBException("8049", "删除自定义扩展失败：此记录仍被使用");
      }
      str2 = "delete from selfext" + str3;
      str2 = str2.toLowerCase();
      localPreparedStatement1 = localConnection.prepareStatement(str2);
      for (m = 0; m < i; m++)
        localPreparedStatement1.setString(m + 1, arrayOfString[m]);
      j = localPreparedStatement1.executeUpdate();
      m = j;
    }
    catch (DBException localDBException)
    {
      int m;
      throw localDBException;
    }
    catch (SQLException localSQLException1)
    {
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8041", "删除模板失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement1 != null)
        try
        {
          localPreparedStatement1.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public CertInfo getCertInfo(String paramString)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    CertInfo localCertInfo1 = null;
    try
    {
      if (paramString == null)
      {
        localObject1 = localCertInfo1;
        jsr 373;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      Object localObject1 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark,authcode_updatetime,isretainkey,oldsn,iswaiting from cert where certsn=?";
      localPreparedStatement = localConnection.prepareStatement((String)localObject1);
      localPreparedStatement.setString(1, paramString);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        localCertInfo1 = new CertInfo();
        localCertInfo1.setCertSN(localResultSet.getString(1));
        localCertInfo1.setSubject(localResultSet.getString(2));
        localCertInfo1.setNotBefore(localResultSet.getLong(3));
        localCertInfo1.setNotAfter(localResultSet.getLong(4));
        localCertInfo1.setValidity(localResultSet.getInt(5));
        localCertInfo1.setAuthCode(localResultSet.getString(6));
        localCertInfo1.setCdpid(localResultSet.getInt(7));
        localCertInfo1.setCtmlName(localResultSet.getString(8));
        localCertInfo1.setCertStatus(localResultSet.getString(9));
        localCertInfo1.setIsValid(localResultSet.getLong(10));
        localCertInfo1.setCreateTime(localResultSet.getLong(11));
        localCertInfo1.setApplicant(localResultSet.getString(12));
        localCertInfo1.setEmail(localResultSet.getString(13));
        localCertInfo1.setRemark(localResultSet.getString(14));
        localCertInfo1.setAuthCodeUpdateTime(localResultSet.getLong(15));
        localCertInfo1.setIsRetainKey(localResultSet.getString(16));
        localCertInfo1.setOldSN(localResultSet.getString(17));
        localCertInfo1.setIswaiting(localResultSet.getString(18));
      }
      localCertInfo2 = localCertInfo1;
    }
    catch (SQLException localSQLException1)
    {
      CertInfo localCertInfo2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8012", "按序列号获取证书信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public CertInfo getCertInfo(String paramString1, String paramString2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    CertInfo localCertInfo1 = null;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        localObject1 = localCertInfo1;
        jsr 333;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      Object localObject1 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark from cert where subjectuppercase=? and ctmlname=? and isvalid=1";
      localPreparedStatement = localConnection.prepareStatement((String)localObject1);
      localPreparedStatement.setString(1, paramString1.toUpperCase());
      localPreparedStatement.setString(2, paramString2);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        localCertInfo1 = new CertInfo();
        localCertInfo1.setCertSN(localResultSet.getString(1));
        localCertInfo1.setSubject(localResultSet.getString(2));
        localCertInfo1.setNotBefore(localResultSet.getLong(3));
        localCertInfo1.setNotAfter(localResultSet.getLong(4));
        localCertInfo1.setValidity(localResultSet.getInt(5));
        localCertInfo1.setAuthCode(localResultSet.getString(6));
        localCertInfo1.setCdpid(localResultSet.getInt(7));
        localCertInfo1.setCtmlName(localResultSet.getString(8));
        localCertInfo1.setCertStatus(localResultSet.getString(9));
        localCertInfo1.setIsValid(localResultSet.getLong(10));
        localCertInfo1.setCreateTime(localResultSet.getLong(11));
        localCertInfo1.setApplicant(localResultSet.getString(12));
        localCertInfo1.setEmail(localResultSet.getString(13));
        localCertInfo1.setRemark(localResultSet.getString(14));
      }
      localCertInfo2 = localCertInfo1;
    }
    catch (SQLException localSQLException1)
    {
      CertInfo localCertInfo2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8013", "按主题获取证书信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public CertInfo getHoldCertInfo(String paramString1, String paramString2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    CertInfo localCertInfo1 = null;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        localObject1 = localCertInfo1;
        jsr 344;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      Object localObject1 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark from cert where subjectuppercase=? and ctmlname=? and certstatus=?";
      localPreparedStatement = localConnection.prepareStatement((String)localObject1);
      localPreparedStatement.setString(1, paramString1.toUpperCase());
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.setString(3, "Hold");
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        localCertInfo1 = new CertInfo();
        localCertInfo1.setCertSN(localResultSet.getString(1));
        localCertInfo1.setSubject(localResultSet.getString(2));
        localCertInfo1.setNotBefore(localResultSet.getLong(3));
        localCertInfo1.setNotAfter(localResultSet.getLong(4));
        localCertInfo1.setValidity(localResultSet.getInt(5));
        localCertInfo1.setAuthCode(localResultSet.getString(6));
        localCertInfo1.setCdpid(localResultSet.getInt(7));
        localCertInfo1.setCtmlName(localResultSet.getString(8));
        localCertInfo1.setCertStatus(localResultSet.getString(9));
        localCertInfo1.setIsValid(localResultSet.getLong(10));
        localCertInfo1.setCreateTime(localResultSet.getLong(11));
        localCertInfo1.setApplicant(localResultSet.getString(12));
        localCertInfo1.setEmail(localResultSet.getString(13));
        localCertInfo1.setRemark(localResultSet.getString(14));
      }
      localCertInfo2 = localCertInfo1;
    }
    catch (SQLException localSQLException1)
    {
      CertInfo localCertInfo2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8060", "获取冻结证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public CertInfo getUNRVKCertInfo(String paramString1, String paramString2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    CertInfo localCertInfo1 = null;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        localObject1 = localCertInfo1;
        jsr 366;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      Object localObject1 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark from cert where subjectuppercase=? and ctmlname=? and certstatus in(?,?,?)";
      localPreparedStatement = localConnection.prepareStatement((String)localObject1);
      localPreparedStatement.setString(1, paramString1.toUpperCase());
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.setString(3, "Hold");
      localPreparedStatement.setString(4, "Undown");
      localPreparedStatement.setString(5, "Use");
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        localCertInfo1 = new CertInfo();
        localCertInfo1.setCertSN(localResultSet.getString(1));
        localCertInfo1.setSubject(localResultSet.getString(2));
        localCertInfo1.setNotBefore(localResultSet.getLong(3));
        localCertInfo1.setNotAfter(localResultSet.getLong(4));
        localCertInfo1.setValidity(localResultSet.getInt(5));
        localCertInfo1.setAuthCode(localResultSet.getString(6));
        localCertInfo1.setCdpid(localResultSet.getInt(7));
        localCertInfo1.setCtmlName(localResultSet.getString(8));
        localCertInfo1.setCertStatus(localResultSet.getString(9));
        localCertInfo1.setIsValid(localResultSet.getLong(10));
        localCertInfo1.setCreateTime(localResultSet.getLong(11));
        localCertInfo1.setApplicant(localResultSet.getString(12));
        localCertInfo1.setEmail(localResultSet.getString(13));
        localCertInfo1.setRemark(localResultSet.getString(14));
      }
      localCertInfo2 = localCertInfo1;
    }
    catch (SQLException localSQLException1)
    {
      CertInfo localCertInfo2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8061", "获取未注销证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int checkCertReq(String paramString1, String paramString2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    int i = 0;
    try
    {
      int j;
      if ((paramString1 == null) || (paramString2 == null))
      {
        j = i;
        jsr 175;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      String str = "select count(certsn) from cert where subjectuppercase=? and ctmlname=? and certstatus in(?,?,?)";
      localPreparedStatement = localConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString1.toUpperCase());
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.setString(3, "Use");
      localPreparedStatement.setString(4, "Undown");
      localPreparedStatement.setString(5, "Hold");
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
        i = localResultSet.getInt(1);
      k = i;
    }
    catch (SQLException localSQLException1)
    {
      int k;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8059", "检查证书申请失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public Vector getCertInfo(Properties paramProperties, int paramInt1, int paramInt2, boolean paramBoolean)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    int i = 0;
    CertInfo[] arrayOfCertInfo = null;
    Vector localVector1 = new Vector();
    Vector localVector2 = new Vector();
    String str3 = "";
    Enumeration localEnumeration = null;
    String[][] arrayOfString = (String[][])null;
    long l = 0L;
    try
    {
      if (paramProperties == null)
      {
        localObject1 = localVector1;
        jsr 1672;
      }
      int k = paramProperties.size();
      Object localObject1 = paramProperties.getProperty("ctmlName");
      if ((localObject1 != null) && (((String)localObject1).indexOf("|") >= 0))
      {
        localObject2 = new StringTokenizer((String)localObject1, "|");
        k += ((StringTokenizer)localObject2).countTokens();
      }
      localObject1 = paramProperties.getProperty("certStatus");
      if ((localObject1 != null) && (((String)localObject1).indexOf("|") >= 0))
      {
        localObject2 = new StringTokenizer((String)localObject1, "|");
        k += ((StringTokenizer)localObject2).countTokens();
      }
      arrayOfString = new String[k][2];
      localEnumeration = paramProperties.propertyNames();
      if (localEnumeration.hasMoreElements())
      {
        str3 = " where ";
        while (localEnumeration.hasMoreElements())
        {
          String str1 = localEnumeration.nextElement().toString();
          arrayOfString[i][0] = paramProperties.getProperty(str1);
          if (str1.equals("createTimeStart"))
          {
            str3 = str3 + "createTime" + ">=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("createTimeEnd"))
          {
            str3 = str3 + "createTime" + "<=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("ctmlName"))
          {
            if (arrayOfString[i][0].indexOf("|") >= 0)
            {
              localObject2 = new StringTokenizer(arrayOfString[i][0], "|");
              k = 0;
              for (str3 = str3 + str1 + " in ("; ((StringTokenizer)localObject2).hasMoreTokens(); str3 = str3 + "?,")
              {
                arrayOfString[i][0] = ((StringTokenizer)localObject2).nextToken();
                arrayOfString[i][1] = "S";
                i++;
              }
              i--;
              str3 = str3.substring(0, str3.length() - 1) + ") and ";
            }
            else
            {
              str3 = str3 + str1 + "=? and ";
              arrayOfString[i][1] = "S";
            }
          }
          else if (str1.equals("certStatus"))
          {
            if (arrayOfString[i][0].indexOf("|") >= 0)
            {
              localObject2 = new StringTokenizer(arrayOfString[i][0], "|");
              for (str3 = str3 + str1 + " in ("; ((StringTokenizer)localObject2).hasMoreTokens(); str3 = str3 + "?,")
              {
                arrayOfString[i][0] = ((StringTokenizer)localObject2).nextToken();
                arrayOfString[i][1] = "S";
                i++;
              }
              i--;
              str3 = str3.substring(0, str3.length() - 1) + ") and ";
            }
            else
            {
              str3 = str3 + str1 + "=? and ";
              arrayOfString[i][1] = "S";
            }
          }
          else if (str1.equals("subject"))
          {
            if (!paramBoolean)
            {
              arrayOfString[i][0] = ("%" + setEscape4(arrayOfString[i][0]) + "%");
              str3 = str3 + "subjectuppercase" + " like ? escape '\\' and ";
            }
            else
            {
              str3 = str3 + "subjectuppercase" + "=? and ";
            }
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else if (str1.equals("applicant"))
          {
            str3 = str3 + "applicantuppercase" + "=? and ";
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else if (str1.equals("certSN"))
          {
            str3 = str3 + "cert." + "certSN" + "=? and ";
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else if (str1.equals("cdpid"))
          {
            str3 = str3 + "cert." + "cdpid" + "=? and ";
            arrayOfString[i][1] = "S";
          }
          else if ((str1.equals("notBefore")) || (str1.equals("notAfter")) || (str1.equals("validity")) || (str1.equals("cdpid")) || (str1.equals("isvalid")) || (str1.equals("createTime")))
          {
            str3 = str3 + str1 + "=? and ";
            arrayOfString[i][1] = "L";
          }
          else
          {
            str3 = str3 + str1 + "=? and ";
            arrayOfString[i][1] = "S";
          }
          i++;
        }
        str3 = str3.substring(0, str3.length() - 5);
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      if (paramInt1 < 1)
        paramInt1 = 1;
      String str2 = "select cert.certsn,subject,notbefore,notafter,validity,authcode,cert.cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark,reason,reasondesc,oldsn from cert left outer join revokedcert on (cert.certsn=revokedcert.certsn)";
      if (orderby)
        str3 = str3 + " order by createtime desc";
      str2 = str2 + str3;
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase(), 1004, 1007);
      for (int j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      localResultSet = localPreparedStatement.executeQuery();
      if (paramInt2 < 1)
        while (localResultSet.next())
          localVector2.add(getCertinfoCondition(localResultSet));
      if (paramInt1 == 1)
      {
        do
        {
          if (!localResultSet.next())
            break;
          localVector2.add(getCertinfoCondition(localResultSet));
        }
        while (localResultSet.getRow() != paramInt1 + paramInt2 - 1);
      }
      else
      {
        localResultSet.absolute(paramInt1 - 1);
        while (localResultSet.next())
        {
          localVector2.add(getCertinfoCondition(localResultSet));
          if (localResultSet.getRow() != paramInt1 + paramInt2 - 1)
            continue;
        }
      }
      if (localResultSet.next())
      {
        localResultSet.last();
        l = localResultSet.getRow();
      }
      else
      {
        localResultSet.absolute(-1);
        l = localResultSet.getRow();
      }
      arrayOfCertInfo = new CertInfo[localVector2.size()];
      localVector2.toArray(arrayOfCertInfo);
      localVector1.add(Long.toString(l));
      localVector1.add(arrayOfCertInfo);
      localObject2 = localVector1;
    }
    catch (SQLException localSQLException1)
    {
      Object localObject2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8014", "获取证书信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public Vector getCertInfoForIsnotWaiting(Properties paramProperties, int paramInt1, int paramInt2, boolean paramBoolean)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    int i = 0;
    CertInfo[] arrayOfCertInfo = null;
    Vector localVector1 = new Vector();
    Vector localVector2 = new Vector();
    String str3 = "";
    Enumeration localEnumeration = null;
    String[][] arrayOfString = (String[][])null;
    long l = 0L;
    try
    {
      if (paramProperties == null)
      {
        localObject1 = localVector1;
        jsr 1722;
      }
      int k = paramProperties.size();
      Object localObject1 = paramProperties.getProperty("ctmlName");
      if ((localObject1 != null) && (((String)localObject1).indexOf("|") >= 0))
      {
        localObject2 = new StringTokenizer((String)localObject1, "|");
        k += ((StringTokenizer)localObject2).countTokens();
      }
      localObject1 = paramProperties.getProperty("certStatus");
      if ((localObject1 != null) && (((String)localObject1).indexOf("|") >= 0))
      {
        localObject2 = new StringTokenizer((String)localObject1, "|");
        k += ((StringTokenizer)localObject2).countTokens();
      }
      arrayOfString = new String[k][2];
      localEnumeration = paramProperties.propertyNames();
      if (localEnumeration.hasMoreElements())
      {
        str3 = " where ";
        while (localEnumeration.hasMoreElements())
        {
          String str1 = localEnumeration.nextElement().toString();
          arrayOfString[i][0] = paramProperties.getProperty(str1);
          if (str1.equals("createTimeStart"))
          {
            str3 = str3 + "createTime" + ">=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("createTimeEnd"))
          {
            str3 = str3 + "createTime" + "<=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("ctmlName"))
          {
            if (arrayOfString[i][0].indexOf("|") >= 0)
            {
              localObject2 = new StringTokenizer(arrayOfString[i][0], "|");
              k = 0;
              for (str3 = str3 + str1 + " in ("; ((StringTokenizer)localObject2).hasMoreTokens(); str3 = str3 + "?,")
              {
                arrayOfString[i][0] = ((StringTokenizer)localObject2).nextToken();
                arrayOfString[i][1] = "S";
                i++;
              }
              i--;
              str3 = str3.substring(0, str3.length() - 1) + ") and ";
            }
            else
            {
              str3 = str3 + str1 + "=? and ";
              arrayOfString[i][1] = "S";
            }
          }
          else if (str1.equals("certStatus"))
          {
            if (arrayOfString[i][0].indexOf("|") >= 0)
            {
              localObject2 = new StringTokenizer(arrayOfString[i][0], "|");
              for (str3 = str3 + str1 + " in ("; ((StringTokenizer)localObject2).hasMoreTokens(); str3 = str3 + "?,")
              {
                arrayOfString[i][0] = ((StringTokenizer)localObject2).nextToken();
                arrayOfString[i][1] = "S";
                i++;
              }
              i--;
              str3 = str3.substring(0, str3.length() - 1) + ") and ";
            }
            else
            {
              str3 = str3 + str1 + "=? and ";
              arrayOfString[i][1] = "S";
            }
          }
          else if (str1.equals("subject"))
          {
            if (!paramBoolean)
            {
              arrayOfString[i][0] = ("%" + setEscape4(arrayOfString[i][0]) + "%");
              str3 = str3 + "subjectuppercase" + " like ? escape '\\' and ";
            }
            else
            {
              str3 = str3 + "subjectuppercase" + "=? and ";
            }
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else if (str1.equals("applicant"))
          {
            str3 = str3 + "applicantuppercase" + "=? and ";
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else if (str1.equals("certSN"))
          {
            str3 = str3 + "cert." + "certSN" + "=? and ";
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else if (str1.equals("cdpid"))
          {
            str3 = str3 + "cert." + "cdpid" + "=? and ";
            arrayOfString[i][1] = "S";
          }
          else if ((str1.equals("notBefore")) || (str1.equals("notAfter")) || (str1.equals("validity")) || (str1.equals("cdpid")) || (str1.equals("isvalid")) || (str1.equals("createTime")))
          {
            str3 = str3 + str1 + "=? and ";
            arrayOfString[i][1] = "L";
          }
          else
          {
            str3 = str3 + str1 + "=? and ";
            arrayOfString[i][1] = "S";
          }
          i++;
        }
        str3 = str3.substring(0, str3.length() - 5);
      }
      Object localObject2 = "";
      if (str3.indexOf("where") >= 0)
        localObject2 = " and iswaiting = '0'";
      else
        localObject2 = " where iswaiting = '0'";
      str3 = str3 + (String)localObject2;
      localConnection = DriverManager.getConnection("proxool.ida");
      if (paramInt1 < 1)
        paramInt1 = 1;
      String str2 = "select cert.certsn,subject,notbefore,notafter,validity,authcode,cert.cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark,reason,reasondesc,oldsn from cert left outer join revokedcert on (cert.certsn=revokedcert.certsn)";
      if (orderby)
        str3 = str3 + " order by createtime desc";
      str2 = str2 + str3;
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase(), 1004, 1007);
      for (int j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      localResultSet = localPreparedStatement.executeQuery();
      if (paramInt2 < 1)
        while (localResultSet.next())
          localVector2.add(getCertinfoCondition(localResultSet));
      if (paramInt1 == 1)
      {
        do
        {
          if (!localResultSet.next())
            break;
          localVector2.add(getCertinfoCondition(localResultSet));
        }
        while (localResultSet.getRow() != paramInt1 + paramInt2 - 1);
      }
      else
      {
        localResultSet.absolute(paramInt1 - 1);
        while (localResultSet.next())
        {
          localVector2.add(getCertinfoCondition(localResultSet));
          if (localResultSet.getRow() != paramInt1 + paramInt2 - 1)
            continue;
        }
      }
      if (localResultSet.next())
      {
        localResultSet.last();
        l = localResultSet.getRow();
      }
      else
      {
        localResultSet.absolute(-1);
        l = localResultSet.getRow();
      }
      arrayOfCertInfo = new CertInfo[localVector2.size()];
      localVector2.toArray(arrayOfCertInfo);
      localVector1.add(Long.toString(l));
      localVector1.add(arrayOfCertInfo);
      localVector3 = localVector1;
    }
    catch (SQLException localSQLException1)
    {
      Vector localVector3;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8077", "获得非待操作状态证书信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	protected CertInfo getCertinfoCondition(ResultSet paramResultSet)
			throws SQLException {
		CertInfo localCertInfo = new CertInfo();
		localCertInfo.setCertSN(paramResultSet.getString(1));
		localCertInfo.setSubject(paramResultSet.getString(2));
		localCertInfo.setNotBefore(paramResultSet.getLong(3));
		localCertInfo.setNotAfter(paramResultSet.getLong(4));
		localCertInfo.setValidity(paramResultSet.getInt(5));
		localCertInfo.setAuthCode(paramResultSet.getString(6));
		localCertInfo.setCdpid(paramResultSet.getInt(7));
		localCertInfo.setCtmlName(paramResultSet.getString(8));
		localCertInfo.setCertStatus(paramResultSet.getString(9));
		localCertInfo.setIsValid(paramResultSet.getLong(10));
		localCertInfo.setCreateTime(paramResultSet.getLong(11));
		localCertInfo.setApplicant(paramResultSet.getString(12));
		localCertInfo.setEmail(paramResultSet.getString(13));
		localCertInfo.setRemark(paramResultSet.getString(14));
		localCertInfo.setRevokeReason(paramResultSet.getString(15));
		localCertInfo.setRevokeDesc(paramResultSet.getString(16));
		localCertInfo.setOldSN(paramResultSet.getString(17));
		return localCertInfo;
	}

	protected CertInfo getCertinfoArcCondition(ResultSet paramResultSet)
			throws SQLException {
		CertInfo localCertInfo = new CertInfo();
		localCertInfo.setCertSN(paramResultSet.getString(1));
		localCertInfo.setSubject(paramResultSet.getString(2));
		localCertInfo.setNotBefore(paramResultSet.getLong(3));
		localCertInfo.setNotAfter(paramResultSet.getLong(4));
		localCertInfo.setValidity(paramResultSet.getInt(5));
		localCertInfo.setAuthCode(paramResultSet.getString(6));
		localCertInfo.setCdpid(paramResultSet.getInt(7));
		localCertInfo.setCtmlName(paramResultSet.getString(8));
		localCertInfo.setCertStatus(paramResultSet.getString(9));
		localCertInfo.setIsValid(paramResultSet.getLong(10));
		localCertInfo.setCreateTime(paramResultSet.getLong(11));
		localCertInfo.setApplicant(paramResultSet.getString(12));
		localCertInfo.setEmail(paramResultSet.getString(13));
		localCertInfo.setRemark(paramResultSet.getString(14));
		return localCertInfo;
	}

	public int saveCertReq(CertInfo paramCertInfo) throws DBException {
		Connection localConnection = null;
		int i = -1;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			localConnection.setAutoCommit(false);
			i = saveCertReq(localConnection, paramCertInfo);
			localConnection.commit();
			j = i;
		} catch (DBException localDBException) {
			int j;
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException2) {
				}
			throw localDBException;
		} catch (SQLException localSQLException1) {
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8005", "证书申请失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException3) {
				}
		}
	}

	protected int saveCertReq(Connection paramConnection, CertInfo paramCertInfo)
    throws DBException
  {
    PreparedStatement localPreparedStatement = null;
    int i = -1;
    try
    {
      int j;
      if (paramCertInfo == null)
      {
        j = i;
        jsr 686;
      }
      String str = "insert into cert values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,?,?,?,?,?,?,?,?)";
      localPreparedStatement = paramConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramCertInfo.getCertSN());
      localPreparedStatement.setString(2, paramCertInfo.getSubject().toUpperCase());
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
      localPreparedStatement.setString(13, paramCertInfo.getApplicant().toUpperCase());
      localPreparedStatement.setString(14, paramCertInfo.getApplicant());
      localPreparedStatement.setString(15, paramCertInfo.getEmail());
      localPreparedStatement.setString(16, paramCertInfo.getRemark());
      localPreparedStatement.setLong(17, paramCertInfo.getAuthCodeUpdateTime());
      localPreparedStatement.setString(18, "tmp_signServer");
      localPreparedStatement.setString(19, "tmp_signClient");
      localPreparedStatement.setString(20, paramCertInfo.getIsRetainKey());
      localPreparedStatement.setString(21, "0");
      localPreparedStatement.setString(22, paramCertInfo.getOldSN());
      i = localPreparedStatement.executeUpdate();
      Vector localVector = paramCertInfo.getStandardExtensions();
      if (localVector != null)
        for (int k = 0; k < localVector.size(); k++)
        {
          str = "insert into cert_standard_ext values(?,?,?,?,?,?,?,?)";
          StandardExtension localStandardExtension = (StandardExtension)localVector.get(k);
          localPreparedStatement = paramConnection.prepareStatement(str);
          localPreparedStatement.setString(1, paramCertInfo.getCertSN());
          localPreparedStatement.setString(2, localStandardExtension.getParentOID());
          localPreparedStatement.setString(3, localStandardExtension.getParentName());
          localPreparedStatement.setString(4, localStandardExtension.getChildName());
          localPreparedStatement.setString(5, localStandardExtension.getOtherNameOid());
          localPreparedStatement.setString(6, localStandardExtension.getStandardValue());
          localPreparedStatement.setString(7, "tmp_signServer");
          localPreparedStatement.setString(8, "tmp_signClient");
          localPreparedStatement.executeUpdate();
        }
      CertExtensions localCertExtensions = paramCertInfo.getCertExtensions();
      if (localCertExtensions != null)
        for (m = 0; m < localCertExtensions.getExtensionsCount(); m++)
        {
          str = "insert into cert_selfext values(?,?,?,?,?,?)";
          Extension localExtension = localCertExtensions.getExtension(m);
          localPreparedStatement = paramConnection.prepareStatement(str);
          localPreparedStatement.setString(1, paramCertInfo.getCertSN());
          localPreparedStatement.setString(2, localExtension.getOid());
          localPreparedStatement.setString(3, localExtension.getName());
          localPreparedStatement.setString(4, localExtension.getValue());
          localPreparedStatement.setString(5, "tmp_signServer");
          localPreparedStatement.setString(6, "tmp_signClient");
          localPreparedStatement.executeUpdate();
        }
      str = "update ctml set ctml_status=? where ctml_name=? and ctml_status!=?";
      localPreparedStatement = paramConnection.prepareStatement(str);
      localPreparedStatement.setString(1, "USING");
      localPreparedStatement.setString(2, paramCertInfo.getCtmlName());
      localPreparedStatement.setString(3, "USING");
      localPreparedStatement.executeUpdate();
      addCertSUM();
      m = i;
    }
    catch (SQLException localSQLException1)
    {
      int m;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8005", "证书申请失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	public int saveCertEntity(CertInfo paramCertInfo)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ByteArrayInputStream localByteArrayInputStream1 = -1;
    try
    {
      int i;
      if (paramCertInfo == null)
      {
        i = localByteArrayInputStream1;
        jsr 277;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      String str;
      if (paramCertInfo.getApplicant() == null)
      {
        str = "update cert set certstatus=?,notbefore=?,notafter=?,certentity=? where certsn=?";
        localPreparedStatement = localConnection.prepareStatement(str);
        localPreparedStatement.setString(1, paramCertInfo.getCertStatus());
        localPreparedStatement.setLong(2, paramCertInfo.getNotBefore());
        localPreparedStatement.setLong(3, paramCertInfo.getNotAfter());
        localByteArrayInputStream2 = new ByteArrayInputStream(paramCertInfo.getCertEntity());
        localPreparedStatement.setBinaryStream(4, localByteArrayInputStream2, paramCertInfo.getCertEntity().length);
        localPreparedStatement.setString(5, paramCertInfo.getCertSN());
      }
      else
      {
        str = "update cert set certstatus=?,notbefore=?,notafter=?,applicant=?,certentity=? where certsn=?";
        localPreparedStatement = localConnection.prepareStatement(str);
        localPreparedStatement.setString(1, paramCertInfo.getCertStatus());
        localPreparedStatement.setLong(2, paramCertInfo.getNotBefore());
        localPreparedStatement.setLong(3, paramCertInfo.getNotAfter());
        localPreparedStatement.setString(4, paramCertInfo.getApplicant());
        localByteArrayInputStream2 = new ByteArrayInputStream(paramCertInfo.getCertEntity());
        localPreparedStatement.setBinaryStream(5, localByteArrayInputStream2, paramCertInfo.getCertEntity().length);
        localPreparedStatement.setString(6, paramCertInfo.getCertSN());
      }
      localByteArrayInputStream1 = localPreparedStatement.executeUpdate();
      localByteArrayInputStream2 = localByteArrayInputStream1;
    }
    catch (SQLException localSQLException1)
    {
      ByteArrayInputStream localByteArrayInputStream2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8006", "保存证书实体失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int holdCert(CertRevokeInfo paramCertRevokeInfo) throws DBException {
		Connection localConnection = null;
		int i = -1;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			localConnection.setAutoCommit(false);
			i = holdCert(localConnection, paramCertRevokeInfo);
			if (i == 1)
				localConnection.commit();
			else
				localConnection.rollback();
			j = i;
		} catch (DBException localDBException) {
			int j;
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
			throw new DBException("8018", "冻结证书失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException4) {
				}
		}
	}

	protected int holdCert(Connection paramConnection, CertRevokeInfo paramCertRevokeInfo)
    throws DBException
  {
    PreparedStatement localPreparedStatement = null;
    Object localObject1 = null;
    int i = 0;
    int j = -1;
    CertInfo localCertInfo = null;
    try
    {
      if (paramCertRevokeInfo == null)
      {
        k = j;
        jsr 398;
      }
      String str;
      if (paramCertRevokeInfo.getCertSN() == null)
      {
        localCertInfo = getCertInfo(paramCertRevokeInfo.getCertDN(), paramCertRevokeInfo.getCtmlName());
        if (localCertInfo == null)
        {
          j = -1;
        }
        else
        {
          str = "update cert set certstatus=?,isvalid=?,applicant=? where subjectuppercase=? and ctmlname=? and isvalid=1 and certstatus=?";
          localPreparedStatement = paramConnection.prepareStatement(str);
          localPreparedStatement.setString(1, "Hold");
          localPreparedStatement.setLong(2, getRandom());
          localPreparedStatement.setString(3, paramCertRevokeInfo.getApplicant());
          localPreparedStatement.setString(4, paramCertRevokeInfo.getCertDN().toUpperCase());
          localPreparedStatement.setString(5, paramCertRevokeInfo.getCtmlName());
          localPreparedStatement.setString(6, "Use");
          j = localPreparedStatement.executeUpdate();
        }
      }
      else
      {
        localCertInfo = getCertInfo(paramCertRevokeInfo.getCertSN());
        if (localCertInfo == null)
        {
          j = -1;
        }
        else
        {
          str = "update cert set certstatus=?,isvalid=? where certsn=? and certstatus=?";
          localPreparedStatement = paramConnection.prepareStatement(str);
          localPreparedStatement.setString(1, "Hold");
          localPreparedStatement.setLong(2, getRandom());
          localPreparedStatement.setString(3, paramCertRevokeInfo.getCertSN());
          localPreparedStatement.setString(4, "Use");
          j = localPreparedStatement.executeUpdate();
        }
      }
      if (j == 1)
      {
        str = "insert into revokedcert values(?,?,?,?,?,?,?,?)";
        localPreparedStatement = paramConnection.prepareStatement(str);
        localPreparedStatement.setString(1, localCertInfo.getCertSN());
        localPreparedStatement.setLong(2, paramCertRevokeInfo.getCDPID());
        localPreparedStatement.setInt(3, paramCertRevokeInfo.getReasonID());
        localPreparedStatement.setString(4, paramCertRevokeInfo.getReasonDESC());
        localPreparedStatement.setLong(5, getTime());
        localPreparedStatement.setString(6, "tmp_signServer");
        localPreparedStatement.setString(7, "tmp_signClient");
        localPreparedStatement.setLong(8, localCertInfo.getNotAfter());
        j = localPreparedStatement.executeUpdate();
      }
      k = j;
    }
    catch (SQLException localSQLException1)
    {
      int k;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8018", "冻结证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	public int revokeCert(CertRevokeInfo paramCertRevokeInfo)
    throws DBException
  {
    Connection localConnection = null;
    try
    {
      localConnection = DriverManager.getConnection("proxool.ida");
      localConnection.setAutoCommit(false);
      String str = revokeCert(localConnection, paramCertRevokeInfo);
      if (str == null)
      {
        localConnection.rollback();
        i = 0;
        jsr 125;
      }
      deleteAdmin(localConnection, str);
      deleteTemplateAdmin(localConnection, str);
      localConnection.commit();
      i = 1;
    }
    catch (DBException localDBException)
    {
      int i;
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException2)
        {
        }
      throw localDBException;
    }
    catch (SQLException localSQLException1)
    {
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException3)
        {
        }
      throw new DBException("8043", "8043", localSQLException1);
    }
    finally
    {
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException4)
        {
        }
    }
  }

	protected String revokeCert(Connection paramConnection, CertRevokeInfo paramCertRevokeInfo)
    throws DBException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    int i = 0;
    int j = -1;
    CertInfo localCertInfo = null;
    String str2 = null;
    try
    {
      if (paramCertRevokeInfo == null)
      {
        str3 = null;
        jsr 626;
      }
      String str1;
      if (paramCertRevokeInfo.getCertSN() == null)
      {
        localCertInfo = getUNRVKCertInfo(paramCertRevokeInfo.getCertDN(), paramCertRevokeInfo.getCtmlName());
        if (localCertInfo == null)
        {
          j = -1;
        }
        else
        {
          str1 = "update cert set certstatus=?,isvalid=?,applicant=? where certsn=? and certstatus in(?,?,?)";
          localPreparedStatement = paramConnection.prepareStatement(str1);
          if (localCertInfo.getCertStatus().equals("Undown"))
            localPreparedStatement.setString(1, "UndownRevoke");
          else
            localPreparedStatement.setString(1, "Revoke");
          localPreparedStatement.setLong(2, getRandom());
          localPreparedStatement.setString(3, paramCertRevokeInfo.getApplicant());
          localPreparedStatement.setString(4, localCertInfo.getCertSN());
          localPreparedStatement.setString(5, "Hold");
          localPreparedStatement.setString(6, "Undown");
          localPreparedStatement.setString(7, "Use");
          j = localPreparedStatement.executeUpdate();
        }
      }
      else
      {
        localCertInfo = getCertInfo(paramCertRevokeInfo.getCertSN());
        if (localCertInfo == null)
        {
          j = -1;
        }
        else
        {
          str1 = "update cert set certstatus=?,isvalid=?,applicant=? where certsn=? and certstatus in(?,?,?)";
          localPreparedStatement = paramConnection.prepareStatement(str1);
          if (localCertInfo.getCertStatus().equals("Undown"))
            localPreparedStatement.setString(1, "UndownRevoke");
          else
            localPreparedStatement.setString(1, "Revoke");
          localPreparedStatement.setLong(2, getRandom());
          localPreparedStatement.setString(3, paramCertRevokeInfo.getApplicant());
          localPreparedStatement.setString(4, paramCertRevokeInfo.getCertSN());
          localPreparedStatement.setString(5, "Hold");
          localPreparedStatement.setString(6, "Undown");
          localPreparedStatement.setString(7, "Use");
          j = localPreparedStatement.executeUpdate();
        }
      }
      if (j == 1)
      {
        str1 = "select count(*) from revokedcert where certsn=?";
        localPreparedStatement = paramConnection.prepareStatement(str1);
        localPreparedStatement.setString(1, localCertInfo.getCertSN());
        localResultSet = localPreparedStatement.executeQuery();
        localResultSet.next();
        if (localResultSet.getInt(1) == 0)
        {
          str1 = "insert into revokedcert values(?,?,?,?,?,?,?,?)";
          localPreparedStatement = paramConnection.prepareStatement(str1);
          localPreparedStatement.setString(1, localCertInfo.getCertSN());
          localPreparedStatement.setLong(2, paramCertRevokeInfo.getCDPID());
          localPreparedStatement.setInt(3, paramCertRevokeInfo.getReasonID());
          localPreparedStatement.setString(4, paramCertRevokeInfo.getReasonDESC());
          localPreparedStatement.setLong(5, getTime());
          localPreparedStatement.setString(6, "tmp_signServer");
          localPreparedStatement.setString(7, "tmp_signClient");
          localPreparedStatement.setLong(8, localCertInfo.getNotAfter());
        }
        else
        {
          str1 = "update revokedcert set cdpid=?,reason=?,reasondesc=?,revoketime=? where certsn=?";
          localPreparedStatement = paramConnection.prepareStatement(str1);
          localPreparedStatement.setLong(1, paramCertRevokeInfo.getCDPID());
          localPreparedStatement.setInt(2, paramCertRevokeInfo.getReasonID());
          localPreparedStatement.setString(3, paramCertRevokeInfo.getReasonDESC());
          localPreparedStatement.setLong(4, getTime());
          localPreparedStatement.setString(5, localCertInfo.getCertSN());
        }
        j = localPreparedStatement.executeUpdate();
        str2 = localCertInfo.getCertSN();
      }
      str3 = str2;
    }
    catch (SQLException localSQLException1)
    {
      String str3;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8043", "注销证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	public CertRevokeInfo[] getRevokedCerts(long paramLong) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		CertRevokeInfo localCertRevokeInfo = null;
		Vector localVector = null;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			String str = "select certsn,cdpid,reason,reasondesc,revoketime from revokedcert where cdpid=?";
			localPreparedStatement = localConnection.prepareStatement(str);
			localPreparedStatement.setLong(1, paramLong);
			localResultSet = localPreparedStatement.executeQuery();
			localVector = new Vector();
			while (localResultSet.next()) {
				localCertRevokeInfo = new CertRevokeInfo();
				localCertRevokeInfo.setCertSN(localResultSet.getString(1));
				localCertRevokeInfo.setCDPID(localResultSet.getLong(2));
				localCertRevokeInfo.setReasonID(localResultSet.getInt(3));
				localCertRevokeInfo.setReasonDESC(localResultSet.getString(4));
				localCertRevokeInfo.setRevokeTime(localResultSet.getLong(5));
				localVector.add(localCertRevokeInfo);
			}
			CertRevokeInfo[] arrayOfCertRevokeInfo1 = new CertRevokeInfo[localVector
					.size()];
			localVector.toArray(arrayOfCertRevokeInfo1);
			arrayOfCertRevokeInfo2 = arrayOfCertRevokeInfo1;
		} catch (SQLException localSQLException1) {
			CertRevokeInfo[] arrayOfCertRevokeInfo2;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8058", "获取注销证书失败", localSQLException1);
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

	public CertRevokeInfo[] getRevokedCerts() throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		CertRevokeInfo localCertRevokeInfo = null;
		Vector localVector = null;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			String str = "select certsn,cdpid,reason,reasondesc,revoketime from revokedcert";
			localPreparedStatement = localConnection.prepareStatement(str);
			localResultSet = localPreparedStatement.executeQuery();
			localVector = new Vector();
			while (localResultSet.next()) {
				localCertRevokeInfo = new CertRevokeInfo();
				localCertRevokeInfo.setCertSN(localResultSet.getString(1));
				localCertRevokeInfo.setCDPID(localResultSet.getLong(2));
				localCertRevokeInfo.setReasonID(localResultSet.getInt(3));
				localCertRevokeInfo.setReasonDESC(localResultSet.getString(4));
				localCertRevokeInfo.setRevokeTime(localResultSet.getLong(5));
				localVector.add(localCertRevokeInfo);
			}
			CertRevokeInfo[] arrayOfCertRevokeInfo1 = new CertRevokeInfo[localVector
					.size()];
			localVector.toArray(arrayOfCertRevokeInfo1);
			arrayOfCertRevokeInfo2 = arrayOfCertRevokeInfo1;
		} catch (SQLException localSQLException1) {
			CertRevokeInfo[] arrayOfCertRevokeInfo2;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8058", "获取注销证书失败", localSQLException1);
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

	public int unHoldCert(String paramString1, String paramString2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    Object localObject1 = null;
    int i = -1;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        j = i;
        jsr 217;
      }
      String str = "update cert set certstatus=?,isvalid=1,applicant=? where certsn=? and certstatus=?";
      localConnection = DriverManager.getConnection("proxool.ida");
      localConnection.setAutoCommit(false);
      localPreparedStatement = localConnection.prepareStatement(str);
      localPreparedStatement.setString(1, "Use");
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.setString(3, paramString1);
      localPreparedStatement.setString(4, "Hold");
      i = localPreparedStatement.executeUpdate();
      if (i == 1)
      {
        str = "delete from revokedcert where certsn=?";
        localPreparedStatement = localConnection.prepareStatement(str);
        localPreparedStatement.setString(1, paramString1);
        i = localPreparedStatement.executeUpdate();
        localConnection.commit();
      }
      else
      {
        localConnection.rollback();
      }
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException2)
        {
        }
      throw new DBException("8019", "解冻证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException3)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException4)
        {
        }
    }
  }

	public int updateCert(CertRevokeInfo paramCertRevokeInfo,
			CertInfo paramCertInfo) throws DBException {
		Connection localConnection = null;
		int i = -1;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			localConnection.setAutoCommit(false);
			String str = revokeCert(localConnection, paramCertRevokeInfo);
			if (str != null) {
				paramCertInfo.setOldSN(str);
				i = saveCertReq(localConnection, paramCertInfo);
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
			j = i;
		} catch (DBException localDBException) {
			int j;
			syslogger.info("数据库异常：" + localDBException.getMessage());
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
			throw new DBException("8034", "更新证书失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException4) {
				}
		}
	}

	public int updateAdminCert(CertRevokeInfo paramCertRevokeInfo,
			CertInfo paramCertInfo) throws DBException {
		Connection localConnection = null;
		Information localInformation1 = -1;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			localConnection.setAutoCommit(false);
			String str = revokeCert(localConnection, paramCertRevokeInfo);
			if (str != null) {
				localInformation1 = saveCertReq(localConnection, paramCertInfo);
				if (localInformation1 == 1) {
					updateAdmin(localConnection, str, paramCertInfo.getCertSN());
					updateTemplateAdmin(localConnection, str, paramCertInfo
							.getCertSN());
					localInformation2 = new Information("CAAdminSN",
							paramCertInfo.getCertSN(), "N");
					localInformation1 = setConfig("CAConfig", localInformation2);
					if (localInformation1 == 1)
						localConnection.commit();
					else
						localConnection.rollback();
				} else {
					localConnection.rollback();
				}
			} else {
				localConnection.rollback();
			}
			localInformation2 = localInformation1;
		} catch (DBException localDBException) {
			Information localInformation2;
			syslogger.info("数据库异常：" + localDBException.getMessage());
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
			throw new DBException("8045", "更新管理员证书失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException4) {
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
			j = i;
		} catch (DBException localDBException) {
			int j;
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

	private int reqAndDownCert(Connection paramConnection, CertInfo paramCertInfo)
    throws DBException
  {
    PreparedStatement localPreparedStatement = null;
    ByteArrayInputStream localByteArrayInputStream = null;
    int i = -1;
    try
    {
      int j;
      if (paramCertInfo == null)
      {
        j = i;
        jsr 662;
      }
      String str = "insert into cert values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      localPreparedStatement = paramConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramCertInfo.getCertSN());
      localPreparedStatement.setString(2, paramCertInfo.getSubject().toUpperCase());
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
      localPreparedStatement.setString(13, paramCertInfo.getApplicant().toUpperCase());
      localPreparedStatement.setString(14, paramCertInfo.getApplicant());
      localByteArrayInputStream = new ByteArrayInputStream(paramCertInfo.getCertEntity());
      localPreparedStatement.setBinaryStream(15, localByteArrayInputStream, paramCertInfo.getCertEntity().length);
      localPreparedStatement.setString(16, paramCertInfo.getEmail());
      localPreparedStatement.setString(17, paramCertInfo.getRemark());
      localPreparedStatement.setLong(18, paramCertInfo.getAuthCodeUpdateTime());
      localPreparedStatement.setString(19, "tmp_signServer");
      localPreparedStatement.setString(20, "tmp_signClient");
      localPreparedStatement.setString(21, paramCertInfo.getIsRetainKey());
      localPreparedStatement.setString(22, "0");
      localPreparedStatement.setString(23, paramCertInfo.getOldSN());
      i = localPreparedStatement.executeUpdate();
      Vector localVector = paramCertInfo.getStandardExtensions();
      if (localVector != null)
        for (int k = 0; k < localVector.size(); k++)
        {
          str = "insert into cert_standard_ext values(?,?,?,?,?,?,?,?)";
          StandardExtension localStandardExtension = (StandardExtension)localVector.get(k);
          localPreparedStatement = paramConnection.prepareStatement(str);
          localPreparedStatement.setString(1, paramCertInfo.getCertSN());
          localPreparedStatement.setString(2, localStandardExtension.getParentOID());
          localPreparedStatement.setString(3, localStandardExtension.getParentName());
          localPreparedStatement.setString(4, localStandardExtension.getChildName());
          localPreparedStatement.setString(5, localStandardExtension.getOtherNameOid());
          localPreparedStatement.setString(6, localStandardExtension.getStandardValue());
          localPreparedStatement.setString(7, "tmp_signServer");
          localPreparedStatement.setString(8, "tmp_signClient");
          localPreparedStatement.executeUpdate();
        }
      CertExtensions localCertExtensions = paramCertInfo.getCertExtensions();
      if (localCertExtensions != null)
        for (m = 0; m < localCertExtensions.getExtensionsCount(); m++)
        {
          str = "insert into cert_selfext values(?,?,?,?,?,?)";
          Extension localExtension = localCertExtensions.getExtension(m);
          localPreparedStatement = paramConnection.prepareStatement(str);
          localPreparedStatement.setString(1, paramCertInfo.getCertSN());
          localPreparedStatement.setString(2, localExtension.getOid());
          localPreparedStatement.setString(3, localExtension.getName());
          localPreparedStatement.setString(4, localExtension.getValue());
          localPreparedStatement.setString(5, "tmp_signServer");
          localPreparedStatement.setString(6, "tmp_signClient");
          localPreparedStatement.executeUpdate();
        }
      addCertSUM();
      m = i;
    }
    catch (SQLException localSQLException1)
    {
      int m;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8035", "申请并下载证书失败", localSQLException1);
    }
    finally
    {
      if (localByteArrayInputStream != null)
        try
        {
          localByteArrayInputStream.close();
        }
        catch (IOException localIOException)
        {
        }
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
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
			j = i;
		} catch (DBException localDBException) {
			int j;
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
			j = i;
		} catch (DBException localDBException) {
			int j;
			syslogger.info("数据库异常：" + localDBException.getMessage());
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

	public byte[] getCertEntity(String paramString)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    byte[] arrayOfByte1 = null;
    try
    {
      if (paramString == null)
      {
        arrayOfByte2 = arrayOfByte1;
        jsr 127;
      }
      String str = "select certentity from cert where certsn=?";
      localConnection = DriverManager.getConnection("proxool.ida");
      localPreparedStatement = localConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
        arrayOfByte1 = localResultSet.getBytes(1);
      arrayOfByte2 = arrayOfByte1;
    }
    catch (SQLException localSQLException1)
    {
      byte[] arrayOfByte2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8010", "获取证书实体失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public CertInfo getCertReqInfo(String paramString1, String paramString2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    CertInfo localCertInfo = null;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        localObject1 = localCertInfo;
        jsr 512;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      Object localObject1 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark from cert where certsn=? and authcode=?";
      localPreparedStatement = localConnection.prepareStatement((String)localObject1);
      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, paramString2);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        localCertInfo = new CertInfo();
        localCertInfo.setCertSN(localResultSet.getString(1));
        localCertInfo.setSubject(localResultSet.getString(2));
        localCertInfo.setNotBefore(localResultSet.getLong(3));
        localCertInfo.setNotAfter(localResultSet.getLong(4));
        localCertInfo.setValidity(localResultSet.getInt(5));
        localCertInfo.setAuthCode(localResultSet.getString(6));
        localCertInfo.setCdpid(localResultSet.getInt(7));
        localCertInfo.setCtmlName(localResultSet.getString(8));
        localCertInfo.setCertStatus(localResultSet.getString(9));
        localCertInfo.setIsValid(localResultSet.getInt(10));
        localCertInfo.setCreateTime(localResultSet.getLong(11));
        localCertInfo.setApplicant(localResultSet.getString(12));
        localCertInfo.setEmail(localResultSet.getString(13));
        localCertInfo.setRemark(localResultSet.getString(14));
        localObject1 = "select oid,selfext_name,value from cert_selfext where certsn=?";
        localPreparedStatement = localConnection.prepareStatement((String)localObject1);
        localPreparedStatement.setString(1, paramString1);
        localResultSet = localPreparedStatement.executeQuery();
        localObject2 = new Vector();
        Properties localProperties = new Properties();
        while (localResultSet.next())
        {
          localObject3 = new Extension();
          ((Extension)localObject3).setOid(localResultSet.getString(1));
          ((Extension)localObject3).setName(localResultSet.getString(2));
          ((Extension)localObject3).setValue(localResultSet.getString(3));
          ((Vector)localObject2).add(localObject3);
          localProperties.setProperty(localResultSet.getString(2), localResultSet.getString(3));
        }
        localCertInfo.setExtensions(localProperties);
        Object localObject3 = new Extension[((Vector)localObject2).size()];
        ((Vector)localObject2).toArray(localObject3);
        localCertInfo.setCertExtensions(new CertExtensions(localObject3));
      }
      localObject2 = localCertInfo;
    }
    catch (SQLException localSQLException1)
    {
      Object localObject2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8013", "按主题获取证书信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public CertExtensions getCertExtensions(String paramString)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    CertExtensions localCertExtensions1 = null;
    Vector localVector = new Vector();
    try
    {
      if (paramString == null)
      {
        localObject1 = localCertExtensions1;
        jsr 205;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      Object localObject1 = "select oid,selfext_name,value from cert_selfext where certsn=?";
      localPreparedStatement = localConnection.prepareStatement((String)localObject1);
      localPreparedStatement.setString(1, paramString);
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
        localObject2 = new Extension();
        ((Extension)localObject2).setOid(localResultSet.getString(1));
        ((Extension)localObject2).setName(localResultSet.getString(2));
        ((Extension)localObject2).setValue(localResultSet.getString(3));
        localVector.add(localObject2);
      }
      Object localObject2 = new Extension[localVector.size()];
      localVector.toArray(localObject2);
      localCertExtensions1 = new CertExtensions(localObject2);
      localCertExtensions2 = localCertExtensions1;
    }
    catch (SQLException localSQLException1)
    {
      CertExtensions localCertExtensions2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8048", "获取证书扩展失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int saveToCertTBP(String paramString)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    int i = -1;
    try
    {
      if (paramString == null)
      {
        j = i;
        jsr 126;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      String str = "insert into certtbp values(?,?,?)";
      localPreparedStatement = localConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString);
      localPreparedStatement.setString(2, "tmp_signServer");
      localPreparedStatement.setString(3, "tmp_signClient");
      i = localPreparedStatement.executeUpdate();
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("DBException:" + localSQLException1.toString());
      throw new DBException("8033", "保存待处理证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public CertInfo[] getCertTBPInfo() throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		CertInfo localCertInfo = null;
		Vector localVector = new Vector();
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			String str = "select t1.certsn,t1.subject,t1.ctmlname,t1.certstatus,t1.certentity from cert t1,certtbp t2 where t1.certsn=t2.certsn";
			localPreparedStatement = localConnection.prepareStatement(str);
			localPreparedStatement.setMaxRows(maxCount);
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next()) {
				localCertInfo = new CertInfo();
				localCertInfo.setCertSN(localResultSet.getString(1));
				localCertInfo.setSubject(localResultSet.getString(2));
				localCertInfo.setCtmlName(localResultSet.getString(3));
				localCertInfo.setCertStatus(localResultSet.getString(4));
				localCertInfo.setCertEntity(localResultSet.getBytes(5));
				localVector.add(localCertInfo);
			}
			CertInfo[] arrayOfCertInfo1 = new CertInfo[localVector.size()];
			localVector.toArray(arrayOfCertInfo1);
			arrayOfCertInfo2 = arrayOfCertInfo1;
		} catch (SQLException localSQLException1) {
			CertInfo[] arrayOfCertInfo2;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8056", "获取待发布证书信息失败", localSQLException1);
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

	public int deleteCertTBP(String[] paramArrayOfString)
    throws DBException
  {
    int i = -1;
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    try
    {
      if (paramArrayOfString == null)
      {
        j = i;
        jsr 174;
      }
      if (paramArrayOfString.length == 0)
      {
        j = i;
        jsr 160;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      localConnection.setAutoCommit(false);
      localPreparedStatement = localConnection.prepareStatement("delete from certtbp where certsn=?");
      for (j = 0; j < paramArrayOfString.length; j++)
      {
        localPreparedStatement.setString(1, paramArrayOfString[j]);
        localPreparedStatement.addBatch();
        if (j != 1000)
          continue;
        localPreparedStatement.executeBatch();
      }
      localPreparedStatement.executeBatch();
      localConnection.commit();
      j = paramArrayOfString.length;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8054", "删除待发布证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int updateAuthCode(String paramString1, String paramString2, String paramString3)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    int i = -1;
    try
    {
      if ((paramString1 == null) || (paramString2 == null) || (paramString3 == null))
      {
        j = i;
        jsr 142;
      }
      String str = "update cert set authcode=?,applicant=?,authcode_updatetime=? where certsn=?";
      localConnection = DriverManager.getConnection("proxool.ida");
      localPreparedStatement = localConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString2);
      localPreparedStatement.setString(2, paramString3);
      localPreparedStatement.setLong(3, getTime());
      localPreparedStatement.setString(4, paramString1);
      i = localPreparedStatement.executeUpdate();
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8030", "8030", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public Hashtable getAdminsInfo() throws DBException {
		Connection localConnection = null;
		Statement localStatement = null;
		ResultSet localResultSet = null;
		Hashtable localHashtable1 = new Hashtable();
		Vector localVector = null;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			String str1 = "select a.certsn,a.role_id from admin a,cert c where a.certsn=c.certsn and c.isvalid=1 order by c.createtime desc";
			localStatement = localConnection.createStatement();
			localResultSet = localStatement.executeQuery(str1);
			localVector = new Vector();
			String str2 = null;
			while (localResultSet.next()) {
				if (str2 == null) {
					str2 = localResultSet.getString(1);
					localVector.add(localResultSet.getString(2));
				} else if (str2.equals(localResultSet.getString(1))) {
					localVector.add(localResultSet.getString(2));
				} else {
					localHashtable1.put(str2, localVector);
					localVector = new Vector();
					str2 = localResultSet.getString(1);
					localVector.add(localResultSet.getString(2));
				}
				localHashtable1.put(str2, localVector);
			}
			return localHashtable1;
		} catch (SQLException localSQLException1) {
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8026", "获取管理员信息失败", localSQLException1);
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

	public int setAdminInfo(String paramString, Vector paramVector)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    int i = -1;
    try
    {
      if ((paramString == null) || (paramVector == null))
      {
        j = i;
        jsr 345;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      localConnection.setAutoCommit(false);
      String str = "select role_id from admin where certsn=?";
      localPreparedStatement = localConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        str = "delete from admin where certsn=?";
        localPreparedStatement = localConnection.prepareStatement(str);
        localPreparedStatement.setString(1, paramString);
        localPreparedStatement.execute();
        for (j = 0; j < paramVector.size(); j++)
        {
          str = "insert into admin values(?,?,?,?)";
          localPreparedStatement = localConnection.prepareStatement(str);
          localPreparedStatement.setString(1, paramString);
          localPreparedStatement.setString(2, (String)paramVector.get(j));
          localPreparedStatement.setString(3, "tmp_signServer");
          localPreparedStatement.setString(4, "tmp_signClient");
          localPreparedStatement.execute();
        }
      }
      for (j = 0; j < paramVector.size(); j++)
      {
        str = "insert into admin values(?,?,?,?)";
        localPreparedStatement = localConnection.prepareStatement(str);
        localPreparedStatement.setString(1, paramString);
        localPreparedStatement.setString(2, (String)paramVector.get(j));
        localPreparedStatement.setString(3, "tmp_signServer");
        localPreparedStatement.setString(4, "tmp_signClient");
        localPreparedStatement.execute();
      }
      localConnection.commit();
      i = paramVector.size();
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8023", "设置管理员角色失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int updateAdmin(String paramString1, String paramString2)
			throws DBException {
		Connection localConnection = null;
		int i = -1;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			i = updateAdmin(localConnection, paramString1, paramString2);
			j = i;
		} catch (DBException localDBException) {
			int j;
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
			throw new DBException("8031", "更新管理员证书失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException4) {
				}
		}
	}

	protected int updateAdmin(Connection paramConnection, String paramString1, String paramString2)
    throws DBException
  {
    PreparedStatement localPreparedStatement = null;
    int i = -1;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        j = i;
        jsr 113;
      }
      String str = "update admin set certsn=? where certsn=?";
      localPreparedStatement = paramConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString2);
      localPreparedStatement.setString(2, paramString1);
      i = localPreparedStatement.executeUpdate();
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8031", "更新管理员证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	public int deleteAdmin(String paramString) throws DBException {
		int i = -1;
		Connection localConnection = null;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			i = deleteAdmin(localConnection, paramString);
			j = i;
		} catch (SQLException localSQLException1) {
			int j;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8039", "删除管理员失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException2) {
				}
		}
	}

	protected int deleteAdmin(Connection paramConnection, String paramString)
    throws DBException
  {
    int i = -1;
    PreparedStatement localPreparedStatement = null;
    String str = null;
    try
    {
      if (paramString == null)
      {
        j = i;
        jsr 102;
      }
      str = "delete from admin where certsn=?";
      localPreparedStatement = paramConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString);
      i = localPreparedStatement.executeUpdate();
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8039", "删除管理员失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	public Hashtable getRolesInfo() throws DBException {
		Connection localConnection = null;
		Statement localStatement = null;
		ResultSet localResultSet = null;
		Hashtable localHashtable1 = new Hashtable();
		HashSet localHashSet = null;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			String str1 = "select role_id,privilege_id from ba_role order by role_id";
			localStatement = localConnection.createStatement();
			localResultSet = localStatement.executeQuery(str1);
			localHashSet = new HashSet();
			String str2 = null;
			while (localResultSet.next()) {
				if (str2 == null) {
					str2 = localResultSet.getString(1);
					localHashSet.add(localResultSet.getString(2));
				} else if (str2.equals(localResultSet.getString(1))) {
					localHashSet.add(localResultSet.getString(2));
				} else {
					localHashtable1.put(str2, localHashSet);
					localHashSet = new HashSet();
					str2 = localResultSet.getString(1);
					localHashSet.add(localResultSet.getString(2));
				}
				localHashtable1.put(str2, localHashSet);
			}
			return localHashtable1;
		} catch (SQLException localSQLException1) {
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8025", "获取角色信息失败", localSQLException1);
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

	public Hashtable getRolesRelationInfo() throws DBException {
		Connection localConnection = null;
		Statement localStatement = null;
		ResultSet localResultSet = null;
		Hashtable localHashtable1 = new Hashtable();
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			String str = "select distinct role_id,rolename from ba_role";
			localStatement = localConnection.createStatement();
			localResultSet = localStatement.executeQuery(str);
			while (localResultSet.next())
				localHashtable1.put(localResultSet.getString(1), localResultSet
						.getString(2));
			return localHashtable1;
		} catch (SQLException localSQLException1) {
			Hashtable localHashtable2;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8029", "获取角色关系信息失败", localSQLException1);
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

	public String setRoleInfo(String paramString, Vector paramVector)
			throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		String str2 = null;
		try {
			Object localObject1;
			if ((paramString == null) || (paramVector == null)) {
				return str2;
			}
			localConnection = DriverManager.getConnection("proxool.ida");
			localConnection.setAutoCommit(false);
			String str1 = "select role_id from ba_role where rolename=?";
			localPreparedStatement = localConnection.prepareStatement(str1);
			localPreparedStatement.setString(1, paramString);
			localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next()) {
				str2 = localResultSet.getString(1);
				str1 = "delete from ba_role where role_id=?";
				localPreparedStatement = localConnection.prepareStatement(str1);
				localPreparedStatement.setString(1, str2);
				localPreparedStatement.execute();
				for (int i = 0; i < paramVector.size(); i++) {
					str1 = "insert into ba_role values(?,?,?,?,?)";
					localPreparedStatement = localConnection
							.prepareStatement(str1);
					localPreparedStatement.setString(1, str2);
					localPreparedStatement.setString(2, paramString);
					localPreparedStatement.setString(3, (String) paramVector
							.get(i));
					localPreparedStatement.setString(4, "tmp_signServer");
					localPreparedStatement.setString(5, "tmp_signClient");
					localPreparedStatement.execute();
				}
			}
			str2 = String.valueOf(getRandom());
			str1 = "select role_id from ba_role where role_id=?";
			localPreparedStatement = localConnection.prepareStatement(str1);
			localPreparedStatement.setString(1, str2);
			for (localResultSet = localPreparedStatement.executeQuery(); localResultSet
					.next(); localResultSet = localPreparedStatement
					.executeQuery()) {
				str2 = String.valueOf(getRandom());
				localPreparedStatement = localConnection.prepareStatement(str1);
				localPreparedStatement.setString(1, str2);
			}
			for (int i = 0; i < paramVector.size(); i++) {
				str1 = "insert into ba_role values(?,?,?,?,?)";
				localPreparedStatement = localConnection.prepareStatement(str1);
				localPreparedStatement.setString(1, str2);
				localPreparedStatement.setString(2, paramString);
				localPreparedStatement
						.setString(3, (String) paramVector.get(i));
				localPreparedStatement.setString(4, "tmp_signServer");
				localPreparedStatement.setString(5, "tmp_signClient");
				localPreparedStatement.execute();
			}
			localConnection.commit();
			return str2;
		} catch (SQLException localSQLException1) {
			String str3;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8022", "保存角色信息失败", localSQLException1);
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

	public int deleteRole(String paramString) throws DBException {
		int i = -2;
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		String str = null;
		try {
			if (paramString == null) {
				return i;
			}
			localConnection = DriverManager.getConnection("proxool.ida");
			localConnection.setAutoCommit(false);
			str = "select role_id from admin where role_id=?";
			localPreparedStatement = localConnection.prepareStatement(str);
			localPreparedStatement.setString(1, paramString);
			localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next()) {
				i = -1;
			} else {
				str = "delete from ba_role where role_id=?";
				localPreparedStatement = localConnection.prepareStatement(str);
				localPreparedStatement.setString(1, paramString);
				i = localPreparedStatement.executeUpdate();
			}
			localConnection.commit();
			return i;
		} catch (SQLException localSQLException1) {
			int j;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException2) {
				}
			throw new DBException("8037", "删除角色失败", localSQLException1);
		} finally {
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

	public Hashtable getFuctionPointsInfo() throws DBException {
		Connection localConnection = null;
		Statement localStatement = null;
		ResultSet localResultSet = null;
		Hashtable localHashtable1 = new Hashtable();
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			String str = "select privilege_id,privilege from privilege";
			localStatement = localConnection.createStatement();
			localResultSet = localStatement.executeQuery(str);
			while (localResultSet.next())
				localHashtable1.put(localResultSet.getString(1), localResultSet
						.getString(2));
			return localHashtable1;
		} catch (SQLException localSQLException1) {
			Hashtable localHashtable2;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8027", "获取权限点信息失败", localSQLException1);
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

	public Hashtable getTemplateAdminsInfo() throws DBException {
		Connection localConnection = null;
		Statement localStatement = null;
		ResultSet localResultSet = null;
		Hashtable localHashtable = new Hashtable();
		Vector localVector1 = null;
		Vector localVector2 = null;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			String str1 = "select b.certsn,b.ctmlname,b.basedn,b.operation from ba_privilege b,cert c where b.certsn=c.certsn and isvalid=1 order by b.certsn";
			localStatement = localConnection.createStatement();
			localResultSet = localStatement.executeQuery(str1);
			localVector1 = new Vector();
			String str2 = null;
			while (localResultSet.next()) {
				int i;
				String str6;
				if (str2 == null) {
					str2 = localResultSet.getString(1);
					localVector2 = new Vector();
					localVector2.add(localResultSet.getString(2).trim());
					localObject1 = localResultSet.getString(3).trim();
					if (!((String) localObject1).equals("null")) {
						i = ((String) localObject1).length();
						for (int j = ((String) localObject1).length() - 1; j >= 0; j--) {
							if (((String) localObject1).charAt(j) != ',')
								continue;
							str6 = ((String) localObject1).substring(j + 1, i);
							i = j;
							localVector2.add(str6.trim());
						}
						String str3 = ((String) localObject1).substring(0, i);
						localVector2.add(str3.trim());
					}
					localVector1.add(localVector2);
					continue;
				}
				if (str2.equals(localResultSet.getString(1))) {
					localVector2 = new Vector();
					localVector2.add(localResultSet.getString(2).trim());
					localObject1 = localResultSet.getString(3).trim();
					if (!((String) localObject1).equals("null")) {
						i = ((String) localObject1).length();
						for (int k = ((String) localObject1).length() - 1; k >= 0; k--) {
							if (((String) localObject1).charAt(k) != ',')
								continue;
							str6 = ((String) localObject1).substring(k + 1, i);
							i = k;
							localVector2.add(str6.trim());
						}
						String str4 = ((String) localObject1).substring(0, i);
						localVector2.add(str4.trim());
					}
					localVector1.add(localVector2);
					continue;
				}
				localHashtable.put(str2, localVector1);
				localVector1 = new Vector();
				str2 = localResultSet.getString(1);
				localVector2 = new Vector();
				localVector2.add(localResultSet.getString(2).trim());
				localObject1 = localResultSet.getString(3).trim();
				if (!((String) localObject1).equals("null")) {
					i = ((String) localObject1).length();
					for (int m = ((String) localObject1).length() - 1; m >= 0; m--) {
						if (((String) localObject1).charAt(m) != ',')
							continue;
						str6 = ((String) localObject1).substring(m + 1, i);
						i = m;
						localVector2.add(str6.trim());
					}
					String str5 = ((String) localObject1).substring(0, i);
					localVector2.add(str5.trim());
				}
				localVector1.add(localVector2);
			}
			if (str2 != null)
				localHashtable.put(str2, localVector1);
			localObject1 = localHashtable;
		} catch (SQLException localSQLException1) {
			Object localObject1;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8028", "获取业务管理员信息失败", localSQLException1);
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

	public int setTemplateAdmin(String paramString1, String paramString2,
			String paramString3, String paramString4) throws DBException {
		Connection localConnection = null;
		int i = -1;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			i = setTemplateAdmin(localConnection, paramString1, paramString2,
					paramString3, paramString4);
			j = i;
		} catch (SQLException localSQLException1) {
			int j;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8024", "设置业务管理员权限失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException2) {
				}
		}
	}

	protected int setTemplateAdmin(Connection paramConnection, String paramString1, String paramString2, String paramString3, String paramString4)
    throws DBException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    int i = -1;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        j = i;
        jsr 267;
      }
      String str = "select count(certsn) from ba_privilege where certsn=? and ctmlname=? and basedn=?";
      localPreparedStatement = paramConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, paramString2);
      if (paramString3 == null)
        localPreparedStatement.setString(3, "null");
      else
        localPreparedStatement.setString(3, paramString3);
      localResultSet = localPreparedStatement.executeQuery();
      localResultSet.next();
      if (localResultSet.getInt(1) == 0)
      {
        str = "insert into ba_privilege values(?,?,?,?,?,?)";
        localPreparedStatement = paramConnection.prepareStatement(str);
        localPreparedStatement.setString(1, paramString1);
        localPreparedStatement.setString(2, paramString2);
        if (paramString3 == null)
          localPreparedStatement.setString(3, "null");
        else
          localPreparedStatement.setString(3, paramString3);
        localPreparedStatement.setString(4, paramString4);
        localPreparedStatement.setString(5, "tmp_signServer");
        localPreparedStatement.setString(6, "tmp_signClient");
        i = localPreparedStatement.executeUpdate();
      }
      else
      {
        i = 0;
      }
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8024", "设置业务管理员权限失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	public int setTemplateAdmin(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    Object localObject1 = null;
    int i = -1;
    try
    {
      if ((paramString == null) || (paramArrayOfString1 == null) || (paramArrayOfString2 == null))
      {
        j = i;
        jsr 321;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      localConnection.setAutoCommit(false);
      String str;
      if ((paramArrayOfString1 == null) || (paramArrayOfString2 == null))
      {
        str = "delete from ba_privilege where certsn=?";
        localPreparedStatement = localConnection.prepareStatement(str);
        localPreparedStatement.setString(1, paramString);
        i = localPreparedStatement.executeUpdate();
      }
      else
      {
        if (paramArrayOfString1.length != paramArrayOfString2.length)
          throw new DBException("8057", "设置业务管理员权限失败:模板和主题数量不相等");
        str = "delete from ba_privilege where certsn=?";
        localPreparedStatement = localConnection.prepareStatement(str);
        localPreparedStatement.setString(1, paramString);
        i = localPreparedStatement.executeUpdate();
        for (j = 0; j < paramArrayOfString1.length; j++)
        {
          str = "insert into ba_privilege values(?,?,?,?,?,?)";
          localPreparedStatement = localConnection.prepareStatement(str);
          localPreparedStatement.setString(1, paramString);
          localPreparedStatement.setString(2, paramArrayOfString1[j]);
          if (paramArrayOfString2[j].equals(""))
            localPreparedStatement.setString(3, "null");
          else
            localPreparedStatement.setString(3, paramArrayOfString2[j]);
          localPreparedStatement.setString(4, null);
          localPreparedStatement.setString(5, "tmp_signServer");
          localPreparedStatement.setString(6, "tmp_signClient");
          localPreparedStatement.executeUpdate();
        }
      }
      localConnection.commit();
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8046", "设置业务管理员权限失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int updateTemplateAdmin(String paramString1, String paramString2)
			throws DBException {
		Connection localConnection = null;
		int i = -1;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			i = updateTemplateAdmin(localConnection, paramString1, paramString2);
			j = i;
		} catch (DBException localDBException) {
			int j;
			if (localConnection != null)
				try {
					localConnection.rollback();
				} catch (SQLException localSQLException2) {
				}
			throw localDBException;
		} catch (SQLException localSQLException1) {
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8032", "更新业务管理员证书失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException3) {
				}
		}
	}

	protected int updateTemplateAdmin(Connection paramConnection, String paramString1, String paramString2)
    throws DBException
  {
    PreparedStatement localPreparedStatement = null;
    int i = -1;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        j = i;
        jsr 113;
      }
      String str = "update ba_privilege set certsn=? where certsn=?";
      localPreparedStatement = paramConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString2);
      localPreparedStatement.setString(2, paramString1);
      i = localPreparedStatement.executeUpdate();
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8032", "更新业务管理员证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	public int deleteTemplateAdmin(String paramString) throws DBException {
		int i = -1;
		Connection localConnection = null;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			i = deleteTemplateAdmin(localConnection, paramString);
			j = i;
		} catch (SQLException localSQLException1) {
			int j;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8040", "删除业务管理员失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException2) {
				}
		}
	}

	protected int deleteTemplateAdmin(Connection paramConnection, String paramString)
    throws DBException
  {
    int i = -1;
    PreparedStatement localPreparedStatement = null;
    String str = null;
    try
    {
      if (paramString == null)
      {
        j = i;
        jsr 102;
      }
      str = "delete from ba_privilege where certsn=?";
      localPreparedStatement = paramConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString);
      i = localPreparedStatement.executeUpdate();
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8040", "删除业务管理员失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	public Vector getConfig(String paramString) throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		Information localInformation = null;
		Vector localVector1 = new Vector();
		try {
			if (paramString == null) {
				return localVector1;
			}
			String str = "select property,value,isencrypted from config where modulename=?";
			localConnection = DriverManager.getConnection("proxool.ida");
			localPreparedStatement = localConnection.prepareStatement(str);
			localPreparedStatement.setString(1, paramString);
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next()) {
				localInformation = new Information();
				localInformation.setName(localResultSet.getString(1));
				localInformation.setValue(localResultSet.getString(2));
				localInformation.setIsEncrypted(localResultSet.getString(3));
				localVector1.addElement(localInformation);
			}
			return localVector1;
		} catch (SQLException localSQLException1) {
			throw new DBException("8021", "获取配置信息失败", localSQLException1);
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

	public int setConfig(String paramString, Information paramInformation)
			throws DBException {
		Connection localConnection = null;
		int i = -1;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			i = setConfig(localConnection, paramString, paramInformation);
			return i;
		} catch (SQLException localSQLException1) {
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8020", "保存配置信息失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException2) {
				}
		}
	}

	protected int setConfig(Connection paramConnection, String paramString,
			Information paramInformation) throws DBException {
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		int i = -1;
		try {
			if ((paramString == null) || (paramInformation == null)) {
				return i;
			}
			String str = "select count(modulename) from config where modulename=? and property=?";
			localPreparedStatement = paramConnection.prepareStatement(str);
			localPreparedStatement.setString(1, paramString);
			localPreparedStatement.setString(2, paramInformation.getName());
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			if (localResultSet.getInt(1) == 0) {
				str = "insert into config values(?,?,?,?,?,?)";
				localPreparedStatement = paramConnection.prepareStatement(str);
				localPreparedStatement.setString(1, paramString);
				localPreparedStatement.setString(2, paramInformation.getName());
				localPreparedStatement.setCharacterStream(3,
						new CharArrayReader(paramInformation.getValue()
								.toCharArray()), paramInformation.getValue()
								.length());
				localPreparedStatement.setString(4, paramInformation
						.getIsEncrypted());
				localPreparedStatement.setString(5, "tmp_signServer");
				localPreparedStatement.setString(6, "tmp_signClient");
				i = localPreparedStatement.executeUpdate();
			} else if (localResultSet.getInt(1) == 1) {
				str = "update config set value=?,isencrypted=? where modulename=? and property=?";
				localPreparedStatement = paramConnection.prepareStatement(str);
				localPreparedStatement.setCharacterStream(1,
						new CharArrayReader(paramInformation.getValue()
								.toCharArray()), paramInformation.getValue()
								.length());
				localPreparedStatement.setString(2, paramInformation
						.getIsEncrypted());
				localPreparedStatement.setString(3, paramString);
				localPreparedStatement.setString(4, paramInformation.getName());
				i = localPreparedStatement.executeUpdate();
			}
			return i;
		} catch (SQLException localSQLException1) {
			int j;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8020", "保存配置信息失败", localSQLException1);
		} finally {
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException2) {
				}
		}
	}

	public int saveOperationlog(Operation paramOperation) throws DBException {
		Connection localConnection = null;
		int i = -1;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			i = saveOperationlog(localConnection, paramOperation);
			return i;
		} catch (DBException localDBException) {
			int j;
			throw localDBException;
		} catch (SQLException localSQLException1) {
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8055", "保存操作日志失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException2) {
				}
		}
	}

	protected int saveOperationlog(Connection paramConnection,
			Operation paramOperation) throws DBException {
		PreparedStatement localPreparedStatement = null;
		int localSQLException1 = -1;
		try {
			int i;
			if (paramOperation == null) {
				return localSQLException1;
			}
			String str1 = getRandom(40);
			String str2 = "insert into operationlog values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			localPreparedStatement = paramConnection.prepareStatement(str2);
			try {
				localPreparedStatement.setString(1, str1);
				localPreparedStatement.setString(2, paramOperation
						.getOperatorSN());
				localPreparedStatement.setString(3, paramOperation
						.getOperatorDN().toUpperCase());
				localPreparedStatement.setString(4, paramOperation
						.getOperatorDN());
				if (paramOperation.getObjCertSN() == null)
					localPreparedStatement.setString(5, null);
				else
					localPreparedStatement.setString(5, paramOperation
							.getObjCertSN().toUpperCase());
				if (paramOperation.getObjSubject() != null)
					localPreparedStatement.setString(6, paramOperation
							.getObjSubject().toUpperCase());
				else
					localPreparedStatement.setString(6, paramOperation
							.getObjSubject());
				localPreparedStatement.setString(7, paramOperation
						.getObjSubject());
				localPreparedStatement.setString(8, paramOperation
						.getObjCTMLName());
				localPreparedStatement
						.setString(9, paramOperation.getOptType());
				localPreparedStatement.setLong(10, getTime());
				localPreparedStatement.setInt(11, paramOperation.getResult());
				localPreparedStatement.setString(12, "tmp_signServer");
				localPreparedStatement.setString(13, "tmp_signClient");
				localSQLException1 = localPreparedStatement.executeUpdate();
			} catch (SQLException localSQLException3) {
				str1 = getRandom(40);
				localPreparedStatement.setString(1, str1);
				localPreparedStatement.setString(2, paramOperation
						.getOperatorSN());
				localPreparedStatement.setString(3, paramOperation
						.getOperatorDN().toUpperCase());
				localPreparedStatement.setString(4, paramOperation
						.getOperatorDN());
				localPreparedStatement.setString(5, paramOperation
						.getObjCertSN());
				if (paramOperation.getObjSubject() != null)
					localPreparedStatement.setString(6, paramOperation
							.getObjSubject().toUpperCase());
				else
					localPreparedStatement.setString(6, paramOperation
							.getObjSubject());
				localPreparedStatement.setString(7, paramOperation
						.getObjSubject());
				localPreparedStatement.setString(8, paramOperation
						.getObjCTMLName());
				localPreparedStatement
						.setString(9, paramOperation.getOptType());
				localPreparedStatement.setLong(10, getTime());
				localPreparedStatement.setInt(11, paramOperation.getResult());
				localPreparedStatement.setString(12, "tmp_signServer");
				localPreparedStatement.setString(13, "tmp_signClient");
				localSQLException1 = localPreparedStatement.executeUpdate();
			}
			return localSQLException1;
		} catch (SQLException localSQLException2) {
			syslogger.info("数据库异常：" + localSQLException2.getMessage());
			throw new DBException("8055", "保存操作日志失败", localSQLException2);
		} finally {
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException4) {
				}
		}
	}

	public Vector getOperationLogInfo(Properties paramProperties, int paramInt1, int paramInt2, boolean paramBoolean)
    throws DBException
  {
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
    String[][] arrayOfString = (String[][])null;
    Object localObject1 = null;
    long l = 0L;
    try
    {
      if (paramProperties == null)
      {
        localVector3 = localVector1;
        jsr 1290;
      }
      arrayOfString = new String[paramProperties.size()][2];
      localEnumeration = paramProperties.propertyNames();
      if (localEnumeration.hasMoreElements())
      {
        str3 = " where ";
        while (localEnumeration.hasMoreElements())
        {
          String str1 = localEnumeration.nextElement().toString();
          arrayOfString[i][0] = paramProperties.getProperty(str1);
          if (str1.equals("optTimeBegin"))
          {
            str3 = str3 + "optTime" + ">=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("optTimeEnd"))
          {
            str3 = str3 + "optTime" + "<=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("result"))
          {
            str3 = str3 + str1 + "=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("objectSubject"))
          {
            if (!paramBoolean)
            {
              arrayOfString[i][0] = ("%" + setEscape4(arrayOfString[i][0]) + "%");
              str3 = str3 + "objectsubjectuppercase" + " like ? escape '\\' and ";
            }
            else
            {
              str3 = str3 + "objectsubjectuppercase" + "=? and ";
            }
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else if (str1.equals("operatorSubject"))
          {
            str3 = str3 + "operatorsubjectuppercase" + "=? and ";
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else
          {
            str3 = str3 + str1 + "=? and ";
            arrayOfString[i][1] = "S";
          }
          i++;
        }
        str3 = str3.substring(0, str3.length() - 5);
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      if (paramInt1 < 1)
        paramInt1 = 1;
      String str2 = "select operatorsn,operatorsubject,objectcertsn,objectsubject,objectctmlname,opttype,opttime,result from operationlog";
      if (orderby)
        str3 = str3 + " order by opttime desc";
      str2 = str2 + str3;
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase(), 1004, 1007);
      for (int j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      localResultSet = localPreparedStatement.executeQuery();
      if (paramInt2 < 1)
        while (localResultSet.next())
        {
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
      if (paramInt1 == 1)
      {
        do
        {
          if (!localResultSet.next())
            break;
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
        while (localResultSet.getRow() != paramInt1 + paramInt2 - 1);
      }
      else
      {
        localResultSet.absolute(paramInt1 - 1);
        while (localResultSet.next())
        {
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
          if (localResultSet.getRow() != paramInt1 + paramInt2 - 1)
            continue;
        }
      }
      if (localResultSet.next())
      {
        localResultSet.last();
        l = localResultSet.getRow();
      }
      else
      {
        localResultSet.absolute(-1);
        l = localResultSet.getRow();
      }
      arrayOfOperation = new Operation[localVector2.size()];
      localVector2.toArray(arrayOfOperation);
      localVector1.add(Long.toString(l));
      localVector1.add(arrayOfOperation);
      localVector3 = localVector1;
    }
    catch (SQLException localSQLException1)
    {
      Vector localVector3;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8050", "获取操作日志信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public Vector getCertStatistic(Properties paramProperties, String paramString, boolean paramBoolean)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    int i = 0;
    Properties localProperties = new Properties();
    Vector localVector1 = new Vector();
    String str3 = "";
    Enumeration localEnumeration = null;
    String[][] arrayOfString = (String[][])null;
    Object localObject1 = null;
    try
    {
      if (paramProperties == null)
      {
        localObject2 = localVector1;
        jsr 1407;
      }
      int k = paramProperties.size();
      Object localObject2 = paramProperties.getProperty("ctmlName");
      StringTokenizer localStringTokenizer;
      if ((localObject2 != null) && (((String)localObject2).indexOf("|") >= 0))
      {
        localStringTokenizer = new StringTokenizer((String)localObject2, "|");
        k += localStringTokenizer.countTokens();
      }
      localObject2 = paramProperties.getProperty("certStatus");
      if ((localObject2 != null) && (((String)localObject2).indexOf("|") >= 0))
      {
        localStringTokenizer = new StringTokenizer((String)localObject2, "|");
        k += localStringTokenizer.countTokens();
      }
      arrayOfString = new String[k][2];
      localEnumeration = paramProperties.propertyNames();
      if (localEnumeration.hasMoreElements())
      {
        str3 = " where ";
        while (localEnumeration.hasMoreElements())
        {
          String str1 = localEnumeration.nextElement().toString();
          arrayOfString[i][0] = paramProperties.getProperty(str1);
          if (str1.equals("createTimeStart"))
          {
            str3 = str3 + "createTime" + ">=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("createTimeEnd"))
          {
            str3 = str3 + "createTime" + "<=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("ctmlName"))
          {
            if (arrayOfString[i][0].indexOf("|") >= 0)
            {
              localStringTokenizer = new StringTokenizer(arrayOfString[i][0], "|");
              k = 0;
              for (str3 = str3 + str1 + " in ("; localStringTokenizer.hasMoreTokens(); str3 = str3 + "?,")
              {
                arrayOfString[i][0] = localStringTokenizer.nextToken();
                arrayOfString[i][1] = "S";
                i++;
              }
              i--;
              str3 = str3.substring(0, str3.length() - 1) + ") and ";
            }
            else
            {
              str3 = str3 + str1 + "=? and ";
              arrayOfString[i][1] = "S";
            }
          }
          else if (str1.equals("certStatus"))
          {
            if (arrayOfString[i][0].indexOf("|") >= 0)
            {
              localStringTokenizer = new StringTokenizer(arrayOfString[i][0], "|");
              for (str3 = str3 + str1 + " in ("; localStringTokenizer.hasMoreTokens(); str3 = str3 + "?,")
              {
                arrayOfString[i][0] = localStringTokenizer.nextToken();
                arrayOfString[i][1] = "S";
                i++;
              }
              i--;
              str3 = str3.substring(0, str3.length() - 1) + ") and ";
            }
            else
            {
              str3 = str3 + str1 + "=? and ";
              arrayOfString[i][1] = "S";
            }
          }
          else if (str1.equals("subject"))
          {
            if (!paramBoolean)
            {
              arrayOfString[i][0] = ("%" + setEscape4(arrayOfString[i][0]) + "%");
              str3 = str3 + "subjectuppercase" + " like ? escape '\\' and ";
            }
            else
            {
              str3 = str3 + "subjectuppercase" + "=? and ";
            }
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else if (str1.equals("applicant"))
          {
            str3 = str3 + "applicantuppercase" + "=? and ";
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else if ((str1.equals("notBefore")) || (str1.equals("notAfter")) || (str1.equals("validity")) || (str1.equals("createTime")))
          {
            str3 = str3 + str1 + "=? and ";
            arrayOfString[i][1] = "L";
          }
          else
          {
            str3 = str3 + str1 + "=? and ";
            arrayOfString[i][1] = "S";
          }
          i++;
        }
        str3 = str3.substring(0, str3.length() - 5);
      }
      if (paramString.equalsIgnoreCase("applicant"))
        paramString = "applicantuppercase";
      String str2 = "select " + paramString + ",count(" + paramString + ") from cert ";
      str2 = str2 + str3 + " group by " + paramString + " order by " + paramString;
      localConnection = DriverManager.getConnection("proxool.ida");
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase());
      for (int j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      localResultSet = localPreparedStatement.executeQuery();
      long l1 = 0L;
      long l2 = 0L;
      while (localResultSet.next())
      {
        String str4 = localResultSet.getString(1);
        l2 = localResultSet.getLong(2);
        l1 += l2;
        localProperties.put(str4, Long.toString(l2));
      }
      localVector1.add(Long.toString(l1));
      localVector1.add(localProperties);
      localVector2 = localVector1;
    }
    catch (SQLException localSQLException1)
    {
      Vector localVector2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8051", "获取证书统计信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int revokeRootCert(RevokeCertInfo paramRevokeCertInfo)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    int i = -1;
    try
    {
      if (paramRevokeCertInfo == null)
      {
        j = i;
        jsr 186;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      String str = "insert into revokedcert values(?,?,?,?,?,?,?,?)";
      localPreparedStatement = localConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramRevokeCertInfo.getCertSN());
      localPreparedStatement.setLong(2, paramRevokeCertInfo.getCDPID());
      localPreparedStatement.setInt(3, paramRevokeCertInfo.getReason());
      localPreparedStatement.setString(4, paramRevokeCertInfo.getReasionESC());
      localPreparedStatement.setLong(5, getTime());
      localPreparedStatement.setString(6, "tmp_signServer");
      localPreparedStatement.setString(7, "tmp_signClient");
      localPreparedStatement.setLong(8, paramRevokeCertInfo.getNotAfter());
      i = localPreparedStatement.executeUpdate();
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8047", "8047", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public long getMaxCDPID() throws DBException {
		Connection localConnection = null;
		Statement localStatement = null;
		ResultSet localResultSet = null;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			String str = "select max(cdpid) from cert";
			localStatement = localConnection.createStatement();
			localResultSet = localStatement.executeQuery(str);
			localResultSet.next();
			l = localResultSet.getLong(1);
		} catch (SQLException localSQLException1) {
			long l;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8053", "获取最大的CDPID失败", localSQLException1);
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

	public String getRABaseDN(String paramString)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    String str1 = null;
    try
    {
      String str2;
      if (paramString == null)
      {
        str2 = str1;
        jsr 394;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      localPreparedStatement = localConnection.prepareStatement("select ra_basedn from ra_basedn where certsn=?");
      localPreparedStatement.setString(1, paramString);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        str1 = localResultSet.getString(1);
      }
      else
      {
        localPreparedStatement = localConnection.prepareStatement("select value from config where modulename=? and property=?");
        localPreparedStatement.setString(1, "CAConfig");
        localPreparedStatement.setString(2, "BaseDN");
        localResultSet = localPreparedStatement.executeQuery();
        if (localResultSet.next())
        {
          str1 = localResultSet.getString(1);
          localPreparedStatement = localConnection.prepareStatement("select count(certsn) from ra_basedn");
          localResultSet = localPreparedStatement.executeQuery();
          localResultSet.next();
          int i = localResultSet.getInt(1);
          InternalConfig localInternalConfig = InternalConfig.getInstance();
          Vector localVector = localInternalConfig.getRaAdminDN();
          if (localVector.size() > i)
            str1 = (String)localVector.get(i) + "," + str1;
          else
            str1 = "OU=RA" + (i + 1) + "," + str1;
          localPreparedStatement = localConnection.prepareStatement("insert into ra_basedn values(?,?,?,?)");
          localPreparedStatement.setString(1, paramString);
          localPreparedStatement.setString(2, str1);
          localPreparedStatement.setString(3, "tmp_signServer");
          localPreparedStatement.setString(4, "tmp_signClient");
          localPreparedStatement.executeUpdate();
        }
      }
      str3 = str1;
    }
    catch (SQLException localSQLException1)
    {
      String str3;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8064", "获取RA管理员的BaseDN失败", localSQLException1);
    }
    catch (IDAException localIDAException)
    {
      throw new DBException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException.getHistory());
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public String updateRABaseDN(String paramString1, String paramString2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    String str1 = null;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        str2 = str1;
        jsr 184;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      localPreparedStatement = localConnection.prepareStatement("select ra_basedn from ra_basedn where certsn=?");
      localPreparedStatement.setString(1, paramString1);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        str1 = localResultSet.getString(1);
        localPreparedStatement = localConnection.prepareStatement("insert into ra_basedn values(?,?,?,?)");
        localPreparedStatement.setString(1, paramString2);
        localPreparedStatement.setString(2, str1);
        localPreparedStatement.setString(3, "tmp_signServer");
        localPreparedStatement.setString(4, "tmp_signClient");
        localPreparedStatement.executeUpdate();
      }
      str2 = str1;
    }
    catch (SQLException localSQLException1)
    {
      String str2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8064", "修改RA管理员的BaseDN失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public Vector getCRLInfo() throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		Vector localVector1 = new Vector();
		Vector localVector2 = new Vector();
		CRLInfo[] arrayOfCRLInfo = null;
		long l1 = 0L;
		try {
			String str1 = "select crl_name,crl_entity from crl";
			localConnection = DriverManager.getConnection("proxool.ida");
			localPreparedStatement = localConnection.prepareStatement(str1,
					1004, 1007);
			localResultSet = localPreparedStatement.executeQuery();
			long l2 = 0L;
			while (localResultSet.next()) {
				localObject1 = new CRLInfo();
				((CRLInfo) localObject1)
						.setCRLName(localResultSet.getString(1));
				byte[] arrayOfByte = localResultSet.getBytes(2);
				String str2 = null;
				if (arrayOfByte != null)
					str2 = new String(arrayOfByte);
				((CRLInfo) localObject1).setCRLEntity(str2);
				l1 += l2;
				localVector2.add(localObject1);
			}
			arrayOfCRLInfo = new CRLInfo[localVector2.size()];
			localVector2.toArray(arrayOfCRLInfo);
			localVector1.add(Long.toString(l1));
			localVector1.add(arrayOfCRLInfo);
			localObject1 = localVector1;
		} catch (SQLException localSQLException1) {
			Object localObject1;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8065", "获取证书注销列表信息失败", localSQLException1);
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

	protected int deleteCRL(Connection paramConnection)
    throws DBException
  {
    int i = -1;
    PreparedStatement localPreparedStatement = null;
    String str = null;
    try
    {
      if (paramConnection == null)
      {
        j = i;
        jsr 91;
      }
      str = "delete from crl";
      localPreparedStatement = paramConnection.prepareStatement(str);
      i = localPreparedStatement.executeUpdate();
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8066", "删除证书注销列表失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	public int saveCRL(CRLInfo[] paramArrayOfCRLInfo)
    throws DBException
  {
    SQLException localSQLException1 = -1;
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    String str1 = "insert into crl values (?,?,?,?)";
    try
    {
      int i;
      if (paramArrayOfCRLInfo == null)
      {
        i = localSQLException1;
        jsr 272;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      localConnection.setAutoCommit(false);
      deleteCRL(localConnection);
      String str2 = null;
      String str3 = null;
      localPreparedStatement = localConnection.prepareStatement(str1);
      for (j = 0; j < paramArrayOfCRLInfo.length; j++)
      {
        str2 = paramArrayOfCRLInfo[j].getCRLName();
        str3 = paramArrayOfCRLInfo[j].getCRLEntity();
        if ((str2 == null) || (str3 == null))
        {
          if (localConnection != null)
            try
            {
              localConnection.rollback();
            }
            catch (SQLException localSQLException3)
            {
            }
          localSQLException3 = localSQLException1;
          jsr 175;
        }
        localPreparedStatement.setString(1, str2);
        ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(str3.getBytes());
        localPreparedStatement.setBinaryStream(2, localByteArrayInputStream, str3.getBytes().length);
        localPreparedStatement.setString(3, "tmp_signServer");
        localPreparedStatement.setString(4, "tmp_signClient");
        localPreparedStatement.addBatch();
      }
      localPreparedStatement.executeBatch();
      localConnection.commit();
      localSQLException1 = paramArrayOfCRLInfo.length;
      j = localSQLException1;
    }
    catch (Exception localException)
    {
      int j;
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException2)
        {
        }
      syslogger.info("数据库异常：" + localException.getMessage());
      throw new DBException("8066", "批量增加证书注销列表失败", localException);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException4)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException5)
        {
        }
    }
  }

	public Hashtable getCertStandardExt(String paramString)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet1 = null;
    ResultSet localResultSet2 = null;
    Hashtable localHashtable = null;
    Vector localVector = null;
    try
    {
      if (paramString == null)
      {
        localObject1 = localHashtable;
        jsr 307;
      }
      Object localObject1 = "select ext_name,count(certsn) from cert_standard_ext where certsn=? group by ext_name order by ext_name";
      String str = "select ext_oid,ext_name,child_name,othername_oid,value from cert_standard_ext where certsn=? order by ext_name";
      localConnection = DriverManager.getConnection("proxool.ida");
      localPreparedStatement = localConnection.prepareStatement((String)localObject1);
      localPreparedStatement.setString(1, paramString);
      localResultSet1 = localPreparedStatement.executeQuery();
      localPreparedStatement = localConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString);
      localResultSet2 = localPreparedStatement.executeQuery();
      localHashtable = new Hashtable();
      while (localResultSet1.next())
      {
        localVector = new Vector();
        localObject2 = null;
        localObject2 = localResultSet1.getString(1);
        int i = localResultSet1.getInt(2);
        for (int j = 0; j < i; j++)
        {
          localResultSet2.next();
          StandardExtension localStandardExtension = new StandardExtension();
          localStandardExtension.setParentOID(localResultSet2.getString(1));
          localStandardExtension.setParentName(localResultSet2.getString(2));
          localStandardExtension.setChildName(localResultSet2.getString(3));
          localStandardExtension.setOtherNameOid(localResultSet2.getString(4));
          localStandardExtension.setStandardValue(localResultSet2.getString(5));
          localVector.add(localStandardExtension);
        }
        localHashtable.put(localObject2, localVector);
      }
      localObject2 = localHashtable;
    }
    catch (SQLException localSQLException1)
    {
      Object localObject2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8067", "获取证书标准扩展信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
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
			String str = "select count(certsn) from cert  where ctmlname=?  and (certstatus = 'Use'   or certstatus = 'Revoke'   or certstatus = 'Hold')";
			localConnection = DriverManager.getConnection("proxool.ida");
			localPreparedStatement = localConnection.prepareStatement(str);
			localPreparedStatement.setString(1, paramString);
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			l1 = localResultSet.getLong(1);
			l2 = l1;
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

	public CertInfo[] getCertsToPublish(String paramString, int paramInt1, int paramInt2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    CertInfo[] arrayOfCertInfo1 = null;
    Vector localVector = new Vector();
    String str = "";
    try
    {
      if (paramString == null)
      {
        arrayOfCertInfo2 = arrayOfCertInfo1;
        jsr 297;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      if (paramInt1 < 1)
        paramInt1 = 1;
      str = "select certsn,subject,certstatus,certentity from cert  where ctmlname=?  and (certstatus = 'Use'   or certstatus = 'Revoke'   or certstatus = 'Hold')";
      if (orderby)
        str = str + " order by createtime desc";
      localPreparedStatement = localConnection.prepareStatement(str, 1004, 1007);
      localPreparedStatement.setString(1, paramString);
      localResultSet = localPreparedStatement.executeQuery();
      if (paramInt2 < 1)
        while (localResultSet.next())
          localVector.add(getCertinfoForCertsToPublish(localResultSet));
      if (paramInt1 == 1)
      {
        do
        {
          if (!localResultSet.next())
            break;
          localVector.add(getCertinfoForCertsToPublish(localResultSet));
        }
        while (localResultSet.getRow() != paramInt1 + paramInt2 - 1);
      }
      else
      {
        localResultSet.absolute(paramInt1 - 1);
        while (localResultSet.next())
        {
          localVector.add(getCertinfoForCertsToPublish(localResultSet));
          if (localResultSet.getRow() != paramInt1 + paramInt2 - 1)
            continue;
        }
      }
      arrayOfCertInfo1 = new CertInfo[localVector.size()];
      localVector.toArray(arrayOfCertInfo1);
      arrayOfCertInfo2 = arrayOfCertInfo1;
    }
    catch (SQLException localSQLException1)
    {
      CertInfo[] arrayOfCertInfo2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8069", "依据模板获取证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	protected CertInfo getCertinfoForCertsToPublish(ResultSet paramResultSet)
			throws SQLException {
		CertInfo localCertInfo = new CertInfo();
		localCertInfo.setCertSN(paramResultSet.getString(1));
		localCertInfo.setSubject(paramResultSet.getString(2));
		localCertInfo.setCertStatus(paramResultSet.getString(3));
		localCertInfo.setCertEntity(paramResultSet.getBytes(4));
		return localCertInfo;
	}

	public Vector getAdminDN() throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		Vector localVector1 = new Vector();
		try {
			String str = "select distinct subject from cert t1  where exists (select t2.certsn from admin t2 where t2.certsn=t1.certsn)";
			localConnection = DriverManager.getConnection("proxool.ida");
			localPreparedStatement = localConnection.prepareStatement(str);
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next())
				localVector1.add(localResultSet.getString(1));
			if (localVector1 == null)
				throw new DBException("8070", "获取DN信息失败");
			localVector2 = localVector1;
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

	public int moveLog2Arc(Operation paramOperation)
    throws DBException
  {
    Connection localConnection = null;
    ArrayList localArrayList1 = 0;
    int i = 1000;
    try
    {
      if (paramOperation == null)
      {
        j = localArrayList1;
        jsr 168;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      localConnection.setAutoCommit(false);
      int j = i;
      while (j == i)
      {
        localArrayList2 = getOperationLog(localConnection, paramOperation, i, true);
        j = insertOperationLogArc(localConnection, localArrayList2);
        int k;
        if (j != localArrayList2.size())
        {
          localConnection.rollback();
          k = localArrayList1;
          jsr 99;
        }
        localArrayList1 += j;
        localConnection.commit();
      }
      localArrayList2 = localArrayList1;
    }
    catch (SQLException localSQLException1)
    {
      ArrayList localArrayList2;
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException2)
        {
        }
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8071", "归档操作日志失败", localSQLException1);
    }
    finally
    {
      if (localConnection != null)
        try
        {
          localConnection.close();
          localConnection = null;
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	protected Vector getOperationlogCondition(Operation paramOperation,
			boolean paramBoolean) {
		if (paramOperation == null)
			return null;
		Vector localVector = new Vector();
		String str1 = " where 1=1";
		ArrayList localArrayList = new ArrayList();
		if (paramOperation.getId() != null) {
			str1 = str1 + " and id=?";
			localArrayList.add(new ValuePair(paramOperation.getId(), "S"));
		}
		if (paramOperation.getOperatorSN() != null) {
			str1 = str1 + " and operatorsn=?";
			localArrayList.add(new ValuePair(paramOperation.getOperatorSN(),
					"S"));
		}
		if (paramOperation.getOperatorDN() != null) {
			str1 = str1 + " and operatorsubjectuppercase=?";
			localArrayList.add(new ValuePair(paramOperation.getOperatorDN()
					.toUpperCase(), "S"));
		}
		if (paramOperation.getObjCertSN() != null) {
			str1 = str1 + " and objectcertsn=?";
			localArrayList.add(new ValuePair(paramOperation.getObjCertSN()
					.toUpperCase(), "S"));
		}
		if (paramOperation.getObjSubject() != null)
			if (!paramBoolean) {
				str1 = str1 + " and objectsubjectuppercase like ?"
						+ setEscapeChar();
				localArrayList.add(new ValuePair("%"
						+ setEscape(paramOperation.getObjSubject()
								.toUpperCase()) + "%", "S"));
			} else {
				str1 = str1 + " and objectsubjectuppercase=?";
				localArrayList.add(new ValuePair(paramOperation.getObjSubject()
						.toUpperCase(), "S"));
			}
		Object localObject;
		if (paramOperation.getObjCTMLName() != null) {
			localObject = paramOperation.getObjCTMLName();
			String str2 = null;
			if (((String) localObject).indexOf("|") >= 0) {
				StringTokenizer localStringTokenizer = new StringTokenizer(
						(String) localObject, "|");
				for (str1 = str1 + " and objectctmlname in ("; localStringTokenizer
						.hasMoreTokens(); str1 = str1 + "?,") {
					str2 = localStringTokenizer.nextToken();
					localArrayList.add(new ValuePair(str2, "S"));
				}
				str1 = str1.substring(0, str1.length() - 1) + ")";
			} else {
				str2 = paramOperation.getObjCTMLName();
				str1 = str1 + " and objectctmlname=?";
				localArrayList.add(new ValuePair(str2, "S"));
			}
		}
		if (paramOperation.getOptType() != null)
			if (paramOperation.getOptType().indexOf("|") >= 0) {
				localObject = new StringTokenizer(paramOperation.getOptType(),
						"|");
				for (str1 = str1 + " and opttype in ("; ((StringTokenizer) localObject)
						.hasMoreTokens(); str1 = str1 + "?,")
					localArrayList.add(new ValuePair(
							((StringTokenizer) localObject).nextToken(), "S"));
				str1 = str1.substring(0, str1.length() - 1) + ")";
			} else {
				str1 = str1 + " and opttype=?";
				localArrayList.add(new ValuePair(paramOperation.getOptType(),
						"S"));
			}
		if (paramOperation.getResult() > -1) {
			str1 = str1 + " and result=?";
			localArrayList.add(new ValuePair(Integer.toString(paramOperation
					.getResult()), "S"));
		}
		if (paramOperation.getOptTime() > 0L) {
			str1 = str1 + " and opttime=?";
			localArrayList.add(new ValuePair(Long.toString(paramOperation
					.getOptTime()), "L"));
		}
		if (paramOperation.getOptTimeBegin() != null) {
			str1 = str1 + " and opttime>=?";
			localArrayList.add(new ValuePair(paramOperation.getOptTimeBegin(),
					"L"));
		}
		if (paramOperation.getOptTimeEnd() != null) {
			str1 = str1 + " and opttime<=?";
			localArrayList.add(new ValuePair(paramOperation.getOptTimeEnd(),
					"L"));
		}
		localVector.add(str1);
		localVector.add(localArrayList);
		return (Vector) localVector;
	}

	protected Operation getOperationlog(ResultSet paramResultSet)
			throws SQLException {
		Operation localOperation = new Operation();
		localOperation.setId(paramResultSet.getString(1));
		localOperation.setOperatorSN(paramResultSet.getString(2));
		localOperation.setOperatorDN(paramResultSet.getString(3));
		localOperation.setObjCertSN(paramResultSet.getString(4));
		localOperation.setObjSubject(paramResultSet.getString(5));
		localOperation.setObjCTMLName(paramResultSet.getString(6));
		localOperation.setOptType(paramResultSet.getString(7));
		localOperation.setOptTime(paramResultSet.getLong(8));
		localOperation.setResult(paramResultSet.getInt(9));
		localOperation.setSignServer(paramResultSet.getString(10));
		localOperation.setSignClient(paramResultSet.getString(11));
		return localOperation;
	}

	protected int insertOperationLogArc(Connection paramConnection, ArrayList paramArrayList)
    throws SQLException
  {
    Operation localOperation = null;
    PreparedStatement localPreparedStatement = null;
    int i = 0;
    try
    {
      if ((paramConnection == null) || (paramArrayList == null))
      {
        j = i;
        jsr 293;
      }
      localPreparedStatement = paramConnection.prepareStatement("insert into operationlogarc values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
      for (j = 0; j < paramArrayList.size(); j++)
      {
        localOperation = (Operation)paramArrayList.get(j);
        localPreparedStatement.setString(1, localOperation.getId());
        localPreparedStatement.setString(2, localOperation.getOperatorSN());
        if (localOperation.getOperatorDN() == null)
          localPreparedStatement.setString(3, null);
        else
          localPreparedStatement.setString(3, localOperation.getOperatorDN().toUpperCase());
        localPreparedStatement.setString(4, localOperation.getOperatorDN());
        localPreparedStatement.setString(5, localOperation.getObjCertSN());
        if (localOperation.getObjSubject() == null)
          localPreparedStatement.setString(6, null);
        else
          localPreparedStatement.setString(6, localOperation.getObjSubject().toUpperCase());
        localPreparedStatement.setString(7, localOperation.getObjSubject());
        localPreparedStatement.setString(8, localOperation.getObjCTMLName());
        localPreparedStatement.setString(9, localOperation.getOptType());
        localPreparedStatement.setLong(10, localOperation.getOptTime());
        localPreparedStatement.setInt(11, localOperation.getResult());
        localPreparedStatement.setString(12, localOperation.getSignServer());
        localPreparedStatement.setString(13, localOperation.getSignClient());
        localPreparedStatement.addBatch();
      }
      localPreparedStatement.executeBatch();
      i = paramArrayList.size();
      j = i;
    }
    finally
    {
      int j;
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException)
        {
        }
    }
  }

	protected ArrayList getOperationLog(Connection paramConnection, Operation paramOperation, int paramInt, boolean paramBoolean)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    ArrayList localArrayList1 = new ArrayList();
    String str = "";
    ArrayList localArrayList2 = null;
    try
    {
      if ((paramConnection == null) || (paramOperation == null) || (paramInt < 0))
      {
        localObject1 = localArrayList1;
        jsr 284;
      }
      Object localObject1 = getOperationlogCondition(paramOperation, paramBoolean);
      if (localObject1 == null)
      {
        localObject2 = localArrayList1;
        jsr 260;
      }
      str = ((Vector)localObject1).get(0).toString();
      localArrayList2 = (ArrayList)((Vector)localObject1).get(1);
      localPreparedStatement = paramConnection.prepareStatement("select id,operatorsn,operatorsubject,objectcertsn,objectsubject,objectctmlname,opttype,opttime,result,sign_server,sign_client from operationlog" + str, 1003, 1008);
      localPreparedStatement.setMaxRows(paramInt);
      localPreparedStatement.setFetchSize(paramInt);
      Object localObject2 = null;
      for (int i = 0; i < localArrayList2.size(); i++)
      {
        localObject2 = (ValuePair)localArrayList2.get(i);
        if (((ValuePair)localObject2).getType().equals("I"))
          localPreparedStatement.setInt(i + 1, Integer.parseInt(((ValuePair)localObject2).getValue()));
        else if (((ValuePair)localObject2).getType().equals("L"))
          localPreparedStatement.setLong(i + 1, Long.parseLong(((ValuePair)localObject2).getValue()));
        else
          localPreparedStatement.setString(i + 1, ((ValuePair)localObject2).getValue());
      }
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
        localArrayList1.add(getOperationlog(localResultSet));
        localResultSet.deleteRow();
      }
      localArrayList3 = localArrayList1;
    }
    finally
    {
      ArrayList localArrayList3;
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException)
        {
        }
    }
  }

	protected String setEscapeChar() {
		return " escape '\\'";
	}

	protected String setEscape(String paramString) {
		if (paramString.indexOf("%") >= 0)
			paramString = paramString.replaceAll("%", "\\\\%");
		if (paramString.indexOf("_") >= 0)
			paramString = paramString.replaceAll("_", "\\\\_");
		return paramString;
	}

	public int moveCert2Arc(String paramString, Vector paramVector)
    throws DBException
  {
    Connection localConnection = null;
    ArrayList localArrayList1 = 0;
    int i = 100;
    try
    {
      if (paramString == null)
      {
        j = localArrayList1;
        jsr 177;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      localConnection.setAutoCommit(false);
      int j = i;
      while (j == i)
      {
        localArrayList2 = getCerts(localConnection, paramString, paramVector, i);
        j = insertCertArc(localConnection, localArrayList2);
        int k;
        if (j != localArrayList2.size())
        {
          localConnection.rollback();
          k = localArrayList1;
          jsr 107;
        }
        localArrayList1 += j;
        localConnection.commit();
      }
      localArrayList2 = localArrayList1;
    }
    catch (SQLException localSQLException1)
    {
      ArrayList localArrayList2;
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException2)
        {
          localSQLException2.printStackTrace();
        }
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8072", "归档证书失败", localSQLException1);
    }
    finally
    {
      if (localConnection != null)
        try
        {
          localConnection.close();
          localConnection = null;
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	protected ArrayList getCerts(Connection paramConnection, String paramString, Vector paramVector, int paramInt)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    ArrayList localArrayList1 = new ArrayList();
    String str1 = "";
    ArrayList localArrayList2 = null;
    try
    {
      if ((paramConnection == null) || (paramString == null) || (paramInt < 0))
      {
        localObject1 = localArrayList1;
        jsr 416;
      }
      Object localObject1 = getCertsCondition(paramString);
      if (localObject1 == null)
      {
        localObject2 = localArrayList1;
        jsr 394;
      }
      str1 = ((Vector)localObject1).get(0).toString();
      localArrayList2 = (ArrayList)((Vector)localObject1).get(1);
      localPreparedStatement = paramConnection.prepareStatement("select certsn,subjectuppercase,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicantuppercase,applicant,certentity,email,remark,authcode_updatetime,sign_server,sign_client,isretainkey,iswaiting,oldsn from cert" + str1, 1003, 1008);
      localPreparedStatement.setMaxRows(paramInt);
      localPreparedStatement.setFetchSize(paramInt);
      Object localObject2 = null;
      for (int i = 0; i < localArrayList2.size(); i++)
      {
        localObject2 = (ValuePair)localArrayList2.get(i);
        if (((ValuePair)localObject2).getType().equals("I"))
          localPreparedStatement.setInt(i + 1, Integer.parseInt(((ValuePair)localObject2).getValue()));
        else if (((ValuePair)localObject2).getType().equals("L"))
          localPreparedStatement.setLong(i + 1, Long.parseLong(((ValuePair)localObject2).getValue()));
        else
          localPreparedStatement.setString(i + 1, ((ValuePair)localObject2).getValue());
      }
      localResultSet = localPreparedStatement.executeQuery();
      CertInfo localCertInfo = null;
      String str2 = null;
      String str3 = null;
      String str4 = null;
      while (localResultSet.next())
      {
        localCertInfo = getCert(localResultSet);
        str2 = localCertInfo.getCertSN();
        str3 = localCertInfo.getCertStatus();
        str4 = localCertInfo.getCtmlName();
        localArrayList1.add(localCertInfo);
        try
        {
          if ((str4 != null) && (paramVector != null) && (paramVector.contains(str4)) && (str3 != null) && (!str3.equals("Undown")) && (!str3.equals("UndownRevoke")) && (insertCertArcForKMC(paramConnection, str2) == -1))
            paramConnection.rollback();
          if (deleteFKDataforCert(paramConnection, str2) == -1)
            paramConnection.rollback();
        }
        catch (Exception localException)
        {
          paramConnection.rollback();
          localException.printStackTrace();
        }
        localResultSet.deleteRow();
      }
      localArrayList3 = localArrayList1;
    }
    finally
    {
      ArrayList localArrayList3;
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException)
        {
        }
    }
  }

	protected Vector getCertsCondition(String paramString) {
		if (paramString == null)
			return null;
		Vector localVector = new Vector();
		String str = " where 1=1";
		ArrayList localArrayList = new ArrayList();
		if (paramString != null) {
			str = str + " and notafter<=?";
			localArrayList.add(new ValuePair(paramString, "L"));
		}
		localVector.add(str);
		localVector.add(localArrayList);
		return localVector;
	}

	protected CertInfo getCert(ResultSet paramResultSet) throws SQLException {
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
		byte[] arrayOfByte = paramResultSet.getBytes(15);
		if (arrayOfByte != null)
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

	protected int insertCertArc(Connection paramConnection, ArrayList paramArrayList)
    throws SQLException
  {
    CertInfo localCertInfo = null;
    PreparedStatement localPreparedStatement = null;
    int i = 0;
    try
    {
      if ((paramConnection == null) || (paramArrayList == null))
      {
        j = i;
        jsr 372;
      }
      localPreparedStatement = paramConnection.prepareStatement("insert into certarc values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
      for (j = 0; j < paramArrayList.size(); j++)
      {
        localCertInfo = (CertInfo)paramArrayList.get(j);
        localPreparedStatement.setString(1, localCertInfo.getCertSN());
        localPreparedStatement.setString(2, localCertInfo.getSubjectUppercase());
        localPreparedStatement.setString(3, localCertInfo.getSubject());
        localPreparedStatement.setLong(4, localCertInfo.getNotBefore());
        localPreparedStatement.setLong(5, localCertInfo.getNotAfter());
        localPreparedStatement.setLong(6, localCertInfo.getValidity());
        localPreparedStatement.setString(7, localCertInfo.getAuthCode());
        localPreparedStatement.setLong(8, localCertInfo.getCdpid());
        localPreparedStatement.setString(9, localCertInfo.getCtmlName());
        localPreparedStatement.setString(10, localCertInfo.getCertStatus());
        localPreparedStatement.setLong(11, localCertInfo.getIsValid());
        localPreparedStatement.setLong(12, localCertInfo.getCreateTime());
        localPreparedStatement.setString(13, localCertInfo.getApplicantUppercase());
        localPreparedStatement.setString(14, localCertInfo.getApplicant());
        localPreparedStatement.setBytes(15, localCertInfo.getCertEntity());
        localPreparedStatement.setString(16, localCertInfo.getEmail());
        localPreparedStatement.setString(17, localCertInfo.getRemark());
        localPreparedStatement.setLong(18, localCertInfo.getAuthCodeUpdateTime());
        localPreparedStatement.setString(19, localCertInfo.getSignServer());
        localPreparedStatement.setString(20, localCertInfo.getSignClient());
        localPreparedStatement.setString(21, localCertInfo.getIsRetainKey());
        localPreparedStatement.setString(22, localCertInfo.getIswaiting());
        localPreparedStatement.setString(23, localCertInfo.getOldSN());
        localPreparedStatement.executeUpdate();
      }
      i = paramArrayList.size();
      j = i;
    }
    finally
    {
      int j;
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException)
        {
        }
    }
  }

	public Vector getOperationLogArcInfo(Properties paramProperties, int paramInt1, int paramInt2, boolean paramBoolean)
    throws DBException
  {
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
    String[][] arrayOfString = (String[][])null;
    Object localObject1 = null;
    long l = 0L;
    try
    {
      if (paramProperties == null)
      {
        localVector3 = localVector1;
        jsr 1290;
      }
      arrayOfString = new String[paramProperties.size()][2];
      localEnumeration = paramProperties.propertyNames();
      if (localEnumeration.hasMoreElements())
      {
        str3 = " where ";
        while (localEnumeration.hasMoreElements())
        {
          String str1 = localEnumeration.nextElement().toString();
          arrayOfString[i][0] = paramProperties.getProperty(str1);
          if (str1.equals("optTimeBegin"))
          {
            str3 = str3 + "optTime" + ">=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("optTimeEnd"))
          {
            str3 = str3 + "optTime" + "<=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("result"))
          {
            str3 = str3 + str1 + "=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("objectSubject"))
          {
            if (!paramBoolean)
            {
              arrayOfString[i][0] = ("%" + setEscape4(arrayOfString[i][0]) + "%");
              str3 = str3 + "objectsubjectuppercase" + " like ? escape '\\' and ";
            }
            else
            {
              str3 = str3 + "objectsubjectuppercase" + "=? and ";
            }
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else if (str1.equals("operatorSubject"))
          {
            str3 = str3 + "operatorsubjectuppercase" + "=? and ";
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else
          {
            str3 = str3 + str1 + "=? and ";
            arrayOfString[i][1] = "S";
          }
          i++;
        }
        str3 = str3.substring(0, str3.length() - 5);
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      if (paramInt1 < 1)
        paramInt1 = 1;
      String str2 = "select operatorsn,operatorsubject,objectcertsn,objectsubject,objectctmlname,opttype,opttime,result from operationlogarc";
      if (orderby)
        str3 = str3 + " order by opttime desc";
      str2 = str2 + str3;
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase(), 1004, 1007);
      for (int j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      localResultSet = localPreparedStatement.executeQuery();
      if (paramInt2 < 1)
        while (localResultSet.next())
        {
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
      if (paramInt1 == 1)
      {
        do
        {
          if (!localResultSet.next())
            break;
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
        while (localResultSet.getRow() != paramInt1 + paramInt2 - 1);
      }
      else
      {
        localResultSet.absolute(paramInt1 - 1);
        while (localResultSet.next())
        {
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
          if (localResultSet.getRow() != paramInt1 + paramInt2 - 1)
            continue;
        }
      }
      if (localResultSet.next())
      {
        localResultSet.last();
        l = localResultSet.getRow();
      }
      else
      {
        localResultSet.absolute(-1);
        l = localResultSet.getRow();
      }
      arrayOfOperation = new Operation[localVector2.size()];
      localVector2.toArray(arrayOfOperation);
      localVector1.add(Long.toString(l));
      localVector1.add(arrayOfOperation);
      localVector3 = localVector1;
    }
    catch (SQLException localSQLException1)
    {
      Vector localVector3;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8076", "获取归档操作日志信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	protected ArrayList getCertSelfext(Connection paramConnection, String paramString)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    ArrayList localArrayList = new ArrayList();
    try
    {
      if ((paramConnection == null) || (paramString == null))
      {
        localObject1 = localArrayList;
        jsr 176;
      }
      Object localObject1 = "select certsn,oid,selfext_name,value,sign_server,sign_client from cert_selfext where certsn=?";
      localPreparedStatement = paramConnection.prepareStatement((String)localObject1, 1003, 1008);
      localPreparedStatement.setString(1, paramString);
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
        localObject2 = new CertSelfExt();
        ((CertSelfExt)localObject2).setCertSn(localResultSet.getString(1));
        ((CertSelfExt)localObject2).setOid(localResultSet.getString(2));
        ((CertSelfExt)localObject2).setName(localResultSet.getString(3));
        ((CertSelfExt)localObject2).setValue(localResultSet.getString(4));
        ((CertSelfExt)localObject2).setSignServer(localResultSet.getString(5));
        ((CertSelfExt)localObject2).setSignClient(localResultSet.getString(6));
        localArrayList.add(localObject2);
        localResultSet.deleteRow();
      }
      localObject2 = localArrayList;
    }
    finally
    {
      Object localObject2;
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException)
        {
        }
    }
  }

	protected int insertCertSelfextArc(Connection paramConnection, ArrayList paramArrayList)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    CertSelfExt localCertSelfExt = null;
    int i = 0;
    try
    {
      int j;
      if ((paramConnection == null) || (paramArrayList == null))
      {
        j = i;
        jsr 159;
      }
      String str = "insert into cert_selfext_arc values(?,?,?,?,?,?)";
      localPreparedStatement = paramConnection.prepareStatement(str);
      for (k = 0; k < paramArrayList.size(); k++)
      {
        localCertSelfExt = (CertSelfExt)paramArrayList.get(k);
        localPreparedStatement.setString(1, localCertSelfExt.getCertSn());
        localPreparedStatement.setString(2, localCertSelfExt.getOid());
        localPreparedStatement.setString(3, localCertSelfExt.getName());
        localPreparedStatement.setString(4, localCertSelfExt.getValue());
        localPreparedStatement.setString(5, localCertSelfExt.getSignServer());
        localPreparedStatement.setString(6, localCertSelfExt.getSignClient());
        localPreparedStatement.addBatch();
      }
      localPreparedStatement.executeBatch();
      i = paramArrayList.size();
      k = i;
    }
    finally
    {
      int k;
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException)
        {
        }
    }
  }

	protected ArrayList getCertStandardExt(Connection paramConnection, String paramString)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    ArrayList localArrayList = new ArrayList();
    try
    {
      if ((paramConnection == null) || (paramString == null))
      {
        localObject1 = localArrayList;
        jsr 204;
      }
      Object localObject1 = "select certsn,ext_oid,ext_name,child_name,othername_oid,value,sign_server,sign_client from cert_standard_ext where certsn=?";
      localPreparedStatement = paramConnection.prepareStatement((String)localObject1, 1003, 1008);
      localPreparedStatement.setString(1, paramString);
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
        localObject2 = new CertStandardExt();
        ((CertStandardExt)localObject2).setCertSn(localResultSet.getString(1));
        ((CertStandardExt)localObject2).setExtOID(localResultSet.getString(2));
        ((CertStandardExt)localObject2).setExtName(localResultSet.getString(3));
        ((CertStandardExt)localObject2).setChildName(localResultSet.getString(4));
        ((CertStandardExt)localObject2).setOtherNameOID(localResultSet.getString(5));
        ((CertStandardExt)localObject2).setValue(localResultSet.getString(6));
        ((CertStandardExt)localObject2).setSignServer(localResultSet.getString(7));
        ((CertStandardExt)localObject2).setSignClient(localResultSet.getString(8));
        localArrayList.add(localObject2);
        localResultSet.deleteRow();
      }
      localObject2 = localArrayList;
    }
    finally
    {
      Object localObject2;
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException)
        {
        }
    }
  }

	protected int insertCertStandardExtArc(Connection paramConnection, ArrayList paramArrayList)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    CertStandardExt localCertStandardExt = null;
    int i = 0;
    try
    {
      int j;
      if ((paramConnection == null) || (paramArrayList == null))
      {
        j = i;
        jsr 185;
      }
      String str = "insert into cert_standard_ext_arc values(?,?,?,?,?,?,?,?)";
      localPreparedStatement = paramConnection.prepareStatement(str);
      for (k = 0; k < paramArrayList.size(); k++)
      {
        localCertStandardExt = (CertStandardExt)paramArrayList.get(k);
        localPreparedStatement.setString(1, localCertStandardExt.getCertSn());
        localPreparedStatement.setString(2, localCertStandardExt.getExtOID());
        localPreparedStatement.setString(3, localCertStandardExt.getExtName());
        localPreparedStatement.setString(4, localCertStandardExt.getChildName());
        localPreparedStatement.setString(5, localCertStandardExt.getOtherNameOID());
        localPreparedStatement.setString(6, localCertStandardExt.getValue());
        localPreparedStatement.setString(7, localCertStandardExt.getSignServer());
        localPreparedStatement.setString(8, localCertStandardExt.getSignClient());
        localPreparedStatement.addBatch();
      }
      localPreparedStatement.executeBatch();
      i = paramArrayList.size();
      k = i;
    }
    finally
    {
      int k;
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException)
        {
        }
    }
  }

	protected ArrayList getCertRevoked(Connection paramConnection, String paramString)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    ArrayList localArrayList = new ArrayList();
    try
    {
      if ((paramConnection == null) || (paramString == null))
      {
        localObject1 = localArrayList;
        jsr 204;
      }
      Object localObject1 = "select certsn,cdpid,reason,reasondesc,revoketime,sign_server,sign_client,certnotafter from revokedcert where certsn=?";
      localPreparedStatement = paramConnection.prepareStatement((String)localObject1, 1003, 1008);
      localPreparedStatement.setString(1, paramString);
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
        localObject2 = new RevokeCert();
        ((RevokeCert)localObject2).setCertSn(localResultSet.getString(1));
        ((RevokeCert)localObject2).setCdPID(localResultSet.getLong(2));
        ((RevokeCert)localObject2).setReason(localResultSet.getInt(3));
        ((RevokeCert)localObject2).setReasonDesc(localResultSet.getString(4));
        ((RevokeCert)localObject2).setRevokeTime(localResultSet.getLong(5));
        ((RevokeCert)localObject2).setSignServer(localResultSet.getString(6));
        ((RevokeCert)localObject2).setSignClient(localResultSet.getString(7));
        ((RevokeCert)localObject2).setNotAfter(localResultSet.getLong(8));
        localArrayList.add(localObject2);
        localResultSet.deleteRow();
      }
      localObject2 = localArrayList;
    }
    finally
    {
      Object localObject2;
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException)
        {
        }
    }
  }

	protected int insertCertRevokedArc(Connection paramConnection, ArrayList paramArrayList)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    RevokeCert localRevokeCert = null;
    int i = 0;
    try
    {
      int j;
      if ((paramConnection == null) || (paramArrayList == null))
      {
        j = i;
        jsr 179;
      }
      String str = "insert into revokedcertarc values(?,?,?,?,?,?,?,?)";
      localPreparedStatement = paramConnection.prepareStatement(str);
      for (k = 0; k < paramArrayList.size(); k++)
      {
        localRevokeCert = (RevokeCert)paramArrayList.get(k);
        localPreparedStatement.setString(1, localRevokeCert.getCertSn());
        localPreparedStatement.setLong(2, localRevokeCert.getCdPID());
        localPreparedStatement.setInt(3, localRevokeCert.getReason());
        localPreparedStatement.setString(4, localRevokeCert.getReasonDesc());
        localPreparedStatement.setLong(5, localRevokeCert.getRevokeTime());
        localPreparedStatement.setString(6, localRevokeCert.getSignServer());
        localPreparedStatement.setString(7, localRevokeCert.getSignClient());
        localPreparedStatement.setLong(8, localRevokeCert.getNotAfter());
        localPreparedStatement.executeUpdate();
      }
      i = paramArrayList.size();
      k = i;
    }
    finally
    {
      int k;
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException)
        {
        }
    }
  }

	protected int deleteCertTBP(Connection paramConnection, String paramString)
    throws DBException
  {
    int i = 0;
    PreparedStatement localPreparedStatement = null;
    try
    {
      if ((paramConnection == null) || (paramString == null))
      {
        j = i;
        jsr 98;
      }
      localPreparedStatement = paramConnection.prepareStatement("delete from certtbp where certsn=?");
      localPreparedStatement.setString(1, paramString);
      i = localPreparedStatement.executeUpdate();
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8054", "删除待发布证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	protected int deletePendingTask(Connection paramConnection, String paramString)
    throws DBException
  {
    int i = 0;
    PreparedStatement localPreparedStatement = null;
    try
    {
      if ((paramConnection == null) || (paramString == null))
      {
        j = i;
        jsr 98;
      }
      localPreparedStatement = paramConnection.prepareStatement("delete from pendingtask where certsn=?");
      localPreparedStatement.setString(1, paramString);
      i = localPreparedStatement.executeUpdate();
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8602", "删除非待操作状态证书信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	protected int deleteFKDataforCert(Connection paramConnection, String paramString)
    throws DBException
  {
    int i = -1;
    Object localObject1 = null;
    try
    {
      int j;
      if ((paramConnection == null) || (paramString == null))
      {
        j = i;
        jsr 190;
      }
      ArrayList localArrayList1 = getCertSelfext(paramConnection, paramString);
      int k = insertCertSelfextArc(paramConnection, localArrayList1);
      i = k;
      int m;
      if (k != localArrayList1.size())
      {
        i = -1;
        m = i;
        jsr 149;
      }
      ArrayList localArrayList2 = getCertStandardExt(paramConnection, paramString);
      int n = insertCertStandardExtArc(paramConnection, localArrayList2);
      i = n;
      int i1;
      if (n != localArrayList2.size())
      {
        i = -1;
        i1 = i;
        jsr 108;
      }
      try
      {
        deleteAdmin(paramConnection, paramString);
        deleteTemplateAdmin(paramConnection, paramString);
        deleteCertTBP(paramConnection, paramString);
        deletePendingTask(paramConnection, paramString);
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      i2 = 1;
    }
    catch (SQLException localSQLException1)
    {
      int i2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8072", "归档证书失败", localSQLException1);
    }
    finally
    {
      if (localObject1 != null)
        try
        {
          localObject1.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	public CertInfo getCertArcInfo(String paramString)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    CertInfo localCertInfo1 = null;
    try
    {
      if (paramString == null)
      {
        localObject1 = localCertInfo1;
        jsr 360;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      Object localObject1 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark,authcode_updatetime,isretainkey,oldsn from certarc where certsn=?";
      localPreparedStatement = localConnection.prepareStatement((String)localObject1);
      localPreparedStatement.setString(1, paramString);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        localCertInfo1 = new CertInfo();
        localCertInfo1.setCertSN(localResultSet.getString(1));
        localCertInfo1.setSubject(localResultSet.getString(2));
        localCertInfo1.setNotBefore(localResultSet.getLong(3));
        localCertInfo1.setNotAfter(localResultSet.getLong(4));
        localCertInfo1.setValidity(localResultSet.getInt(5));
        localCertInfo1.setAuthCode(localResultSet.getString(6));
        localCertInfo1.setCdpid(localResultSet.getInt(7));
        localCertInfo1.setCtmlName(localResultSet.getString(8));
        localCertInfo1.setCertStatus(localResultSet.getString(9));
        localCertInfo1.setIsValid(localResultSet.getLong(10));
        localCertInfo1.setCreateTime(localResultSet.getLong(11));
        localCertInfo1.setApplicant(localResultSet.getString(12));
        localCertInfo1.setEmail(localResultSet.getString(13));
        localCertInfo1.setRemark(localResultSet.getString(14));
        localCertInfo1.setAuthCodeUpdateTime(localResultSet.getLong(15));
        localCertInfo1.setIsRetainKey(localResultSet.getString(16));
        localCertInfo1.setOldSN(localResultSet.getString(17));
      }
      localCertInfo2 = localCertInfo1;
    }
    catch (SQLException localSQLException1)
    {
      CertInfo localCertInfo2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8073", "按序列号获取归档证书信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public CertInfo getCertArcInfo(String paramString1, String paramString2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    CertInfo localCertInfo1 = null;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        localObject1 = localCertInfo1;
        jsr 333;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      Object localObject1 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark from certarc where subjectuppercase=? and ctmlname=? and isvalid=1";
      localPreparedStatement = localConnection.prepareStatement((String)localObject1);
      localPreparedStatement.setString(1, paramString1.toUpperCase());
      localPreparedStatement.setString(2, paramString2);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        localCertInfo1 = new CertInfo();
        localCertInfo1.setCertSN(localResultSet.getString(1));
        localCertInfo1.setSubject(localResultSet.getString(2));
        localCertInfo1.setNotBefore(localResultSet.getLong(3));
        localCertInfo1.setNotAfter(localResultSet.getLong(4));
        localCertInfo1.setValidity(localResultSet.getInt(5));
        localCertInfo1.setAuthCode(localResultSet.getString(6));
        localCertInfo1.setCdpid(localResultSet.getInt(7));
        localCertInfo1.setCtmlName(localResultSet.getString(8));
        localCertInfo1.setCertStatus(localResultSet.getString(9));
        localCertInfo1.setIsValid(localResultSet.getLong(10));
        localCertInfo1.setCreateTime(localResultSet.getLong(11));
        localCertInfo1.setApplicant(localResultSet.getString(12));
        localCertInfo1.setEmail(localResultSet.getString(13));
        localCertInfo1.setRemark(localResultSet.getString(14));
      }
      localCertInfo2 = localCertInfo1;
    }
    catch (SQLException localSQLException1)
    {
      CertInfo localCertInfo2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8074", "按主题获取归档证书信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public Vector getCertArcInfo(Properties paramProperties, int paramInt1, int paramInt2, boolean paramBoolean)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    int i = 0;
    CertInfo[] arrayOfCertInfo = null;
    Vector localVector1 = new Vector();
    Vector localVector2 = new Vector();
    String str3 = "";
    Enumeration localEnumeration = null;
    String[][] arrayOfString = (String[][])null;
    long l = 0L;
    try
    {
      if (paramProperties == null)
      {
        localObject1 = localVector1;
        jsr 1672;
      }
      int k = paramProperties.size();
      Object localObject1 = paramProperties.getProperty("ctmlName");
      if ((localObject1 != null) && (((String)localObject1).indexOf("|") >= 0))
      {
        localObject2 = new StringTokenizer((String)localObject1, "|");
        k += ((StringTokenizer)localObject2).countTokens();
      }
      localObject1 = paramProperties.getProperty("certStatus");
      if ((localObject1 != null) && (((String)localObject1).indexOf("|") >= 0))
      {
        localObject2 = new StringTokenizer((String)localObject1, "|");
        k += ((StringTokenizer)localObject2).countTokens();
      }
      arrayOfString = new String[k][2];
      localEnumeration = paramProperties.propertyNames();
      if (localEnumeration.hasMoreElements())
      {
        str3 = " where ";
        while (localEnumeration.hasMoreElements())
        {
          String str1 = localEnumeration.nextElement().toString();
          arrayOfString[i][0] = paramProperties.getProperty(str1);
          if (str1.equals("createTimeStart"))
          {
            str3 = str3 + "createTime" + ">=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("createTimeEnd"))
          {
            str3 = str3 + "createTime" + "<=? and ";
            arrayOfString[i][1] = "L";
          }
          else if (str1.equals("ctmlName"))
          {
            if (arrayOfString[i][0].indexOf("|") >= 0)
            {
              localObject2 = new StringTokenizer(arrayOfString[i][0], "|");
              k = 0;
              for (str3 = str3 + str1 + " in ("; ((StringTokenizer)localObject2).hasMoreTokens(); str3 = str3 + "?,")
              {
                arrayOfString[i][0] = ((StringTokenizer)localObject2).nextToken();
                arrayOfString[i][1] = "S";
                i++;
              }
              i--;
              str3 = str3.substring(0, str3.length() - 1) + ") and ";
            }
            else
            {
              str3 = str3 + str1 + "=? and ";
              arrayOfString[i][1] = "S";
            }
          }
          else if (str1.equals("certStatus"))
          {
            if (arrayOfString[i][0].indexOf("|") >= 0)
            {
              localObject2 = new StringTokenizer(arrayOfString[i][0], "|");
              for (str3 = str3 + str1 + " in ("; ((StringTokenizer)localObject2).hasMoreTokens(); str3 = str3 + "?,")
              {
                arrayOfString[i][0] = ((StringTokenizer)localObject2).nextToken();
                arrayOfString[i][1] = "S";
                i++;
              }
              i--;
              str3 = str3.substring(0, str3.length() - 1) + ") and ";
            }
            else
            {
              str3 = str3 + str1 + "=? and ";
              arrayOfString[i][1] = "S";
            }
          }
          else if (str1.equals("subject"))
          {
            if (!paramBoolean)
            {
              arrayOfString[i][0] = ("%" + setEscape4(arrayOfString[i][0]) + "%");
              str3 = str3 + "subjectuppercase" + " like ? escape '\\' and ";
            }
            else
            {
              str3 = str3 + "subjectuppercase" + "=? and ";
            }
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else if (str1.equals("applicant"))
          {
            str3 = str3 + "applicantuppercase" + "=? and ";
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else if (str1.equals("certSN"))
          {
            str3 = str3 + "certarc." + "certSN" + "=? and ";
            arrayOfString[i][0] = arrayOfString[i][0].toUpperCase();
            arrayOfString[i][1] = "S";
          }
          else if (str1.equals("cdpid"))
          {
            str3 = str3 + "certarc." + "cdpid" + "=? and ";
            arrayOfString[i][1] = "S";
          }
          else if ((str1.equals("notBefore")) || (str1.equals("notAfter")) || (str1.equals("validity")) || (str1.equals("cdpid")) || (str1.equals("isvalid")) || (str1.equals("createTime")))
          {
            str3 = str3 + str1 + "=? and ";
            arrayOfString[i][1] = "L";
          }
          else
          {
            str3 = str3 + str1 + "=? and ";
            arrayOfString[i][1] = "S";
          }
          i++;
        }
        str3 = str3.substring(0, str3.length() - 5);
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      if (paramInt1 < 1)
        paramInt1 = 1;
      String str2 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark from certarc";
      if (orderby)
        str3 = str3 + " order by createtime desc";
      str2 = str2 + str3;
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase(), 1004, 1007);
      for (int j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      localResultSet = localPreparedStatement.executeQuery();
      if (paramInt2 < 1)
        while (localResultSet.next())
          localVector2.add(getCertinfoArcCondition(localResultSet));
      if (paramInt1 == 1)
      {
        do
        {
          if (!localResultSet.next())
            break;
          localVector2.add(getCertinfoArcCondition(localResultSet));
        }
        while (localResultSet.getRow() != paramInt1 + paramInt2 - 1);
      }
      else
      {
        localResultSet.absolute(paramInt1 - 1);
        while (localResultSet.next())
        {
          localVector2.add(getCertinfoArcCondition(localResultSet));
          if (localResultSet.getRow() != paramInt1 + paramInt2 - 1)
            continue;
        }
      }
      if (localResultSet.next())
      {
        localResultSet.last();
        l = localResultSet.getRow();
      }
      else
      {
        localResultSet.absolute(-1);
        l = localResultSet.getRow();
      }
      arrayOfCertInfo = new CertInfo[localVector2.size()];
      localVector2.toArray(arrayOfCertInfo);
      localVector1.add(Long.toString(l));
      localVector1.add(arrayOfCertInfo);
      localObject2 = localVector1;
    }
    catch (SQLException localSQLException1)
    {
      Object localObject2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8075", "获取归档证书信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public Vector getCertSnForKMC() throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		Vector localVector1 = new Vector();
		try {
			String str = "select certsn from certarcforkmc";
			localConnection = DriverManager.getConnection("proxool.ida");
			localPreparedStatement = localConnection.prepareStatement(str);
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next())
				localVector1.add(localResultSet.getString(1));
			localVector2 = localVector1;
		} catch (SQLException localSQLException1) {
			Vector localVector2;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8078", "获取已过期加密证书的CERTSN信息失败",
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
				} catch (SQLException localSQLException3) {
				}
		}
	}

	protected int deleteCertArcForKMC(Connection paramConnection)
			throws DBException {
		int i = -1;
		PreparedStatement localPreparedStatement = null;
		String str = null;
		try {
			str = "delete from certarcforkmc";
			localPreparedStatement = paramConnection.prepareStatement(str);
			i = localPreparedStatement.executeUpdate();
			j = i;
		} catch (SQLException localSQLException1) {
			int j;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8079", "删除已过期加密证书信息表失败", localSQLException1);
		} finally {
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException2) {
				}
		}
	}

	public int deleteCertArcForKMC() throws DBException {
		int i = -1;
		Connection localConnection = null;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			i = deleteCertArcForKMC(localConnection);
			j = i;
		} catch (SQLException localSQLException1) {
			int j;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8079", "删除已过期加密证书信息表失败", localSQLException1);
		} finally {
			if (localConnection != null)
				try {
					localConnection.close();
				} catch (SQLException localSQLException2) {
				}
		}
	}

	protected int insertCertArcForKMC(Connection paramConnection, String paramString)
    throws DBException
  {
    PreparedStatement localPreparedStatement = null;
    int i = -1;
    try
    {
      int j;
      if (paramString == null)
      {
        j = i;
        jsr 119;
      }
      String str = "insert into certarcforkmc values(?,?,?)";
      localPreparedStatement = paramConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString);
      localPreparedStatement.setString(2, "tmp_signServer");
      localPreparedStatement.setString(3, "tmp_signClient");
      i = localPreparedStatement.executeUpdate();
      k = i;
    }
    catch (SQLException localSQLException1)
    {
      int k;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8601", "增加已过期加密证书信息表失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	protected int deleteCert(Connection paramConnection, String paramString)
    throws DBException
  {
    PreparedStatement localPreparedStatement = null;
    int i = -1;
    try
    {
      int j;
      if (paramString == null)
      {
        j = i;
        jsr 101;
      }
      String str = "delete from cert where certsn=?";
      localPreparedStatement = paramConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString);
      i = localPreparedStatement.executeUpdate();
      k = i;
    }
    catch (SQLException localSQLException1)
    {
      int k;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8603", "删除状态证书信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	protected int insertAdmin(Connection paramConnection, String paramString1, String paramString2)
    throws DBException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    int i = -1;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        j = i;
        jsr 183;
      }
      String str = "select role_id from admin where certsn=?";
      localPreparedStatement = paramConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString1);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        str = "insert into admin values (?,?,?,?)";
        localPreparedStatement = paramConnection.prepareStatement(str);
        localPreparedStatement.setString(1, paramString2);
        localPreparedStatement.setString(2, localResultSet.getString(1));
        localPreparedStatement.setString(3, "tmp_signServer");
        localPreparedStatement.setString(4, "tmp_signClient");
        i = localPreparedStatement.executeUpdate();
      }
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8604", "增加管理员证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	protected int insertTemplateAdmin(Connection paramConnection, String paramString1, String paramString2)
    throws DBException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    int i = -1;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        j = i;
        jsr 216;
      }
      String str = "select ctmlname,basedn,operation from ba_privilege where certsn=?";
      localPreparedStatement = paramConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString1);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        str = "insert into ba_privilege values (?,?,?,?,?,?)";
        localPreparedStatement = paramConnection.prepareStatement(str);
        localPreparedStatement.setString(1, paramString2);
        localPreparedStatement.setString(2, localResultSet.getString(1));
        localPreparedStatement.setString(3, localResultSet.getString(2));
        localPreparedStatement.setString(4, localResultSet.getString(3));
        localPreparedStatement.setString(5, "tmp_signServer");
        localPreparedStatement.setString(6, "tmp_signClient");
        i = localPreparedStatement.executeUpdate();
      }
      j = i;
    }
    catch (SQLException localSQLException1)
    {
      int j;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8605", "增加业务管理员证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	public long getCertCountForIsvaild() throws DBException {
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		long l1 = -1L;
		try {
			String str = "select count(certsn) from cert  where isvalid = 1";
			localConnection = DriverManager.getConnection("proxool.ida");
			localPreparedStatement = localConnection.prepareStatement(str);
			localResultSet = localPreparedStatement.executeQuery();
			localResultSet.next();
			l1 = localResultSet.getLong(1);
			l2 = l1;
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

	public int pendingTaskCertRevoke(CertPendingRevokeInfo paramCertPendingRevokeInfo)
    throws DBException
  {
    Connection localConnection = null;
    IDAException localIDAException1 = -1;
    if ((paramCertPendingRevokeInfo == null) || (paramCertPendingRevokeInfo.getCertSN() == null))
      return localIDAException1;
    try
    {
      localConnection = DriverManager.getConnection("proxool.ida");
      CertRevokeInfo localCertRevokeInfo = new CertRevokeInfo();
      localCertRevokeInfo.setCertSN(paramCertPendingRevokeInfo.getCertSN());
      localCertRevokeInfo.setReasonID(paramCertPendingRevokeInfo.getReasonID());
      localCertRevokeInfo.setCDPID(paramCertPendingRevokeInfo.getCDPID());
      localCertRevokeInfo.setApplicant(paramCertPendingRevokeInfo.getApplicant());
      String str1 = revokeCert(localConnection, localCertRevokeInfo);
      if (str1 != null)
        try
        {
          int i;
          if (updateCertPendingStatus(localConnection, paramCertPendingRevokeInfo.getCertSN()) == -1)
          {
            localConnection.rollback();
            i = localIDAException1;
            jsr 228;
          }
          CertInfo localCertInfo = new CertInfo();
          localCertInfo = getCertInfo(paramCertPendingRevokeInfo.getCertSN());
          String str2 = null;
          str2 = InternalConfig.getInstance().getAdminTemplateName();
          if (localCertInfo.getCtmlName().equals(str2))
          {
            deleteAdmin(localConnection, paramCertPendingRevokeInfo.getCertSN());
            deleteTemplateAdmin(localConnection, paramCertPendingRevokeInfo.getCertSN());
            TemplateAdmin.getInstance().deletePendingTemplateAdmin(paramCertPendingRevokeInfo.getCertSN());
            Privilege.getInstance().deletePendingAdmin(paramCertPendingRevokeInfo.getCertSN());
          }
          localIDAException1 = deletePendingtaskRecord(localConnection, paramCertPendingRevokeInfo.getTaskID());
        }
        catch (IDAException localIDAException2)
        {
          throw new DBException("8607", "注销待操作任务表对应的证书失败", localIDAException2);
        }
      if (localIDAException1 == -1)
      {
        localConnection.rollback();
      }
      else
      {
        localConnection.commit();
        localIDAException2 = localIDAException1;
        jsr 85;
      }
      localIDAException2 = localIDAException1;
    }
    catch (SQLException localSQLException1)
    {
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException2)
        {
        }
      throw new DBException("8607", "注销待操作任务表对应的证书失败", localSQLException1);
    }
    finally
    {
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int modifyCertPendingStatus(String paramString)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    int i = -1;
    try
    {
      int j;
      if (paramString == null)
      {
        j = i;
        jsr 107;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      String str = "update cert set iswaiting='1' where certsn=?";
      localPreparedStatement = localConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString);
      i = localPreparedStatement.executeUpdate();
      k = i;
    }
    catch (SQLException localSQLException1)
    {
      int k;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8608", "更改证书待处理状态失败", localSQLException1);
    }
    finally
    {
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int saveCertPendingTask(CertPendingRevokeInfo paramCertPendingRevokeInfo)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    SQLException localSQLException1 = -1;
    try
    {
      int i;
      if (paramCertPendingRevokeInfo == null)
      {
        i = localSQLException1;
        jsr 370;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      String str1 = "insert into pendingtask values(?,?,?,?,?,?,?,?,?,?,?)";
      localPreparedStatement = localConnection.prepareStatement(str1);
      String str2 = getRandom(40);
      try
      {
        localPreparedStatement.setString(1, str2);
        localPreparedStatement.setString(2, paramCertPendingRevokeInfo.getCertSN());
        localPreparedStatement.setString(3, paramCertPendingRevokeInfo.getSubject());
        localPreparedStatement.setLong(4, paramCertPendingRevokeInfo.getExectime());
        localPreparedStatement.setInt(5, paramCertPendingRevokeInfo.getReasonID());
        localPreparedStatement.setString(6, paramCertPendingRevokeInfo.getReasonDesc());
        localPreparedStatement.setString(7, paramCertPendingRevokeInfo.getOptType());
        localPreparedStatement.setLong(8, paramCertPendingRevokeInfo.getCDPID());
        localPreparedStatement.setString(9, paramCertPendingRevokeInfo.getApplicant());
        localPreparedStatement.setString(10, "tmp_signServer");
        localPreparedStatement.setString(11, "tmp_signClient");
        localSQLException1 = localPreparedStatement.executeUpdate();
      }
      catch (SQLException localSQLException3)
      {
        str2 = getRandom(40);
        localPreparedStatement.setString(1, str2);
        localPreparedStatement.setString(2, paramCertPendingRevokeInfo.getCertSN());
        localPreparedStatement.setString(3, paramCertPendingRevokeInfo.getSubject());
        localPreparedStatement.setLong(4, paramCertPendingRevokeInfo.getExectime());
        localPreparedStatement.setInt(5, paramCertPendingRevokeInfo.getReasonID());
        localPreparedStatement.setString(6, paramCertPendingRevokeInfo.getReasonDesc());
        localPreparedStatement.setString(7, paramCertPendingRevokeInfo.getOptType());
        localPreparedStatement.setLong(8, paramCertPendingRevokeInfo.getCDPID());
        localPreparedStatement.setString(9, paramCertPendingRevokeInfo.getApplicant());
        localPreparedStatement.setString(10, "tmp_signServer");
        localPreparedStatement.setString(11, "tmp_signClient");
        localSQLException1 = localPreparedStatement.executeUpdate();
      }
      localSQLException3 = localSQLException1;
    }
    catch (SQLException localSQLException2)
    {
      syslogger.info("数据库异常：" + localSQLException2.getMessage());
      throw new DBException("8609", "增加证书待处理表失败", localSQLException2);
    }
    finally
    {
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException4)
        {
        }
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException5)
        {
        }
    }
  }

	private int deletePendingtaskRecord(Connection paramConnection, String paramString)
    throws DBException
  {
    PreparedStatement localPreparedStatement = null;
    int i = -1;
    try
    {
      int j;
      if ((paramConnection == null) || (paramString == null))
      {
        j = i;
        jsr 101;
      }
      String str = "delete from pendingtask where taskid=?";
      localPreparedStatement = paramConnection.prepareStatement(str);
      localPreparedStatement.setString(1, paramString);
      i = localPreparedStatement.executeUpdate();
      k = i;
    }
    catch (SQLException localSQLException1)
    {
      int k;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8602", "删除非待操作状态证书信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
    }
  }

	public CertInfo getPendingCertInfo(String paramString1, String paramString2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    CertInfo localCertInfo1 = null;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        localObject1 = localCertInfo1;
        jsr 333;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      Object localObject1 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark from cert where subjectuppercase=? and ctmlname=? and isvalid=1 and iswaiting='0'";
      localPreparedStatement = localConnection.prepareStatement((String)localObject1);
      localPreparedStatement.setString(1, paramString1.toUpperCase());
      localPreparedStatement.setString(2, paramString2);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        localCertInfo1 = new CertInfo();
        localCertInfo1.setCertSN(localResultSet.getString(1));
        localCertInfo1.setSubject(localResultSet.getString(2));
        localCertInfo1.setNotBefore(localResultSet.getLong(3));
        localCertInfo1.setNotAfter(localResultSet.getLong(4));
        localCertInfo1.setValidity(localResultSet.getInt(5));
        localCertInfo1.setAuthCode(localResultSet.getString(6));
        localCertInfo1.setCdpid(localResultSet.getInt(7));
        localCertInfo1.setCtmlName(localResultSet.getString(8));
        localCertInfo1.setCertStatus(localResultSet.getString(9));
        localCertInfo1.setIsValid(localResultSet.getLong(10));
        localCertInfo1.setCreateTime(localResultSet.getLong(11));
        localCertInfo1.setApplicant(localResultSet.getString(12));
        localCertInfo1.setEmail(localResultSet.getString(13));
        localCertInfo1.setRemark(localResultSet.getString(14));
      }
      localCertInfo2 = localCertInfo1;
    }
    catch (SQLException localSQLException1)
    {
      CertInfo localCertInfo2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8013", "按主题获取证书信息失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public CertInfo getPendignHoldCertInfo(String paramString1, String paramString2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    CertInfo localCertInfo1 = null;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        localObject1 = localCertInfo1;
        jsr 344;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      Object localObject1 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark from cert where subjectuppercase=? and ctmlname=? and certstatus=? and iswaiting='0'";
      localPreparedStatement = localConnection.prepareStatement((String)localObject1);
      localPreparedStatement.setString(1, paramString1.toUpperCase());
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.setString(3, "Hold");
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        localCertInfo1 = new CertInfo();
        localCertInfo1.setCertSN(localResultSet.getString(1));
        localCertInfo1.setSubject(localResultSet.getString(2));
        localCertInfo1.setNotBefore(localResultSet.getLong(3));
        localCertInfo1.setNotAfter(localResultSet.getLong(4));
        localCertInfo1.setValidity(localResultSet.getInt(5));
        localCertInfo1.setAuthCode(localResultSet.getString(6));
        localCertInfo1.setCdpid(localResultSet.getInt(7));
        localCertInfo1.setCtmlName(localResultSet.getString(8));
        localCertInfo1.setCertStatus(localResultSet.getString(9));
        localCertInfo1.setIsValid(localResultSet.getLong(10));
        localCertInfo1.setCreateTime(localResultSet.getLong(11));
        localCertInfo1.setApplicant(localResultSet.getString(12));
        localCertInfo1.setEmail(localResultSet.getString(13));
        localCertInfo1.setRemark(localResultSet.getString(14));
      }
      localCertInfo2 = localCertInfo1;
    }
    catch (SQLException localSQLException1)
    {
      CertInfo localCertInfo2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8060", "获取冻结证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int updateCertPending(CertRevokeInfo paramCertRevokeInfo, CertInfo paramCertInfo)
    throws DBException
  {
    Connection localConnection = null;
    int i = -1;
    try
    {
      if ((paramCertRevokeInfo == null) || (paramCertInfo == null))
      {
        j = i;
        jsr 205;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      localConnection.setAutoCommit(false);
      i = saveCertReq(localConnection, paramCertInfo);
      if (i == 1)
      {
        insertAdmin(localConnection, paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
        insertTemplateAdmin(localConnection, paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
        localConnection.commit();
      }
      else
      {
        localConnection.rollback();
      }
      j = i;
    }
    catch (DBException localDBException)
    {
      int j;
      syslogger.info("数据库异常：" + localDBException.getMessage());
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException2)
        {
        }
      throw localDBException;
    }
    catch (SQLException localSQLException1)
    {
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException3)
        {
        }
      throw new DBException("8034", "更新证书失败", localSQLException1);
    }
    finally
    {
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException4)
        {
        }
    }
  }

	public int updAndDownCertPending(CertRevokeInfo paramCertRevokeInfo, CertInfo paramCertInfo)
    throws DBException
  {
    Connection localConnection = null;
    int i = -1;
    try
    {
      if ((paramCertRevokeInfo == null) || (paramCertInfo == null))
      {
        j = i;
        jsr 213;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      localConnection.setAutoCommit(false);
      paramCertInfo.setOldSN(paramCertRevokeInfo.getCertSN());
      i = reqAndDownCert(localConnection, paramCertInfo);
      if (i == 1)
      {
        insertAdmin(localConnection, paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
        insertTemplateAdmin(localConnection, paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
        localConnection.commit();
      }
      else
      {
        localConnection.rollback();
      }
      j = i;
    }
    catch (DBException localDBException)
    {
      int j;
      syslogger.info("数据库异常：" + localDBException.getMessage());
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException2)
        {
        }
      throw localDBException;
    }
    catch (SQLException localSQLException1)
    {
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      if (localConnection != null)
        try
        {
          localConnection.rollback();
        }
        catch (SQLException localSQLException3)
        {
        }
      throw new DBException("8036", "更新并下载证书失败", localSQLException1);
    }
    finally
    {
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException4)
        {
        }
    }
  }

	public Vector pendingTaskVector()
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    Vector localVector = new Vector();
    try
    {
      localConnection = DriverManager.getConnection("proxool.ida");
      String str = "select taskid,certsn,subject,exectime,reasonid,reasondesc,opttype,cdpid,applicant,sign_server,sign_client from pendingtask";
      localPreparedStatement = localConnection.prepareStatement(str);
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
        CertPendingRevokeInfo localCertPendingRevokeInfo = new CertPendingRevokeInfo();
        localCertPendingRevokeInfo.setTaskID(localResultSet.getString(1));
        localCertPendingRevokeInfo.setCertSN(localResultSet.getString(2));
        localCertPendingRevokeInfo.setSubject(localResultSet.getString(3));
        localCertPendingRevokeInfo.setExectime(localResultSet.getLong(4));
        localCertPendingRevokeInfo.setReasonID(localResultSet.getInt(5));
        localCertPendingRevokeInfo.setReasonDesc(localResultSet.getString(6));
        localCertPendingRevokeInfo.setOptType(localResultSet.getString(7));
        localCertPendingRevokeInfo.setCDPID(localResultSet.getLong(8));
        localCertPendingRevokeInfo.setApplicant(localResultSet.getString(9));
        localCertPendingRevokeInfo.setSIGN_SERVER(localResultSet.getString(10));
        localCertPendingRevokeInfo.setSIGN_CLIENT(localResultSet.getString(11));
        localVector.add(localCertPendingRevokeInfo);
      }
    }
    catch (SQLException localSQLException1)
    {
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      try
      {
        throw new DBException("8610", "获取证书待处理表失败", localSQLException1);
      }
      catch (DBException localDBException)
      {
        jsr 14;
      }
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
    return localVector;
  }

	private int updateCertPendingStatus(Connection paramConnection,
			String paramString) throws DBException {
		PreparedStatement localPreparedStatement = null;
		int i = -1;
		if ((paramConnection == null) || (paramString == null))
			return i;
		try {
			String str = "update cert set iswaiting = '0' where certsn=?";
			localPreparedStatement = paramConnection.prepareStatement(str);
			localPreparedStatement.setString(1, paramString);
			i = localPreparedStatement.executeUpdate();
			j = i;
		} catch (SQLException localSQLException1) {
			int j;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8608", "更改证书待处理状态失败", localSQLException1);
		} finally {
			if (paramConnection != null)
				try {
					paramConnection.close();
				} catch (SQLException localSQLException2) {
				}
			if (localPreparedStatement != null)
				try {
					localPreparedStatement.close();
				} catch (SQLException localSQLException3) {
				}
		}
	}

	public CertInfo getPendingUNRVKCertInfo(String paramString1, String paramString2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    CertInfo localCertInfo1 = null;
    try
    {
      if ((paramString1 == null) || (paramString2 == null))
      {
        localObject1 = localCertInfo1;
        jsr 366;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      Object localObject1 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark from cert where subjectuppercase=? and ctmlname=? and iswaiting = '0' and certstatus in(?,?,?)";
      localPreparedStatement = localConnection.prepareStatement((String)localObject1);
      localPreparedStatement.setString(1, paramString1.toUpperCase());
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.setString(3, "Hold");
      localPreparedStatement.setString(4, "Undown");
      localPreparedStatement.setString(5, "Use");
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        localCertInfo1 = new CertInfo();
        localCertInfo1.setCertSN(localResultSet.getString(1));
        localCertInfo1.setSubject(localResultSet.getString(2));
        localCertInfo1.setNotBefore(localResultSet.getLong(3));
        localCertInfo1.setNotAfter(localResultSet.getLong(4));
        localCertInfo1.setValidity(localResultSet.getInt(5));
        localCertInfo1.setAuthCode(localResultSet.getString(6));
        localCertInfo1.setCdpid(localResultSet.getInt(7));
        localCertInfo1.setCtmlName(localResultSet.getString(8));
        localCertInfo1.setCertStatus(localResultSet.getString(9));
        localCertInfo1.setIsValid(localResultSet.getLong(10));
        localCertInfo1.setCreateTime(localResultSet.getLong(11));
        localCertInfo1.setApplicant(localResultSet.getString(12));
        localCertInfo1.setEmail(localResultSet.getString(13));
        localCertInfo1.setRemark(localResultSet.getString(14));
      }
      localCertInfo2 = localCertInfo1;
    }
    catch (SQLException localSQLException1)
    {
      CertInfo localCertInfo2;
      syslogger.info("数据库异常：" + localSQLException1.getMessage());
      throw new DBException("8061", "获取未注销证书失败", localSQLException1);
    }
    finally
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
        }
      if (localConnection != null)
        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
    }
  }

	public int deleteRevokedCertByNotafter() throws DBException {
		int i = -1;
		PreparedStatement localPreparedStatement = null;
		String str = null;
		Connection localConnection = null;
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			str = "delete from revokedcert where certnotafter<?";
			localPreparedStatement = localConnection.prepareStatement(str);
			localPreparedStatement.setLong(1, getTime());
			i = localPreparedStatement.executeUpdate();
			j = i;
		} catch (SQLException localSQLException1) {
			int j;
			syslogger.info("数据库异常：" + localSQLException1.getMessage());
			throw new DBException("8611", "删除已过期注销证书表失败", localSQLException1);
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
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.db.DBManager JD-Core Version: 0.6.0
 */