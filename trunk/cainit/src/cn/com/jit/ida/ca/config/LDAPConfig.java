package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.globalconfig.ConfigFromXML;

public class LDAPConfig
{
  private ConfigFromXML config;
  private static LDAPConfig instance;
  private String LDAPServerAddress;
  private String LDAPUserDN;
  private String LDAP_USER_Password;
  private int LDAPPort;

  private LDAPConfig()
    throws IDAException
  {
    init();
  }

  public void init()
    throws IDAException
  {
    this.config = new ConfigFromXML("LDAPConfig", "./config/CAConfig.xml");
    this.LDAPServerAddress = this.config.getString("LDAPServerAddress");
    this.LDAPPort = this.config.getNumber("LDAPPort");
    this.LDAPUserDN = this.config.getString("LDAPUserDN");
    this.LDAP_USER_Password = this.config.getString("LDAP_USER_Password");
  }

  public static LDAPConfig getInstance()
    throws IDAException
  {
    if (instance == null)
      instance = new LDAPConfig();
    return instance;
  }

  public String getLDAPServerAddress()
  {
    return this.LDAPServerAddress;
  }

  public void setLDAPServerAddress(String paramString)
    throws IDAException
  {
    this.LDAPServerAddress = paramString;
    this.config.setString("LDAPServerAddress", paramString);
  }

  public int getLDAPPort()
  {
    return this.LDAPPort;
  }

  public void setLDAPPort(int paramInt)
    throws IDAException
  {
    this.LDAPPort = paramInt;
    this.config.setNumber("LDAPPort", paramInt);
  }

  public String getLDAPUserDN()
  {
    return this.LDAPUserDN;
  }

  public void setLDAPUserDN(String paramString)
    throws IDAException
  {
    this.LDAPUserDN = paramString;
    this.config.setString("LDAPUserDN", paramString);
  }

  public String getLDAP_USER_Password()
  {
    return this.LDAP_USER_Password;
  }

  public void setLDAP_USER_Password(String paramString)
    throws IDAException
  {
    this.LDAP_USER_Password = paramString;
    this.config.setString("LDAP_USER_Password", paramString);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.LDAPConfig
 * JD-Core Version:    0.6.0
 */