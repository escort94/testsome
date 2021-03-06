package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.ca.config.DBConfig;
import cn.com.jit.ida.ca.db.DBException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLInit extends DBInit
{
  MySQLInit()
    throws DBException
  {
  }

  public void createDB(String paramString1, String paramString2)
    throws DBException
  {
    Connection localConnection = null;
    Statement localStatement = null;
    String str1 = "";
    String str2 = "";
    FileReader localFileReader = null;
    BufferedReader localBufferedReader = null;
    try
    {
      String str3 = "./config/db/mysql/tables.sql";
      File localFile = new File(str3);
      try
      {
        localFileReader = new FileReader(localFile);
        localBufferedReader = new BufferedReader(localFileReader);
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        throw new DBException("8086", "下列文件不存在：" + str3);
      }
      localConnection = DriverManager.getConnection(dbConfig.getURL(), dbConfig.getUser(), dbConfig.getPassword());
      localStatement = localConnection.createStatement();
      while ((str2 = localBufferedReader.readLine()) != null)
      {
        str2 = str2.trim();
        if ((str2.equals("")) || (str2.substring(0, 1).equals("#")))
          continue;
        if (str2.endsWith(";"))
        {
          str1 = str1 + " " + str2;
          localStatement.execute(str1);
          str1 = "";
          continue;
        }
        str1 = str1 + " " + str2;
      }
      initPrivilege();
      initRole();
    }
    catch (IOException localIOException1)
    {
      throw new DBException("8081", "建库脚本文件读取失败", localIOException1);
    }
    catch (SQLException localSQLException1)
    {
      System.out.println(str1);
      throw new DBException("8081", "数据库异常：" + localSQLException1.getMessage(), localSQLException1);
    }
    finally
    {
      if (localFileReader != null)
        try
        {
          localFileReader.close();
        }
        catch (IOException localIOException2)
        {
        }
      if (localBufferedReader != null)
        try
        {
          localBufferedReader.close();
        }
        catch (IOException localIOException3)
        {
        }
      if (localStatement != null)
        try
        {
          localStatement.close();
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
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.MySQLInit
 * JD-Core Version:    0.6.0
 */