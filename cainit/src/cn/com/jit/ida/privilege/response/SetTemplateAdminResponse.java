package cn.com.jit.ida.privilege.response;

import cn.com.jit.ida.Response;
import cn.com.jit.ida.privilege.PrivilegeException;

public class SetTemplateAdminResponse extends Response
{
  public SetTemplateAdminResponse()
  {
    super.setOperation("PRIVILEGESETTEMPLATEADMIN");
  }

  public SetTemplateAdminResponse(byte[] paramArrayOfByte)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80510506", "证书业务管理员授权:应答信息格式不合法");
    }
  }

  public SetTemplateAdminResponse(Response paramResponse)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80510506", "证书业务管理员授权:应答信息格式不合法");
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.response.SetTemplateAdminResponse
 * JD-Core Version:    0.6.0
 */