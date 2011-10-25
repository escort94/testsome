package cn.com.jit.ida.ca.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MSSQL extends DBManager
{
  MSSQL()
    throws DBException
  {
  }

  public String getDBVersion()
  {
    Connection localConnection = null;
    Statement localStatement = null;
    ResultSet localResultSet = null;
    String str1 = null;
    String str2 = "";
    try
    {
      localConnection = DriverManager.getConnection("proxool.ida");
      str1 = "select @@version";
      localStatement = localConnection.createStatement();
      localResultSet = localStatement.executeQuery(str1);
      if (localResultSet.next())
        str2 = localResultSet.getString(1);
    }
    catch (SQLException localSQLException1)
    {
      str2 = "返回数据库版本失败:数据库访问异常";
    }
    finally
    {
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
    return str2;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.db.MSSQL
 * JD-Core Version:    0.6.0
 */