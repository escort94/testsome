package cn.com.jit.ida.ca.certmanager.service.iss;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.issue.ISSException;
import cn.com.jit.ida.ca.issue.IssuanceManager;
import cn.com.jit.ida.ca.issue.entity.CertEntity;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;
import java.util.Vector;

public class CertIss
{
  private SysLogger sysLogger = LogManager.getSysLogger();

  public void issCert()
  {
    DBManager localDBManager = null;
    CertInfo[] arrayOfCertInfo = null;
    try
    {
      localDBManager = DBManager.getInstance();
      arrayOfCertInfo = localDBManager.getCertTBPInfo();
    }
    catch (DBException localDBException)
    {
      this.sysLogger.info("后台证书发布操作异常 " + localDBException.toString());
      return;
    }
    IssuanceManager localIssuanceManager = null;
    try
    {
      localIssuanceManager = IssuanceManager.getInstance();
    }
    catch (Exception localException1)
    {
      this.sysLogger.info("构造发布管理器失败 " + localException1.toString());
    }
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CertEntity localCertEntity = null;
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    Vector localVector = new Vector();
    for (int i = 0; i < arrayOfCertInfo.length; i++)
    {
      str1 = null;
      str2 = null;
      str3 = null;
      str4 = null;
      str1 = arrayOfCertInfo[i].getCertSN();
      str2 = arrayOfCertInfo[i].getSubject();
      str3 = arrayOfCertInfo[i].getCtmlName();
      str4 = arrayOfCertInfo[i].getCertStatus();
      CTML localCTML = null;
      try
      {
        localCTML = localCTMLManager.getCTML(str3);
      }
      catch (Exception localException3)
      {
        this.sysLogger.info("后台证书发布操作异常 " + localException3.toString());
        return;
      }
      X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
      int k = 1;
      int m = 1;
      if (localX509V3CTML.isIssueToLDAP())
      {
        k = 0;
        try
        {
          localCertEntity = new CertEntity("LDAP");
          localCertEntity.setCertSN(str1);
          localCertEntity.setCertDN(str2);
          localCertEntity.setCertStatus(str4);
          localCertEntity.setCertType(str3);
          localCertEntity.setCertContent(arrayOfCertInfo[i].getCertEntity());
          String str5 = null;
          try
          {
            str5 = CAConfig.getInstance().getBaseDN();
          }
          catch (IDAException localIDAException)
          {
            this.sysLogger.info(localIDAException.getErrCode() + ":" + localIDAException.getErrDesc());
            this.sysLogger.info("后台发布证书到LDAP异常 [CertSN=" + str1 + "]");
          }
          localCertEntity.setBaseDN(str5);
          localIssuanceManager.issue(localCertEntity);
          k = 1;
        }
        catch (ISSException localISSException1)
        {
          this.sysLogger.info(localISSException1.getErrCode() + ":" + localISSException1.getErrDesc());
          this.sysLogger.info("后台发布证书到LDAP异常 [CertSN=" + str1 + "]");
        }
        catch (Exception localException4)
        {
          this.sysLogger.info(localException4.toString());
          this.sysLogger.info("后台发布证书到LDAP异常 [CertSN=" + str1 + "]");
        }
      }
      if (localX509V3CTML.isIssueToDisk())
      {
        m = 0;
        try
        {
          localCertEntity = new CertEntity("FILE");
          localCertEntity.setCertSN(str1);
          localCertEntity.setCertDN(str2);
          localCertEntity.setCertStatus(str4);
          localCertEntity.setCertType(str3);
          localCertEntity.setCertContent(arrayOfCertInfo[i].getCertEntity());
          localIssuanceManager.issue(localCertEntity);
          m = 1;
        }
        catch (ISSException localISSException2)
        {
          this.sysLogger.info(localISSException2.getErrCode() + ":" + localISSException2.getErrDesc());
          this.sysLogger.info("后台发布证书到磁盘异常 [CertSN=" + str1 + "]");
        }
        catch (Exception localException5)
        {
          this.sysLogger.info(localException5.toString());
          this.sysLogger.info("后台发布证书到磁盘异常 [CertSN=" + str1 + "]");
        }
      }
      if ((k == 0) || (m == 0))
        continue;
      localVector.add(str1);
    }
    if (localVector.size() > 0)
    {
      String[] arrayOfString = new String[localVector.size()];
      localVector.toArray(arrayOfString);
      try
      {
        int j = localDBManager.deleteCertTBP(arrayOfString);
        if (j != arrayOfString.length)
          throw new Exception("清理操作未完全完成");
      }
      catch (Exception localException2)
      {
        this.sysLogger.info("清理待发布证书记录失败 " + localException2.toString());
      }
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.iss.CertIss
 * JD-Core Version:    0.6.0
 */