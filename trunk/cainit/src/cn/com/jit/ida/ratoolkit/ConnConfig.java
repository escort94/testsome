package cn.com.jit.ida.ratoolkit;

public class ConnConfig
{
  public static final String KEYTYPE_JKS = "JKS";
  public static final String KEYTYPE_P12 = "PKCS12";
  private String serverIP = null;
  private int serverPort = -1;
  private String userKeyPath = null;
  private char[] userKeyPassword = null;
  private String userKeyType = null;
  private String deviceID;
  private String pin;
  private String cfgName;
  private String hardDriver;
  private String sjyName;

  public String getServerIP()
  {
    return this.serverIP;
  }

  public int getServerPort()
  {
    return this.serverPort;
  }

  public void setServer(String paramString, int paramInt)
  {
    this.serverIP = paramString;
    this.serverPort = paramInt;
  }

  public String getUserKeyPath()
    throws Exception
  {
    return this.userKeyPath;
  }

  public char[] getUserKeyPassword()
  {
    return this.userKeyPassword;
  }

  public String getUserKeyType()
  {
    return this.userKeyType;
  }

  public void setUserKeyFile_JKS(String paramString, char[] paramArrayOfChar)
  {
    this.userKeyType = "JKS";
    this.userKeyPath = paramString;
    this.userKeyPassword = paramArrayOfChar;
  }

  public String getCfgName()
  {
    return this.cfgName;
  }

  public void setCfgName(String paramString)
  {
    this.cfgName = paramString;
  }

  public String getDeviceID()
  {
    return this.deviceID;
  }

  public void setDeviceID(String paramString)
  {
    this.deviceID = paramString;
  }

  public String getHardDriver()
  {
    return this.hardDriver;
  }

  public void setHardDriver(String paramString)
  {
    this.hardDriver = paramString;
  }

  public String getSjyName()
  {
    return this.sjyName;
  }

  public void setSjyName(String paramString)
  {
    this.sjyName = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ratoolkit.ConnConfig
 * JD-Core Version:    0.6.0
 */