package cn.com.jit.ida.ca.log.service.request;

import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.log.LogException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LogArchiveRequest extends Request
{
  private String startTime = null;
  private String endTime = null;

  public LogArchiveRequest()
  {
    super.setOperation("AUDITARCHIVELOG");
  }

  public LogArchiveRequest(Request paramRequest)
    throws LogException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new LogException("0204", "数据有效性检查 请求信息格式不合法", localException);
    }
  }

  public LogArchiveRequest(byte[] paramArrayOfByte)
    throws LogException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new LogException("0204", "数据有效性检查 请求信息格式不合法", localException);
    }
  }

  public void updateBody(boolean paramBoolean)
    throws LogException
  {
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      Element localElement = XMLTool.newChildElement(this.body, "entry");
      if (this.startTime != null)
        XMLTool.newChildElement(localElement, "startTime", this.startTime);
      if (this.endTime != null)
        XMLTool.newChildElement(localElement, "endTime", this.endTime);
    }
    else
    {
      this.startTime = XMLTool.getValueByTagName(this.body, "startTime");
      this.endTime = XMLTool.getValueByTagName(this.body, "endTime");
    }
  }

  public String getStartTime()
  {
    return this.startTime;
  }

  public void setStartTime(String paramString)
  {
    this.startTime = paramString;
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
 * Qualified Name:     cn.com.jit.ida.ca.log.service.request.LogArchiveRequest
 * JD-Core Version:    0.6.0
 */