package cn.com.jit.ida.ca.certmanager.service.response;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CertReqResponse extends Response
{
  private String refcode = null;
  private String authcode = null;

  public CertReqResponse()
  {
    super.setOperation("CERTREQUST");
  }

  public CertReqResponse(byte[] paramArrayOfByte)
    throws CertException
  {
    this.refcode = null;
    this.authcode = null;
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("0705", "其他错误 应答信息格式不合法", localException);
    }
  }

  public CertReqResponse(Response paramResponse)
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

  public String getAuthcode()
  {
    return this.authcode;
  }

  public String getRefcode()
  {
    return this.refcode;
  }

  public void setRefcode(String paramString)
  {
    this.refcode = paramString;
  }

  public void setAuthcode(String paramString)
  {
    this.authcode = paramString;
  }

  public final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject;
    Element localElement2;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localObject = this.doc.createElement("entry");
      this.body.appendChild((Node)localObject);
      Element localElement1 = XMLTool.newElement(this.doc, "refCode", this.refcode);
      ((Element)localObject).appendChild(localElement1);
      localElement2 = XMLTool.newElement(this.doc, "authCode", this.authcode);
      ((Element)localObject).appendChild(localElement2);
    }
    else
    {
      localObject = this.body.getElementsByTagName("entry");
      int i = ((NodeList)localObject).getLength();
      if (i == 0)
        return;
      this.refcode = null;
      this.authcode = null;
      localElement2 = (Element)((NodeList)localObject).item(0);
      this.refcode = XMLTool.getValueByTagName(localElement2, "refCode");
      this.authcode = XMLTool.getValueByTagName(localElement2, "authCode");
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.response.CertReqResponse
 * JD-Core Version:    0.6.0
 */