package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.globalconfig.ConfigFromXML;

public class CSPConfig
{
  private static CSPConfig instance;
  private ConfigFromXML config;
  private String CSPAdminShowType;
  private String CSPAdminSpecify;
  private String CSPUserShowType;
  private String CSPUserSpecify;

  private CSPConfig()
    throws IDAException
  {
    init();
  }

  public static CSPConfig getInstance()
    throws IDAException
  {
    if (instance == null)
      instance = new CSPConfig();
    return instance;
  }

  public String getCSPUserSpecify()
  {
    return this.CSPUserSpecify;
  }

  public String getCSPUserShowType()
  {
    return this.CSPUserShowType;
  }

  public String getCSPAdminSpecify()
  {
    return this.CSPAdminSpecify;
  }

  public String getCSPAdminShowType()
  {
    return this.CSPAdminShowType;
  }

  public void init()
    throws IDAException
  {
    this.config = new ConfigFromXML("CSPConfig", "./config/CAConfig.xml");
    this.CSPAdminShowType = this.config.getString(CAConfigConstant.CSPAdminShowType);
    this.CSPAdminSpecify = this.config.getString(CAConfigConstant.CSPAdminSpecify);
    this.CSPUserShowType = this.config.getString(CAConfigConstant.CSPUserShowType);
    this.CSPUserSpecify = this.config.getString(CAConfigConstant.CSPUserSpecify);
  }

  public void setCSPAdminShowType(String paramString)
    throws IDAException
  {
    this.CSPAdminShowType = paramString;
    this.config.setString(CAConfigConstant.CSPAdminShowType, paramString);
  }

  public void setCSPAdminSpecify(String paramString)
    throws IDAException
  {
    this.CSPAdminSpecify = paramString;
    this.config.setString(CAConfigConstant.CSPAdminSpecify, paramString);
  }

  public void setCSPUserShowType(String paramString)
    throws IDAException
  {
    this.CSPUserShowType = paramString;
    this.config.setString(CAConfigConstant.CSPUserShowType, paramString);
  }

  public void setCSPUserSpecify(String paramString)
    throws IDAException
  {
    this.CSPUserSpecify = paramString;
    this.config.setString(CAConfigConstant.CSPUserSpecify, paramString);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.CSPConfig
 * JD-Core Version:    0.6.0
 */