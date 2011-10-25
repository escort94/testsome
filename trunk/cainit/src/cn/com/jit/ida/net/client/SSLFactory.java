package cn.com.jit.ida.net.client;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.net.NetConfig;
import cn.com.jit.ida.net.NetException;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SSLFactory
{
  private String KeyStoreType = "JKS";
  private String KeyStoreTypePKCS11 = "PKCS11";
  private String KeyStoreTypeIBMPKCS11 = "PKCS11IMPLKS";
  private TrustManagerFactory trustManagerFactory = null;
  private NetConfig config;
  private CryptoConfig cryptoConfig;

  public SSLFactory(NetConfig paramNetConfig)
  {
    this.config = paramNetConfig;
  }

  public void setConfig(NetConfig paramNetConfig)
  {
    this.config = paramNetConfig;
  }

  public Socket getSocket(String paramString, int paramInt)
    throws NetException
  {
    if (this.config.getConnectType().equals("SOCKET"))
    {
      SocketFactory localSocketFactory = SocketFactory.getDefault();
      localObject = null;
      try
      {
        localObject = localSocketFactory.createSocket(paramString, paramInt);
      }
      catch (IOException localIOException1)
      {
        throw new NetException("03", "服务器拒绝连接，可能是服务器没有启动", localIOException1);
      }
      return localObject;
    }
    try
    {
      this.cryptoConfig = CryptoConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
      throw new NetException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException.getHistory());
    }
    SSLContext localSSLContext = null;
    Object localObject = null;
    KeyStore localKeyStore1 = null;
    String str1 = "12345678";
    String str2 = System.getProperty("java.vm.vendor");
    try
    {
      if (this.config.getKeyStoreType().equalsIgnoreCase("JKS"))
      {
        localObject = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        localKeyStore1 = KeyStore.getInstance(this.KeyStoreType);
        localSSLContext = SSLContext.getInstance(this.config.getSecureProtocol());
      }
      else if (str2.toUpperCase().indexOf("SUN") != -1)
      {
        localSSLContext = SSLContext.getInstance("SSL");
        localObject = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        localKeyStore1 = KeyStore.getInstance(this.KeyStoreTypePKCS11);
      }
      else if (str2.toUpperCase().indexOf("HEWLETT") != -1)
      {
        localSSLContext = SSLContext.getInstance("SSL");
        localObject = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        localKeyStore1 = KeyStore.getInstance(this.KeyStoreTypePKCS11);
      }
      else
      {
        localSSLContext = SSLContext.getInstance("SSL", "IBMJSSE2");
        localObject = KeyManagerFactory.getInstance("IBMX509", "IBMJSSE2");
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
    try
    {
      if (this.config.getKeyStoreType().equalsIgnoreCase("JKS"))
      {
        FileInputStream localFileInputStream1 = new FileInputStream(this.cryptoConfig.getCommKeyStore());
        localKeyStore1.load(localFileInputStream1, this.cryptoConfig.getCommKeyStorePWD().toCharArray());
        localFileInputStream1.close();
        ((KeyManagerFactory)localObject).init(localKeyStore1, this.cryptoConfig.getCommKeyStorePWD().toCharArray());
      }
      else
      {
        localKeyStore1.load(null, str1.toCharArray());
        ((KeyManagerFactory)localObject).init(localKeyStore1, str1.toCharArray());
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
    catch (IOException localIOException2)
    {
      throw new NetException("11", "密钥存储文件错误", localIOException2);
    }
    catch (CertificateException localCertificateException1)
    {
      throw new NetException("11", "密钥存储文件错误", localCertificateException1);
    }
    KeyStore localKeyStore2 = null;
    try
    {
      localKeyStore2 = KeyStore.getInstance(this.KeyStoreType);
      FileInputStream localFileInputStream2 = new FileInputStream(this.config.getTrustCerts());
      localKeyStore2.load(localFileInputStream2, this.config.getTrustPassword().toCharArray());
      localFileInputStream2.close();
      this.trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      this.trustManagerFactory.init(localKeyStore2);
      localSSLContext.init(((KeyManagerFactory)localObject).getKeyManagers(), this.trustManagerFactory.getTrustManagers(), null);
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
    catch (IOException localIOException3)
    {
      throw new NetException("11", "密钥存储文件错误", localIOException3);
    }
    catch (KeyManagementException localKeyManagementException)
    {
      throw new NetException("12", "获取密钥错误", localKeyManagementException);
    }
    SSLSocketFactory localSSLSocketFactory = localSSLContext.getSocketFactory();
    SSLSocket localSSLSocket = null;
    try
    {
      localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket(paramString, paramInt);
    }
    catch (IOException localIOException4)
    {
      throw new NetException("03", "服务器拒绝连接，可能是服务器没有启动", localIOException4);
    }
    try
    {
      localSSLSocket.startHandshake();
    }
    catch (IOException localIOException5)
    {
      throw new NetException("13", "建立安全连接失败", localIOException5);
    }
    return (Socket)localSSLSocket;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.net.client.SSLFactory
 * JD-Core Version:    0.6.0
 */