package cn.com.jit.ida.ca.ctml.service.response;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException.REQUEST;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CTMLSelfExtGetResponse extends Response
{
  private static final String XMLTAG_NAME = "name";
  private static final String XMLTAG_OID = "oid";
  private static final String XMLTAG_ENCODING = "encoding";
  private static final String XMLTAG_STATUS = "status";
  private static final String XMLTAG_DESCRIPTION = "description";
  private Vector resultContainer;

  public CTMLSelfExtGetResponse()
  {
    super.setOperation("CTMLSELFEXTGET");
  }

  public CTMLSelfExtGetResponse(Response paramResponse)
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

  public CTMLSelfExtGetResponse(byte[] paramArrayOfByte)
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
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      for (int i = 0; i < this.resultContainer.size(); i++)
      {
        Element localElement1 = this.doc.createElement("entry");
        this.body.appendChild(localElement1);
        ResultItem localResultItem1 = (ResultItem)this.resultContainer.get(i);
        if (localResultItem1.name != null)
          XMLTool.newChildElement(localElement1, "name", localResultItem1.name);
        if (localResultItem1.oid != null)
          XMLTool.newChildElement(localElement1, "oid", localResultItem1.oid);
        if (localResultItem1.encoding != null)
          XMLTool.newChildElement(localElement1, "encoding", localResultItem1.encoding);
        if (localResultItem1.status != null)
          XMLTool.newChildElement(localElement1, "status", localResultItem1.status);
        if (localResultItem1.description == null)
          continue;
        XMLTool.newChildElement(localElement1, "description", localResultItem1.description);
      }
    }
    this.resultContainer = new Vector();
    NodeList localNodeList = this.body.getElementsByTagName("entry");
    int j = localNodeList.getLength();
    for (int k = 0; k < j; k++)
    {
      Element localElement2 = (Element)localNodeList.item(k);
      ResultItem localResultItem2 = new ResultItem();
      localResultItem2.name = XMLTool.getValueByTagName(localElement2, "name");
      localResultItem2.oid = XMLTool.getValueByTagName(localElement2, "oid");
      localResultItem2.status = XMLTool.getValueByTagName(localElement2, "status");
      localResultItem2.encoding = XMLTool.getValueByTagName(localElement2, "encoding");
      localResultItem2.description = XMLTool.getValueByTagName(localElement2, "description");
      this.resultContainer.add(localResultItem2);
    }
  }

  public static class ResultItem
  {
    public String name;
    public String oid;
    public String encoding;
    public String status;
    public String description;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.response.CTMLSelfExtGetResponse
 * JD-Core Version:    0.6.0
 */