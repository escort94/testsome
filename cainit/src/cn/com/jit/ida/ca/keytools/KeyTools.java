package cn.com.jit.ida.ca.keytools;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.CAServer;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.ca.control.CommandConstant;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.initserver.GenP10;
import cn.com.jit.ida.ca.initserver.InitServerException;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.Information;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cert.X509CertGenerator;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.JKey;
import cn.com.jit.ida.util.pki.cipher.JKeyPair;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.pkcs.PKCS10;
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
import java.util.Date;
import java.util.Vector;

public class KeyTools
{
  String deviceName;
  String keyType;
  int keyLen;
  Session session;
  char[] keyStorePwd;
  String keyStrorePath;
  String certReqPath;
  int CASignKeyID;
  int actionType;
  private String caDN;

  private int ReadInitInfo()
    throws InitServerException
  {
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
    try
    {
      this.actionType = ConfigTool.displayMenu("请选择该申请用途：", new String[] { "初始化子CA", "更新子CA证书", "产生交叉认证申请书" });
      if (this.actionType == 3)
        return 1;
      if (this.actionType == 0)
        return 0;
      if (this.actionType == 1)
      {
        this.caDN = ConfigTool.getDN("请输入子CA的DN:");
        if (this.caDN == null)
          return 0;
      }
      Object localObject1;
      Object localObject2;
      if (this.actionType == 2)
        try
        {
          DBManager localDBManager = DBManager.getInstance();
          localObject1 = localDBManager.getConfig("CAConfig");
          for (int k = 0; k < ((Vector)localObject1).size(); k++)
          {
            localObject2 = (Information)((Vector)localObject1).get(k);
            String str2 = ((Information)localObject2).getName();
            if (!str2.equals("CASubject"))
              continue;
            this.caDN = ((Information)localObject2).getValue();
            break;
          }
        }
        catch (IDAException localIDAException)
        {
          throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException.getHistory());
        }
      int i = ConfigTool.displayMenu("请选择加密设备：", new String[] { "软加密库(默认)", "加密机" }, 1);
      if (i == 1)
      {
        this.deviceName = "JSOFT_LIB";
      }
      else if (i == 2)
      {
        this.deviceName = "JSJY05B_LIB";
        localObject1 = JCrypto.getInstance();
        try
        {
          ((JCrypto)localObject1).initialize(this.deviceName, new Integer(2));
        }
        catch (PKIException localPKIException)
        {
          throw new InitServerException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
        }
      }
      else if (i == 0)
      {
        return 0;
      }
      if (this.deviceName.equals("JSOFT_LIB"))
      {
        int j = 0;
        j = ConfigTool.displayMenu("请选择密钥类型：", new String[] { "RSA(默认)" }, 1);
        if (j == 1)
          this.keyType = "RSA";
        else
          return 0;
        int m = ConfigTool.displayMenu("**                   密钥长度                 **", new String[] { "1024(默认)", "2048" }, 1);
        if (m == 1)
          this.keyLen = 1024;
        else if (m == 2)
          this.keyLen = 2048;
        else if (m == 0)
          return 0;
        localObject2 = ConfigTool.getNewPassword("请输入密钥存储密码", 6, 16);
        if (localObject2 == null)
        {
          System.out.println("用户取消");
          return 0;
        }
        this.keyStorePwd = ((String)localObject2).toCharArray();
        this.keyStrorePath = ConfigTool.getFilePathFromUser("请输入密钥保存的文件名\n(默认： ./keystore/subCAKeystore.jks，直接回车使用默认文件名)：", ConfigTool.FILE_TO_WRITE, "./keystore/subCAKeystore.jks");
        if (this.keyStrorePath == null)
          return 0;
        this.certReqPath = ConfigTool.getFilePathFromUser("请输入证书申请书保存的文件名\n(默认：./keystore/subCACertReq.req，直接回车使用默认文件名)：", ConfigTool.FILE_TO_WRITE, "./keystore/subCACertReq.req");
        if (this.certReqPath == null)
          return 0;
      }
      else
      {
        this.keyType = "RSA";
        while (true)
        {
          System.out.println("请输入密钥对标识（0-退出）：");
          String str1 = localBufferedReader.readLine().trim();
          if (str1.equals("0"))
            return 0;
          try
          {
            this.CASignKeyID = Integer.parseInt(str1);
          }
          catch (NumberFormatException localNumberFormatException)
          {
            System.out.println("输入有误，请重新输入。");
          }
          continue;
          if (this.CASignKeyID >= 0)
            break;
          System.out.println("输入有误，请重新输入。");
        }
        String str1 = ConfigTool.getNewPassword("请输入密钥存储密码", 6, 16);
        if (str1 == null)
        {
          System.out.println("用户取消");
          return 0;
        }
        this.keyStorePwd = str1.toCharArray();
        this.keyStrorePath = ConfigTool.getFilePathFromUser("请输入密钥保存的文件名\n(默认： ./keystore/subCAKeystore.jks，直接回车使用默认文件名)：", ConfigTool.FILE_TO_WRITE, "./keystore/subCAKeystore.jks");
        if (this.keyStrorePath == null)
          return 0;
        this.certReqPath = ConfigTool.getFilePathFromUser("请输入证书申请书保存的文件名\n(默认：./keystore/subCACertReq.req，直接回车使用默认文件名)：", ConfigTool.FILE_TO_WRITE, "./keystore/subCACertReq.req");
        if (this.certReqPath == null)
          return 0;
      }
    }
    catch (IOException localIOException)
    {
      System.err.println("文件名错误");
      return 0;
    }
    return 1;
  }

  private void GenPKCS10()
    throws InitServerException
  {
    GenP10 localGenP10;
    if (this.deviceName.equals("JSOFT_LIB"))
    {
      localGenP10 = new GenP10(this.caDN, this.keyType, this.keyLen, this.session);
      X509CertGenerator localX509CertGenerator = new X509CertGenerator();
      try
      {
        localX509CertGenerator.setIssuer(this.caDN);
        localX509CertGenerator.setSubject(this.caDN);
        localX509CertGenerator.setSerialNumber("01");
        Date localDate1 = new Date();
        localX509CertGenerator.setNotBefore(localDate1);
        Date localDate2 = new Date();
        localX509CertGenerator.setNotAfter(localDate2);
        localX509CertGenerator.setSignatureAlg("SHA1withRSAEncryption");
        localX509CertGenerator.setPublicKey(localGenP10.getKeyPair().getPublicKey());
        byte[] arrayOfByte = localX509CertGenerator.generateX509Cert(localGenP10.getKeyPair().getPrivateKey(), this.session);
        GenKeyStore(arrayOfByte, localGenP10.getPrivateKey());
        SavePKCS10(localGenP10.getP10String());
      }
      catch (PKIException localPKIException)
      {
        throw new InitServerException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
      }
      catch (Exception localException2)
      {
        throw new InitServerException("0958", "保存证书文件错误", localException2);
      }
    }
    else
    {
      localGenP10 = new GenP10(this.caDN, this.keyType, this.keyLen, this.session, this.CASignKeyID);
      try
      {
        System.out.println("密钥产生完毕,保存在:" + this.keyStrorePath);
        SavePKCS10(localGenP10.getP10String());
      }
      catch (Exception localException1)
      {
        throw new InitServerException("0958", "保存证书文件错误", localException1);
      }
    }
  }

  private void SavePKCS10(String paramString)
    throws Exception
  {
    FileOutputStream localFileOutputStream = new FileOutputStream(this.certReqPath);
    localFileOutputStream.write(paramString.getBytes());
    localFileOutputStream.flush();
    localFileOutputStream.close();
    System.out.println("申请书产生成功.");
  }

  private void GenKeyStore(byte[] paramArrayOfByte, Key paramKey)
    throws InitServerException
  {
    Certificate localCertificate = null;
    try
    {
      KeyStore localKeyStore = KeyStore.getInstance("JKS");
      localKeyStore.load(null, null);
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      CertificateFactory localCertificateFactory = CertificateFactory.getInstance("X.509");
      localCertificate = localCertificateFactory.generateCertificate(localByteArrayInputStream);
      Certificate[] arrayOfCertificate = { localCertificate };
      localKeyStore.setKeyEntry(this.caDN, paramKey, this.keyStorePwd, arrayOfCertificate);
      FileOutputStream localFileOutputStream = null;
      localFileOutputStream = new FileOutputStream(new File(this.keyStrorePath));
      localKeyStore.store(localFileOutputStream, this.keyStorePwd);
      localFileOutputStream.close();
      System.out.println("密钥产生成功.");
    }
    catch (Exception localException)
    {
      throw new InitServerException("0917", "产生KeyStore错误", localException);
    }
  }

  public boolean run()
    throws InitServerException
  {
    boolean bool = false;
    if (ReadInitInfo() == 0)
      return false;
    Operation localOperation = new Operation();
    localOperation.setOperatorDN("系统管理员");
    localOperation.setOptTime(System.currentTimeMillis());
    if (this.actionType == 3)
    {
      localOperation.setOptType(CommandConstant.GenerateAcrossCert);
      try
      {
        bool = GeneralAcrossReq();
        if (bool)
        {
          localOperation.setResult(1);
          try
          {
            LogManager.getOPtLogger().info(localOperation);
          }
          catch (IDAException localIDAException1)
          {
          }
        }
      }
      catch (InitServerException localInitServerException1)
      {
        localOperation.setResult(0);
        try
        {
          LogManager.getOPtLogger().info(localOperation);
        }
        catch (IDAException localIDAException3)
        {
        }
        throw localInitServerException1;
      }
      return bool;
    }
    try
    {
      openSession();
      GenPKCS10();
      saveConfig();
      if (this.actionType == 2)
      {
        localOperation.setOptType(CommandConstant.UpdateChild);
        localOperation.setResult(1);
        try
        {
          LogManager.getOPtLogger().info(localOperation);
        }
        catch (IDAException localIDAException2)
        {
        }
      }
    }
    catch (InitServerException localInitServerException2)
    {
      if (this.actionType == 2)
      {
        localOperation.setOptType(CommandConstant.UpdateChild);
        localOperation.setResult(0);
        try
        {
          LogManager.getOPtLogger().info(localOperation);
        }
        catch (IDAException localIDAException4)
        {
        }
      }
      throw localInitServerException2;
    }
    return true;
  }

  private void openSession()
    throws InitServerException
  {
    JCrypto localJCrypto = JCrypto.getInstance();
    try
    {
      if (!this.deviceName.equalsIgnoreCase("JSOFT_LIB"))
        localJCrypto.initialize(this.deviceName, new Integer(2));
      localJCrypto.initialize("JSOFT_LIB", null);
      this.session = localJCrypto.openSession(this.deviceName);
    }
    catch (PKIException localPKIException)
    {
      throw new InitServerException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
  }

  public void saveConfig()
  {
    try
    {
      ParseXML localParseXML;
      if (this.actionType == 1)
      {
        localParseXML = new ParseXML("./config/init.xml");
        if (this.caDN != null)
          localParseXML.setString("CASubject", this.caDN);
      }
      else
      {
        localParseXML = new ParseXML("./config/CAConfig.xml");
      }
      if (this.deviceName != null)
        localParseXML.setString("CASigningDeviceID", this.deviceName);
      if (this.keyStrorePath != null)
        localParseXML.setString("SigningKeyStore", this.keyStrorePath);
      if (this.keyStorePwd != null)
        localParseXML.setString("SigningKeyStorePWD", new String(this.keyStorePwd));
      if (this.CASignKeyID != 0)
        localParseXML.setString("CASigningKeyID", String.valueOf(this.CASignKeyID));
    }
    catch (ConfigException localConfigException)
    {
    }
  }

  public boolean GeneralAcrossReq()
    throws InitServerException
  {
    if (!CAServer.isBeenInit())
      return false;
    CAConfig localCAConfig;
    CryptoConfig localCryptoConfig;
    try
    {
      localCAConfig = CAConfig.getInstance();
      localCryptoConfig = CryptoConfig.getInstance();
      localCryptoConfig.init();
    }
    catch (IDAException localIDAException)
    {
      throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
    JCrypto localJCrypto = JCrypto.getInstance();
    Integer localInteger = null;
    if (localCryptoConfig.getDeviceID().equalsIgnoreCase("JSJY05B_LIB"))
      localInteger = new Integer(2);
    try
    {
      localJCrypto.initialize(localCryptoConfig.getDeviceID(), localInteger);
      this.session = localJCrypto.openSession(localCryptoConfig.getDeviceID());
    }
    catch (PKIException localPKIException1)
    {
      throw new InitServerException(localPKIException1.getErrCode(), localPKIException1.getErrDesc(), localPKIException1);
    }
    byte[] arrayOfByte = null;
    try
    {
      JKey localJKey1 = localCAConfig.getRootCert().getPublicKey();
      JKey localJKey2 = localCAConfig.getPrivateKey();
      String str = localCAConfig.getDN();
      PKCS10 localPKCS10 = new PKCS10(this.session);
      arrayOfByte = localPKCS10.generateCertificationRequestData_B64("SHA1withRSAEncryption", str, localJKey1, null, localJKey2);
    }
    catch (PKIException localPKIException2)
    {
      throw new InitServerException(localPKIException2.getErrCode(), localPKIException2.getErrDesc(), localPKIException2);
    }
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream("./cert.req");
      localFileOutputStream.write(arrayOfByte);
    }
    catch (Exception localException)
    {
      throw new InitServerException("0958", "存储证书错误", localException);
    }
    return true;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.keytools.KeyTools
 * JD-Core Version:    0.6.0
 */