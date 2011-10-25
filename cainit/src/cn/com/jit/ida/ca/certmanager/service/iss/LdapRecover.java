package cn.com.jit.ida.ca.certmanager.service.iss;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.issue.IssuanceManager;
import cn.com.jit.ida.ca.issue.entity.CAEntity;
import cn.com.jit.ida.ca.issue.entity.CertEntity;
import cn.com.jit.ida.ca.issue.entity.CrossCertPairEntity;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.certpair.CertPairGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class LdapRecover
{
  private DBManager dbManager = null;
  private SysLogger logger = LogManager.getSysLogger();
  private IssuanceManager issuanceManager = null;

  public LdapRecover()
  {
    try
    {
      this.dbManager = DBManager.getInstance();
      this.issuanceManager = IssuanceManager.getInstance();
    }
    catch (Exception localException)
    {
      this.logger.info("LDAP恢复操作失败:" + localException.toString());
    }
  }

  public String[] getCTMLNames()
    throws IDAException
  {
    CTMLManager.initializeInstance();
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    String[] arrayOfString1 = localCTMLManager.getCTMLList();
    ArrayList localArrayList = new ArrayList();
    X509V3CTML localX509V3CTML = null;
    for (int i = 0; i < arrayOfString1.length; i++)
    {
      localX509V3CTML = (X509V3CTML)localCTMLManager.getCTML(arrayOfString1[i]);
      if (!localX509V3CTML.isIssueToLDAP())
        continue;
      localArrayList.add(arrayOfString1[i]);
    }
    String[] arrayOfString2 = new String[localArrayList.size()];
    return (String[])localArrayList.toArray(arrayOfString2);
  }

  public long getCertNumber(String paramString)
    throws IDAException
  {
    return this.dbManager.getCertCountByCtmlNameCertStatus(paramString);
  }

  public void issUserCerts()
    throws IDAException
  {
    String str1 = null;
    try
    {
      str1 = CAConfig.getInstance().getBaseDN();
    }
    catch (IDAException localIDAException1)
    {
      throw localIDAException1;
    }
    try
    {
      int i = 500;
      String[] arrayOfString = getCTMLNames();
      for (int j = 0; j < arrayOfString.length; j++)
      {
        long l = this.dbManager.getCertCountByCtmlNameCertStatus(arrayOfString[j]);
        String str2 = "模板(" + arrayOfString[j] + ")共有 " + l + " 个待恢复证书";
        if (l > 0L)
          System.out.println(str2);
        int k = 1;
        while (k <= l)
        {
          CertInfo[] arrayOfCertInfo = this.dbManager.getCertsToPublish(arrayOfString[j], k, i);
          for (int m = 0; m < arrayOfCertInfo.length; m++)
          {
            CertEntity localCertEntity = new CertEntity("LDAP");
            localCertEntity.setBaseDN(str1);
            localCertEntity.setCertSN(arrayOfCertInfo[m].getCertSN());
            localCertEntity.setCertDN(arrayOfCertInfo[m].getSubject());
            localCertEntity.setCertContent(arrayOfCertInfo[m].getCertEntity());
            this.issuanceManager.issue(localCertEntity);
          }
          k += i;
        }
        if (l > 0L)
        {
          str2 = "模板(" + arrayOfString[j] + ")的证书恢复完成\n";
          System.out.println(str2);
        }
        str2 = null;
      }
    }
    catch (IDAException localIDAException2)
    {
      throw localIDAException2;
    }
  }

  public void issCARootCert()
    throws IDAException
  {
    try
    {
      CAConfig localCAConfig = null;
      try
      {
        localCAConfig = CAConfig.getInstance();
      }
      catch (IDAException localIDAException2)
      {
        throw new IDAException(localIDAException2.getErrCode(), localIDAException2.getErrDesc(), localIDAException2);
      }
      X509Cert localX509Cert = localCAConfig.getRootCert();
      String str1 = localX509Cert.getSubject();
      X509Cert[] arrayOfX509Cert = localCAConfig.getRootCerts();
      for (int i = 0; i < arrayOfX509Cert.length; i++)
      {
        CAEntity localCAEntity = new CAEntity("LDAP");
        String str2 = localCAConfig.getBaseDN();
        localCAEntity.setBaseDN(str2);
        localCAEntity.setCACertSN(arrayOfX509Cert[i].getStringSerialNumber());
        localCAEntity.setCASubject(arrayOfX509Cert[i].getSubject());
        if (arrayOfX509Cert[i].getSubject().equals(str1))
          localCAEntity.setCrossCertPairs(getCrossCert(localCAConfig));
        localCAEntity.setCACert(arrayOfX509Cert[i].getEncoded());
        this.issuanceManager.issue(localCAEntity);
      }
    }
    catch (IDAException localIDAException1)
    {
      throw localIDAException1;
    }
  }

  private Vector getCrossCert(CAConfig paramCAConfig)
  {
    X509Cert localX509Cert1 = paramCAConfig.getRootCert();
    String str = "./crosscert";
    File localFile1 = new File(str);
    if (!localFile1.exists())
      return null;
    String[] arrayOfString = localFile1.list();
    Vector localVector = new Vector();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      File localFile2 = new File(str + File.separator + arrayOfString[i]);
      if (!localFile2.isDirectory())
        continue;
      File[] arrayOfFile = localFile2.listFiles();
      Object localObject1 = null;
      Object localObject2 = null;
      Object localObject3;
      for (int j = 0; j < arrayOfFile.length; j++)
      {
        if (!arrayOfFile[j].isFile())
          continue;
        localObject3 = arrayOfFile[j].getName();
        if (!((String)localObject3).toLowerCase().endsWith(".cer"))
          continue;
        try
        {
          FileInputStream localFileInputStream = new FileInputStream(arrayOfFile[j].getAbsolutePath());
          byte[] arrayOfByte2 = new byte[localFileInputStream.available()];
          localFileInputStream.read(arrayOfByte2);
          localFileInputStream.close();
          X509Cert localX509Cert2 = null;
          try
          {
            localX509Cert2 = new X509Cert(arrayOfByte2);
          }
          catch (PKIException localPKIException)
          {
            continue;
          }
          if (localX509Cert2.getSubject().equalsIgnoreCase(localX509Cert1.getSubject()))
            localObject1 = localX509Cert2;
          else
            localObject2 = localX509Cert2;
        }
        catch (Exception localException2)
        {
          return null;
        }
      }
      if ((localObject1 == null) || (localObject2 == null))
        continue;
      if ((!localObject1.getIssuer().equals(localObject2.getSubject())) || (!localObject1.getSubject().equals(localObject2.getIssuer())))
        return null;
      byte[] arrayOfByte1 = null;
      try
      {
        localObject3 = new CertPairGenerator(localObject1, localObject2);
        arrayOfByte1 = ((CertPairGenerator)localObject3).generateCertPair();
        FileOutputStream localFileOutputStream = new FileOutputStream(localFile2 + File.separator + "crosscert.dat");
        localFileOutputStream.write(arrayOfByte1);
        localFileOutputStream.flush();
        localFileOutputStream.close();
      }
      catch (Exception localException1)
      {
        return null;
      }
      localVector.add(arrayOfByte1);
    }
    return (Vector)localVector;
  }

  public void issCrossCert()
    throws IDAException
  {
    CAConfig localCAConfig = null;
    try
    {
      localCAConfig = CAConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
      throw new IDAException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
    X509Cert localX509Cert1 = localCAConfig.getRootCert();
    String str = "./crosscert";
    File localFile1 = new File(str);
    if (!localFile1.exists())
      throw new IDAException("0991", "交叉证书目录: crosscert不存在");
    String[] arrayOfString = localFile1.list();
    Vector localVector = new Vector();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      File localFile2 = new File(str + File.separator + arrayOfString[i]);
      if (!localFile2.isDirectory())
        continue;
      File[] arrayOfFile = localFile2.listFiles();
      Object localObject1 = null;
      Object localObject2 = null;
      Object localObject3;
      for (int j = 0; j < arrayOfFile.length; j++)
      {
        if (!arrayOfFile[j].isFile())
          continue;
        localObject3 = arrayOfFile[j].getName();
        if (!((String)localObject3).toLowerCase().endsWith(".cer"))
          continue;
        try
        {
          FileInputStream localFileInputStream = new FileInputStream(arrayOfFile[j].getAbsolutePath());
          byte[] arrayOfByte2 = new byte[localFileInputStream.available()];
          localFileInputStream.read(arrayOfByte2);
          localFileInputStream.close();
          X509Cert localX509Cert2 = null;
          try
          {
            localX509Cert2 = new X509Cert(arrayOfByte2);
          }
          catch (PKIException localPKIException)
          {
            continue;
          }
          if (localX509Cert2.getSubject().equals(localX509Cert1.getSubject()))
            localObject1 = localX509Cert2;
          else
            localObject2 = localX509Cert2;
        }
        catch (Exception localException2)
        {
          throw new IDAException("0994", "读取CA证书失败");
        }
      }
      if ((localObject1 == null) && (localObject2 == null))
        continue;
      if ((!localObject1.getIssuer().equals(localObject2.getSubject())) || (!localObject1.getSubject().equals(localObject2.getIssuer())))
        throw new IDAException("0992", "交叉证书不匹配,路径为: " + localFile2.getAbsolutePath());
      byte[] arrayOfByte1 = null;
      try
      {
        localObject3 = new CertPairGenerator(localObject1, localObject2);
        arrayOfByte1 = ((CertPairGenerator)localObject3).generateCertPair();
        FileOutputStream localFileOutputStream = new FileOutputStream(localFile2 + File.separator + "crosscert.dat");
        localFileOutputStream.write(arrayOfByte1);
        localFileOutputStream.flush();
        localFileOutputStream.close();
      }
      catch (Exception localException1)
      {
        throw new IDAException("0993", "产生交叉证书失败", localException1);
      }
      localVector.add(arrayOfByte1);
    }
    CrossCertPairEntity localCrossCertPairEntity = new CrossCertPairEntity("LDAP");
    localCrossCertPairEntity.setCaSerialNumber(localX509Cert1.getStringSerialNumber());
    localCrossCertPairEntity.setCaSubject(localX509Cert1.getSubject());
    localCrossCertPairEntity.setCrossCertPairs(localVector);
    localCrossCertPairEntity.setBaseDN(localCAConfig.getBaseDN());
    this.issuanceManager.issue(localCrossCertPairEntity);
  }

  public static void main(String[] paramArrayOfString)
  {
    LdapRecover localLdapRecover = new LdapRecover();
    try
    {
      localLdapRecover.issUserCerts();
    }
    catch (IDAException localIDAException)
    {
      System.out.println(localIDAException.toString());
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.iss.LdapRecover
 * JD-Core Version:    0.6.0
 */