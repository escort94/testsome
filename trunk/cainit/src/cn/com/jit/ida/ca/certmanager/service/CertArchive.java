package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.Service;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.service.kmc.KMCCertArcRequest;
import cn.com.jit.ida.ca.certmanager.service.kmc.KMCCertArcResponse;
import cn.com.jit.ida.ca.certmanager.service.kmc.KMCConnector;
import cn.com.jit.ida.ca.certmanager.service.request.CertArchiveRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CertArchiveResponse;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import java.io.PrintStream;
import java.util.Vector;

public class CertArchive
  implements Service
{
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.ca.certmanager.service.CertArchive");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private String endTime = new String();
  private Operation operation = null;
  private DBManager db = null;
  private CertArchiveResponse response = new CertArchiveResponse();
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
          throw new CertException("0704", "其他错误 请求信息格式不合法");
        }
      this.operation = new Operation();
      this.operator = paramRequest.getOperator();
      if (this.operator == null)
      {
        this.debugLogger.appendMsg_L1("operator=null");
        throw new CertException("0701", "其他错误 操作员为空");
      }
      if ((this.operator.getOperatorDN() == null) || (this.operator.getOperatorDN().trim().equals("")))
      {
        this.debugLogger.appendMsg_L1("operatorDN=" + this.operator.getOperatorDN());
        throw new CertException("0702", "其他错误 操作员DN为空");
      }
      this.operation.setOptType(paramRequest.getOperation());
      this.operation.setOperatorSN(this.operator.getOperatorSN());
      this.operation.setOperatorDN(this.operator.getOperatorDN());
      this.debugLogger.appendMsg_L2("operation=" + paramRequest.getOperation());
      this.debugLogger.appendMsg_L2("operatorSN=" + this.operator.getOperatorSN());
      this.debugLogger.appendMsg_L2("operatorDN=" + this.operator.getOperatorDN());
      this.db = DBManager.getInstance();
      parseCertArchive(paramRequest);
      try
      {
        int i = this.db.moveCert2Arc(this.endTime, getCTMLWithKMC());
        this.response.setCertCount(String.valueOf(i));
      }
      catch (DBException localDBException1)
      {
        throw new CertException("0708", "其他错误 DB错误");
      }
      Vector localVector = null;
      try
      {
        localVector = this.db.getCertSnForKMC();
      }
      catch (DBException localDBException2)
      {
        throw new CertException("0708", "其他错误 DB错误");
      }
      if ((localVector != null) && (localVector.size() > 0))
      {
        KMCConnector localKMCConnector = new KMCConnector();
        localObject1 = new KMCCertArcRequest();
        ((KMCCertArcRequest)localObject1).setVecCertSN(localVector);
        KMCCertArcResponse localKMCCertArcResponse = localKMCConnector.setCertArcToKMC((KMCCertArcRequest)localObject1);
        if ((localKMCCertArcResponse != null) && (localKMCCertArcResponse.getErr().equals("0")))
          try
          {
            this.db.deleteCertArcForKMC();
          }
          catch (DBException localDBException3)
          {
            System.out.println("删除已归档加密证书失败:" + localDBException3.getMessage());
          }
      }
      this.operation.setResult(1);
      this.optLogger.info(this.operation);
      this.response.setErr("0");
      this.response.setMsg("success");
    }
    catch (IDAException localIDAException1)
    {
      Object localObject1;
      this.debugLogger.appendMsg_L1("Exception:" + localIDAException1.toString());
      if (this.operator != null)
      {
        this.operation.setResult(0);
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
          localObject1 = this.response;
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
    label810: break label810;
  }

  protected void parseCertArchive(Request paramRequest)
    throws IDAException
  {
    CertArchiveRequest localCertArchiveRequest = new CertArchiveRequest(paramRequest);
    localCertArchiveRequest.updateBody(false);
    this.endTime = localCertArchiveRequest.getEndTime();
  }

  private Vector getCTMLWithKMC()
    throws IDAException
  {
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    String[] arrayOfString = localCTMLManager.getCTMLList();
    X509V3CTML localX509V3CTML = null;
    String str1 = null;
    String str2 = null;
    Vector localVector = new Vector();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      str1 = arrayOfString[i];
      localX509V3CTML = (X509V3CTML)localCTMLManager.getCTML(str1);
      str2 = localX509V3CTML.getKeyGenPlace();
      if ((str2 == null) || (!str2.equals("KMC")))
        continue;
      localVector.add(str1);
    }
    return localVector;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.CertArchive
 * JD-Core Version:    0.6.0
 */