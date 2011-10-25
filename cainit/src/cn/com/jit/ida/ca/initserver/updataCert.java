package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Service;
import cn.com.jit.ida.ca.certmanager.service.AdminCertUpdDown;
import cn.com.jit.ida.ca.certmanager.service.request.CertUPDDownRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CertUPDDownResponse;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.privilege.TemplateAdmin;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.JKeyPair;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.pkcs.PKCS12;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class updataCert
{
  CertUPDDownRequest request = new CertUPDDownRequest();
  String p7bStr;
  GenP10 genP10;
  String p10;
  P7Bxp p7bxp;
  GlobalConfig globalConfig;
  int validity;

  public updataCert(String paramString1, int paramInt, String paramString2)
    throws InitServerException
  {
    this.request.setCertSN(paramString1);
    try
    {
      this.request.setValidity(String.valueOf(paramInt));
    }
    catch (IDAException localIDAException)
    {
      throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
    this.p10 = paramString2;
  }

  public updataCert(String paramString1, String paramString2)
    throws InitServerException
  {
    try
    {
      this.request.setCertDN(paramString1);
      this.request.setCtmlName(paramString2);
    }
    catch (IDAException localIDAException)
    {
      throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
  }

  private void genP10()
    throws InitServerException
  {
    Session localSession = null;
    try
    {
      this.globalConfig = GlobalConfig.getInstance();
      localSession = JCrypto.getInstance().openSession(this.globalConfig.getCryptoConfig().getDeviceID());
    }
    catch (PKIException localPKIException)
    {
      throw new InitServerException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
    catch (IDAException localIDAException)
    {
      throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
    if ((this.p10 == null) || (this.p10.equalsIgnoreCase("NULL")))
    {
      this.genP10 = new GenP10("CN=tmp," + this.globalConfig.getCAConfig().getBaseDN(), "RSA", 1024, localSession);
      this.request.setP10(this.genP10.getP10String());
    }
    else
    {
      this.request.setP10(this.p10);
    }
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
    catch (IDAException localIDAException1)
    {
      throw new InitServerException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1);
    }
    AdminCertUpdDown localAdminCertUpdDown = new AdminCertUpdDown();
    localCertUPDDownResponse = (CertUPDDownResponse)localAdminCertUpdDown.dealRequest(this.request);
    if (localCertUPDDownResponse.getMsg().equalsIgnoreCase("success"))
      this.p7bStr = localCertUPDDownResponse.getP7b();
    else
      throw new InitServerException(localCertUPDDownResponse.getErr(), localCertUPDDownResponse.getMsg());
    this.p7bxp = new P7Bxp(this.p7bStr);
    try
    {
      this.globalConfig.reflush();
    }
    catch (IDAException localIDAException2)
    {
      throw new InitServerException(localIDAException2.getErrCode(), localIDAException2.getErrDesc());
    }
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
      throw new InitServerException("0917", "产生KeyStore错误", localException1);
    }
    Certificate[] arrayOfCertificate = null;
    arrayOfCertificate = this.p7bxp.getJavaCert();
    try
    {
      Key localKey = this.genP10.getPrivateKey();
      localKeyStore.setKeyEntry(this.p7bxp.getUserCert().getSubject(), localKey, paramArrayOfChar, arrayOfCertificate);
    }
    catch (Exception localException2)
    {
      throw new InitServerException("0917", "产生KeyStore错误", localException2);
    }
    try
    {
      for (int i = 0; i < arrayOfCertificate.length; i++)
        localKeyStore.setCertificateEntry(Integer.toString(i), arrayOfCertificate[i]);
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new InitServerException("0917", "产生密钥错误", localKeyStoreException);
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
      localPKCS12.generatePfxFile(this.genP10.getKeyPair().getPrivateKey(), this.p7bxp.getCertChain(), paramArrayOfChar, paramString);
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

  public void saveP7B(String paramString)
    throws InitServerException
  {
    if (!this.p7bxp.saveToFile(paramString))
    {
      InitServerException localInitServerException = new InitServerException("0958", "保存证书错误");
      localInitServerException.appendMsg(paramString);
      throw localInitServerException;
    }
  }

  public String getNewCertSN()
  {
    if (this.p7bxp == null)
      return null;
    X509Cert localX509Cert = this.p7bxp.getUserCert();
    return localX509Cert.getSerialNumber().toString();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.updataCert
 * JD-Core Version:    0.6.0
 */