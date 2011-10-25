package cn.com.jit.ida.globalconfig;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.CAConfigConstant;

public class ConfigFromXML extends Config
{
  ParseXML parseXML;
  String fileName = null;

  public ConfigFromXML(String paramString1, String paramString2)
    throws IDAException
  {
    super(paramString1);
    this.fileName = paramString2;
    this.parseXML = new ParseXML(paramString2);
  }

  public String getString(String paramString)
  {
    return this.parseXML.getString(paramString);
  }

  public void setString(String paramString1, String paramString2)
    throws ConfigException
  {
    this.parseXML.setString(paramString1, paramString2);
    if ((paramString1.equalsIgnoreCase("SigningKeyStorePWD")) && (CAConfigConstant.SaveSignPwd.equals("1")) && ("./config/CAConfig.xml".equals(this.fileName)))
    {
      this.parseXML.setString("SignPWDIsConfirmed", "true");
      if (CAConfigConstant.SignPwd != null)
        this.parseXML.setString(paramString1, CAConfigConstant.SignPwd);
    }
    this.parseXML.SavaToFile(this.fileName);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.globalconfig.ConfigFromXML
 * JD-Core Version:    0.6.0
 */