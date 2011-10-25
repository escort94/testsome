package cn.com.jit.ida.ca.control;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.CAServer;
import cn.com.jit.ida.ca.certmanager.service.iss.CRLSchedule;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CRLConfig;
import cn.com.jit.ida.ca.config.ServerConfig;
import cn.com.jit.ida.ca.initserver.InitServerException;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.TemplateAdmin;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ControlServer
  implements Runnable
{
  CAServer caServer;
  private boolean ifContinute = true;
  ServerSocket serverSocket = null;
  OptLogger optLogger = null;

  public ControlServer(CAServer paramCAServer)
  {
    this.caServer = paramCAServer;
    LogManager.init();
  }

  public void stopControl()
  {
    this.ifContinute = false;
    try
    {
      this.serverSocket.close();
    }
    catch (IOException localIOException)
    {
    }
  }

  public void run()
  {
    this.optLogger = LogManager.getOPtLogger();
    ServerConfig localServerConfig = null;
    try
    {
      localServerConfig = ServerConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
      System.err.println(new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException).toString());
      System.err.println("控制服务启动失败");
      return;
    }
    try
    {
      this.serverSocket = new ServerSocket(localServerConfig.getControlPort());
    }
    catch (IOException localIOException1)
    {
      System.err.println("控制服务启动失败");
      return;
    }
    while (this.ifContinute)
    {
      Socket localSocket = null;
      try
      {
        localSocket = this.serverSocket.accept();
      }
      catch (IOException localIOException2)
      {
      }
      continue;
      byte[] arrayOfByte1 = null;
      try
      {
        DataOutputStream localDataOutputStream = new DataOutputStream(localSocket.getOutputStream());
        DataInputStream localDataInputStream = new DataInputStream(localSocket.getInputStream());
        int i = localDataInputStream.readInt();
        if ((i > 0) && (i > 100000))
          continue;
        arrayOfByte1 = new byte[i];
        localDataInputStream.read(arrayOfByte1);
        securePackage localsecurePackage;
        try
        {
          localsecurePackage = new securePackage(null, new String(arrayOfByte1));
        }
        catch (InitServerException localInitServerException)
        {
        }
        continue;
        byte[] arrayOfByte2 = ExeCommand(localsecurePackage.getCommand()).getBytes();
        localDataOutputStream.writeInt(arrayOfByte2.length);
        localDataOutputStream.write(arrayOfByte2);
        localDataOutputStream.flush();
        String str = new String(arrayOfByte2);
        if (!str.equalsIgnoreCase("womeichi"))
          System.out.println(str);
        localSocket.shutdownInput();
      }
      catch (IOException localIOException3)
      {
      }
    }
  }

  String ExeCommand(String paramString)
  {
    String str1 = null;
    Operation localOperation = new Operation();
    localOperation.setOperatorDN("系统管理员");
    localOperation.setOptTime(System.currentTimeMillis());
    if (paramString == null)
    {
      str1 = "命令无效";
    }
    else if (paramString.equalsIgnoreCase("nichileme"))
    {
      str1 = "womeichi";
    }
    else if (paramString.equalsIgnoreCase(CommandConstant.Debug_1))
    {
      if (LogManager.isDebug1())
      {
        localOperation.setOptType(CommandConstant.Debug_1);
        localOperation.setResult(0);
        str1 = "Server已经是实时故障诊断模式，无需再次进入。";
      }
      else
      {
        localOperation.setOptType(CommandConstant.Debug_1);
        localOperation.setResult(1);
        this.caServer.debugServer_L1();
        str1 = "Server进入实时故障诊断模式";
      }
    }
    else if (paramString.equalsIgnoreCase(CommandConstant.Debug_2))
    {
      if (LogManager.isDebug2())
      {
        localOperation.setOptType(CommandConstant.Debug_2);
        localOperation.setResult(0);
        str1 = "Server已经是实时故障诊断模式(2)，无需再次进入。";
      }
      else
      {
        localOperation.setOptType(CommandConstant.Debug_2);
        localOperation.setResult(1);
        this.caServer.debugServer_L2();
        str1 = "Server进入实时故障诊断模式(2)";
      }
    }
    else if (paramString.equalsIgnoreCase(CommandConstant.StopWebServer))
    {
      localOperation.setOptType(CommandConstant.Debug_1);
      if (this.caServer.stopWebServer())
      {
        str1 = "WebServer停止";
        localOperation.setResult(1);
      }
      else
      {
        localOperation.setResult(0);
        str1 = "WebServer停止失败";
      }
    }
    else if (paramString.equalsIgnoreCase(CommandConstant.StartWebServer))
    {
      localOperation.setOptType(CommandConstant.StartWebServer);
      if (this.caServer.startWebServer())
      {
        localOperation.setResult(1);
        str1 = "WebServer启动";
      }
      else
      {
        localOperation.setResult(0);
        str1 = "WebServer启动失败";
      }
    }
    else if (paramString.equalsIgnoreCase(CommandConstant.StopSocketServer))
    {
      this.caServer.stopSocketServer();
      str1 = "SocketServer停止";
    }
    else if (paramString.equalsIgnoreCase(CommandConstant.StartSocketServer))
    {
      this.caServer.startSocketServer();
      str1 = "SocketServer启动";
    }
    else if (paramString.equalsIgnoreCase(CommandConstant.StopDebug_1))
    {
      localOperation.setOptType(CommandConstant.StopDebug_1);
      if (this.caServer.stopDebugServer_L1())
      {
        localOperation.setResult(1);
        str1 = "Server退出实时诊断状态";
      }
      else
      {
        localOperation.setResult(0);
        str1 = "Server没有在实时诊断状态";
      }
    }
    else if (paramString.equalsIgnoreCase(CommandConstant.StopDebug_2))
    {
      localOperation.setOptType(CommandConstant.StopDebug_2);
      if (this.caServer.stopDebugServer_L1())
      {
        localOperation.setResult(1);
        str1 = "Server退出实时诊断状态";
      }
      else
      {
        localOperation.setResult(0);
        str1 = "Server没有在实时诊断状态";
      }
    }
    else
    {
      int i;
      String str3;
      CAConfig localCAConfig;
      if (paramString.toLowerCase().startsWith(CommandConstant.UpdateAdminBind.toLowerCase()))
      {
        i = paramString.toLowerCase().indexOf(CommandConstant.UpdateAdminBind.toLowerCase());
        str3 = paramString.substring(CommandConstant.UpdateAdminBind.length());
        localOperation.setOptType(CommandConstant.UpdateAdmin);
        localCAConfig = null;
        try
        {
          localCAConfig = CAConfig.getInstance();
        }
        catch (IDAException localIDAException2)
        {
          str1 = localIDAException2.getMessage();
        }
        if (this.caServer.updateAdmin(str3, true, "NON"))
        {
          String str4 = new File(localCAConfig.getAdminKeyStorePath()).getParent();
          if (str4 == null)
            str4 = "./keystore";
          str1 = "更新超级管理员证书成功,证书保存在(" + str4 + ")下";
          localOperation.setResult(1);
        }
        else
        {
          str1 = "更新超级管理员证书失败";
          localOperation.setResult(0);
        }
      }
      else if (paramString.toLowerCase().startsWith(CommandConstant.UpdateAdmin.toLowerCase()))
      {
        i = paramString.toLowerCase().indexOf(CommandConstant.UpdateAdmin.toLowerCase());
        str3 = paramString.substring(CommandConstant.UpdateAdmin.length());
        localOperation.setOptType(CommandConstant.UpdateAdmin);
        localCAConfig = null;
        try
        {
          localCAConfig = CAConfig.getInstance();
        }
        catch (IDAException localIDAException3)
        {
          str1 = localIDAException3.getMessage();
        }
        if (this.caServer.updateAdmin(str3, true, "NON"))
        {
          String str5 = new File(localCAConfig.getAdminKeyStorePath()).getParent();
          if (str5 == null)
            str5 = "./keystore";
          str1 = "更新超级管理员证书成功,证书保存在(" + str5 + ")下";
          localOperation.setResult(1);
        }
        else
        {
          str1 = "更新超级管理员证书失败";
          localOperation.setResult(0);
        }
      }
      else if (paramString.toLowerCase().startsWith(CommandConstant.UpdateAuditAdmin.toLowerCase()))
      {
        i = paramString.toLowerCase().indexOf(CommandConstant.UpdateAuditAdmin.toLowerCase());
        str3 = paramString.substring(CommandConstant.UpdateAuditAdmin.length());
        localOperation.setOptType(CommandConstant.UpdateAuditAdmin);
        localCAConfig = null;
        try
        {
          localCAConfig = CAConfig.getInstance();
        }
        catch (IDAException localIDAException4)
        {
          str1 = localIDAException4.getMessage();
        }
        if (this.caServer.updateAdmin(str3, false, "NON"))
        {
          String str6 = new File(localCAConfig.getAuditAdminKeyStorePath()).getParent();
          if (str6 == null)
            str6 = "./keystore";
          str1 = "更新审计管理员证书成功,证书保存在(" + str6 + ")下";
          localOperation.setResult(1);
        }
        else
        {
          str1 = "更新审计管理员证书失败";
          localOperation.setResult(0);
        }
      }
      else if (paramString.toLowerCase().startsWith(CommandConstant.UpdateSuperRemoveAudit.toLowerCase()))
      {
        i = paramString.toLowerCase().indexOf(CommandConstant.UpdateSuperRemoveAudit.toLowerCase());
        str3 = paramString.substring(CommandConstant.UpdateSuperRemoveAudit.length());
        localOperation.setOptType(CommandConstant.UpdateAdmin);
        localCAConfig = null;
        try
        {
          localCAConfig = CAConfig.getInstance();
        }
        catch (IDAException localIDAException5)
        {
          str1 = localIDAException5.getMessage();
        }
        if (this.caServer.updateAdmin(str3, true, "REMOVE"))
        {
          String str7 = new File(localCAConfig.getAdminKeyStorePath()).getParent();
          if (str7 == null)
            str7 = "./keystore";
          str1 = "更新超级管理员证书成功,证书保存在(" + str7 + ")下";
          localOperation.setResult(1);
        }
        else
        {
          str1 = "更新超级管理员证书失败";
          localOperation.setResult(0);
        }
      }
      else
      {
        Object localObject;
        if (paramString.toLowerCase().startsWith(CommandConstant.UpdateAuditRemoveSuper.toLowerCase()))
        {
          i = paramString.toLowerCase().indexOf(CommandConstant.UpdateAuditRemoveSuper.toLowerCase());
          str3 = paramString.substring(CommandConstant.UpdateAuditRemoveSuper.length());
          localOperation.setOptType(CommandConstant.UpdateAuditAdmin);
          localCAConfig = null;
          try
          {
            localCAConfig = CAConfig.getInstance();
          }
          catch (IDAException localIDAException6)
          {
            str1 = localIDAException6.getMessage();
          }
          if (this.caServer.updateAdmin(str3, false, "REMOVE"))
          {
            localObject = new File(localCAConfig.getAdminKeyStorePath()).getParent();
            if (localObject == null)
              localObject = "./keystore";
            str1 = "更新审计管理员证书成功,证书保存在(" + (String)localObject + ")下";
            localOperation.setResult(1);
          }
          else
          {
            str1 = "更新审计管理员证书失败";
            localOperation.setResult(0);
          }
        }
        else if (paramString.toLowerCase().startsWith(CommandConstant.UpdateAuditAddSuper.toLowerCase()))
        {
          i = paramString.toLowerCase().indexOf(CommandConstant.UpdateAuditAddSuper.toLowerCase());
          str3 = paramString.substring(CommandConstant.UpdateAuditAddSuper.length());
          localOperation.setOptType(CommandConstant.UpdateAuditAdmin);
          localCAConfig = null;
          try
          {
            localCAConfig = CAConfig.getInstance();
            localObject = Privilege.getInstance();
            ((Privilege)localObject).setSuperAdmin(localCAConfig.getCAAuditAdminSN(), localCAConfig.getCAAuditAdminDN());
            TemplateAdmin localTemplateAdmin = TemplateAdmin.getInstance();
            localTemplateAdmin.setSuperAdmin(localCAConfig.getCAAuditAdminSN());
            str1 = "更新超级管理员操作成功";
            localOperation.setResult(1);
          }
          catch (IDAException localIDAException7)
          {
            str1 = "update super administrator error: " + localIDAException7.getMessage();
            localOperation.setResult(0);
          }
        }
        else if (paramString.toLowerCase().startsWith(CommandConstant.UpdateSuperAddAudit.toLowerCase()))
        {
          i = paramString.toLowerCase().indexOf(CommandConstant.UpdateAuditAdmin.toLowerCase());
          str3 = paramString.substring(CommandConstant.UpdateAuditAdmin.length());
          localOperation.setOptType(CommandConstant.UpdateAdmin);
          localCAConfig = null;
          try
          {
            localCAConfig = CAConfig.getInstance();
            Privilege localPrivilege = Privilege.getInstance();
            localPrivilege.setAuditAdmin(localCAConfig.getCaAdminSN(), localCAConfig.getCaAdminDN());
            str1 = "更新审计管理员操作成功";
            localOperation.setResult(1);
          }
          catch (IDAException localIDAException8)
          {
            str1 = "update audit administrator error: " + localIDAException8.getMessage();
            localOperation.setResult(0);
          }
        }
        else if (paramString.toLowerCase().startsWith(CommandConstant.UpdateAdminGen.toLowerCase()))
        {
          i = paramString.toLowerCase().indexOf(CommandConstant.UpdateAdminGen.toLowerCase());
          str3 = paramString.substring(CommandConstant.UpdateAdminGen.length());
          localOperation.setOptType(CommandConstant.UpdateAdmin);
          if (this.caServer.generateAdmin(str3, true))
          {
            str1 = "更新超级管理员操作成功,证书保存在(./keystore/)目录下";
            localOperation.setResult(1);
          }
          else
          {
            str1 = "更新超级管理员操作失败";
            localOperation.setResult(0);
          }
        }
        else if (paramString.toLowerCase().startsWith(CommandConstant.UpdateAuditAdminGen.toLowerCase()))
        {
          i = paramString.toLowerCase().indexOf(CommandConstant.UpdateAuditAdminGen.toLowerCase());
          str3 = paramString.substring(CommandConstant.UpdateAuditAdminGen.length());
          localOperation.setOptType(CommandConstant.UpdateAuditAdmin);
          if (this.caServer.generateAdmin(str3, false))
          {
            str1 = "更新超级管理员操作成功,证书保存在(./keystore/)目录下";
            localOperation.setResult(1);
          }
          else
          {
            str1 = "更新超级管理员操作失败";
            localOperation.setResult(0);
          }
        }
        else if (paramString.equalsIgnoreCase(CommandConstant.Stop))
        {
          localOperation.setOptType(CommandConstant.Stop);
          localOperation.setResult(1);
          this.caServer.stopAll();
          this.ifContinute = false;
          str1 = "Server停止";
        }
        else
        {
          if (paramString.indexOf(CommandConstant.UpdateCRLInterval) != -1)
          {
            String str2 = paramString.substring(paramString.indexOf(":") + 1, paramString.length());
            long l = Long.parseLong(str2) * 60000L;
            try
            {
              CRLConfig localCRLConfig = CRLConfig.getInstance();
              localCRLConfig.setPeriods(l);
            }
            catch (IDAException localIDAException9)
            {
            }
            CRLSchedule localCRLSchedule = CRLSchedule.getInstance();
            localCRLSchedule.restart(l);
            str1 = "更新系统CRL发布周期成功";
            return str1;
          }
          str1 = "命令无效";
        }
      }
    }
    if (paramString == null)
      return str1;
    if (!paramString.equalsIgnoreCase("nichileme"))
      try
      {
        this.optLogger.info(localOperation);
      }
      catch (IDAException localIDAException1)
      {
      }
    return (String)str1;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.control.ControlServer
 * JD-Core Version:    0.6.0
 */