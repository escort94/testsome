package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.CertService;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertExtensions;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertPendingRevokeInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertRevokeInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.Extension;
import cn.com.jit.ida.ca.certmanager.service.kmc.KMCCertInfo;
import cn.com.jit.ida.ca.certmanager.service.kmc.KMCConnector;
import cn.com.jit.ida.ca.certmanager.service.kmc.KeyApplyResponse;
import cn.com.jit.ida.ca.certmanager.service.kmc.KeyApplyResponse.EnvelopedData;
import cn.com.jit.ida.ca.certmanager.service.kmc.KeyStateTrackRequest;
import cn.com.jit.ida.ca.certmanager.service.operation.CertQueryOpt;
import cn.com.jit.ida.ca.certmanager.service.operation.CertUPDDownOpt;
import cn.com.jit.ida.ca.certmanager.service.operation.CodeGenerator;
import cn.com.jit.ida.ca.certmanager.service.request.CertUPDDownRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CertUPDDownResponse;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.util.CTMLConstant;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
import cn.com.jit.ida.ca.ctml.x509v3.extension.StandardExtension;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import cn.com.jit.ida.privilege.TemplateAdmin;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.Parser;
import cn.com.jit.ida.util.pki.asn1.ASN1Set;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.JKey;
import cn.com.jit.ida.util.pki.cipher.JKeyPair;
import cn.com.jit.ida.util.pki.cipher.Mechanism;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.encoders.Base64;
import cn.com.jit.ida.util.pki.pkcs.P7B;
import cn.com.jit.ida.util.pki.pkcs.PKCS10;
import cn.com.jit.ida.util.pki.pkcs.PKCS12;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class CertUpdDown extends CertService
{
  private CertInfo certInfo = new CertInfo();
  private CertRevokeInfo certRevokeInfo = new CertRevokeInfo();
  private X509Cert userCert = null;
  private String oldCertSN = null;
  private String certDN = null;
  private String ctmlName = null;
  private String notBefore = null;
  private long lnotBefore = 0L;
  private String validity = null;
  private int ivalidity = 0;
  private long lnotAfter = 0L;
  private String certStatus = null;
  private String p10 = null;
  private ASN1Set attributes = null;
  private JKeyPair keyPair = null;
  private boolean returnP7B = false;
  private boolean isRetainKey;
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.ca.certmanager.service.CertUpdDown");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private Operation operation = null;
  private CertUPDDownResponse response = new CertUPDDownResponse();
  private Request request = null;
  private String certSN = null;
  private String authCode = null;
  private String tempPubKey = null;
  private String encryptedSessionKey = null;
  private String sessionKeyAlg = null;
  private String sessionKeyPad = null;
  private String encryptedPrivateKey = null;
  private boolean useKMC = false;
  private String pendingTime = null;
  private Properties proExtensions = null;
  private Hashtable standardExtensions = null;
  private String isWaiting = null;

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
      if (this.operator.getOperatorDN() == null)
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
      licenceCheck();
      validateCheck();
      parseExtensions();
      policyCheck();
      certStatusCheck();
      CTMLCheck();
      this.certRevokeInfo.setCertSN(this.oldCertSN);
      this.certRevokeInfo.setReasonID(4);
      this.certRevokeInfo.setCDPID(this.certInfo.getCdpid());
      this.certRevokeInfo.setApplicant(this.operator.getOperatorDN());
      this.certInfo.setCertSN(this.certSN);
      this.certInfo.setAuthCode(this.authCode);
      this.certInfo.setNotBefore(this.lnotBefore);
      this.certInfo.setValidity(this.ivalidity);
      this.certInfo.setNotAfter(this.lnotAfter);
      this.certInfo.setApplicant(this.operator.getOperatorDN());
      if (this.isRetainKey)
        this.certInfo.setIsRetainKey(this.oldCertSN);
      InternalConfig localInternalConfig = InternalConfig.getInstance();
      String str1 = localInternalConfig.getAdminTemplateName();
      if ((this.pendingTime != null) && (!this.pendingTime.trim().equals("")))
      {
        long l1 = Integer.parseInt(this.pendingTime) * 24L * 60L * 60L * 1000L;
        long l2 = System.currentTimeMillis();
        long l3 = l1 + l2;
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String str2 = localSimpleDateFormat.format(new Date(l3));
        long l4 = Long.parseLong(str2);
        CertPendingRevokeInfo localCertPendingRevokeInfo = new CertPendingRevokeInfo();
        localCertPendingRevokeInfo.setApplicant(this.operator.getOperatorDN());
        localCertPendingRevokeInfo.setCertSN(this.oldCertSN);
        localCertPendingRevokeInfo.setCDPID(this.certInfo.getCdpid());
        localCertPendingRevokeInfo.setOptType("CERTREVOKE");
        localCertPendingRevokeInfo.setExectime(l4);
        localCertPendingRevokeInfo.setReasonID(4);
        localCertPendingRevokeInfo.setSubject(this.certDN);
        if (this.lnotAfter <= l4)
        {
          localCertPendingRevokeInfo.setExectime(this.lnotAfter);
          CertUPDDownOpt.saveCertPendingTask(localCertPendingRevokeInfo);
          CertUPDDownOpt.modifyCertPendingStatus(this.oldCertSN);
          this.userCert = isPending(str1);
        }
        if (this.lnotAfter > l4)
        {
          CertUPDDownOpt.saveCertPendingTask(localCertPendingRevokeInfo);
          CertUPDDownOpt.modifyCertPendingStatus(this.oldCertSN);
          this.userCert = isPending(str1);
        }
      }
      else
      {
        this.userCert = noPending(str1);
      }
      this.response.setCertSN(this.certSN);
      Object localObject4;
      Object localObject5;
      Object localObject6;
      if (this.returnP7B)
      {
        localObject1 = CAConfig.getInstance();
        localObject2 = ((CAConfig)localObject1).getRootCerts();
        localObject3 = new X509Cert[localObject2.length + 1];
        for (int i = 0; i < localObject2.length; i++)
          localObject3[i] = localObject2[i];
        localObject3[(localObject3.length - 1)] = this.userCert;
        localObject4 = new P7B();
        localObject5 = null;
        try
        {
          localObject5 = ((P7B)localObject4).generateP7bData_B64(localObject3);
        }
        catch (PKIException localPKIException)
        {
          throw new CertException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException.getHistory());
        }
        localObject6 = new String(localObject5);
        this.debugLogger.appendMsg_L2("p7b=" + (String)localObject6);
        this.response.setP7b((String)localObject6);
        if (this.useKMC)
        {
          this.response.setEncryptedSessionKey(this.encryptedSessionKey);
          this.response.setSessionKeyAlg(this.sessionKeyAlg);
          this.response.setSessionKeyPad(this.sessionKeyPad);
          this.response.setEncryptedPrivateKey(this.encryptedPrivateKey);
        }
      }
      else
      {
        localObject1 = this.keyPair.getPrivateKey();
        localObject2 = new PKCS12();
        localObject3 = ((PKCS12)localObject2).generatePfxData((JKey)localObject1, this.userCert, "JIT".toCharArray());
        localObject4 = new String(Base64.encode(localObject3));
        this.debugLogger.appendMsg_L2("pfx=" + (String)localObject4);
        this.response.setPfx((String)localObject4);
      }
      localObject1 = CTMLManager.getInstance();
      Object localObject2 = null;
      Object localObject3 = null;
      try
      {
        localObject2 = ((CTMLManager)localObject1).getCTML(this.ctmlName);
        localObject4 = (X509V3CTML)localObject2;
        localObject3 = ((X509V3CTML)localObject4).getKeyGenPlace();
      }
      catch (IDAException localIDAException3)
      {
        this.debugLogger.appendMsg_L1("get ctml keygenpalace err :" + localIDAException3.toString());
        throw localIDAException3;
      }
      if (((String)localObject3).equals("KMC"))
      {
        KeyStateTrackRequest localKeyStateTrackRequest = new KeyStateTrackRequest();
        localKeyStateTrackRequest.setCertsn(this.oldCertSN);
        localKeyStateTrackRequest.setKeystate("revoked");
        localObject5 = new KMCConnector();
        localObject6 = ((KMCConnector)localObject5).updateKeyStateFromKMC(localKeyStateTrackRequest);
        if ((localObject6 == null) || (!localObject6.equals("0")));
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
          this.response.setErr("80110703");
          this.response.setMsg("证书更新并下载服务 其他错误 记录业务日志失败");
          this.response.appendDetail(localIDAException1.getHistory());
          this.debugLogger.doLog();
          localObject1 = this.response;
          jsr 130;
        }
      }
      if ((localException2 instanceof IDAException))
      {
        IDAException localIDAException2 = (IDAException)localException2;
        this.response.setErr("8011" + localIDAException2.getErrCode());
        this.response.setMsg("证书更新并下载服务 " + localIDAException2.getErrDesc());
        this.response.appendDetail(localIDAException2.getHistory());
      }
      else
      {
        this.response.setErr("80110706");
        this.response.setMsg("证书更新并下载服务 其他错误 系统错误");
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
    label1517: break label1517;
  }

  protected void certStatusCheck()
    throws CertException
  {
    this.debugLogger.appendMsg_L2("certStatus=" + this.certStatus);
    if (!this.certStatus.equals("Use"))
    {
      this.debugLogger.appendMsg_L1("certStatus=" + this.certStatus);
      throw new CertException("0401", "证书状态检查 证书状态非法", new Exception("此证书未处于使用状态，无法完成更新操作"));
    }
  }

  protected void validateCheck()
    throws CertException
  {
    CertUPDDownRequest localCertUPDDownRequest = new CertUPDDownRequest(this.request);
    this.oldCertSN = localCertUPDDownRequest.getCertSN();
    this.certDN = localCertUPDDownRequest.getCertDN();
    this.ctmlName = localCertUPDDownRequest.getCtmlName();
    this.notBefore = localCertUPDDownRequest.getNotBefore();
    this.validity = localCertUPDDownRequest.getValidity();
    this.p10 = localCertUPDDownRequest.getP10();
    this.tempPubKey = localCertUPDDownRequest.getTempPubKey();
    this.isRetainKey = localCertUPDDownRequest.getIsRetainKey();
    this.debugLogger.appendMsg_L2("certSN=" + this.oldCertSN);
    this.debugLogger.appendMsg_L2("certDN=" + this.certDN);
    this.debugLogger.appendMsg_L2("ctmlName=" + this.ctmlName);
    this.debugLogger.appendMsg_L2("notBefore=" + this.notBefore);
    this.debugLogger.appendMsg_L2("validity=" + this.validity);
    this.debugLogger.appendMsg_L2("p10" + this.p10);
    this.debugLogger.appendMsg_L2("tempPubKey=" + this.tempPubKey);
    this.debugLogger.appendMsg_L2("isRetainKey=" + this.isRetainKey);
    if ((this.oldCertSN == null) || (this.oldCertSN.trim().equals("")))
    {
      if ((this.certDN == null) || (this.certDN.trim().equals("")) || (this.ctmlName == null) || (this.ctmlName.trim().equals("")))
      {
        this.debugLogger.appendMsg_L1("数据有效性检查 证书序列号为空时，证书主题和证书模板不能为空");
        throw new CertException("0228", "数据有效性检查 证书序列号为空时，证书主题和证书模板不能为空");
      }
      try
      {
        this.operation.setObjSubject(this.certDN);
        this.operation.setObjCTMLName(this.ctmlName);
        this.certInfo = CertQueryOpt.queryPendingCertInfo(this.certDN, this.ctmlName);
      }
      catch (IDAException localIDAException1)
      {
        throw new CertException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1.getHistory());
      }
      if (this.certInfo != null)
      {
        this.certStatus = this.certInfo.getCertStatus();
        this.oldCertSN = this.certInfo.getCertSN();
      }
    }
    else
    {
      try
      {
        this.operation.setObjCertSN(this.oldCertSN);
        this.certInfo = CertQueryOpt.queryCertInfo(this.oldCertSN);
        this.isWaiting = this.certInfo.getIswaiting();
        if (this.isWaiting.equals("1"))
          this.certInfo = null;
      }
      catch (IDAException localIDAException2)
      {
        throw new CertException(localIDAException2.getErrCode(), localIDAException2.getErrDesc(), localIDAException2.getHistory());
      }
      if (this.certInfo != null)
      {
        this.certStatus = this.certInfo.getCertStatus();
        this.certDN = this.certInfo.getSubject();
        this.ctmlName = this.certInfo.getCtmlName();
      }
    }
    if (this.certStatus == null)
    {
      this.debugLogger.appendMsg_L1("certInfo=null");
      throw new CertException("0229", "数据有效性检查 证书不存在");
    }
    InternalConfig localInternalConfig = null;
    try
    {
      localInternalConfig = InternalConfig.getInstance();
    }
    catch (IDAException localIDAException3)
    {
      throw new CertException(localIDAException3.getErrCode(), localIDAException3.getErrDesc(), localIDAException3.getHistory());
    }
    String str = localInternalConfig.getAdminTemplateName();
    if ((this.certDN.toUpperCase().trim().equals(this.operator.getOperatorDN().toUpperCase().trim())) && (this.ctmlName.trim().equals(str)))
    {
      this.debugLogger.appendMsg_L1("operatorDN=" + this.operator.getOperatorDN() + " && certDN=" + this.certDN);
      throw new CertException("0234", "数据有效性检查 禁止管理员对本身进行操作");
    }
    if ((this.notBefore == null) || (this.notBefore.trim().equals("")))
    {
      this.debugLogger.appendMsg_L1("notBefore=" + this.notBefore);
      throw new CertException("0206", "数据有效性检查 有效期起始日期为空");
    }
    if (this.notBefore.length() != 17)
    {
      this.debugLogger.appendMsg_L1("notBefore Length=" + this.notBefore.length());
      throw new CertException("0207", "数据有效性检查 有效期起始日期长度错误");
    }
    try
    {
      this.lnotBefore = Long.parseLong(this.notBefore);
    }
    catch (Exception localException1)
    {
      this.debugLogger.appendMsg_L1("notBefore=" + this.notBefore);
      throw new CertException("0209", "数据有效性检查 有效期起始日期格式格式不合法");
    }
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    Date localDate1 = null;
    CAConfig localCAConfig = null;
    try
    {
      localDate1 = localSimpleDateFormat.parse(this.notBefore);
    }
    catch (Exception localException2)
    {
      this.debugLogger.appendMsg_L1("notBefore=" + this.notBefore);
      throw new CertException("0209", "数据有效性检查 有效期起始日期格式格式不合法", localException2);
    }
    try
    {
      localCAConfig = CAConfig.getInstance();
    }
    catch (IDAException localIDAException4)
    {
      throw new CertException(localIDAException4.getErrCode(), localIDAException4.getErrDesc(), localIDAException4.getHistory());
    }
    long l = localCAConfig.getTimeDifAllow();
    Date localDate2 = new Date(localDate1.getTime() + l);
    Date localDate3 = new Date();
    this.debugLogger.appendMsg_L2("notBefore Date=" + localDate1.toString());
    this.debugLogger.appendMsg_L2("timeDifAllow=" + l);
    this.debugLogger.appendMsg_L2("System Date=" + localDate3.toString());
    if (localDate2.compareTo(localDate3) < 0)
    {
      this.debugLogger.appendMsg_L1("notBefore Date=" + localDate1.toString());
      this.debugLogger.appendMsg_L1("timeDifAllow=" + l);
      this.debugLogger.appendMsg_L1("System Date=" + localDate3.toString());
      throw new CertException("0208", "数据有效性检查 有效期起始日期超出服务允许范围");
    }
    if ((this.validity == null) || (this.validity.trim().equals("")))
    {
      this.debugLogger.appendMsg_L1("validity=" + this.validity);
      throw new CertException("0210", "数据有效性检查 有效期天数为空");
    }
    if (this.validity.length() > 6)
    {
      this.debugLogger.appendMsg_L1("validity length=" + this.validity.length());
      throw new CertException("0222", "数据有效性检查 有效期长度内容超长");
    }
    try
    {
      this.ivalidity = Integer.parseInt(this.validity);
    }
    catch (Exception localException3)
    {
      this.debugLogger.appendMsg_L1("validity=" + this.validity);
      throw new CertException("0211", "数据有效性检查 有效期格式不合法", localException3);
    }
    if (this.ivalidity <= 0)
    {
      this.debugLogger.appendMsg_L1("validity value=" + this.ivalidity);
      throw new CertException("0212", "数据有效性检查 ");
    }
    String[] arrayOfString = CodeGenerator.generateCodes();
    this.certSN = arrayOfString[0];
    this.authCode = arrayOfString[1];
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
    catch (IDAException localIDAException1)
    {
      throw new CertException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1.getHistory());
    }
    String str1 = localCTML.getCTMLStatus();
    this.debugLogger.appendMsg_L2("ctmlName=" + this.ctmlName);
    this.debugLogger.appendMsg_L2("ctmlStatus=" + str1);
    if (!str1.equals("USING"))
    {
      this.debugLogger.appendMsg_L1("ctmlStatus=" + str1);
      throw new CertException("0501", "模板策略检查 证书模板状态无效");
    }
    X509V3CTML localX509V3CTML1 = (X509V3CTML)localCTML;
    long l1 = localX509V3CTML1.getNotAfter();
    this.pendingTime = localX509V3CTML1.getUpdTransPeriod();
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    Date localDate1 = null;
    Date localDate2 = null;
    Date localDate3 = null;
    try
    {
      localDate1 = localSimpleDateFormat.parse(this.notBefore);
    }
    catch (Exception localException1)
    {
      this.debugLogger.appendMsg_L1("notBefore=" + this.notBefore);
      throw new CertException("0209", "数据有效性检查 有效期起始日期格式格式不合法", localException1);
    }
    this.debugLogger.appendMsg_L2("certNotBefore Date=" + localDate1.toString());
    try
    {
      localDate3 = localSimpleDateFormat.parse(Long.toString(l1));
    }
    catch (Exception localException2)
    {
      this.debugLogger.appendMsg_L1("ctmlNotAfter=" + l1);
      throw new CertException("0508", "模板策略检查 解析模板终止有效期错误");
    }
    this.debugLogger.appendMsg_L2("ctmlNotAfter Date=" + localDate3.toString());
    int i = localX509V3CTML1.getMaxValidty();
    this.debugLogger.appendMsg_L2("ctmlMaxValidity=" + i);
    if (this.ivalidity > i)
    {
      this.debugLogger.appendMsg_L1("ivalidity=" + this.ivalidity);
      this.debugLogger.appendMsg_L1("ctmlMaxValidity=" + i);
      throw new CertException("0502", "模板策略检查 超出证书模板允许有效期的最大效期");
    }
    long l2 = localDate1.getTime();
    long l3 = this.ivalidity * 24L * 60L * 60L * 1000L;
    long l4 = l2 + l3;
    localDate2 = new Date(l4);
    this.debugLogger.appendMsg_L2("certNotAfter Date=" + localDate2.toString());
    this.lnotAfter = Long.parseLong(localSimpleDateFormat.format(localDate2));
    if (localDate2.compareTo(localDate3) > 0)
    {
      this.debugLogger.appendMsg_L1("certNotAfter Date=" + localDate2.toString());
      this.debugLogger.appendMsg_L1("ctmlNotAfter Date=" + localDate3.toString());
      throw new CertException("0503", "模板策略检查 超出模板有效期范围");
    }
    CAConfig localCAConfig = null;
    try
    {
      localCAConfig = CAConfig.getInstance();
    }
    catch (IDAException localIDAException2)
    {
      throw new CertException(localIDAException2.getErrCode(), localIDAException2.getErrDesc(), localIDAException2.getHistory());
    }
    X509Cert localX509Cert = localCAConfig.getRootCert();
    Date localDate4 = localX509Cert.getNotAfter();
    if (localDate2.compareTo(localDate4) > 0)
    {
      this.debugLogger.appendMsg_L1("certNotAfter Date=" + localDate2.toString());
      this.debugLogger.appendMsg_L1("rootCertNotAfter Date=" + localDate4.toString());
      throw new CertException("0224", "数据有效性检查 证书终止有效期超过CA签名证书有效期");
    }
    JCrypto localJCrypto = JCrypto.getInstance();
    Session localSession = null;
    try
    {
      localSession = localJCrypto.openSession("JSOFT_LIB");
    }
    catch (PKIException localPKIException1)
    {
      throw new CertException(localPKIException1.getErrCode(), localPKIException1.getErrDesc(), localPKIException1.getHistory());
    }
    X509V3CTML localX509V3CTML2 = (X509V3CTML)localCTML;
    String str2 = localX509V3CTML2.getKeyGenPlace();
    JKey localJKey = null;
    Object localObject2;
    Object localObject1;
    Object localObject3;
    if (str2.equals("LOCAL"))
    {
      this.returnP7B = true;
      this.useKMC = false;
      if ((this.p10 == null) || (this.p10.trim().equals("")))
      {
        this.debugLogger.appendMsg_L1("p10=" + this.p10);
        throw new CertException("0218", "数据有效性检查 P10申请书必须非空");
      }
      int j = 0;
      try
      {
        PKCS10 localPKCS10 = new PKCS10(localSession);
        localPKCS10.load(this.p10.getBytes());
        localJKey = localPKCS10.getPubKey();
        this.attributes = localPKCS10.getAttributes();
        j = Parser.getRSAKeyLength(localJKey);
      }
      catch (PKIException localPKIException2)
      {
        throw new CertException(localPKIException2.getErrCode(), localPKIException2.getErrDesc(), localPKIException2.getHistory());
      }
      this.debugLogger.appendMsg_L2("p10 keyType=" + localJKey.getKeyType());
      this.debugLogger.appendMsg_L2("p10 keyLength=" + j);
      localObject2 = localX509V3CTML2.getKeyType();
      if (localJKey.getKeyType().indexOf((String)localObject2) == -1)
      {
        this.debugLogger.appendMsg_L1("user keyType=" + localJKey.getKey());
        this.debugLogger.appendMsg_L1("ctml request keyType=" + (String)localObject2);
        throw new CertException("0506", "模板策略检查 密钥类型不合法");
      }
      if (j != localX509V3CTML2.getKeyLength())
      {
        this.debugLogger.appendMsg_L1("user keyLength=" + j);
        this.debugLogger.appendMsg_L1("ctml request keyLength=" + localX509V3CTML2.getKeyLength());
        throw new CertException("0507", "模板策略检查 密钥长度不合法");
      }
    }
    else if (str2.equals("KMC"))
    {
      this.returnP7B = true;
      this.useKMC = true;
      this.response.setUseKMC("TRUE");
      if ((this.tempPubKey == null) || (this.tempPubKey.trim().equals("")))
      {
        this.debugLogger.appendMsg_L1("tempPubKey=" + this.tempPubKey);
        throw new CertException("0513", "模板策略检查 未检测到KMC产生密钥所需的临时公钥", new Exception("请选用CSP方式向KMCServer申请密钥"));
      }
      localObject1 = new KMCCertInfo();
      ((KMCCertInfo)localObject1).setCertSN(this.certSN);
      ((KMCCertInfo)localObject1).setCertDN(this.certDN);
      ((KMCCertInfo)localObject1).setNotBefore(this.lnotBefore);
      ((KMCCertInfo)localObject1).setNotAfter(this.lnotAfter);
      ((KMCCertInfo)localObject1).setValidity(this.ivalidity);
      ((KMCCertInfo)localObject1).setCtmlName(this.ctmlName);
      ((KMCCertInfo)localObject1).setTempPubKey(this.tempPubKey);
      if (this.isRetainKey)
      {
        ((KMCCertInfo)localObject1).setRetainKey("true");
        ((KMCCertInfo)localObject1).setOldCertSN(this.oldCertSN);
      }
      localObject2 = new KMCConnector();
      localObject3 = ((KMCConnector)localObject2).requestKeyFromKMC((KMCCertInfo)localObject1);
      if (!((KeyApplyResponse)localObject3).getErr().equals("0"))
      {
        localObject4 = ((KeyApplyResponse)localObject3).getDetail();
        Exception localException3 = null;
        if ((localObject4 != null) && (localObject4.length > 0))
          if (localObject4[0].length == 1)
            localException3 = new Exception(localObject4[0][0]);
          else if (localObject4[0].length == 2)
            localException3 = new Exception(localObject4[0][0] + " " + localObject4[0][1]);
        throw new CertException("0517", "模板策略检查 向KMC申请密钥失败:" + ((KeyApplyResponse)localObject3).getMsg(), localException3);
      }
      Object localObject4 = ((KeyApplyResponse)localObject3).getPubKey();
      this.encryptedSessionKey = new String(((KeyApplyResponse)localObject3).getEnvelopedData().getEncryptedSessionKey());
      this.sessionKeyAlg = ((KeyApplyResponse)localObject3).getEnvelopedData().getSessionKeyAlg();
      this.sessionKeyPad = ((KeyApplyResponse)localObject3).getEnvelopedData().getSessionKeyPad();
      this.encryptedPrivateKey = new String(((KeyApplyResponse)localObject3).getEnvelopedData().getEncryptedPrivateKey());
      try
      {
        localObject4 = Parser.hardKey2SoftKey("RSA_Public", Base64.decode(localObject4));
      }
      catch (PKIException localPKIException4)
      {
        throw new CertException("05" + localPKIException4.getErrCode(), "模板策略检查 " + localPKIException4.getErrDesc(), localPKIException4.getHistory());
      }
      String str4 = localX509V3CTML2.getKeyType();
      if (str4.equals("RSA"))
        localJKey = new JKey("RSA_Public", localObject4);
    }
    else if (str2.equals("CA"))
    {
      this.returnP7B = false;
      this.useKMC = false;
      localObject1 = localX509V3CTML2.getKeyType();
      int m = localX509V3CTML2.getKeyLength();
      this.debugLogger.appendMsg_L2("CA Generate keyType=" + (String)localObject1);
      this.debugLogger.appendMsg_L2("CA Generate keyLength=" + m);
      localObject3 = null;
      if (((String)localObject1).equals("RSA"))
      {
        localObject3 = new Mechanism("RSA");
        try
        {
          this.keyPair = localSession.generateKeyPair((Mechanism)localObject3, m);
        }
        catch (PKIException localPKIException3)
        {
          throw new CertException(localPKIException3.getErrCode(), localPKIException3.getErrDesc(), localPKIException3.getHistory());
        }
      }
      localJKey = this.keyPair.getPublicKey();
    }
    this.certInfo.setPubKey(localJKey);
    int i3;
    Object localObject6;
    String str6;
    if (this.standardExtensions != null)
    {
      localObject1 = this.standardExtensions.elements();
      Vector localVector1 = new Vector();
      while (((Enumeration)localObject1).hasMoreElements())
      {
        localObject3 = (Vector)((Enumeration)localObject1).nextElement();
        localVector1.addAll((Collection)localObject3);
      }
      localObject3 = new Vector();
      int i1 = localVector1.size();
      i3 = 0;
      int i4 = 0;
      for (int i6 = 0; i6 < i1; i6++)
      {
        localObject6 = (StandardExtension)localVector1.get(i6);
        str6 = ((StandardExtension)localObject6).getStandardValue();
        String str7 = ((StandardExtension)localObject6).getAllowNull();
        String str8 = ((StandardExtension)localObject6).getParentName();
        String str9 = ((StandardExtension)localObject6).getChildName();
        if ((str8.equals("PolicyMappings")) && (str6.equals("")))
          i3 = i3 == 0 ? 1 : 0;
        if ((str8.equals("PolicyConstrants")) && (str6.equals("")))
          i4 = i4 == 0 ? 1 : 0;
        String str10 = null;
        if (str9.equalsIgnoreCase("otherName"))
        {
          str11 = ((StandardExtension)localObject6).getOtherNameOid();
          str10 = (String)CTMLConstant.standardExtAndEncodeType.get(str11 + "@" + str8);
        }
        else
        {
          str10 = (String)CTMLConstant.standardExtAndEncodeType.get(str9 + "@" + str8);
        }
        String str11 = (String)CTMLConstant.standardExtAndLable.get(str9 + "@" + str8);
        if ((str7.equalsIgnoreCase("FALSE")) && ((str6 == null) || (str6.trim().equals(""))))
        {
          this.debugLogger.appendMsg_L1("StandardExtension name=" + str11);
          this.debugLogger.appendMsg_L1("StandardExtension value=" + str6);
          throw new CertException("0518", "模板策略检查 标准扩展域内容格式不合法", new Exception("标准扩展域 [" + str11 + "] 不能为空."));
        }
        if ((str7.equalsIgnoreCase("TRUE")) && ((str6 == null) || (str6.trim().equals(""))))
          ((Vector)localObject3).add(localObject6);
        if ((str6 == null) || (str6.trim().equals("")))
          continue;
        boolean bool2 = checkExtenValue(str10, str6);
        if (bool2)
          continue;
        this.debugLogger.appendMsg_L1("StandardExtension name=" + str11);
        this.debugLogger.appendMsg_L1("StandardExtension encoding=" + str10);
        this.debugLogger.appendMsg_L1("StandardExtension value=" + str6);
        throw new CertException("0518", "模板策略检查 标准扩展域内容格式不合法", new Exception("标准扩展域 [" + str11 + "] 必须是 " + str10 + " 类型."));
      }
      if ((i3 == 1) || (i4 == 1))
        throw new CertException("0518", "模板策略检查 标准扩展域内容格式不合法", new Exception("使用策略映射或策略限制必须成对赋值"));
      this.certInfo.setStandardExtensionsHT(this.standardExtensions);
      localVector1.removeAll((Collection)localObject3);
      this.certInfo.setStandardExtensions(localVector1);
    }
    int k = localX509V3CTML1.getExtensionCount();
    String str3;
    for (int n = 0; n < k; n++)
    {
      localObject3 = localX509V3CTML1.getExtensionName(n);
      if (!localX509V3CTML1.getUserProcessPolicy(n).equals("NEED"))
        continue;
      if ((this.proExtensions == null) || (!this.proExtensions.containsKey(localObject3)))
      {
        this.debugLogger.appendMsg_L1((String)localObject3 + " value=null");
        throw new CertException("0504", "模板策略检查 必须的扩展域信息不完整", new Exception("error occurred in extion iterm " + (String)localObject3));
      }
      str3 = (String)this.proExtensions.get(localObject3);
      if ((str3 != null) && (!str3.equals("")))
        continue;
      this.debugLogger.appendMsg_L1((String)localObject3 + " value=" + str3);
      throw new CertException("0504", "模板策略检查 必须的扩展域信息不完整", new Exception("error occurred in extion iterm " + (String)localObject3));
    }
    if (this.proExtensions != null)
    {
      Enumeration localEnumeration = this.proExtensions.keys();
      while (localEnumeration.hasMoreElements())
      {
        localObject3 = (String)localEnumeration.nextElement();
        str3 = this.proExtensions.getProperty((String)localObject3);
        if ((str3 != null) && (!str3.trim().equals("")))
          continue;
        this.proExtensions.remove(localObject3);
      }
      localObject3 = new Extension[this.proExtensions.size()];
      int i2 = 0;
      Object localObject5;
      for (i3 = 0; i3 < k; i3++)
      {
        String str5 = localX509V3CTML1.getExtensionName(i3);
        localObject5 = localX509V3CTML1.getExtensionEncoding(i3);
        if (!this.proExtensions.containsKey(str5))
          continue;
        localObject6 = localX509V3CTML1.getExtensionOID(i3);
        localObject3[i2] = new Extension();
        localObject3[i2].setName(str5);
        localObject3[i2].setOid((String)localObject6);
        str6 = this.proExtensions.getProperty(str5);
        int i7 = str6.getBytes().length;
        if (i7 > 4000)
        {
          this.debugLogger.appendMsg_L1("user self extension value length=" + i7);
          this.debugLogger.appendMsg_L1("sytem self extension max length=4000");
          throw new CertException("0512", "模板策略检查 自定义扩展域内容格式不合法", new Exception("the value of self extension is too long, max size [4000]"));
        }
        if ((str6 != null) && (!str6.trim().equals("")))
        {
          boolean bool1 = CertReqDown.checkExtValue((String)localObject5, str6);
          if (!bool1)
          {
            this.debugLogger.appendMsg_L1("extension name=" + str5);
            this.debugLogger.appendMsg_L1("extension encoding=" + (String)localObject5);
            this.debugLogger.appendMsg_L1("extension value=" + str6);
            throw new CertException("0512", "模板策略检查 自定义扩展域内容格式不合法", new Exception("self extension [" + str5 + "] must be " + (String)localObject5 + " type."));
          }
        }
        localObject3[i2].setValue(this.proExtensions.getProperty(str5));
        i2++;
      }
      Vector localVector2 = new Vector();
      for (int i5 = 0; i5 < localObject3.length; i5++)
      {
        if (localObject3[i5] == null)
          continue;
        localVector2.add(localObject3[i5]);
      }
      Extension[] arrayOfExtension = new Extension[localVector2.size()];
      if (arrayOfExtension.length > 0)
      {
        localVector2.toArray(arrayOfExtension);
        localObject5 = new CertExtensions(arrayOfExtension);
        this.certInfo.setCertExtensions((CertExtensions)localObject5);
      }
    }
  }

  private void parseExtensions()
    throws CertException
  {
    CertUPDDownRequest localCertUPDDownRequest = new CertUPDDownRequest(this.request);
    this.standardExtensions = localCertUPDDownRequest.getStandardExtensions();
    if (this.standardExtensions == null)
    {
      this.certInfo.setStandardExtensionsHT(null);
      this.certInfo.setStandardExtensions(null);
    }
    this.proExtensions = localCertUPDDownRequest.getExtensions();
    if (this.proExtensions == null)
      this.certInfo.setExtensions(null);
  }

  private boolean checkExtenValue(String paramString1, String paramString2)
  {
    String[] arrayOfString = paramString2.split("[;]");
    int i = 1;
    for (int j = 0; j < arrayOfString.length; j++)
    {
      boolean bool = CertReqDown.checkExtValue(paramString1, arrayOfString[j].trim());
      if (!bool)
        return false;
    }
    return i;
  }

  public X509Cert isPending(String paramString)
    throws IDAException
  {
    if (this.ctmlName.equals(paramString))
    {
      if (this.attributes == null)
        this.userCert = CertUPDDownOpt.certPendingUPDDown(this.certRevokeInfo, this.certInfo, true);
      else
        this.userCert = CertUPDDownOpt.certPendingUPDDown(this.certRevokeInfo, this.certInfo, this.attributes, true);
    }
    else if (this.attributes == null)
      this.userCert = CertUPDDownOpt.certPendingUPDDown(this.certRevokeInfo, this.certInfo, false);
    else
      this.userCert = CertUPDDownOpt.certPendingUPDDown(this.certRevokeInfo, this.certInfo, this.attributes, false);
    return this.userCert;
  }

  public X509Cert noPending(String paramString)
    throws IDAException
  {
    if (this.ctmlName.equals(paramString))
    {
      if (this.attributes == null)
        this.userCert = CertUPDDownOpt.certUPDDown(this.certRevokeInfo, this.certInfo, true);
      else
        this.userCert = CertUPDDownOpt.certUPDDown(this.certRevokeInfo, this.certInfo, this.attributes, true);
    }
    else if (this.attributes == null)
      this.userCert = CertUPDDownOpt.certUPDDown(this.certRevokeInfo, this.certInfo, false);
    else
      this.userCert = CertUPDDownOpt.certUPDDown(this.certRevokeInfo, this.certInfo, this.attributes, false);
    return this.userCert;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.CertUpdDown
 * JD-Core Version:    0.6.0
 */