package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.CertService;
import cn.com.jit.ida.ca.certmanager.service.operation.CertQueryOpt;
import cn.com.jit.ida.ca.certmanager.service.request.CertEntityQueryRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CertEntityQueryResponse;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;

public class CertEntityQuery extends CertService
{
  private Request request = null;
  private DebugLogger debugLogger = null;
  private OptLogger optLogger = null;
  private CertEntityQueryResponse response = null;
  private Operation operation = null;
  private String certSN = null;

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      if (LogManager.isDebug2())
        try
        {
          this.debugLogger.appendMsg_L2("cert entity query request XML:\n" + new String(paramRequest.getData()));
        }
        catch (Exception localException1)
        {
          throw new CertException("0704", "其他错误 请求信息格式不合法");
        }
      this.request = paramRequest;
      Operator localOperator = paramRequest.getOperator();
      if (localOperator == null)
        throw new CertException("0701", "其他错误 操作员为空");
      this.operation.setOptType(paramRequest.getOperation());
      this.operation.setOperatorSN(localOperator.getOperatorSN());
      this.operation.setOperatorDN(localOperator.getOperatorDN());
      this.debugLogger.appendMsg_L2("operation =" + paramRequest.getOperation());
      this.debugLogger.appendMsg_L2("operator SN =" + localOperator.getOperatorSN());
      this.debugLogger.appendMsg_L2("operator DN=" + localOperator.getOperatorDN());
      validateCheck();
      byte[] arrayOfByte = null;
      try
      {
        arrayOfByte = CertQueryOpt.queryCertEntity(this.certSN);
      }
      catch (IDAException localIDAException3)
      {
        this.debugLogger.appendMsg_L1("query cert_entity operation error :" + localIDAException3.toString());
        throw localIDAException3;
      }
      this.operation.setResult(1);
      try
      {
        this.optLogger.info(this.operation);
      }
      catch (IDAException localIDAException4)
      {
        throw localIDAException4;
      }
      this.response.setErr("0");
      this.response.setMsg("success");
      this.response.setCertEntity(arrayOfByte);
    }
    catch (Exception localException2)
    {
      this.debugLogger.appendMsg_L1("Exception:" + localException2.toString());
      if (this.operator != null)
      {
        this.operation.setResult(0);
        CertEntityQueryResponse localCertEntityQueryResponse;
        try
        {
          this.optLogger.info(this.operation);
        }
        catch (IDAException localIDAException1)
        {
          this.response.setErr("8008" + localIDAException1.getErrCode());
          this.response.setMsg("证书实体查询服务 " + localIDAException1.getErrDesc());
          this.response.appendDetail(localIDAException1.getHistory());
          this.debugLogger.doLog();
          localCertEntityQueryResponse = this.response;
          jsr 130;
        }
      }
      if ((localException2 instanceof IDAException))
      {
        IDAException localIDAException2 = (IDAException)localException2;
        this.response.setErr("8008" + localIDAException2.getErrCode());
        this.response.setMsg("证书实体查询服务 " + localIDAException2.getErrDesc());
        this.response.appendDetail(localIDAException2.getHistory());
      }
      else
      {
        this.response.setErr("80080706");
        this.response.setMsg("证书实体查询服务 其他错误 系统错误");
        this.response.appendDetail(localException2);
      }
    }
    finally
    {
      try
      {
        if (LogManager.isDebug2())
          this.debugLogger.appendMsg_L2("cert entity query response XML:\n" + new String(this.response.getData()));
      }
      catch (Exception localException3)
      {
        this.debugLogger.doLog();
        throw new RuntimeException(localException3);
      }
      this.debugLogger.doLog();
    }
    label673: break label673;
  }

  protected void validateCheck()
    throws CertException
  {
    CertEntityQueryRequest localCertEntityQueryRequest = null;
    try
    {
      localCertEntityQueryRequest = new CertEntityQueryRequest(this.request);
    }
    catch (CertException localCertException)
    {
      throw new CertException("0704", "其他错误 请求信息格式不合法");
    }
    String str = localCertEntityQueryRequest.getCertSN();
    if (str == null)
    {
      this.debugLogger.appendMsg_L1("cert entity query error: cert SN = " + str);
      throw new CertException("0201", "数据有效性检查 证书序列号为空");
    }
    this.certSN = str;
    this.operation.setObjCertSN(str);
    this.debugLogger.appendMsg_L2("cert entity query error: cert SN = " + str);
  }

  protected void certStatusCheck()
    throws CertException
  {
  }

  protected void policyCheck()
    throws CertException
  {
  }

  protected void CTMLCheck()
    throws CertException
  {
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.CertEntityQuery
 * JD-Core Version:    0.6.0
 */