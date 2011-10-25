package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.globalconfig.ParseXML;

public class InitDBConfig
{
  ParseXML config;

  public InitDBConfig(ParseXML paramParseXML)
  {
    this.config = paramParseXML;
  }

  public String getDirverURL()
  {
    String[] arrayOfString = { "Config", "DBConfig", "driver-url" };
    return this.config.getString(this.config.ComposeStr(arrayOfString));
  }

  public String getDriverClass()
  {
    String[] arrayOfString = { "Config", "DBConfig", "driver-class" };
    return this.config.getString(this.config.ComposeStr(arrayOfString));
  }

  public String getAdmin()
  {
    String[] arrayOfString = { "Config", "DBConfig", "Admin" };
    return this.config.getString(this.config.ComposeStr(arrayOfString));
  }

  public String getAdminPWD()
  {
    String[] arrayOfString = { "Config", "DBConfig", "AdminPWD" };
    return this.config.getString(this.config.ComposeStr(arrayOfString));
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.InitDBConfig
 * JD-Core Version:    0.6.0
 */