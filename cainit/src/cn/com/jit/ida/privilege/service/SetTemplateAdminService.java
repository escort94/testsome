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
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.privilege.TemplateAdmin;
import cn.com.jit.ida.privilege.request.SetTemplateAdminRequest;
import cn.com.jit.ida.privilege.response.SetTemplateAdminResponse;

public class SetTemplateAdminService
  implements Service
{
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.privilege.service.SetTemplateAdminService");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private Operation operation;
  private Operator operator;
  private boolean doLog = false;
  private Request request;
  private SetTemplateAdminResponse response = new SetTemplateAdminResponse();
  private String temAdminSN = null;
  private boolean isRa = false;
  private String[] ctmlName = null;
  private String[] dn = null;
  private String[] reserve = null;

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      if (LogManager.isDebug2())
        this.debugLogger.appendMsg_L2("request XML=" + new String(paramRequest.getData()));
    }
    catch (Exception localException1)
    {
      this.response.setErr("80510506");
      this.response.setMsg("证书业务管理员授权:应答信息格式不合法");
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
      this.response.setErr("80510504");
      this.response.setMsg("证书业务管理员授权:操作员无效");
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
    SetTemplateAdminRequest localSetTemplateAdminRequest = null;
    try
    {
      localSetTemplateAdminRequest = new SetTemplateAdminRequest(paramRequest);
    }
    catch (PrivilegeException localPrivilegeException)
    {
      this.debugLogger.appendMsg_L1("request format error: " + localPrivilegeException.toString());
      this.response.setErr("80510505");
      this.response.setMsg("证书业务管理员授权:请求信息格式不合法");
      this.debugLogger.doLog();
      return this.response;
    }
    this.temAdminSN = localSetTemplateAdminRequest.getAdminSN();
    this.isRa = localSetTemplateAdminRequest.getIsRa();
    String str = null;
    try
    {
      str = CAConfig.getInstance().getCaAdminSN();
    }
    catch (IDAException localIDAException1)
    {
      this.debugLogger.appendMsg_L1("get superadmin SN fail");
      this.debugLogger.doLog();
      this.response.setErr("80510508");
      this.response.setMsg("证书业务管理员授权:获取超级管理员序列号失败");
      return this.response;
    }
    if ((this.temAdminSN == null) || (this.temAdminSN.equals("")))
    {
      this.debugLogger.appendMsg_L1("admin SN is null");
      this.debugLogger.doLog();
      this.response.setErr("80510127");
      this.response.setMsg("证书业务管理员授权:管理员SN为空");
      return this.response;
    }
    if (this.temAdminSN.equals(localOperator.getOperatorSN()))
    {
      this.debugLogger.appendMsg_L1("admin could not authorize himself");
      this.debugLogger.doLog();
      this.response.setErr("80510128");
      this.response.setMsg("证书业务管理员授权:管理员不能授权自己");
      return this.response;
    }
    if (this.temAdminSN.equalsIgnoreCase(str))
    {
      this.debugLogger.appendMsg_L1("admin could not authorize superadmin");
      this.debugLogger.doLog();
      this.response.setErr("80510129");
      this.response.setMsg("证书业务管理员授权:不能对超级管理员进行授权");
      return this.response;
    }
    this.debugLogger.appendMsg_L2("admin SN = " + this.temAdminSN);
    this.ctmlName = localSetTemplateAdminRequest.getCtmlName();
    this.dn = localSetTemplateAdminRequest.getDN();
    this.reserve = localSetTemplateAdminRequest.getReserve();
    if ((this.ctmlName != null) && (this.ctmlName.length != this.dn.length))
    {
      this.debugLogger.appendMsg_L1("ctmlname not match basedn");
      this.debugLogger.doLog();
      this.response.setErr("80510127");
      this.response.setMsg("证书业务管理员授权:管理员SN为空");
      return this.response;
    }
    for (int i = 0; i < this.dn.length; i++)
    {
      this.debugLogger.appendMsg_L2("ctmlname = " + this.ctmlName[i]);
      this.debugLogger.appendMsg_L2("baseDN = " + this.dn[i]);
    }
    TemplateAdmin localTemplateAdmin = null;
    try
    {
      localTemplateAdmin = TemplateAdmin.getInstance();
      if (this.isRa)
      {
        String[] arrayOfString1 = new String[this.ctmlName.length + 1];
        String[] arrayOfString2 = new String[this.ctmlName.length + 1];
        for (int j = 0; j < this.ctmlName.length; j++)
        {
          arrayOfString1[j] = this.ctmlName[j];
          arrayOfString2[j] = this.dn[j];
        }
        arrayOfString1[this.ctmlName.length] = InternalConfig.getInstance().getRaadminTemplateName();
        arrayOfString2[this.ctmlName.length] = localTemplateAdmin.getRAAdminBasedn(this.temAdminSN);
        localTemplateAdmin.setTemplateAdmin(this.temAdminSN, arrayOfString1, arrayOfString2, this.reserve);
      }
      else
      {
        localTemplateAdmin.setTemplateAdmin(this.temAdminSN, this.ctmlName, this.dn, this.reserve);
      }
    }
    catch (IDAException localIDAException2)
    {
      this.debugLogger.appendMsg_L1(localIDAException2.toString());
      if (this.doLog)
        try
        {
          this.operation.setResult(0);
          this.optLogger.info(this.operation);
        }
        catch (Exception localException5)
        {
          this.debugLogger.appendMsg_L1("recod operation-log error: " + localIDAException2.toString());
          this.response.setErr("80510503");
          this.response.setMsg("证书业务管理员授权:记录业务日志失败");
          this.debugLogger.doLog();
          return this.response;
        }
      this.response.setErr(localIDAException2.getErrCode());
      this.response.setMsg(localIDAException2.getErrDesc());
      this.response.appendDetail(localIDAException2.getHistory());
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
        this.response.setErr("80510503");
        this.response.setMsg("证书业务管理员授权:记录业务日志失败");
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
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.service.SetTemplateAdminService
 * JD-Core Version:    0.6.0
 */