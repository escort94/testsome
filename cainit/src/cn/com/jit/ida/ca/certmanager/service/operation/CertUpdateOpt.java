package cn.com.jit.ida.ca.certmanager.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertExtensions;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertPendingRevokeInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertRevokeInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.Extension;
import cn.com.jit.ida.ca.config.CRLConfig;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.TemplateAdmin;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class CertUpdateOpt
{
  public static String[] updateCert(CertRevokeInfo paramCertRevokeInfo, CertInfo paramCertInfo, boolean paramBoolean)
    throws IDAException
  {
    String[] arrayOfString = CodeGenerator.generateCodes();
    paramCertInfo.setCertSN(arrayOfString[0]);
    paramCertInfo.setAuthCode(arrayOfString[1]);
    DBManager localDBManager = DBManager.getInstance();
    Vector localVector1 = paramCertInfo.getStandardExtensions();
    CertExtensions localCertExtensions1 = paramCertInfo.getCertExtensions();
    if ((localVector1 == null) && (localCertExtensions1 == null))
    {
      CertExtensions localCertExtensions2 = localDBManager.getCertExtensions(paramCertRevokeInfo.getCertSN());
      Properties localProperties = new Properties();
      localObject = null;
      for (int i = 0; i < localCertExtensions2.getExtensionsCount(); i++)
      {
        localObject = localCertExtensions2.getExtension(i);
        localProperties.put(((Extension)localObject).getName(), ((Extension)localObject).getValue());
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
          Vector localVector3 = (Vector)localEnumeration.nextElement();
          localVector2.addAll(localVector3);
        }
        paramCertInfo.setStandardExtensions(localVector2);
        paramCertInfo.setStandardExtensionsHT(localHashtable);
      }
    }
    long l = localDBManager.getCertAccount();
    Object localObject = CRLConfig.getInstance();
    int j = ((CRLConfig)localObject).getCertCount();
    int k = (int)(l / j + 1L);
    paramCertInfo.setCdpid(k);
    int m = localDBManager.updateCert(paramCertRevokeInfo, paramCertInfo);
    if (m != 1)
      throw new IDAException("0607", "执行业务操作 数据库更新证书无效");
    if (paramBoolean)
    {
      TemplateAdmin.getInstance().updateTemplateAdmin(paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
      Privilege.getInstance().updateAdmin(paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
      localDBManager.updateRABaseDN(paramCertRevokeInfo.getCertSN(), arrayOfString[0]);
    }
    return (String)arrayOfString;
  }

  public static String[] updatePendingCert(CertRevokeInfo paramCertRevokeInfo, CertInfo paramCertInfo, boolean paramBoolean)
    throws IDAException
  {
    String[] arrayOfString = CodeGenerator.generateCodes();
    paramCertInfo.setCertSN(arrayOfString[0]);
    paramCertInfo.setAuthCode(arrayOfString[1]);
    DBManager localDBManager = DBManager.getInstance();
    Vector localVector1 = paramCertInfo.getStandardExtensions();
    CertExtensions localCertExtensions1 = paramCertInfo.getCertExtensions();
    if ((localVector1 == null) && (localCertExtensions1 == null))
    {
      CertExtensions localCertExtensions2 = localDBManager.getCertExtensions(paramCertRevokeInfo.getCertSN());
      Properties localProperties = new Properties();
      localObject = null;
      for (int i = 0; i < localCertExtensions2.getExtensionsCount(); i++)
      {
        localObject = localCertExtensions2.getExtension(i);
        localProperties.put(((Extension)localObject).getName(), ((Extension)localObject).getValue());
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
          Vector localVector3 = (Vector)localEnumeration.nextElement();
          localVector2.addAll(localVector3);
        }
        paramCertInfo.setStandardExtensions(localVector2);
        paramCertInfo.setStandardExtensionsHT(localHashtable);
      }
    }
    long l = localDBManager.getCertAccount();
    Object localObject = CRLConfig.getInstance();
    int j = ((CRLConfig)localObject).getCertCount();
    int k = (int)(l / j + 1L);
    paramCertInfo.setCdpid(k);
    int m = localDBManager.updateCertPending(paramCertRevokeInfo, paramCertInfo);
    if (m != 1)
      throw new IDAException("0607", "执行业务操作 数据库更新证书无效");
    if (paramBoolean)
    {
      TemplateAdmin.getInstance().setPendingTemplateAdmin(paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
      Privilege.getInstance().setPendingAdmin(paramCertRevokeInfo.getCertSN(), paramCertInfo.getCertSN());
      localDBManager.updateRABaseDN(paramCertRevokeInfo.getCertSN(), arrayOfString[0]);
    }
    return (String)arrayOfString;
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
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.operation.CertUpdateOpt
 * JD-Core Version:    0.6.0
 */