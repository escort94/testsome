package cn.com.jit.ida.ca.certmanager.service.response;

import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CertArchiveResponse extends Response
{
  private String certCount = null;

  public CertArchiveResponse()
  {
    super.setOperation("SUPERARCHIVECERT");
  }

  public CertArchiveResponse(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("0705", "其他错误 应答信息格式不合法", localException);
    }
  }

  public CertArchiveResponse(Response paramResponse)
    throws CertException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("0705", "其他错误 应答信息格式不合法", localException);
    }
  }

  public final void updateBody(boolean paramBoolean)
    throws CertException
  {
    Object localObject;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localObject = this.doc.createElement("entry");
      this.body.appendChild((Node)localObject);
      Element localElement1 = XMLTool.newElement(this.doc, "certCount", this.certCount);
      ((Element)localObject).appendChild(localElement1);
    }
    else
    {
      localObject = this.body.getElementsByTagName("entry");
      int i = ((NodeList)localObject).getLength();
      if (i == 0)
        return;
      Element localElement2 = (Element)((NodeList)localObject).item(0);
      this.certCount = XMLTool.getValueByTagName(localElement2, "certCount");
    }
  }

  public void setCertCount(String paramString)
  {
    this.certCount = paramString;
  }

  public String getCertCount()
  {
    return this.certCount;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.response.CertArchiveResponse
 * JD-Core Version:    0.6.0
 */