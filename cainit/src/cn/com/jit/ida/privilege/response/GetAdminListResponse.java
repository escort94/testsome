package cn.com.jit.ida.privilege.response;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.web.privilege.AdminInfo;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GetAdminListResponse extends Response
{
  private Vector admins = null;

  public GetAdminListResponse()
  {
    super.setOperation("PRIVILEGEGETADMINLIST");
  }

  public GetAdminListResponse(byte[] paramArrayOfByte)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80480506", "获取管理员列表:应答信息格式不合法");
    }
  }

  public GetAdminListResponse(Response paramResponse)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80480506", "获取管理员列表:应答信息格式不合法");
    }
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Element localElement;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      if (this.admins != null)
        for (int i = 0; i < this.admins.size(); i++)
        {
          AdminInfo localAdminInfo = (AdminInfo)this.admins.get(i);
          localElement = this.doc.createElement("entry");
          this.body.appendChild(localElement);
          localObject1 = XMLTool.newElement(this.doc, "AdminSN", localAdminInfo.getSn());
          localObject2 = XMLTool.newElement(this.doc, "DN", localAdminInfo.getDn());
          localObject3 = XMLTool.newElement(this.doc, "Roles", localAdminInfo.getRoleinfo());
          localElement.appendChild((Node)localObject1);
          localElement.appendChild((Node)localObject2);
          localElement.appendChild((Node)localObject3);
        }
    }
    else
    {
      NodeList localNodeList = this.body.getElementsByTagName("entry");
      this.admins = new Vector();
      if (localNodeList.getLength() != 0)
        for (int j = 0; j < localNodeList.getLength(); j++)
        {
          localElement = (Element)localNodeList.item(j);
          localObject1 = new AdminInfo();
          localObject2 = XMLTool.getValueByTagName(localElement, "AdminSN");
          ((AdminInfo)localObject1).setSn((String)localObject2);
          localObject3 = XMLTool.getValueByTagName(localElement, "DN");
          ((AdminInfo)localObject1).setDn((String)localObject3);
          String str = XMLTool.getValueByTagName(localElement, "Roles");
          ((AdminInfo)localObject1).setRoleinfo(str);
          this.admins.add(localObject1);
        }
    }
  }

  public Vector getAdmins()
  {
    return this.admins;
  }

  public void setAdmins(Vector paramVector)
  {
    this.admins = paramVector;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.response.GetAdminListResponse
 * JD-Core Version:    0.6.0
 */