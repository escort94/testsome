package cn.com.jit.ida.ca.log.service.response;

import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.log.LogException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LogArchiveResponse extends Response
{
  private String logCount = null;

  public LogArchiveResponse()
  {
    super.setOperation("AUDITARCHIVELOG");
  }

  public LogArchiveResponse(byte[] paramArrayOfByte)
    throws LogException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new LogException("0205", "数据有效性检查 应答信息格式不合法", localException);
    }
  }

  public LogArchiveResponse(Response paramResponse)
    throws LogException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      throw new LogException("0205", "数据有效性检查 应答信息格式不合法", localException);
    }
  }

  public final void updateBody(boolean paramBoolean)
    throws LogException
  {
    Object localObject;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localObject = this.doc.createElement("entry");
      this.body.appendChild((Node)localObject);
      Element localElement1 = XMLTool.newElement(this.doc, "logCount", this.logCount);
      ((Element)localObject).appendChild(localElement1);
    }
    else
    {
      localObject = this.body.getElementsByTagName("entry");
      int i = ((NodeList)localObject).getLength();
      if (i == 0)
        return;
      Element localElement2 = (Element)((NodeList)localObject).item(0);
      this.logCount = XMLTool.getValueByTagName(localElement2, "logCount");
    }
  }

  public void setLogCount(String paramString)
  {
    this.logCount = paramString;
  }

  public String getLogCount()
  {
    return this.logCount;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.log.service.response.LogArchiveResponse
 * JD-Core Version:    0.6.0
 */