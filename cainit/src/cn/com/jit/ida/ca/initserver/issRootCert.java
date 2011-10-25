package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.LDAPConfig;
import cn.com.jit.ida.ca.issue.ISSException;
import cn.com.jit.ida.ca.issue.IssuanceManager;
import cn.com.jit.ida.ca.issue.entity.CAEntity;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.cert.X509Cert;

public class issRootCert
{
  GlobalConfig globalConfig;

  public void run()
    throws InitServerException
  {
    try
    {
      this.globalConfig = GlobalConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
      new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
    if (!this.globalConfig.getLDAPConfig().getLDAPServerAddress().equalsIgnoreCase(""))
      issCert("LDAP");
    if (!this.globalConfig.getCAConfig().getCertFilePath().equalsIgnoreCase(""))
      issCert("FILE");
  }

  private void issCert(String paramString)
    throws InitServerException
  {
    CAConfig localCAConfig = null;
    try
    {
      localCAConfig = CAConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
      throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
    CAEntity localCAEntity = new CAEntity(paramString);
    String str = localCAConfig.getBaseDN();
    localCAEntity.setBaseDN(str);
    X509Cert localX509Cert = this.globalConfig.getCAConfig().getRootCert();
    try
    {
      localCAEntity.setCACert(localX509Cert.getEncoded());
      localCAEntity.setCACertSN(localX509Cert.getStringSerialNumber());
      localCAEntity.setCASubject(localX509Cert.getSubject());
      IssuanceManager localIssuanceManager = IssuanceManager.getInstance();
      localIssuanceManager.issue(localCAEntity);
    }
    catch (PKIException localPKIException)
    {
      throw new InitServerException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
    catch (ISSException localISSException)
    {
      throw new InitServerException(localISSException.getErrCode(), localISSException.getErrDesc(), localISSException);
    }
    catch (Exception localException)
    {
      throw new InitServerException("0999", "系统错误 构造发布管理器失败", localException);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.issRootCert
 * JD-Core Version:    0.6.0
 */