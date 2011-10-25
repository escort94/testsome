package cn.com.jit.ida.ca.certmanager.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CertRevokeRequest extends Request
{
  private String certSN = null;
  private String certDN = null;
  private String ctmlName = null;
  private String revokeID = null;
  private String revokeDESC = null;

  public CertRevokeRequest()
  {
    super.setOperation("CERTREVOKE");
  }

  public CertRevokeRequest(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("81040704", "证书注销服务 其他错误 请求信息格式不合法", localException);
    }
  }

  public CertRevokeRequest(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("81040704", "证书注销服务 其他错误 请求信息格式不合法", localException);
    }
  }

  protected void updateBody(boolean paramBoolean)
    throws CertException
  {
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      Element localElement = XMLTool.newChildElement(this.body, "entry");
      XMLTool.newChildElement(localElement, "certSN", this.certSN);
      XMLTool.newChildElement(localElement, "subject", this.certDN);
      XMLTool.newChildElement(localElement, "ctmlName", this.ctmlName);
      XMLTool.newChildElement(localElement, "revokeReason", this.revokeID);
      XMLTool.newChildElement(localElement, "revokeDesc", this.revokeDESC);
    }
    else
    {
      this.certSN = XMLTool.getValueByTagName(this.body, "certSN");
      this.certDN = XMLTool.getValueByTagName(this.body, "subject");
      this.ctmlName = XMLTool.getValueByTagName(this.body, "ctmlName");
      this.revokeID = XMLTool.getValueByTagName(this.body, "revokeReason");
      this.revokeDESC = XMLTool.getValueByTagName(this.body, "revokeDesc");
    }
  }

  public String getCertDN()
  {
    return this.certDN;
  }

  public String getCertSN()
  {
    return this.certSN;
  }

  public String getCtmlName()
  {
    return this.ctmlName;
  }

  public String getRevokeDESC()
  {
    return this.revokeDESC;
  }

  public String getRevokeID()
  {
    return this.revokeID;
  }

  public void setRevokeID(String paramString)
  {
    if (paramString != null)
      this.revokeID = paramString.trim();
  }

  public void setRevokeDESC(String paramString)
  {
    if (paramString != null)
      this.revokeDESC = paramString.trim();
  }

  public void setCtmlName(String paramString)
  {
    if (paramString != null)
      this.ctmlName = paramString.trim();
  }

  public void setCertSN(String paramString)
  {
    if (paramString != null)
      this.certSN = paramString.trim();
  }

  public void setCertDN(String paramString)
    throws IDAException
  {
    if ((paramString != null) && (!paramString.trim().equals("")))
    {
      if (!ReqCheck.checkDN(paramString))
        throw new IDAException("81040801", "证书注销服务 设置申请信息错误 证书主题内容错误");
      this.certDN = ReqCheck.filterDN(paramString);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.request.CertRevokeRequest
 * JD-Core Version:    0.6.0
 */