package cn.com.jit.ida.ca.certmanager.service.iss;

import cn.com.jit.ida.ca.certmanager.reqinfo.CRLInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertRevokeInfo;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CRLConfig;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.issue.ISSException;
import cn.com.jit.ida.ca.issue.IssuanceManager;
import cn.com.jit.ida.ca.issue.entity.BaseEntity;
import cn.com.jit.ida.ca.issue.entity.CRLEntity;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;
import cn.com.jit.ida.util.pki.asn1.x509.X509Name;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.JKey;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.crl.X509CRLGenerator;
import cn.com.jit.ida.util.pki.encoders.Base64;
import cn.com.jit.ida.util.pki.extension.AuthorityKeyIdentifierExt;
import cn.com.jit.ida.util.pki.extension.CRLNumberExt;
import cn.com.jit.ida.util.pki.extension.Extension;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CRLIss
{
  private SysLogger sysLogger = LogManager.getSysLogger();

  public void issCRL()
  {
    executeIssCrl(null);
  }

  private void executeIssCrl(String paramString)
  {
    int i = 1;
    JCrypto localJCrypto = JCrypto.getInstance();
    DBManager localDBManager = null;
    Session localSession = null;
    JKey localJKey1 = null;
    JKey localJKey2 = null;
    String[][] arrayOfString = (String[][])null;
    X509Name localX509Name = null;
    long l1 = 0L;
    long l2 = 0L;
    String str1 = null;
    boolean bool = false;
    IssuanceManager localIssuanceManager = null;
    try
    {
      localIssuanceManager = IssuanceManager.getInstance();
    }
    catch (Exception localException1)
    {
      this.sysLogger.info("构造发布管理器失败 " + localException1.toString());
    }
    try
    {
      localDBManager = DBManager.getInstance();
      CryptoConfig localCryptoConfig = CryptoConfig.getInstance();
      localSession = localJCrypto.openSession(localCryptoConfig.getDeviceID());
      CRLConfig localCRLConfig = CRLConfig.getInstance();
      arrayOfString = localCRLConfig.getCRLPubAddressForService();
      l1 = localDBManager.getMaxCDPID();
      if (paramString != null)
      {
        l2 = Long.parseLong(paramString) * 1000L * 60L;
      }
      else
      {
        l2 = localCRLConfig.getPeriods();
        if (l2 <= 0L)
          l2 = 86400000L;
      }
      bool = localCRLConfig.getPUBALLCRL();
      localObject1 = CAConfig.getInstance();
      localJKey2 = ((CAConfig)localObject1).getPrivateKey();
      localX509Name = ((CAConfig)localObject1).getRootCert().getX509NameSubject();
      X509Cert localX509Cert = ((CAConfig)localObject1).getRootCert();
      localJKey1 = localX509Cert.getPublicKey();
      str1 = ((CAConfig)localObject1).getBaseDN();
    }
    catch (Exception localException2)
    {
      this.sysLogger.info("发布CRL操作异常 " + localException2.toString());
      return;
    }
    int j = 0;
    int k = 0;
    Object localObject1 = null;
    for (int m = 0; m < arrayOfString.length; m++)
    {
      if (arrayOfString[m][0].equals("URI"))
        k = 1;
      if (!arrayOfString[m][0].equals("DN"))
        continue;
      j = 1;
      localObject1 = arrayOfString[m][1];
    }
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    int n = 0;
    CRLInfo[] arrayOfCRLInfo = null;
    int i1 = 0;
    try
    {
      i1 = localDBManager.deleteRevokedCertByNotafter();
    }
    catch (DBException localDBException1)
    {
      System.err.println(localDBException1.toString());
    }
    Object localObject2;
    Object localObject3;
    Object localObject4;
    long l9;
    Object localObject6;
    Object localObject5;
    if (bool)
    {
      arrayOfCRLInfo = new CRLInfo[(int)l1 + 1];
      n++;
      try
      {
        X509CRLGenerator localX509CRLGenerator = new X509CRLGenerator();
        CertRevokeInfo[] arrayOfCertRevokeInfo = localDBManager.getRevokedCerts();
        localObject2 = null;
        localObject3 = new CRLNumberExt();
        ((CRLNumberExt)localObject3).SetCRLNumber(0);
        localObject4 = new AuthorityKeyIdentifierExt(localJKey1);
        for (int i2 = 0; i2 < arrayOfCertRevokeInfo.length; i2++)
        {
          String str2 = arrayOfCertRevokeInfo[i2].getCertSN();
          String str3 = Long.toString(arrayOfCertRevokeInfo[i2].getRevokeTime());
          Date localDate1 = localSimpleDateFormat.parse(str3);
          int i6 = arrayOfCertRevokeInfo[i2].getReasonID();
          localX509CRLGenerator.setIssuer(localX509Name);
          localX509CRLGenerator.setThisUpdate(new Date());
          long l8 = System.currentTimeMillis();
          l9 = l8 + l2;
          Date localDate4 = new Date(l9);
          localX509CRLGenerator.setNextUpdate(localDate4);
          localX509CRLGenerator.setSignatureAlg("SHA1withRSAEncryption");
          localX509CRLGenerator.addRevokeCert(str2, localDate1, i6);
          localX509CRLGenerator.addExtension((Extension)localObject3);
          localX509CRLGenerator.addExtension((Extension)localObject4);
          localObject2 = localX509CRLGenerator.generateCRL(localJKey2, localSession);
        }
        if (localObject2 == null)
        {
          localX509CRLGenerator.setIssuer(localX509Name);
          localX509CRLGenerator.setThisUpdate(new Date());
          long l4 = System.currentTimeMillis();
          long l5 = l4 + l2;
          localObject6 = new Date(l5);
          localX509CRLGenerator.setNextUpdate((Date)localObject6);
          localX509CRLGenerator.setSignatureAlg("SHA1withRSAEncryption");
          localX509CRLGenerator.addExtension((Extension)localObject3);
          localX509CRLGenerator.addExtension((Extension)localObject4);
          localObject2 = localX509CRLGenerator.generateCRL(localJKey2, localSession);
        }
        if (k != 0)
        {
          localObject5 = new CRLEntity("FILE");
          ((CRLEntity)localObject5).setCrlContent(localObject2);
          try
          {
            localIssuanceManager.issue((BaseEntity)localObject5);
          }
          catch (ISSException localISSException1)
          {
            i = 0;
            this.sysLogger.info(localISSException1.getErrCode() + ":" + localISSException1.getErrDesc());
            this.sysLogger.info("发布CRL到磁盘异常 AllCRL.");
          }
          catch (Exception localException4)
          {
            i = 0;
            this.sysLogger.info(localException4.toString());
            this.sysLogger.info("发布CRL到磁盘异常 AllCRL.");
          }
        }
        if (j != 0)
        {
          localObject5 = new CRLEntity("LDAP");
          ((CRLEntity)localObject5).setBaseDN(str1);
          int i3 = ((String)localObject1).indexOf(str1);
          String str4 = ((String)localObject1).substring(0, i3 - 1);
          ((CRLEntity)localObject5).setIssuerRelativePath(str4);
          ((CRLEntity)localObject5).setCrlContent(localObject2);
          try
          {
            localIssuanceManager.issue((BaseEntity)localObject5);
          }
          catch (ISSException localISSException2)
          {
            i = 0;
            this.sysLogger.info(localISSException2.getErrCode() + ":" + localISSException2.getErrDesc());
            this.sysLogger.info("发布CRL到LDAP异常 ALLCRL.");
          }
          catch (Exception localException6)
          {
            i = 0;
            this.sysLogger.info(localException6.toString());
            this.sysLogger.info("发布CRL到LDAP异常 ALLCRL.");
          }
        }
        arrayOfCRLInfo[0] = new CRLInfo();
        arrayOfCRLInfo[0].setCRLName("crl");
        arrayOfCRLInfo[0].setCRLEntity(new String(Base64.encode(localObject2)));
      }
      catch (Exception localException3)
      {
        this.sysLogger.info("发布CRL操作异常 " + localException3.toString());
      }
    }
    else
    {
      arrayOfCRLInfo = new CRLInfo[(int)l1];
      n = 0;
    }
    long l3 = 1L;
    while (l3 <= l1)
    {
      localObject2 = null;
      localObject3 = null;
      localObject4 = new X509CRLGenerator();
      localObject5 = new CRLNumberExt();
      ((CRLNumberExt)localObject5).SetCRLNumber(Long.toString(l3));
      AuthorityKeyIdentifierExt localAuthorityKeyIdentifierExt = new AuthorityKeyIdentifierExt(localJKey1);
      try
      {
        localObject2 = localDBManager.getRevokedCerts(l3);
        for (int i4 = 0; i4 < localObject2.length; i4++)
        {
          String str5 = localObject2[i4].getCertSN();
          localObject6 = Long.toString(localObject2[i4].getRevokeTime());
          Date localDate2 = localSimpleDateFormat.parse((String)localObject6);
          int i7 = localObject2[i4].getReasonID();
          ((X509CRLGenerator)localObject4).setIssuer(localX509Name);
          ((X509CRLGenerator)localObject4).setThisUpdate(new Date());
          l9 = System.currentTimeMillis();
          long l10 = l9 + l2;
          Date localDate5 = new Date(l10);
          ((X509CRLGenerator)localObject4).setNextUpdate(localDate5);
          ((X509CRLGenerator)localObject4).setSignatureAlg("SHA1withRSAEncryption");
          ((X509CRLGenerator)localObject4).addRevokeCert(str5, localDate2, i7);
          ((X509CRLGenerator)localObject4).addExtension((Extension)localObject5);
          ((X509CRLGenerator)localObject4).addExtension(localAuthorityKeyIdentifierExt);
          localObject3 = ((X509CRLGenerator)localObject4).generateCRL(localJKey2, localSession);
        }
        if (localObject3 == null)
        {
          ((X509CRLGenerator)localObject4).setIssuer(localX509Name);
          ((X509CRLGenerator)localObject4).setThisUpdate(new Date());
          long l6 = System.currentTimeMillis();
          long l7 = l6 + l2;
          Date localDate3 = new Date(l7);
          ((X509CRLGenerator)localObject4).setNextUpdate(localDate3);
          ((X509CRLGenerator)localObject4).setSignatureAlg("SHA1withRSAEncryption");
          ((X509CRLGenerator)localObject4).addExtension((Extension)localObject5);
          ((X509CRLGenerator)localObject4).addExtension(localAuthorityKeyIdentifierExt);
          localObject3 = ((X509CRLGenerator)localObject4).generateCRL(localJKey2, localSession);
        }
      }
      catch (Exception localException5)
      {
        this.sysLogger.info("发布CRL操作异常 " + localException5.toString());
      }
      CRLEntity localCRLEntity;
      if (k != 0)
      {
        localCRLEntity = new CRLEntity("FILE");
        localCRLEntity.setCdpid(l3);
        localCRLEntity.setCrlContent(localObject3);
        try
        {
          localIssuanceManager.issue(localCRLEntity);
        }
        catch (ISSException localISSException3)
        {
          i = 0;
          this.sysLogger.info(localISSException3.getErrCode() + ":" + localISSException3.getErrDesc());
          this.sysLogger.info("发布CRL到磁盘异常 [cdpid=" + l3 + "] ");
        }
        catch (Exception localException7)
        {
          i = 0;
          this.sysLogger.info(localException7.toString());
          this.sysLogger.info("发布CRL到磁盘异常 [cdpid=" + l3 + "] ");
        }
      }
      if (j != 0)
      {
        localCRLEntity = new CRLEntity("LDAP");
        localCRLEntity.setBaseDN(str1);
        localCRLEntity.setCdpid(l3);
        int i5 = ((String)localObject1).indexOf(str1);
        String str6 = ((String)localObject1).substring(0, i5 - 1);
        localCRLEntity.setIssuerRelativePath(str6);
        localCRLEntity.setCrlContent(localObject3);
        try
        {
          localIssuanceManager.issue(localCRLEntity);
        }
        catch (ISSException localISSException4)
        {
          i = 0;
          this.sysLogger.info(localISSException4.getErrCode() + ":" + localISSException4.getErrDesc());
          this.sysLogger.info("发布CRL到LDAP异常 [cdpid=" + l3 + "]");
        }
        catch (Exception localException8)
        {
          i = 0;
          this.sysLogger.info(localException8.toString());
          this.sysLogger.info("发布CRL到LDAP异常 [cdpid=" + l3 + "]");
        }
      }
      arrayOfCRLInfo[n] = new CRLInfo();
      arrayOfCRLInfo[n].setCRLName("crl" + l3);
      arrayOfCRLInfo[n].setCRLEntity(new String(Base64.encode(localObject3)));
      n++;
      l3 += 1L;
    }
    try
    {
      localDBManager.saveCRL(arrayOfCRLInfo);
    }
    catch (DBException localDBException2)
    {
      System.out.println("save CRLInfos to DB failture: " + localDBException2.toString());
    }
    if (i != 0)
      this.sysLogger.info("发布CRL操作完成");
  }

  public void issCRL(String paramString)
  {
    executeIssCrl(paramString);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.iss.CRLIss
 * JD-Core Version:    0.6.0
 */