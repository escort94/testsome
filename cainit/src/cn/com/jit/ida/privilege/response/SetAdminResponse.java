package cn.com.jit.ida.privilege.response;

import cn.com.jit.ida.Response;
import cn.com.jit.ida.privilege.PrivilegeException;

public class SetAdminResponse extends Response
{
  public SetAdminResponse()
  {
    super.setOperation("PRIVILEGESETADMIN");
  }

  public SetAdminResponse(byte[] paramArrayOfByte)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80430506", "管理员授权:应答信息格式不合法");
    }
  }

  public SetAdminResponse(Response paramResponse)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80430506", "管理员授权:应答信息格式不合法");
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.response.SetAdminResponse
 * JD-Core Version:    0.6.0
 */