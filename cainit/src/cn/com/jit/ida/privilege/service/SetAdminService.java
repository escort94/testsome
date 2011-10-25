package cn.com.jit.ida.privilege.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.Service;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.privilege.request.SetAdminRequest;
import cn.com.jit.ida.privilege.response.SetAdminResponse;
import cn.com.jit.ida.util.xml.XMLTool;
import java.io.PrintStream;
import java.util.Vector;

public class SetAdminService
  implements Service
{
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.privilege.service.SetAdminService");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private Operation operation = new Operation();
  private Operator operator;
  private Request request;
  private SetAdminResponse response = new SetAdminResponse();
  private String adminSN = null;
  private Vector roles = null;

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      if (LogManager.isDebug2())
        this.debugLogger.appendMsg_L2("request XML=" + new String(paramRequest.getData()));
    }
    catch (Exception localException1)
    {
      this.response.setErr("80430506");
      this.response.setMsg("管理员授权:应答信息格式不合法");
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
      this.response.setErr("80430504");
      this.response.setMsg("管理员授权:操作员无效");
      this.debugLogger.doLog();
      return this.response;
    }
    this.operation.setOptType(paramRequest.getOperation());
    this.operation.setOperatorSN(localOperator.getOperatorSN());
    this.operation.setOperatorDN(localOperator.getOperatorDN());
    SetAdminRequest localSetAdminRequest = null;
    try
    {
      localSetAdminRequest = new SetAdminRequest(paramRequest);
    }
    catch (PrivilegeException localPrivilegeException)
    {
      this.debugLogger.appendMsg_L1("request format error: " + localPrivilegeException.toString());
      this.response.setErr("80430505");
      this.response.setMsg("管理员授权:请求信息格式不合法");
      this.debugLogger.doLog();
      return this.response;
    }
    this.adminSN = localSetAdminRequest.getAdminSN();
    this.operation.setObjCertSN(this.adminSN);
    this.operation.setObjSubject(localSetAdminRequest.getAdminDN());
    try
    {
      this.operation.setObjCTMLName(InternalConfig.getInstance().getAdminTemplateName());
    }
    catch (IDAException localIDAException1)
    {
    }
    String str = null;
    try
    {
      str = CAConfig.getInstance().getCaAdminSN();
    }
    catch (IDAException localIDAException2)
    {
      this.debugLogger.appendMsg_L1("get superadmin SN fail");
      this.debugLogger.doLog();
      this.response.setErr("80430508");
      this.response.setMsg("管理员授权:获取超级管理员序列号失败");
      return this.response;
    }
    if ((this.adminSN == null) || (this.adminSN.equals("")))
    {
      this.debugLogger.appendMsg_L1("admin SN is null");
      this.debugLogger.doLog();
      this.response.setErr("80430127");
      this.response.setMsg("管理员授权:管理员SN为空");
      return this.response;
    }
    if (this.adminSN.equals(localOperator.getOperatorSN()))
    {
      this.debugLogger.appendMsg_L1("admin could not authorize himself");
      this.debugLogger.doLog();
      this.response.setErr("80430128");
      this.response.setMsg("管理员授权:管理员不能授权自己");
      return this.response;
    }
    if (this.adminSN.equalsIgnoreCase(str))
    {
      this.debugLogger.appendMsg_L1("admin could not authorize superadmin");
      this.debugLogger.doLog();
      this.response.setErr("80430129");
      this.response.setMsg("管理员授权:不能对超级管理员进行授权");
      return this.response;
    }
    this.debugLogger.appendMsg_L2("admin SN = " + this.adminSN);
    this.roles = localSetAdminRequest.getRoles();
    if ((this.roles == null) || (this.roles.isEmpty()))
    {
      this.debugLogger.appendMsg_L1("roles is null");
      this.debugLogger.doLog();
      this.response.setErr("80430326");
      this.response.setMsg("管理员授权:角色集合为空");
      return this.response;
    }
    for (int i = 0; i < this.roles.size(); i++)
      this.debugLogger.appendMsg_L2("roleinfo = " + this.roles.get(i));
    boolean bool = localSetAdminRequest.getIsRa();
    if ((bool) && (!this.roles.contains("证书管理角色")))
      this.roles.add("证书管理角色");
    Privilege localPrivilege = null;
    try
    {
      localPrivilege = Privilege.getInstance();
      localPrivilege.setAdmin(this.adminSN, this.roles);
    }
    catch (IDAException localIDAException3)
    {
      this.debugLogger.appendMsg_L1(localIDAException3.toString());
      try
      {
        this.operation.setResult(0);
        this.optLogger.info(this.operation);
      }
      catch (Exception localException5)
      {
        this.debugLogger.appendMsg_L1("recod operation-log error: " + localIDAException3.toString());
        this.response.setErr("80430503");
        this.response.setMsg("管理员授权:记录业务日志失败");
        this.debugLogger.doLog();
        return this.response;
      }
      this.response.setErr(localIDAException3.getErrCode());
      this.response.setMsg(localIDAException3.getErrDesc());
      this.response.appendDetail(localIDAException3.getHistory());
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
      this.response.setErr("80430503");
      this.response.setMsg("管理员授权:记录业务日志失败");
      this.debugLogger.doLog();
      return this.response;
    }
    this.response.setMsg("success");
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

  public static void main(String[] paramArrayOfString)
  {
    SetAdminService localSetAdminService = new SetAdminService();
    SetAdminRequest localSetAdminRequest = new SetAdminRequest();
    String str = new String("系统管理员");
    Vector localVector = new Vector();
    localVector.add("CA管理角色");
    localVector.add("日志管理角色");
    localSetAdminRequest.setAdminSN(str);
    localSetAdminRequest.setRoles(localVector);
    Response localResponse = null;
    try
    {
      byte[] arrayOfByte1 = XMLTool.xmlSerialize(localSetAdminRequest.getDocument());
      System.out.println("request:" + new String(arrayOfByte1));
      localResponse = localSetAdminService.dealRequest(localSetAdminRequest);
      byte[] arrayOfByte2 = XMLTool.xmlSerialize(localResponse.getDocument());
      System.out.println("response:" + new String(arrayOfByte2));
    }
    catch (Exception localException)
    {
      System.out.println(localException.toString());
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.service.SetAdminService
 * JD-Core Version:    0.6.0
 */