package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.globalconfig.ConfigFromXML;

public class LOGConfig
{
  private ConfigFromXML config;
  private static LOGConfig instance;
  private String LOG_Path;

  private LOGConfig()
    throws IDAException
  {
    init();
  }

  public void init()
    throws IDAException
  {
    this.config = new ConfigFromXML("LOGConfig", "./config/CAConfig.xml");
    try
    {
      DBManager localDBManager = DBManager.getInstance();
    }
    catch (DBException localDBException)
    {
    }
    this.LOG_Path = this.config.getString("LOG_Path");
  }

  public static LOGConfig getInstance()
    throws IDAException
  {
    if (instance == null)
      instance = new LOGConfig();
    return instance;
  }

  public String getLOG_Path()
  {
    return this.LOG_Path;
  }

  public void setLOG_Path(String paramString)
    throws IDAException
  {
    this.LOG_Path = paramString;
    this.config.setString("LOG_Path", paramString);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.LOGConfig
 * JD-Core Version:    0.6.0
 */