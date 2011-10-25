package cn.com.jit.ida.net.server;

import cn.com.jit.ida.net.NetConfig;
import cn.com.jit.ida.net.NetException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Acceptor
  implements Runnable
{
  Server server;

  public Acceptor(Server paramServer)
  {
    this.server = paramServer;
  }

  public void run()
  {
    while (true)
      try
      {
        if (this.server.getStatic() == 4)
          return;
        if (this.server.getStatic() == 2)
          continue;
        try
        {
          Thread.sleep(200L);
        }
        catch (InterruptedException localInterruptedException)
        {
          throw new NetException("99", "系统错误", localInterruptedException);
        }
        Socket localSocket;
        try
        {
          localSocket = this.server.server.accept();
          try
          {
            localSocket.setSoTimeout(this.server.config.getTimeOut());
          }
          catch (SocketException localSocketException)
          {
            throw new NetException("04", "超时错误", localSocketException);
          }
        }
        catch (IOException localIOException)
        {
          throw new NetException("06", "接收客户端连接错误", localIOException);
        }
        ConnectHandle localConnectHandle = new ConnectHandle(localSocket);
        try
        {
          DealReqPool.processRequest(localConnectHandle);
        }
        catch (Exception localException)
        {
          throw new NetException("99", "系统错误", localException);
        }
        continue;
      }
      catch (NetException localNetException)
      {
      }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.net.server.Acceptor
 * JD-Core Version:    0.6.0
 */