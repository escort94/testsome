package cn.com.jit.ida.globalconfig;

import cn.com.jit.ida.IDAException;

public abstract class Config
{
  String type;

  public Config(String paramString)
  {
    this.type = paramString;
  }

  public abstract String getString(String paramString)
    throws IDAException;

  public abstract void setString(String paramString1, String paramString2)
    throws IDAException;

  public int getNumber(String paramString)
    throws IDAException
  {
    String str = getString(paramString);
    int i = 0;
    try
    {
      i = Integer.parseInt(str);
    }
    catch (Exception localException)
    {
//    	System.out.println("<" + paramString + ">值配置非法");
      throw new IDAException(ConfigException.DATA_TYPE_ERROR, "<" + paramString + ">值配置非法");
    }
    if (i < 0)
      throw new IDAException(ConfigException.DATA_TYPE_ERROR, "<" + paramString + ">值配置不正确(要求>=0)");
    return i;
  }

  public void setNumber(String paramString, int paramInt)
    throws IDAException
  {
    String str = Integer.toString(paramInt);
    setString(paramString, str);
  }

  public long getLong(String paramString)
    throws IDAException
  {
    String str = getString(paramString);
    long l = 0L;
    try
    {
      l = Long.parseLong(str);
    }
    catch (Exception localException)
    {
      throw new IDAException(ConfigException.DATA_TYPE_ERROR, "<" + paramString + ">值配置非法");
    }
    if (l < 0L)
      throw new IDAException(ConfigException.DATA_TYPE_ERROR, "<" + paramString + ">值配置不正确(要求>=0)");
    return l;
  }

  public void setLong(String paramString, long paramLong)
    throws IDAException
  {
    String str = Long.toString(paramLong);
    setString(paramString, str);
  }

  public boolean getBoolean(String paramString)
    throws IDAException
  {
    String str = getString(paramString);
    return str.equalsIgnoreCase("true");
  }

  public void setBoolean(String paramString, boolean paramBoolean)
    throws IDAException
  {
    String str;
    if (paramBoolean)
      str = "TRUE";
    else
      str = "FALSE";
    setString(paramString, str);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.globalconfig.Config
 * JD-Core Version:    0.6.0
 */