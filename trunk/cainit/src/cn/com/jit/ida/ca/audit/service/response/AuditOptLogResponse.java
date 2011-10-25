package cn.com.jit.ida.ca.audit.service.response;

import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.audit.AuditException;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AuditOptLogResponse extends Response
{
  private Operation[] optLogs = null;
  private String totalRowCount = null;

  public AuditOptLogResponse()
  {
    super.setOperation("AUDITQUERYLOG");
  }

  public AuditOptLogResponse(Response paramResponse)
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

  public AuditOptLogResponse(byte[] paramArrayOfByte)
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
    Element localElement1;
    Object localObject3;
    String str;
    Object localObject4;
    if (paramBoolean)
    {
      if ((!getErr().equals("0")) || (this.totalRowCount == null))
        return;
      this.body = this.doc.createElement("body");
      localObject1 = XMLTool.newElement(this.doc, "totalCount", this.totalRowCount);
      this.body.appendChild((Node)localObject1);
      for (i = 0; i < this.optLogs.length; i++)
      {
        Operation localOperation = this.optLogs[i];
        localObject2 = XMLTool.newElement(this.doc, "entry");
        this.body.appendChild((Node)localObject2);
        localElement1 = XMLTool.newElement(this.doc, "operatorSN", localOperation.getOperatorSN());
        ((Element)localObject2).appendChild(localElement1);
        localObject3 = XMLTool.newElement(this.doc, "operatorSubject", localOperation.getOperatorDN());
        ((Element)localObject2).appendChild((Node)localObject3);
        str = localOperation.getObjCertSN();
        if ((str != null) && (str.trim().length() != 0))
        {
          localObject4 = XMLTool.newElement(this.doc, "objectCertSN", str);
          ((Element)localObject2).appendChild((Node)localObject4);
        }
        localObject4 = localOperation.getObjSubject();
        if ((localObject4 != null) && (((String)localObject4).trim().length() != 0))
        {
          localObject5 = XMLTool.newElement(this.doc, "objectSubject", (String)localObject4);
          ((Element)localObject2).appendChild((Node)localObject5);
        }
        Object localObject5 = localOperation.getObjCTMLName();
        if ((localObject5 != null) && (((String)localObject5).trim().length() != 0))
        {
          localElement2 = XMLTool.newElement(this.doc, "objectCTMLName", (String)localObject5);
          ((Element)localObject2).appendChild(localElement2);
        }
        Element localElement2 = XMLTool.newElement(this.doc, "optType", localOperation.getOptType());
        ((Element)localObject2).appendChild(localElement2);
        Element localElement3 = XMLTool.newElement(this.doc, "optTime", Long.toString(localOperation.getOptTime()));
        ((Element)localObject2).appendChild(localElement3);
        Element localElement4 = XMLTool.newElement(this.doc, "result", Integer.toString(localOperation.getResult()));
        ((Element)localObject2).appendChild(localElement4);
      }
    }
    this.totalRowCount = XMLTool.getValueByTagName(this.body, "totalCount");
    Object localObject1 = this.body.getElementsByTagName("entry");
    int i = ((NodeList)localObject1).getLength();
    if (i == 0)
      return;
    this.optLogs = new Operation[i];
    for (int j = 0; j < i; j++)
    {
      localObject2 = new Operation();
      localElement1 = (Element)((NodeList)localObject1).item(j);
      ((Operation)localObject2).setOperatorSN(XMLTool.getValueByTagName(localElement1, "operatorSN"));
      ((Operation)localObject2).setOperatorDN(XMLTool.getValueByTagName(localElement1, "operatorSubject"));
      localObject3 = XMLTool.getValueByTagName(localElement1, "objectCertSN");
      if ((localObject3 != null) && (((String)localObject3).trim().length() != 0))
        ((Operation)localObject2).setObjCertSN((String)localObject3);
      str = XMLTool.getValueByTagName(localElement1, "objectSubject");
      if ((str != null) && (str.trim().length() != 0))
        ((Operation)localObject2).setObjSubject(str);
      localObject4 = XMLTool.getValueByTagName(localElement1, "objectCTMLName");
      if ((localObject4 != null) && (((String)localObject4).trim().length() != 0))
        ((Operation)localObject2).setObjCTMLName((String)localObject4);
      ((Operation)localObject2).setOptType(XMLTool.getValueByTagName(localElement1, "optType"));
      ((Operation)localObject2).setOptTime(Long.parseLong(XMLTool.getValueByTagName(localElement1, "optTime")));
      ((Operation)localObject2).setResult(Integer.parseInt(XMLTool.getValueByTagName(localElement1, "result")));
      this.optLogs[j] = localObject2;
    }
  }

  public Operation[] getOptLogs()
  {
    return this.optLogs;
  }

  public String getTotalRowCount()
  {
    return this.totalRowCount;
  }

  public void setOptLogs(Operation[] paramArrayOfOperation)
  {
    this.optLogs = paramArrayOfOperation;
  }

  public void setTotalRowCount(String paramString)
  {
    this.totalRowCount = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.audit.service.response.AuditOptLogResponse
 * JD-Core Version:    0.6.0
 */