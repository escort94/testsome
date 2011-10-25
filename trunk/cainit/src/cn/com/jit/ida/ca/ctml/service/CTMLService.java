package cn.com.jit.ida.ca.ctml.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.Service;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;

public abstract class CTMLService
  implements Service
{
  private static final String PROGRAM_ID = "80";
  private static final String[][] SERVICES = { { "CTMLCREATE", "21" }, { "CTMLDELETE", "22" }, { "CTMLGET", "23" }, { "CTMLMODIFY", "24" }, { "CTMLSELFEXTCREATE", "25" }, { "CTMLSELFEXTDELETE", "26" }, { "CTMLSELFEXTGET", "27" }, { "CTMLSELFEXTMODIFY", "28" }, { "CTMLREVOKE", "29" }, { "CTMLSELFEXTREVOKE", "30" }, { "RACTMLUPDATE", "31" } };
  String operation;
  String operationID;
  protected Request request;
  protected Response response;
  protected IDAException exception;
  protected Operator operator;
  protected Operation logInfo = new Operation();
  protected boolean isLog;
  protected boolean isDebug;
  private OptLogger optLogger;
  protected DebugLogger debuger;
  GlobalConfig config;

  public CTMLService(String paramString)
  {
    this.logInfo.setResult(1);
    this.isLog = false;
    this.isDebug = false;
    this.optLogger = LogManager.getOPtLogger();
    for (int i = 0; (i < SERVICES.length) && (!paramString.equalsIgnoreCase(SERVICES[i][0])); i++);
    if (i >= SERVICES.length)
      throw new RuntimeException("invalid operation " + paramString);
    this.operation = SERVICES[i][0];
    this.operationID = SERVICES[i][1];
    this.isDebug = LogManager.isDebug();
    String str = null;
    try
    {
      str = InternalConfig.getInstance().getIsLog();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
    if (str == null)
      this.isLog = false;
    else
      this.isLog = str.equalsIgnoreCase("true");
  }

  boolean licenceCheck()
    throws IDAException
  {
    return true;
  }

  boolean privilegeCheck()
    throws IDAException
  {
    return true;
  }

  void analyseRequest(Request paramRequest)
    throws IDAException
  {
    if (this.isDebug)
    {
      byte[] arrayOfByte = paramRequest.getOriginalData();
      if (arrayOfByte != null)
        this.debuger.appendMsg_L2(new String(arrayOfByte));
      else
        this.debuger.appendMsg_L2(new String(paramRequest.getData()));
    }
    this.request = paramRequest;
    this.operator = paramRequest.getOperator();
    if (this.isLog)
    {
      this.logInfo.setOperatorDN(this.operator.getOperatorDN());
      this.logInfo.setOperatorSN(this.operator.getOperatorSN());
      this.logInfo.setOptType(paramRequest.getOperation());
    }
  }

  abstract void execute()
    throws IDAException;

  Response constructResponse()
  {
    this.response = new Response();
    this.response.setErr("0");
    this.response.setMsg("SUCCESS");
    this.response.setOperation(this.operation);
    return this.response;
  }

  Response constructExceptionResponse(IDAException paramIDAException)
  {
    this.exception = paramIDAException;
    this.response = new Response();
    this.response.setErr("80" + this.operationID + paramIDAException.getErrCode());
    this.response.setMsg(paramIDAException.getErrDescEx());
    this.response.appendDetail(paramIDAException.getHistory());
    this.response.setOperation(this.operation);
    if (this.isLog)
      this.logInfo.setResult(0);
    return this.response;
  }

  void writeLog()
    throws IDAException
  {
    if (!this.isLog)
      return;
    try
    {
      this.optLogger.info(this.logInfo);
    }
    catch (IDAException localIDAException)
    {
      this.debuger.appendMsg_L1("write log exception!");
      this.debuger.doLog(localIDAException);
      throw localIDAException;
    }
  }

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      analyseRequest(paramRequest);
      licenceCheck();
      privilegeCheck();
      execute();
      doOtherUpdate();
      this.response = constructResponse();
      writeLog();
      if (this.isDebug)
        try
        {
          byte[] arrayOfByte1 = this.response.getData();
          this.debuger.appendMsg_L2(new String(arrayOfByte1));
          this.debuger.doLog();
        }
        catch (Exception localException1)
        {
          throw new RuntimeException(localException1);
        }
    }
    catch (IDAException localIDAException)
    {
      this.response = constructExceptionResponse(localIDAException);
      if (this.isDebug)
        try
        {
          byte[] arrayOfByte2 = this.response.getData();
          this.debuger.appendMsg_L1(new String(arrayOfByte2));
          this.debuger.doLog();
        }
        catch (Exception localException2)
        {
          this.debuger.appendMsg_L1("response.getData exception!");
          this.debuger.doLog();
        }
      this.debuger.doLog(localIDAException);
    }
    catch (RuntimeException localRuntimeException)
    {
      this.debuger.doLog(localRuntimeException);
      throw localRuntimeException;
    }
    return this.response;
  }

  protected void doOtherUpdate()
  {
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.CTMLService
 * JD-Core Version:    0.6.0
 */