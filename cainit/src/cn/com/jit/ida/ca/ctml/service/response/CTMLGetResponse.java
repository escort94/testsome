package cn.com.jit.ida.ca.ctml.service.response;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException.REQUEST;
import cn.com.jit.ida.ca.ctml.util.Base64;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CTMLGetResponse extends Response
{
  private static final String XMLTAG_NAME = "name";
  private static final String XMLTAG_ID = "id";
  private static final String XMLTAG_TYPE = "type";
  private static final String XMLTAG_STATUS = "status";
  private static final String XMLTAG_DESCRIPTION = "description";
  private static final String XMLTAG_POLICY = "policy";
  private Vector resultContainer;

  public CTMLGetResponse()
  {
    super.setOperation("CTMLGET");
  }

  public CTMLGetResponse(Response paramResponse)
    throws IDAException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.REQUEST.PARSE_REQUESTXMLDOC, localException);
      throw localCTMLServiceException;
    }
  }

  public CTMLGetResponse(byte[] paramArrayOfByte)
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

  public void setResult(Object[] paramArrayOfObject)
  {
    this.resultContainer = new Vector(paramArrayOfObject.length);
    for (int i = 0; i < paramArrayOfObject.length; i++)
      this.resultContainer.add(i, (ResultItem)paramArrayOfObject[i]);
  }

  public ResultItem[] getResult()
  {
    ResultItem[] arrayOfResultItem = new ResultItem[this.resultContainer.size()];
    this.resultContainer.copyInto(arrayOfResultItem);
    return arrayOfResultItem;
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      for (int i = 0; i < this.resultContainer.size(); i++)
      {
        Element localElement = this.doc.createElement("entry");
        this.body.appendChild(localElement);
        localObject = (ResultItem)this.resultContainer.get(i);
        if (((ResultItem)localObject).name != null)
          XMLTool.newChildElement(localElement, "name", ((ResultItem)localObject).name);
        if (((ResultItem)localObject).id != null)
          XMLTool.newChildElement(localElement, "id", ((ResultItem)localObject).id);
        if (((ResultItem)localObject).type != null)
          XMLTool.newChildElement(localElement, "type", ((ResultItem)localObject).type);
        if (((ResultItem)localObject).status != null)
          XMLTool.newChildElement(localElement, "status", ((ResultItem)localObject).status);
        if (((ResultItem)localObject).description != null)
          XMLTool.newChildElement(localElement, "description", ((ResultItem)localObject).description);
        if (((ResultItem)localObject).policy == null)
          continue;
        XMLTool.newChildElement(localElement, "policy", Base64.byteArrayToBase64(((ResultItem)localObject).policy));
      }
    }
    this.resultContainer = new Vector();
    NodeList localNodeList = this.body.getElementsByTagName("entry");
    for (int j = 0; j < localNodeList.getLength(); j++)
    {
      localObject = (Element)localNodeList.item(j);
      ResultItem localResultItem = new ResultItem();
      localResultItem.name = XMLTool.getValueByTagName((Element)localObject, "name");
      localResultItem.id = XMLTool.getValueByTagName((Element)localObject, "id");
      localResultItem.type = XMLTool.getValueByTagName((Element)localObject, "type");
      localResultItem.status = XMLTool.getValueByTagName((Element)localObject, "status");
      localResultItem.description = XMLTool.getValueByTagName((Element)localObject, "description");
      String str = XMLTool.getValueByTagName((Element)localObject, "policy");
      if (str != null)
        localResultItem.policy = Base64.base64ToByteArray(str);
      this.resultContainer.add(localResultItem);
    }
  }

  public static class ResultItem
  {
    public String name;
    public String id;
    public String status;
    public String type;
    public String description;
    public byte[] policy;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.response.CTMLGetResponse
 * JD-Core Version:    0.6.0
 */