package cn.com.jit.ida.ca;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.Service;
import cn.com.jit.ida.ServiceFactory;
import cn.com.jit.ida.privilege.Privilege;

public class Dispenser
{
  static Privilege privailege;

  public Dispenser()
  {
    try
    {
      privailege = Privilege.getInstance();
    }
    catch (Exception localException)
    {
    }
  }

  public static Response dealRequest(Request paramRequest)
  {
    Operator localOperator = paramRequest.getOperator();
    String str = paramRequest.getOperation();
    boolean bool = true;
    IDAException localIDAException2;
    try
    {
      if (privailege == null)
        privailege = Privilege.getInstance();
      bool = privailege.isPass(localOperator.getOperatorSN(), str);
    }
    catch (Exception localException)
    {
      if ((localException instanceof IDAException))
        return new ErrorResponse(paramRequest, (IDAException)localException);
      localIDAException2 = new IDAException("80000099", "权限验证失败,请与系统管理员联系");
      return new ErrorResponse(paramRequest, localIDAException2);
    }
    try
    {
      if (bool)
        return ServiceFactory.newServiceInstance(str).dealRequest(paramRequest);
      IDAException localIDAException1 = new IDAException("80000098", "您没有权限做此操作，如有问题，请与系统管理员联系");
      return new ErrorResponse(paramRequest, localIDAException1);
    }
    catch (Throwable localThrowable)
    {
      localIDAException2 = new IDAException("80009999", "系统发生严重错误，建议重新启动服务器");
    }
    return new ErrorResponse(paramRequest, localIDAException2);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.Dispenser
 * JD-Core Version:    0.6.0
 */