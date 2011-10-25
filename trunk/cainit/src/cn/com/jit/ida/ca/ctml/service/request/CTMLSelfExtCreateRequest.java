package cn.com.jit.ida.ca.ctml.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException.REQUEST;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CTMLSelfExtCreateRequest extends Request
{
  private static final String XMLTAG_NAME = "name";
  private static final String XMLTAG_OID = "oid";
  private static final String XMLTAG_ENCODING = "encoding";
  private static final String XMLTAG_DESCRIPTION = "description";
  private String name;
  private String oid;
  private String encoding;
  private String description;

  public CTMLSelfExtCreateRequest()
  {
    super.setOperation("CTMLSELFEXTCREATE");
  }

  public CTMLSelfExtCreateRequest(Request paramRequest)
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

  public CTMLSelfExtCreateRequest(byte[] paramArrayOfByte)
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

  public void setName(String paramString)
  {
    this.name = paramString;
  }

  public String getOid()
  {
    return this.oid;
  }

  public void setOid(String paramString)
  {
    this.oid = paramString;
  }

  public String getEncoding()
  {
    return this.encoding;
  }

  public void setEncoding(String paramString)
  {
    this.encoding = paramString;
  }

  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String paramString)
  {
    this.description = paramString;
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
      if (this.encoding != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "encoding", this.encoding);
        localElement1.appendChild(localElement2);
      }
      if (this.description != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "description", this.description);
        localElement1.appendChild(localElement2);
      }
    }
    else
    {
      this.name = XMLTool.getValueByTagName(this.body, "name");
      this.oid = XMLTool.getValueByTagName(this.body, "oid");
      this.encoding = XMLTool.getValueByTagName(this.body, "encoding");
      this.description = XMLTool.getValueByTagName(this.body, "description");
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.request.CTMLSelfExtCreateRequest
 * JD-Core Version:    0.6.0
 */