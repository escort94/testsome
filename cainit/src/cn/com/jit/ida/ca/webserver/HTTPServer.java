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

public class HTTPServer
{
  private String appBase = null;
  private String appContextPath = null;
  private String appDocBase = null;
  private String catalinaHome = null;
  private String catalinaBase = null;
  private String hostIP = null;
  private int hostPort = 0;
  private String webType = null;
  private String keyStoreFile = null;
  private String keyStorePass = null;
  private Embedded emTomcat = null;
  private InternalConfig internalConfig = null;
  private String commDeviceID = null;

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
    this.catalinaHome = localServerConfig.getWebHomePath();
    this.catalinaBase = localServerConfig.getWebHomePath();
    this.keyStoreFile = localServerConfig.getCommunicateCert();
    this.keyStorePass = localServerConfig.getCommunicateCertPWD();
    this.appBase = localServerConfig.getWebAppBasePath();
    this.appContextPath = localServerConfig.getWebAppPath();
    this.appDocBase = localServerConfig.getWebDocBasePath();
    this.hostIP = localServerConfig.getWebServerAddress();
    this.hostPort = localServerConfig.getWebServerPort();
    this.webType = localServerConfig.getWebType();
    this.commDeviceID = localCryptoConfig.getCommDeviceID();
  }

  public void startTomcat()
    throws WebServerException
  {
    init();
    System.setProperty("catalina.home", this.catalinaHome);
    System.setProperty("catalina.base", this.catalinaHome);
    Engine localEngine = this.emTomcat.createEngine();
    localEngine.setName("server_engine");
    localEngine.setDefaultHost("localhost");
    Host localHost = this.emTomcat.createHost("localhost", this.appBase);
    localEngine.addChild(localHost);
    Context localContext = this.emTomcat.createContext(this.appContextPath, this.appDocBase);
    localHost.addChild(localContext);
    this.emTomcat.addEngine(localEngine);
    Connector localConnector = null;
    String str1 = System.getProperty("java.vm.vendor");
    if (this.webType.equalsIgnoreCase("GENERAL"))
    {
      localConnector = this.emTomcat.createConnector((InetAddress)null, this.hostPort, false);
    }
    else
    {
      localConnector = this.emTomcat.createConnector((InetAddress)null, this.hostPort, true);
      Http11Protocol localHttp11Protocol = (Http11Protocol)localConnector.getProtocolHandler();
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
        String str3 = null;
        try
        {
          str3 = PKIToolConfig.getP11Password();
        }
        catch (PKIException localPKIException1)
        {
          throw new WebServerException(localPKIException1.getErrCode(), localPKIException1.getErrDescEx(), localPKIException1.getHistory());
        }
        if (str1.toUpperCase().indexOf("SUN") != -1)
        {
          try
          {
            str2 = PKIToolConfig.getP11ProviderName();
          }
          catch (PKIException localPKIException2)
          {
            throw new WebServerException(localPKIException2.getErrCode(), localPKIException2.getErrDescEx(), localPKIException2.getHistory());
          }
          localConnector.setProperty("keystoreType", "PKCS11");
          localConnector.setProperty("keystore", str2);
          localConnector.setProperty("keystorePass", str3);
        }
        else if (str1.toUpperCase().indexOf("HEWLETT") != -1)
        {
          try
          {
            str2 = PKIToolConfig.getP11ProviderName();
          }
          catch (PKIException localPKIException3)
          {
            throw new WebServerException(localPKIException3.getErrCode(), localPKIException3.getErrDescEx(), localPKIException3.getHistory());
          }
          localConnector.setProperty("keystoreType", "PKCS11");
          localConnector.setProperty("keystore", str2);
          localConnector.setProperty("keystorePass", str3);
        }
        else
        {
          localConnector.setProperty("keystoreType", "PKCS11IMPLKS");
          localConnector.setProperty("keystore", "IBMPKCS11Impl");
          localConnector.setProperty("keystorePass", str3);
          localConnector.setProperty("algorithm", "IbmX509");
          localHttp11Protocol.setProtocol("SSL_TLSWithIBM");
        }
      }
      localConnector.setProperty("truststoreType", "JKS");
      localConnector.setProperty("truststorePass", this.keyStorePass);
      localConnector.setProperty("truststoreFile", this.keyStoreFile);
      localHttp11Protocol.setSecure(true);
      localConnector.setProperty("clientAuth", "false");
    }
    this.emTomcat.addConnector(localConnector);
    try
    {
      this.emTomcat.start();
    }
    catch (Exception localException)
    {
      if (this.webType.equalsIgnoreCase("GENERAL"))
        throw new WebServerException("8504", "HTTP 服务启动失败", localException);
      throw new WebServerException("8502", "无客户端验证模式的HTTPS 服务启动失败", localException);
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
      if (this.webType.equalsIgnoreCase("GENERAL"))
        throw new WebServerException("8505", "HTTP 服务关闭失败", localException);
      throw new WebServerException("8503", "无客户端验证模式的HTTPS 服务关闭失败", localException);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.webserver.HTTPServer
 * JD-Core Version:    0.6.0
 */