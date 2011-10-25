package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.globalconfig.ConfigFromXML;

public class AutoServiceConfig
{
  private ConfigFromXML config;
  private static AutoServiceConfig instance;
  private long checkUpdateServiceInterval;
  private boolean useAutoCertArchive;
  private int autoCertArchiveInterval;
  private int certAfterDays;
  private boolean useAutoLogArchive;
  private int autoLogArchiveInterval;
  private int logBeforeDays;

  private AutoServiceConfig()
    throws IDAException
  {
    init();
  }

  public void init()
    throws IDAException
  {
    this.config = new ConfigFromXML("AutoServiceConfig", "./config/CAConfig.xml");
    this.checkUpdateServiceInterval = this.config.getLong("CheckUpdateServiceInterval");
    this.useAutoCertArchive = this.config.getBoolean("UseAutoCertArchive");
    this.autoCertArchiveInterval = this.config.getNumber("AutoCertArchiveInterval");
    this.useAutoLogArchive = this.config.getBoolean("UseAutoLogArchive");
    this.autoLogArchiveInterval = this.config.getNumber("AutoLogArchiveInterval");
    this.certAfterDays = this.config.getNumber("CertAfterDays");
    this.logBeforeDays = this.config.getNumber("LogBeforeDays");
  }

  public static AutoServiceConfig getInstance()
    throws IDAException
  {
    if (instance == null)
      instance = new AutoServiceConfig();
    return instance;
  }

  public int getAutoCertArchiveInterval()
  {
    return this.autoCertArchiveInterval;
  }

  public int getAutoLogArchiveInterval()
  {
    return this.autoLogArchiveInterval;
  }

  public long getCheckUpdateServiceInterval()
  {
    return this.checkUpdateServiceInterval;
  }

  public boolean isUseAutoCertArchive()
  {
    return this.useAutoCertArchive;
  }

  public boolean isUseAutoLogArchive()
  {
    return this.useAutoLogArchive;
  }

  public int getCertAfterDays()
  {
    return this.certAfterDays;
  }

  public int getLogBeforeDays()
  {
    return this.logBeforeDays;
  }

  public void setUseAutoLogArchive(boolean paramBoolean)
    throws IDAException
  {
    this.useAutoLogArchive = paramBoolean;
    this.config.setBoolean("UseAutoLogArchive", paramBoolean);
  }

  public void setUseAutoCertArchive(boolean paramBoolean)
    throws IDAException
  {
    this.useAutoCertArchive = paramBoolean;
    this.config.setBoolean("UseAutoCertArchive", paramBoolean);
  }

  public void setCheckUpdateServiceInterval(long paramLong)
    throws IDAException
  {
    this.checkUpdateServiceInterval = paramLong;
    this.config.setLong("CheckUpdateServiceInterval", paramLong);
  }

  public void setAutoLogArchiveInterval(int paramInt)
    throws IDAException
  {
    this.autoLogArchiveInterval = paramInt;
    this.config.setNumber("AutoLogArchiveInterval", paramInt);
  }

  public void setAutoCertArchiveInterval(int paramInt)
    throws IDAException
  {
    this.autoCertArchiveInterval = paramInt;
    this.config.setNumber("AutoCertArchiveInterval", paramInt);
  }

  public void setCertAfterDays(int paramInt)
    throws IDAException
  {
    this.certAfterDays = paramInt;
    this.config.setNumber("CertAfterDays", paramInt);
  }

  public void setLogBeforeDays(int paramInt)
    throws IDAException
  {
    this.logBeforeDays = paramInt;
    this.config.setNumber("LogBeforeDays", paramInt);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.AutoServiceConfig
 * JD-Core Version:    0.6.0
 */