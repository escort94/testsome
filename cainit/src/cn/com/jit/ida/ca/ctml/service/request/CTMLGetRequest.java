package cn.com.jit.ida.ca.ctml.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException.REQUEST;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CTMLGetRequest extends Request
{
  private static final String XMLTAG_CONDITION = "condition";
  private static final String XMLTAG_CONTENT = "content";
  private static final String XMLTAG_ITEM = "item";
  private static final String XMLTAG_NAME = "name";
  private static final String XMLTAG_ID = "id";
  private static final String XMLTAG_TYPE = "type";
  private static final String XMLTAG_STATUS = "status";
  private static final String XMLTAG_DESCRIPTION = "description";
  private static final String XMLTAG_POLICY = "policy";
  private static final String[] CONTENT_MAP = { "name", "id", "type", "status", "description", "policy" };
  private static final String[] STATUS_MAP = { "UNUSED", "USING", "REVOKED" };
  private String name;
  private String id;
  private String type;
  private int status;
  private int content;

  public CTMLGetRequest()
  {
    super.setOperation("CTMLGET");
  }

  public CTMLGetRequest(Request paramRequest)
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

  public CTMLGetRequest(byte[] paramArrayOfByte)
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

  public String getId()
  {
    return this.id;
  }

  public String getName()
  {
    return this.name;
  }

  public int getStatus()
  {
    return this.status;
  }

  public String getType()
  {
    return this.type;
  }

  public void setId(String paramString)
  {
    this.id = paramString;
  }

  public void setName(String paramString)
  {
    this.name = paramString;
  }

  public void setStatus(int paramInt)
  {
    this.status = paramInt;
  }

  public void setType(String paramString)
  {
    this.type = paramString;
  }

  public int getContent()
  {
    return this.content;
  }

  public void setContent(int paramInt)
  {
    this.content = paramInt;
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    int m;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localElement1 = this.doc.createElement("entry");
      this.body.appendChild(localElement1);
      localObject = XMLTool.newChildElement(this.body, "condition");
      if (this.name != null)
        XMLTool.newChildElement((Element)localObject, "name", this.name);
      if (this.id != null)
        XMLTool.newChildElement((Element)localObject, "id", this.id);
      if (this.type != null)
        XMLTool.newChildElement((Element)localObject, "type", this.type);
      if (this.status > 0)
      {
        localElement2 = XMLTool.newChildElement((Element)localObject, "status");
        j = this.status;
        for (m = 0; m < STATUS_MAP.length; m++)
        {
          if ((j & 0x1) != 0)
            XMLTool.newChildElement(localElement2, "item", STATUS_MAP[m]);
          j >>>= 1;
        }
      }
      Element localElement2 = XMLTool.newChildElement(this.body, "content");
      int j = this.content;
      for (m = 0; m < CONTENT_MAP.length; m++)
      {
        if ((j & 0x1) != 0)
          XMLTool.newChildElement(localElement2, "item", CONTENT_MAP[m]);
        j >>>= 1;
      }
    }
    this.name = XMLTool.getValueByTagName(this.body, "name");
    this.id = XMLTool.getValueByTagName(this.body, "id");
    this.type = XMLTool.getValueByTagName(this.body, "type");
    this.status = 0;
    Element localElement1 = XMLTool.getElementByTagName(this.body, "status");
    int n;
    if (localElement1 != null)
    {
      localObject = localElement1.getElementsByTagName("item");
      for (int i = 0; i < ((NodeList)localObject).getLength(); i++)
      {
        String str1 = XMLTool.getElementValue((Element)((NodeList)localObject).item(i));
        if (str1 == null)
          continue;
        m = 1;
        for (n = 0; n < STATUS_MAP.length; n++)
        {
          if (((this.status & m) == 0) && (str1.equalsIgnoreCase(STATUS_MAP[n])))
            this.status |= m;
          m <<= 1;
        }
      }
    }
    this.content = 0;
    Object localObject = XMLTool.getElementByTagName(this.body, "content");
    if (localObject != null)
    {
      NodeList localNodeList = ((Element)localObject).getElementsByTagName("item");
      for (int k = 0; k < localNodeList.getLength(); k++)
      {
        String str2 = XMLTool.getElementValue((Element)localNodeList.item(k));
        if (str2 == null)
          continue;
        n = 1;
        for (int i1 = 0; i1 < CONTENT_MAP.length; i1++)
        {
          if (((this.content & n) == 0) && (str2.equalsIgnoreCase(CONTENT_MAP[i1])))
            this.content |= n;
          n <<= 1;
        }
      }
    }
  }

  public static class STATUS
  {
    public static final int ALL = 65535;
    public static final int UNUSED = 1;
    public static final int USING = 2;
    public static final int REVOKED = 4;
  }

  public static class CONTENT
  {
    public static final int ALL = 65535;
    public static final int NAME = 1;
    public static final int ID = 2;
    public static final int TYPE = 4;
    public static final int STATUS = 8;
    public static final int DESCRIPTION = 16;
    public static final int POLICY = 32;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.request.CTMLGetRequest
 * JD-Core Version:    0.6.0
 */