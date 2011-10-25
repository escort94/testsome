package cn.com.jit.ida.privilege.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GetAdminRoleRequest extends Request
{
  private String adminSN = null;
  private String adminDN = null;

  public GetAdminRoleRequest()
  {
    super.setOperation("PRIVILEGEGETADMINROLES");
  }

  public GetAdminRoleRequest(Request paramRequest)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80440505", "获取管理员角色信息:请求信息格式不合法");
    }
  }

  public GetAdminRoleRequest(byte[] paramArrayOfByte)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80440505", "获取管理员角色信息:请求信息格式不合法");
    }
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      Element localElement1 = this.doc.createElement("entry");
      this.body.appendChild(localElement1);
      Element localElement2;
      if (this.adminSN != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "AdminSN", this.adminSN);
        localElement1.appendChild(localElement2);
      }
      if (this.adminDN != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "AdminDN", this.adminDN);
        localElement1.appendChild(localElement2);
      }
    }
    else
    {
      this.adminSN = XMLTool.getValueByTagName(this.body, "AdminSN");
      this.adminDN = XMLTool.getValueByTagName(this.body, "AdminDN");
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

  public String getAdminDN()
  {
    return this.adminDN;
  }

  public void setAdminDN(String paramString)
  {
    this.adminDN = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.request.GetAdminRoleRequest
 * JD-Core Version:    0.6.0
 */