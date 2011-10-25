package cn.com.jit.ida.ca.certmanager.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CertQueryRequest extends Request
{
  private Properties prop = null;
  private String certSN = null;
  private String subject = null;
  private String ctmlName = null;
  private String certStatus = null;
  private String applicant = null;
  private String notBefore = null;
  private String notAfter = null;
  private String validity = null;
  private String createTimeBegin = null;
  private String createTimeEnd = null;
  private String fromIndex = null;
  private String rowCount = null;
  private String exactQuery = "false";
  private String action = null;

  public CertQueryRequest()
  {
    super.setOperation("CERTQUERY");
  }

  public CertQueryRequest(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("81070704", "证书查询服务 其他错误 请求信息格式不合法", localException);
    }
  }

  public CertQueryRequest(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("81070704", "8107 其他错误 请求信息格式不合法", localException);
    }
  }

  protected void updateBody(boolean paramBoolean)
    throws CertException
  {
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      Element localElement = XMLTool.newChildElement(this.body, "entry");
      if (this.certSN != null)
        XMLTool.newChildElement(localElement, "certSN", this.certSN);
      if (this.subject != null)
        XMLTool.newChildElement(localElement, "subject", this.subject);
      if (this.ctmlName != null)
        XMLTool.newChildElement(localElement, "ctmlName", this.ctmlName);
      if (this.certStatus != null)
        XMLTool.newChildElement(localElement, "certStatus", this.certStatus);
      if (this.applicant != null)
        XMLTool.newChildElement(localElement, "applicant", this.applicant);
      if (this.notBefore != null)
        XMLTool.newChildElement(localElement, "notBefore", this.notBefore);
      if (this.notAfter != null)
        XMLTool.newChildElement(localElement, "notAfter", this.notAfter);
      if (this.createTimeBegin != null)
        XMLTool.newChildElement(localElement, "createTimeStart", this.createTimeBegin);
      if (this.createTimeEnd != null)
        XMLTool.newChildElement(localElement, "createTimeEnd", this.createTimeEnd);
      if (this.validity != null)
        XMLTool.newChildElement(localElement, "validity", this.validity);
      if (this.fromIndex != null)
        XMLTool.newChildElement(localElement, "fromIndex", this.fromIndex);
      if (this.rowCount != null)
        XMLTool.newChildElement(localElement, "rowCount", this.rowCount);
      if (this.action != null)
        XMLTool.newChildElement(localElement, "action", this.action);
      XMLTool.newChildElement(localElement, "exactQuery", this.exactQuery);
    }
    else
    {
      this.certSN = XMLTool.getValueByTagName(this.body, "certSN");
      if (this.certSN != null)
        this.prop.put("certSN", this.certSN);
      this.subject = XMLTool.getValueByTagName(this.body, "subject");
      if (this.subject != null)
        this.prop.put("subject", this.subject);
      this.ctmlName = XMLTool.getValueByTagName(this.body, "ctmlName");
      if (this.ctmlName != null)
        this.prop.put("ctmlName", this.ctmlName);
      this.certStatus = XMLTool.getValueByTagName(this.body, "certStatus");
      if (this.certStatus != null)
        this.prop.put("certStatus", this.certStatus);
      this.applicant = XMLTool.getValueByTagName(this.body, "applicant");
      if (this.applicant != null)
        this.prop.put("applicant", this.applicant);
      this.notBefore = XMLTool.getValueByTagName(this.body, "notBefore");
      if (this.notBefore != null)
        this.prop.put("notBefore", this.notBefore);
      this.notAfter = XMLTool.getValueByTagName(this.body, "notAfter");
      if (this.notAfter != null)
        this.prop.put("notAfter", this.notAfter);
      this.createTimeBegin = XMLTool.getValueByTagName(this.body, "createTimeStart");
      if (this.createTimeBegin != null)
        this.prop.put("createTimeStart", this.createTimeBegin);
      this.createTimeEnd = XMLTool.getValueByTagName(this.body, "createTimeEnd");
      if (this.createTimeEnd != null)
        this.prop.put("createTimeEnd", this.createTimeEnd);
      this.validity = XMLTool.getValueByTagName(this.body, "validity");
      if (this.validity != null)
        this.prop.put("validity", this.validity);
      this.action = XMLTool.getValueByTagName(this.body, "action");
      this.fromIndex = XMLTool.getValueByTagName(this.body, "fromIndex");
      this.rowCount = XMLTool.getValueByTagName(this.body, "rowCount");
      this.exactQuery = XMLTool.getValueByTagName(this.body, "exactQuery");
    }
  }

  public Properties getCertInfoProperties()
  {
    return this.prop;
  }

  public String getApplicant()
  {
    return this.applicant;
  }

  public String getCertSN()
  {
    return this.certSN;
  }

  public String getCertStatus()
  {
    return this.certStatus;
  }

  public String getCtmlName()
  {
    return this.ctmlName;
  }

  public String getNotAfter()
  {
    return this.notAfter;
  }

  public String getNotBefore()
  {
    return this.notBefore;
  }

  public String getCreateTimeEnd()
  {
    return this.createTimeEnd;
  }

  public String getCreateTimeBegin()
  {
    return this.createTimeBegin;
  }

  public String getSubject()
  {
    return this.subject;
  }

  public String getValidity()
  {
    return this.validity;
  }

  public void setValidity(String paramString)
  {
    if (paramString != null)
      this.validity = paramString.trim();
  }

  public void setSubject(String paramString)
    throws IDAException
  {
    int i = 0;
    if ((paramString != null) && (!paramString.trim().equals("")))
    {
      if (!ReqCheck.checkDN(paramString))
        i = 1;
      if (i != 0)
        this.subject = ReqCheck.filterDN(paramString);
      else
        this.subject = paramString.trim();
    }
  }

  public void setCreateTimeBegin(String paramString)
  {
    if (paramString != null)
      this.createTimeBegin = paramString.trim();
  }

  public void setCreateTimeEnd(String paramString)
  {
    if (paramString != null)
      this.createTimeEnd = paramString.trim();
  }

  public void setNotBefore(String paramString)
  {
    if (paramString != null)
      this.notBefore = paramString.trim();
  }

  public void setCtmlName(String paramString)
  {
    if (paramString != null)
      this.ctmlName = paramString.trim();
  }

  public void setCertStatus(String paramString)
  {
    if (paramString != null)
      this.certStatus = paramString.trim();
  }

  public void setCertSN(String paramString)
  {
    if (paramString != null)
      this.certSN = paramString.trim();
  }

  public void setApplicant(String paramString)
  {
    if (paramString != null)
      this.applicant = paramString.trim();
  }

  public String getRowCount()
  {
    return this.rowCount;
  }

  public String getFromIndex()
  {
    return this.fromIndex;
  }

  public void setFromIndex(String paramString)
  {
    if (paramString != null)
      this.fromIndex = paramString.trim();
  }

  public void setNotAfter(String paramString)
  {
    if (paramString != null)
      this.notAfter = paramString.trim();
  }

  public void setRowCount(String paramString)
  {
    if (paramString != null)
      this.rowCount = paramString.trim();
  }

  public String getExactQuery()
  {
    return this.exactQuery;
  }

  public void setExactQuery(String paramString)
  {
    if (paramString != null)
      this.exactQuery = paramString.trim();
  }

  public String getAction()
  {
    return this.action;
  }

  public void setAction(String paramString)
  {
    this.action = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.request.CertQueryRequest
 * JD-Core Version:    0.6.0
 */