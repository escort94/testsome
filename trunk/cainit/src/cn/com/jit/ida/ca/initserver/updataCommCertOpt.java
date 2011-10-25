package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Service;
import cn.com.jit.ida.ca.certmanager.service.CertUpdDown;
import cn.com.jit.ida.ca.certmanager.service.request.CertUPDDownRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CertUPDDownResponse;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.util.JITKeyStore;
import cn.com.jit.ida.globalconfig.util.JITKeyStore.JITCertKeyStruct;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.privilege.TemplateAdmin;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.JKeyPair;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.cipher.lib.JHARDLib;
import cn.com.jit.ida.util.pki.pkcs.PKCS12;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class updataCommCertOpt
{
  CertUPDDownRequest request = new CertUPDDownRequest();
  String p7bStr;
  GenP10 genP10;
  P7Bxp p7bxp;
  GlobalConfig globalConfig;
  CryptoConfig cryptoConfig;
  String deviceID;
  Session session;

  public updataCommCertOpt(String paramString)
  {
    this.request.setCertSN(paramString);
  }

  public updataCommCertOpt(String paramString1, String paramString2, String paramString3)
    throws InitServerException
  {
    try
    {
      this.request.setCertDN(paramString1);
      this.request.setCtmlName(paramString2);
      this.request.setValidity(paramString3);
    }
    catch (IDAException localIDAException)
    {
      throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
  }

  private void genP10()
    throws InitServerException
  {
    try
    {
      this.globalConfig = GlobalConfig.getInstance();
      this.cryptoConfig = this.globalConfig.getCryptoConfig();
      this.deviceID = this.cryptoConfig.getCommDeviceID();
      JCrypto localJCrypto = JCrypto.getInstance();
      localJCrypto.initialize(this.deviceID, null);
      this.session = localJCrypto.openSession(this.deviceID);
    }
    catch (PKIException localPKIException)
    {
      throw new InitServerException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
    catch (IDAException localIDAException1)
    {
      throw new InitServerException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1);
    }
    if (this.deviceID.equalsIgnoreCase("JSOFT_LIB"))
    {
      this.genP10 = new GenP10("CN=tmp," + this.globalConfig.getCAConfig().getBaseDN(), "RSA", 1024, this.session);
    }
    else
    {
      System.out.print("请选择密钥对标示(>0):");
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
      String str1 = null;
      try
      {
        str1 = localBufferedReader.readLine();
      }
      catch (Exception localException1)
      {
        throw new InitServerException("0999", "更新系统通信证书，读取控制台输入错误");
      }
      int i = -1;
      try
      {
        i = Integer.parseInt(str1);
      }
      catch (Exception localException2)
      {
        throw new InitServerException("0999", "更新系统通信证书，读取控制台输入错误，请输入有效整数");
      }
      if (i <= 0)
        throw new InitServerException("0999", "更新系统通信证书，读取控制台输入错误，请输入>0整数");
      CryptoConfig localCryptoConfig = null;
      try
      {
        localCryptoConfig = CryptoConfig.getInstance();
      }
      catch (IDAException localIDAException2)
      {
        throw new InitServerException("0999", "更新系统通信证书，初始化CryptoConfig配置信息失败", localIDAException2);
      }
      String str2 = localCryptoConfig.getDeviceID();
      if (str2.equals("JSJY05B_LIB"))
      {
        int j = localCryptoConfig.getSigningKeyID();
        if (j == i)
          throw new InitServerException("0989", "不允许CA签名证书和通信证书使用同一加密设备的同一密钥，请重新配置");
      }
      this.genP10 = new GenP10("CN=tmp," + this.globalConfig.getCAConfig().getBaseDN(), "RSA", 1024, this.session, i);
      try
      {
        localCryptoConfig.setCommKeyID(i);
      }
      catch (IDAException localIDAException3)
      {
        throw new InitServerException(localIDAException3.getErrCode(), localIDAException3.getErrDesc(), localIDAException3.getHistory());
      }
    }
    this.request.setP10(this.genP10.getP10String());
  }

  public void doUpdata()
    throws InitServerException
  {
    genP10();
    CertUPDDownResponse localCertUPDDownResponse = null;
    Operator localOperator = new Operator();
    localOperator.setOperatorDN("系统管理员");
    localOperator.setOperatorSN("系统管理员");
    localOperator.setOperatorCert("系统管理员".getBytes());
    try
    {
      this.request.setOperator(localOperator);
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
      Date localDate = new Date();
      String str = localSimpleDateFormat.format(localDate);
      this.request.setNotBefore(str);
    }
    catch (IDAException localIDAException)
    {
      throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
    CertUpdDown localCertUpdDown = new CertUpdDown();
    disablePrivilege();
    localCertUPDDownResponse = (CertUPDDownResponse)localCertUpdDown.dealRequest(this.request);
    enablePrivilege();
    if (localCertUPDDownResponse.getMsg().equalsIgnoreCase("success"))
      this.p7bStr = localCertUPDDownResponse.getP7b();
    else
      throw new InitServerException(localCertUPDDownResponse.getErr(), localCertUPDDownResponse.getMsg());
    this.p7bxp = new P7Bxp(this.p7bStr);
  }

  public void SavaKeyStore(String paramString, char[] paramArrayOfChar)
    throws InitServerException
  {
    KeyStore localKeyStore = null;
    try
    {
      localKeyStore = KeyStore.getInstance("JKS");
      localKeyStore.load(null, null);
    }
    catch (Exception localException1)
    {
      throw new InitServerException("0917", "产生通信证书KeyStore错误", localException1);
    }
    Certificate[] arrayOfCertificate = null;
    arrayOfCertificate = new Certificate[] { this.p7bxp.getJavaUserCert() };
    try
    {
      Key localKey = this.genP10.getPrivateKey();
      localKeyStore.deleteEntry(this.p7bxp.getUserCert().getSubject());
      localKeyStore.setKeyEntry(this.p7bxp.getUserCert().getSubject(), localKey, paramArrayOfChar, arrayOfCertificate);
    }
    catch (Exception localException2)
    {
      throw new InitServerException("0917", "产生KeyStore错误", localException2);
    }
    X509Cert[] arrayOfX509Cert = this.p7bxp.getCertChain();
    arrayOfCertificate = this.p7bxp.getJavaCertChain_NoUser();
    try
    {
      for (int i = 0; i < arrayOfCertificate.length; i++)
      {
        String str = localKeyStore.getCertificateAlias(arrayOfCertificate[i]);
        if (str != null)
          continue;
        for (str = arrayOfX509Cert[(i + 1)].getSubject(); localKeyStore.containsAlias(str); str = str + String.valueOf(System.currentTimeMillis()));
        localKeyStore.setCertificateEntry(str, arrayOfCertificate[i]);
      }
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new InitServerException("0999", "系统错误", localKeyStoreException);
    }
    FileOutputStream localFileOutputStream;
    try
    {
      localFileOutputStream = new FileOutputStream(new File(paramString));
    }
    catch (Exception localException3)
    {
      throw new InitServerException("0917", "产生KeyStore错误", localException3);
    }
    try
    {
      localKeyStore.store(localFileOutputStream, paramArrayOfChar);
    }
    catch (Exception localException4)
    {
      throw new InitServerException("0917", "产生KeyStore错误", localException4);
    }
    finally
    {
      try
      {
        localFileOutputStream.close();
      }
      catch (IOException localIOException2)
      {
      }
    }
  }

  public void savePKCS12(String paramString, char[] paramArrayOfChar)
    throws InitServerException
  {
    PKCS12 localPKCS12 = new PKCS12();
    try
    {
      localPKCS12.generatePfxFile(this.genP10.getKeyPair().getPrivateKey(), this.p7bxp.getUserCert(), paramArrayOfChar, paramString);
    }
    catch (PKIException localPKIException)
    {
      throw new InitServerException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
  }

  private void enablePrivilege()
    throws InitServerException
  {
    try
    {
      Privilege localPrivilege = Privilege.getInstance();
      localPrivilege.setPrivilegeNormal();
      TemplateAdmin localTemplateAdmin = TemplateAdmin.getInstance();
      localTemplateAdmin.setTemplateAdminNormal();
    }
    catch (PrivilegeException localPrivilegeException)
    {
      throw new InitServerException(localPrivilegeException.getErrCode(), localPrivilegeException.getErrDesc(), localPrivilegeException);
    }
  }

  private void disablePrivilege()
    throws InitServerException
  {
    try
    {
      Privilege localPrivilege = Privilege.getInstance();
      localPrivilege.setPrivilegeSpecial();
      TemplateAdmin localTemplateAdmin = TemplateAdmin.getInstance();
      localTemplateAdmin.setTemplateAdminSpecial();
    }
    catch (PrivilegeException localPrivilegeException)
    {
      throw new InitServerException(localPrivilegeException.getErrCode(), localPrivilegeException.getErrDesc(), localPrivilegeException);
    }
  }

  public boolean importCert(boolean paramBoolean)
    throws IDAException
  {
    X509Cert[] arrayOfX509Cert = this.p7bxp.getCertChain();
    CryptoConfig localCryptoConfig = CryptoConfig.getInstance();
    JITKeyStore localJITKeyStore = new JITKeyStore(false, localCryptoConfig.getCommKeyStore(), localCryptoConfig.getCommKeyStorePWD().toCharArray(), localCryptoConfig.getCommDeviceID(), localCryptoConfig.getCommKeyID(), null);
    if (paramBoolean)
    {
      i = 0;
      Vector localVector = localJITKeyStore.get_KeyStruct();
      for (int j = 0; j < localVector.size(); j++)
      {
        JITKeyStore.JITCertKeyStruct localJITCertKeyStruct = (JITKeyStore.JITCertKeyStruct)localVector.get(j);
        for (int k = 0; k < arrayOfX509Cert.length; k++)
        {
          if (arrayOfX509Cert[k].getSubject().equalsIgnoreCase(localJITCertKeyStruct.m_Cert.getSubject()))
          {
            i = 1;
            break;
          }
          if (!arrayOfX509Cert[k].getSubject().equalsIgnoreCase(ConfigTool.formatDN(localJITCertKeyStruct.m_Cert.getSubject())))
            continue;
          i = 1;
          break;
        }
      }
      if (i == 0)
        throw new InitServerException("0934", "通信证书存储错误，未找到匹配的通信证书");
    }
    if (!localCryptoConfig.getCommDeviceID().equalsIgnoreCase("JSOFT_LIB"))
      ((JHARDLib)this.session).destroyCertObject(null, "trustcertid".getBytes());
    for (int i = 0; i < arrayOfX509Cert.length; i++)
      if (i == 0)
      {
        localJITKeyStore.addJITCert(arrayOfX509Cert[i].getSubject(), arrayOfX509Cert[i], true);
        if (localCryptoConfig.getCommDeviceID().equalsIgnoreCase("JSOFT_LIB"))
          continue;
        localJITKeyStore.addJITCert_pkcs11(arrayOfX509Cert[i], localCryptoConfig.getCommKeyID());
      }
      else
      {
        localJITKeyStore.addJITCert(arrayOfX509Cert[i].getSubject(), arrayOfX509Cert[i], true);
        if (localCryptoConfig.getCommDeviceID().equalsIgnoreCase("JSOFT_LIB"))
          continue;
        localJITKeyStore.addJITTrustCert_pkcs11(arrayOfX509Cert[i]);
      }
    return localJITKeyStore.saveToFile();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.updataCommCertOpt
 * JD-Core Version:    0.6.0
 */