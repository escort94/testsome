package cn.com.jit.ida.ca.ctml.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException.REQUEST;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CTMLSelfExtDeleteRequest extends Request
{
  private static final String XMLTAG_NAME = "name";
  private static final String XMLTAG_OID = "oid";
  private String name;
  private String oid;

  public CTMLSelfExtDeleteRequest()
  {
    super.setOperation("CTMLSELFEXTDELETE");
  }

  public CTMLSelfExtDeleteRequest(Request paramRequest)
    throws IDAException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.REQUEST.PARSE_REQUESTXMLDOC, localException);
      throw localCTMLServiceException;
    }
  }

  public CTMLSelfExtDeleteRequest(byte[] paramArrayOfByte)
    throws IDAException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.REQUEST.PARSE_REQUESTXMLDATA, localException);
      throw localCTMLServiceException;
    }
  }

  public String getName()
  {
    return this.name;
  }

  public String getOid()
  {
    return this.oid;
  }

  public void setOid(String paramString)
  {
    this.oid = paramString;
  }

  public void setName(String paramString)
  {
    this.name = paramString;
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
      if (this.name != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "name", this.name);
        localElement1.appendChild(localElement2);
      }
      if (this.oid != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "oid", this.oid);
        localElement1.appendChild(localElement2);
      }
    }
    else
    {
      this.name = XMLTool.getValueByTagName(this.body, "name");
      this.oid = XMLTool.getValueByTagName(this.body, "oid");
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.request.CTMLSelfExtDeleteRequest
 * JD-Core Version:    0.6.0
 */