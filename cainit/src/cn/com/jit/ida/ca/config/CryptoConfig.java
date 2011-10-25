package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.initserver.InitServerException;
import cn.com.jit.ida.globalconfig.ConfigFromXML;

public class CryptoConfig
{
  private ConfigFromXML config;
  private static CryptoConfig instance;
  private String rootCert;
  private String rootCertPWD;
  private String deviceID;
  private int signingKeyID;
  private String SignPWDIsConfirmed;
  private String commDeviceID;
  private int commKeyID;
  private String commKeyStore;
  private String commKeyStorePWD;

  private CryptoConfig()
    throws IDAException
  {
    init();
  }

  public static CryptoConfig getInstance()
    throws IDAException
  {
    if (instance == null)
      instance = new CryptoConfig();
    return instance;
  }

  public void init()
    throws IDAException
  {
    this.config = new ConfigFromXML("Config||CryptoConfig", "./config/CAConfig.xml");
    this.rootCert = this.config.getString("SigningKeyStore");
    this.rootCertPWD = this.config.getString("SigningKeyStorePWD");
    this.SignPWDIsConfirmed = this.config.getString("SignPWDIsConfirmed");
    this.config.setString("SigningKeyStorePWD", this.rootCertPWD);
    this.deviceID = this.config.getString("CASigningDeviceID");
    this.signingKeyID = this.config.getNumber("CASigningKeyID");
    this.commDeviceID = this.config.getString("CACommDeviceID");
    this.commKeyID = this.config.getNumber("CACommKeyID");
    this.commKeyStore = this.config.getString("CommKeyStore");
    this.commKeyStorePWD = this.config.getString("CommKeyStorePWD");
    if ((this.deviceID.equalsIgnoreCase("JSJY05B_LIB")) && (this.deviceID.equalsIgnoreCase(this.commDeviceID)) && (this.signingKeyID == this.commKeyID))
      throw new InitServerException("0989", "不允许CA签名证书和通信证书使用同一加密设备的同一密钥，请重新配置");
    if (this.commKeyStore.equalsIgnoreCase(this.rootCert))
      throw new InitServerException("0990", "不允许CA签名证书和通信证书使用同一密钥容器");
  }

  public String getDeviceID()
  {
    return this.deviceID;
  }

  public void setDeviceID(String paramString)
    throws IDAException
  {
    this.deviceID = paramString;
    this.config.setString("CASigningDeviceID", paramString);
  }

  public String getRootCert()
  {
    return this.rootCert;
  }

  public void setRootCert(String paramString)
    throws IDAException
  {
    this.rootCert = paramString;
    this.config.setString("SigningKeyStore", paramString);
  }

  public String getRootCertPWD()
  {
    return this.rootCertPWD;
  }

  public String getCommDeviceID()
  {
    return this.commDeviceID;
  }

  public int getCommKeyID()
  {
    return this.commKeyID;
  }

  public String getCommKeyStorePWD()
  {
    return this.commKeyStorePWD;
  }

  public String getCommKeyStore()
  {
    return this.commKeyStore;
  }

  public String getSignPWDIsConfirmed()
  {
    return this.SignPWDIsConfirmed;
  }

  public void setRootCertPWD(String paramString)
    throws IDAException
  {
    this.rootCertPWD = paramString;
    this.config.setString("SigningKeyStorePWD", paramString);
  }

  public void setCommKeyID(int paramInt)
    throws IDAException
  {
    this.config.setNumber("CACommKeyID", paramInt);
    this.commKeyID = paramInt;
  }

  public void setCommDeviceID(String paramString)
    throws IDAException
  {
    this.config.setString("CACommDeviceID", paramString);
    this.commDeviceID = paramString;
  }

  public void setCommKeyStorePWD(String paramString)
    throws IDAException
  {
    this.config.setString("CommKeyStorePWD", paramString);
    this.commKeyStorePWD = paramString;
  }

  public void setCommKeyStore(String paramString)
    throws IDAException
  {
    this.config.setString("CommKeyStore", paramString);
    this.commKeyStore = paramString;
  }

  public void setSignPWDIsConfirmed(String paramString)
    throws IDAException
  {
    this.SignPWDIsConfirmed = paramString;
    this.config.setString("SignPWDIsConfirmed", paramString);
  }

  public int getSigningKeyID()
  {
    return this.signingKeyID;
  }

  public void setSigningKeyID(int paramInt)
  {
    this.signingKeyID = paramInt;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.CryptoConfig
 * JD-Core Version:    0.6.0
 */