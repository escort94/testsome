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

public class GetAdminRoleResponse extends Response
{
  private Vector roles = null;

  public GetAdminRoleResponse()
  {
    super.setOperation("PRIVILEGEGETADMINROLES");
  }

  public GetAdminRoleResponse(byte[] paramArrayOfByte)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80440506", "获取管理员角色信息:应答信息格式不合法");
    }
  }

  public GetAdminRoleResponse(Response paramResponse)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80440506", "获取管理员角色信息:应答信息格式不合法");
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
    return this.roles;
  }

  public void setRoles(Vector paramVector)
  {
    this.roles = paramVector;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.response.GetAdminRoleResponse
 * JD-Core Version:    0.6.0
 */