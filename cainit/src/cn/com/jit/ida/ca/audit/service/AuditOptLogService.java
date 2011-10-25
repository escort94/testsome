package cn.com.jit.ida.ca.audit.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.audit.AuditException;
import cn.com.jit.ida.ca.audit.service.operation.AuditOperation;
import cn.com.jit.ida.ca.audit.service.request.AuditOptLogRequest;
import cn.com.jit.ida.ca.audit.service.response.AuditOptLogResponse;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import java.util.Properties;
import java.util.Vector;

public class AuditOptLogService extends AuditService
{
  private OptLogger optLog = null;
  private DebugLogger debugLog = null;
  private Operation operation = null;
  private AuditOptLogRequest auditOptLogRequest = null;
  private AuditOptLogResponse response = null;
  private Operator operator = null;
  private int fromIndex = -1;
  private int rowCount = -1;
  private Properties prop = null;
  private boolean exactQuery = false;
  private String isLog = "false";

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      if (LogManager.isDebug2())
        try
        {
          this.debugLog.appendMsg_L2("audit operation_log request XML:\n" + new String(paramRequest.getData()));
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
        this.auditOptLogRequest = new AuditOptLogRequest(paramRequest);
      }
      catch (AuditException localAuditException)
      {
        this.debugLog.appendMsg_L1("request format error");
        throw localAuditException;
      }
      this.isLog = this.auditOptLogRequest.getIsLog();
      try
      {
        validateCheck();
      }
      catch (IDAException localIDAException1)
      {
        throw localIDAException1;
      }
      Vector localVector = null;
      try
      {
        localVector = AuditOperation.queryOptLog(this.prop, this.fromIndex, this.rowCount, this.exactQuery);
      }
      catch (IDAException localIDAException2)
      {
        this.debugLog.appendMsg_L1("audit optLog operate DB error:" + localIDAException2.toString());
        throw localIDAException2;
      }
      if (this.isLog.equalsIgnoreCase("true"))
      {
        this.operation.setResult(1);
        try
        {
          this.optLog.info(this.operation);
        }
        catch (IDAException localIDAException3)
        {
          throw localIDAException3;
        }
      }
      this.response.setErr("0");
      this.response.setMsg("success");
      String str = (String)localVector.get(0);
      this.response.setTotalRowCount(str);
      localObject1 = (Operation[])localVector.get(1);
      this.response.setOptLogs(localObject1);
    }
    catch (Exception localException2)
    {
      Object localObject1;
      this.debugLog.appendMsg_L1("Exception:" + localException2.toString());
      if ((this.operator != null) && (this.isLog.equalsIgnoreCase("true")))
      {
        this.operation.setResult(0);
        try
        {
          this.optLog.info(this.operation);
        }
        catch (IDAException localIDAException4)
        {
          this.response.setErr("8071" + localIDAException4.getErrCode());
          this.response.setMsg("业务日志审计服务 " + localIDAException4.getErrDesc());
          this.response.appendDetail(localIDAException4.getHistory());
          this.debugLog.doLog();
          localObject1 = this.response;
          jsr 130;
        }
      }
      if ((localException2 instanceof IDAException))
      {
        IDAException localIDAException5 = (IDAException)localException2;
        this.response.setErr("8071" + localIDAException5.getErrCode());
        this.response.setMsg("业务日志审计服务 " + localIDAException5.getErrDesc());
        this.response.appendDetail(localIDAException5.getHistory());
      }
      else
      {
        this.response.setErr("80719999");
        this.response.setMsg("业务日志审计服务 系统错误");
        this.response.appendDetail(localException2);
      }
    }
    finally
    {
      try
      {
        if (LogManager.isDebug2())
          this.debugLog.appendMsg_L2("audit operation_log response XML:\n" + new String(this.response.getData()));
      }
      catch (Exception localException3)
      {
        this.debugLog.doLog();
        throw new RuntimeException(localException3);
      }
      this.debugLog.doLog();
    }
    label789: break label789;
  }

  void validateCheck()
    throws AuditException
  {
    String str1 = this.auditOptLogRequest.getObjectCertSN();
    if (str1 != null)
      this.operation.setObjCertSN(str1);
    String str2 = this.auditOptLogRequest.getObjectCertSubjcet();
    if (str2 != null)
      this.operation.setObjSubject(str2);
    String str3 = this.auditOptLogRequest.getObjectCTMLName();
    if (str3 != null)
      this.operation.setObjCTMLName(str3);
    String str4 = this.auditOptLogRequest.getExactQuery();
    if (str4.equalsIgnoreCase("true"))
      this.exactQuery = true;
    String str5 = this.auditOptLogRequest.getFromIndex();
    if ((str5 == null) || (str5.trim().equals("")))
      throw new AuditException("0101", "数据检查错误 查询起始位置为空");
    this.fromIndex = Integer.parseInt(str5);
    this.debugLog.appendMsg_L2("audit optLog fromIndex=" + str5);
    String str6 = this.auditOptLogRequest.getRowCount();
    if ((str6 == null) || (str6.trim().equals("")))
      throw new AuditException("0102", "数据检查错误 查询记录数为空");
    int i = Integer.parseInt(this.auditOptLogRequest.getRowCount());
    int j = 1000;
    CAConfig localCAConfig = null;
    try
    {
      localCAConfig = CAConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
      throw new AuditException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException.getHistory());
    }
    j = localCAConfig.getMaxCountPerPage();
    if (i > j)
      this.rowCount = j;
    else
      this.rowCount = i;
    this.debugLog.appendMsg_L2("audit optLog rowCount=" + str6);
    Properties localProperties = this.auditOptLogRequest.getProp();
    if (localProperties == null)
      throw new AuditException("0103", "数据检查错误 查询条件为空");
    this.prop = localProperties;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.audit.service.AuditOptLogService
 * JD-Core Version:    0.6.0
 */