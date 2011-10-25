package cn.com.jit.ida.ca.certmanager.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertExtensions;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertRevokeInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.Extension;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CRLConfig;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.issue.IssuanceManager;
import cn.com.jit.ida.ca.issue.entity.BaseEntity;
import cn.com.jit.ida.ca.issue.entity.CertEntity;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.TemplateAdmin;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.asn1.ASN1Set;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import java.util.Properties;

public class AdminCertUPDDownOpt
{
  public static X509Cert certUPDDown(CertRevokeInfo paramCertRevokeInfo, CertInfo paramCertInfo)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    CertExtensions localCertExtensions = localDBManager.getCertExtensions(paramCertRevokeInfo.getCertSN());
    Properties localProperties = new Properties();
    Extension localExtension = null;
    for (int i = 0; i < localCertExtensions.getExtensionsCount(); i++)
    {
      localExtension = localCertExtensions.getExtension(i);
      localProperties.put(localExtension.getName(), localExtension.getValue());
    }
    paramCertInfo.setExtensions(localProperties);
    String str = paramCertInfo.getCtmlName();
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CTML localCTML = localCTMLManager.getCTML(str);
    X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
    byte[] arrayOfByte = localX509V3CTML.generateCertificate(paramCertInfo);
    paramCertInfo.setCertEntity(arrayOfByte);
    paramCertInfo.setCertStatus("Use");
    return doOperation(paramCertRevokeInfo, paramCertInfo);
  }

  public static X509Cert certUPDDown(CertRevokeInfo paramCertRevokeInfo, CertInfo paramCertInfo, ASN1Set paramASN1Set)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    CertExtensions localCertExtensions = localDBManager.getCertExtensions(paramCertRevokeInfo.getCertSN());
    Properties localProperties = new Properties();
    Extension localExtension = null;
    for (int i = 0; i < localCertExtensions.getExtensionsCount(); i++)
    {
      localExtension = localCertExtensions.getExtension(i);
      localProperties.put(localExtension.getName(), localExtension.getValue());
    }
    paramCertInfo.setExtensions(localProperties);
    String str = paramCertInfo.getCtmlName();
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CTML localCTML = localCTMLManager.getCTML(str);
    X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
    byte[] arrayOfByte = localX509V3CTML.generateCertificate(paramCertInfo);
    paramCertInfo.setCertEntity(arrayOfByte);
    paramCertInfo.setCertStatus("Use");
    return doOperation(paramCertRevokeInfo, paramCertInfo);
  }

  private static X509Cert doOperation(CertRevokeInfo paramCertRevokeInfo, CertInfo paramCertInfo)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    long l1 = localDBManager.getCertAccount();
    CRLConfig localCRLConfig = CRLConfig.getInstance();
    int i = localCRLConfig.getCertCount();
    long l2 = l1 / i + 1L;
    paramCertInfo.setCdpid(l2);
    CertExtensions localCertExtensions = localDBManager.getCertExtensions(paramCertRevokeInfo.getCertSN());
    paramCertInfo.setCertExtensions(localCertExtensions);
    int j = localDBManager.updAndDownAdminCert(paramCertRevokeInfo, paramCertInfo);
    if (j == -1)
      throw new IDAException("0608", "执行业务操作 数据库执行更新并下载操作无效");
    String str1 = paramCertRevokeInfo.getCertSN();
    String str2 = paramCertInfo.getCertSN();
    CAConfig localCAConfig = CAConfig.getInstance();
    if (str1.equalsIgnoreCase(localCAConfig.getCaAdminSN()))
      localCAConfig.setCaAdminSN(str2);
    if (str1.equalsIgnoreCase(localCAConfig.getCAAuditAdminSN()))
      localCAConfig.setCAAuditAdminSN(str2);
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CTML localCTML = localCTMLManager.getCTML(paramCertInfo.getCtmlName());
    X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
    int k = 0;
    IssuanceManager localIssuanceManager = null;
    try
    {
      localIssuanceManager = IssuanceManager.getInstance();
    }
    catch (Exception localException1)
    {
      throw new IDAException("0706", "其他错误 系统错误 构造发布管理器失败", localException1);
    }
    if (localX509V3CTML.isIssue())
    {
      localObject = null;
      if (localX509V3CTML.isIssueToLDAP())
      {
        localObject = new CertEntity("LDAP");
        String str3 = null;
        try
        {
          str3 = CAConfig.getInstance().getBaseDN();
        }
        catch (IDAException localIDAException)
        {
          throw new IDAException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
        }
        ((CertEntity)localObject).setBaseDN(str3);
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
          k = 1;
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
          if (k == 0)
            localDBManager.saveToCertTBP(paramCertInfo.getCertSN());
        }
      }
    }
    TemplateAdmin.getInstance().updateTemplateAdmin(paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
    Privilege.getInstance().updateAdmin(paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
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
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.operation.AdminCertUPDDownOpt
 * JD-Core Version:    0.6.0
 */