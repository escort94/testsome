package cn.com.jit.ida.ca.audit.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.audit.AuditException;
import cn.com.jit.ida.ca.audit.service.operation.AuditOperation;
import cn.com.jit.ida.ca.audit.service.request.AuditCertRequest;
import cn.com.jit.ida.ca.audit.service.response.AuditCertResponse;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import java.util.Properties;
import java.util.Vector;

public class AduitCertService extends AuditService
{
  private OptLogger optLog = null;
  private DebugLogger debugLog = null;
  private Operation operation = null;
  private AuditCertRequest auditCertRequest = null;
  private AuditCertResponse response = null;
  private Operator operator = null;
  private Properties prop = null;
  private String sortBy = null;
  private boolean exactQuery = false;

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      if (LogManager.isDebug2())
        try
        {
          this.debugLog.appendMsg_L2("audit cert request XML:\n" + new String(paramRequest.getData()));
        }
        catch (Exception localException1)
        {
          throw new AuditException("0000", "通信格式错误 请求信息格式不合法");
        }
      this.operator = paramRequest.getOperator();
      if (this.operator == null)
        throw new AuditException("0100", "数据检查错误 操作员为空");
      this.operation.setOptType(paramRequest.getOperation());
      this.operation.setOperatorSN(this.operator.getOperatorSN());
      this.operation.setOperatorDN(this.operator.getOperatorDN());
      this.debugLog.appendMsg_L2("operation =" + paramRequest.getOperation());
      this.debugLog.appendMsg_L2("operator SN =" + this.operator.getOperatorSN());
      this.debugLog.appendMsg_L2("operator DN=" + this.operator.getOperatorDN());
      try
      {
        this.auditCertRequest = new AuditCertRequest(paramRequest);
      }
      catch (AuditException localAuditException1)
      {
        this.debugLog.appendMsg_L1("request format error");
        throw localAuditException1;
      }
      try
      {
        validateCheck();
      }
      catch (AuditException localAuditException2)
      {
        throw localAuditException2;
      }
      Vector localVector = null;
      try
      {
        localVector = AuditOperation.queryCert(this.prop, this.sortBy, this.exactQuery);
      }
      catch (IDAException localIDAException1)
      {
        this.debugLog.appendMsg_L1("aduit cert operate DB error:" + localIDAException1.toString());
        throw localIDAException1;
      }
      this.operation.setResult(1);
      try
      {
        this.optLog.info(this.operation);
      }
      catch (IDAException localIDAException2)
      {
        throw localIDAException2;
      }
      this.response.setErr("0");
      this.response.setMsg("success");
      this.response.setTotalRowCount((String)localVector.get(0));
      this.response.setSortAndCount((Properties)localVector.get(1));
    }
    catch (Exception localException2)
    {
      this.debugLog.appendMsg_L1("Exception:" + localException2.toString());
      if (this.operator != null)
      {
        this.operation.setResult(0);
        AuditCertResponse localAuditCertResponse;
        try
        {
          this.optLog.info(this.operation);
        }
        catch (IDAException localIDAException3)
        {
          this.response.setErr("8072" + localIDAException3.getErrCode());
          this.response.setMsg("证书审计服务 " + localIDAException3.getErrDesc());
          this.response.appendDetail(localIDAException3.getHistory());
          this.debugLog.doLog();
          localAuditCertResponse = this.response;
          jsr 130;
        }
      }
      if ((localException2 instanceof IDAException))
      {
        IDAException localIDAException4 = (IDAException)localException2;
        this.response.setErr("8072" + localIDAException4.getErrCode());
        this.response.setMsg("证书审计服务 " + localIDAException4.getErrDesc());
        this.response.appendDetail(localIDAException4.getHistory());
      }
      else
      {
        this.response.setErr("80729999");
        this.response.setMsg("证书审计服务 系统错误");
        this.response.appendDetail(localException2);
      }
    }
    finally
    {
      try
      {
        if (LogManager.isDebug2())
          this.debugLog.appendMsg_L2("audit cert response XML : \n" + new String(this.response.getData()));
      }
      catch (Exception localException3)
      {
        this.debugLog.doLog();
        throw new RuntimeException(localException3);
      }
      this.debugLog.doLog();
    }
    label744: break label744;
  }

  void validateCheck()
    throws AuditException
  {
    String str1 = this.auditCertRequest.getExactExact();
    if (str1.equalsIgnoreCase("true"))
      this.exactQuery = true;
    String str2 = this.auditCertRequest.getSortBy();
    if ((str2 == null) || (str2.trim().equals("")))
      throw new AuditException("0104", "数据检查错误 统计证书的分类方式为空");
    this.sortBy = str2;
    this.debugLog.appendMsg_L2("audit cert sortBy=" + str2);
    Properties localProperties = this.auditCertRequest.getProp();
    if (localProperties == null)
      throw new AuditException("0103", "数据检查错误 查询条件为空");
    this.prop = localProperties;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.audit.service.AduitCertService
 * JD-Core Version:    0.6.0
 */