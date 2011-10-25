package cn.com.jit.ida.ca.audit.service.response;

import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.audit.AuditException;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Enumeration;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AuditCertResponse extends Response
{
  private Properties sortAndCount = null;
  private String totalRowCount = null;

  public AuditCertResponse()
  {
    super.setOperation("AUDITCOUNTCERT");
  }

  public AuditCertResponse(Response paramResponse)
    throws AuditException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      throw new AuditException("0001", "通信格式错误 响应信息格式不合法", localException);
    }
  }

  public void AuditCertResponse(byte[] paramArrayOfByte)
    throws AuditException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new AuditException("0001", "通信格式错误 响应信息格式不合法", localException);
    }
  }

  protected void updateBody(boolean paramBoolean)
    throws AuditException
  {
    Object localObject2;
    if (paramBoolean)
    {
      if ((!getErr().equals("0")) || (this.totalRowCount == null))
        return;
      this.body = this.doc.createElement("body");
      localObject1 = XMLTool.newElement(this.doc, "totalCount", this.totalRowCount);
      this.body.appendChild((Node)localObject1);
      if (this.totalRowCount.equals("0"))
        return;
      Enumeration localEnumeration = this.sortAndCount.keys();
      while (localEnumeration.hasMoreElements())
      {
        Element localElement1 = XMLTool.newChildElement(this.body, "entry");
        localObject2 = (String)localEnumeration.nextElement();
        String str = (String)this.sortAndCount.get(localObject2);
        Element localElement2 = XMLTool.newElement(this.doc, "sort", (String)localObject2);
        localElement1.appendChild(localElement2);
        Element localElement3 = XMLTool.newElement(this.doc, "count", str);
        localElement1.appendChild(localElement3);
      }
    }
    this.totalRowCount = XMLTool.getValueByTagName(this.body, "totalCount");
    Object localObject1 = this.body.getElementsByTagName("entry");
    int i = ((NodeList)localObject1).getLength();
    if (i == 0)
      return;
    this.sortAndCount = new Properties();
    for (int j = 0; j < i; j++)
    {
      localObject2 = (Element)((NodeList)localObject1).item(j);
      this.sortAndCount.put(XMLTool.getValueByTagName((Element)localObject2, "sort"), XMLTool.getValueByTagName((Element)localObject2, "count"));
    }
  }

  public Properties getSortAndCount()
  {
    return this.sortAndCount;
  }

  public String getTotalRowCount()
  {
    return this.totalRowCount;
  }

  public void setSortAndCount(Properties paramProperties)
  {
    this.sortAndCount = paramProperties;
  }

  public void setTotalRowCount(String paramString)
  {
    this.totalRowCount = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.audit.service.response.AuditCertResponse
 * JD-Core Version:    0.6.0
 */