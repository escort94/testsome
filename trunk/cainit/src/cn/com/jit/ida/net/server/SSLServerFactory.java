package cn.com.jit.ida.net.server;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.net.NetConfig;
import cn.com.jit.ida.net.NetException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SSLServerFactory
{
  private String KeyStoreType = "JKS";
  private String KeyStoreTypePKCS11 = "PKCS11";
  private String KeyStoreTypeIBMPKCS11 = "PKCS11IMPLKS";
  TrustManagerFactory trustManagerFactory = null;
  NetConfig config;
  private CryptoConfig cryptoConfig;

  public SSLServerFactory(NetConfig paramNetConfig)
  {
    this.config = paramNetConfig;
  }

  public ServerSocket getServerSocket()
    throws NetException
  {
    ServerSocket localServerSocket = null;
    try
    {
      ServerSocketFactory localServerSocketFactory = getServerSocketFactory(this.config.getSecureProtocol());
      localServerSocket = localServerSocketFactory.createServerSocket(this.config.getPort(), 1024);
      if ((this.config.getNeedClientAuth()) && (this.config.getConnectType().equals("SSL")))
        ((SSLServerSocket)localServerSocket).setNeedClientAuth(true);
    }
    catch (IOException localIOException)
    {
      throw new NetException("14", "服务端口被占用", localIOException);
    }
    return localServerSocket;
  }

  private ServerSocketFactory getServerSocketFactory(String paramString)
    throws NetException
  {
    try
    {
      this.cryptoConfig = CryptoConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
      throw new NetException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException.getHistory());
    }
    if (this.config.getConnectType().equals("SOCKET"))
      return ServerSocketFactory.getDefault();
    SSLContext localSSLContext = null;
    KeyManagerFactory localKeyManagerFactory = null;
    KeyStore localKeyStore1 = null;
    String str1 = null;
    String str2 = "12345678";
    String str3 = System.getProperty("java.vm.vendor");
    try
    {
      str1 = this.cryptoConfig.getCommDeviceID();
    }
    catch (Exception localException1)
    {
      str1 = "";
    }
    try
    {
      if (str1.equalsIgnoreCase("JSOFT_LIB"))
      {
        localKeyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        localKeyStore1 = KeyStore.getInstance(this.KeyStoreType);
        localSSLContext = SSLContext.getInstance("SSL");
      }
      else if (str3.toUpperCase().indexOf("SUN") != -1)
      {
        localSSLContext = SSLContext.getInstance("SSL");
        localKeyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        localKeyStore1 = KeyStore.getInstance(this.KeyStoreTypePKCS11);
      }
      else if (str3.toUpperCase().indexOf("HEWLETT") != -1)
      {
        localSSLContext = SSLContext.getInstance("SSL");
        localKeyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        localKeyStore1 = KeyStore.getInstance(this.KeyStoreTypePKCS11);
      }
      else
      {
        localSSLContext = SSLContext.getInstance("SSL", "IBMJSSE2");
        localKeyManagerFactory = KeyManagerFactory.getInstance("IBMX509", "IBMJSSE2");
        localKeyStore1 = KeyStore.getInstance(this.KeyStoreTypeIBMPKCS11);
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException1)
    {
      throw new NetException("08", "算法类型不支持", localNoSuchAlgorithmException1);
    }
    catch (KeyStoreException localKeyStoreException1)
    {
      throw new NetException("09", "密钥存储类型不支持", localKeyStoreException1);
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      throw new NetException("98", "Provider不匹配", localNoSuchProviderException);
    }
    FileInputStream localFileInputStream = null;
    if (str1.equalsIgnoreCase("JSOFT_LIB"))
      try
      {
        localFileInputStream = new FileInputStream(this.cryptoConfig.getCommKeyStore());
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        throw new NetException("10", "密钥存储文件没有找到", localFileNotFoundException);
      }
    try
    {
      if (str1.equalsIgnoreCase("JSOFT_LIB"))
      {
        localKeyStore1.load(localFileInputStream, this.cryptoConfig.getCommKeyStorePWD().toCharArray());
        localFileInputStream.close();
        localKeyManagerFactory.init(localKeyStore1, this.cryptoConfig.getCommKeyStorePWD().toCharArray());
      }
      else
      {
        localKeyStore1.load(null, str2.toCharArray());
        localKeyManagerFactory.init(localKeyStore1, str2.toCharArray());
      }
    }
    catch (KeyStoreException localKeyStoreException2)
    {
      throw new NetException("09", "密钥存储类型不支持", localKeyStoreException2);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException2)
    {
      throw new NetException("08", "算法类型不支持", localNoSuchAlgorithmException2);
    }
    catch (UnrecoverableKeyException localUnrecoverableKeyException)
    {
      throw new NetException("15", "密钥存储文件没有找到", localUnrecoverableKeyException);
    }
    catch (IOException localIOException1)
    {
      throw new NetException("11", "密钥存储文件错误", localIOException1);
    }
    catch (CertificateException localCertificateException1)
    {
      throw new NetException("11", "密钥存储文件错误", localCertificateException1);
    }
    KeyStore localKeyStore2 = null;
    try
    {
      Object localObject;
      if (str1.equalsIgnoreCase("JSOFT_LIB"))
      {
        localKeyStore2 = KeyStore.getInstance(this.KeyStoreType);
        localObject = new FileInputStream(this.config.getTrustCerts());
        localKeyStore2.load((InputStream)localObject, this.config.getTrustPassword().toCharArray());
        ((FileInputStream)localObject).close();
        this.trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        this.trustManagerFactory.init(localKeyStore2);
        localSSLContext.init(localKeyManagerFactory.getKeyManagers(), this.trustManagerFactory.getTrustManagers(), null);
      }
      else
      {
        localObject = null;
        if (str3.toUpperCase().indexOf("SUN") != -1)
          localObject = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        else if (str3.toUpperCase().indexOf("HEWLETT") != -1)
          localObject = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        else
          try
          {
            localObject = TrustManagerFactory.getInstance("IbmX509", "IBMJSSE2");
          }
          catch (Exception localException2)
          {
          }
        ((TrustManagerFactory)localObject).init(localKeyStore1);
        localSSLContext.init(localKeyManagerFactory.getKeyManagers(), ((TrustManagerFactory)localObject).getTrustManagers(), null);
      }
    }
    catch (KeyStoreException localKeyStoreException3)
    {
      throw new NetException("09", "密钥存储类型不支持", localKeyStoreException3);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException3)
    {
      throw new NetException("08", "算法类型不支持", localNoSuchAlgorithmException3);
    }
    catch (CertificateException localCertificateException2)
    {
      throw new NetException("11", "密钥存储文件错误", localCertificateException2);
    }
    catch (IOException localIOException2)
    {
      throw new NetException("11", "密钥存储文件错误", localIOException2);
    }
    catch (KeyManagementException localKeyManagementException)
    {
      throw new NetException("12", "获取密钥错误", localKeyManagementException);
    }
    SSLServerSocketFactory localSSLServerSocketFactory = localSSLContext.getServerSocketFactory();
    return (ServerSocketFactory)localSSLServerSocketFactory;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.net.server.SSLServerFactory
 * JD-Core Version:    0.6.0
 */