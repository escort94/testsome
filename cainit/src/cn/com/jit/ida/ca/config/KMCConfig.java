package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.globalconfig.ConfigFromXML;

public class KMCConfig
{
  private ConfigFromXML config;
  private static KMCConfig instance;
  private String KMCServer;
  private int KMCPort;

  private KMCConfig()
    throws IDAException
  {
    init();
  }

  public void init()
    throws IDAException
  {
    this.config = new ConfigFromXML("LOGConfig", "./config/CAConfig.xml");
    this.KMCServer = this.config.getString("KMCServerAddress");
    this.KMCPort = this.config.getNumber("KMCServerPort");
  }

  public static KMCConfig getInstance()
    throws IDAException
  {
    if (instance == null)
      instance = new KMCConfig();
    return instance;
  }

  public String getKMCServer()
  {
    return this.KMCServer;
  }

  public void setKMCServer(String paramString)
    throws IDAException
  {
    this.config.setString("KMCServerAddress", paramString);
    this.KMCServer = paramString;
  }

  public int getKMCPort()
  {
    return this.KMCPort;
  }

  public void setKMCPort(int paramInt)
    throws IDAException
  {
    this.config.setNumber("KMCServerPort", paramInt);
    this.KMCPort = paramInt;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.KMCConfig
 * JD-Core Version:    0.6.0
 */