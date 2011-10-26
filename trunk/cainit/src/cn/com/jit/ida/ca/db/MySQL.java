package cn.com.jit.ida.ca.db;

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

import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertSelfExt;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertStandardExt;
import cn.com.jit.ida.log.Operation;

public class MySQL extends DBManager {
	MySQL() throws DBException {
	}

	public String getDBVersion() {
		Connection localConnection = null;
		Statement localStatement = null;
		ResultSet localResultSet = null;
		String str1 = null;
		String str2 = "";
		try {
			localConnection = DriverManager.getConnection("proxool.ida");
			str1 = "select version()";
			localStatement = localConnection.createStatement();
			localResultSet = localStatement.executeQuery(str1);
			if (localResultSet.next())
				str2 = "MySQL数据库版本是:" + localResultSet.getString(1);
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
    try
    {
      if (paramProperties == null)
      {
        return localVector1;
      }
      String str2 = "select count(ctmlname) from cert";
      int k = paramProperties.size();
      Object localObject1 = paramProperties.getProperty("ctmlName");
      if ((localObject1 != null) && (((String)localObject1).indexOf("|") >= 0))
      {
    	  StringTokenizer localObject2 = new StringTokenizer((String)localObject1, "|");
        k += ((StringTokenizer)localObject2).countTokens();
      }
      Object localObject2 = paramProperties.getProperty("certStatus");
      StringTokenizer localStringTokenizer;
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
              arrayOfString[i][0] = ("%" + setEscape2(arrayOfString[i][0]) + "%");
              str3 = str3 + "subjectuppercase" + " like ? and ";
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
      str2 = str2 + str3;
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase());
      for (int j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      localResultSet = localPreparedStatement.executeQuery();
      localResultSet.next();
      long l = localResultSet.getLong(1);
      localVector1.add(Long.toString(l));
      if (paramInt1 < 1)
        paramInt1 = 1;
      if (orderby)
        str3 = str3 + " order by createtime desc";
      str2 = "select cert.certsn,subject,notbefore,notafter,validity,authcode,cert.cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark,reason,reasondesc,oldsn from cert left outer join revokedcert on (cert.certsn=revokedcert.certsn) ";
      if (paramInt2 < 1)
        str2 = str2 + str3;
      else
        str2 = str2 + str3 + " limit ?,?";
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase());
      int j = 0;
      for (j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      if (paramInt2 > 0)
      {
        localPreparedStatement.setInt(j + 1, paramInt1 - 1);
        localPreparedStatement.setInt(j + 2, paramInt2);
      }
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
        localVector2.add(getCertinfoCondition(localResultSet));
      arrayOfCertInfo = new CertInfo[localVector2.size()];
      localVector2.toArray(arrayOfCertInfo);
      localVector1.add(arrayOfCertInfo);
      return localVector1;
    }
    catch (SQLException localSQLException1)
    {
      Vector localVector3;
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
    try
    {
      if (paramProperties == null)
      {
        return localVector1;
      }
      String str2 = "select count(ctmlname) from cert";
      int k = paramProperties.size();
      Object localObject1 = paramProperties.getProperty("ctmlName");
      if ((localObject1 != null) && (((String)localObject1).indexOf("|") >= 0))
      {
    	  StringTokenizer localObject2 = new StringTokenizer((String)localObject1, "|");
        k += ((StringTokenizer)localObject2).countTokens();
      }
      Object localObject2 = paramProperties.getProperty("certStatus");
      if ((localObject2 != null) && (((String)localObject2).indexOf("|") >= 0))
      {
    	  StringTokenizer localObject3 = new StringTokenizer((String)localObject2, "|");
        k += ((StringTokenizer)localObject3).countTokens();
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
            	StringTokenizer  localObject3 = new StringTokenizer(arrayOfString[i][0], "|");
              k = 0;
              for (str3 = str3 + str1 + " in ("; ((StringTokenizer)localObject3).hasMoreTokens(); str3 = str3 + "?,")
              {
                arrayOfString[i][0] = ((StringTokenizer)localObject3).nextToken();
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
            	StringTokenizer localObject3 = new StringTokenizer(arrayOfString[i][0], "|");
              for (str3 = str3 + str1 + " in ("; ((StringTokenizer)localObject3).hasMoreTokens(); str3 = str3 + "?,")
              {
                arrayOfString[i][0] = ((StringTokenizer)localObject3).nextToken();
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
              arrayOfString[i][0] = ("%" + setEscape2(arrayOfString[i][0]) + "%");
              str3 = str3 + "subjectuppercase" + " like ? and ";
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
      Object localObject3 = "";
      if (str3.indexOf("where") >= 0)
        localObject3 = " and iswaiting = '0'";
      else
        localObject3 = " where iswaiting = '0'";
      str3 = str3 + (String)localObject3;
      localConnection = DriverManager.getConnection("proxool.ida");
      str2 = str2 + str3;
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase());
      for (int j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      localResultSet = localPreparedStatement.executeQuery();
      localResultSet.next();
      long l = localResultSet.getLong(1);
      localVector1.add(Long.toString(l));
      if (paramInt1 < 1)
        paramInt1 = 1;
      if (orderby)
        str3 = str3 + " order by createtime desc";
      str2 = "select cert.certsn,subject,notbefore,notafter,validity,authcode,cert.cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark,reason,reasondesc,oldsn from cert left outer join revokedcert on (cert.certsn=revokedcert.certsn) ";
      if (paramInt2 < 1)
        str2 = str2 + str3;
      else
        str2 = str2 + str3 + " limit ?,?";
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase());
      int j = 0;
      for (j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      if (paramInt2 > 0)
      {
        localPreparedStatement.setInt(j + 1, paramInt1 - 1);
        localPreparedStatement.setInt(j + 2, paramInt2);
      }
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
        localVector2.add(getCertinfoCondition(localResultSet));
      arrayOfCertInfo = new CertInfo[localVector2.size()];
      localVector2.toArray(arrayOfCertInfo);
      localVector1.add(arrayOfCertInfo);
      return localVector1;
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
    try
    {
      Vector localVector3;
      if (paramProperties == null)
      {
        return localVector1;
      }
      String str2 = "select count(operatorsubjectuppercase) from operationlog";
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
              arrayOfString[i][0] = ("%" + setEscape2(arrayOfString[i][0]) + "%");
              str3 = str3 + "objectsubjectuppercase" + " like ? and ";
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
      str2 = str2 + str3;
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase());
      for (int j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      localResultSet = localPreparedStatement.executeQuery();
      localResultSet.next();
      long l = localResultSet.getLong(1);
      localVector1.add(Long.toString(l));
      if (paramInt1 < 1)
        paramInt1 = 1;
      if (orderby)
        str3 = str3 + " order by opttime desc";
      str2 = "select operatorsn,operatorsubject,objectcertsn,objectsubject,objectctmlname,opttype,opttime,result from operationlog";
      if (paramInt2 < 1)
        str2 = str2 + str3;
      else
        str2 = str2 + str3 + " limit ?,?";
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase());
      int j = 0;
      for (j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      if (paramInt2 > 0)
      {
        localPreparedStatement.setInt(j + 1, paramInt1 - 1);
        localPreparedStatement.setInt(j + 2, paramInt2);
      }
      localResultSet = localPreparedStatement.executeQuery();
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
      arrayOfOperation = new Operation[localVector2.size()];
      localVector2.toArray(arrayOfOperation);
      localVector1.add(arrayOfOperation);
      return localVector1;
    }
    catch (SQLException localSQLException1)
    {
      Vector localVector4;
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
    	  return localVector1;
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
              str3 = str3 + "subjectuppercase" + " like ? and ";
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
      return localVector1;
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

	public CertInfo[] getCertsToPublish(String paramString, int paramInt1, int paramInt2)
    throws DBException
  {
    Connection localConnection = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    CertInfo[] arrayOfCertInfo1 = null;
    Vector localVector = new Vector();
    String str1 = "";
    String str2 = "";
    try
    {
      if (paramString == null)
      {
    	  return arrayOfCertInfo1;
      }
      localConnection = DriverManager.getConnection("proxool.ida");
      if (paramInt1 < 1)
        paramInt1 = 1;
      if (orderby)
        str2 = " order by createtime desc";
      str1 = "select certsn,subject,certstatus,certentity from cert  where ctmlname=?  and (certstatus = 'Use'   or certstatus = 'Revoke'   or certstatus = 'Hold')";
      if (paramInt2 < 1)
        str1 = str1 + str2;
      else
        str1 = str1 + str2 + " limit ?,?";
      localPreparedStatement = localConnection.prepareStatement(str1);
      localPreparedStatement.setString(1, paramString);
      if (paramInt2 > 0)
      {
        localPreparedStatement.setInt(2, paramInt1 - 1);
        localPreparedStatement.setInt(3, paramInt2);
      }
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
        localVector.add(getCertinfoForCertsToPublish(localResultSet));
      arrayOfCertInfo1 = new CertInfo[localVector.size()];
      localVector.toArray(arrayOfCertInfo1);
      return arrayOfCertInfo1;
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
    try
    {
      if (paramProperties == null)
      {
    	  return localVector1;
      }
      String str2 = "select count(ctmlname) from certarc";
      int k = paramProperties.size();
      Object localObject1 = paramProperties.getProperty("ctmlName");
      if ((localObject1 != null) && (((String)localObject1).indexOf("|") >= 0))
      {
    	  StringTokenizer localObject2 = new StringTokenizer((String)localObject1, "|");
        k += ((StringTokenizer)localObject2).countTokens();
      }
      Object localObject2 = paramProperties.getProperty("certStatus");
      StringTokenizer localStringTokenizer;
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
              arrayOfString[i][0] = ("%" + setEscape2(arrayOfString[i][0]) + "%");
              str3 = str3 + "subjectuppercase" + " like ? and ";
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
      str2 = str2 + str3;
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase());
      for (int j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      localResultSet = localPreparedStatement.executeQuery();
      localResultSet.next();
      long l = localResultSet.getLong(1);
      localVector1.add(Long.toString(l));
      if (paramInt1 < 1)
        paramInt1 = 1;
      if (orderby)
        str3 = str3 + " order by createtime desc";
      str2 = "select certsn,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicant,email,remark from certarc ";
      if (paramInt2 < 1)
        str2 = str2 + str3;
      else
        str2 = str2 + str3 + " limit ?,?";
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase());
      int j = 0;
      for (j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      if (paramInt2 > 0)
      {
        localPreparedStatement.setInt(j + 1, paramInt1 - 1);
        localPreparedStatement.setInt(j + 2, paramInt2);
      }
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
        localVector2.add(getCertinfoArcCondition(localResultSet));
      arrayOfCertInfo = new CertInfo[localVector2.size()];
      localVector2.toArray(arrayOfCertInfo);
      localVector1.add(arrayOfCertInfo);
      return localVector1;
    }
    catch (SQLException localSQLException1)
    {
      Vector localVector3;
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
    try
    {
      Vector localVector3;
      if (paramProperties == null)
      {
    	  return localVector1;
      }
      String str2 = "select count(operatorsubjectuppercase) from operationlogarc";
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
              arrayOfString[i][0] = ("%" + setEscape2(arrayOfString[i][0]) + "%");
              str3 = str3 + "objectsubjectuppercase" + " like ? and ";
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
      str2 = str2 + str3;
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase());
      for (int j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      localResultSet = localPreparedStatement.executeQuery();
      localResultSet.next();
      long l = localResultSet.getLong(1);
      localVector1.add(Long.toString(l));
      if (paramInt1 < 1)
        paramInt1 = 1;
      if (orderby)
        str3 = str3 + " order by opttime desc";
      str2 = "select operatorsn,operatorsubject,objectcertsn,objectsubject,objectctmlname,opttype,opttime,result from operationlogarc";
      if (paramInt2 < 1)
        str2 = str2 + str3;
      else
        str2 = str2 + str3 + " limit ?,?";
      localPreparedStatement = localConnection.prepareStatement(str2.toLowerCase());
      int j = 0;
      for (j = 0; j < i; j++)
        if (arrayOfString[j][1].equals("L"))
          localPreparedStatement.setLong(j + 1, Long.parseLong(arrayOfString[j][0]));
        else
          localPreparedStatement.setString(j + 1, arrayOfString[j][0]);
      if (paramInt2 > 0)
      {
        localPreparedStatement.setInt(j + 1, paramInt1 - 1);
        localPreparedStatement.setInt(j + 2, paramInt2);
      }
      localResultSet = localPreparedStatement.executeQuery();
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
      arrayOfOperation = new Operation[localVector2.size()];
      localVector2.toArray(arrayOfOperation);
      localVector1.add(arrayOfOperation);
      return localVector1;
    }
    catch (SQLException localSQLException1)
    {
      Vector localVector4;
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

	protected ArrayList getOperationLog(Connection paramConnection, Operation paramOperation, int paramInt, boolean paramBoolean)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    Object localObject1 = null;
    ArrayList localArrayList1 = new ArrayList();
    String str = "";
    ArrayList localArrayList2 = null;
    try
    {
      if ((paramConnection == null) || (paramOperation == null) || (paramInt < 0))
      {
    	  return localArrayList1;
      }
      Object localObject2 = getOperationlogCondition(paramOperation, paramBoolean);
      if (localObject2 == null)
      {
    	  return localArrayList1;
      }
      str = ((Vector)localObject2).get(0).toString();
      localArrayList2 = (ArrayList)((Vector)localObject2).get(1);
      localPreparedStatement = paramConnection.prepareStatement("select id,operatorsn,operatorsubject,objectcertsn,objectsubject,objectctmlname,opttype,opttime,result,sign_server,sign_client from operationlog" + str, 1004, 1008);
      localPreparedStatement.setMaxRows(paramInt);
      localPreparedStatement.setFetchSize(paramInt);
      Object localObject3 = null;
      for (int i = 0; i < localArrayList2.size(); i++)
      {
        localObject3 = (ValuePair)localArrayList2.get(i);
        if (((ValuePair)localObject3).getType().equals("I"))
          localPreparedStatement.setInt(i + 1, Integer.parseInt(((ValuePair)localObject3).getValue()));
        else if (((ValuePair)localObject3).getType().equals("L"))
          localPreparedStatement.setLong(i + 1, Long.parseLong(((ValuePair)localObject3).getValue()));
        else
          localPreparedStatement.setString(i + 1, ((ValuePair)localObject3).getValue());
      }
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
        localArrayList1.add(getOperationlog(localResultSet));
        localResultSet.deleteRow();
        localResultSet.previous();
      }
      return localArrayList1;
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
    	  return localArrayList1;
      }
      Object localObject1 = getCertsCondition(paramString);
      if (localObject1 == null)
      {
    	  return localArrayList1;
      }
      str1 = ((Vector)localObject1).get(0).toString();
      localArrayList2 = (ArrayList)((Vector)localObject1).get(1);
      localPreparedStatement = paramConnection.prepareStatement("select certsn,subjectuppercase,subject,notbefore,notafter,validity,authcode,cdpid,ctmlname,certstatus,isvalid,createtime,applicantuppercase,applicant,certentity,email,remark,authcode_updatetime,sign_server,sign_client,isretainkey,iswaiting,oldsn from cert" + str1, 1004, 1008);
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
        localResultSet.previous();
      }
      return localArrayList1;
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
    	  return localArrayList;
      }
      Object localObject1 = "select certsn,oid,selfext_name,value,sign_server,sign_client from cert_selfext where certsn=?";
      localPreparedStatement = paramConnection.prepareStatement((String)localObject1, 1004, 1008);
      localPreparedStatement.setString(1, paramString);
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
    	  CertSelfExt  localObject2 = new CertSelfExt();
        ((CertSelfExt)localObject2).setCertSn(localResultSet.getString(1));
        ((CertSelfExt)localObject2).setOid(localResultSet.getString(2));
        ((CertSelfExt)localObject2).setName(localResultSet.getString(3));
        ((CertSelfExt)localObject2).setValue(localResultSet.getString(4));
        ((CertSelfExt)localObject2).setSignServer(localResultSet.getString(5));
        ((CertSelfExt)localObject2).setSignClient(localResultSet.getString(6));
        localArrayList.add(localObject2);
        localResultSet.deleteRow();
        localResultSet.previous();
      }
      return localArrayList;
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
    	  return localArrayList;
      }
      Object localObject1 = "select certsn,ext_oid,ext_name,child_name,othername_oid,value,sign_server,sign_client from cert_standard_ext where certsn=?";
      localPreparedStatement = paramConnection.prepareStatement((String)localObject1, 1004, 1008);
      localPreparedStatement.setString(1, paramString);
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
    	  CertStandardExt localObject2 = new CertStandardExt();
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
        localResultSet.previous();
      }
      return localArrayList;
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
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.db.MySQL JD-Core Version: 0.6.0
 */