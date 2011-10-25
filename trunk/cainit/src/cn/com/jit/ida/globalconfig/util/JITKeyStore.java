package cn.com.jit.ida.globalconfig.util;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.PKIToolConfig;
import cn.com.jit.ida.util.pki.Parser;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cert.X509CertGenerator;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.JKey;
import cn.com.jit.ida.util.pki.cipher.JKeyPair;
import cn.com.jit.ida.util.pki.cipher.Mechanism;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.cipher.lib.JHARDLib;
import cn.com.jit.ida.util.pki.cipher.param.GenKeyAttribute;
import cn.com.jit.ida.util.pki.pkcs.PKCS10;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

public class JITKeyStore
{
  private boolean m_isNewKeyStore = false;
  private String m_filePath = null;
  private int m_keyID = -1;
  private char[] m_pwd = null;
  private String m_deviceID = "JSOFT_LIB";
  private String m_KeyAlia = null;
  private Vector m_KeyStruct = new Vector();
  KeyStore m_keyStore;
  Vector m_TrustCerts = new Vector();
  Session m_session = null;

  public JITKeyStore(boolean paramBoolean, String paramString1, char[] paramArrayOfChar, String paramString2, int paramInt, String paramString3)
    throws ConfigException
  {
    this.m_isNewKeyStore = paramBoolean;
    this.m_filePath = paramString1;
    this.m_pwd = paramArrayOfChar;
    this.m_deviceID = paramString2;
    this.m_keyID = paramInt;
    this.m_KeyAlia = paramString3;
    open();
  }

  public JITKeyStore(boolean paramBoolean, String paramString, char[] paramArrayOfChar)
    throws ConfigException
  {
    this(paramBoolean, paramString, paramArrayOfChar, "JSOFT_LIB", 0, null);
  }

  private boolean open()
    throws ConfigException
  {
    try
    {
      JCrypto.getInstance().initialize(this.m_deviceID, null);
      this.m_session = JCrypto.getInstance().openSession(this.m_deviceID);
    }
    catch (PKIException localPKIException1)
    {
      throw new ConfigException(localPKIException1.getErrCode(), localPKIException1.getErrDesc(), localPKIException1);
    }
    try
    {
      if (this.m_isNewKeyStore)
      {
        this.m_keyStore = KeyStore.getInstance("JKS");
        this.m_keyStore.load(null, null);
        return true;
      }
      this.m_keyStore = KeyStore.getInstance("JKS");
      FileInputStream localFileInputStream = new FileInputStream(this.m_filePath);
      this.m_keyStore.load(localFileInputStream, this.m_pwd);
    }
    catch (Exception localException1)
    {
      throw new ConfigException(ConfigException.KEY_STORE_ERROR, "证书存储错误", localException1);
    }
    try
    {
      Enumeration localEnumeration = this.m_keyStore.aliases();
      Vector localVector = new Vector();
      while (localEnumeration.hasMoreElements())
      {
        String str = (String)localEnumeration.nextElement();
        if (this.m_keyStore.isCertificateEntry(str))
        {
          if (this.m_deviceID.equalsIgnoreCase("JSOFT_LIB"))
          {
            localObject1 = this.m_keyStore.getCertificate(str);
            this.m_TrustCerts.add(Java2JITCert((Certificate)localObject1));
            continue;
          }
          if (this.m_KeyAlia == null)
            continue;
          if (this.m_KeyAlia.equalsIgnoreCase(str))
          {
            localObject1 = new JITCertKeyStruct();
            localObject2 = new Mechanism("RSA");
            GenKeyAttribute localGenKeyAttribute = new GenKeyAttribute();
            localGenKeyAttribute.setIsExport(false);
            localGenKeyAttribute.setKeyNum(this.m_keyID);
            ((Mechanism)localObject2).setParam(localGenKeyAttribute);
            try
            {
              ((JITCertKeyStruct)localObject1).m_JPriKey = this.m_session.generateKeyPair((Mechanism)localObject2, 0).getPrivateKey();
            }
            catch (PKIException localPKIException2)
            {
              throw new IDAException(localPKIException2.getErrCode(), localPKIException2.getErrDesc());
            }
            Certificate localCertificate = this.m_keyStore.getCertificate(str);
            ((JITCertKeyStruct)localObject1).m_Cert = Java2JITCert(localCertificate);
            this.m_KeyStruct.add(localObject1);
            continue;
          }
          localObject1 = this.m_keyStore.getCertificate(str);
          this.m_TrustCerts.add(Java2JITCert((Certificate)localObject1));
          continue;
        }
        if (!this.m_keyStore.isKeyEntry(str))
          continue;
        Object localObject1 = new JITCertKeyStruct();
        Object localObject2 = this.m_keyStore.getKey(str, this.m_pwd);
        ((JITCertKeyStruct)localObject1).m_JPriKey = new JKey("RSA_Private", ((Key)localObject2).getEncoded());
        ((JITCertKeyStruct)localObject1).m_Cert = Java2JITCert(this.m_keyStore.getCertificateChain(str)[0]);
        this.m_KeyStruct.add(localObject1);
      }
    }
    catch (Exception localException2)
    {
      throw new ConfigException(ConfigException.KEY_STORE_ERROR, "证书存储错误", localException2);
    }
    return true;
  }

  X509Cert getJITCertFormAlia(String paramString)
    throws ConfigException
  {
    X509Cert localX509Cert = null;
    try
    {
      Certificate localCertificate = this.m_keyStore.getCertificate(paramString);
      if (localCertificate == null)
      {
        localObject = this.m_keyStore.getCertificateChain(paramString);
        localCertificate = localObject[0];
      }
      byte[] arrayOfByte = localCertificate.getEncoded();
      localX509Cert = new X509Cert(arrayOfByte);
    }
    catch (KeyStoreException localKeyStoreException)
    {
      localObject = new ConfigException(ConfigException.NOCERT_IN_KEYSTORE, "证书存储中没有需要的证书", localKeyStoreException);
      ((ConfigException)localObject).appendMsg(this.m_filePath + "   " + paramString);
      throw ((Throwable)localObject);
    }
    catch (Exception localException)
    {
      Object localObject = new ConfigException(ConfigException.CONFIG_ERROR, "配置错误", localException);
      ((ConfigException)localObject).appendMsg(this.m_filePath + "   " + paramString);
    }
    return (X509Cert)localX509Cert;
  }

  public boolean saveToFile()
    throws ConfigException
  {
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(new File(this.m_filePath));
      this.m_keyStore.store(localFileOutputStream, this.m_pwd);
      localFileOutputStream.close();
    }
    catch (Exception localException)
    {
      throw new ConfigException(ConfigException.WRITE_FILE_ERROR, "写文件错误", localException);
    }
    return true;
  }

  public boolean addJITKEY(String paramString, X509Cert[] paramArrayOfX509Cert, JKey paramJKey, boolean paramBoolean)
    throws ConfigException
  {
    int i;
    try
    {
      if (this.m_keyStore.isCertificateEntry(paramString))
        return false;
      if (this.m_keyStore.isKeyEntry(paramString))
        if (paramBoolean)
        {
          Key localKey1 = this.m_keyStore.getKey(paramString, this.m_pwd);
          localObject = new JITCertKeyStruct();
          ((JITCertKeyStruct)localObject).m_JPriKey = new JKey("RSA_Private", localKey1.getEncoded());
          ((JITCertKeyStruct)localObject).m_Cert = paramArrayOfX509Cert[0];
          for (i = 0; i < this.m_KeyStruct.size(); i++)
          {
            if (!((JITCertKeyStruct)this.m_KeyStruct.get(i)).m_Cert.getSubject().equalsIgnoreCase(paramString))
              continue;
            this.m_KeyStruct.remove(i);
            break;
          }
          this.m_keyStore.deleteEntry(paramString);
        }
        else
        {
          return false;
        }
    }
    catch (Exception localException)
    {
      Object localObject = new ConfigException(ConfigException.KEY_STORE_ERROR, "密钥存储错误", localException);
      if (this.m_filePath != null)
        ((ConfigException)localObject).appendMsg(this.m_filePath);
      throw ((Throwable)localObject);
    }
    Key localKey2 = null;
    try
    {
      localKey2 = Parser.convertKey(paramJKey);
    }
    catch (PKIException localPKIException)
    {
      throw new ConfigException(localPKIException.getErrCode(), localPKIException.getErrDescEx(), localPKIException);
    }
    Certificate[] arrayOfCertificate = new Certificate[paramArrayOfX509Cert.length];
    try
    {
      for (i = 0; i < paramArrayOfX509Cert.length; i++)
        arrayOfCertificate[i] = JIT2JavaCert(paramArrayOfX509Cert[i]);
    }
    catch (IDAException localIDAException)
    {
      throw new ConfigException(localIDAException.getErrCode(), localIDAException.getErrDescEx(), localIDAException);
    }
    try
    {
      this.m_keyStore.setKeyEntry(paramString, localKey2, this.m_pwd, arrayOfCertificate);
    }
    catch (KeyStoreException localKeyStoreException)
    {
      ConfigException localConfigException = new ConfigException(ConfigException.ADDKEY_ERROR, "导入密钥错误", localKeyStoreException);
      if (this.m_filePath != null)
        localConfigException.appendMsg(this.m_filePath);
      throw localConfigException;
    }
    return true;
  }

  private boolean addCertKeyStruct(JITCertKeyStruct paramJITCertKeyStruct, boolean paramBoolean)
    throws ConfigException
  {
    X509Cert[] arrayOfX509Cert = { paramJITCertKeyStruct.m_Cert };
    JKey localJKey = paramJITCertKeyStruct.m_JPriKey;
    return addJITKEY(arrayOfX509Cert[0].getSubject().toLowerCase(), arrayOfX509Cert, localJKey, paramBoolean);
  }

  public boolean addJITCert(String paramString, X509Cert paramX509Cert, boolean paramBoolean)
    throws ConfigException
  {
    try
    {
      Certificate localCertificate = JIT2JavaCert(paramX509Cert);
      if (this.m_keyStore.isCertificateEntry(paramString))
      {
        if (!paramBoolean)
          return false;
        this.m_keyStore.deleteEntry(paramString);
        this.m_keyStore.setCertificateEntry(paramString, localCertificate);
      }
      else if (this.m_keyStore.isKeyEntry(paramString))
      {
        if (paramBoolean)
        {
          Key localKey = this.m_keyStore.getKey(paramString, this.m_pwd);
          addJITKEY(paramString, new X509Cert[] { paramX509Cert }, new JKey("RSA_Private", localKey.getEncoded()), paramBoolean);
        }
        else
        {
          return false;
        }
      }
      else
      {
        this.m_keyStore.setCertificateEntry(paramString, localCertificate);
        this.m_TrustCerts.add(paramX509Cert);
      }
    }
    catch (IDAException localIDAException)
    {
      throw new ConfigException(localIDAException.getErrCode(), localIDAException.getErrDescEx(), localIDAException);
    }
    catch (Exception localException)
    {
      throw new ConfigException(ConfigException.ADDCERT_ERROR, "添加证书失败", localException);
    }
    return true;
  }

  public String generalP10(String paramString1, String paramString2, String paramString3, int paramInt)
    throws ConfigException
  {
    JKeyPair localJKeyPair = generalKeyPair(paramString2, paramInt);
    X509Cert localX509Cert = generalTempCert(localJKeyPair, paramString1);
    JITCertKeyStruct localJITCertKeyStruct = new JITCertKeyStruct();
    localJITCertKeyStruct.m_Cert = localX509Cert;
    localJITCertKeyStruct.m_JPriKey = localJKeyPair.getPrivateKey();
    if (!this.m_KeyStruct.contains(localJITCertKeyStruct))
      this.m_KeyStruct.add(localJITCertKeyStruct);
    if (!addCertKeyStruct(localJITCertKeyStruct, true))
      return null;
    PKCS10 localPKCS10 = new PKCS10(this.m_session);
    try
    {
      return new String(localPKCS10.generateCertificationRequestData_B64(paramString3, paramString1, localJKeyPair.getPublicKey(), null, localJKeyPair.getPrivateKey()));
    }
    catch (PKIException localPKIException)
    {
    }
    throw new ConfigException(localPKIException.getErrCode(), localPKIException.getErrDescEx(), localPKIException);
  }

  public boolean generalP10(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4)
    throws ConfigException
  {
    String str = generalP10(paramString1, paramString2, paramString3, paramInt);
    File localFile = new File(paramString4);
    try
    {
      new File(localFile.getParent()).mkdirs();
    }
    catch (Exception localException1)
    {
    }
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      localFileOutputStream.write(str.getBytes());
      localFileOutputStream.flush();
      localFileOutputStream.close();
    }
    catch (Exception localException2)
    {
      ConfigException localConfigException = new ConfigException(ConfigException.WRITE_FILE_ERROR, "写文件错误", localException2);
      localConfigException.appendMsg(paramString4);
      throw localConfigException;
    }
    return true;
  }

  private JKeyPair generalKeyPair(String paramString, int paramInt)
    throws ConfigException
  {
    JKeyPair localJKeyPair = null;
    Mechanism localMechanism = null;
    try
    {
      if (this.m_deviceID.equalsIgnoreCase("JSOFT_LIB"))
      {
        localMechanism = new Mechanism(paramString);
        localJKeyPair = this.m_session.generateKeyPair(localMechanism, paramInt);
      }
      else
      {
        GenKeyAttribute localGenKeyAttribute = new GenKeyAttribute();
        localGenKeyAttribute.setKeyNum(paramInt);
        localMechanism = new Mechanism(paramString, localGenKeyAttribute);
        localJKeyPair = this.m_session.generateKeyPair(localMechanism, 0);
      }
    }
    catch (PKIException localPKIException)
    {
      ConfigException localConfigException = new ConfigException(localPKIException.getErrCode(), localPKIException.getErrDescEx(), localPKIException);
      throw localConfigException;
    }
    return localJKeyPair;
  }

  private X509Cert generalTempCert(JKeyPair paramJKeyPair, String paramString)
    throws ConfigException
  {
    X509Cert localX509Cert = null;
    X509CertGenerator localX509CertGenerator = new X509CertGenerator();
    try
    {
      localX509CertGenerator.setIssuer(paramString);
      localX509CertGenerator.setSubject(paramString);
      localX509CertGenerator.setPublicKey(paramJKeyPair.getPublicKey());
      localX509CertGenerator.setNotBefore(new Date());
      localX509CertGenerator.setNotAfter(new Date());
      localX509CertGenerator.setSerialNumber("1");
      localX509CertGenerator.setSignatureAlg("SHA1withRSAEncryption");
      byte[] arrayOfByte = localX509CertGenerator.generateX509Cert(paramJKeyPair.getPrivateKey(), this.m_session);
      localX509Cert = new X509Cert(arrayOfByte);
    }
    catch (PKIException localPKIException)
    {
      ConfigException localConfigException = new ConfigException(localPKIException.getErrCode(), localPKIException.getErrDescEx(), localPKIException);
      localConfigException.appendMsg("临时证书");
      throw localConfigException;
    }
    return localX509Cert;
  }

  public boolean importCert(String paramString, X509Cert paramX509Cert)
    throws ConfigException
  {
    boolean bool = false;
    JKey localJKey = null;
    byte[] arrayOfByte = null;
    try
    {
      arrayOfByte = this.m_keyStore.getKey(paramString, this.m_pwd).getEncoded();
      localJKey = new JKey("RSA_Private", arrayOfByte);
    }
    catch (Exception localException)
    {
      throw new ConfigException(ConfigException.KEY_STORE_ERROR, "证书存储错误", localException);
    }
    bool = addJITKEY(paramString, new X509Cert[] { paramX509Cert }, localJKey, true);
    return true;
  }

  public static X509Cert Java2JITCert(Certificate paramCertificate)
    throws IDAException
  {
    if (paramCertificate == null)
      return null;
    X509Cert localX509Cert = null;
    try
    {
      localX509Cert = new X509Cert(paramCertificate.getEncoded());
    }
    catch (CertificateEncodingException localCertificateEncodingException)
    {
      throw new IDAException(ConfigException.ENCODE_ERROR, "证书编码错误", localCertificateEncodingException);
    }
    catch (PKIException localPKIException)
    {
      throw new IDAException(localPKIException.getErrCode(), localPKIException.getErrDescEx(), localPKIException);
    }
    return localX509Cert;
  }

  public static Certificate JIT2JavaCert(X509Cert paramX509Cert)
    throws IDAException
  {
    if (paramX509Cert == null)
      return null;
    Certificate localCertificate = null;
    try
    {
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramX509Cert.getEncoded());
      CertificateFactory localCertificateFactory = CertificateFactory.getInstance("X.509");
      localCertificate = localCertificateFactory.generateCertificate(localByteArrayInputStream);
    }
    catch (PKIException localPKIException)
    {
      throw new IDAException(localPKIException.getErrCode(), localPKIException.getErrDescEx(), localPKIException);
    }
    catch (CertificateException localCertificateException)
    {
      throw new IDAException(ConfigException.DECODE_ERROR, "证书码错误", localCertificateException);
    }
    return localCertificate;
  }

  public static void main(String[] paramArrayOfString)
  {
    try
    {
      JITKeyStore localJITKeyStore = new JITKeyStore(false, "./myTestKeyStore.jks", "11111111".toCharArray());
      System.out.println("打开KeyStore成功");
      if (localJITKeyStore.generalP10("CN=gx2,C=CN", "RSA", "SHA1withRSAEncryption", 1024, "./certreq.req"))
        System.out.println("产生P10成功");
      X509Cert localX509Cert = new X509Cert(new FileInputStream("a.cer"));
      if (localJITKeyStore.addJITCert(localX509Cert.getSubject().toLowerCase(), localX509Cert, true))
        System.out.println("导入信任证书成功");
      if (localJITKeyStore.importCert("CN=gx2,C=CN", localX509Cert))
        System.out.println("导入证书成功");
      if (localJITKeyStore.saveToFile())
        System.out.println("保存成功");
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public Vector get_KeyStruct()
  {
    return this.m_KeyStruct;
  }

  public Vector get_TrustCerts()
  {
    return this.m_TrustCerts;
  }

  public boolean addJITCert_pkcs11(X509Cert paramX509Cert, int paramInt)
    throws ConfigException
  {
    try
    {
      boolean bool = ((JHARDLib)this.m_session).destroyCertObject(null, PKIToolConfig.getNoExportRSAKey(paramInt).getBytes());
      ((JHARDLib)this.m_session).createCertObject(paramX509Cert.getSubject().getBytes(), paramX509Cert.getEncoded(), PKIToolConfig.getNoExportRSAKey(paramInt).getBytes());
    }
    catch (IDAException localIDAException)
    {
      throw new ConfigException(localIDAException.getErrCode(), localIDAException.getErrDescEx(), localIDAException);
    }
    catch (Exception localException)
    {
      throw new ConfigException(ConfigException.ADDCERT_ERROR, "添加证书失败", localException);
    }
    return true;
  }

  public boolean addJITTrustCert_pkcs11(X509Cert paramX509Cert)
    throws ConfigException
  {
    try
    {
      boolean bool = ((JHARDLib)this.m_session).destroyCertObject(paramX509Cert.getSubject().getBytes(), paramX509Cert.getSubject().getBytes());
      ((JHARDLib)this.m_session).createCertObject(paramX509Cert.getSubject().getBytes(), paramX509Cert.getEncoded(), "trustcertid".getBytes());
    }
    catch (IDAException localIDAException)
    {
      throw new ConfigException(localIDAException.getErrCode(), localIDAException.getErrDescEx(), localIDAException);
    }
    catch (Exception localException)
    {
      throw new ConfigException(ConfigException.ADDCERT_ERROR, "添加证书失败", localException);
    }
    return true;
  }

  public class JITCertKeyStruct
  {
    public X509Cert m_Cert = null;
    public JKey m_JPriKey = null;

    public JITCertKeyStruct()
    {
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.globalconfig.util.JITKeyStore
 * JD-Core Version:    0.6.0
 */