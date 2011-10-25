package cn.com.jit.ida.ca.certmanager.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CertDownloadRequest extends Request
{
  private String refCode = null;
  private String authCode = null;
  private String p10 = null;
  private String tempPubKey = null;

  public CertDownloadRequest()
  {
    super.setOperation("CERTDOWNLOAD");
  }

  public CertDownloadRequest(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("81020704", "证书下载服务 其他错误 请求信息格式不合法");
    }
  }

  public CertDownloadRequest(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("81020704", "证书下载服务 其他错误 请求信息格式不合法", localException);
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
      Element localElement2 = XMLTool.newElement(this.doc, "certSN", this.refCode);
      localElement1.appendChild(localElement2);
      Element localElement3 = XMLTool.newElement(this.doc, "authCode", this.authCode);
      localElement1.appendChild(localElement3);
      Element localElement4;
      if (this.p10 != null)
      {
        localElement4 = XMLTool.newElement(this.doc, "p10", this.p10);
        localElement1.appendChild(localElement4);
      }
      if (this.tempPubKey != null)
      {
        localElement4 = XMLTool.newElement(this.doc, "tempPubKey", this.tempPubKey);
        localElement1.appendChild(localElement4);
      }
    }
    else
    {
      this.refCode = XMLTool.getValueByTagName(this.body, "certSN");
      this.authCode = XMLTool.getValueByTagName(this.body, "authCode");
      this.p10 = XMLTool.getValueByTagName(this.body, "p10");
      this.tempPubKey = XMLTool.getValueByTagName(this.body, "tempPubKey");
    }
  }

  public String getAuthCode()
  {
    return this.authCode;
  }

  public String getP10()
  {
    return this.p10;
  }

  public String getRefCode()
  {
    return this.refCode;
  }

  public void setRefCode(String paramString)
    throws IDAException
  {
    if ((paramString == null) || (paramString.trim().equals("")))
      throw new IDAException("81020805", "证书下载服务 设置申请信息错误 参考号内容错误");
    this.refCode = paramString.trim();
  }

  public void setP10(String paramString)
  {
    if (paramString != null)
      this.p10 = paramString.trim();
  }

  public void setTempPubKey(String paramString)
  {
    if (paramString != null)
      this.tempPubKey = paramString.trim();
  }

  public String getTempPubKey()
  {
    return this.tempPubKey;
  }

  public void setAuthCode(String paramString)
    throws IDAException
  {
    if ((paramString == null) || (paramString.trim().equals("")))
      throw new IDAException("81020806", "证书下载服务 设置申请信息错误 授权码内容错误");
    this.authCode = paramString.trim();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.request.CertDownloadRequest
 * JD-Core Version:    0.6.0
 */