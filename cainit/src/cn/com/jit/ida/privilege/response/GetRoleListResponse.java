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
import org.w3c.dom.Text;

public class GetRoleListResponse extends Response
{
  private Vector roles = null;

  public GetRoleListResponse()
  {
    super.setOperation("PRIVILEGEGETROLELIST");
  }

  public GetRoleListResponse(byte[] paramArrayOfByte)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80620506", "获取角色列表:应答信息格式不合法");
    }
  }

  public GetRoleListResponse(Response paramResponse)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80620506", "获取角色列表:应答信息格式不合法");
    }
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject;
    int i;
    Element localElement;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localObject = this.doc.createElement("entry");
      this.body.appendChild((Node)localObject);
      if (this.roles != null)
        for (i = 0; i < this.roles.size(); i++)
        {
          localElement = XMLTool.newElement(this.doc, "Roles", (String)this.roles.get(i));
          ((Element)localObject).appendChild(localElement);
        }
    }
    else
    {
      localObject = this.body.getElementsByTagName("Roles");
      this.roles = new Vector();
      for (i = 0; i < ((NodeList)localObject).getLength(); i++)
      {
        localElement = (Element)((NodeList)localObject).item(i);
        Text localText = (Text)localElement.getFirstChild();
        this.roles.add(localText.getData());
      }
    }
  }

  public Vector getRoles()
  {
    if (this.roles.size() == 4)
    {
      Vector localVector = new Vector(this.roles);
      for (int i = 0; i < 4; i++)
      {
        String str = (String)this.roles.get(i);
        if (str.startsWith("证"))
        {
          localVector.set(0, str);
        }
        else if (str.startsWith("模"))
        {
          localVector.set(1, str);
        }
        else if (str.startsWith("授"))
        {
          localVector.set(2, str);
        }
        else
        {
          if (!str.startsWith("审"))
            continue;
          localVector.set(3, str);
        }
      }
      return localVector;
    }
    return this.roles;
  }

  public void setRoles(Vector paramVector)
  {
    this.roles = paramVector;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.response.GetRoleListResponse
 * JD-Core Version:    0.6.0
 */