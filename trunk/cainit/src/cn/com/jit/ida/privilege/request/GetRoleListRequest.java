package cn.com.jit.ida.privilege.request;

import cn.com.jit.ida.Request;
import cn.com.jit.ida.privilege.PrivilegeException;

public class GetRoleListRequest extends Request
{
  public GetRoleListRequest()
  {
    super.setOperation("PRIVILEGEGETROLELIST");
  }

  public GetRoleListRequest(Request paramRequest)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80620505", "获取角色列表:请求信息格式不合法");
    }
  }

  public GetRoleListRequest(byte[] paramArrayOfByte)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80620505", "获取角色列表:请求信息格式不合法");
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.request.GetRoleListRequest
 * JD-Core Version:    0.6.0
 */