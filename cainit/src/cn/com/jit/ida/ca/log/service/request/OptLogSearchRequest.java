package cn.com.jit.ida.ca.log.service.request;

import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.log.LogException;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OptLogSearchRequest extends Request
{
  private Properties prop = null;
  private String operatorSN = null;
  private String operatorSubject = null;
  private String objectCertSN = null;
  private String objectCertSubjcet = null;
  private String objectCTMLName = null;
  private String operationType = null;
  private String operationTimeBegin = null;
  private String operationTimeEnd = null;
  private String operationResult = null;
  private String fromIndex = null;
  private String rowCount = null;
  private String exactQuery = "false";
  private String isLog = "false";

  public OptLogSearchRequest()
  {
    super.setOperation("AUDITQUERYARCHIVELOG");
  }

  public OptLogSearchRequest(Request paramRequest)
    throws LogException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new LogException("0204", "数据有效性检查 请求信息格式不合法");
    }
  }

  public OptLogSearchRequest(byte[] paramArrayOfByte)
    throws LogException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new LogException("0204", "数据有效性检查 请求信息格式不合法");
    }
  }

  protected void updateBody(boolean paramBoolean)
    throws LogException
  {
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      Element localElement = XMLTool.newChildElement(this.body, "entry");
      if (this.operatorSN != null)
        XMLTool.newChildElement(localElement, "operatorSN", this.operatorSN.trim());
      if (this.operatorSubject != null)
        XMLTool.newChildElement(localElement, "operatorSubject", this.operatorSubject.trim());
      if (this.objectCertSN != null)
        XMLTool.newChildElement(localElement, "objectCertSN", this.objectCertSN.trim());
      if (this.objectCertSubjcet != null)
        XMLTool.newChildElement(localElement, "objectSubject", this.objectCertSubjcet.trim());
      if (this.objectCTMLName != null)
        XMLTool.newChildElement(localElement, "objectCTMLName", this.objectCTMLName.trim());
      if (this.operationType != null)
        XMLTool.newChildElement(localElement, "optType", this.operationType.trim());
      if (this.operationTimeBegin != null)
        XMLTool.newChildElement(localElement, "optTimeBegin", this.operationTimeBegin.trim());
      if (this.operationTimeEnd != null)
        XMLTool.newChildElement(localElement, "optTimeEnd", this.operationTimeEnd.trim());
      if (this.operationResult != null)
        XMLTool.newChildElement(localElement, "result", this.operationResult.trim());
      if (this.fromIndex != null)
        XMLTool.newChildElement(localElement, "fromIndex", this.fromIndex);
      if (this.rowCount != null)
        XMLTool.newChildElement(localElement, "rowCount", this.rowCount);
      XMLTool.newChildElement(localElement, "exactQuery", this.exactQuery);
      XMLTool.newChildElement(localElement, "isLog", this.isLog);
    }
    else
    {
      this.operatorSN = XMLTool.getValueByTagName(this.body, "operatorSN");
      if (this.operatorSN != null)
        this.prop.put("operatorSN", this.operatorSN.trim());
      this.operatorSubject = XMLTool.getValueByTagName(this.body, "operatorSubject");
      if (this.operatorSubject != null)
        this.prop.put("operatorSubject", this.operatorSubject.trim());
      this.objectCertSN = XMLTool.getValueByTagName(this.body, "objectCertSN");
      if (this.objectCertSN != null)
        this.prop.put("objectCertSN", this.objectCertSN.trim());
      this.objectCertSubjcet = XMLTool.getValueByTagName(this.body, "objectSubject");
      if (this.objectCertSubjcet != null)
        this.prop.put("objectSubject", this.objectCertSubjcet.trim());
      this.objectCTMLName = XMLTool.getValueByTagName(this.body, "objectCTMLName");
      if (this.objectCTMLName != null)
        this.prop.put("objectCTMLName", this.objectCTMLName.trim());
      this.operationType = XMLTool.getValueByTagName(this.body, "optType");
      if (this.operationType != null)
        this.prop.put("optType", this.operationType.trim());
      this.operationTimeBegin = XMLTool.getValueByTagName(this.body, "optTimeBegin");
      if (this.operationTimeBegin != null)
        this.prop.put("optTimeBegin", this.operationTimeBegin.trim());
      this.operationTimeEnd = XMLTool.getValueByTagName(this.body, "optTimeEnd");
      if (this.operationTimeEnd != null)
        this.prop.put("optTimeEnd", this.operationTimeEnd.trim());
      this.operationResult = XMLTool.getValueByTagName(this.body, "result");
      if (this.operationResult != null)
        this.prop.put("result", this.operationResult.trim());
      this.fromIndex = XMLTool.getValueByTagName(this.body, "fromIndex");
      this.rowCount = XMLTool.getValueByTagName(this.body, "rowCount");
      this.exactQuery = XMLTool.getValueByTagName(this.body, "exactQuery");
      this.isLog = XMLTool.getValueByTagName(this.body, "isLog");
    }
  }

  public String getRowCount()
  {
    return this.rowCount;
  }

  public void setRowCount(String paramString)
  {
    this.rowCount = paramString;
  }

  public Properties getProp()
  {
    return this.prop;
  }

  public String getOperatorSubject()
  {
    return this.operatorSubject;
  }

  public void setOperatorSubject(String paramString)
  {
    this.operatorSubject = paramString;
  }

  public String getOperatorSN()
  {
    return this.operatorSN;
  }

  public void setOperatorSN(String paramString)
  {
    this.operatorSN = paramString;
  }

  public String getOperationType()
  {
    return this.operationType;
  }

  public void setOperationType(String paramString)
  {
    this.operationType = paramString;
  }

  public void setOperationResult(String paramString)
  {
    this.operationResult = paramString;
  }

  public void setObjectCTMLName(String paramString)
  {
    this.objectCTMLName = paramString;
  }

  public void setObjectCertSubjcet(String paramString)
  {
    this.objectCertSubjcet = paramString;
  }

  public void setObjectCertSN(String paramString)
  {
    this.objectCertSN = paramString;
  }

  public void setFromIndex(String paramString)
  {
    this.fromIndex = paramString;
  }

  public String getFromIndex()
  {
    return this.fromIndex;
  }

  public String getObjectCertSN()
  {
    return this.objectCertSN;
  }

  public String getObjectCertSubjcet()
  {
    return this.objectCertSubjcet;
  }

  public String getObjectCTMLName()
  {
    return this.objectCTMLName;
  }

  public String getOperationResult()
  {
    return this.operationResult;
  }

  public String getOperationTimeBegin()
  {
    return this.operationTimeBegin;
  }

  public String getOperationTimeEnd()
  {
    return this.operationTimeEnd;
  }

  public void setOperationTimeBegin(String paramString)
  {
    this.operationTimeBegin = paramString;
  }

  public void setOperationTimeEnd(String paramString)
  {
    this.operationTimeEnd = paramString;
  }

  public String getExactQuery()
  {
    return this.exactQuery;
  }

  public void setExactQuery(String paramString)
  {
    this.exactQuery = paramString;
  }

  public String getIsLog()
  {
    return this.isLog;
  }

  public void setIsLog(String paramString)
  {
    this.isLog = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.log.service.request.OptLogSearchRequest
 * JD-Core Version:    0.6.0
 */