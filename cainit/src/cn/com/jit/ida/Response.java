package cn.com.jit.ida;

import cn.com.jit.ida.util.xml.XMLTool;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Response extends Protocol
{
  public static final String ERROR = "err";
  public static final String MESSAGE = "msg";
  public static final String SUCCESS = "success";
  public static final String DETAIL = "detail";
  private static final String type = "response";
  private String operation;
  private String err;
  private String msg;
  private ArrayList detail = new ArrayList();

  public String getOperation()
  {
    return this.operation;
  }

  public void setOperation(String paramString)
  {
    this.operation = paramString;
  }

  public String getErr()
  {
    return this.err;
  }

  public void setErr(String paramString)
  {
    this.err = paramString;
  }

  public String getMsg()
  {
    return this.msg;
  }

  public void setMsg(String paramString)
  {
    this.msg = paramString;
  }

  public void appendDetail(String paramString)
  {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = paramString;
    this.detail.add(arrayOfString);
  }

  public void appendDetail(String paramString1, String paramString2)
  {
    String[] arrayOfString = new String[2];
    arrayOfString[0] = paramString1;
    arrayOfString[1] = paramString2;
    this.detail.add(arrayOfString);
  }

  public void appendDetail(Exception paramException)
  {
    if (paramException == null)
      return;
    if ((paramException instanceof IDAException))
    {
      appendDetail(((IDAException)paramException).getErrCode(), ((IDAException)paramException).getErrDescEx());
      appendDetail(((IDAException)paramException).getHistory());
    }
    else if (paramException.getMessage() != null)
    {
      appendDetail(paramException.getMessage());
    }
  }

  public String[][] getDetail()
  {
    if (this.detail.size() == 0)
      return (String[][])null;
    return (String[][])this.detail.toArray(new String[0][]);
  }

  protected final void updateHeader(boolean paramBoolean)
    throws IDAException
  {
    Element localElement1;
    Object localObject1;
    Object localObject2;
    if (paramBoolean)
    {
      this.header = this.doc.createElement("header");
      localElement1 = XMLTool.newElement(this.doc, "type", "response");
      this.header.appendChild(localElement1);
      localObject1 = XMLTool.newElement(this.doc, "operation", this.operation);
      this.header.appendChild((Node)localObject1);
      Element localElement2;
      if (this.err != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "err", this.err);
        this.header.appendChild(localElement2);
      }
      if (this.msg != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "msg", this.msg);
        this.header.appendChild(localElement2);
      }
      if (this.detail.size() > 0)
      {
        localElement2 = XMLTool.newElement(this.doc, "detail");
        this.header.appendChild(localElement2);
        for (int j = 0; j < this.detail.size(); j++)
        {
          localObject2 = (String[])this.detail.get(j);
          Element localElement3 = XMLTool.newElement(this.doc, "entry");
          localElement2.appendChild(localElement3);
          Element localElement4;
          if (localObject2.length == 1)
          {
            localElement4 = XMLTool.newElement(this.doc, "msg", localObject2[0]);
            localElement3.appendChild(localElement4);
          }
          else
          {
            localElement4 = XMLTool.newElement(this.doc, "err", localObject2[0]);
            Element localElement5 = XMLTool.newElement(this.doc, "msg", localObject2[1]);
            localElement3.appendChild(localElement4);
            localElement3.appendChild(localElement5);
          }
        }
      }
    }
    else
    {
      this.operation = XMLTool.getValueByTagName(this.header, "operation");
      this.err = XMLTool.getValueByTagName(this.header, "err");
      if (this.err == null)
        this.err = "0";
      this.msg = XMLTool.getValueByTagName(this.header, "msg");
      if (this.msg == null)
        this.msg = "success";
      this.detail = new ArrayList();
      localElement1 = XMLTool.getElementByTagName(this.header, "detail");
      if (localElement1 != null)
      {
        localObject1 = localElement1.getElementsByTagName("entry");
        for (int i = 0; i < ((NodeList)localObject1).getLength(); i++)
        {
          String str = XMLTool.getValueByTagName((Element)((NodeList)localObject1).item(i), "msg");
          localObject2 = XMLTool.getValueByTagName((Element)((NodeList)localObject1).item(i), "err");
          if (str == null)
            continue;
          if (localObject2 == null)
            appendDetail(str);
          else
            appendDetail((String)localObject2, str);
        }
      }
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
 * Qualified Name:     cn.com.jit.ida.Response
 * JD-Core Version:    0.6.0
 */