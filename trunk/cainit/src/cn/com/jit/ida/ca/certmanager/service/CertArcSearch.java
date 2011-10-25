package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.CertService;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.service.operation.CertArcSearchOpt;
import cn.com.jit.ida.ca.certmanager.service.request.CertArcSearchRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CertArcSearchResponse;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import cn.com.jit.ida.privilege.TemplateAdmin;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class CertArcSearch extends CertService
{
  private Request request = null;
  private DebugLogger debugLogger = null;
  private OptLogger optLogger = null;
  private CertArcSearchResponse response = null;
  private Operation operation = null;
  private Properties prop = null;
  private String certSN = null;
  private boolean isCheck = false;
  private int fromIndex = -1;
  private int rowCount = -1;
  private final String CTML_SEPERATOR = "|";
  private boolean exactQuery = false;

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      if (LogManager.isDebug2())
        try
        {
          this.debugLogger.appendMsg_L2("archive cert query request XML:\n" + new String(paramRequest.getData()));
        }
        catch (Exception localException1)
        {
          throw new CertException("0704", "其他错误 请求信息格式不合法");
        }
      this.request = paramRequest;
      this.operator = paramRequest.getOperator();
      if (this.operator == null)
        throw new CertException("0701", "其他错误 操作员为空");
      this.operation = new Operation();
      this.operation.setOptType(paramRequest.getOperation());
      this.operation.setOperatorSN(this.operator.getOperatorSN());
      this.operation.setOperatorDN(this.operator.getOperatorDN());
      this.debugLogger.appendMsg_L2("operation =" + paramRequest.getOperation());
      this.debugLogger.appendMsg_L2("operator SN =" + this.operator.getOperatorSN());
      this.debugLogger.appendMsg_L2("operator DN=" + this.operator.getOperatorDN());
      validateCheck();
      Vector localVector = null;
      try
      {
        localVector = CertArcSearchOpt.queryCertInfo(this.prop, this.fromIndex, this.rowCount, this.exactQuery);
      }
      catch (IDAException localIDAException1)
      {
        this.debugLogger.appendMsg_L1("query cert error ：" + localIDAException1.toString());
        throw localIDAException1;
      }
      this.operation.setResult(1);
      try
      {
        this.optLogger.info(this.operation);
      }
      catch (IDAException localIDAException2)
      {
        throw localIDAException2;
      }
      this.response.setErr("0");
      this.response.setMsg("success");
      String str = (String)localVector.get(0);
      this.response.setTotalRowCount(str);
      localObject1 = (CertInfo[])localVector.get(1);
      this.response.setCertInfo(localObject1);
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
        catch (IDAException localIDAException3)
        {
          this.response.setErr("8007" + localIDAException3.getErrCode());
          this.response.setMsg("证书查询服务 " + localIDAException3.getErrDesc());
          this.response.appendDetail(localIDAException3.getHistory());
          this.debugLogger.doLog();
          localObject1 = this.response;
          jsr 130;
        }
      }
      if ((localException2 instanceof IDAException))
      {
        IDAException localIDAException4 = (IDAException)localException2;
        this.response.setErr("8007" + localIDAException4.getErrCode());
        this.response.setMsg("证书查询服务 " + localIDAException4.getErrDesc());
        this.response.appendDetail(localIDAException4.getHistory());
      }
      else
      {
        this.response.setErr("80070706");
        this.response.setMsg("证书查询服务 其他错误 系统错误");
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
    label737: break label737;
  }

  protected void validateCheck()
    throws CertException
  {
    CertArcSearchRequest localCertArcSearchRequest = null;
    try
    {
      localCertArcSearchRequest = new CertArcSearchRequest(this.request);
    }
    catch (CertException localCertException)
    {
      throw new CertException("0704", "其他错误 请求信息格式不合法");
    }
    String str1 = localCertArcSearchRequest.getSubject();
    if (str1 != null)
      this.operation.setObjSubject(str1);
    String str2 = localCertArcSearchRequest.getCertSN();
    if (str2 != null)
      this.operation.setObjCertSN(str2);
    String str3 = localCertArcSearchRequest.getCtmlName();
    if (str3 != null)
      if (str3.indexOf("|") != -1)
        this.operation.setObjCTMLName("权限内所有模板");
      else
        this.operation.setObjCTMLName(localCertArcSearchRequest.getCtmlName());
    this.prop = localCertArcSearchRequest.getCertInfoProperties();
    String str4 = this.prop.getProperty("subject");
    if (str4 != null)
      this.prop.setProperty("subject", str4);
    String str5 = localCertArcSearchRequest.getExactQuery();
    if (str5.equalsIgnoreCase("true"))
      this.exactQuery = true;
    if ((localCertArcSearchRequest.getFromIndex() == null) || (localCertArcSearchRequest.getFromIndex().equals("")))
      throw new CertException("0232", "数据有效性检查 查询证书信息，查询起始位置为空");
    this.fromIndex = Integer.parseInt(localCertArcSearchRequest.getFromIndex());
    this.debugLogger.appendMsg_L2("query cert from index = " + localCertArcSearchRequest.getFromIndex());
    if ((localCertArcSearchRequest.getRowCount() == null) || (localCertArcSearchRequest.getRowCount().equals("")))
      throw new CertException("0233", "数据有效性检查 查询证书信息，查询数量为空");
    int i = Integer.parseInt(localCertArcSearchRequest.getRowCount());
    int j = 1000;
    CAConfig localCAConfig = null;
    try
    {
      localCAConfig = CAConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
      throw new CertException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException.getHistory());
    }
    j = localCAConfig.getMaxCountPerPage();
    if (i > j)
      this.rowCount = j;
    else
      this.rowCount = i;
    this.debugLogger.appendMsg_L2("query cert row count = " + i);
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
    String str1 = this.prop.getProperty("ctmlName");
    if (str1 == null)
      return;
    Vector localVector = new Vector();
    getClass();
    if (str1.indexOf("|") == -1)
    {
      localVector.add(str1);
    }
    else
    {
      localObject = new StringTokenizer(str1, "|");
      while (((StringTokenizer)localObject).hasMoreTokens())
        localVector.add(((StringTokenizer)localObject).nextToken());
    }
    Object localObject = localTemplateAdmin.selectLegalCtml(this.operator.getOperatorSN(), localVector);
    if (((Vector)localObject).size() == 0)
      throw new CertException("0303", "操作员权限检查 操作员没有待操作模板的查询权限");
    String str2 = "";
    for (int i = 0; i < ((Vector)localObject).size() - 1; i++)
      str2 = str2 + (String)((Vector)localObject).get(i) + "|";
    str2 = str2 + (String)((Vector)localObject).get(((Vector)localObject).size() - 1);
    this.prop.setProperty("ctmlName", str2);
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
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.CertArcSearch
 * JD-Core Version:    0.6.0
 */