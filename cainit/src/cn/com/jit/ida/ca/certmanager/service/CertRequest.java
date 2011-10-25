package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.CertService;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertExtensions;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.Extension;
import cn.com.jit.ida.ca.certmanager.service.operation.CertQueryOpt;
import cn.com.jit.ida.ca.certmanager.service.operation.CertReqOpt;
import cn.com.jit.ida.ca.certmanager.service.request.CertReqRequest;
import cn.com.jit.ida.ca.certmanager.service.request.ReqCheck;
import cn.com.jit.ida.ca.certmanager.service.response.CertReqResponse;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.util.CTMLConstant;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
import cn.com.jit.ida.ca.ctml.x509v3.extension.StandardExtension;
import cn.com.jit.ida.globalconfig.ConfigTool;
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

public class CertRequest extends CertService
{
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.ca.certmanager.service.CertRequest");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private CertInfo certInfo = null;
  private String errmsg = null;
  private String errcode = null;
  private Request request = null;
  private CertReqResponse response = new CertReqResponse();
  private Operation operation = null;
  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  private String strSubject = null;
  private String strCtmlName = null;
  private String strNotBefore = null;
  private long lnotBefore = 0L;
  private String strNotAfter = null;
  private long lnotAfter = 0L;
  private String strValidate = null;
  private int ivalidate = 0;
  private Properties proExtensions = null;
  private String email = null;
  private String remark = null;
  private String strapplicant = null;
  private Hashtable standardExtensions = null;

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
      this.certInfo = new CertInfo();
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
      this.strapplicant = this.operator.getOperatorDN();
      this.debugLogger.appendMsg_L2("operation=" + paramRequest.getOperation());
      this.debugLogger.appendMsg_L2("operatorSN=" + this.operator.getOperatorSN());
      this.debugLogger.appendMsg_L2("operatorDN=" + this.operator.getOperatorDN());
      parseCertInfo(this.request);
      this.operation.setObjSubject(this.strSubject);
      this.operation.setObjCTMLName(this.strCtmlName);
      this.debugLogger.appendMsg_L2("ObjDN=" + this.strSubject);
      this.debugLogger.appendMsg_L2("ObjCTMLName=" + this.strCtmlName);
      licenceCheck();
      policyCheck();
      certStatusCheck();
      validateCheck();
      CTMLCheck();
      createCertInfo();
      String[] arrayOfString = null;
      arrayOfString = CertReqOpt.requestCert(this.certInfo);
      createResponse(arrayOfString, true);
      this.operation.setResult(1);
      this.optLogger.info(this.operation);
      this.response.setErr("0");
      this.response.setMsg("success");
    }
    catch (Exception localException2)
    {
      this.debugLogger.appendMsg_L1("Exception:" + localException2.toString());
      if (this.operation != null)
      {
        this.operation.setResult(0);
        CertReqResponse localCertReqResponse;
        try
        {
          this.optLogger.info(this.operation);
        }
        catch (IDAException localIDAException1)
        {
          this.response.setErr("8001" + localIDAException1.getErrCode());
          this.response.setMsg("证书申请服务 " + localIDAException1.getErrDesc());
          this.response.appendDetail(localIDAException1.getHistory());
          this.debugLogger.doLog();
          localCertReqResponse = this.response;
          jsr 130;
        }
      }
      if ((localException2 instanceof IDAException))
      {
        IDAException localIDAException2 = (IDAException)localException2;
        this.response.setErr("8001" + localIDAException2.getErrCode());
        this.response.setMsg("证书申请服务 " + localIDAException2.getErrDesc());
        this.response.appendDetail(localIDAException2.getHistory());
      }
      else
      {
        this.response.setErr("80010706");
        this.response.setMsg("证书申请服务 其他错误 系统错误");
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
    label835: break label835;
  }

  protected void createResponse(String[] paramArrayOfString, boolean paramBoolean)
  {
    CertReqResponse localCertReqResponse;
    if (paramBoolean)
    {
      localCertReqResponse = new CertReqResponse();
      localCertReqResponse.setRefcode(paramArrayOfString[0]);
      localCertReqResponse.setAuthcode(paramArrayOfString[1]);
      this.response = localCertReqResponse;
    }
    else
    {
      localCertReqResponse = new CertReqResponse();
      this.response = localCertReqResponse;
      this.response.setOperation("Cert Request");
      this.response.setErr(this.errcode);
      this.response.setMsg(this.errmsg);
    }
  }

  protected void createCertInfo()
  {
    this.certInfo.setSubject(ConfigTool.formatDN(this.strSubject));
    this.certInfo.setNotBefore(this.lnotBefore);
    this.certInfo.setValidity(this.ivalidate);
    this.certInfo.setNotAfter(this.lnotAfter);
    this.certInfo.setApplicant(this.strapplicant);
    this.certInfo.setCtmlName(this.strCtmlName);
    this.certInfo.setEmail(this.email);
    this.certInfo.setRemark(this.remark);
  }

  protected void parseCertInfo(Request paramRequest)
    throws CertException
  {
    CertReqRequest localCertReqRequest = new CertReqRequest(paramRequest);
    localCertReqRequest.updateBody(false);
    this.strSubject = localCertReqRequest.getCertDN();
    this.strCtmlName = localCertReqRequest.getCtmlName();
    this.strNotBefore = localCertReqRequest.getNotBefore();
    this.strValidate = localCertReqRequest.getValidate();
    this.email = localCertReqRequest.getEmail();
    this.remark = localCertReqRequest.getRemark();
    this.standardExtensions = localCertReqRequest.getStandardExtensions();
    this.proExtensions = localCertReqRequest.getExtensions();
  }

  protected void validateCheck()
    throws CertException
  {
    this.debugLogger.appendMsg_L2("certDN=" + this.strSubject);
    this.debugLogger.appendMsg_L2("ctmlName=" + this.strCtmlName);
    this.debugLogger.appendMsg_L2("notBefore=" + this.strNotBefore);
    this.debugLogger.appendMsg_L2("Validate=" + this.strValidate);
    this.debugLogger.appendMsg_L2("email=" + this.email);
    this.debugLogger.appendMsg_L2("remark=" + this.remark);
    if ((this.strSubject == null) || (this.strSubject.equals("")))
    {
      this.debugLogger.appendMsg_L1("Subject=" + this.strSubject);
      throw new CertException("0202", "数据有效性检查 证书主题为空");
    }
    byte[] arrayOfByte = this.strSubject.getBytes();
    if (arrayOfByte.length > 255)
    {
      this.debugLogger.appendMsg_L1("certDN Length=" + arrayOfByte.length);
      throw new CertException("0203", "数据有效性检查 证书主题长度过长");
    }
    if (!ReqCheck.checkDN(this.strSubject))
    {
      this.debugLogger.appendMsg_L1("Subject=" + this.strSubject);
      throw new CertException("0204", "数据有效性检查 证书主题格式不合法");
    }
    this.strSubject = ReqCheck.filterDN(this.strSubject);
    if ((this.strCtmlName == null) || (this.strCtmlName.equals("")))
    {
      this.debugLogger.appendMsg_L1("CtmlName=" + this.strCtmlName);
      throw new CertException("0205", "数据有效性检查 证书模板为空");
    }
    if ((this.strNotBefore == null) || (this.strNotBefore.equals("")))
    {
      this.debugLogger.appendMsg_L1("NotBefore=" + this.strNotBefore);
      throw new CertException("0206", "数据有效性检查 有效期起始日期为空");
    }
    if (this.strNotBefore.length() != 17)
    {
      this.debugLogger.appendMsg_L1("NotBefore=" + this.strNotBefore);
      throw new CertException("0207", "数据有效性检查 有效期起始日期长度错误");
    }
    try
    {
      this.lnotBefore = Long.parseLong(this.strNotBefore);
    }
    catch (Exception localException1)
    {
      this.debugLogger.appendMsg_L1("notBefore=" + this.strNotBefore);
      throw new CertException("0209", "数据有效性检查 有效期起始日期格式格式不合法", localException1);
    }
    try
    {
      this.sdf.parse(this.strNotBefore);
    }
    catch (Exception localException2)
    {
      this.debugLogger.appendMsg_L1("notBefore=" + this.strNotBefore);
      throw new CertException("0209", "数据有效性检查 有效期起始日期格式格式不合法", localException2);
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
    long l1 = localCAConfig.getTimeDifAllow();
    Date localDate = new Date();
    long l2 = 0L;
    long l3 = 0L;
    try
    {
      l2 = localDate.getTime();
      l3 = this.sdf.parse(this.strNotBefore).getTime();
    }
    catch (Exception localException4)
    {
      this.debugLogger.appendMsg_L1("notBefore=" + this.strNotBefore);
      throw new CertException("0209", "数据有效性检查 有效期起始日期格式格式不合法", localException4);
    }
    if (l2 > l1 + l3)
    {
      this.debugLogger.appendMsg_L1("nowtime=" + l2);
      this.debugLogger.appendMsg_L1("starttime=" + l3);
      this.debugLogger.appendMsg_L1("deftime=" + l1);
      throw new CertException("0208", "数据有效性检查 有效期起始日期超出服务允许范围");
    }
    if ((this.strValidate == null) || (this.strValidate.equals("")))
    {
      this.debugLogger.appendMsg_L1("Validate=" + this.strValidate);
      throw new CertException("0210", "0210");
    }
    try
    {
      this.ivalidate = Integer.parseInt(this.strValidate);
    }
    catch (Exception localException3)
    {
      this.debugLogger.appendMsg_L1("validity=" + this.strValidate);
      throw new CertException("0211", "数据有效性检查 有效期格式不合法", localException3);
    }
    if (this.ivalidate <= 0)
    {
      this.debugLogger.appendMsg_L1("Validate=" + this.strValidate);
      throw new CertException("0212", "数据有效性检查 有效天数小于0");
    }
    try
    {
      CTMLManager localCTMLManager = CTMLManager.getInstance();
      X509V3CTML localX509V3CTML = (X509V3CTML)localCTMLManager.getCTML(this.strCtmlName);
      int i = localX509V3CTML.getMaxValidty();
      int j = Integer.parseInt(this.strValidate);
      if (j > i)
      {
        this.debugLogger.appendMsg_L1("Validate=" + this.strValidate);
        this.debugLogger.appendMsg_L1("ctmlvalidate=" + i);
        throw new CertException("0502", "模板策略检查 超出证书模板允许有效期的最大效期");
      }
    }
    catch (IDAException localIDAException1)
    {
      throw new CertException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1);
    }
    if ((this.email != null) && (this.email.length() > 100))
    {
      this.debugLogger.appendMsg_L1("email length=" + this.email.length());
      throw new CertException("0220", "数据有效性检查 e-mail内容超长");
    }
    if ((this.strapplicant == null) || (this.strapplicant.equals("")))
    {
      this.debugLogger.appendMsg_L1("strapplicant=" + this.strapplicant);
      throw new CertException("0702", "其他错误 操作员DN为空");
    }
  }

  protected void certStatusCheck()
    throws CertException
  {
    int i = 0;
    try
    {
      i = CertQueryOpt.checkCertReq(this.strSubject, this.strCtmlName);
      if (i > 0)
      {
        this.debugLogger.appendMsg_L1("certnum = " + i);
        throw new CertException("0401", "证书状态检查 证书状态非法", new Exception("此证书已存在并处于使用状态，请重新确认申请信息"));
      }
    }
    catch (IDAException localIDAException)
    {
      this.debugLogger.appendMsg_L1("certnum = " + i);
      throw new CertException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException.getHistory());
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
    catch (Exception localException)
    {
      throw new CertException("03", "操作员权限检查 ", localException);
    }
    boolean bool = localTemplateAdmin.isPass(this.operator.getOperatorSN(), this.strCtmlName, this.strSubject, null);
    if (!bool)
    {
      this.debugLogger.appendMsg_L1("operator=" + this.operator.getOperatorDN());
      this.debugLogger.appendMsg_L1("ctmlName=" + this.strCtmlName);
      this.debugLogger.appendMsg_L1("certDN=" + this.strSubject);
      throw new CertException("0301", "操作员权限检查 操作员业务权限检查失败", new Exception("操作员不具备此操作权限，请检查操作员是否有操作此模板的权限，并且确保证书DN符合管理员权限范围"));
    }
  }

  protected void CTMLCheck()
    throws CertException
  {
    CTMLManager localCTMLManager = null;
    CTML localCTML = null;
    localCTMLManager = CTMLManager.getInstance();
    try
    {
      localCTML = localCTMLManager.getCTML(this.strCtmlName);
      if (localCTML.getCTMLStatus().equals("REVOKED"))
      {
        this.debugLogger.appendMsg_L1("ctmlstatus=" + localCTMLManager.getCTML(this.strCtmlName).getCTMLStatus());
        throw new CertException("0501", "模板策略检查 证书模板状态无效");
      }
    }
    catch (IDAException localIDAException1)
    {
      throw new CertException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1);
    }
    X509V3CTML localX509V3CTML = (X509V3CTML)localCTML;
    Date localDate1 = null;
    Date localDate2 = null;
    try
    {
      localDate1 = this.sdf.parse(Long.toString(localX509V3CTML.getNotAfter()));
      localDate2 = getreferencedate(this.sdf.parse(this.strNotBefore), this.ivalidate);
    }
    catch (Exception localException)
    {
      throw new CertException("0209", "数据有效性检查 有效期起始日期格式格式不合法", localException);
    }
    if (localDate2.after(localDate1))
    {
      this.debugLogger.appendMsg_L1("enddate=" + localDate2.toString());
      this.debugLogger.appendMsg_L1("ctmldate=" + localDate1.toString());
      throw new CertException("0503", "模板策略检查 超出模板有效期范围");
    }
    this.strNotAfter = this.sdf.format(localDate2);
    this.lnotAfter = Long.parseLong(this.strNotAfter);
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
    Date localDate3 = localX509Cert.getNotAfter();
    if (localDate2.compareTo(localDate3) > 0)
    {
      this.debugLogger.appendMsg_L1("certNotAfter Date=" + localDate2.toString());
      this.debugLogger.appendMsg_L1("rootCertNotAfter Date=" + localDate3.toString());
      throw new CertException("0224", "数据有效性检查 证书终止有效期超过CA签名证书有效期");
    }
    Object localObject1;
    int n;
    Object localObject3;
    String str3;
    if (this.standardExtensions != null)
    {
      Enumeration localEnumeration1 = this.standardExtensions.elements();
      Vector localVector1 = new Vector();
      while (localEnumeration1.hasMoreElements())
      {
        localObject1 = (Vector)localEnumeration1.nextElement();
        localVector1.addAll((Collection)localObject1);
      }
      localObject1 = new Vector();
      int k = localVector1.size();
      n = 0;
      int i1 = 0;
      for (int i3 = 0; i3 < k; i3++)
      {
        localObject3 = (StandardExtension)localVector1.get(i3);
        str3 = ((StandardExtension)localObject3).getStandardValue();
        String str4 = ((StandardExtension)localObject3).getAllowNull();
        String str5 = ((StandardExtension)localObject3).getParentName();
        String str6 = ((StandardExtension)localObject3).getChildName();
        if ((str5.equals("PolicyMappings")) && (str3.equals("")))
          n = n == 0 ? 1 : 0;
        if ((str5.equals("PolicyConstrants")) && (str3.equals("")))
          i1 = i1 == 0 ? 1 : 0;
        String str7 = null;
        if (str6.equalsIgnoreCase("otherName"))
        {
          str8 = ((StandardExtension)localObject3).getOtherNameOid();
          str7 = (String)CTMLConstant.standardExtAndEncodeType.get(str8 + "@" + str5);
        }
        else
        {
          str7 = (String)CTMLConstant.standardExtAndEncodeType.get(str6 + "@" + str5);
        }
        String str8 = (String)CTMLConstant.standardExtAndLable.get(str6 + "@" + str5);
        if ((str4.equalsIgnoreCase("FALSE")) && ((str3 == null) || (str3.trim().equals(""))))
        {
          this.debugLogger.appendMsg_L1("StandardExtension name=" + str8);
          this.debugLogger.appendMsg_L1("StandardExtension value=" + str3);
          throw new CertException("0518", "模板策略检查 标准扩展域内容格式不合法", new Exception("标准扩展域 [" + str8 + "] 不能为空."));
        }
        if ((str4.equalsIgnoreCase("TRUE")) && ((str3 == null) || (str3.trim().equals(""))))
          ((Vector)localObject1).add(localObject3);
        if ((str3 == null) || (str3.trim().equals("")))
          continue;
        boolean bool2 = checkExtenValue(str7, str3);
        if (bool2)
          continue;
        this.debugLogger.appendMsg_L1("StandardExtension name=" + str8);
        this.debugLogger.appendMsg_L1("StandardExtension encoding=" + str7);
        this.debugLogger.appendMsg_L1("StandardExtension value=" + str3);
        throw new CertException("0518", "模板策略检查 标准扩展域内容格式不合法", new Exception("标准扩展域 [" + str8 + "] 必须是 " + str7 + " 类型."));
      }
      if ((n == 1) || (i1 == 1))
        throw new CertException("0518", "模板策略检查 标准扩展域内容格式不合法", new Exception("使用策略映射或策略限制必须成对赋值"));
      this.certInfo.setStandardExtensionsHT(this.standardExtensions);
      localVector1.removeAll((Collection)localObject1);
      this.certInfo.setStandardExtensions(localVector1);
    }
    int i = localX509V3CTML.getExtensionCount();
    String str1;
    for (int j = 0; j < i; j++)
    {
      localObject1 = localX509V3CTML.getExtensionName(j);
      if (!localX509V3CTML.getUserProcessPolicy(j).equals("NEED"))
        continue;
      if ((this.proExtensions == null) || (!this.proExtensions.containsKey(localObject1)))
      {
        this.debugLogger.appendMsg_L1((String)localObject1 + " value=null");
        throw new CertException("0504", "模板策略检查 必须的扩展域信息不完整", new Exception("error occurred in extion iterm " + (String)localObject1));
      }
      str1 = (String)this.proExtensions.get(localObject1);
      if ((str1 != null) && (!str1.equals("")))
        continue;
      this.debugLogger.appendMsg_L1((String)localObject1 + " value=" + str1);
      throw new CertException("0504", "模板策略检查 必须的扩展域信息不完整", new Exception("error occurred in extion iterm " + (String)localObject1));
    }
    if (this.proExtensions != null)
    {
      Enumeration localEnumeration2 = this.proExtensions.keys();
      while (localEnumeration2.hasMoreElements())
      {
        localObject1 = (String)localEnumeration2.nextElement();
        str1 = this.proExtensions.getProperty((String)localObject1);
        if ((str1 != null) && (!str1.trim().equals("")))
          continue;
        this.proExtensions.remove(localObject1);
      }
      localObject1 = new Extension[this.proExtensions.size()];
      int m = 0;
      Object localObject2;
      for (n = 0; n < i; n++)
      {
        String str2 = localX509V3CTML.getExtensionName(n);
        localObject2 = localX509V3CTML.getExtensionEncoding(n);
        if (!this.proExtensions.containsKey(str2))
          continue;
        localObject3 = localX509V3CTML.getExtensionOID(n);
        localObject1[m] = new Extension();
        localObject1[m].setName(str2);
        localObject1[m].setOid((String)localObject3);
        str3 = this.proExtensions.getProperty(str2);
        int i4 = str3.getBytes().length;
        if (i4 > 4000)
        {
          this.debugLogger.appendMsg_L1("user self extension value length=" + i4);
          this.debugLogger.appendMsg_L1("sytem self extension max length=4000");
          throw new CertException("0512", "模板策略检查 自定义扩展域内容格式不合法", new Exception("the value of self extension is too long, max size [4000]"));
        }
        if ((str3 != null) && (!str3.trim().equals("")))
        {
          boolean bool1 = CertReqDown.checkExtValue((String)localObject2, str3);
          if (!bool1)
          {
            this.debugLogger.appendMsg_L1("extension name=" + str2);
            this.debugLogger.appendMsg_L1("extension encoding=" + (String)localObject2);
            this.debugLogger.appendMsg_L1("extension value=" + str3);
            throw new CertException("0512", "模板策略检查 自定义扩展域内容格式不合法", new Exception("self extension [" + str2 + "] must be " + (String)localObject2 + " type."));
          }
        }
        localObject1[m].setValue(this.proExtensions.getProperty(str2));
        m++;
      }
      Vector localVector2 = new Vector();
      for (int i2 = 0; i2 < localObject1.length; i2++)
      {
        if (localObject1[i2] == null)
          continue;
        localVector2.add(localObject1[i2]);
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

  private Date getreferencedate(Date paramDate, int paramInt)
  {
    long l1 = 0L;
    long l2 = 0L;
    Date localDate = null;
    if (paramDate != null)
    {
      l1 = paramDate.getTime();
      l2 = l1 + paramInt * 24L * 60L * 60L * 1000L;
      localDate = new Date(l2);
    }
    return localDate;
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
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.CertRequest
 * JD-Core Version:    0.6.0
 */