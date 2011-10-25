package cn.com.jit.ida.ca.ctml.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException.REQUEST;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CTMLSelfExtGetRequest extends Request
{
  private static final String XMLTAG_CONDITION = "condition";
  private static final String XMLTAG_CONTENT = "content";
  private static final String XMLTAG_NAME = "name";
  private static final String XMLTAG_OID = "oid";
  private static final String XMLTAG_ENCODING = "encoding";
  private static final String XMLTAG_STATUS = "status";
  private static final String XMLTAG_ITEM = "item";
  private static final String XMLTAG_DESCRIPTION = "description";
  private static final String XMLTAG_UNUSED = "unused";
  private static final String XMLTAG_USING = "using";
  private static final String XMLTAG_REVOKED = "revoked";
  private static final String[] CONTENT_MAP = { "name", "oid", "encoding", "status", "description" };
  private static final String[] STATUS_MAP = { "unused", "using", "revoked" };
  private String name;
  private String oid;
  private String encoding;
  private String desc;
  private int content;
  private int status;

  public CTMLSelfExtGetRequest()
  {
    super.setOperation("CTMLSELFEXTGET");
  }

  public CTMLSelfExtGetRequest(Request paramRequest)
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

  public CTMLSelfExtGetRequest(byte[] paramArrayOfByte)
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

  public String getDesc()
  {
    return this.desc;
  }

  public String getEncoding()
  {
    return this.encoding;
  }

  public String getName()
  {
    return this.name;
  }

  public String getOid()
  {
    return this.oid;
  }

  public int getStatus()
  {
    return this.status;
  }

  public void setStatus(int paramInt)
  {
    this.status = paramInt;
  }

  public void setOid(String paramString)
  {
    this.oid = paramString;
  }

  public void setName(String paramString)
  {
    this.name = paramString;
  }

  public void setEncoding(String paramString)
  {
    this.encoding = paramString;
  }

  public void setDesc(String paramString)
  {
    this.desc = paramString;
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
    int i1;
    int i2;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localElement1 = this.doc.createElement("entry");
      this.body.appendChild(localElement1);
      localObject = XMLTool.newChildElement(localElement1, "condition");
      if (this.name != null)
        XMLTool.newChildElement((Element)localObject, "name", this.name);
      if (this.oid != null)
        XMLTool.newChildElement((Element)localObject, "oid", this.oid);
      if (this.encoding != null)
        XMLTool.newChildElement((Element)localObject, "encoding", this.encoding);
      Element localElement2 = XMLTool.newChildElement((Element)localObject, "status");
      int j = this.status;
      for (int m = 0; m < STATUS_MAP.length; m++)
      {
        if ((j & 0x1) != 0)
          XMLTool.newChildElement(localElement2, "item", STATUS_MAP[m]);
        j >>>= 1;
      }
      Element localElement3 = XMLTool.newChildElement(localElement1, "content");
      i1 = this.content;
      for (i2 = 0; i2 < CONTENT_MAP.length; i2++)
      {
        if ((i1 & 0x1) != 0)
          XMLTool.newChildElement(localElement3, "item", CONTENT_MAP[i2]);
        i1 >>>= 1;
      }
    }
    this.name = XMLTool.getValueByTagName(this.body, "name");
    this.oid = XMLTool.getValueByTagName(this.body, "oid");
    this.encoding = XMLTool.getValueByTagName(this.body, "encoding");
    this.status = 0;
    Element localElement1 = XMLTool.getElementByTagName(this.body, "status");
    if (localElement1 != null)
    {
      localObject = localElement1.getElementsByTagName("item");
      for (int i = 0; i < ((NodeList)localObject).getLength(); i++)
      {
        String str1 = XMLTool.getElementValue((Element)((NodeList)localObject).item(i));
        if (str1 == null)
          continue;
        int n = 1;
        for (i1 = 0; i1 < STATUS_MAP.length; i1++)
        {
          if (((this.status & n) == 0) && (str1.equalsIgnoreCase(STATUS_MAP[i1])))
            this.status |= n;
          n <<= 1;
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
        i1 = 1;
        for (i2 = 0; i2 < CONTENT_MAP.length; i2++)
        {
          if (((this.content & i1) == 0) && (str2.equalsIgnoreCase(CONTENT_MAP[i2])))
            this.content |= i1;
          i1 <<= 1;
        }
      }
    }
  }

  public class STATUS
  {
    public static final int ALL = 65535;
    public static final int UNUSED = 1;
    public static final int USING = 2;
    public static final int REVOKED = 4;

    public STATUS()
    {
    }
  }

  public class CONTENT
  {
    public static final int ALL = 65535;
    public static final int NAME = 1;
    public static final int OID = 2;
    public static final int STATUS = 4;
    public static final int ENCODING = 8;
    public static final int DESCRIPTION = 16;

    public CONTENT()
    {
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.request.CTMLSelfExtGetRequest
 * JD-Core Version:    0.6.0
 */