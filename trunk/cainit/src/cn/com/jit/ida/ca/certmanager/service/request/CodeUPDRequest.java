package cn.com.jit.ida.ca.certmanager.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CodeUPDRequest extends Request
{
  private String certSN = null;
  private String certDN = null;
  private String ctmlName = null;

  public CodeUPDRequest()
  {
    super.setOperation("AUTHCODEUPDATE");
  }

  public CodeUPDRequest(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("81120704", "授权码更新服务其他错误 请求信息格式不合法", localException);
    }
  }

  public CodeUPDRequest(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("81120704", "授权码更新服务 其他错误 请求信息格式不合法");
    }
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      Element localElement1 = this.doc.createElement("entry");
      this.body.appendChild(localElement1);
      Element localElement2;
      if (this.certSN != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "certSN", this.certSN);
        localElement1.appendChild(localElement2);
      }
      if (this.certDN != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "subject", this.certDN);
        localElement1.appendChild(localElement2);
      }
      if (this.ctmlName != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "ctmlName", this.ctmlName);
        localElement1.appendChild(localElement2);
      }
    }
    else
    {
      this.certSN = XMLTool.getValueByTagName(this.body, "certSN");
      this.certDN = XMLTool.getValueByTagName(this.body, "subject");
      this.ctmlName = XMLTool.getValueByTagName(this.body, "ctmlName");
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
        throw new IDAException("81120801", "授权码更新服务 设置申请信息错误 证书主题内容错误");
      this.certDN = ReqCheck.filterDN(paramString);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.request.CodeUPDRequest
 * JD-Core Version:    0.6.0
 */