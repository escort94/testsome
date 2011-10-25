package cn.com.jit.ida.ca.certmanager.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertExtensions;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.issue.IssuanceManager;
import cn.com.jit.ida.ca.issue.entity.BaseEntity;
import cn.com.jit.ida.ca.issue.entity.CertEntity;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.asn1.ASN1Set;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import java.util.Hashtable;

public class CertDownloadOpt
{
  public static X509Cert downloadCert(CertInfo paramCertInfo)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    CertExtensions localCertExtensions = localDBManager.getCertExtensions(paramCertInfo.getCertSN());
    paramCertInfo.setCertExtensions(localCertExtensions);
    String str = paramCertInfo.getCtmlName();
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CTML localCTML = localCTMLManager.getCTML(str);
    X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
    byte[] arrayOfByte = localX509V3CTML.generateCertificate(paramCertInfo);
    paramCertInfo.setCertEntity(arrayOfByte);
    paramCertInfo.setCertStatus("Use");
    return doOperation(paramCertInfo);
  }

  public static X509Cert downloadCert(CertInfo paramCertInfo, ASN1Set paramASN1Set)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    CertExtensions localCertExtensions = localDBManager.getCertExtensions(paramCertInfo.getCertSN());
    Hashtable localHashtable = localDBManager.getCertStandardExt(paramCertInfo.getCertSN());
    paramCertInfo.setCertExtensions(localCertExtensions);
    paramCertInfo.setStandardExtensionsHT(localHashtable);
    String str = paramCertInfo.getCtmlName();
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CTML localCTML = localCTMLManager.getCTML(str);
    X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
    byte[] arrayOfByte = localX509V3CTML.generateCertificate(paramCertInfo);
    paramCertInfo.setCertEntity(arrayOfByte);
    paramCertInfo.setCertStatus("Use");
    return doOperation(paramCertInfo);
  }

  private static X509Cert doOperation(CertInfo paramCertInfo)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    int i = localDBManager.saveCertEntity(paramCertInfo);
    if (i == -1)
      throw new IDAException("0605", "执行业务操作 数据库保存证书无效");
    IssuanceManager localIssuanceManager = null;
    try
    {
      localIssuanceManager = IssuanceManager.getInstance();
    }
    catch (Exception localException1)
    {
      throw new IDAException("0610", "执行业务操作 构造发布管理器失败", localException1);
    }
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CTML localCTML = localCTMLManager.getCTML(paramCertInfo.getCtmlName());
    X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
    int j = 0;
    if (localX509V3CTML.isIssue())
    {
      localObject = null;
      if (localX509V3CTML.isIssueToLDAP())
      {
        localObject = new CertEntity("LDAP");
        String str = null;
        try
        {
          str = CAConfig.getInstance().getBaseDN();
        }
        catch (IDAException localIDAException)
        {
          throw localIDAException;
        }
        ((CertEntity)localObject).setBaseDN(str);
        ((CertEntity)localObject).setCertSN(paramCertInfo.getCertSN());
        ((CertEntity)localObject).setCertDN(paramCertInfo.getSubject());
        ((CertEntity)localObject).setCertType(paramCertInfo.getCtmlName());
        ((CertEntity)localObject).setCertStatus("Use");
        ((CertEntity)localObject).setCertContent(paramCertInfo.getCertEntity());
        try
        {
          localIssuanceManager.issue((BaseEntity)localObject);
        }
        catch (Exception localException3)
        {
          localDBManager.saveToCertTBP(paramCertInfo.getCertSN());
          j = 1;
        }
      }
      if (localX509V3CTML.isIssueToDisk())
      {
        localObject = new CertEntity("FILE");
        ((CertEntity)localObject).setCertSN(paramCertInfo.getCertSN());
        ((CertEntity)localObject).setCertDN(paramCertInfo.getSubject());
        ((CertEntity)localObject).setCertType(paramCertInfo.getCtmlName());
        ((CertEntity)localObject).setCertStatus("Use");
        ((CertEntity)localObject).setCertContent(paramCertInfo.getCertEntity());
        try
        {
          localIssuanceManager.issue((BaseEntity)localObject);
        }
        catch (Exception localException2)
        {
          if (j == 0)
            localDBManager.saveToCertTBP(paramCertInfo.getCertSN());
        }
      }
    }
    Object localObject = null;
    try
    {
      localObject = new X509Cert(paramCertInfo.getCertEntity());
    }
    catch (PKIException localPKIException)
    {
      throw new IDAException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException.getHistory());
    }
    return (X509Cert)localObject;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.operation.CertDownloadOpt
 * JD-Core Version:    0.6.0
 */