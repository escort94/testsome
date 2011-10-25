package cn.com.jit.ida.ca.control;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.ServerConfig;
import cn.com.jit.ida.ca.initserver.InitServerException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class SendCommand
{
  securePackage securePag;

  public SendCommand(String paramString)
  {
    try
    {
      this.securePag = new securePackage(paramString, null);
    }
    catch (InitServerException localInitServerException)
    {
      System.err.println(localInitServerException.getMessage());
    }
  }

  public boolean run()
  {
    if (this.securePag == null)
      return false;
    ServerConfig localServerConfig = null;
    try
    {
      localServerConfig = ServerConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
      System.err.println("无法读取配置文件，可能是系统没有被初始化。发送命令失败");
      return false;
    }
    Socket localSocket = null;
    Object localObject;
    try
    {
      localSocket = new Socket("localhost", localServerConfig.getControlPort());
    }
    catch (IOException localIOException1)
    {
      localObject = null;
      try
      {
        localObject = this.securePag.getCommand();
      }
      catch (Exception localException)
      {
      }
      if (((String)localObject).indexOf(CommandConstant.UpdateCRLInterval) == -1)
        System.out.println("服务器没有启动。请先启动服务器，再进行此操作。");
      return false;
    }
    try
    {
      DataOutputStream localDataOutputStream = new DataOutputStream(localSocket.getOutputStream());
      localObject = new DataInputStream(localSocket.getInputStream());
      String str1 = this.securePag.getSecureCommand();
      localDataOutputStream.writeInt(str1.getBytes().length);
      localDataOutputStream.write(str1.getBytes());
      localDataOutputStream.flush();
      int i = ((DataInputStream)localObject).readInt();
      if ((i <= 0) || (i > 10000))
        return false;
      byte[] arrayOfByte = new byte[i];
      ((DataInputStream)localObject).read(arrayOfByte);
      String str2 = new String(arrayOfByte);
      if (!str2.equalsIgnoreCase("womeichi"))
        System.out.println(str2);
      if (str2.equalsIgnoreCase("命令无效"))
        return false;
    }
    catch (IOException localIOException2)
    {
      System.out.println(localIOException2.toString());
      return false;
    }
    catch (InitServerException localInitServerException)
    {
      System.out.println(localInitServerException.toString());
      return false;
    }
    return true;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.control.SendCommand
 * JD-Core Version:    0.6.0
 */