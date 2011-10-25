package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.CertService;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertExtensions;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.service.operation.CertUpdateQueryOpt;
import cn.com.jit.ida.ca.certmanager.service.request.CertUpdateQueryRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CertUpdateQueryResponse;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import cn.com.jit.ida.privilege.TemplateAdmin;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class CertUpdateQuery extends CertService
{
  private Request request = null;
  private DebugLogger debugLogger = null;
  private OptLogger optLogger = null;
  private CertUpdateQueryResponse response = null;
  private Operation operation = null;
  private Properties prop = null;
  private String certSN = null;
  private final String CTML_SEPERATOR = "|";
  private boolean exactQuery = false;

  public CertUpdateQuery()
  {
    try
    {
      jbInit();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public Response dealRequest(Request paramRequest)
  {
    try
    {
      if (LogManager.isDebug2())
        try
        {
          this.debugLogger.appendMsg_L2("cert updateQuery request XML:\n" + new String(paramRequest.getData()));
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
      Hashtable localHashtable = null;
      String str = null;
      CertInfo[] arrayOfCertInfo = null;
      try
      {
        CertUpdateQueryRequest localCertUpdateQueryRequest = new CertUpdateQueryRequest(this.request);
        this.prop = localCertUpdateQueryRequest.getCertInfoProperties();
        localVector = CertUpdateQueryOpt.queryCertInfo(this.prop, 1, 1, this.exactQuery);
        str = (String)localVector.get(0);
        arrayOfCertInfo = (CertInfo[])localVector.get(1);
        this.certSN = arrayOfCertInfo[0].getCertSN();
        localHashtable = CertUpdateQueryOpt.querryCertStandardExt(this.certSN);
        localObject1 = CertUpdateQueryOpt.querryCertExtensions(this.certSN);
      }
      catch (IDAException localIDAException3)
      {
        this.debugLogger.appendMsg_L1("updateQuery cert error ：" + localIDAException3.toString());
        throw localIDAException3;
      }
      this.operation.setResult(1);
      try
      {
        this.optLogger.info(this.operation);
      }
      catch (IDAException localIDAException4)
      {
        throw localIDAException4;
      }
      this.response.setErr("0");
      this.response.setMsg("success");
      this.response.setTotalRowCount(str);
      this.response.setCertInfo(arrayOfCertInfo);
      this.response.setExtensions((CertExtensions)localObject1);
      this.response.setStandardExtensions(localHashtable);
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
        catch (IDAException localIDAException1)
        {
          this.response.setErr("8007" + localIDAException1.getErrCode());
          this.response.setMsg("证书查询服务 " + localIDAException1.getErrDesc());
          this.response.appendDetail(localIDAException1.getHistory());
          this.debugLogger.doLog();
          localObject1 = this.response;
          jsr 130;
        }
      }
      if ((localException2 instanceof IDAException))
      {
        IDAException localIDAException2 = (IDAException)localException2;
        this.response.setErr("8007" + localIDAException2.getErrCode());
        this.response.setMsg("证书查询服务 " + localIDAException2.getErrDesc());
        this.response.appendDetail(localIDAException2.getHistory());
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
          this.debugLogger.appendMsg_L2("cert updateQuery response XML:\n" + new String(this.response.getData()));
      }
      catch (Exception localException3)
      {
        this.debugLogger.doLog();
        throw new RuntimeException(localException3);
      }
      this.debugLogger.doLog();
    }
    label813: break label813;
  }

  protected void validateCheck()
    throws CertException
  {
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

  private void jbInit()
    throws Exception
  {
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.CertUpdateQuery
 * JD-Core Version:    0.6.0
 */