package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;

public class GlobalConfig
{
  private static GlobalConfig instance;
  private CryptoConfig cryptoConfig;
  private DBConfig dbConfig;
  private InternalConfig internalConfig;
  private ServerConfig serverConfig;
  private CAConfig caConfig;
  private LOGConfig logConfig;
  private LDAPConfig ldapConfig;
  private KMCConfig kmcConfig;
  private AutoServiceConfig autoServiceConfig;

  private GlobalConfig()
    throws IDAException
  {
    init();
  }

  private void init()
    throws IDAException
  {
    this.cryptoConfig = CryptoConfig.getInstance();
    this.dbConfig = DBConfig.getInstance();
    this.internalConfig = InternalConfig.getInstance();
    this.serverConfig = ServerConfig.getInstance();
    this.caConfig = CAConfig.getInstance();
    this.logConfig = LOGConfig.getInstance();
    this.ldapConfig = LDAPConfig.getInstance();
    this.kmcConfig = KMCConfig.getInstance();
    this.autoServiceConfig = AutoServiceConfig.getInstance();
  }

  public static GlobalConfig getInstance()
    throws IDAException
  {
    if (instance == null)
      instance = new GlobalConfig();
    return instance;
  }

  public DBConfig getDBConfig()
  {
    return this.dbConfig;
  }

  public LDAPConfig getLDAPConfig()
  {
    return this.ldapConfig;
  }

  public CryptoConfig getCryptoConfig()
  {
    return this.cryptoConfig;
  }

  public ServerConfig getServerConfig()
  {
    return this.serverConfig;
  }

  public void getMasterKeyConfig()
  {
  }

  public KMCConfig getKMCConfig()
  {
    return this.kmcConfig;
  }

  public CAConfig getCAConfig()
  {
    return this.caConfig;
  }

  public InternalConfig getInternalConfig()
  {
    return this.internalConfig;
  }


  public AutoServiceConfig getAutoServiceConfig()
  {
    return this.autoServiceConfig;
  }

  public KMCConfig getKmcConfig()
  {
    return this.kmcConfig;
  }

  public LOGConfig getLogConfig()
  {
    return this.logConfig;
  }

  public LOGConfig getLOGConfig()
  {
    return this.logConfig;
  }

  public void reflush()
    throws IDAException
  {
    this.cryptoConfig.init();
    this.caConfig.init();
    this.dbConfig.init();
    this.ldapConfig.init();
    LOGConfig.getInstance();
    this.serverConfig.init();
    this.kmcConfig.init();
    this.autoServiceConfig.init();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.GlobalConfig
 * JD-Core Version:    0.6.0
 */