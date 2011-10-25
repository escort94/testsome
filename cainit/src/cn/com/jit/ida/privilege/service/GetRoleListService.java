package cn.com.jit.ida.privilege.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.Service;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.privilege.request.GetRoleListRequest;
import cn.com.jit.ida.privilege.response.GetRoleListResponse;
import java.util.Vector;

public class GetRoleListService
  implements Service
{
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.privilege.service.SetAdminService");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private Operation operation;
  private Operator operator;
  private boolean doLog = false;
  private Request request;
  private GetRoleListResponse response = new GetRoleListResponse();

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      if (LogManager.isDebug2())
        this.debugLogger.appendMsg_L2("request XML=" + new String(paramRequest.getData()));
    }
    catch (Exception localException1)
    {
      this.response.setErr("80620506");
      this.response.setMsg("获取角色列表:应答信息格式不合法");
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
      this.response.setErr("80620504");
      this.response.setMsg("获取角色列表:操作员无效");
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
    try
    {
      GetRoleListRequest localGetRoleListRequest = new GetRoleListRequest(paramRequest);
    }
    catch (PrivilegeException localPrivilegeException)
    {
      this.debugLogger.appendMsg_L1("request format error: " + localPrivilegeException.toString());
      this.response.setErr("80620505");
      this.response.setMsg("获取角色列表:请求信息格式不合法");
      this.debugLogger.doLog();
      return this.response;
    }
    Vector localVector = new Vector();
    Privilege localPrivilege = null;
    try
    {
      localPrivilege = Privilege.getInstance();
      localVector = localPrivilege.getRoleList();
      localVector.remove("审计管理角色");
      localVector.remove("超级管理员角色");
    }
    catch (IDAException localIDAException)
    {
      this.debugLogger.appendMsg_L1(localIDAException.toString());
      if (this.doLog)
        try
        {
          this.operation.setResult(0);
          this.optLogger.info(this.operation);
        }
        catch (Exception localException5)
        {
          this.debugLogger.appendMsg_L1("recod operation-log error: " + localIDAException.toString());
          this.response.setErr("80620503");
          this.response.setMsg("获取角色列表:记录业务日志失败");
          this.debugLogger.doLog();
          return this.response;
        }
      this.response.setErr(localIDAException.getErrCode());
      this.response.setMsg(localIDAException.getErrDesc());
      this.response.appendDetail(localIDAException.getHistory());
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
        this.response.setErr("80620503");
        this.response.setMsg("获取角色列表:记录业务日志失败");
        this.debugLogger.doLog();
        return this.response;
      }
    this.response.setMsg("success");
    this.response.setRoles(localVector);
    if ((localVector == null) || (localVector.isEmpty()))
      this.debugLogger.appendMsg_L2("rolelist = null");
    else
      for (int i = 0; i < localVector.size(); i++)
        this.debugLogger.appendMsg_L2("rolelist = " + localVector.get(i));
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
 * Qualified Name:     cn.com.jit.ida.privilege.service.GetRoleListService
 * JD-Core Version:    0.6.0
 */