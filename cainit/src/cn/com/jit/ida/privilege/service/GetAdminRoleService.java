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
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.privilege.request.GetAdminRoleRequest;
import cn.com.jit.ida.privilege.response.GetAdminRoleResponse;
import java.util.Vector;

public class GetAdminRoleService
  implements Service
{
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.privilege.service.GetAdminRoleService");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private Operation operation = new Operation();
  private Operator operator;
  private Request request;
  private GetAdminRoleResponse response = new GetAdminRoleResponse();
  private String adminSN = null;

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      if (LogManager.isDebug2())
        this.debugLogger.appendMsg_L2("request XML=" + new String(paramRequest.getData()));
    }
    catch (Exception localException1)
    {
      this.response.setErr("80440506");
      this.response.setMsg("获取管理员角色信息:应答信息格式不合法");
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
      this.response.setErr("80440504");
      this.response.setMsg("获取管理员角色信息:操作员无效");
      this.debugLogger.doLog();
      return this.response;
    }
    this.debugLogger.appendMsg_L2("operation =" + paramRequest.getOperation());
    this.debugLogger.appendMsg_L2("operator SN =" + localOperator.getOperatorSN());
    this.debugLogger.appendMsg_L2("operator DN=" + localOperator.getOperatorDN());
    this.operation.setOptType(paramRequest.getOperation());
    this.operation.setOperatorSN(localOperator.getOperatorSN());
    this.operation.setOperatorDN(localOperator.getOperatorDN());
    GetAdminRoleRequest localGetAdminRoleRequest = null;
    try
    {
      localGetAdminRoleRequest = new GetAdminRoleRequest(paramRequest);
    }
    catch (PrivilegeException localPrivilegeException)
    {
      this.debugLogger.appendMsg_L1("request format error: " + localPrivilegeException.toString());
      this.response.setErr("80440505");
      this.response.setMsg("获取管理员角色信息:请求信息格式不合法");
      this.debugLogger.doLog();
      return this.response;
    }
    this.adminSN = localGetAdminRoleRequest.getAdminSN();
    this.operation.setObjSubject(localGetAdminRoleRequest.getAdminDN());
    this.operation.setObjCertSN(this.adminSN);
    try
    {
      this.operation.setObjCTMLName(InternalConfig.getInstance().getAdminTemplateName());
    }
    catch (IDAException localIDAException1)
    {
    }
    if ((this.adminSN == null) || (this.adminSN.equals("")))
    {
      this.debugLogger.appendMsg_L1("admin SN is null");
      this.debugLogger.doLog();
      this.response.setErr("80440127");
      this.response.setMsg("获取管理员角色信息:管理员SN为空");
      return this.response;
    }
    this.debugLogger.appendMsg_L2("admin SN = " + this.adminSN);
    Vector localVector = new Vector();
    Privilege localPrivilege = null;
    try
    {
      localPrivilege = Privilege.getInstance();
      localVector = localPrivilege.getAdminRole(this.adminSN);
    }
    catch (IDAException localIDAException2)
    {
      this.debugLogger.appendMsg_L1(localIDAException2.toString());
      try
      {
        this.operation.setResult(0);
        this.optLogger.info(this.operation);
      }
      catch (Exception localException5)
      {
        this.debugLogger.appendMsg_L1("recod operation-log error: " + localIDAException2.toString());
        this.response.setErr("80440503");
        this.response.setMsg("获取管理员角色信息:记录业务日志失败");
        this.debugLogger.doLog();
        return this.response;
      }
      this.response.setErr(localIDAException2.getErrCode());
      this.response.setMsg(localIDAException2.getErrDesc());
      this.response.appendDetail(localIDAException2.getHistory());
      this.debugLogger.doLog();
      return this.response;
    }
    try
    {
      this.operation.setResult(1);
      this.optLogger.info(this.operation);
    }
    catch (Exception localException3)
    {
      this.debugLogger.appendMsg_L1("recod operation-log error: " + localException3.toString());
      this.debugLogger.doLog();
      this.response.setErr("80440503");
      this.response.setMsg("获取管理员角色信息:记录业务日志失败");
      this.debugLogger.doLog();
      return this.response;
    }
    this.response.setMsg("success");
    this.response.setRoles(localVector);
    if ((localVector == null) || (localVector.isEmpty()))
      this.debugLogger.appendMsg_L2("roleinfo = null");
    else
      for (int i = 0; i < localVector.size(); i++)
        this.debugLogger.appendMsg_L2("roleinfo = " + localVector.get(i));
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
 * Qualified Name:     cn.com.jit.ida.privilege.service.GetAdminRoleService
 * JD-Core Version:    0.6.0
 */