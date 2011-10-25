package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.CertService;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.service.kmc.KMCConnector;
import cn.com.jit.ida.ca.certmanager.service.kmc.RecoverKeyInfo;
import cn.com.jit.ida.ca.certmanager.service.kmc.recoverResponse;
import cn.com.jit.ida.ca.certmanager.service.operation.CAKeyRecoverOpt;
import cn.com.jit.ida.ca.certmanager.service.request.CAKeyRecoverRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CAKeyRecoverResponse;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import java.util.Vector;

public class CAKeyRecover extends CertService
{
  private String certSubject;
  private String certSN;
  private String comparetime;
  private byte[] temppubkey;
  private String keyID;
  private String ctmlname;
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.ca.certmanager.service.CAKeyRecover");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private Operation operation = null;
  private CAKeyRecoverResponse response = new CAKeyRecoverResponse();
  private Request request;

  public CAKeyRecover()
  {
    this.operator = null;
  }

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
      this.request = paramRequest;
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
      validateCheck();
      policyCheck();
      CAKeyRecoverRequest localCAKeyRecoverRequest = new CAKeyRecoverRequest(paramRequest);
      KMCConnector localKMCConnector = new KMCConnector();
      localObject1 = localKMCConnector.recoverkeyFromKMC(localCAKeyRecoverRequest);
      if ((localObject1 != null) && (((recoverResponse)localObject1).getErr().equals("0")))
      {
        Vector localVector1 = new Vector();
        Vector localVector2 = ((recoverResponse)localObject1).getVrecoverkey();
        if ((localVector2 != null) && (localVector2.size() > 0))
        {
          for (int i = 0; i < localVector2.size(); i++)
          {
            RecoverKeyInfo localRecoverKeyInfo1 = (RecoverKeyInfo)localVector2.get(i);
            String str = localRecoverKeyInfo1.getCertSN();
            CertInfo localCertInfo = CAKeyRecoverOpt.getCertInfo(str);
            if ((localCertInfo == null) || (localCertInfo.getCertEntity() == null))
              throw new CertException("0229", "数据有效性检查 证书不存在");
            RecoverKeyInfo localRecoverKeyInfo2 = new RecoverKeyInfo();
            localRecoverKeyInfo2.setCertSN(str);
            localRecoverKeyInfo2.setCertificate(localCertInfo.getCertEntity());
            localRecoverKeyInfo2.setPrivateKey(localRecoverKeyInfo1.getPrivateKey());
            localRecoverKeyInfo2.setSessionKeyAlg(localRecoverKeyInfo1.getSessionKeyAlg());
            localRecoverKeyInfo2.setSessionKeyPad(localRecoverKeyInfo1.getSessionKeyPad());
            localRecoverKeyInfo2.setEncryptedSessionKey(localRecoverKeyInfo1.getEncryptedSessionKey());
            localVector1.add(localRecoverKeyInfo2);
          }
          this.response.setVrecoverkey(localVector1);
        }
        else
        {
          throw new CertException("0707", "其他错误 从KMC恢复密钥失败");
        }
      }
      else
      {
        throw new CertException("0516", "模板策略检查 与KMCServer通信异常");
      }
      this.operation.setResult(1);
      this.optLogger.info(this.operation);
      this.response.setErr("0");
      this.response.setMsg("success");
    }
    catch (Exception localException2)
    {
      Object localObject1;
      this.debugLogger.appendMsg_L1("Exception:" + localException2.toString());
      if (this.operation != null)
      {
        this.operation.setResult(0);
        try
        {
          this.optLogger.info(this.operation);
        }
        catch (IDAException localIDAException1)
        {
          this.response.setErr("8002" + localIDAException1.getErrCode());
          this.response.setMsg("证书下载服务 " + localIDAException1.getErrDesc());
          this.response.appendDetail(localIDAException1.getHistory());
          this.debugLogger.doLog();
          localObject1 = this.response;
          jsr 130;
        }
      }
      if ((localException2 instanceof IDAException))
      {
        IDAException localIDAException2 = (IDAException)localException2;
        this.response.setErr("8002" + localIDAException2.getErrCode());
        this.response.setMsg("证书下载服务 " + localIDAException2.getErrDesc());
        this.response.appendDetail(localIDAException2.getHistory());
      }
      else
      {
        this.response.setErr("80020706");
        this.response.setMsg("证书下载服务 其他错误 系统错误");
        this.response.appendDetail(localException2);
      }
    }
    finally
    {
      try
      {
        if (LogManager.isDebug())
          this.debugLogger.appendMsg_L2("responseXML=" + new String(this.response.getData()));
      }
      catch (Exception localException3)
      {
        throw new RuntimeException(localException3);
      }
      this.debugLogger.doLog();
    }
    label962: break label962;
  }

  protected void validateCheck()
    throws CertException
  {
    CAKeyRecoverRequest localCAKeyRecoverRequest = new CAKeyRecoverRequest(this.request);
    this.certSubject = localCAKeyRecoverRequest.getCertSubject();
    this.certSN = localCAKeyRecoverRequest.getCertSerialNumbe();
    this.comparetime = localCAKeyRecoverRequest.getComparetime();
    this.temppubkey = localCAKeyRecoverRequest.getPubkey();
    this.keyID = localCAKeyRecoverRequest.getKeyID();
    this.ctmlname = localCAKeyRecoverRequest.getCtmlname();
    this.debugLogger.appendMsg_L2("certSN=" + this.certSN);
    this.debugLogger.appendMsg_L2("certSubject=" + this.certSubject);
    this.debugLogger.appendMsg_L2("tempPubKey=" + this.temppubkey);
    this.debugLogger.appendMsg_L2("comparetime=" + this.comparetime);
    this.debugLogger.appendMsg_L2("keyID=" + this.keyID);
    this.debugLogger.appendMsg_L2("ctmlname=" + this.ctmlname);
    if ((this.temppubkey == null) || (this.temppubkey.toString().trim().equals("")))
    {
      this.debugLogger.appendMsg_L1("tempPubKey=" + this.temppubkey);
      throw new CertException("0513", "模板策略检查 未检测到KMC产生密钥所需的临时公钥");
    }
    if ((this.keyID == null) && (this.certSN == null) && ((this.certSubject == null) || (this.comparetime == null)))
    {
      this.debugLogger.appendMsg_L1("request condition not validate");
      throw new CertException("0235", "数据有效性检查 欲恢复密钥的检索条件不正确");
    }
    if ((this.comparetime != null) && (this.comparetime.length() != 17))
    {
      this.debugLogger.appendMsg_L1("comparetime Length=" + this.comparetime.length());
      throw new CertException("0236", "0236");
    }
  }

  protected void policyCheck()
    throws CertException
  {
  }

  protected void CTMLCheck()
    throws CertException
  {
  }

  protected void certStatusCheck()
    throws CertException
  {
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.CAKeyRecover
 * JD-Core Version:    0.6.0
 */