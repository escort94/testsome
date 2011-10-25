package cn.com.jit.ida.privilege.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class SetAdminRequest extends Request
{
  private String adminSN = null;
  private String adminDN = null;
  private boolean isRa = false;
  private Vector roles = null;

  public SetAdminRequest()
  {
    super.setOperation("PRIVILEGESETADMIN");
  }

  public SetAdminRequest(Request paramRequest)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80430505", "管理员授权:请求信息格式不合法");
    }
  }

  public SetAdminRequest(byte[] paramArrayOfByte)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80430505", "管理员授权:请求信息格式不合法");
    }
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localObject = this.doc.createElement("entry");
      this.body.appendChild((Node)localObject);
      Element localElement1;
      if (this.adminSN != null)
      {
        localElement1 = XMLTool.newElement(this.doc, "AdminSN", this.adminSN);
        ((Element)localObject).appendChild(localElement1);
      }
      if (this.adminDN != null)
      {
        localElement1 = XMLTool.newElement(this.doc, "AdminDN", this.adminDN);
        ((Element)localObject).appendChild(localElement1);
      }
      if (!this.isRa)
      {
        localElement1 = XMLTool.newElement(this.doc, "ISRA", "false");
        ((Element)localObject).appendChild(localElement1);
      }
      else
      {
        localElement1 = XMLTool.newElement(this.doc, "ISRA", "true");
        ((Element)localObject).appendChild(localElement1);
      }
      if (this.roles != null)
        for (int i = 0; i < this.roles.size(); i++)
        {
          Element localElement2 = XMLTool.newElement(this.doc, "Roles", (String)this.roles.get(i));
          ((Element)localObject).appendChild(localElement2);
        }
    }
    else
    {
      this.adminSN = XMLTool.getValueByTagName(this.body, "AdminSN");
      this.adminDN = XMLTool.getValueByTagName(this.body, "AdminDN");
      localObject = XMLTool.getValueByTagName(this.body, "ISRA");
      if (((String)localObject).equalsIgnoreCase("true"))
        this.isRa = true;
      else
        this.isRa = false;
      NodeList localNodeList = this.body.getElementsByTagName("Roles");
      this.roles = new Vector();
      for (int j = 0; j < localNodeList.getLength(); j++)
      {
        Element localElement3 = (Element)localNodeList.item(j);
        Text localText = (Text)localElement3.getFirstChild();
        this.roles.add(localText.getData());
      }
    }
  }

  public String getAdminSN()
  {
    return this.adminSN;
  }

  public void setAdminSN(String paramString)
  {
    this.adminSN = paramString;
  }

  public Vector getRoles()
  {
    return this.roles;
  }

  public void setRoles(Vector paramVector)
  {
    this.roles = paramVector;
  }

  public String getAdminDN()
  {
    return this.adminDN;
  }

  public void setAdminDN(String paramString)
  {
    this.adminDN = paramString;
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
 * Qualified Name:     cn.com.jit.ida.privilege.request.SetAdminRequest
 * JD-Core Version:    0.6.0
 */