package cn.com.jit.ida.ca.certmanager.service.response;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CRLInfo;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CRLQueryResponse extends Response
{
  private Vector resultContainer;

  public CRLQueryResponse()
  {
    super.setOperation("CRLQUERY");
  }

  public CRLQueryResponse(Response paramResponse)
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

  public CRLQueryResponse(byte[] paramArrayOfByte)
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

  public void setResult(Object[] paramArrayOfObject)
  {
    this.resultContainer = new Vector(paramArrayOfObject.length);
    for (int i = 0; i < paramArrayOfObject.length; i++)
      this.resultContainer.add(i, (CRLInfo)paramArrayOfObject[i]);
  }

  public CRLInfo[] getResult()
  {
    CRLInfo[] arrayOfCRLInfo = new CRLInfo[this.resultContainer.size()];
    this.resultContainer.copyInto(arrayOfCRLInfo);
    return arrayOfCRLInfo;
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
        localObject = (CRLInfo)this.resultContainer.get(i);
        if (((CRLInfo)localObject).getCRLName() != null)
          XMLTool.newChildElement(localElement, "CRLName", ((CRLInfo)localObject).getCRLName());
        if (((CRLInfo)localObject).getCRLEntity() == null)
          continue;
        XMLTool.newChildElement(localElement, "CRLEntity", ((CRLInfo)localObject).getCRLEntity());
      }
    }
    this.resultContainer = new Vector();
    NodeList localNodeList = this.body.getElementsByTagName("entry");
    for (int j = 0; j < localNodeList.getLength(); j++)
    {
      localObject = (Element)localNodeList.item(j);
      CRLInfo localCRLInfo = new CRLInfo();
      localCRLInfo.setCRLName(XMLTool.getValueByTagName((Element)localObject, "CRLName"));
      localCRLInfo.setCRLEntity(XMLTool.getValueByTagName((Element)localObject, "CRLEntity"));
      this.resultContainer.add(localCRLInfo);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.response.CRLQueryResponse
 * JD-Core Version:    0.6.0
 */