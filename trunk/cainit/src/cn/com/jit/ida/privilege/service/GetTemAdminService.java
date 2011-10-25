package cn.com.jit.ida.privilege.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.Service;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.privilege.TemplateAdmin;
import cn.com.jit.ida.privilege.request.GetTemAdminRequest;
import cn.com.jit.ida.privilege.response.GetTemAdminResponse;
import java.util.Vector;

public class GetTemAdminService
  implements Service
{
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.privilege.service.GetTemAdminService");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private Operation operation;
  private Operator operator;
  private boolean doLog = false;
  private Request request;
  private GetTemAdminResponse response = new GetTemAdminResponse();
  private String temAdminSN = null;

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      if (LogManager.isDebug2())
        this.debugLogger.appendMsg_L2("request XML=" + new String(paramRequest.getData()));
    }
    catch (Exception localException1)
    {
      this.response.setErr("80520506");
      this.response.setMsg("获取证书业务管理员信息:应答信息格式不合法");
      try
      {
        this.debugLogger.appendMsg_L2("response XML=" + new String(this.response.getData()));
        this.debugLogger.doLog();
        return this.response;
      }
      catch (Exception localException2)
      {
        throw new RuntimeException(localException2);
      }
    }
    this.request = paramRequest;
    Operator localOperator = paramRequest.getOperator();
    if (localOperator == null)
    {
      this.debugLogger.appendMsg_L1("operator = null");
      this.response.setErr("80520504");
      this.response.setMsg("获取证书业务管理员信息:操作员无效");
      this.debugLogger.doLog();
      return this.response;
    }
    if (this.doLog)
    {
      this.operation = new Operation();
      this.operation.setOptType(paramRequest.getOperation());
      this.operation.setOperatorSN(localOperator.getOperatorSN());
      this.operation.setOperatorDN(localOperator.getOperatorDN());
    }
    GetTemAdminRequest localGetTemAdminRequest = null;
    try
    {
      localGetTemAdminRequest = new GetTemAdminRequest(paramRequest);
    }
    catch (PrivilegeException localPrivilegeException)
    {
      this.debugLogger.appendMsg_L1("request format error: " + localPrivilegeException.toString());
      this.response.setErr("80520505");
      this.response.setMsg("获取证书业务管理员信息:请求信息格式不合法");
      this.debugLogger.doLog();
      return this.response;
    }
    this.temAdminSN = localGetTemAdminRequest.getTemAdminSN();
    if ((this.temAdminSN == null) || (this.temAdminSN.equals("")))
    {
      this.debugLogger.appendMsg_L1("admin SN is null");
      this.debugLogger.doLog();
      this.response.setErr("80520127");
      this.response.setMsg("获取证书业务管理员信息:管理员SN为空");
      return this.response;
    }
    this.debugLogger.appendMsg_L2("admin SN = " + this.temAdminSN);
    Vector localVector1 = null;
    TemplateAdmin localTemplateAdmin = null;
    try
    {
      localTemplateAdmin = TemplateAdmin.getInstance();
      localVector1 = localTemplateAdmin.getAdminInfo(this.temAdminSN);
    }
    catch (IDAException localIDAException1)
    {
      if (this.doLog)
      {
        this.debugLogger.appendMsg_L1(localIDAException1.toString());
        try
        {
          this.operation.setResult(0);
          this.optLogger.info(this.operation);
        }
        catch (Exception localException5)
        {
          this.debugLogger.appendMsg_L1("recod operation-log error: " + localIDAException1.toString());
          this.response.setErr("80520503");
          this.response.setMsg("获取证书业务管理员信息:记录业务日志失败");
          this.debugLogger.doLog();
          return this.response;
        }
      }
      this.response.setErr(localIDAException1.getErrCode());
      this.response.setMsg(localIDAException1.getErrDesc());
      this.response.appendDetail(localIDAException1.getHistory());
      this.debugLogger.doLog();
      return this.response;
    }
    if (this.doLog)
      try
      {
        this.operation.setResult(1);
        this.optLogger.info(this.operation);
      }
      catch (Exception localException3)
      {
        this.debugLogger.appendMsg_L1("recod operation-log error: " + localException3.toString());
        this.response.setErr("80520503");
        this.response.setMsg("获取证书业务管理员信息:记录业务日志失败");
        this.debugLogger.doLog();
        return this.response;
      }
    this.response.setMsg("success");
    if (localVector1 != null)
    {
      boolean bool = false;
      Vector localVector2 = new Vector();
      String str1 = null;
      try
      {
        str1 = InternalConfig.getInstance().getRaadminTemplateName();
      }
      catch (IDAException localIDAException2)
      {
        this.debugLogger.appendMsg_L1("get RACtml Name fail");
        this.debugLogger.doLog();
        this.response.setErr("80520509");
        this.response.setMsg("获取证书业务管理员信息:获取RA管理员模板名称失败");
        return this.response;
      }
      for (int i = 0; i < localVector1.size(); i++)
      {
        Vector localVector3 = (Vector)localVector1.get(i);
        if (localVector3.size() <= 0)
          continue;
        Vector localVector4 = new Vector();
        String str2 = (String)localVector3.get(0);
        if (!str2.equals(str1))
        {
          localVector4.add(str2);
          if (localVector3.size() > 1)
          {
            String str3 = new String("");
            for (int j = 1; j < localVector3.size(); j++)
              str3 = "," + (String)localVector3.get(j) + str3;
            localVector4.add(str3.substring(1));
          }
          localVector2.add(localVector4);
        }
        else
        {
          bool = true;
        }
      }
      this.response.setIsRa(bool);
      this.response.setTemAdminInfo(localVector2);
    }
    try
    {
      if (LogManager.isDebug2())
      {
        this.debugLogger.appendMsg_L2("response XML=" + new String(this.response.getData()));
        this.debugLogger.doLog();
      }
    }
    catch (Exception localException4)
    {
      throw new RuntimeException(localException4);
    }
    return this.response;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.service.GetTemAdminService
 * JD-Core Version:    0.6.0
 */