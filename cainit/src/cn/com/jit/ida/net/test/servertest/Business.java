package cn.com.jit.ida.net.test.servertest;

import cn.com.jit.ida.net.PeerInfo;
import cn.com.jit.ida.net.server.DealRequest;
import java.io.PrintStream;

public class Business
  implements DealRequest
{
  public byte[] process(byte[] paramArrayOfByte, PeerInfo paramPeerInfo)
  {
    try
    {
      for (int i = 0; i < 100; i++)
        for (int j = 0; j < 100; j++)
        {
          int k = 3;
          k *= k;
        }
      System.out.println(new String(paramArrayOfByte));
      System.out.println("Peer IP : " + paramPeerInfo.getIP());
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return "I hava receive it".getBytes();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.net.test.servertest.Business
 * JD-Core Version:    0.6.0
 */