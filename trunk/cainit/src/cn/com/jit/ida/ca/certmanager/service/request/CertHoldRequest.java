package cn.com.jit.ida.ca.certmanager.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CertHoldRequest extends Request
{
  private String certSN = null;
  private String certDN = null;
  private String ctmlName = null;
  private String revokeDESC = null;

  public CertHoldRequest()
  {
    super.setOperation("CERTHOLD");
  }

  public CertHoldRequest(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("81050704", "证书冻结服务 其他错误 请求信息格式不合法", localException);
    }
  }

  public CertHoldRequest(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("81050704", "证书冻结服务 其他错误 请求信息格式不合法", localException);
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

  public void setRevokeDESC(String paramString)
  {
    if (paramString != null)
      this.revokeDESC = paramString.trim();
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
        throw new IDAException("81050801", "证书冻结服务 设置申请信息错误 证书主题内容错误");
      this.certDN = ReqCheck.filterDN(paramString);
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
      XMLTool.newChildElement(localElement, "revokeDesc", this.revokeDESC);
    }
    else
    {
      this.certSN = XMLTool.getValueByTagName(this.body, "certSN");
      this.certDN = XMLTool.getValueByTagName(this.body, "subject");
      this.ctmlName = XMLTool.getValueByTagName(this.body, "ctmlName");
      this.revokeDESC = XMLTool.getValueByTagName(this.body, "revokeDesc");
    }
  }

  public void setCtmlName(String paramString)
  {
    if (paramString != null)
      this.ctmlName = paramString.trim();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.request.CertHoldRequest
 * JD-Core Version:    0.6.0
 */