package cn.com.jit.ida.ca.ctml.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException.REQUEST;
import cn.com.jit.ida.ca.ctml.util.Base64;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CTMLCreateRequest extends Request
{
  private static final String XMLTAG_NAME = "name";
  private static final String XMLTAG_ID = "id";
  private static final String XMLTAG_TYPE = "type";
  private static final String XMLTAG_DESCRIPTION = "description";
  private static final String XMLTAG_POLICY = "policy";
  private String name;
  private String id;
  private String type;
  private String description;
  private byte[] policy;

  public CTMLCreateRequest()
  {
    super.setOperation("CTMLCREATE");
  }

  public CTMLCreateRequest(Request paramRequest)
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

  public CTMLCreateRequest(byte[] paramArrayOfByte)
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

  public String getId()
  {
    return this.id;
  }

  public void setId(String paramString)
  {
    this.id = paramString;
  }

  public String getType()
  {
    return this.type;
  }

  public void setType(String paramString)
  {
    this.type = paramString;
  }

  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String paramString)
  {
    this.description = paramString;
  }

  public byte[] getPolicy()
  {
    return this.policy;
  }

  public void setPolicy(byte[] paramArrayOfByte)
  {
    this.policy = paramArrayOfByte;
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localObject = this.doc.createElement("entry");
      this.body.appendChild((Node)localObject);
      Element localElement1 = XMLTool.newElement(this.doc, "name", this.name);
      ((Element)localObject).appendChild(localElement1);
      if (this.id != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "id", this.id);
        ((Element)localObject).appendChild(localElement2);
      }
      Element localElement2 = XMLTool.newElement(this.doc, "type", this.type);
      ((Element)localObject).appendChild(localElement2);
      if (this.description != null)
      {
        localElement3 = XMLTool.newElement(this.doc, "description", this.description);
        ((Element)localObject).appendChild(localElement3);
      }
      Element localElement3 = XMLTool.newElement(this.doc, "policy", Base64.byteArrayToBase64(this.policy));
      ((Element)localObject).appendChild(localElement3);
    }
    else
    {
      this.name = XMLTool.getValueByTagName(this.body, "name");
      this.id = XMLTool.getValueByTagName(this.body, "id");
      this.type = XMLTool.getValueByTagName(this.body, "type");
      this.description = XMLTool.getValueByTagName(this.body, "description");
      localObject = XMLTool.getValueByTagName(this.body, "policy");
      if (localObject == null)
        this.policy = null;
      else
        this.policy = Base64.base64ToByteArray((String)localObject);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.request.CTMLCreateRequest
 * JD-Core Version:    0.6.0
 */