package cn.com.jit.ida.net.test.servertest;

import cn.com.jit.ida.net.NetConfig;
import cn.com.jit.ida.net.server.Server;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class ServerTest
{
  public static void main(String[] paramArrayOfString)
  {
    try
    {
      ServerTest localServerTest = new ServerTest();
      NetConfig localNetConfig = new NetConfig();
      localNetConfig.setConnectType("SSL");
      localNetConfig.setKeystore("./keystore/CommCert.jks");
      localNetConfig.setKeystorePassword("11111111");
      localNetConfig.setTrustCerts("./keystore/CommCert.jks");
      localNetConfig.setTrustCertsPassword("11111111");
      localNetConfig.setPort(30829);
      localNetConfig.setServerAddress("192.168.9.145");
      localNetConfig.setDataType("BYTE");
      Server localServer = new Server(localNetConfig, 5, 200, new Business());
      Thread localThread = null;
      try
      {
        localThread = new Thread(localServer);
        localThread.start();
      }
      catch (Exception localException2)
      {
        localException2.printStackTrace();
        System.out.println("ServerError");
      }
      while (true)
      {
        System.out.println("1) 暂停Server");
        System.out.println("2) 恢复Server");
        System.out.println("3) 停止Server");
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String str = localBufferedReader.readLine().trim();
        if (str.equals("1"))
        {
          localServer.pauseServer();
          continue;
        }
        if (str.equals("2"))
        {
          localServer.starServer();
          continue;
        }
        if (!str.equals("3"))
          continue;
        localServer.stopServer();
      }
    }
    catch (Exception localException1)
    {
      localException1.printStackTrace();
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.net.test.servertest.ServerTest
 * JD-Core Version:    0.6.0
 */