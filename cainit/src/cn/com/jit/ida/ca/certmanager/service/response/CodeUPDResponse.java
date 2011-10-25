package cn.com.jit.ida.ca.certmanager.service.response;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CodeUPDResponse extends Response
{
  private String refCode;
  private String authCode;

  public CodeUPDResponse()
  {
    this.refCode = null;
    this.authCode = null;
    super.setOperation("AUTHCODEUPDATE");
  }

  public CodeUPDResponse(byte[] paramArrayOfByte)
    throws CertException
  {
    this.refCode = null;
    this.authCode = null;
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("0705", "其他错误 应答信息格式不合法", localException);
    }
  }

  public CodeUPDResponse(Response paramResponse)
    throws CertException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("0705", "其他错误 应答信息格式不合法");
    }
  }

  protected void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject;
    Element localElement2;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localObject = this.doc.createElement("entry");
      this.body.appendChild((Node)localObject);
      Element localElement1 = XMLTool.newElement(this.doc, "certSN", this.refCode);
      ((Element)localObject).appendChild(localElement1);
      localElement2 = XMLTool.newElement(this.doc, "authCode", this.authCode);
      ((Element)localObject).appendChild(localElement2);
    }
    else
    {
      localObject = this.body.getElementsByTagName("entry");
      int i = ((NodeList)localObject).getLength();
      if (i == 0)
        return;
      this.refCode = null;
      this.authCode = null;
      localElement2 = (Element)((NodeList)localObject).item(0);
      this.refCode = XMLTool.getValueByTagName(localElement2, "certSN");
      this.authCode = XMLTool.getValueByTagName(localElement2, "authCode");
    }
  }

  public void setRefCode(String paramString)
  {
    this.refCode = paramString;
  }

  public void setAuthCode(String paramString)
  {
    this.authCode = paramString;
  }

  public String getAuthCode()
  {
    return this.authCode;
  }

  public String getRefCode()
  {
    return this.refCode;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.response.CodeUPDResponse
 * JD-Core Version:    0.6.0
 */