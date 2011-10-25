package cn.com.jit.ida.ca;

import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.license.IDAFormula;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Version
{
  public static final String PROJECT_NAME = "SRQ05-CA";
  public static final String VERSION = "5_0_2_6";
  public static final String Description = "-lastmodification_";
  public static final String BUILD_TIME = "200607311600";
  public static final String FULL = "SRQ05-CA5_0_2_6-lastmodification_200607311600";

  public static String asString()
  {
    return "SRQ05-CA5_0_2_6-lastmodification_200607311600";
  }

  public static String getAllVersion()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(asString() + "\n");
    localStringBuffer.append("    " + cn.com.jit.ida.util.pki.Version.getVersion() + "\n");
    localStringBuffer.append("    " + IDAFormula.getVersion() + "\n");
    try
    {
      localStringBuffer.append("    " + DBManager.getInstance().getDBVersion() + "\n");
    }
    catch (DBException localDBException)
    {
      localStringBuffer.append("        无法获取数据库版本\n");
    }
    return localStringBuffer.toString();
  }

  public static void main(String[] paramArrayOfString)
  {
    System.out.println("SRQ05-CA5_0_2_6-lastmodification_200607311600");
  }

  public static String getFormulaInfo1()
  {
    try
    {
      IDAFormula localIDAFormula = IDAFormula.getInstance("./license.lce");
      String str = localIDAFormula.getSystemValue3();
      if (str.equalsIgnoreCase("null"))
        return new String("无限制".getBytes(), "GBK");
      long l = Long.parseLong(str) * 1000L;
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
      Date localDate = new Date(l);
      return localSimpleDateFormat.format(localDate);
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public static String getFormulaInfo2()
  {
    try
    {
      IDAFormula localIDAFormula = IDAFormula.getInstance("./license.lce");
      String str = localIDAFormula.getSystemValue5();
      if (str.equalsIgnoreCase("null"))
        return new String("无限制".getBytes(), "GBK");
      return str;
    }
    catch (Exception localException)
    {
    }
    return null;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.Version
 * JD-Core Version:    0.6.0
 */