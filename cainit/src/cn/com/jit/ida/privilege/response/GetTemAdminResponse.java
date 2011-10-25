package cn.com.jit.ida.privilege.response;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GetTemAdminResponse extends Response
{
  private boolean isRa = false;
  private Vector vector = null;

  public GetTemAdminResponse()
  {
    super.setOperation("PRIVILEGEGETTEMPLATEADMIN");
  }

  public GetTemAdminResponse(byte[] paramArrayOfByte)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80520506", "获取证书业务管理员信息:应答信息格式不合法");
    }
  }

  public GetTemAdminResponse(Response paramResponse)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80520506", "获取证书业务管理员信息:应答信息格式不合法");
    }
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject1;
    Element localElement2;
    Object localObject2;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localObject1 = this.doc.createElement("entry");
      this.body.appendChild((Node)localObject1);
      Element localElement1;
      if (!this.isRa)
      {
        localElement1 = XMLTool.newElement(this.doc, "ISRA", "false");
        ((Element)localObject1).appendChild(localElement1);
      }
      else
      {
        localElement1 = XMLTool.newElement(this.doc, "ISRA", "true");
        ((Element)localObject1).appendChild(localElement1);
      }
      if (this.vector != null)
        for (int i = 0; i < this.vector.size(); i++)
        {
          Vector localVector = (Vector)this.vector.get(i);
          localElement2 = XMLTool.newElement(this.doc, "CtmlName", (String)localVector.get(0));
          localObject2 = XMLTool.newElement(this.doc, "DN", (String)localVector.get(1));
          ((Element)localObject1).appendChild(localElement2);
          ((Element)localObject1).appendChild((Node)localObject2);
        }
    }
    else
    {
      localObject1 = this.body.getElementsByTagName("entry");
      String str1 = XMLTool.getValueByTagName(this.body, "ISRA");
      if (str1.equalsIgnoreCase("true"))
        this.isRa = true;
      else
        this.isRa = false;
      this.vector = new Vector();
      if (((NodeList)localObject1).getLength() != 0)
        for (int j = 0; j < ((NodeList)localObject1).getLength(); j++)
        {
          localElement2 = (Element)((NodeList)localObject1).item(j);
          localObject2 = new Vector();
          String str2 = XMLTool.getValueByTagName(localElement2, "CtmlName");
          ((Vector)localObject2).add(str2);
          String str3 = XMLTool.getValueByTagName(localElement2, "DN");
          ((Vector)localObject2).add(str3);
          this.vector.add(localObject2);
        }
    }
  }

  public Vector getTemAdminInfo()
  {
    return this.vector;
  }

  public void setTemAdminInfo(Vector paramVector)
  {
    this.vector = paramVector;
  }

  public boolean getIsRa()
  {
    return this.isRa;
  }

  public void setIsRa(boolean paramBoolean)
  {
    this.isRa = paramBoolean;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.response.GetTemAdminResponse
 * JD-Core Version:    0.6.0
 */