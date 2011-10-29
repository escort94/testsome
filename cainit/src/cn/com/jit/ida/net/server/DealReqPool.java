package cn.com.jit.ida.net.server;

import cn.com.jit.ida.net.NetConfig;
import cn.com.jit.ida.net.NetException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class DealReqPool
  implements Runnable
{
  public static List pool = new LinkedList();
  DealRequest dealRequest;
  NetConfig config;
  Server server;

  public DealReqPool(Server paramServer, DealRequest paramDealRequest)
  {
    this.dealRequest = paramDealRequest;
    this.config = paramServer.getConfig();
    this.server = paramServer;
  }

  public void run()
  {
    while (true)
      try
      {
        if (this.server.getStatic() == 4)
          return;
        if (this.server.getStatic() != 1)
          continue;
        try
        {
          Thread.sleep(200L);
        }
        catch (InterruptedException localInterruptedException1)
        {
          throw new NetException("99", "系统错误", localInterruptedException1);
        }
        ConnectHandle localConnectHandle = null;
        synchronized (pool)
        {
          try
          {
            if (!pool.isEmpty())
              continue;
            if (this.server.getStatic() == 4)
              return;
            try
            {
              pool.wait();
            }
            catch (InterruptedException localInterruptedException2)
            {
              throw new NetException("99", "系统错误", localInterruptedException2);
            }
            localConnectHandle = (ConnectHandle)pool.remove(0);
          }
          catch (Throwable localThrowable2)
          {
          }
        }
        if (Server.ServerState == 1)
        dealReq(localConnectHandle);
      }
      catch (Exception localException)
      {
        System.err.println("Exception:");
        localException.printStackTrace();
        continue;
      }
      catch (Throwable localThrowable1)
      {
        System.err.println("Throwable:");
        localThrowable1.printStackTrace();
      }
  }

  private void dealReq(ConnectHandle paramConnectHandle)
    throws NetException
  {
    DataInputStream localDataInputStream;
    DataOutputStream localDataOutputStream;
    if (this.config.getDataType().equals("BYTE"))
    {
      localDataInputStream = null;
      localDataOutputStream = null;
      try
      {
        localDataOutputStream = new DataOutputStream(paramConnectHandle.getSocket().getOutputStream());
        localDataInputStream = new DataInputStream(paramConnectHandle.getSocket().getInputStream());
        int i = localDataInputStream.readInt();
        int j = 0;
        byte[] arrayOfByte2 = new byte[i];
        byte[] arrayOfByte3 = new byte[i];
        while (j < i)
        {
          int k = localDataInputStream.read(arrayOfByte3, j, i - j);
          if (k == -1)
          {
            if (i == j)
              break;
            throw new NetException("07", "读取数据错误");
          }
          System.arraycopy(arrayOfByte3, 0, arrayOfByte2, j, k);
          j += k;
        }
        byte[] arrayOfByte4 = this.dealRequest.process(arrayOfByte2, paramConnectHandle.getPeerInfo());
        if (arrayOfByte4 == null)
        {
          try
          {
            paramConnectHandle.getSocket().close();
          }
          catch (IOException localIOException7)
          {
          }
          return;
        }
        localDataOutputStream.writeInt(arrayOfByte4.length);
        localDataOutputStream.write(arrayOfByte4);
        localDataOutputStream.flush();
      }
      catch (IOException localIOException3)
      {
      }
      finally
      {
        try
        {
          paramConnectHandle.getSocket().close();
        }
        catch (IOException localIOException9)
        {
        }
      }
    }
    else if (this.config.getDataType().equals("STRING"))
    {
      localDataInputStream = null;
      localDataOutputStream = null;
      try
      {
        localDataOutputStream = new DataOutputStream(paramConnectHandle.getSocket().getOutputStream());
        localDataInputStream = new DataInputStream(paramConnectHandle.getSocket().getInputStream());
        String str = localDataInputStream.readUTF();
        byte[] arrayOfByte1 = this.dealRequest.process(str.getBytes(), paramConnectHandle.getPeerInfo());
        localDataOutputStream.writeUTF(new String(arrayOfByte1));
        localDataOutputStream.flush();
      }
      catch (IOException localIOException6)
      {
      }
      finally
      {
        try
        {
          paramConnectHandle.getSocket().close();
        }
        catch (IOException localIOException10)
        {
        }
      }
    }
  }

  public static void processRequest(ConnectHandle paramConnectHandle)
  {
    synchronized (pool)
    {
      try
      {
        pool.add(pool.size(), paramConnectHandle);
        pool.notify();
      }
      catch (Throwable localThrowable)
      {
        return;
      }
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.net.server.DealReqPool
 * JD-Core Version:    0.6.0
 */