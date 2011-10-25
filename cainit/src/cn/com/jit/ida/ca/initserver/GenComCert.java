package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.util.JITKeyStore;
import cn.com.jit.ida.globalconfig.util.JITKeyStore.JITCertKeyStruct;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.cipher.lib.JHARDLib;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Vector;

public class GenComCert
{
  String p7b;
  String DN;
  GenP10 genP10;
  GenCertFromP10 genCert;
  Session session;
  P7Bxp p7bxp;
  private boolean isHardDevice;
  private int hardKeyID;

  public GenComCert(String paramString1, Session paramSession, String paramString2)
    throws InitServerException
  {
    this.session = paramSession;
    this.DN = paramString1;
    GlobalConfig localGlobalConfig;
    Object localObject;
    try
    {
      localGlobalConfig = GlobalConfig.getInstance();
      localGlobalConfig.getCAConfig().setCommCertDN(paramString1);
      CryptoConfig localCryptoConfig = localGlobalConfig.getCryptoConfig();
      localObject = localCryptoConfig.getCommDeviceID();
      if (((String)localObject).trim().equalsIgnoreCase("JSJY05B_LIB"))
      {
        this.isHardDevice = true;
        this.hardKeyID = localCryptoConfig.getCommKeyID();
      }
    }
    catch (IDAException localIDAException)
    {
      throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
    String str = null;
    if (!this.isHardDevice)
    {
      this.genP10 = new GenP10(paramString1, "RSA", 1024, paramSession);
    }
    else if ((paramSession instanceof JHARDLib))
    {
      this.genP10 = new GenP10(paramString1, "RSA", 1024, paramSession, this.hardKeyID);
    }
    else
    {
      localObject = null;
      try
      {
        JCrypto.getInstance().initialize("JSJY05B_LIB", null);
        localObject = JCrypto.getInstance().openSession("JSJY05B_LIB");
      }
      catch (PKIException localPKIException)
      {
        throw new InitServerException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException.getHistory());
      }
      this.genP10 = new GenP10(paramString1, "RSA", 1024, (Session)localObject, this.hardKeyID);
    }
//    str = this.genP10.getP10String();
//    this.genCert = new GenCertFromP10(paramString1, str, localGlobalConfig.getInternalConfig().getCommCertTemplateName(), paramString2);
//    this.p7b = this.genCert.getP7BString();
//    this.p7bxp = new P7Bxp(this.p7b);
  }

  public void SavaCert(String paramString)
    throws InitServerException
  {
    if (this.p7b == null)
      throw new InitServerException("0957", "产生P7B证书链错误");
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(paramString);
      localFileOutputStream.write(this.p7b.getBytes());
      localFileOutputStream.flush();
      localFileOutputStream.close();
    }
    catch (IOException localIOException)
    {
      throw new InitServerException("0958", "保存证书错误", localIOException);
    }
  }

  public boolean importCert(boolean paramBoolean)
    throws IDAException
  {
    X509Cert[] arrayOfX509Cert = this.genCert.getJITCerts();
    CryptoConfig localCryptoConfig = CryptoConfig.getInstance();
    JITKeyStore localJITKeyStore = new JITKeyStore(true, localCryptoConfig.getCommKeyStore(), localCryptoConfig.getCommKeyStorePWD().toCharArray(), localCryptoConfig.getCommDeviceID(), localCryptoConfig.getCommKeyID(), null);
    if (paramBoolean)
    {
      int i = 0;
      Vector localVector = localJITKeyStore.get_KeyStruct();
      for (int k = 0; k < localVector.size(); k++)
      {
        JITKeyStore.JITCertKeyStruct localJITCertKeyStruct = (JITKeyStore.JITCertKeyStruct)localVector.get(k);
        for (int m = 0; m < arrayOfX509Cert.length; m++)
        {
          if (arrayOfX509Cert[m].getSubject().equalsIgnoreCase(localJITCertKeyStruct.m_Cert.getSubject()))
          {
            i = 1;
            break;
          }
          if (!arrayOfX509Cert[m].getSubject().equalsIgnoreCase(ConfigTool.formatDN(localJITCertKeyStruct.m_Cert.getSubject())))
            continue;
          i = 1;
          break;
        }
      }
      if (i == 0)
        throw new InitServerException("0934", "通信证书存储错误，未找到匹配的通信证书");
    }
    if (!localCryptoConfig.getCommDeviceID().equalsIgnoreCase("JSOFT_LIB"))
      if ((this.session instanceof JHARDLib))
      {
        ((JHARDLib)this.session).destroyCertObject(null, "trustcertid".getBytes());
      }
      else
      {
        JHARDLib localJHARDLib = (JHARDLib)JCrypto.getInstance().openSession("JSJY05B_LIB");
        localJHARDLib.destroyCertObject(null, "trustcertid".getBytes());
      }
    for (int j = 0; j < arrayOfX509Cert.length; j++)
      if (arrayOfX509Cert[j].getSubject().equalsIgnoreCase(this.DN))
      {
        localJITKeyStore.addJITCert(arrayOfX509Cert[j].getSubject(), arrayOfX509Cert[j], true);
        if (localCryptoConfig.getCommDeviceID().equalsIgnoreCase("JSOFT_LIB"))
          continue;
        localJITKeyStore.addJITCert_pkcs11(arrayOfX509Cert[j], localCryptoConfig.getCommKeyID());
      }
      else
      {
        localJITKeyStore.addJITCert(arrayOfX509Cert[j].getSubject(), arrayOfX509Cert[j], true);
        if (localCryptoConfig.getCommDeviceID().equalsIgnoreCase("JSOFT_LIB"))
          continue;
        localJITKeyStore.addJITTrustCert_pkcs11(arrayOfX509Cert[j]);
      }
    return localJITKeyStore.saveToFile();
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
    Certificate localCertificate = null;
    Certificate[] arrayOfCertificate = null;
    X509Cert[] arrayOfX509Cert = null;
    localCertificate = this.genCert.getJavaUserCert();
    arrayOfCertificate = this.genCert.getCerts();
    try
    {
      arrayOfX509Cert = this.genCert.getJITCerts();
    }
    catch (IDAException localIDAException)
    {
      throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
    if (!this.isHardDevice)
      try
      {
        Key localKey = this.genP10.getPrivateKey();
        localKeyStore.setKeyEntry(this.DN, localKey, paramArrayOfChar, new Certificate[] { localCertificate });
      }
      catch (Exception localException2)
      {
        throw new InitServerException("0917", "产生KeyStore错误", localException2);
      }
    try
    {
      for (int i = 1; i < arrayOfCertificate.length; i++)
        localKeyStore.setCertificateEntry(arrayOfX509Cert[i].getSubject(), arrayOfCertificate[i]);
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
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.GenComCert
 * JD-Core Version:    0.6.0
 */