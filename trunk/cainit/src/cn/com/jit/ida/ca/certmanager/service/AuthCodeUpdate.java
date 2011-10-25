package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.CertService;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.service.operation.AuthCodeUpdateOpt;
import cn.com.jit.ida.ca.certmanager.service.operation.CertQueryOpt;
import cn.com.jit.ida.ca.certmanager.service.request.CodeUPDRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CodeUPDResponse;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import cn.com.jit.ida.privilege.TemplateAdmin;

public class AuthCodeUpdate extends CertService
{
  private String certSN = null;
  private String certDN = null;
  private String ctmlName = null;
  private String certStatus = null;
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.ca.certmanager.service.AuthCodeUpdate");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private Operation operation = null;
  private CodeUPDResponse response = new CodeUPDResponse();
  private Request request;

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
      certStatusCheck();
      CTMLCheck();
      String str = AuthCodeUpdateOpt.updateAuthCode(this.certSN, this.operator.getOperatorDN());
      this.debugLogger.appendMsg_L2("newAuthCode=" + str);
      this.operation.setResult(1);
      this.optLogger.info(this.operation);
      this.response.setRefCode(this.certSN);
      this.response.setAuthCode(str);
      this.response.setErr("0");
      this.response.setMsg("success");
    }
    catch (Exception localException2)
    {
      this.debugLogger.appendMsg_L1("Exception:" + localException2.toString());
      if (this.operation != null)
      {
        this.operation.setResult(0);
        CodeUPDResponse localCodeUPDResponse;
        try
        {
          this.optLogger.info(this.operation);
        }
        catch (IDAException localIDAException1)
        {
          this.response.setErr("8012" + localIDAException1.getErrCode());
          this.response.setMsg("授权码更新服务 " + localIDAException1.getErrDesc());
          this.response.appendDetail(localIDAException1.getHistory());
          this.debugLogger.doLog();
          localCodeUPDResponse = this.response;
          jsr 130;
        }
      }
      if ((localException2 instanceof IDAException))
      {
        IDAException localIDAException2 = (IDAException)localException2;
        this.response.setErr("8012" + localIDAException2.getErrCode());
        this.response.setMsg("授权码更新服务 " + localIDAException2.getErrDesc());
        this.response.appendDetail(localIDAException2.getHistory());
      }
      else
      {
        this.response.setErr("80120706");
        this.response.setMsg("授权码更新服务 其他错误 系统错误");
        this.response.appendDetail(localException2);
      }
    }
    finally
    {
      try
      {
        if (LogManager.isDebug2())
          this.debugLogger.appendMsg_L2("responseXML=" + new String(this.response.getData()));
      }
      catch (Exception localException3)
      {
        this.debugLogger.doLog();
        throw new RuntimeException(localException3);
      }
      this.debugLogger.doLog();
    }
    label786: break label786;
  }

  protected void certStatusCheck()
    throws CertException
  {
    this.debugLogger.appendMsg_L2("certStatus=" + this.certStatus);
    if (!this.certStatus.equals("Undown"))
    {
      this.debugLogger.appendMsg_L1("certStatus=" + this.certStatus);
      throw new CertException("0401", "证书状态检查 证书状态非法", new Exception("证书已被下载使用，无法完成授权码更新操作"));
    }
  }

  protected void validateCheck()
    throws CertException
  {
    CodeUPDRequest localCodeUPDRequest = new CodeUPDRequest(this.request);
    this.certSN = localCodeUPDRequest.getCertSN();
    this.certDN = localCodeUPDRequest.getCertDN();
    this.ctmlName = localCodeUPDRequest.getCtmlName();
    this.debugLogger.appendMsg_L2("certSN=" + this.certSN);
    this.debugLogger.appendMsg_L2("certDN=" + this.certDN);
    this.debugLogger.appendMsg_L2("ctmlName=" + this.ctmlName);
    CertInfo localCertInfo = null;
    if ((this.certSN == null) || (this.certSN.trim().equals("")))
    {
      if ((this.certDN == null) || (this.certDN.trim().equals("")) || (this.ctmlName == null) || (this.ctmlName.trim().equals("")))
      {
        this.debugLogger.appendMsg_L1("数据有效性检查 证书序列号为空时，证书主题和证书模板不能为空");
        throw new CertException("0228", "数据有效性检查 证书序列号为空时，证书主题和证书模板不能为空");
      }
      try
      {
        localCertInfo = CertQueryOpt.queryCertInfo(this.certDN, this.ctmlName);
      }
      catch (IDAException localIDAException1)
      {
        throw new CertException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1.getHistory());
      }
      if (localCertInfo != null)
      {
        this.certStatus = localCertInfo.getCertStatus();
        this.certSN = localCertInfo.getCertSN();
      }
    }
    else
    {
      try
      {
        localCertInfo = CertQueryOpt.queryCertInfo(this.certSN);
      }
      catch (IDAException localIDAException2)
      {
        throw new CertException(localIDAException2.getErrCode(), localIDAException2.getErrDesc(), localIDAException2.getHistory());
      }
      if (localCertInfo != null)
      {
        this.certStatus = localCertInfo.getCertStatus();
        this.certDN = localCertInfo.getSubject();
        this.ctmlName = localCertInfo.getCtmlName();
      }
    }
    this.operation.setObjCertSN(this.certSN);
    this.operation.setObjSubject(this.certDN);
    this.operation.setObjCTMLName(this.ctmlName);
    if (this.certStatus == null)
    {
      this.debugLogger.appendMsg_L1("certInfo=null");
      throw new CertException("0229", "数据有效性检查 证书不存在");
    }
  }

  protected void policyCheck()
    throws CertException
  {
    TemplateAdmin localTemplateAdmin = null;
    try
    {
      localTemplateAdmin = TemplateAdmin.getInstance();
    }
    catch (IDAException localIDAException)
    {
      throw new CertException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException.getHistory());
    }
    boolean bool = localTemplateAdmin.isPass(this.operator.getOperatorSN(), this.ctmlName, this.certDN, null);
    if (!bool)
    {
      this.debugLogger.appendMsg_L1("operator=" + this.operator.getOperatorDN());
      this.debugLogger.appendMsg_L1("ctmlName=" + this.ctmlName);
      this.debugLogger.appendMsg_L1("certDN=" + this.certDN);
      throw new CertException("0301", "操作员权限检查 操作员业务权限检查失败", new Exception("操作员不具备此操作权限，请检查操作员是否有操作此模板的权限，并且确保证书DN符合管理员权限范围"));
    }
  }

  protected void CTMLCheck()
    throws CertException
  {
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CTML localCTML = null;
    try
    {
      localCTML = localCTMLManager.getCTML(this.ctmlName);
    }
    catch (IDAException localIDAException)
    {
      throw new CertException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException.getHistory());
    }
    String str = localCTML.getCTMLStatus();
    this.debugLogger.appendMsg_L2("ctmlStatus=" + str);
    if (!str.equals("USING"))
    {
      this.debugLogger.appendMsg_L1("ctmlStatus=" + str);
      throw new CertException("0501", "模板策略检查 证书模板状态无效");
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.AuthCodeUpdate
 * JD-Core Version:    0.6.0
 */