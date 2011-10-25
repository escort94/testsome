package cn.com.jit.ida.ca.log.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.Service;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.log.LogException;
import cn.com.jit.ida.ca.log.service.request.LogArchiveRequest;
import cn.com.jit.ida.ca.log.service.response.LogArchiveResponse;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;

public class LogArchive
  implements Service
{
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.ra.log.service.LogArchive");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private String startTime = new String();
  private String endTime = new String();
  private Operation operation = null;
  private DBManager db = null;
  private LogArchiveResponse response = new LogArchiveResponse();
  public Operator operator;

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      if (LogManager.isDebug2())
        try
        {
          this.debugLogger.appendMsg_L2("requestXML=" + new String(paramRequest.getData()));
        }
        catch (Exception localException1)
        {
          throw new LogException("0204", "数据有效性检查 请求信息格式不合法");
        }
      this.operation = new Operation();
      this.operator = paramRequest.getOperator();
      if (this.operator == null)
      {
        this.debugLogger.appendMsg_L1("operator=null");
        throw new LogException("0201", "数据有效性检查 操作员为空");
      }
      if ((this.operator.getOperatorDN() == null) || (this.operator.getOperatorDN().trim().equals("")))
      {
        this.debugLogger.appendMsg_L1("operatorDN=" + this.operator.getOperatorDN());
        throw new LogException("0202", "数据有效性检查 操作员DN为空");
      }
      this.operation.setOptType(paramRequest.getOperation());
      this.operation.setOperatorSN(this.operator.getOperatorSN());
      this.operation.setOperatorDN(this.operator.getOperatorDN());
      this.debugLogger.appendMsg_L2("operation=" + paramRequest.getOperation());
      this.debugLogger.appendMsg_L2("operatorSN=" + this.operator.getOperatorSN());
      this.debugLogger.appendMsg_L2("operatorDN=" + this.operator.getOperatorDN());
      this.db = DBManager.getInstance();
      parseLogPigeonhole(paramRequest);
      Operation localOperation = new Operation();
      localOperation.setOptTimeBegin(this.startTime);
      localOperation.setOptTimeEnd(this.endTime);
      try
      {
        int i = this.db.moveLog2Arc(localOperation);
        this.response.setLogCount(String.valueOf(i));
      }
      catch (DBException localDBException)
      {
        throw new LogException("0702", "其他错误 DB错误");
      }
      this.operation.setResult(1);
      this.optLogger.info(this.operation);
      this.response.setErr("0");
      this.response.setMsg("success");
    }
    catch (IDAException localIDAException1)
    {
      this.debugLogger.appendMsg_L1("Exception:" + localIDAException1.toString());
      if (this.operator != null)
      {
        this.operation.setResult(0);
        LogArchiveResponse localLogArchiveResponse;
        try
        {
          this.optLogger.info(this.operation);
        }
        catch (IDAException localIDAException2)
        {
          this.response.setErr(localIDAException2.getErrCode());
          this.response.setMsg(localIDAException2.getErrDesc());
          this.response.appendDetail(localIDAException2.getHistory());
          this.debugLogger.doLog();
          localLogArchiveResponse = this.response;
          jsr 95;
        }
      }
      if ((localIDAException1 instanceof IDAException))
      {
        IDAException localIDAException3 = localIDAException1;
        this.response.setErr(localIDAException3.getErrCode());
        this.response.setMsg(localIDAException3.getErrDesc());
        this.response.appendDetail(localIDAException3.getHistory());
      }
      else
      {
        this.response.setErr(localIDAException1.getErrCode());
        this.response.setMsg(localIDAException1.getErrDesc());
        this.response.appendDetail(localIDAException1);
      }
    }
    finally
    {
      try
      {
        if (LogManager.isDebug())
          this.debugLogger.appendMsg_L2("responseXML=" + new String(this.response.getData()));
      }
      catch (Exception localException2)
      {
        throw new RuntimeException(localException2);
      }
      this.debugLogger.doLog();
    }
    label699: break label699;
  }

  protected void parseLogPigeonhole(Request paramRequest)
    throws IDAException
  {
    LogArchiveRequest localLogArchiveRequest = new LogArchiveRequest(paramRequest);
    localLogArchiveRequest.updateBody(false);
    this.startTime = localLogArchiveRequest.getStartTime();
    this.endTime = localLogArchiveRequest.getEndTime();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.log.service.LogArchive
 * JD-Core Version:    0.6.0
 */