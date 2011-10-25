package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.config.ServerConfig;
import cn.com.jit.ida.globalconfig.ConfigTool;

public class updataCommCert
{
  GlobalConfig globalConfig = null;
  String validity = this.validity;

  public boolean run()
    throws InitServerException
  {
    try
    {
      this.globalConfig = GlobalConfig.getInstance();
    }
    catch (IDAException localIDAException1)
    {
      new InitServerException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1);
    }
    String str = ConfigTool.getInteger("请输入通信证书有效期，如365", 2147483647, 0, "天");
    if (str == null)
      return false;
    updataCommCertOpt localupdataCommCertOpt = new updataCommCertOpt(this.globalConfig.getCAConfig().getCommCertDN(), this.globalConfig.getInternalConfig().getCommCertTemplateName(), str);
    localupdataCommCertOpt.doUpdata();
    if (this.globalConfig.getCryptoConfig().getCommDeviceID().equalsIgnoreCase("JSOFT_LIB"))
    {
      localupdataCommCertOpt.SavaKeyStore(this.globalConfig.getServerConfig().getCommunicateCert(), this.globalConfig.getServerConfig().getCommunicateCertPWD().toCharArray());
    }
    else
    {
      boolean bool = false;
      try
      {
        bool = localupdataCommCertOpt.importCert(false);
      }
      catch (IDAException localIDAException2)
      {
        throw new InitServerException(localIDAException2.getErrCode(), localIDAException2.getErrDesc(), localIDAException2.getHistory());
      }
      if (!bool)
        throw new InitServerException("0979", "向加密设备导入通信证书失败");
    }
    return true;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.updataCommCert
 * JD-Core Version:    0.6.0
 */