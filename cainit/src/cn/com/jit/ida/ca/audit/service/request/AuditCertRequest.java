package cn.com.jit.ida.ca.audit.service.request;

import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.audit.AuditException;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AuditCertRequest extends Request
{
  private Properties prop = null;
  private String sortBy = null;
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
  private String exactExact = "false";

  public AuditCertRequest()
  {
    super.setOperation("AUDITCOUNTCERT");
  }

  public AuditCertRequest(Request paramRequest)
    throws AuditException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new AuditException("0000", "通信格式错误 请求信息格式不合法");
    }
  }

  public AuditCertRequest(byte[] paramArrayOfByte)
    throws AuditException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new AuditException("0000", "通信格式错误 请求信息格式不合法");
    }
  }

  protected void updateBody(boolean paramBoolean)
    throws AuditException
  {
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      Element localElement = XMLTool.newChildElement(this.body, "entry");
      if (this.certSN != null)
        XMLTool.newChildElement(localElement, "certSN", this.certSN.trim());
      if (this.subject != null)
        XMLTool.newChildElement(localElement, "subject", this.subject.trim());
      if (this.ctmlName != null)
        XMLTool.newChildElement(localElement, "ctmlName", this.ctmlName.trim());
      if (this.certStatus != null)
        XMLTool.newChildElement(localElement, "certStatus", this.certStatus.trim());
      if (this.applicant != null)
        XMLTool.newChildElement(localElement, "applicant", this.applicant.trim());
      if (this.notBefore != null)
        XMLTool.newChildElement(localElement, "notBefore", this.notBefore.trim());
      if (this.notAfter != null)
        XMLTool.newChildElement(localElement, "notAfter", this.notAfter.trim());
      if (this.createTimeBegin != null)
        XMLTool.newChildElement(localElement, "createTimeStart", this.createTimeBegin.trim());
      if (this.createTimeEnd != null)
        XMLTool.newChildElement(localElement, "createTimeEnd", this.createTimeEnd.trim());
      if (this.validity != null)
        XMLTool.newChildElement(localElement, "validity", this.validity.trim());
      if (this.sortBy != null)
        XMLTool.newChildElement(localElement, "sortBy", this.sortBy.trim());
      XMLTool.newChildElement(localElement, "exactQuery", this.exactExact);
    }
    else
    {
      this.certSN = XMLTool.getValueByTagName(this.body, "certSN");
      if (this.certSN != null)
        this.prop.put("certSN", this.certSN.trim());
      this.subject = XMLTool.getValueByTagName(this.body, "subject");
      if (this.subject != null)
        this.prop.put("subject", this.subject.trim());
      this.ctmlName = XMLTool.getValueByTagName(this.body, "ctmlName");
      if (this.ctmlName != null)
        this.prop.put("ctmlName", this.ctmlName.trim());
      this.certStatus = XMLTool.getValueByTagName(this.body, "certStatus");
      if (this.certStatus != null)
        this.prop.put("certStatus", this.certStatus.trim());
      this.applicant = XMLTool.getValueByTagName(this.body, "applicant");
      if (this.applicant != null)
        this.prop.put("applicant", this.applicant.trim());
      this.notBefore = XMLTool.getValueByTagName(this.body, "notBefore");
      if (this.notBefore != null)
        this.prop.put("notBefore", this.notBefore.trim());
      this.notAfter = XMLTool.getValueByTagName(this.body, "notAfter");
      if (this.notAfter != null)
        this.prop.put("notAfter", this.notAfter.trim());
      this.createTimeBegin = XMLTool.getValueByTagName(this.body, "createTimeStart");
      if (this.createTimeBegin != null)
        this.prop.put("createTimeStart", this.createTimeBegin.trim());
      this.createTimeEnd = XMLTool.getValueByTagName(this.body, "createTimeEnd");
      if (this.createTimeEnd != null)
        this.prop.put("createTimeEnd", this.createTimeEnd.trim());
      this.validity = XMLTool.getValueByTagName(this.body, "validity");
      if (this.validity != null)
        this.prop.put("validity", this.validity.trim());
      this.sortBy = XMLTool.getValueByTagName(this.body, "sortBy");
      this.exactExact = XMLTool.getValueByTagName(this.body, "exactQuery");
    }
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

  public String getCreateTimeBegin()
  {
    return this.createTimeBegin;
  }

  public String getCreateTimeEnd()
  {
    return this.createTimeEnd;
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

  public Properties getProp()
  {
    return this.prop;
  }

  public String getSortBy()
  {
    return this.sortBy;
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
    this.validity = paramString;
  }

  public void setSubject(String paramString)
  {
    this.subject = paramString;
  }

  public void setSortBy(String paramString)
  {
    this.sortBy = paramString;
  }

  public void setNotBefore(String paramString)
  {
    this.notBefore = paramString;
  }

  public void setNotAfter(String paramString)
  {
    this.notAfter = paramString;
  }

  public void setCtmlName(String paramString)
  {
    this.ctmlName = paramString;
  }

  public void setCreateTimeEnd(String paramString)
  {
    this.createTimeEnd = paramString;
  }

  public void setCreateTimeBegin(String paramString)
  {
    this.createTimeBegin = paramString;
  }

  public void setCertStatus(String paramString)
  {
    this.certStatus = paramString;
  }

  public void setApplicant(String paramString)
  {
    this.applicant = paramString;
  }

  public void setCertSN(String paramString)
  {
    this.certSN = paramString;
  }

  public String getExactExact()
  {
    return this.exactExact;
  }

  public void setExactExact(String paramString)
  {
    this.exactExact = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.audit.service.request.AuditCertRequest
 * JD-Core Version:    0.6.0
 */