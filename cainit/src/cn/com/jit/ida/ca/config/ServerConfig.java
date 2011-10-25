package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.globalconfig.ConfigFromXML;
import java.io.File;

public class ServerConfig
{
  ConfigFromXML config;
  private static ServerConfig instance;
  private String webServerAddress;
  private int webServerPort;
  private String webHomePath;
  private String webAppPath;
  private String webAppBasePath;
  private String webDocBasePath;
  private String secureWebServerAddress;
  private String secureWebHomePath;
  private String secureWebAppPath;
  private String secureWebDocBasePath;
  private String secureWebAppBasePath;
  private int secureWebPort;
  private String serverAddress;
  private int serverPort;
  private int acceptThreadCount;
  private int maxProcessThread;
  private int minProcessThread;
  private int minFreeThread;
  private String serverType;
  private String communicateCert;
  private String communicateCertPWD;
  private int webSessionTimeOut;
  private int secureWebSessionTimeOut;
  private String webType;
  private int controlPort;
  private int sessionTimeOut;

  private ServerConfig()
    throws IDAException
  {
    init();
  }

  public static ServerConfig getInstance()
    throws IDAException
  {
    if (instance == null)
      instance = new ServerConfig();
    return instance;
  }

  public void init()
    throws IDAException
  {
    this.config = new ConfigFromXML("NetConfig", "./config/CAConfig.xml");
    this.webServerAddress = this.config.getString("ServerAddress");
    this.webServerPort = this.config.getNumber("WebServerPort");
    this.webHomePath = this.config.getString("WebHomePath");
    this.webAppPath = this.config.getString("WebAppPath");
    this.webAppBasePath = this.config.getString("WebAppBasePath");
    this.webDocBasePath = this.config.getString("WebAppDocBasePath");
    this.secureWebServerAddress = this.config.getString("ServerAddress");
    this.secureWebPort = this.config.getNumber("SecureWebServerPort");
    this.secureWebHomePath = this.config.getString("SecureWebHomePath");
    this.secureWebAppPath = this.config.getString("SecureWebAppPath");
    this.secureWebDocBasePath = this.config.getString("SecureWebAppDocBasePath");
    this.secureWebAppBasePath = this.config.getString("SecureWebAppBasePath");
    this.serverAddress = this.config.getString("ServerAddress");
    this.serverPort = this.config.getNumber("ServicePort");
    this.acceptThreadCount = this.config.getNumber("AcceptThreadCount");
    this.maxProcessThread = this.config.getNumber("MaxProcessThread");
    this.serverType = this.config.getString("ServerType");
    this.communicateCert = this.config.getString("CommKeyStore");
    this.communicateCertPWD = this.config.getString("CommKeyStorePWD");
    this.webSessionTimeOut = this.config.getNumber("WebSessionTimeOut");
    this.secureWebSessionTimeOut = this.config.getNumber("SecureWebSessionTimeOut");
    this.webType = this.config.getString("WebType");
    this.controlPort = this.config.getNumber("ControlPort");
    this.sessionTimeOut = this.config.getNumber("ServiceTimeOut");
  }

  public String getServerAddress()
  {
    if (this.serverAddress.equalsIgnoreCase(""))
      return "localhost";
    return this.serverAddress;
  }

  public void setServerAddress(String paramString)
    throws IDAException
  {
    this.serverAddress = paramString;
    this.config.setString("ServerAddress", paramString);
  }

  public int getPort()
  {
    return this.serverPort;
  }

  public void setPort(int paramInt)
    throws IDAException
  {
    this.config.setNumber("ServicePort", paramInt);
    this.serverPort = paramInt;
  }

  public int getAcceptThreadCount()
  {
    return this.acceptThreadCount;
  }

  public void setAcceptCount(int paramInt)
    throws IDAException
  {
    this.acceptThreadCount = paramInt;
    this.config.setNumber("AcceptThreadCount", paramInt);
  }

  public int getMaxProcessThread()
  {
    return this.maxProcessThread;
  }

  public void setMaxProcessThread(int paramInt)
    throws IDAException
  {
    this.config.setNumber("MaxProcessThread", paramInt);
    this.maxProcessThread = paramInt;
  }

  public String getServerType()
  {
    return this.serverType;
  }

  public void setServerType(String paramString)
    throws IDAException
  {
    this.serverType = paramString;
    this.config.setString("ServerType", paramString);
  }

  public String getCommunicateCert()
  {
    File localFile = new File(this.communicateCert);
    return localFile.getAbsolutePath();
  }

  public void setCommunicateCert(String paramString)
    throws IDAException
  {
    this.communicateCert = paramString;
    this.config.setString("CommKeyStore", paramString);
  }

  public String getCommunicateCertPWD()
  {
    return this.communicateCertPWD;
  }

  public void setCommunicateCertPWD(String paramString)
    throws IDAException
  {
    this.communicateCertPWD = paramString;
    this.config.setString("CommKeyStorePWD", paramString);
  }

  public String getWebServerAddress()
  {
    if (this.webServerAddress.equalsIgnoreCase(""))
      return "localhost";
    return this.webServerAddress;
  }

  public void setWebServerAddress(String paramString)
    throws IDAException
  {
    this.webServerAddress = paramString;
    this.config.setString("ServerAddress", paramString);
  }

  public int getWebServerPort()
  {
    return this.webServerPort;
  }

  public void setWebServerPort(int paramInt)
    throws IDAException
  {
    this.config.setNumber("WebServerPort", paramInt);
    this.webServerPort = paramInt;
  }

  public String getWebHomePath()
  {
    return this.webHomePath;
  }

  public void setWebHomePath(String paramString)
    throws IDAException
  {
    this.config.setString("WebHomePath", paramString);
    this.webHomePath = paramString;
  }

  public String getWebAppPath()
  {
    return this.webAppPath;
  }

  public void setWebAppPath(String paramString)
    throws IDAException
  {
    this.config.setString("WebAppPath", this.webHomePath);
    this.webAppPath = paramString;
  }

  public String getWebAppBasePath()
  {
    return this.webAppBasePath;
  }

  public void setWebAppBasePath(String paramString)
    throws IDAException
  {
    this.config.setString("WebAppBasePath", paramString);
    this.webAppBasePath = paramString;
  }

  public String getWebDocBasePath()
  {
    return this.webDocBasePath;
  }

  public void setWebDocBasePath(String paramString)
    throws IDAException
  {
    this.config.setString("WebAppDocBasePath", paramString);
    this.webDocBasePath = paramString;
  }

  public String getSecureWebServerAddress()
  {
    if (this.secureWebServerAddress.equalsIgnoreCase(""))
      return "localhost";
    return this.secureWebServerAddress;
  }

  public void setSecureWebServerAddress(String paramString)
    throws IDAException
  {
    this.config.setString("ServerAddress", paramString);
    this.secureWebServerAddress = paramString;
  }

  public int getSecureWebPort()
  {
    return this.secureWebPort;
  }

  public void setSecureWebPort(int paramInt)
    throws IDAException
  {
    this.config.setNumber("SecureWebServerPort", paramInt);
    this.secureWebPort = paramInt;
  }

  public String getSecureWebHomePath()
  {
    return this.secureWebHomePath;
  }

  public void setSecureWebHomePath(String paramString)
    throws IDAException
  {
    this.config.setString("SecureWebHomePath", paramString);
    this.secureWebHomePath = paramString;
  }

  public String getSecureWebAppPath()
  {
    return this.secureWebAppPath;
  }

  public void setSecureWebAppPath(String paramString)
    throws IDAException
  {
    this.config.setString("SecureWebAppPath", paramString);
    this.secureWebAppPath = paramString;
  }

  public String getSecureWebDocBasePath()
  {
    return this.secureWebDocBasePath;
  }

  public void setSecureWebDocBasePath(String paramString)
    throws IDAException
  {
    this.config.setString("SecureWebAppDocBasePath", paramString);
    this.secureWebDocBasePath = paramString;
  }

  public String getSecureWebAppBasePath()
  {
    return this.secureWebAppBasePath;
  }

  public void setSecureWebAppBasePath(String paramString)
    throws IDAException
  {
    this.config.setString("SecureWebAppBasePath", paramString);
    this.secureWebAppBasePath = paramString;
  }

  public int getWebSessionTimeOut()
  {
    return this.webSessionTimeOut;
  }

  public void setWebSessionTimeOut(int paramInt)
    throws IDAException
  {
    this.config.setNumber("WebSessionTimeOut", paramInt);
    this.webSessionTimeOut = paramInt;
  }

  public int getSecureWebSessionTimeOut()
  {
    return this.secureWebSessionTimeOut;
  }

  public void setSecureWebSessionTimeOut(int paramInt)
    throws IDAException
  {
    this.config.setNumber("SecureWebSessionTimeOut", paramInt);
    this.secureWebSessionTimeOut = paramInt;
  }

  public String getWebType()
  {
    return this.webType;
  }

  public void setWebType(String paramString)
    throws IDAException
  {
    this.config.setString("WebType", paramString);
    this.webType = paramString;
  }

  public int getControlPort()
  {
    return this.controlPort;
  }

  public void setControlPort(int paramInt)
    throws IDAException
  {
    this.config.setNumber("ControlPort", paramInt);
    this.controlPort = paramInt;
  }

  public int getSessionTimeOut()
  {
    return this.sessionTimeOut * 1000;
  }

  public void setSessionTimeOut(int paramInt)
    throws IDAException
  {
    this.config.setNumber("ServiceTimeOut", paramInt / 1000);
    this.sessionTimeOut = paramInt;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.ServerConfig
 * JD-Core Version:    0.6.0
 */