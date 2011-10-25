package cn.com.jit.ida.ca.certmanager.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertExtensions;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertPendingRevokeInfo;
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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class CertUPDDownOpt
{
  public static X509Cert certUPDDown(CertRevokeInfo paramCertRevokeInfo, CertInfo paramCertInfo, boolean paramBoolean)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    Vector localVector1 = paramCertInfo.getStandardExtensions();
    CertExtensions localCertExtensions1 = paramCertInfo.getCertExtensions();
    if ((localVector1 == null) && (localCertExtensions1 == null))
    {
      CertExtensions localCertExtensions2 = localDBManager.getCertExtensions(paramCertRevokeInfo.getCertSN());
      Properties localProperties = new Properties();
      localObject1 = null;
      for (int i = 0; i < localCertExtensions2.getExtensionsCount(); i++)
      {
        localObject1 = localCertExtensions2.getExtension(i);
        localProperties.put(((Extension)localObject1).getName(), ((Extension)localObject1).getValue());
      }
      paramCertInfo.setExtensions(localProperties);
      paramCertInfo.setCertExtensions(localCertExtensions2);
      Hashtable localHashtable = localDBManager.getCertStandardExt(paramCertRevokeInfo.getCertSN());
      if (localHashtable != null)
      {
        Enumeration localEnumeration = localHashtable.elements();
        Vector localVector2 = new Vector();
        while (localEnumeration.hasMoreElements())
        {
          localObject2 = (Vector)localEnumeration.nextElement();
          localVector2.addAll((Collection)localObject2);
        }
        paramCertInfo.setStandardExtensionsHT(localHashtable);
        paramCertInfo.setStandardExtensions(localVector2);
      }
    }
    long l1 = localDBManager.getCertAccount();
    Object localObject1 = CRLConfig.getInstance();
    int j = ((CRLConfig)localObject1).getCertCount();
    long l2 = l1 / j + 1L;
    paramCertInfo.setCdpid(l2);
    Object localObject2 = paramCertInfo.getCtmlName();
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CTML localCTML = localCTMLManager.getCTML((String)localObject2);
    X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
    byte[] arrayOfByte = localX509V3CTML.generateCertificate(paramCertInfo);
    paramCertInfo.setCertEntity(arrayOfByte);
    paramCertInfo.setCertStatus("Use");
    return (X509Cert)(X509Cert)doOperation(paramCertRevokeInfo, paramCertInfo, paramBoolean);
  }

  public static X509Cert certUPDDown(CertRevokeInfo paramCertRevokeInfo, CertInfo paramCertInfo, ASN1Set paramASN1Set, boolean paramBoolean)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    Vector localVector1 = paramCertInfo.getStandardExtensions();
    CertExtensions localCertExtensions1 = paramCertInfo.getCertExtensions();
    if ((localVector1 == null) && (localCertExtensions1 == null))
    {
      CertExtensions localCertExtensions2 = localDBManager.getCertExtensions(paramCertRevokeInfo.getCertSN());
      Properties localProperties = new Properties();
      localObject1 = null;
      for (int i = 0; i < localCertExtensions2.getExtensionsCount(); i++)
      {
        localObject1 = localCertExtensions2.getExtension(i);
        localProperties.put(((Extension)localObject1).getName(), ((Extension)localObject1).getValue());
      }
      paramCertInfo.setExtensions(localProperties);
      paramCertInfo.setCertExtensions(localCertExtensions2);
      Hashtable localHashtable = localDBManager.getCertStandardExt(paramCertRevokeInfo.getCertSN());
      if (localHashtable != null)
      {
        Enumeration localEnumeration = localHashtable.elements();
        Vector localVector2 = new Vector();
        while (localEnumeration.hasMoreElements())
        {
          localObject2 = (Vector)localEnumeration.nextElement();
          localVector2.addAll((Collection)localObject2);
        }
        paramCertInfo.setStandardExtensions(localVector2);
        paramCertInfo.setStandardExtensionsHT(localHashtable);
      }
    }
    long l1 = localDBManager.getCertAccount();
    Object localObject1 = CRLConfig.getInstance();
    int j = ((CRLConfig)localObject1).getCertCount();
    long l2 = l1 / j + 1L;
    paramCertInfo.setCdpid(l2);
    Object localObject2 = paramCertInfo.getCtmlName();
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CTML localCTML = localCTMLManager.getCTML((String)localObject2);
    X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
    byte[] arrayOfByte = localX509V3CTML.generateCertificate(paramCertInfo);
    paramCertInfo.setCertEntity(arrayOfByte);
    paramCertInfo.setCertStatus("Use");
    return (X509Cert)(X509Cert)doOperation(paramCertRevokeInfo, paramCertInfo, paramBoolean);
  }

  private static X509Cert doOperation(CertRevokeInfo paramCertRevokeInfo, CertInfo paramCertInfo, boolean paramBoolean)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    int i = localDBManager.updAndDownCert(paramCertRevokeInfo, paramCertInfo);
    if (i == -1)
      throw new IDAException("0608", "执行业务操作 数据库执行更新并下载操作无效");
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
    if (paramBoolean)
    {
      TemplateAdmin.getInstance().updateTemplateAdmin(paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
      Privilege.getInstance().updateAdmin(paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
      localDBManager.updateRABaseDN(paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
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

  public static void saveCertPendingTask(CertPendingRevokeInfo paramCertPendingRevokeInfo)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    localDBManager.saveCertPendingTask(paramCertPendingRevokeInfo);
  }

  public static void modifyCertPendingStatus(String paramString)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    localDBManager.modifyCertPendingStatus(paramString);
  }

  public static X509Cert certPendingUPDDown(CertRevokeInfo paramCertRevokeInfo, CertInfo paramCertInfo, boolean paramBoolean)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    Vector localVector1 = paramCertInfo.getStandardExtensions();
    CertExtensions localCertExtensions1 = paramCertInfo.getCertExtensions();
    if ((localVector1 == null) && (localCertExtensions1 == null))
    {
      CertExtensions localCertExtensions2 = localDBManager.getCertExtensions(paramCertRevokeInfo.getCertSN());
      Properties localProperties = new Properties();
      localObject1 = null;
      for (int i = 0; i < localCertExtensions2.getExtensionsCount(); i++)
      {
        localObject1 = localCertExtensions2.getExtension(i);
        localProperties.put(((Extension)localObject1).getName(), ((Extension)localObject1).getValue());
      }
      paramCertInfo.setExtensions(localProperties);
      paramCertInfo.setCertExtensions(localCertExtensions2);
      Hashtable localHashtable = localDBManager.getCertStandardExt(paramCertRevokeInfo.getCertSN());
      if (localHashtable != null)
      {
        Enumeration localEnumeration = localHashtable.elements();
        Vector localVector2 = new Vector();
        while (localEnumeration.hasMoreElements())
        {
          localObject2 = (Vector)localEnumeration.nextElement();
          localVector2.addAll((Collection)localObject2);
        }
        paramCertInfo.setStandardExtensions(localVector2);
        paramCertInfo.setStandardExtensionsHT(localHashtable);
      }
    }
    long l1 = localDBManager.getCertAccount();
    Object localObject1 = CRLConfig.getInstance();
    int j = ((CRLConfig)localObject1).getCertCount();
    long l2 = l1 / j + 1L;
    paramCertInfo.setCdpid(l2);
    Object localObject2 = paramCertInfo.getCtmlName();
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CTML localCTML = localCTMLManager.getCTML((String)localObject2);
    X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
    byte[] arrayOfByte = localX509V3CTML.generateCertificate(paramCertInfo);
    paramCertInfo.setCertEntity(arrayOfByte);
    paramCertInfo.setCertStatus("Use");
    return (X509Cert)(X509Cert)doPendingOperation(paramCertRevokeInfo, paramCertInfo, paramBoolean);
  }

  private static X509Cert doPendingOperation(CertRevokeInfo paramCertRevokeInfo, CertInfo paramCertInfo, boolean paramBoolean)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    int i = localDBManager.updAndDownCertPending(paramCertRevokeInfo, paramCertInfo);
    if (i == -1)
      throw new IDAException("0608", "执行业务操作 数据库执行更新并下载操作无效");
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
        catch (Exception localException3)
        {
          if (j == 0)
            localDBManager.saveToCertTBP(paramCertInfo.getCertSN());
        }
      }
    }
    if (paramBoolean)
    {
      TemplateAdmin.getInstance().setPendingTemplateAdmin(paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
      Privilege.getInstance().setPendingAdmin(paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
      localDBManager.updateRABaseDN(paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
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

  public static X509Cert certPendingUPDDown(CertRevokeInfo paramCertRevokeInfo, CertInfo paramCertInfo, ASN1Set paramASN1Set, boolean paramBoolean)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    Vector localVector1 = paramCertInfo.getStandardExtensions();
    CertExtensions localCertExtensions1 = paramCertInfo.getCertExtensions();
    if ((localVector1 == null) && (localCertExtensions1 == null))
    {
      CertExtensions localCertExtensions2 = localDBManager.getCertExtensions(paramCertRevokeInfo.getCertSN());
      Properties localProperties = new Properties();
      localObject1 = null;
      for (int i = 0; i < localCertExtensions2.getExtensionsCount(); i++)
      {
        localObject1 = localCertExtensions2.getExtension(i);
        localProperties.put(((Extension)localObject1).getName(), ((Extension)localObject1).getValue());
      }
      paramCertInfo.setExtensions(localProperties);
      paramCertInfo.setCertExtensions(localCertExtensions2);
      Hashtable localHashtable = localDBManager.getCertStandardExt(paramCertRevokeInfo.getCertSN());
      if (localHashtable != null)
      {
        Enumeration localEnumeration = localHashtable.elements();
        Vector localVector2 = new Vector();
        while (localEnumeration.hasMoreElements())
        {
          localObject2 = (Vector)localEnumeration.nextElement();
          localVector2.addAll((Collection)localObject2);
        }
        paramCertInfo.setStandardExtensions(localVector2);
        paramCertInfo.setStandardExtensionsHT(localHashtable);
      }
    }
    long l1 = localDBManager.getCertAccount();
    Object localObject1 = CRLConfig.getInstance();
    int j = ((CRLConfig)localObject1).getCertCount();
    long l2 = l1 / j + 1L;
    paramCertInfo.setCdpid(l2);
    Object localObject2 = paramCertInfo.getCtmlName();
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CTML localCTML = localCTMLManager.getCTML((String)localObject2);
    X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
    byte[] arrayOfByte = localX509V3CTML.generateCertificate(paramCertInfo);
    paramCertInfo.setCertEntity(arrayOfByte);
    paramCertInfo.setCertStatus("Use");
    return (X509Cert)(X509Cert)doPendingOperation(paramCertRevokeInfo, paramCertInfo, paramBoolean);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.operation.CertUPDDownOpt
 * JD-Core Version:    0.6.0
 */