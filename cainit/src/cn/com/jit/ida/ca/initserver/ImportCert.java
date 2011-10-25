package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.ServerConfig;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Enumeration;

public class ImportCert
{
  P7Bxp p7bxp;
  private ParseXML config;

  public ImportCert(byte[] paramArrayOfByte, ParseXML paramParseXML)
    throws InitServerException
  {
    this.config = paramParseXML;
    this.p7bxp = new P7Bxp(new String(paramArrayOfByte));
  }

  public ImportCert(String paramString, ParseXML paramParseXML)
    throws InitServerException
  {
    this.config = paramParseXML;
    try
    {
      FileInputStream localFileInputStream = new FileInputStream(paramString);
      byte[] arrayOfByte = new byte[localFileInputStream.available()];
      localFileInputStream.read(arrayOfByte);
      this.p7bxp = new P7Bxp(new String(arrayOfByte));
    }
    catch (IOException localIOException)
    {
      throw new InitServerException("0913", "读文件失败", localIOException);
    }
  }

  public ImportCert(String paramString)
    throws InitServerException
  {
    this.config = this.config;
    try
    {
      this.config = new ParseXML("./config/CAConfig.xml");
    }
    catch (ConfigException localConfigException)
    {
      throw new InitServerException(localConfigException.getErrCode(), localConfigException.getErrDesc(), localConfigException);
    }
    try
    {
      FileInputStream localFileInputStream = new FileInputStream(paramString);
      byte[] arrayOfByte = new byte[localFileInputStream.available()];
      localFileInputStream.read(arrayOfByte);
      this.p7bxp = new P7Bxp(new String(arrayOfByte));
    }
    catch (IOException localIOException)
    {
      throw new InitServerException("0913", "读文件失败", localIOException);
    }
  }

  public ImportCert()
    throws InitServerException
  {
    try
    {
      this.config = new ParseXML("./config/CAConfig.xml");
    }
    catch (ConfigException localConfigException)
    {
      throw new InitServerException(localConfigException.getErrCode(), localConfigException.getErrDesc(), localConfigException);
    }
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
    String str = null;
    Object localObject;
    while (true)
    {
      System.out.print("请输入要导入的证书链文件（*.p7b,quit退出）：");
      try
      {
        str = localBufferedReader.readLine().trim();
      }
      catch (IOException localIOException1)
      {
        throw new InitServerException("0999", "系统错误", localIOException1);
      }
      if (str.equalsIgnoreCase("quit"));
      localObject = new File(str);
      if (((File)localObject).exists())
        break;
      System.out.println("文件不存在,请重新输入");
    }
    try
    {
      localObject = new FileInputStream(str);
      byte[] arrayOfByte = new byte[((FileInputStream)localObject).available()];
      ((FileInputStream)localObject).read(arrayOfByte);
      this.p7bxp = new P7Bxp(new String(arrayOfByte));
    }
    catch (IOException localIOException2)
    {
      throw new InitServerException("0913", "读文件失败", localIOException2);
    }
  }

  public void run()
    throws InitServerException
  {
    String str1 = this.p7bxp.getUserCert().getSubject();
    String str2 = this.config.getString("SigningKeyStore");
    char[] arrayOfChar = this.config.getString("SigningKeyStorePWD").toCharArray();
    String str3 = this.config.getString("CASigningDeviceID");
    FileInputStream localFileInputStream = null;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if (str3.equalsIgnoreCase("JSOFT_LIB"))
    {
      try
      {
        localFileInputStream = new FileInputStream(str2);
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        throw new InitServerException("0913", "读文件错误", localFileNotFoundException);
      }
      try
      {
        KeyStore localKeyStore1 = KeyStore.getInstance("JKS");
        localKeyStore1.load(localFileInputStream, arrayOfChar);
        localFileInputStream.close();
        localObject1 = null;
        localObject2 = localKeyStore1.aliases();
        while (((Enumeration)localObject2).hasMoreElements())
        {
          localObject1 = (String)((Enumeration)localObject2).nextElement();
          if (localKeyStore1.isKeyEntry((String)localObject1))
            break;
          localObject1 = null;
        }
        if (localObject1 == null)
          throw new InitServerException("0949", "密钥存储文件错误");
        localObject3 = localKeyStore1.getKey((String)localObject1, arrayOfChar);
        if (localKeyStore1.containsAlias((String)localObject1))
          localKeyStore1.deleteEntry((String)localObject1);
        KeyStore localKeyStore2 = KeyStore.getInstance("JKS");
        localKeyStore2.load(null, null);
        localKeyStore2.setKeyEntry(this.p7bxp.getUserCert().getSubject(), (Key)localObject3, arrayOfChar, new Certificate[] { this.p7bxp.getJavaUserCert() });
        X509Cert[] arrayOfX509Cert = this.p7bxp.getCertChain();
        Certificate[] arrayOfCertificate = this.p7bxp.getJavaCert();
        for (int j = 0; j < arrayOfX509Cert.length; j++)
        {
          if (arrayOfX509Cert[j].getSubject().equalsIgnoreCase(this.p7bxp.getUserCert().getSubject()))
            continue;
          localKeyStore2.setCertificateEntry(arrayOfX509Cert[j].getSubject(), arrayOfCertificate[j]);
        }
        FileOutputStream localFileOutputStream2 = null;
        localFileOutputStream2 = new FileOutputStream(str2);
        localKeyStore2.store(localFileOutputStream2, arrayOfChar);
        localFileOutputStream2.close();
      }
      catch (Exception localException1)
      {
        throw new InitServerException("0932", "根证书存储错误", localException1);
      }
    }
    else
    {
      File localFile = new File(str2);
      localFile.delete();
      try
      {
        localObject1 = KeyStore.getInstance("JKS");
        ((KeyStore)localObject1).load(null, null);
        Certificate[] localObject4 = this.p7bxp.getJavaCert();
        X509Cert[] localObject5 = this.p7bxp.getCertChain();
        for (int i = 0; i < localObject4.length; i++)
          ((KeyStore)localObject1).setCertificateEntry(localObject5[i].getSubject(), localObject4[i]);
        FileOutputStream localFileOutputStream1 = null;
        localFileOutputStream1 = new FileOutputStream(str2);
        ((KeyStore)localObject1).store(localFileOutputStream1, arrayOfChar);
        localFileOutputStream1.close();
      }
      catch (Exception localException2)
      {
        throw new InitServerException("0932", "根证书存储错误", localException2);
      }
    }
  }

  public static void importCertToComm(P7Bxp paramP7Bxp)
    throws InitServerException
  {
    ServerConfig localServerConfig = null;
    try
    {
      localServerConfig = ServerConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
      throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
    File localFile = new File(localServerConfig.getCommunicateCert());
    if (!localFile.exists())
      return;
    FileInputStream localFileInputStream = null;
    char[] arrayOfChar;
    KeyStore localKeyStore;
    try
    {
      localFileInputStream = new FileInputStream(localServerConfig.getCommunicateCert());
      arrayOfChar = localServerConfig.getCommunicateCertPWD().toCharArray();
      localKeyStore = KeyStore.getInstance("JKS");
      localKeyStore.load(localFileInputStream, arrayOfChar);
    }
    catch (Exception localException1)
    {
      throw new InitServerException("0949", "证书存储文件错误", localException1);
    }
    Certificate[] arrayOfCertificate = paramP7Bxp.getJavaCert();
    X509Cert[] arrayOfX509Cert = paramP7Bxp.getCertChain();
    try
    {
      for (int i = 0; i < arrayOfX509Cert.length; i++)
      {
        String str = arrayOfX509Cert[i].getSubject();
        if (localKeyStore.getCertificateAlias(arrayOfCertificate[i]) != null)
          continue;
        while (localKeyStore.containsAlias(str))
          str = str + String.valueOf(System.currentTimeMillis());
        localKeyStore.setCertificateEntry(str, arrayOfCertificate[i]);
      }
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new InitServerException("0949", "证书存储文件错误", localKeyStoreException);
    }
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(localServerConfig.getCommunicateCert());
      localKeyStore.store(localFileOutputStream, arrayOfChar);
    }
    catch (Exception localException2)
    {
      throw new InitServerException("0949", "证书存储文件错误", localException2);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.ImportCert
 * JD-Core Version:    0.6.0
 */