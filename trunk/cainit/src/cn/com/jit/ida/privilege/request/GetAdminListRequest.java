package cn.com.jit.ida.privilege.request;

import cn.com.jit.ida.Request;
import cn.com.jit.ida.privilege.PrivilegeException;

public class GetAdminListRequest extends Request
{
  public GetAdminListRequest()
  {
    super.setOperation("PRIVILEGEGETADMINLIST");
  }

  public GetAdminListRequest(Request paramRequest)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80480505", "获取管理员列表:请求信息格式不合法");
    }
  }

  public GetAdminListRequest(byte[] paramArrayOfByte)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80480505", "获取管理员列表:请求信息格式不合法");
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.request.GetAdminListRequest
 * JD-Core Version:    0.6.0
 */