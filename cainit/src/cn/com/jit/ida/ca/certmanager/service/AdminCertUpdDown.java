package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.CertService;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertRevokeInfo;
import cn.com.jit.ida.ca.certmanager.service.operation.AdminCertUPDDownOpt;
import cn.com.jit.ida.ca.certmanager.service.operation.CertQueryOpt;
import cn.com.jit.ida.ca.certmanager.service.operation.CodeGenerator;
import cn.com.jit.ida.ca.certmanager.service.request.CertUPDDownRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CertUPDDownResponse;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
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
import java.util.Date;

public class AdminCertUpdDown extends CertService
{
  private CertInfo certInfo = new CertInfo();
  private String oldCertSN = null;
  private String certDN = null;
  private String ctmlName = null;
  private String notBefore = null;
  private long lnotBefore;
  private String validity = null;
  private int ivalidity;
  private long lnotAfter;
  private String certStatus = null;
  private String p10 = null;
  private ASN1Set attributes = null;
  private JKeyPair keyPair = null;
  private boolean returnP7B = false;
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.ca.certmanager.service.AdminCertUpdDown");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private Operation operation = null;
  private CertUPDDownResponse response = new CertUPDDownResponse();
  private Request request;
  private String certSN;
  private String authCode;

  public AdminCertUpdDown()
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
      certStatusCheck();
      CTMLCheck();
      CertRevokeInfo localCertRevokeInfo = new CertRevokeInfo();
      localCertRevokeInfo.setCertSN(this.oldCertSN);
      localCertRevokeInfo.setReasonID(4);
      localCertRevokeInfo.setCDPID(this.certInfo.getCdpid());
      localCertRevokeInfo.setApplicant(this.operator.getOperatorDN());
      this.certInfo.setCertSN(this.certSN);
      this.certInfo.setAuthCode(this.authCode);
      this.certInfo.setNotBefore(this.lnotBefore);
      this.certInfo.setValidity(this.ivalidity);
      this.certInfo.setNotAfter(this.lnotAfter);
      this.certInfo.setApplicant(this.operator.getOperatorDN());
      X509Cert localX509Cert = null;
      if (this.attributes == null)
        localX509Cert = AdminCertUPDDownOpt.certUPDDown(localCertRevokeInfo, this.certInfo);
      else
        localX509Cert = AdminCertUPDDownOpt.certUPDDown(localCertRevokeInfo, this.certInfo, this.attributes);
      this.response.setCertSN(this.certSN);
      Object localObject2;
      Object localObject3;
      Object localObject4;
      if (this.returnP7B)
      {
        localObject1 = null;
        localObject2 = null;
        localObject1 = CAConfig.getInstance();
        localObject2 = ((CAConfig)localObject1).getRootCerts();
        localObject3 = new X509Cert[localObject2.length + 1];
        for (int i = 0; i < localObject2.length; i++)
          localObject3[i] = localObject2[i];
        localObject3[(localObject3.length - 1)] = localX509Cert;
        localObject4 = new P7B();
        byte[] arrayOfByte = null;
        try
        {
          arrayOfByte = ((P7B)localObject4).generateP7bData_B64(localObject3);
        }
        catch (PKIException localPKIException)
        {
          throw new CertException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException.getHistory());
        }
        String str = new String(arrayOfByte);
        this.debugLogger.appendMsg_L2("p7b=" + str);
        this.response.setP7b(str);
      }
      else
      {
        localObject1 = this.keyPair.getPrivateKey();
        localObject2 = new PKCS12();
        localObject3 = ((PKCS12)localObject2).generatePfxData((JKey)localObject1, localX509Cert, "JIT".toCharArray());
        localObject4 = new String(Base64.encode(localObject3));
        this.debugLogger.appendMsg_L2("pfx=" + (String)localObject4);
        this.response.setPfx((String)localObject4);
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
          this.response.setErr("8011" + localIDAException1.getErrCode());
          this.response.setMsg("证书更新并下载服务 " + localIDAException1.getErrDesc());
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
    label1111: break label1111;
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
    this.debugLogger.appendMsg_L2("certSN=" + this.oldCertSN);
    this.debugLogger.appendMsg_L2("certDN=" + this.certDN);
    this.debugLogger.appendMsg_L2("ctmlName=" + this.ctmlName);
    this.debugLogger.appendMsg_L2("notBefore=" + this.notBefore);
    this.debugLogger.appendMsg_L2("validity=" + this.validity);
    this.debugLogger.appendMsg_L2("p10" + this.p10);
    if ((this.oldCertSN == null) || (this.oldCertSN.trim().equals("")))
    {
      if ((this.certDN == null) || (this.certDN.trim().equals("")) || (this.ctmlName == null) || (this.ctmlName.trim().equals("")))
      {
        this.debugLogger.appendMsg_L1("数据有效性检查 证书序列号为空时，证书主题和证书模板不能为空");
        throw new CertException("0228", "数据有效性检查 证书序列号为空时，证书主题和证书模板不能为空");
      }
      try
      {
        this.certInfo = CertQueryOpt.queryCertInfo(this.certDN, this.ctmlName);
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
        this.certInfo = CertQueryOpt.queryCertInfo(this.oldCertSN);
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
    this.operation.setObjCertSN(this.oldCertSN);
    this.operation.setObjSubject(this.certDN);
    this.operation.setObjCTMLName(this.ctmlName);
    if (this.certStatus == null)
    {
      this.debugLogger.appendMsg_L1("certInfo=null");
      throw new CertException("0229", "数据有效性检查 证书不存在");
    }
    if ((this.notBefore == null) || (this.notBefore.equals("")))
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
    catch (IDAException localIDAException3)
    {
      throw new CertException(localIDAException3.getErrCode(), localIDAException3.getErrDesc(), localIDAException3.getHistory());
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
    if ((this.validity == null) || (this.validity.equals("")))
    {
      this.debugLogger.appendMsg_L1("validity=" + this.validity);
      throw new CertException("0210", "数据有效性检查 有效期天数为空");
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
    catch (IDAException localIDAException)
    {
      throw new CertException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException.getHistory());
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
    if (str2.equals("LOCAL"))
    {
      this.returnP7B = true;
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
      String str4 = localX509V3CTML2.getKeyType();
      if (localJKey.getKeyType().indexOf(str4) == -1)
      {
        this.debugLogger.appendMsg_L1("user keyType=" + localJKey.getKey());
        this.debugLogger.appendMsg_L1("ctml request keyType=" + str4);
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
      this.returnP7B = false;
    }
    else if (str2.equals("CA"))
    {
      this.returnP7B = false;
      String str3 = localX509V3CTML2.getKeyType();
      int k = localX509V3CTML2.getKeyLength();
      this.debugLogger.appendMsg_L2("CA Generate keyType=" + str3);
      this.debugLogger.appendMsg_L2("CA Generate keyLength=" + k);
      Mechanism localMechanism = null;
      if (str3.equals("RSA"))
      {
        localMechanism = new Mechanism("RSA");
        try
        {
          this.keyPair = localSession.generateKeyPair(localMechanism, k);
        }
        catch (PKIException localPKIException3)
        {
          throw new CertException(localPKIException3.getErrCode(), localPKIException3.getErrDesc(), localPKIException3.getHistory());
        }
      }
      localJKey = this.keyPair.getPublicKey();
    }
    this.certInfo.setPubKey(localJKey);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.AdminCertUpdDown
 * JD-Core Version:    0.6.0
 */