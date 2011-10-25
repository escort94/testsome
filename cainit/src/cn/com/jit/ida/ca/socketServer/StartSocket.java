package cn.com.jit.ida.ca.socketServer;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.config.ServerConfig;
import cn.com.jit.ida.ca.initserver.InitServerException;
import cn.com.jit.ida.net.NetConfig;
import cn.com.jit.ida.net.NetException;
import cn.com.jit.ida.net.server.Server;

public class StartSocket
{
  GlobalConfig globalConfig;
  InternalConfig initConfig;
  NetConfig netConfig;
  Server server;

  private void init()
    throws InitServerException
  {
    try
    {
      this.globalConfig = GlobalConfig.getInstance();
      this.initConfig = InternalConfig.getInstance();
    }
    catch (IDAException localIDAException1)
    {
      throw new InitServerException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1);
    }
    ServerConfig localServerConfig = this.globalConfig.getServerConfig();
    this.netConfig = new NetConfig();
    this.netConfig.setConnectType(localServerConfig.getServerType());
    try
    {
      this.netConfig.setDataType("BYTE");
    }
    catch (IDAException localIDAException2)
    {
      throw new InitServerException(localIDAException2.getErrCode(), localIDAException2.getErrDesc(), localIDAException2);
    }
    this.netConfig.setKeystore(localServerConfig.getCommunicateCert());
    this.netConfig.setKeystorePassword(localServerConfig.getCommunicateCertPWD());
    this.netConfig.setNeadClientAuth(true);
    this.netConfig.setPort(localServerConfig.getPort());
    this.netConfig.setServerAddress(localServerConfig.getServerAddress());
    this.netConfig.setTimeOut(localServerConfig.getSessionTimeOut());
    this.netConfig.setTrustCerts(localServerConfig.getCommunicateCert());
    this.netConfig.setTrustCertsPassword(localServerConfig.getCommunicateCertPWD());
    this.netConfig.setSecureProtocol(this.initConfig.getSocketProtocol());
  }

  public void run()
    throws InitServerException
  {
    try
    {
      init();
      DealSocketMsg localDealSocketMsg = new DealSocketMsg();
      this.server = new Server(this.netConfig, this.globalConfig.getServerConfig().getAcceptThreadCount(), this.globalConfig.getServerConfig().getMaxProcessThread(), localDealSocketMsg);
      this.server.run();
    }
    catch (NetException localNetException)
    {
      throw new InitServerException(localNetException.getErrCode(), localNetException.getErrDesc(), localNetException);
    }
  }

  public void stop()
    throws InitServerException
  {
    try
    {
      this.server.stopServer();
    }
    catch (NetException localNetException)
    {
      throw new InitServerException(localNetException.getErrCode(), localNetException.getErrDesc(), localNetException);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.socketServer.StartSocket
 * JD-Core Version:    0.6.0
 */