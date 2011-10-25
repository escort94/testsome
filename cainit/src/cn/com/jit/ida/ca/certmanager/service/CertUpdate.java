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
import cn.com.jit.ida.ca.certmanager.service.operation.CertQueryOpt;
import cn.com.jit.ida.ca.certmanager.service.operation.CertUPDDownOpt;
import cn.com.jit.ida.ca.certmanager.service.operation.CertUpdateOpt;
import cn.com.jit.ida.ca.certmanager.service.request.CertUPDRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CertUpdateResponse;
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
import cn.com.jit.ida.util.pki.cert.X509Cert;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class CertUpdate extends CertService
{
  private CertInfo certInfo = new CertInfo();
  private CertRevokeInfo certRevokeInfo = new CertRevokeInfo();
  private String[] codes = null;
  private String certSN = null;
  private String certDN = null;
  private String ctmlName = null;
  private String notBefore = null;
  private long lnotBefore;
  private String validity = null;
  private int ivalidity;
  private long lnotAfter;
  private String certStatus = null;
  private boolean isRetainKey = false;
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.ca.certmanager.service.CertUpdate");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private Operation operation = null;
  private CertUpdateResponse response = new CertUpdateResponse();
  private Request request;
  private String pendingTime = null;
  private Properties proExtensions = null;
  private Hashtable standardExtensions = null;
  private String isWaiting = null;

  public CertUpdate()
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
      parseExtensions();
      policyCheck();
      certStatusCheck();
      CTMLCheck();
      this.certRevokeInfo.setCertSN(this.certSN);
      this.certRevokeInfo.setReasonID(4);
      this.certRevokeInfo.setCDPID(this.certInfo.getCdpid());
      this.certRevokeInfo.setApplicant(this.operator.getOperatorDN());
      this.certInfo.setNotBefore(this.lnotBefore);
      this.certInfo.setValidity(this.ivalidity);
      this.certInfo.setNotAfter(this.lnotAfter);
      this.certInfo.setCertStatus("Undown");
      this.certInfo.setApplicant(this.operator.getOperatorDN());
      if (this.isRetainKey)
        this.certInfo.setIsRetainKey(this.certSN);
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
        localCertPendingRevokeInfo.setCertSN(this.certSN);
        localCertPendingRevokeInfo.setCDPID(this.certInfo.getCdpid());
        localCertPendingRevokeInfo.setOptType("CERTREVOKE");
        localCertPendingRevokeInfo.setExectime(l4);
        localCertPendingRevokeInfo.setReasonID(4);
        localCertPendingRevokeInfo.setSubject(this.certDN);
        if (this.lnotAfter <= l4)
        {
          localCertPendingRevokeInfo.setExectime(this.lnotAfter);
          CertUpdateOpt.saveCertPendingTask(localCertPendingRevokeInfo);
          CertUPDDownOpt.modifyCertPendingStatus(this.certSN);
          this.codes = isPending(str1);
        }
        if (this.lnotAfter > l4)
        {
          CertUpdateOpt.saveCertPendingTask(localCertPendingRevokeInfo);
          CertUpdateOpt.modifyCertPendingStatus(this.certSN);
          this.codes = isPending(str1);
        }
      }
      else
      {
        this.codes = noPending(str1);
      }
      this.debugLogger.appendMsg_L2("refCode=" + this.codes[0]);
      this.debugLogger.appendMsg_L2("authCode=" + this.codes[1]);
      this.operation.setResult(1);
      this.optLogger.info(this.operation);
      this.response.setRefcode(this.codes[0]);
      this.response.setRefcode(this.codes[0]);
      this.response.setAuthcode(this.codes[1]);
      this.response.setErr("0");
      this.response.setMsg("success");
    }
    catch (Exception localException2)
    {
      this.debugLogger.appendMsg_L1("Exception:" + localException2.toString());
      if (this.operation != null)
      {
        this.operation.setResult(0);
        CertUpdateResponse localCertUpdateResponse;
        try
        {
          this.optLogger.info(this.operation);
        }
        catch (IDAException localIDAException1)
        {
          this.response.setErr("8009" + localIDAException1.getErrCode());
          this.response.setMsg("证书更新服务 " + localIDAException1.getErrDesc());
          this.response.appendDetail(localIDAException1.getHistory());
          this.debugLogger.doLog();
          localCertUpdateResponse = this.response;
          jsr 130;
        }
      }
      if ((localException2 instanceof IDAException))
      {
        IDAException localIDAException2 = (IDAException)localException2;
        this.response.setErr("8009" + localIDAException2.getErrCode());
        this.response.setMsg("证书更新服务 " + localIDAException2.getErrDesc());
        this.response.appendDetail(localIDAException2.getHistory());
      }
      else
      {
        this.response.setErr("80090706");
        this.response.setMsg("证书更新服务 其他错误 系统错误");
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
    label1188: break label1188;
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
    CertUPDRequest localCertUPDRequest = new CertUPDRequest(this.request);
    this.certSN = localCertUPDRequest.getCertSN();
    this.certDN = localCertUPDRequest.getCertDN();
    this.ctmlName = localCertUPDRequest.getCtmlName();
    this.notBefore = localCertUPDRequest.getNotBefore();
    this.validity = localCertUPDRequest.getValidity();
    this.isRetainKey = localCertUPDRequest.getIsRetainKey();
    this.debugLogger.appendMsg_L2("certSN=" + this.certSN);
    this.debugLogger.appendMsg_L2("certDN=" + this.certDN);
    this.debugLogger.appendMsg_L2("ctmlName=" + this.ctmlName);
    this.debugLogger.appendMsg_L2("notBefore=" + this.notBefore);
    this.debugLogger.appendMsg_L2("validity=" + this.validity);
    this.debugLogger.appendMsg_L2("isRetainKey=" + this.isRetainKey);
    if ((this.certSN == null) || (this.certSN.trim().equals("")))
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
        this.certSN = this.certInfo.getCertSN();
      }
    }
    else
    {
      try
      {
        this.operation.setObjCertSN(this.certSN);
        this.certInfo = CertQueryOpt.queryCertInfo(this.certSN);
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
    if (localCertUPDRequest.getIfCheckValidate())
      notBeforeCheck();
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
    catch (Exception localException2)
    {
      this.debugLogger.appendMsg_L1("validity=" + this.validity);
      throw new CertException("0211", "数据有效性检查 有效期格式不合法", localException2);
    }
    if (this.ivalidity <= 0)
    {
      this.debugLogger.appendMsg_L1("validity value=" + this.ivalidity);
      throw new CertException("0212", "数据有效性检查 ");
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
    X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
    long l1 = localX509V3CTML.getNotAfter();
    this.pendingTime = localX509V3CTML.getUpdTransPeriod();
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
    int i = localX509V3CTML.getMaxValidty();
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
    Object localObject1;
    int i1;
    Object localObject3;
    String str4;
    if ((this.standardExtensions != null) && (this.standardExtensions.size() != 0))
    {
      Enumeration localEnumeration1 = this.standardExtensions.elements();
      Vector localVector1 = new Vector();
      while (localEnumeration1.hasMoreElements())
      {
        localObject1 = (Vector)localEnumeration1.nextElement();
        localVector1.addAll((Collection)localObject1);
      }
      localObject1 = new Vector();
      int m = localVector1.size();
      i1 = 0;
      int i2 = 0;
      for (int i4 = 0; i4 < m; i4++)
      {
        localObject3 = (StandardExtension)localVector1.get(i4);
        str4 = ((StandardExtension)localObject3).getStandardValue();
        String str5 = ((StandardExtension)localObject3).getAllowNull();
        String str6 = ((StandardExtension)localObject3).getParentName();
        String str7 = ((StandardExtension)localObject3).getChildName();
        if ((str6.equals("PolicyMappings")) && (str4.equals("")))
          i1 = i1 == 0 ? 1 : 0;
        if ((str6.equals("PolicyConstrants")) && (str4.equals("")))
          i2 = i2 == 0 ? 1 : 0;
        String str8 = null;
        if (str7.equalsIgnoreCase("otherName"))
        {
          str9 = ((StandardExtension)localObject3).getOtherNameOid();
          str8 = (String)CTMLConstant.standardExtAndEncodeType.get(str9 + "@" + str6);
        }
        else
        {
          str8 = (String)CTMLConstant.standardExtAndEncodeType.get(str7 + "@" + str6);
        }
        String str9 = (String)CTMLConstant.standardExtAndLable.get(str7 + "@" + str6);
        if ((str5.equalsIgnoreCase("FALSE")) && ((str4 == null) || (str4.trim().equals(""))))
        {
          this.debugLogger.appendMsg_L1("StandardExtension name=" + str9);
          this.debugLogger.appendMsg_L1("StandardExtension value=" + str4);
          throw new CertException("0518", "模板策略检查 标准扩展域内容格式不合法", new Exception("标准扩展域 [" + str9 + "] 不能为空."));
        }
        if ((str5.equalsIgnoreCase("TRUE")) && ((str4 == null) || (str4.trim().equals(""))))
          ((Vector)localObject1).add(localObject3);
        if ((str4 == null) || (str4.trim().equals("")))
          continue;
        boolean bool2 = checkExtenValue(str8, str4);
        if (bool2)
          continue;
        this.debugLogger.appendMsg_L1("StandardExtension name=" + str9);
        this.debugLogger.appendMsg_L1("StandardExtension encoding=" + str8);
        this.debugLogger.appendMsg_L1("StandardExtension value=" + str4);
        throw new CertException("0518", "模板策略检查 标准扩展域内容格式不合法", new Exception("标准扩展域 [" + str9 + "] 必须是 " + str8 + " 类型."));
      }
      if ((i1 == 1) || (i2 == 1))
        throw new CertException("0518", "模板策略检查 标准扩展域内容格式不合法", new Exception("使用策略映射或策略限制必须成对赋值"));
      this.certInfo.setStandardExtensionsHT(this.standardExtensions);
      localVector1.removeAll((Collection)localObject1);
      this.certInfo.setStandardExtensions(localVector1);
    }
    int j = localX509V3CTML.getExtensionCount();
    String str2;
    for (int k = 0; k < j; k++)
    {
      localObject1 = localX509V3CTML.getExtensionName(k);
      if (!localX509V3CTML.getUserProcessPolicy(k).equals("NEED"))
        continue;
      if ((this.proExtensions == null) || (!this.proExtensions.containsKey(localObject1)))
      {
        this.debugLogger.appendMsg_L1((String)localObject1 + " value=null");
        throw new CertException("0504", "模板策略检查 必须的扩展域信息不完整", new Exception("error occurred in extion iterm " + (String)localObject1));
      }
      str2 = (String)this.proExtensions.get(localObject1);
      if ((str2 != null) && (!str2.equals("")))
        continue;
      this.debugLogger.appendMsg_L1((String)localObject1 + " value=" + str2);
      throw new CertException("0504", "模板策略检查 必须的扩展域信息不完整", new Exception("error occurred in extion iterm " + (String)localObject1));
    }
    if (this.proExtensions != null)
    {
      Enumeration localEnumeration2 = this.proExtensions.keys();
      while (localEnumeration2.hasMoreElements())
      {
        localObject1 = (String)localEnumeration2.nextElement();
        str2 = this.proExtensions.getProperty((String)localObject1);
        if ((str2 != null) && (!str2.trim().equals("")))
          continue;
        this.proExtensions.remove(localObject1);
      }
      localObject1 = new Extension[this.proExtensions.size()];
      int n = 0;
      Object localObject2;
      for (i1 = 0; i1 < j; i1++)
      {
        String str3 = localX509V3CTML.getExtensionName(i1);
        localObject2 = localX509V3CTML.getExtensionEncoding(i1);
        if (!this.proExtensions.containsKey(str3))
          continue;
        localObject3 = localX509V3CTML.getExtensionOID(i1);
        localObject1[n] = new Extension();
        localObject1[n].setName(str3);
        localObject1[n].setOid((String)localObject3);
        str4 = this.proExtensions.getProperty(str3);
        int i5 = str4.getBytes().length;
        if (i5 > 4000)
        {
          this.debugLogger.appendMsg_L1("user self extension value length=" + i5);
          this.debugLogger.appendMsg_L1("sytem self extension max length=4000");
          throw new CertException("0512", "模板策略检查 自定义扩展域内容格式不合法", new Exception("the value of self extension is too long, max size [4000]"));
        }
        if ((str4 != null) && (!str4.trim().equals("")))
        {
          boolean bool1 = CertReqDown.checkExtValue((String)localObject2, str4);
          if (!bool1)
          {
            this.debugLogger.appendMsg_L1("extension name=" + str3);
            this.debugLogger.appendMsg_L1("extension encoding=" + (String)localObject2);
            this.debugLogger.appendMsg_L1("extension value=" + str4);
            throw new CertException("0512", "模板策略检查 自定义扩展域内容格式不合法", new Exception("self extension [" + str3 + "] must be " + (String)localObject2 + " type."));
          }
        }
        localObject1[n].setValue(this.proExtensions.getProperty(str3));
        n++;
      }
      Vector localVector2 = new Vector();
      for (int i3 = 0; i3 < localObject1.length; i3++)
      {
        if (localObject1[i3] == null)
          continue;
        localVector2.add(localObject1[i3]);
      }
      Extension[] arrayOfExtension = new Extension[localVector2.size()];
      if (arrayOfExtension.length > 0)
      {
        localVector2.toArray(arrayOfExtension);
        localObject2 = new CertExtensions(arrayOfExtension);
        this.certInfo.setCertExtensions((CertExtensions)localObject2);
      }
    }
  }

  private void parseExtensions()
    throws CertException
  {
    CertUPDRequest localCertUPDRequest = new CertUPDRequest(this.request);
    this.standardExtensions = localCertUPDRequest.getStandardExtensions();
    if (this.standardExtensions == null)
    {
      this.certInfo.setStandardExtensionsHT(null);
      this.certInfo.setStandardExtensions(null);
    }
    this.proExtensions = localCertUPDRequest.getExtensions();
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

  private void notBeforeCheck()
    throws CertException
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    Date localDate1 = null;
    CAConfig localCAConfig = null;
    try
    {
      localDate1 = localSimpleDateFormat.parse(this.notBefore);
    }
    catch (Exception localException)
    {
      this.debugLogger.appendMsg_L1("notBefore=" + this.notBefore);
      throw new CertException("0209", "数据有效性检查 有效期起始日期格式格式不合法", localException);
    }
    try
    {
      localCAConfig = CAConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
      throw new CertException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException.getHistory());
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
  }

  public String[] isPending(String paramString)
    throws IDAException
  {
    if (this.ctmlName.equals(paramString))
      this.codes = CertUpdateOpt.updatePendingCert(this.certRevokeInfo, this.certInfo, true);
    else
      this.codes = CertUpdateOpt.updatePendingCert(this.certRevokeInfo, this.certInfo, false);
    return this.codes;
  }

  public String[] noPending(String paramString)
    throws IDAException
  {
    if (this.ctmlName.equals(paramString))
      this.codes = CertUpdateOpt.updateCert(this.certRevokeInfo, this.certInfo, true);
    else
      this.codes = CertUpdateOpt.updateCert(this.certRevokeInfo, this.certInfo, false);
    return this.codes;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.CertUpdate
 * JD-Core Version:    0.6.0
 */