package cn.com.jit.ida.privilege.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GetTemAdminRequest extends Request
{
  private String temAdminSN = null;
  private String adminDN = null;

  public GetTemAdminRequest()
  {
    super.setOperation("PRIVILEGEGETTEMPLATEADMIN");
  }

  public GetTemAdminRequest(Request paramRequest)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80520505", "获取证书业务管理员信息:请求信息格式不合法");
    }
  }

  public GetTemAdminRequest(byte[] paramArrayOfByte)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80520505", "获取证书业务管理员信息:请求信息格式不合法");
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
      if (this.temAdminSN != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "TemplateAdminSN", this.temAdminSN);
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
      this.temAdminSN = XMLTool.getValueByTagName(this.body, "TemplateAdminSN");
      this.adminDN = XMLTool.getValueByTagName(this.body, "AdminDN");
    }
  }

  public String getTemAdminSN()
  {
    return this.temAdminSN;
  }

  public void setTemAdminSN(String paramString)
  {
    this.temAdminSN = paramString;
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
 * Qualified Name:     cn.com.jit.ida.privilege.request.GetTemAdminRequest
 * JD-Core Version:    0.6.0
 */