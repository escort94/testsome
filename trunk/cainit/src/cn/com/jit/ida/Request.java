package cn.com.jit.ida;

import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Request extends Protocol
{
  private Operator operator;
  private String operation;
  private static final String type = "request";

  public Request()
  {
  }

  public Request(byte[] paramArrayOfByte)
    throws IDAException
  {
    super.setData(paramArrayOfByte);
  }

  public Operator getOperator()
  {
    return this.operator;
  }

  public void setOperator(Operator paramOperator)
  {
    this.operator = paramOperator;
  }

  public Element getBody()
  {
    return this.body;
  }

  public String getOperation()
  {
    return this.operation;
  }

  public void setOperation(String paramString)
  {
    this.operation = paramString;
  }

  public void setBody(Element paramElement)
  {
    this.body = paramElement;
  }

  protected final void updateHeader(boolean paramBoolean)
    throws IDAException
  {
    if (paramBoolean)
    {
      this.header = this.doc.createElement("header");
      Element localElement1 = XMLTool.newElement(this.doc, "type", "request");
      this.header.appendChild(localElement1);
      Element localElement2 = XMLTool.newElement(this.doc, "operation", this.operation);
      this.header.appendChild(localElement2);
    }
    else
    {
      this.operation = XMLTool.getValueByTagName(this.header, "operation");
    }
  }

  protected void updateBody(boolean paramBoolean)
    throws IDAException
  {
    if (this.body != null)
      this.body = ((Element)this.doc.importNode(this.body, true));
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.Request
 * JD-Core Version:    0.6.0
 */