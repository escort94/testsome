package cn.com.jit.ida.ca.certmanager.service.response;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.pki.encoders.Base64;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CertEntityQueryResponse extends Response
{
  private byte[] cert = null;

  public CertEntityQueryResponse()
  {
    super.setOperation("CERTENTITYQUERY");
  }

  public CertEntityQueryResponse(byte[] paramArrayOfByte)
    throws IDAException
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

  public CertEntityQueryResponse(Response paramResponse)
    throws IDAException
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

  public void setCertEntity(byte[] paramArrayOfByte)
  {
    this.cert = paramArrayOfByte;
  }

  public byte[] getCertEntity()
  {
    return this.cert;
  }

  protected void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject1;
    Element localElement;
    Object localObject2;
    if (paramBoolean)
    {
      if (this.cert == null)
        return;
      this.body = this.doc.createElement("body");
      localObject1 = Base64.encode(this.cert);
      String str = new String(localObject1);
      localElement = this.doc.createElement("entry");
      this.body.appendChild(localElement);
      localObject2 = XMLTool.newElement(this.doc, "certEntity", str);
      localElement.appendChild((Node)localObject2);
    }
    else
    {
      localObject1 = this.body.getElementsByTagName("entry");
      int i = ((NodeList)localObject1).getLength();
      if (i == 0)
      {
        this.cert = null;
        return;
      }
      localElement = (Element)((NodeList)localObject1).item(0);
      localObject2 = XMLTool.getValueByTagName(localElement, "certEntity");
      if ((localObject2 == null) || (((String)localObject2).equals("")))
      {
        this.cert = null;
        return;
      }
      this.cert = Base64.decode((String)localObject2);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.response.CertEntityQueryResponse
 * JD-Core Version:    0.6.0
 */