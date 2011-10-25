package cn.com.jit.ida.ca.certmanager.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CertUPDRetainKeyRequest extends Request
{
  private String certSN = null;
  private String notBefore = null;
  private String validity = null;
  private String certDN = null;
  private String ctmlName = null;
  private String p10 = null;
  private String tempPubKey = null;

  public CertUPDRetainKeyRequest()
  {
    super.setOperation("CERTUPDATERETAINKEY");
  }

  public CertUPDRetainKeyRequest(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("81110704", "证书更新并下载服务 其他错误 请求信息格式不合法", localException);
    }
  }

  public CertUPDRetainKeyRequest(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("81110704", "证书更新并下载服务 其他错误 请求信息格式不合法");
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
      if (this.notBefore != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "notBefore", this.notBefore);
        localElement1.appendChild(localElement2);
      }
      if (this.validity != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "validity", this.validity);
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
      if (this.p10 != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "p10", this.p10);
        localElement1.appendChild(localElement2);
      }
      if (this.tempPubKey != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "tempPubKey", this.tempPubKey);
        localElement1.appendChild(localElement2);
      }
    }
    else
    {
      this.certSN = XMLTool.getValueByTagName(this.body, "certSN");
      this.notBefore = XMLTool.getValueByTagName(this.body, "notBefore");
      this.validity = XMLTool.getValueByTagName(this.body, "validity");
      this.certDN = XMLTool.getValueByTagName(this.body, "subject");
      this.ctmlName = XMLTool.getValueByTagName(this.body, "ctmlName");
      this.p10 = XMLTool.getValueByTagName(this.body, "p10");
      this.tempPubKey = XMLTool.getValueByTagName(this.body, "tempPubKey");
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

  public String getNotBefore()
  {
    return this.notBefore;
  }

  public String getValidity()
  {
    return this.validity;
  }

  public void setValidity(String paramString)
    throws IDAException
  {
    String str = paramString.trim();
    if (!ReqCheck.checkValidity(str))
      throw new IDAException("81110804", "证书更新并下载服务 设置申请信息错误 有效期长度内容错误");
    this.validity = str;
  }

  public void setNotBefore(String paramString)
    throws IDAException
  {
    String str = paramString.trim();
    if (!ReqCheck.checkNotBefore(str))
      throw new IDAException("81110803", "证书更新并下载服务 设置申请信息错误 起始有效期内容错误");
    this.notBefore = str;
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
        throw new IDAException("81110801", "证书更新并下载服务 设置申请信息错误 证书主题内容错误");
      this.certDN = ReqCheck.filterDN(paramString);
    }
  }

  public String getP10()
  {
    return this.p10;
  }

  public void setP10(String paramString)
  {
    if (paramString != null)
      this.p10 = paramString.trim();
  }

  public String getTempPubKey()
  {
    return this.tempPubKey;
  }

  public void setTempPubKey(String paramString)
  {
    if (paramString != null)
      this.tempPubKey = paramString.trim();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.request.CertUPDRetainKeyRequest
 * JD-Core Version:    0.6.0
 */