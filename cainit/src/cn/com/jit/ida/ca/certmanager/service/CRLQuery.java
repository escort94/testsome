package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.CertService;
import cn.com.jit.ida.ca.certmanager.reqinfo.CRLInfo;
import cn.com.jit.ida.ca.certmanager.service.operation.CRLQueryOpt;
import cn.com.jit.ida.ca.certmanager.service.response.CRLQueryResponse;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import java.util.Vector;

public class CRLQuery extends CertService
{
  private Request request = null;
  private DebugLogger debugLogger = null;
  private OptLogger optLogger = null;
  private CRLQueryResponse response = null;
  private Operation operation = null;

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      if (LogManager.isDebug2())
        try
        {
          this.debugLogger.appendMsg_L2("CRL query request XML:\n" + new String(paramRequest.getData()));
        }
        catch (Exception localException1)
        {
          throw new CertException("0704", "其他错误 请求信息格式不合法");
        }
      this.request = paramRequest;
      this.operator = paramRequest.getOperator();
      if (this.operator == null)
        throw new CertException("0701", "其他错误 操作员为空");
      this.operation = new Operation();
      this.operation.setOptType(paramRequest.getOperation());
      this.operation.setOperatorSN(this.operator.getOperatorSN());
      this.operation.setOperatorDN(this.operator.getOperatorDN());
      this.debugLogger.appendMsg_L2("operation =" + paramRequest.getOperation());
      this.debugLogger.appendMsg_L2("operator SN =" + this.operator.getOperatorSN());
      this.debugLogger.appendMsg_L2("operator DN=" + this.operator.getOperatorDN());
      Vector localVector = null;
      try
      {
        localVector = CRLQueryOpt.queryCRLInfo();
      }
      catch (IDAException localIDAException1)
      {
        this.debugLogger.appendMsg_L1("query CRL error ：" + localIDAException1.toString());
        throw localIDAException1;
      }
      this.operation.setResult(1);
      try
      {
        this.optLogger.info(this.operation);
      }
      catch (IDAException localIDAException2)
      {
        throw localIDAException2;
      }
      this.response.setErr("0");
      this.response.setMsg("success");
      CRLInfo[] arrayOfCRLInfo = (CRLInfo[])localVector.get(1);
      this.response.setResult(arrayOfCRLInfo);
    }
    catch (Exception localException2)
    {
      this.debugLogger.appendMsg_L1("Exception:" + localException2.toString());
      if (this.operator != null)
      {
        this.operation.setResult(0);
        CRLQueryResponse localCRLQueryResponse;
        try
        {
          this.optLogger.info(this.operation);
        }
        catch (IDAException localIDAException3)
        {
          this.response.setErr("8013" + localIDAException3.getErrCode());
          this.response.setMsg("CRL查询服务 " + localIDAException3.getErrDesc());
          this.response.appendDetail(localIDAException3.getHistory());
          this.debugLogger.doLog();
          localCRLQueryResponse = this.response;
          jsr 130;
        }
      }
      if ((localException2 instanceof IDAException))
      {
        IDAException localIDAException4 = (IDAException)localException2;
        this.response.setErr("8013" + localIDAException4.getErrCode());
        this.response.setMsg("CRL查询服务 " + localIDAException4.getErrDesc());
        this.response.appendDetail(localIDAException4.getHistory());
      }
      else
      {
        this.response.setErr("80130706");
        this.response.setMsg("CRL查询服务 其他错误 系统错误");
        this.response.appendDetail(localException2);
      }
    }
    finally
    {
      try
      {
        if (LogManager.isDebug2())
          this.debugLogger.appendMsg_L2("CRL query response XML:\n" + new String(this.response.getData()));
      }
      catch (Exception localException3)
      {
        this.debugLogger.doLog();
        throw new RuntimeException(localException3);
      }
      this.debugLogger.doLog();
    }
    label698: break label698;
  }

  protected void policyCheck()
    throws CertException
  {
  }

  protected void validateCheck()
    throws CertException
  {
  }

  protected void certStatusCheck()
    throws CertException
  {
  }

  protected void CTMLCheck()
    throws CertException
  {
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.CRLQuery
 * JD-Core Version:    0.6.0
 */