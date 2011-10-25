package cn.com.jit.ida.net;

import cn.com.jit.ida.IDAException;

public class NetConfig
{
  public static final String KEYSTORE_JKS = "JKS";
  public static final String KEYSTORE_PKCS11 = "PKCS11";
  public static final String BYTETYPE = "BYTE";
  public static final String STRINGTYPE = "STRING";
  public static final String SSLTYPE = "SSL";
  public static final String SOCKETTYPE = "SOCKET";
  private String ServerAddress;
  private int port;
  String rootkeystore;
  String passWord;
  String trustKeyStore;
  String trustPassword;
  String connectType = "SOCKET";
  String dataType = "BYTE";
  boolean NeedClientAuth = true;
  private int timeOut;
  private String secureProtocol = "SSL";
  private String keyStoreType = "JKS";

  public void setDataType(String paramString)
    throws IDAException
  {
    if (paramString.equals("BYTE"))
      this.dataType = "BYTE";
    else if (paramString.equals("STRING"))
      this.dataType = "STRING";
    else
      throw new IDAException("Error parameter");
  }

  public String getDataType()
  {
    return this.dataType;
  }

  public void setNeadClientAuth(boolean paramBoolean)
  {
    this.NeedClientAuth = paramBoolean;
  }

  public boolean getNeedClientAuth()
  {
    return this.NeedClientAuth;
  }

  public void setPort(int paramInt)
  {
    this.port = paramInt;
  }

  public int getPort()
  {
    return this.port;
  }

  public void setServerAddress(String paramString)
  {
    this.ServerAddress = paramString;
  }

  public String getServerAddress()
  {
    return this.ServerAddress;
  }

  public void setTimeOut(int paramInt)
  {
    if (paramInt > 0)
      this.timeOut = paramInt;
  }

  public int getTimeOut()
  {
    return this.timeOut;
  }

  public void setKeystore(String paramString)
  {
    this.rootkeystore = paramString;
  }

  public void setKeystorePassword(String paramString)
  {
    this.passWord = paramString;
  }

  public void setTrustCerts(String paramString)
  {
    this.trustKeyStore = paramString;
  }

  public void setTrustCertsPassword(String paramString)
  {
    this.trustPassword = paramString;
  }

  public String getKeystore()
  {
    return this.rootkeystore;
  }

  public String getTrustCerts()
  {
    return this.trustKeyStore;
  }

  public String getPassword()
  {
    return this.passWord;
  }

  public String getTrustPassword()
  {
    return this.trustPassword;
  }

  public String getConnectType()
  {
    return this.connectType;
  }

  public void setConnectType(String paramString)
  {
    this.connectType = paramString;
  }

  public String getSecureProtocol()
  {
    return this.secureProtocol;
  }

  public String getKeyStoreType()
  {
    return this.keyStoreType;
  }

  public void setSecureProtocol(String paramString)
  {
    if (paramString.equalsIgnoreCase(""))
      this.secureProtocol = "SSL";
    else
      this.secureProtocol = paramString;
  }

  public void setKeyStoreType(String paramString)
  {
    this.keyStoreType = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.net.NetConfig
 * JD-Core Version:    0.6.0
 */