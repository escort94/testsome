package cn.com.jit.ida.net.client;

import B;
import cn.com.jit.ida.net.NetConfig;
import cn.com.jit.ida.net.NetException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Client
{
  private int DEBUG = 1;
  private NetConfig config;
  private String hostAddress;
  private int hostPort;
  private Socket socket;

  public Client(String paramString, int paramInt, NetConfig paramNetConfig)
    throws NetException
  {
    this.config = paramNetConfig;
    this.hostAddress = paramString;
    this.hostPort = paramInt;
    SSLFactory localSSLFactory = new SSLFactory(paramNetConfig);
    this.socket = localSSLFactory.getSocket(paramString, paramInt);
    try
    {
      this.socket.setSoTimeout(600000);
    }
    catch (SocketException localSocketException)
    {
      throw new NetException("04", "超时错误", localSocketException);
    }
  }

  public byte[] request(byte[] paramArrayOfByte)
    throws NetException
  {
    DataInputStream localDataInputStream1;
    Object localObject1;
    Object localObject2;
    if (this.config.getDataType().equals("BYTE"))
    {
      localDataInputStream1 = null;
      localObject1 = null;
      try
      {
        DataInputStream localDataInputStream2 = null;
        localObject2 = null;
        try
        {
          localObject2 = new DataOutputStream(this.socket.getOutputStream());
          ((DataOutputStream)localObject2).writeInt(paramArrayOfByte.length);
          ((DataOutputStream)localObject2).write(paramArrayOfByte);
          ((DataOutputStream)localObject2).flush();
        }
        catch (IOException localIOException3)
        {
          throw new NetException("16", "写数据错误", localIOException3);
        }
        int i = 0;
        try
        {
          localDataInputStream2 = new DataInputStream(this.socket.getInputStream());
          i = localDataInputStream2.readInt();
        }
        catch (IOException localIOException5)
        {
          throw new NetException("07", "读数据错误", localIOException5);
        }
        localObject1 = new byte[i];
        try
        {
          int j = localDataInputStream2.read(localObject1);
          while (j < i)
          {
            byte[] arrayOfByte2 = new byte[i - j];
            int k = localDataInputStream2.read(arrayOfByte2);
            System.arraycopy(arrayOfByte2, 0, localObject1, j, k);
            j += k;
          }
        }
        catch (IOException localIOException6)
        {
          throw new NetException("07", "读数据错误", localIOException6);
        }
      }
      catch (NetException localNetException1)
      {
        throw localNetException1;
      }
      finally
      {
        try
        {
          this.socket.close();
        }
        catch (IOException localIOException7)
        {
        }
      }
      return localObject1;
    }
    if (this.config.getDataType().equals("STRING"))
    {
      localDataInputStream1 = null;
      localObject1 = null;
      try
      {
        try
        {
          localObject1 = new DataOutputStream(this.socket.getOutputStream());
          ((DataOutputStream)localObject1).writeUTF(new String(paramArrayOfByte));
          ((DataOutputStream)localObject1).flush();
        }
        catch (IOException localIOException2)
        {
          throw new NetException("16", "写数据错误", localIOException2);
        }
        StringBuffer localStringBuffer = new StringBuffer(3);
        localObject2 = null;
        try
        {
          localDataInputStream1 = new DataInputStream(this.socket.getInputStream());
          localObject2 = localDataInputStream1.readUTF();
        }
        catch (IOException localIOException4)
        {
          throw new NetException("07", "读数据错误", localIOException4);
        }
        arrayOfByte1 = ((String)localObject2).getBytes();
      }
      catch (NetException localNetException2)
      {
        byte[] arrayOfByte1;
        throw localNetException2;
      }
      finally
      {
        try
        {
          if (localDataInputStream1 != null)
            localDataInputStream1.close();
          if (localObject1 != null)
            ((DataOutputStream)localObject1).close();
          this.socket.close();
        }
        catch (IOException localIOException8)
        {
        }
      }
    }
    return (B)(B)null;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.net.client.Client
 * JD-Core Version:    0.6.0
 */