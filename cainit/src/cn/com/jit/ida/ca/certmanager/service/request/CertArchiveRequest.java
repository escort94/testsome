package cn.com.jit.ida.ca.certmanager.service.request;

import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CertArchiveRequest extends Request
{
  private String endTime = null;

  public CertArchiveRequest()
  {
    super.setOperation("SUPERARCHIVECERT");
  }

  public CertArchiveRequest(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("0704", "其他错误 请求信息格式不合法", localException);
    }
  }

  public CertArchiveRequest(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("0704", "其他错误 请求信息格式不合法", localException);
    }
  }

  public void updateBody(boolean paramBoolean)
    throws CertException
  {
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      Element localElement = XMLTool.newChildElement(this.body, "entry");
      if (this.endTime != null)
        XMLTool.newChildElement(localElement, "endTime", this.endTime);
    }
    else
    {
      this.endTime = XMLTool.getValueByTagName(this.body, "endTime");
    }
  }

  public String getEndTime()
  {
    return this.endTime;
  }

  public void setEndTime(String paramString)
  {
    this.endTime = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.request.CertArchiveRequest
 * JD-Core Version:    0.6.0
 */