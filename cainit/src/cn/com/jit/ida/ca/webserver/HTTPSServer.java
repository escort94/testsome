package cn.com.jit.ida.ca.webserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.config.ServerConfig;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.PKIToolConfig;
import java.net.InetAddress;
import javax.net.ssl.KeyManagerFactory;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Embedded;
import org.apache.coyote.http11.Http11Protocol;

public class HTTPSServer
{
  private String keyStoreFile = null;
  private String keyStorePass = null;
  private String appBase = null;
  private String appContextPath = null;
  private String appDocBase = null;
  private String catalinaHome = null;
  private String catalinaBase = null;
  private String hostIP = null;
  private int hostPort = 443;
  private Embedded emTomcat = null;
  private InternalConfig internalConfig = null;
  private String commDeviceID = null;
  private String commKeyID = null;

  private void init()
    throws WebServerException
  {
    ServerConfig localServerConfig = null;
    CryptoConfig localCryptoConfig = null;
    try
    {
      localServerConfig = ServerConfig.getInstance();
      this.internalConfig = InternalConfig.getInstance();
      localCryptoConfig = CryptoConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
      throw new WebServerException(localIDAException.getErrCode(), localIDAException.getErrDescEx(), localIDAException.getHistory());
    }
    this.catalinaHome = localServerConfig.getSecureWebHomePath();
    this.catalinaBase = localServerConfig.getSecureWebHomePath();
    this.keyStoreFile = localServerConfig.getCommunicateCert();
    this.keyStorePass = localServerConfig.getCommunicateCertPWD();
    this.appBase = localServerConfig.getSecureWebAppBasePath();
    this.appContextPath = localServerConfig.getSecureWebAppPath();
    this.appDocBase = localServerConfig.getSecureWebDocBasePath();
    this.hostIP = localServerConfig.getSecureWebServerAddress();
    this.hostPort = localServerConfig.getSecureWebPort();
    this.commDeviceID = localCryptoConfig.getCommDeviceID();
  }

  public void startTomcat()
    throws WebServerException
  {
    init();
    System.setProperty("catalina.home", this.catalinaHome);
    System.setProperty("catalina.base", this.catalinaHome);
    Engine localEngine = this.emTomcat.createEngine();
    localEngine.setName("ssl_server_engine");
    localEngine.setDefaultHost("localhost");
    Host localHost = this.emTomcat.createHost("localhost", this.appBase);
    localEngine.addChild(localHost);
    Context localContext = this.emTomcat.createContext(this.appContextPath, this.appDocBase);
    localHost.addChild(localContext);
    this.emTomcat.addEngine(localEngine);
    Connector localConnector = this.emTomcat.createConnector((InetAddress)null, this.hostPort, true);
    Http11Protocol localHttp11Protocol = (Http11Protocol)localConnector.getProtocolHandler();
    localHttp11Protocol.setProtocol(this.internalConfig.getWebProtocol());
    String str1 = System.getProperty("java.vm.vendor");
    String str2;
    if (this.commDeviceID.equalsIgnoreCase("JSOFT_LIB"))
    {
      localConnector.setProperty("keystoreType", "JKS");
      localConnector.setProperty("keystore", this.keyStoreFile);
      localConnector.setProperty("keystorePass", this.keyStorePass);
      if (str1.toUpperCase().indexOf("IBM") != -1)
      {
        str2 = KeyManagerFactory.getDefaultAlgorithm();
        localConnector.setProperty("algorithm", str2);
        localHttp11Protocol.setProtocol("SSL_TLSWithIBM");
      }
    }
    else
    {
      str2 = null;
      try
      {
        str2 = PKIToolConfig.getP11Password();
      }
      catch (PKIException localPKIException1)
      {
        throw new WebServerException(localPKIException1.getErrCode(), localPKIException1.getErrDescEx(), localPKIException1.getHistory());
      }
      String str3;
      if (str1.toUpperCase().indexOf("SUN") != -1)
      {
        localConnector.setProperty("keystoreType", "PKCS11");
        str3 = null;
        try
        {
          str3 = PKIToolConfig.getP11ProviderName();
        }
        catch (PKIException localPKIException2)
        {
          throw new WebServerException(localPKIException2.getErrCode(), localPKIException2.getErrDescEx(), localPKIException2.getHistory());
        }
        localConnector.setProperty("keystore", str3);
        localConnector.setProperty("keystorePass", str2);
      }
      else if (str1.toUpperCase().indexOf("HEWLETT") != -1)
      {
        localConnector.setProperty("keystoreType", "PKCS11");
        str3 = null;
        try
        {
          str3 = PKIToolConfig.getP11ProviderName();
        }
        catch (PKIException localPKIException3)
        {
          throw new WebServerException(localPKIException3.getErrCode(), localPKIException3.getErrDescEx(), localPKIException3.getHistory());
        }
        localConnector.setProperty("keystore", str3);
        localConnector.setProperty("keystorePass", str2);
      }
      else
      {
        localConnector.setProperty("keystoreType", "PKCS11IMPLKS");
        localConnector.setProperty("keystore", "IBMPKCS11Impl");
        localConnector.setProperty("keystorePass", this.keyStorePass);
        localConnector.setProperty("algorithm", "IbmX509");
        localHttp11Protocol.setProtocol("SSL_TLSWithIBM");
      }
    }
    localConnector.setProperty("truststoreType", "JKS");
    localConnector.setProperty("truststorePass", this.keyStorePass);
    localConnector.setProperty("truststoreFile", this.keyStoreFile);
    localConnector.setProperty("sslSessionTimeout", this.internalConfig.getSSLSessionTimeout());
    localHttp11Protocol.setSecure(true);
    localHttp11Protocol.setClientauth("true");
    this.emTomcat.addConnector(localConnector);
    try
    {
      this.emTomcat.start();
    }
    catch (Exception localException)
    {
      throw new WebServerException("8500", "HTTPS 服务启动失败", localException);
    }
  }

  public void stopTomcat()
    throws WebServerException
  {
    try
    {
      this.emTomcat.stop();
    }
    catch (Exception localException)
    {
      throw new WebServerException("8501", "HTTPS 服务关闭失败", localException);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.webserver.HTTPSServer
 * JD-Core Version:    0.6.0
 */