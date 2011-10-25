package cn.com.jit.ida.ca.certmanager.service.request;

import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CertEntityQueryRequest extends Request
{
  private String certSN = null;

  public CertEntityQueryRequest()
  {
    super.setOperation("CERTENTITYQUERY");
  }

  public CertEntityQueryRequest(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("81080704", "证书实体查询服务 其他错误 请求信息格式不合法", localException);
    }
  }

  public CertEntityQueryRequest(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("81080704", "8108 其他错误 请求信息格式不合法", localException);
    }
  }

  public String getCertSN()
  {
    return this.certSN;
  }

  protected void updateBody(boolean paramBoolean)
    throws CertException
  {
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      Element localElement = XMLTool.newChildElement(this.body, "entry");
      XMLTool.newChildElement(localElement, "certSN", this.certSN);
    }
    else
    {
      this.certSN = XMLTool.getValueByTagName(this.body, "certSN");
    }
  }

  public void setCertSN(String paramString)
    throws CertException
  {
    if ((paramString == null) || (paramString.trim().equals("")))
      throw new CertException("81080807", "证书实体查询服务 设置申请信息错误 证书序列号内容错误");
    this.certSN = paramString.trim();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.request.CertEntityQueryRequest
 * JD-Core Version:    0.6.0
 */