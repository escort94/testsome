package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.CertService;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.service.kmc.KMCCertInfo;
import cn.com.jit.ida.ca.certmanager.service.kmc.KMCConnector;
import cn.com.jit.ida.ca.certmanager.service.kmc.KeyApplyResponse;
import cn.com.jit.ida.ca.certmanager.service.kmc.KeyApplyResponse.EnvelopedData;
import cn.com.jit.ida.ca.certmanager.service.operation.CertDownloadOpt;
import cn.com.jit.ida.ca.certmanager.service.operation.CertQueryOpt;
import cn.com.jit.ida.ca.certmanager.service.request.CertDownloadRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CertDownloadResponse;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import cn.com.jit.ida.privilege.Privilege;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserCertDownload extends CertService
{
  private CertInfo certInfo = null;
  private String refCode = null;
  private String authCode = null;
  private String p10 = null;
  private String certStatus = null;
  private String certDN = null;
  private String ctmlName = null;
  private ASN1Set attributes = null;
  private JKeyPair keyPair;
  private boolean returnP7B = false;
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.ca.certmanager.service.CertDownload");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private Operation operation = null;
  private CertDownloadResponse response = new CertDownloadResponse();
  private Request request;
  private String tempPubKey = null;
  private String certSN = null;
  private long lnotBefore = 0L;
  private long lnotAfter = 0L;
  private int ivalidity = 0;
  private String encryptedSessionKey = null;
  private String sessionKeyAlg = null;
  private String sessionKeyPad = null;
  private String encryptedPrivateKey = null;
  private boolean useKMC = false;

  public UserCertDownload()
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
      certStatusCheck();
      CTMLCheck();
      X509Cert localX509Cert = null;
      if (this.attributes == null)
        localX509Cert = CertDownloadOpt.downloadCert(this.certInfo);
      else
        localX509Cert = CertDownloadOpt.downloadCert(this.certInfo, this.attributes);
      updateAdminRole();
      Object localObject1;
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
      Object localObject2;
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
          localObject2 = this.response;
          jsr 136;
        }
      }
      if ((localException2 instanceof IDAException))
      {
        IDAException localIDAException2 = (IDAException)localException2;
        this.response.setErr("8002" + localIDAException2.getErrCode());
        this.response.setMsg("证书下载服务 " + localIDAException2.getErrDesc());
        this.response.appendDetail(((IDAException)localException2).getHistory());
      }
      else
      {
        this.response.setErr("80020706");
        this.response.setMsg("证书下载服务 其他错误 系统错误");
        this.response.appendDetail(localException2.getMessage());
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
    label1056: break label1056;
  }

  protected void certStatusCheck()
    throws CertException
  {
    this.debugLogger.appendMsg_L2("certStatus=" + this.certStatus);
    if ((this.certStatus == null) || (!this.certStatus.equals("Undown")))
    {
      this.debugLogger.appendMsg_L1("certStatus=" + this.certStatus);
      throw new CertException("0401", "证书状态检查 证书状态非法", new Exception("证书已被下载使用,无法完成下载操作"));
    }
  }

  protected void validateCheck()
    throws CertException
  {
    CertDownloadRequest localCertDownloadRequest = new CertDownloadRequest(this.request);
    this.refCode = localCertDownloadRequest.getRefCode();
    this.authCode = localCertDownloadRequest.getAuthCode();
    this.p10 = localCertDownloadRequest.getP10();
    this.tempPubKey = localCertDownloadRequest.getTempPubKey();
    this.debugLogger.appendMsg_L2("refCode=" + this.refCode);
    this.debugLogger.appendMsg_L2("authCode=" + this.authCode);
    this.debugLogger.appendMsg_L2("p10request=" + this.p10);
    this.debugLogger.appendMsg_L2("tempPubKey=" + this.tempPubKey);
    this.operation.setObjCertSN(this.refCode);
    if ((this.refCode == null) || (this.refCode.trim().equals("")))
    {
      this.debugLogger.appendMsg_L1("refCode=" + this.refCode);
      throw new CertException("0214", "数据有效性检查 参考号为空");
    }
    CAConfig localCAConfig = null;
    try
    {
      localCAConfig = CAConfig.getInstance();
    }
    catch (IDAException localIDAException1)
    {
      throw new CertException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1.getHistory());
    }
    if (this.refCode.length() > 40)
    {
      this.debugLogger.appendMsg_L1("refCode Length=" + this.refCode.length());
      throw new CertException("0215", "数据有效性检查 参考号位数超长");
    }
    if ((this.authCode == null) || (this.authCode.trim().equals("")))
    {
      this.debugLogger.appendMsg_L1("authCode=" + this.authCode);
      throw new CertException("0216", "数据有效性检查 授权码必须非空");
    }
    if (this.authCode.length() > 40)
    {
      this.debugLogger.appendMsg_L1("authCode Length=" + this.authCode.length());
      throw new CertException("02", "数据有效性检查  数据有效性检查 授权码位数超长");
    }
    try
    {
      this.certInfo = CertQueryOpt.queryCertInfo(this.refCode);
    }
    catch (IDAException localIDAException2)
    {
      this.debugLogger.appendMsg_L1("refCode=" + this.authCode);
      throw new CertException(localIDAException2.getErrCode(), localIDAException2.getErrDesc(), localIDAException2.getHistory());
    }
    if (this.certInfo == null)
      throw new CertException("0229", "数据有效性检查 证书不存在");
    if (!this.certInfo.getAuthCode().equals(this.authCode))
    {
      this.debugLogger.appendMsg_L1("authCode=" + this.authCode);
      throw new CertException("0223", "数据有效性检查 授权码不正确");
    }
    String str = Long.toString(this.certInfo.getCreateTime());
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    long l1 = 0L;
    try
    {
      Date localDate1 = localSimpleDateFormat.parse(str);
      l1 = localDate1.getTime();
    }
    catch (ParseException localParseException)
    {
      throw new CertException("0227", "数据有效性检查 该授权码已超出使用有效期", localParseException);
    }
    int i = localCAConfig.getAuthCodeValidity();
    long l2 = l1 + i * 24L * 60L * 60L * 1000L;
    Date localDate2 = new Date(l2);
    Date localDate3 = new Date();
    if (localDate3.compareTo(localDate2) > 0)
    {
      this.debugLogger.appendMsg_L1("authCode create time=" + str);
      throw new CertException("0227", "数据有效性检查 该授权码已超出使用有效期");
    }
    this.certStatus = this.certInfo.getCertStatus();
    this.certSN = this.refCode;
    this.certDN = this.certInfo.getSubject();
    this.ctmlName = this.certInfo.getCtmlName();
    this.lnotBefore = this.certInfo.getNotBefore();
    this.lnotAfter = this.certInfo.getNotAfter();
    this.ivalidity = this.certInfo.getValidity();
    this.operation.setObjSubject(this.certDN);
    this.operation.setObjCTMLName(this.ctmlName);
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
    if (str1.equals("REVOKED"))
    {
      this.debugLogger.appendMsg_L1("ctmlStatus=" + str1);
      throw new CertException("05", "模板策略检查 ");
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
    X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
    String str2 = localX509V3CTML.getKeyGenPlace();
    JKey localJKey = null;
    Object localObject2;
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
      int i = 0;
      localObject2 = "";
      try
      {
        PKCS10 localPKCS10 = new PKCS10(localSession);
        localPKCS10.load(this.p10.getBytes());
        localJKey = localPKCS10.getPubKey();
        localObject2 = localPKCS10.getSubject();
        this.attributes = localPKCS10.getAttributes();
        i = Parser.getRSAKeyLength(localJKey);
      }
      catch (PKIException localPKIException2)
      {
        throw new CertException(localPKIException2.getErrCode(), localPKIException2.getErrDesc(), localPKIException2.getHistory());
      }
      this.debugLogger.appendMsg_L2("p10 keyType=" + localJKey.getKeyType());
      this.debugLogger.appendMsg_L2("p10 keyLength=" + i);
      localObject3 = localX509V3CTML.getKeyType();
      if (localJKey.getKeyType().indexOf((String)localObject3) == -1)
      {
        this.debugLogger.appendMsg_L1("user keyType=" + localJKey.getKey());
        this.debugLogger.appendMsg_L1("ctml request keyType=" + (String)localObject3);
        throw new CertException("0506", "模板策略检查 密钥类型不合法");
      }
      if (i != localX509V3CTML.getKeyLength())
      {
        this.debugLogger.appendMsg_L1("user keyLength=" + i);
        this.debugLogger.appendMsg_L1("ctml request keyLength=" + localX509V3CTML.getKeyLength());
        throw new CertException("0507", "模板策略检查 密钥长度不合法");
      }
      try
      {
        InternalConfig localInternalConfig = InternalConfig.getInstance();
        if ((localInternalConfig.isVerifyP10DN()) && (!this.certDN.equalsIgnoreCase((String)localObject2)))
          throw new CertException("0519", "模板策略检查 P10申请书DN与待申请证书DN不符", new Exception("P10_DN=" + (String)localObject2));
      }
      catch (IDAException localIDAException2)
      {
        this.debugLogger.appendMsg_L1("cert DN=" + this.certDN);
        this.debugLogger.appendMsg_L1("p10 DN=" + (String)localObject2);
        throw new CertException(localIDAException2.getErrCode(), localIDAException2.getErrDesc(), localIDAException2.getHistory());
      }
    }
    else
    {
      Object localObject1;
      if (str2.equals("KMC"))
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
        localObject2 = new KMCConnector();
        localObject3 = ((KMCConnector)localObject2).requestKeyFromKMC((KMCCertInfo)localObject1);
        if (!((KeyApplyResponse)localObject3).getErr().equals("0"))
        {
          localObject4 = ((KeyApplyResponse)localObject3).getDetail();
          Exception localException = null;
          if ((localObject4 != null) && (localObject4.length > 0))
            if (localObject4[0].length == 1)
              localException = new Exception(localObject4[0][0]);
            else if (localObject4[0].length == 2)
              localException = new Exception(localObject4[0][0] + " " + localObject4[0][1]);
          throw new CertException("0517", "模板策略检查 向KMC申请密钥失败:" + ((KeyApplyResponse)localObject3).getMsg(), localException);
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
        String str3 = localX509V3CTML.getKeyType();
        if (str3.equals("RSA"))
          localJKey = new JKey("RSA_Public", localObject4);
      }
      else if (str2.equals("CA"))
      {
        this.returnP7B = false;
        this.useKMC = false;
        localObject1 = localX509V3CTML.getKeyType();
        int j = localX509V3CTML.getKeyLength();
        this.debugLogger.appendMsg_L2("CA Generate keyType=" + (String)localObject1);
        this.debugLogger.appendMsg_L2("CA Generate keyLength=" + j);
        localObject3 = null;
        if (((String)localObject1).equals("RSA"))
        {
          localObject3 = new Mechanism("RSA");
          try
          {
            this.keyPair = localSession.generateKeyPair((Mechanism)localObject3, j);
          }
          catch (PKIException localPKIException3)
          {
            throw new CertException(localPKIException3.getErrCode(), localPKIException3.getErrDesc(), localPKIException3.getHistory());
          }
        }
        localJKey = this.keyPair.getPublicKey();
      }
    }
    this.certInfo.setPubKey(localJKey);
  }

  private void updateAdminRole()
    throws IDAException
  {
    int i = 0;
    InternalConfig localInternalConfig = InternalConfig.getInstance();
    String str = localInternalConfig.getAdminTemplateName();
    if (this.ctmlName.equalsIgnoreCase(str))
      i = 1;
    if (i != 0)
    {
      Privilege localPrivilege = Privilege.getInstance();
      localPrivilege.refreshAdminInfo();
      TemplateAdmin localTemplateAdmin = TemplateAdmin.getInstance();
      localTemplateAdmin.refreshAdminInfo();
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.UserCertDownload
 * JD-Core Version:    0.6.0
 */