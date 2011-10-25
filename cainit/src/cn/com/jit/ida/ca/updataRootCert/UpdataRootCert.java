package cn.com.jit.ida.ca.updataRootCert;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.service.operation.CodeGenerator;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CRLConfig;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.initserver.CACertInfo;
import cn.com.jit.ida.ca.initserver.DBInit;
import cn.com.jit.ida.ca.issue.IssuanceManager;
import cn.com.jit.ida.ca.issue.entity.BaseEntity;
import cn.com.jit.ida.ca.issue.entity.CAEntity;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigFromDB;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.globalconfig.ProtectConfig;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.Parser;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cert.X509CertGenerator;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.JKey;
import cn.com.jit.ida.util.pki.cipher.JKeyPair;
import cn.com.jit.ida.util.pki.cipher.Mechanism;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.cipher.param.GenKeyAttribute;
import cn.com.jit.ida.util.pki.encoders.Base64;
import cn.com.jit.ida.util.pki.extension.AuthorityKeyIdentifierExt;
import cn.com.jit.ida.util.pki.extension.BasicConstraintsExt;
import cn.com.jit.ida.util.pki.extension.CRLDistPointExt;
import cn.com.jit.ida.util.pki.extension.CertificatePoliciesExt;
import cn.com.jit.ida.util.pki.extension.DistributionPointExt;
import cn.com.jit.ida.util.pki.extension.Extension;
import cn.com.jit.ida.util.pki.extension.KeyUsageExt;
import cn.com.jit.ida.util.pki.extension.PolicyInformationExt;
import cn.com.jit.ida.util.pki.extension.SubjectKeyIdentifierExt;
import cn.com.jit.ida.util.pki.pkcs.P7B;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

public class UpdataRootCert
{
  X509Cert newCert;
  X509Cert O_NCert;
  X509Cert N_OCert;
  JKeyPair oldKeyPair;
  JKeyPair newKeyPair;
  GlobalConfig globalConfig;
  Session session;
  int newKeyID = 0;
  int validity = 0;

  private boolean readInitInfo()
  {
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
    try
    {
      int i = 0;
      Date localDate = this.globalConfig.getCAConfig().getRootCert().getNotAfter();
      i = (int)((localDate.getTime() - new Date().getTime()) / 86400000L);
      if (i <= 0)
        i = 1;
      i++;
      String str1 = ConfigTool.getInteger("请输入CA证书有效期，单位为天", 1825000, i, "天");
      if (str1 == null)
        return false;
      this.validity = Integer.decode(str1).intValue();
      if (this.globalConfig.getCryptoConfig().getDeviceID().equalsIgnoreCase("JSJY05B_LIB"))
      {
        String str2 = ConfigTool.getInteger("请选择密钥对标识(>0)", 100, 1, "");
        if (str2 == null)
          return false;
        this.newKeyID = Integer.decode(str2).intValue();
        String str3 = this.globalConfig.getCryptoConfig().getCommDeviceID();
        if (str3.equals("JSJY05B_LIB"))
        {
          int j = this.globalConfig.getCryptoConfig().getCommKeyID();
          if (this.newKeyID == j)
          {
            System.out.println("不允许CA签名证书和通信证书使用同一加密设备的同一密钥，请重新配置");
            return false;
          }
        }
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      return false;
    }
    return true;
  }

  public boolean run()
    throws UpdataRootException
  {
    try
    {
      this.globalConfig = GlobalConfig.getInstance();
    }
    catch (IDAException localIDAException1)
    {
      throw new UpdataRootException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1);
    }
    JCrypto localJCrypto = JCrypto.getInstance();
    try
    {
      this.session = localJCrypto.openSession(this.globalConfig.getCryptoConfig().getDeviceID());
    }
    catch (PKIException localPKIException1)
    {
      throw new UpdataRootException(localPKIException1.getErrCode(), localPKIException1.getErrDesc(), localPKIException1);
    }
    if (!readInitInfo())
      return false;
    try
    {
      this.oldKeyPair = new JKeyPair(this.globalConfig.getCAConfig().getRootCert().getPublicKey(), this.globalConfig.getCAConfig().getPrivateKey());
    }
    catch (PKIException localPKIException2)
    {
      throw new UpdataRootException(localPKIException2.getErrCode(), localPKIException2.getErrDesc(), localPKIException2);
    }
    GenNewKeyPair();
    generateAllCert();
    saveCert(this.newCert, this.newKeyPair.getPrivateKey(), "Useing", this.globalConfig.getCryptoConfig().getDeviceID());
    saveCert(this.O_NCert, this.newKeyPair.getPrivateKey(), "Useing", this.globalConfig.getCryptoConfig().getDeviceID());
    saveCert(this.N_OCert, this.oldKeyPair.getPrivateKey(), "Useing", this.globalConfig.getCryptoConfig().getDeviceID());
    GenKeyStore();
    SavaRootCert();
    revokeCert();
    String str1 = this.globalConfig.getCryptoConfig().getRootCert();
    File localFile = new File(str1);
    str1 = localFile.getParent();
    if (str1 == null)
      str1 = ".";
    saveCertToFile(this.newCert, str1 + "/new.cer");
    saveCertToFile(this.O_NCert, str1 + "/O_N.cer");
    saveCertToFile(this.N_OCert, str1 + "/N_O.cer");
    int i = 0;
    Object localObject;
    try
    {
      ParseXML localParseXML = new ParseXML("./config/CAConfig.xml");
      localObject = localParseXML.getString("RootCertLDAPPublish");
      if ((localObject != null) && (((String)localObject).equalsIgnoreCase("true")))
        i = 1;
    }
    catch (ConfigException localConfigException)
    {
      throw new UpdataRootException(localConfigException.getErrCode(), localConfigException.getErrDesc(), localConfigException.getHistory());
    }
    if (!LogManager.isInitialized())
      LogManager.init();
    SysLogger localSysLogger = LogManager.getSysLogger();
    if (i != 0)
    {
      localObject = new CAEntity("LDAP");
      String str2 = null;
      try
      {
        str2 = CAConfig.getInstance().getBaseDN();
      }
      catch (IDAException localIDAException2)
      {
        throw new UpdataRootException(localIDAException2.getErrCode(), localIDAException2.getErrDesc(), localIDAException2);
      }
      ((CAEntity)localObject).setBaseDN(str2);
      IssuanceManager localIssuanceManager = null;
      try
      {
        localIssuanceManager = IssuanceManager.getInstance();
      }
      catch (Exception localException1)
      {
        throw new UpdataRootException("0899", "系统错误 构造发布管理器失败", localException1);
      }
      try
      {
        ((CAEntity)localObject).setCACert(this.newCert.getEncoded());
        ((CAEntity)localObject).setCACertSN(this.newCert.getStringSerialNumber());
        ((CAEntity)localObject).setCASubject(this.newCert.getSubject());
        localIssuanceManager.issue((BaseEntity)localObject);
      }
      catch (Exception localException2)
      {
        localSysLogger.info("更新根证书操作:发布new.cer到目录服务器失败");
      }
      try
      {
        ((CAEntity)localObject).setCACert(this.O_NCert.getEncoded());
        ((CAEntity)localObject).setCACertSN(this.O_NCert.getStringSerialNumber());
        ((CAEntity)localObject).setCASubject(this.O_NCert.getSubject());
        localIssuanceManager.issue((BaseEntity)localObject);
      }
      catch (Exception localException3)
      {
        localSysLogger.info("更新根证书操作:发布O_N.cer到目录服务器失败");
      }
      try
      {
        ((CAEntity)localObject).setCACert(this.N_OCert.getEncoded());
        ((CAEntity)localObject).setCACertSN(this.N_OCert.getStringSerialNumber());
        ((CAEntity)localObject).setCASubject(this.N_OCert.getSubject());
        localIssuanceManager.issue((BaseEntity)localObject);
      }
      catch (Exception localException4)
      {
        localSysLogger.info("更新根证书操作:发布N_0.cer到目录服务器失败");
      }
    }
    return true;
  }

  void GenKeyStore()
    throws UpdataRootException
  {
    Certificate localCertificate = null;
    Object localObject1 = null;
    KeyStore localKeyStore;
    try
    {
      localKeyStore = KeyStore.getInstance("JKS");
      localKeyStore.load(null, null);
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(this.newCert.getEncoded());
      CertificateFactory localCertificateFactory = CertificateFactory.getInstance("X.509");
      localCertificate = localCertificateFactory.generateCertificate(localByteArrayInputStream);
      Certificate[] arrayOfCertificate = { localCertificate };
      if (this.globalConfig.getCryptoConfig().getDeviceID().equalsIgnoreCase("JSOFT_LIB"))
      {
        Key localKey = Parser.convertKey(this.newKeyPair.getPrivateKey());
        localKeyStore.setKeyEntry(this.globalConfig.getCAConfig().getRootCert().getSubject(), localKey, this.globalConfig.getCryptoConfig().getRootCertPWD().toCharArray(), arrayOfCertificate);
      }
      else
      {
        this.globalConfig.getCAConfig().setCASignKeyID(this.newKeyID);
        localKeyStore.setCertificateEntry(this.globalConfig.getCAConfig().getDN(), localCertificate);
      }
    }
    catch (Exception localException1)
    {
      throw new UpdataRootException("0803", "产生KeyStore错误", localException1);
    }
    FileOutputStream localFileOutputStream = null;
    try
    {
      localFileOutputStream = new FileOutputStream(new File(this.globalConfig.getCryptoConfig().getRootCert()));
      localKeyStore.store(localFileOutputStream, this.globalConfig.getCryptoConfig().getRootCertPWD().toCharArray());
    }
    catch (Exception localException2)
    {
      throw new UpdataRootException("0804", "产生KeyStore文件错误", localException2);
    }
    finally
    {
      try
      {
        if (localFileOutputStream != null)
          localFileOutputStream.close();
      }
      catch (IOException localIOException)
      {
      }
    }
  }

  private void generateAllCert()
    throws UpdataRootException
  {
    byte[] arrayOfByte1 = GenerateNewCert();
    String str = this.globalConfig.getCAConfig().getDN();
    try
    {
      this.newCert = new X509Cert(arrayOfByte1);
    }
    catch (PKIException localPKIException1)
    {
      throw new UpdataRootException(localPKIException1.getErrCode(), localPKIException1.getErrDesc(), localPKIException1);
    }
    byte[] arrayOfByte2 = GenerateAcrossCert(str, this.newKeyPair.getPublicKey(), this.oldKeyPair);
    try
    {
      this.O_NCert = new X509Cert(arrayOfByte2);
    }
    catch (PKIException localPKIException2)
    {
      throw new UpdataRootException(localPKIException2.getErrCode(), localPKIException2.getErrDesc(), localPKIException2);
    }
    byte[] arrayOfByte3 = GenerateAcrossCert(str, this.oldKeyPair.getPublicKey(), this.newKeyPair);
    try
    {
      this.N_OCert = new X509Cert(arrayOfByte3);
    }
    catch (PKIException localPKIException3)
    {
      throw new UpdataRootException(localPKIException3.getErrCode(), localPKIException3.getErrDesc(), localPKIException3);
    }
  }

  private byte[] GenerateNewCert()
    throws UpdataRootException
  {
    X509CertGenerator localX509CertGenerator = new X509CertGenerator();
    String str1 = CodeGenerator.generateRefCode();
    X509Cert localX509Cert = this.globalConfig.getCAConfig().getRootCert();
    ConfigFromDB localConfigFromDB = null;
    try
    {
      DBManager localDBManager = DBManager.getInstance();
      localConfigFromDB = new ConfigFromDB(localDBManager, "SigningCertExtension");
    }
    catch (IDAException localIDAException)
    {
      throw new UpdataRootException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
    String str2 = localConfigFromDB.getString("SigningCertExtension");
    ParseXML localParseXML;
    try
    {
      localParseXML = new ParseXML(str2.getBytes());
    }
    catch (ConfigException localConfigException)
    {
      throw new UpdataRootException(localConfigException.getErrCode(), localConfigException.getErrDesc(), localConfigException);
    }
    try
    {
      localX509CertGenerator.setSerialNumber(str1);
      localX509CertGenerator.setIssuer(localX509Cert.getX509NameIssuer());
      localX509CertGenerator.setSubject(localX509Cert.getSubject());
      localX509CertGenerator.setPublicKey(this.newKeyPair.getPublicKey());
      GregorianCalendar localGregorianCalendar = new GregorianCalendar();
      Date localDate1 = new Date();
      localGregorianCalendar.setTime(localDate1);
      localGregorianCalendar.add(6, this.validity);
      Date localDate2 = localGregorianCalendar.getTime();
      localX509CertGenerator.setNotBefore(localDate1);
      localX509CertGenerator.setNotAfter(localDate2);
      localX509CertGenerator.setSignatureAlg(localX509Cert.getSignatureAlgName());
      addAuthorityKeyIdentifier(localX509CertGenerator, localParseXML);
      addBasicConstraints(localX509CertGenerator, localParseXML);
      addCertificatePolicy(localX509CertGenerator, localParseXML);
      addCRLDistPointExt(localX509CertGenerator, localParseXML);
      addSubjectKeyIdentifier(localX509CertGenerator, localParseXML);
      addKeyUsageExt(localX509CertGenerator, localParseXML);
      return localX509CertGenerator.generateX509Cert(this.newKeyPair.getPrivateKey(), this.session);
    }
    catch (PKIException localPKIException)
    {
    }
    throw new UpdataRootException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
  }

  private byte[] GenerateAcrossCert(String paramString, JKey paramJKey, JKeyPair paramJKeyPair)
    throws UpdataRootException
  {
    X509CertGenerator localX509CertGenerator = new X509CertGenerator();
    String str1 = this.globalConfig.getCAConfig().getBaseDN();
    String str2 = CodeGenerator.generateRefCode();
    Date localDate = new Date();
    try
    {
      localX509CertGenerator.setSerialNumber(str2);
      localX509CertGenerator.setSubject(paramString);
      localX509CertGenerator.setIssuer(this.globalConfig.getCAConfig().getRootCert().getX509NameSubject());
      localX509CertGenerator.setNotBefore(localDate);
      localX509CertGenerator.setNotAfter(this.globalConfig.getCAConfig().getRootCert().getNotAfter());
      localX509CertGenerator.setPublicKey(paramJKey);
      localX509CertGenerator.setSignatureAlg(this.globalConfig.getCAConfig().getRootCert().getSignatureAlgName());
      AuthorityKeyIdentifierExt localAuthorityKeyIdentifierExt = new AuthorityKeyIdentifierExt(paramJKeyPair.getPublicKey());
      SubjectKeyIdentifierExt localSubjectKeyIdentifierExt = new SubjectKeyIdentifierExt(paramJKey);
      Vector localVector = new Vector();
      localVector.add(localAuthorityKeyIdentifierExt);
      localVector.add(localSubjectKeyIdentifierExt);
      localX509CertGenerator.setExtensiond(localVector);
      return localX509CertGenerator.generateX509Cert(paramJKeyPair.getPrivateKey(), this.session);
    }
    catch (PKIException localPKIException)
    {
    }
    throw new UpdataRootException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
  }

  private void GenNewKeyPair()
    throws UpdataRootException
  {
    Mechanism localMechanism;
    if (this.globalConfig.getCAConfig().getPrivateKey().getKeyType().equalsIgnoreCase("RSA_Private"))
    {
      localMechanism = new Mechanism("RSA");
    }
    else if (this.globalConfig.getCAConfig().getPrivateKey().getKeyType().equalsIgnoreCase("RSA_PrivateID"))
    {
      GenKeyAttribute localGenKeyAttribute = new GenKeyAttribute();
      localGenKeyAttribute.setIsExport(false);
      localGenKeyAttribute.setKeyNum(this.newKeyID);
      localMechanism = new Mechanism("RSA");
      localMechanism.setParam(localGenKeyAttribute);
    }
    else
    {
      throw new UpdataRootException("0801", "密钥类型错误");
    }
    try
    {
      int i = Parser.getRSAKeyLength(this.globalConfig.getCAConfig().getRootCert().getPublicKey());
      this.newKeyPair = this.session.generateKeyPair(localMechanism, i);
    }
    catch (PKIException localPKIException)
    {
      throw new UpdataRootException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
  }

  private void saveCert(X509Cert paramX509Cert, JKey paramJKey, String paramString1, String paramString2)
    throws UpdataRootException
  {
    CACertInfo localCACertInfo = new CACertInfo();
    localCACertInfo.setBaseDN(this.globalConfig.getCAConfig().getBaseDN());
    localCACertInfo.setCA_DESC("");
    localCACertInfo.setCA_ID(0);
    localCACertInfo.setSubject(paramX509Cert.getSubject());
    try
    {
      localCACertInfo.setCertEntity(Base64.encode(paramX509Cert.getEncoded()));
    }
    catch (PKIException localPKIException)
    {
      throw new UpdataRootException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
    localCACertInfo.setCertStatus(paramString1);
    localCACertInfo.setDeviceID(paramString2);
    try
    {
      if (paramString2.equalsIgnoreCase("JSOFT_LIB"))
      {
        localCACertInfo.setPrivateKey(ProtectConfig.getInstance().Encrypto(new String(paramJKey.getKey())));
      }
      else if (paramString2.equalsIgnoreCase("JSJY05B_LIB"))
      {
        localCACertInfo.setPrivateKey(ProtectConfig.getInstance().Encrypto(String.valueOf(this.newKeyID)));
        this.globalConfig.getCAConfig().setCASignKeyID(this.newKeyID);
      }
      else
      {
        throw new UpdataRootException("0802", "机密设备名称错误");
      }
    }
    catch (IDAException localIDAException)
    {
      throw new UpdataRootException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
    localCACertInfo.setSN(paramX509Cert.getStringSerialNumber());
    try
    {
      DBInit localDBInit = DBInit.getInstance();
      localDBInit.saveCACert(localCACertInfo);
    }
    catch (DBException localDBException)
    {
      throw new UpdataRootException(localDBException.getErrCode(), localDBException.getErrDesc(), localDBException);
    }
  }

  private void SavaRootCert()
    throws UpdataRootException
  {
    byte[] arrayOfByte = null;
    X509Cert[] arrayOfX509Cert = { this.newCert };
    P7B localP7B = new P7B();
    try
    {
      arrayOfByte = localP7B.generateP7bData_B64(arrayOfX509Cert);
    }
    catch (PKIException localPKIException)
    {
      throw new UpdataRootException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
    File localFile = new File(this.globalConfig.getCryptoConfig().getRootCert());
    String str;
    if (localFile.getParent() == null)
      str = "./rootcert.p7b";
    else
      str = localFile.getParent() + "/rootcert.p7b";
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(new File(str));
      localFileOutputStream.write(arrayOfByte);
      localFileOutputStream.close();
    }
    catch (Exception localException)
    {
      throw new UpdataRootException("0805", "存储根证书错误", localException);
    }
  }

  private void revokeCert()
    throws UpdataRootException
  {
  }

  private void addCertificatePolicy(X509CertGenerator paramX509CertGenerator, ParseXML paramParseXML)
    throws UpdataRootException
  {
    try
    {
      String str1 = paramParseXML.getString("CertificatePolicy_IsExist");
      if (str1.equalsIgnoreCase(""))
        return;
      if (str1.equalsIgnoreCase("false"))
        return;
      CAConfig localCAConfig = null;
      try
      {
        localCAConfig = CAConfig.getInstance();
      }
      catch (IDAException localIDAException)
      {
        throw new UpdataRootException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
      }
      str1 = localCAConfig.getCertificatePolicies();
      String[] arrayOfString = str1.split("[;]");
      CertificatePoliciesExt localCertificatePoliciesExt = new CertificatePoliciesExt();
      for (int i = 0; i < arrayOfString.length; i++)
      {
        String str2 = arrayOfString[i];
        if ((str2 == null) || (str2.trim().equals("")))
          continue;
        int j = str2.indexOf("=");
        String str3 = "2.5.29.32";
        if (j != -1)
        {
          str3 = str2.substring(0, j);
          String str4 = str2.substring(j + 1);
          localCertificatePoliciesExt.addPolicy(str3.trim());
          if ((str4 == null) || (str4.trim().equals("")))
            continue;
          localCertificatePoliciesExt.getPolicy(i).addPolicyQualifierinfo(str4.trim());
        }
        else
        {
          str3 = str2.substring(0);
          localCertificatePoliciesExt.addPolicy(str3.trim());
        }
      }
      if (paramParseXML.getString("CertificatePolicy_IsCritical").equalsIgnoreCase("true"))
        localCertificatePoliciesExt.setCritical(true);
      paramX509CertGenerator.addExtensiond(localCertificatePoliciesExt);
    }
    catch (PKIException localPKIException)
    {
      throw new UpdataRootException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
  }

  private void addAuthorityKeyIdentifier(X509CertGenerator paramX509CertGenerator, ParseXML paramParseXML)
    throws UpdataRootException
  {
    try
    {
      String str = paramParseXML.getString("AuthorityKeyIdentifier_IsExist");
      if (!str.equalsIgnoreCase("true"))
        return;
      AuthorityKeyIdentifierExt localAuthorityKeyIdentifierExt = new AuthorityKeyIdentifierExt(this.newKeyPair.getPublicKey());
      if (paramParseXML.getString("AuthorityKeyIdentifier_IsCritical").equalsIgnoreCase("true"))
        localAuthorityKeyIdentifierExt.setCritical(true);
      paramX509CertGenerator.addExtensiond(localAuthorityKeyIdentifierExt);
    }
    catch (PKIException localPKIException)
    {
      throw new UpdataRootException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
  }

  private void addSubjectKeyIdentifier(X509CertGenerator paramX509CertGenerator, ParseXML paramParseXML)
    throws UpdataRootException
  {
    try
    {
      String str = paramParseXML.getString("SubjectKeyIdentifier_IsExist");
      if (str.equalsIgnoreCase("false"))
        return;
      SubjectKeyIdentifierExt localSubjectKeyIdentifierExt = new SubjectKeyIdentifierExt(this.newKeyPair.getPublicKey());
      if (paramParseXML.getString("SubjectKeyIdentifier_IsCritical").equalsIgnoreCase("true"))
        localSubjectKeyIdentifierExt.setCritical(true);
      paramX509CertGenerator.addExtensiond(localSubjectKeyIdentifierExt);
    }
    catch (PKIException localPKIException)
    {
      throw new UpdataRootException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
  }

  private void addBasicConstraints(X509CertGenerator paramX509CertGenerator, ParseXML paramParseXML)
    throws UpdataRootException
  {
    try
    {
      String str = paramParseXML.getString("BasicConstraints_IsExist");
      if (!str.equalsIgnoreCase("true"))
        return;
      int i = 0;
      try
      {
        i = paramParseXML.getNumber("BasicConstraints_PathLength");
      }
      catch (ConfigException localConfigException)
      {
        i = -1;
      }
      BasicConstraintsExt localBasicConstraintsExt = null;
      if (i >= 0)
        localBasicConstraintsExt = new BasicConstraintsExt(true, i);
      else
        localBasicConstraintsExt = new BasicConstraintsExt(true);
      if (paramParseXML.getString("BasicConstraints_IsCritical").equalsIgnoreCase("true"))
        localBasicConstraintsExt.setCritical(true);
      paramX509CertGenerator.addExtensiond(localBasicConstraintsExt);
    }
    catch (PKIException localPKIException)
    {
      throw new UpdataRootException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
  }

  private void addCRLDistPointExt(X509CertGenerator paramX509CertGenerator, ParseXML paramParseXML)
    throws UpdataRootException
  {
    try
    {
      if (!paramParseXML.getString("CDP_IsExist").equalsIgnoreCase("true"))
        return;
      Object localObject1 = null;
      String str1 = this.globalConfig.getCrlConfig().getCRLLDAPPath();
      DistributionPointExt localDistributionPointExt1 = null;
      DistributionPointExt localDistributionPointExt2 = null;
      DistributionPointExt localDistributionPointExt3 = null;
      ArrayList localArrayList = new ArrayList();
      int i = 0;
      if (this.globalConfig.getCrlConfig().isCDP_DN_Publish())
      {
        str2 = this.globalConfig.getCAConfig().getBaseDN();
        str3 = "CN=CRL1," + str1;
        localDistributionPointExt1 = new DistributionPointExt();
        localDistributionPointExt1.setDistributionPointName(4, str3);
        localArrayList.add(localDistributionPointExt1);
        i++;
      }
      String str2 = paramParseXML.getString("CDP_LDAP_URI");
      if (paramParseXML.getString("CDP_LDAP_URI_Publish").equalsIgnoreCase("true"))
      {
        str3 = paramParseXML.getString("BaseDN");
        localObject2 = InternalConfig.getInstance();
        localObject3 = ((InternalConfig)localObject2).getCrlEnding();
        String str4 = null;
        if (!str2.endsWith("/"))
          str2 = str2 + "/";
        str4 = str2 + "CN=CRL1," + str1 + (String)localObject3;
        localDistributionPointExt3 = new DistributionPointExt();
        localDistributionPointExt3.setDistributionPointName(6, str4);
        localArrayList.add(localDistributionPointExt3);
        i++;
      }
      String str3 = this.globalConfig.getCrlConfig().getCDP_URI();
      if (this.globalConfig.getCrlConfig().isCDP_URI_Publish())
      {
        if (str3.endsWith("/"))
          localObject2 = str3 + "crl1.crl";
        else
          localObject2 = str3 + "/crl1.crl";
        localDistributionPointExt2 = new DistributionPointExt();
        localDistributionPointExt2.setDistributionPointName(6, (String)localObject2);
        localArrayList.add(localDistributionPointExt2);
        i++;
      }
      if (i == 0)
        return;
      Object localObject2 = new DistributionPointExt[i];
      localArrayList.toArray(localObject2);
      Object localObject3 = new CRLDistPointExt();
      ((CRLDistPointExt)localObject3).setDistributionPointExts(localObject2);
      if (paramParseXML.getString("CDP_IsCritical").equalsIgnoreCase("true"))
        ((CRLDistPointExt)localObject3).setCritical(true);
      paramX509CertGenerator.addExtensiond((Extension)localObject3);
    }
    catch (PKIException localPKIException)
    {
      throw new UpdataRootException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException.getHistory());
    }
    catch (IDAException localIDAException)
    {
      throw new UpdataRootException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException.getHistory());
    }
  }

  private void addKeyUsageExt(X509CertGenerator paramX509CertGenerator, ParseXML paramParseXML)
    throws UpdataRootException
  {
    try
    {
      KeyUsageExt localKeyUsageExt = new KeyUsageExt();
      int i = 0;
      String str = paramParseXML.getString("KeyUsage_DIGITAL_SIGNATURE");
      if (str.equalsIgnoreCase("true"))
      {
        localKeyUsageExt.addKeyUsage("digitalSignature");
        i = 1;
      }
      str = paramParseXML.getString("KeyUsage_NON_REPUDIATION");
      if (str.equalsIgnoreCase("true"))
      {
        localKeyUsageExt.addKeyUsage("nonRepudiation");
        i = 1;
      }
      str = paramParseXML.getString("KeyUsage_KEY_ENCIPHERMENT");
      if (str.equalsIgnoreCase("true"))
      {
        localKeyUsageExt.addKeyUsage("keyEncipherment");
        i = 1;
      }
      str = paramParseXML.getString("KeyUsage_DATA_ENCIPHERMENT");
      if (str.equalsIgnoreCase("true"))
      {
        localKeyUsageExt.addKeyUsage("dataEncipherment");
        i = 1;
      }
      str = paramParseXML.getString("KeyUsage_KEY_AGREEMENT");
      if (str.equalsIgnoreCase("true"))
      {
        localKeyUsageExt.addKeyUsage("keyAgreement");
        i = 1;
      }
      str = paramParseXML.getString("KeyUsage_KEY_CERT_SIGN");
      if (str.equalsIgnoreCase("true"))
      {
        localKeyUsageExt.addKeyUsage("keyCertSign");
        i = 1;
      }
      str = paramParseXML.getString("KeyUsage_CRL_SIGN");
      if (str.equalsIgnoreCase("true"))
      {
        localKeyUsageExt.addKeyUsage("cRLSign");
        i = 1;
      }
      if (paramParseXML.getString("KeyUsage_IsCritical").equalsIgnoreCase("true"))
        localKeyUsageExt.setCritical(true);
      if (i != 0)
        paramX509CertGenerator.addExtensiond(localKeyUsageExt);
      else
        return;
    }
    catch (PKIException localPKIException)
    {
      throw new UpdataRootException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
  }

  public void saveCertToFile(X509Cert paramX509Cert, String paramString)
    throws UpdataRootException
  {
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(paramString);
      localFileOutputStream.write(paramX509Cert.getEncoded());
    }
    catch (PKIException localPKIException)
    {
      throw new UpdataRootException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
    catch (IOException localIOException)
    {
      throw new UpdataRootException("0804", "写文件失败", localIOException);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.updataRootCert.UpdataRootCert
 * JD-Core Version:    0.6.0
 */