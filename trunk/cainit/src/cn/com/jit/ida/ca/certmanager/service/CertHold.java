package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.CertService;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertRevokeInfo;
import cn.com.jit.ida.ca.certmanager.service.kmc.KMCConnector;
import cn.com.jit.ida.ca.certmanager.service.kmc.KeyStateTrackRequest;
import cn.com.jit.ida.ca.certmanager.service.kmc.KeyStateTrackResponse;
import cn.com.jit.ida.ca.certmanager.service.operation.CertQueryOpt;
import cn.com.jit.ida.ca.certmanager.service.operation.CertRevokeOpt;
import cn.com.jit.ida.ca.certmanager.service.request.CertHoldRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CertHoldResponse;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.TemplateAdmin;

public class CertHold extends CertService
{
  private Request request = null;
  private DebugLogger debugLogger = null;
  private OptLogger optLogger = null;
  private CertHoldResponse response = null;
  private CertRevokeInfo certRvkInfo = null;
  private CertInfo certInfo = null;
  private Operation operation = null;
  private String sn = null;
  private String subject = null;
  private String ctmlName = null;
  private String isWaiting = null;

  public CertHold()
  {
    this.certRvkInfo.setReasonID(6);
    this.response = new CertHoldResponse();
    this.debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.ca.certmanager.service.CertHold");
    this.optLogger = LogManager.getOPtLogger();
    this.operation = new Operation();
  }

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      if (LogManager.isDebug2())
        try
        {
          this.debugLogger.appendMsg_L2("cert hold request request XML:\n" + new String(paramRequest.getData()));
        }
        catch (Exception localException1)
        {
          throw new CertException("0704", "其他错误 请求信息格式不合法");
        }
      this.request = paramRequest;
      this.operator = paramRequest.getOperator();
      if (this.operator == null)
        throw new CertException("0701", "其他错误 操作员为空");
      this.operation.setOptType(paramRequest.getOperation());
      this.operation.setOperatorSN(this.operator.getOperatorSN());
      this.operation.setOperatorDN(this.operator.getOperatorDN());
      this.debugLogger.appendMsg_L2("operation =" + paramRequest.getOperation());
      this.debugLogger.appendMsg_L2("operator SN =" + this.operator.getOperatorSN());
      this.debugLogger.appendMsg_L2("operator DN=" + this.operator.getOperatorDN());
      validateCheck();
      policyCheck();
      certStatusCheck();
      this.certRvkInfo.setCDPID(this.certInfo.getCdpid());
      try
      {
        int i = CertRevokeOpt.holdCert(this.certRvkInfo);
        if (i == 0)
          throw new CertException("0602", "执行业务操作 没有证书被冻结");
      }
      catch (IDAException localIDAException1)
      {
        this.debugLogger.appendMsg_L1("hold cert operation error :" + localIDAException1.toString());
        throw localIDAException1;
      }
      String str1 = null;
      try
      {
        str1 = InternalConfig.getInstance().getAdminTemplateName();
      }
      catch (IDAException localIDAException2)
      {
        this.debugLogger.appendMsg_L1("get admin_CTML_name error :" + localIDAException2.toString());
        throw localIDAException2;
      }
      if (this.ctmlName.equals(str1))
        try
        {
          Privilege.getInstance().delAdmin(this.sn);
          TemplateAdmin.getInstance().delTemplateAdmin(this.sn);
        }
        catch (IDAException localIDAException3)
        {
          this.debugLogger.appendMsg_L1("delete admin's privilege error :" + localIDAException3.toString());
          throw localIDAException3;
        }
      CTMLManager localCTMLManager = CTMLManager.getInstance();
      localObject1 = null;
      String str2 = null;
      try
      {
        localObject1 = localCTMLManager.getCTML(this.ctmlName);
        X509V3CTML localX509V3CTML = (X509V3CTML)localObject1;
        str2 = localX509V3CTML.getKeyGenPlace();
      }
      catch (IDAException localIDAException6)
      {
        this.debugLogger.appendMsg_L1("get ctml keygenpalace err :" + localIDAException6.toString());
        throw localIDAException6;
      }
      if (str2.equals("KMC"))
      {
        KeyStateTrackRequest localKeyStateTrackRequest = new KeyStateTrackRequest();
        localKeyStateTrackRequest.setCertsn(this.sn);
        localKeyStateTrackRequest.setKeystate("hold");
        KMCConnector localKMCConnector = new KMCConnector();
        KeyStateTrackResponse localKeyStateTrackResponse = localKMCConnector.updateKeyStateFromKMC(localKeyStateTrackRequest);
        if ((localKeyStateTrackResponse == null) || (!localKeyStateTrackResponse.equals("0")));
      }
      this.operation.setResult(1);
      try
      {
        this.optLogger.info(this.operation);
      }
      catch (IDAException localIDAException7)
      {
        throw localIDAException7;
      }
      this.response.setErr("0");
      this.response.setMsg("success");
    }
    catch (Exception localException2)
    {
      Object localObject1;
      this.debugLogger.appendMsg_L1("Exception:" + localException2.toString());
      if (this.operator != null)
      {
        this.operation.setResult(0);
        try
        {
          this.optLogger.info(this.operation);
        }
        catch (IDAException localIDAException4)
        {
          this.response.setErr("8005" + localIDAException4.getErrCode());
          this.response.setMsg("证书冻结服务 " + localIDAException4.getErrDesc());
          this.response.appendDetail(localIDAException4.getHistory());
          this.debugLogger.doLog();
          localObject1 = this.response;
          jsr 130;
        }
      }
      if ((localException2 instanceof IDAException))
      {
        IDAException localIDAException5 = (IDAException)localException2;
        this.response.setErr("8005" + localIDAException5.getErrCode());
        this.response.setMsg("证书冻结服务 " + localIDAException5.getErrDesc());
        this.response.appendDetail(localIDAException5.getHistory());
      }
      else
      {
        this.response.setErr("80050706");
        this.response.setMsg("证书冻结服务 其他错误 系统错误");
        this.response.appendDetail(localException2);
      }
    }
    finally
    {
      try
      {
        if (LogManager.isDebug2())
          this.debugLogger.appendMsg_L2("cert hold response XML:\n" + new String(this.response.getData()));
      }
      catch (Exception localException3)
      {
        this.debugLogger.doLog();
        throw new RuntimeException(localException3);
      }
      this.debugLogger.doLog();
    }
    label966: break label966;
  }

  protected void certStatusCheck()
    throws CertException
  {
    String str = this.certInfo.getCertStatus();
    if (!str.equals("Use"))
    {
      this.debugLogger.appendMsg_L1("cert status check error : status = " + str);
      throw new CertException("0401", "证书状态检查 证书状态非法", new Exception("此证书未处于使用状态，无法完成冻结操作"));
    }
  }

  protected void validateCheck()
    throws CertException
  {
    CertHoldRequest localCertHoldRequest = null;
    try
    {
      localCertHoldRequest = new CertHoldRequest(this.request);
    }
    catch (CertException localCertException)
    {
      throw new CertException("0704", "其他错误 请求信息格式不合法");
    }
    String str1 = this.operator.getOperatorDN();
    if ((str1.equals("")) || (str1 == null))
      throw new CertException("0702", "其他错误 操作员DN为空");
    this.certRvkInfo.setApplicant(str1);
    this.certRvkInfo.setCertSN(localCertHoldRequest.getCertSN());
    this.sn = localCertHoldRequest.getCertSN();
    if (this.sn == null)
    {
      this.certRvkInfo.setCertDN(localCertHoldRequest.getCertDN());
      this.subject = localCertHoldRequest.getCertDN();
      this.certRvkInfo.setCtmlName(localCertHoldRequest.getCtmlName());
      this.ctmlName = localCertHoldRequest.getCtmlName();
      if ((this.subject == null) || (this.ctmlName == null))
      {
        this.debugLogger.appendMsg_L1("not enough req : SN = " + this.sn + ";" + "subject = " + this.subject + ";" + "ctmlName = " + this.ctmlName);
        throw new CertException("0228", "数据有效性检查 证书序列号为空时，证书主题和证书模板不能为空");
      }
      InternalConfig localInternalConfig = null;
      try
      {
        localInternalConfig = InternalConfig.getInstance();
      }
      catch (IDAException localIDAException2)
      {
        throw new CertException(localIDAException2.getErrCode(), localIDAException2.getErrDesc(), localIDAException2.getHistory());
      }
      String str3 = localInternalConfig.getAdminTemplateName();
      if ((this.operator.getOperatorDN().trim().equalsIgnoreCase(this.subject.trim())) && (this.ctmlName.trim().equals(str3)))
      {
        this.debugLogger.appendMsg_L1("operator must not operate self ,OperatorDN = " + this.subject);
        this.debugLogger.doLog();
        throw new CertException("0302", "操作员权限检查 操作员没有自操作权限");
      }
      try
      {
        this.certInfo = CertQueryOpt.queryPendingCertInfo(this.subject, this.ctmlName);
      }
      catch (IDAException localIDAException3)
      {
        this.debugLogger.appendMsg_L1("query certInfo error by subject = " + this.subject + " - ctmlName = " + this.ctmlName + ":" + localIDAException3.toString());
        throw new CertException(localIDAException3.getErrCode(), localIDAException3.getErrDescEx(), localIDAException3);
      }
      if (this.certInfo == null)
      {
        this.debugLogger.appendMsg_L1("no certInfo record match with subject = " + this.subject + " - ctmlName = " + this.ctmlName);
        throw new CertException("0230", "数据有效性检查 根据[证书主题+模板名称]条件没有查询到待操作记录");
      }
      this.sn = this.certInfo.getCertSN();
      this.operation.setObjCertSN(this.sn);
      this.operation.setObjSubject(this.subject);
      this.operation.setObjCTMLName(this.ctmlName);
      this.debugLogger.appendMsg_L2("operation cert SN = " + this.sn);
      this.debugLogger.appendMsg_L2("operation subject = " + this.subject);
      this.debugLogger.appendMsg_L2("operation CTML name = " + this.ctmlName);
    }
    else
    {
      if (this.sn.trim().equalsIgnoreCase(this.operator.getOperatorSN().trim()))
      {
        this.debugLogger.appendMsg_L1("operator must not operate self ,OperatorSN = " + this.sn);
        this.debugLogger.doLog();
        throw new CertException("0302", "操作员权限检查 操作员没有自操作权限");
      }
      try
      {
        this.certInfo = CertQueryOpt.queryCertInfo(this.sn);
        this.isWaiting = this.certInfo.getIswaiting();
        if (this.isWaiting.equals("1"))
          this.certInfo = null;
      }
      catch (IDAException localIDAException1)
      {
        this.debugLogger.appendMsg_L1("query certInfo error by SN = " + this.sn + ":" + localIDAException1.toString());
        throw new CertException(localIDAException1.getErrCode(), localIDAException1.getErrDescEx(), localIDAException1);
      }
      if (this.certInfo == null)
      {
        this.debugLogger.appendMsg_L1("no certInfo record match with SN =" + this.sn);
        throw new CertException("0231", "数据有效性检查 根据SN没有查询到记录");
      }
      this.subject = this.certInfo.getSubject();
      this.ctmlName = this.certInfo.getCtmlName();
      this.operation.setObjCertSN(this.sn);
      this.operation.setObjSubject(this.subject);
      this.operation.setObjCTMLName(this.ctmlName);
      this.debugLogger.appendMsg_L2("operation cert SN = " + this.sn);
      this.debugLogger.appendMsg_L2("operation subject = " + this.subject);
      this.debugLogger.appendMsg_L2("operation CTML name = " + this.ctmlName);
    }
    String str2 = localCertHoldRequest.getRevokeDESC();
    if ((str2 != null) && (!str2.trim().equals("")))
    {
      if (str2.trim().length() > 255)
        throw new CertException("0226", "数据有效性检查 冻结证书原因描述的长度超长");
      this.certRvkInfo.setReasonDESC(str2);
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
      this.debugLogger.appendMsg_L1("get template-admin instance error :" + localIDAException.toString());
      throw new CertException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
    boolean bool = localTemplateAdmin.isPass(this.operator.getOperatorSN(), this.ctmlName, this.subject, null);
    if (!bool)
    {
      this.debugLogger.appendMsg_L1("operator=" + this.operator.getOperatorDN());
      this.debugLogger.appendMsg_L1("ctmlName=" + this.ctmlName);
      this.debugLogger.appendMsg_L1("certDN=" + this.subject);
      throw new CertException("0301", "操作员权限检查 操作员业务权限检查失败", new Exception("操作员不具备此操作权限，请检查操作员是否有操作此模板的权限，并且确保证书DN符合管理员权限范围"));
    }
  }

  protected void CTMLCheck()
    throws CertException
  {
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.CertHold
 * JD-Core Version:    0.6.0
 */