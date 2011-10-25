package cn.com.jit.ida.net.server;

import cn.com.jit.ida.net.NetConfig;
import cn.com.jit.ida.net.NetException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.List;

public class Server
  implements Runnable
{
  public static final int SSL = 2;
  public static final int SOCKET = 1;
  private int connectType = 1;
  NetConfig config;
  ServerSocket server;
  private int localPort;
  private int poolSize;
  private int acceptorSize;
  private ThreadGroup acceptorGroup;
  private ThreadGroup dealGroup;
  DealRequest dealRequest;
  SSLServerFactory sslFactory;
  private int ConnectType;
  static final int SERVERSTOPED = 1;
  static final int SERVERSTARED = 2;
  static final int SERVERPAUSED = 3;
  static final int SERVERCLOSED = 4;
  static int ServerState = 1;

  public Server(NetConfig paramNetConfig, DealRequest paramDealRequest)
    throws NetException
  {
    this(paramNetConfig, 4, 8, paramDealRequest);
  }

  public Server(NetConfig paramNetConfig, int paramInt1, int paramInt2, DealRequest paramDealRequest)
    throws NetException
  {
    this.poolSize = paramInt2;
    this.acceptorSize = paramInt1;
    this.localPort = this.localPort;
    this.dealRequest = paramDealRequest;
    this.acceptorGroup = new ThreadGroup("ServerPoolGroup");
    this.dealGroup = new ThreadGroup("DealRequestGroup");
    this.config = paramNetConfig;
    this.sslFactory = new SSLServerFactory(paramNetConfig);
    this.server = this.sslFactory.getServerSocket();
    for (int i = 0; i < paramInt2; i++)
      new Thread(this.dealGroup, new DealReqPool(this, paramDealRequest)).start();
    for (i = 0; i < paramInt1; i++)
      new Thread(this.acceptorGroup, new Acceptor(this)).start();
  }

  public boolean addDealThread()
  {
    new Thread(this.dealGroup, new DealReqPool(this, this.dealRequest)).start();
    return true;
  }

  public void run()
  {
    try
    {
      starServer();
    }
    catch (NetException localNetException)
    {
      System.err.println(localNetException.toString());
    }
  }

  public int getType()
  {
    return this.connectType;
  }

  public String[] getUserList()
  {
    return null;
  }

  public int getActiveThreadCount()
  {
    return this.dealGroup.activeCount();
  }

  public void pauseServer()
    throws NetException
  {
    try
    {
      this.server.close();
    }
    catch (IOException localIOException)
    {
      throw new NetException("17", "停止服务器错误", localIOException);
    }
    synchronized (new Integer(ServerState))
    {
      if (ServerState != 1)
        ServerState = 3;
    }
  }

  public void stopServer()
    throws NetException
  {
    if (ServerState != 2)
      return;
    synchronized (new Integer(ServerState))
    {
      ServerState = 1;
    }
    synchronized (DealReqPool.pool)
    {
      DealReqPool.pool.clear();
    }
    try
    {
      this.server.close();
    }
    catch (IOException )
    {
      throw new NetException("17", "停止服务器错误", ???);
    }
    ServerState = 4;
    synchronized (DealReqPool.pool)
    {
      DealReqPool.pool.notifyAll();
    }
  }

  public void starServer()
    throws NetException
  {
    if (this.server.isClosed())
    {
      this.server = this.sslFactory.getServerSocket();
      try
      {
        this.server.setSoTimeout(this.config.getTimeOut());
      }
      catch (SocketException localSocketException)
      {
        throw new NetException("19", "服务已经被关闭", localSocketException);
      }
    }
    synchronized (new Integer(ServerState))
    {
      ServerState = 2;
    }
  }

  public NetConfig getConfig()
  {
    return this.config;
  }

  public int getStatic()
  {
    return ServerState;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.net.server.Server
 * JD-Core Version:    0.6.0
 */