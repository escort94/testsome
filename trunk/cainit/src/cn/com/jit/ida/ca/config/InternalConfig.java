package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.initserver.InitServerException;
import cn.com.jit.ida.globalconfig.ConfigFromXML;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

public class InternalConfig
{
  ConfigFromXML config;
  private static InternalConfig instance;
  private String authCharSet = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
  private String logType = "TXT";
  private int logSize = 5000000;
  private String isLog = "TRUE";
  private String isPrivilage = "TRUE";
  private String isCTML = "TRUE";
  private String howGenerateAuthCode = "RANDOM";
  private String commCertTemplateName = "本系统服务器证书模板";
  private String adminTemplateName = "本系统管理员证书模板";
  private String raadminTemplateName = "RA业务员证书模板";
  private String socketProtocol = "SSLv3";
  private String webProtocol = "SSLv3";
  private long certCountSynchrointerval = 300000L;
  private boolean isOrderDB = true;
  private String crlEnding = "?certificateRevocationList?base?objectclass=cRLDistributionPoint";
  private int minKeySize = 128;
  private boolean limitSuperAdmin = false;
  private boolean exchangeSysCRLInternal = true;
  private Vector raAdminDN = new Vector();
  private boolean verifyP10DN = false;
  private int downloadNameType = 0;
  private boolean startEndUser = true;
  private String sslSessionTimeout = "60";
  private String ldapStoreClass = "cn.com.jit.ida.ca.issue.opt.LDAPStore";
  private boolean useCRLExtension = true;
  private boolean changeCRLIssPeriod = false;

  private InternalConfig()
    throws IDAException
  {
    init();
  }

  private void init()
    throws IDAException
  {
    File localFile = new File("./config/InternalConf.xml");
    if (!localFile.exists())
      return;
    String str = "";
    this.config = new ConfigFromXML("InternalConfig", "./config/InternalConf.xml");
    str = this.config.getString("R1");
    if (!str.equalsIgnoreCase(""))
      this.authCharSet = str;
    str = this.config.getString("R6");
    if (!str.equalsIgnoreCase(""))
      this.logType = str;
    str = this.config.getString("R7");
    if (!str.equalsIgnoreCase(""))
      this.logSize = Integer.parseInt(str);
    str = this.config.getString("R3");
    if (!str.equalsIgnoreCase(""))
      this.isLog = str;
    str = this.config.getString("R4");
    if (!str.equalsIgnoreCase(""))
      this.isPrivilage = str;
    str = this.config.getString("R5");
    if (!str.equalsIgnoreCase(""))
      this.isCTML = str;
    str = this.config.getString("R2");
    if (!str.equalsIgnoreCase(""))
      this.howGenerateAuthCode = str;
    this.adminTemplateName = this.config.getString("AdminTemplateName");
    if (this.adminTemplateName.equalsIgnoreCase(""))
      this.adminTemplateName = "本系统管理员证书模板";
    this.commCertTemplateName = this.config.getString("CommCertTemplateName");
    if (this.commCertTemplateName.equalsIgnoreCase(""))
      this.commCertTemplateName = "本系统服务器证书模板";
    this.raadminTemplateName = this.config.getString("RAAdminTemplateName");
    if (this.raadminTemplateName.equalsIgnoreCase(""))
      this.raadminTemplateName = "RA业务员证书模板";
    str = ChangeProtocol(this.config.getString("R8"));
    if (!str.equalsIgnoreCase(""))
      this.socketProtocol = str;
    str = ChangeProtocol(this.config.getString("R9"));
    if (!str.equalsIgnoreCase(""))
      this.webProtocol = str;
    str = this.config.getString("R12");
    if (!str.equalsIgnoreCase(""))
      if (str.equalsIgnoreCase("true"))
        this.isOrderDB = true;
      else
        this.isOrderDB = false;
    str = this.config.getString("R11");
    if (!str.equalsIgnoreCase(""))
      this.certCountSynchrointerval = Long.parseLong(str);
    if (this.certCountSynchrointerval > 300000L)
      this.certCountSynchrointerval = 300000L;
    str = this.config.getString("R13");
    if (!str.equalsIgnoreCase(""))
      this.crlEnding = str;
    str = this.config.getString("R14");
    if (!str.equalsIgnoreCase(""))
      this.minKeySize = Integer.parseInt(str);
    str = this.config.getString("R15");
    if (!str.equalsIgnoreCase(""))
      if (str.equalsIgnoreCase("true"))
        this.limitSuperAdmin = true;
      else
        this.limitSuperAdmin = false;
    str = this.config.getString("R17");
    if (!str.equalsIgnoreCase(""))
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(str, ";");
      while (localStringTokenizer.hasMoreElements())
        this.raAdminDN.add(localStringTokenizer.nextToken());
    }
    str = this.config.getString("R18");
    if (!str.equalsIgnoreCase(""))
      if (str.equalsIgnoreCase("true"))
        this.verifyP10DN = true;
      else
        this.verifyP10DN = false;
    str = this.config.getString("R19");
    if (!str.equalsIgnoreCase(""))
      this.downloadNameType = Integer.parseInt(str);
    str = this.config.getString("R20");
    if (!str.equalsIgnoreCase(""))
      if (str.equalsIgnoreCase("true"))
        this.startEndUser = true;
      else
        this.startEndUser = false;
    str = this.config.getString("R21");
    if (!str.equals(""))
      this.sslSessionTimeout = str;
    str = this.config.getString("R22");
    if (!str.equals(""))
      this.ldapStoreClass = str;
    str = this.config.getString("R23");
    if (!str.equalsIgnoreCase(""))
      if (str.equalsIgnoreCase("true"))
        this.useCRLExtension = true;
      else
        this.useCRLExtension = false;
    str = this.config.getString("R24");
    if (!str.equalsIgnoreCase(""))
      if (str.equalsIgnoreCase("true"))
        this.changeCRLIssPeriod = true;
      else
        this.changeCRLIssPeriod = false;
  }

  public static InternalConfig getInstance()
    throws IDAException
  {
    if (instance == null)
      instance = new InternalConfig();
    return instance;
  }

  public String getLdapStoreClass()
  {
    return this.ldapStoreClass;
  }

  public void setLdapStoreClass(String paramString)
    throws IDAException
  {
    this.ldapStoreClass = paramString;
    this.config.setString("R22", paramString);
  }

  public String getSSLSessionTimeout()
  {
    return this.sslSessionTimeout;
  }

  public void setSSLSessionTimeout(String paramString)
    throws IDAException
  {
    this.sslSessionTimeout = paramString;
    this.config.setString("R21", paramString);
  }

  public boolean getLimitSuperAdmin()
  {
    return this.limitSuperAdmin;
  }

  public void setLimitSuperAdmin(boolean paramBoolean)
    throws IDAException
  {
    this.limitSuperAdmin = paramBoolean;
    this.config.setBoolean("R15", paramBoolean);
  }

  public String getAuthCharSet()
  {
    return this.authCharSet;
  }

  public void setAuthCharSet(String paramString)
    throws IDAException
  {
    this.authCharSet = paramString;
    this.config.setString("R1", paramString);
  }

  public String getLogType()
  {
    return this.logType;
  }

  public void setLogType(String paramString)
    throws IDAException
  {
    this.logType = paramString;
    this.config.setString("R6", paramString);
  }

  public int getLogSize()
  {
    return this.logSize;
  }

  public void setLogSize(int paramInt)
    throws IDAException
  {
    this.logSize = paramInt;
    this.config.setNumber("R7", paramInt);
  }

  public String getIsLog()
  {
    return this.isLog;
  }

  public void setIsLog(String paramString)
    throws IDAException
  {
    this.isLog = paramString;
    this.config.setString("R3", paramString);
  }

  public String getIsPrivilage()
  {
    return this.isPrivilage;
  }

  public void setIsPrivilage(String paramString)
    throws IDAException
  {
    this.isPrivilage = paramString;
    this.config.setString("R4", paramString);
  }

  public String getIsCTML()
  {
    return this.isCTML;
  }

  public void setIsCTML(String paramString)
    throws IDAException
  {
    this.isCTML = paramString;
    this.config.setString("R5", paramString);
  }

  public String getHowGenerateAuthCode()
  {
    return this.howGenerateAuthCode;
  }

  public void setHowGenerateAuthCode(String paramString)
    throws IDAException
  {
    this.howGenerateAuthCode = paramString;
    this.config.setString("R2", paramString);
  }

  public String getCommCertTemplateName()
    throws InitServerException
  {
    return this.commCertTemplateName;
  }

  public void setCommCertTemplateName(String paramString)
    throws IDAException
  {
    this.commCertTemplateName = paramString;
    this.config.setString("CommCertTemplateName", paramString);
  }

  public String getAdminTemplateName()
  {
    return this.adminTemplateName;
  }

  public void setAdminTemplateName(String paramString)
    throws IDAException
  {
    this.adminTemplateName = paramString;
    this.config.setString("AdminTemplateName", paramString);
  }

  public String getSocketProtocol()
  {
    return this.socketProtocol;
  }

  public void setSocketProtocol(String paramString)
    throws IDAException
  {
    this.config.setString("R8", ChangeProtocol(paramString));
    this.socketProtocol = paramString;
  }

  public String getWebProtocol()
  {
    return this.webProtocol;
  }

  public void setWebProtocol(String paramString)
    throws IDAException
  {
    this.config.setString("R9", ChangeProtocol(paramString));
    this.webProtocol = paramString;
  }

  public long getCertCountSynchrointerval()
  {
    return this.certCountSynchrointerval;
  }

  public void setCertCountSynchrointerval(long paramLong)
    throws IDAException
  {
    this.config.setLong("R11", paramLong);
    this.certCountSynchrointerval = paramLong;
  }

  private String ChangeProtocol(String paramString)
  {
    if (paramString.equalsIgnoreCase("4"))
      return "TLSv1";
    if (paramString.equalsIgnoreCase("3"))
      return "SSLv3";
    if (paramString.equalsIgnoreCase("TLSv1"))
      return "4";
    if (paramString.equalsIgnoreCase("SSLv3"))
      return "3";
    return "";
  }

  public boolean getIsOrderDB()
  {
    return this.isOrderDB;
  }

  public void setIsOrderDB(boolean paramBoolean)
    throws IDAException
  {
    this.config.setBoolean("R12", paramBoolean);
    this.isOrderDB = paramBoolean;
  }

  public String getRaadminTemplateName()
  {
    return this.raadminTemplateName;
  }

  public void setRaadminTemplateName(String paramString)
  {
    this.raadminTemplateName = paramString;
  }

  public String getCrlEnding()
  {
    return this.crlEnding;
  }

  public int getMinKeySize()
  {
    return this.minKeySize;
  }

  public boolean isExchangeSysCRLInternal()
  {
    return this.exchangeSysCRLInternal;
  }

  public Vector getRaAdminDN()
  {
    return this.raAdminDN;
  }

  public boolean isVerifyP10DN()
  {
    return this.verifyP10DN;
  }

  public int getDownloadNameType()
  {
    return this.downloadNameType;
  }

  public boolean isStartEndUser()
  {
    return this.startEndUser;
  }

  public boolean isChangeCRLIssPeriod()
  {
    return this.changeCRLIssPeriod;
  }

  public boolean isUseCRLextension()
  {
    return this.useCRLExtension;
  }

  public void setCrlEnding(String paramString)
    throws IDAException
  {
    this.config.setString("R13", paramString);
    this.crlEnding = paramString;
  }

  public void setMinKeySize(int paramInt)
    throws IDAException
  {
    this.config.setNumber("R14", paramInt);
    this.minKeySize = paramInt;
  }

  public void setExchangeSysCRLInternal(boolean paramBoolean)
    throws IDAException
  {
    this.config.setBoolean("R16", paramBoolean);
    this.exchangeSysCRLInternal = paramBoolean;
  }

  public void setVerifyP10DN(boolean paramBoolean)
    throws IDAException
  {
    this.config.setBoolean("R18", paramBoolean);
    this.verifyP10DN = paramBoolean;
  }

  public void setStartEndUser(boolean paramBoolean)
    throws IDAException
  {
    this.config.setBoolean("R20", paramBoolean);
    this.startEndUser = paramBoolean;
  }

  public void setChangeCRLIssPeriod(boolean paramBoolean)
    throws IDAException
  {
    this.config.setBoolean("R24", paramBoolean);
    this.changeCRLIssPeriod = paramBoolean;
  }

  public void setUseCRLextension(boolean paramBoolean)
    throws IDAException
  {
    this.useCRLExtension = paramBoolean;
    this.config.setBoolean("R23", paramBoolean);
  }

  public void setDownloadName(int paramInt)
    throws IDAException
  {
    this.config.setNumber("R19", paramInt);
    this.downloadNameType = paramInt;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.InternalConfig
 * JD-Core Version:    0.6.0
 */