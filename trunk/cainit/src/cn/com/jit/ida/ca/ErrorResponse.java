package cn.com.jit.ida.ca;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;

public class ErrorResponse extends Response
{
  public ErrorResponse()
  {
  }

  public ErrorResponse(Request paramRequest, IDAException paramIDAException)
  {
    setOperation(paramRequest.getOperation());
    setErr(paramIDAException.getErrCode());
    setMsg(paramIDAException.getErrDesc());
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ErrorResponse
 * JD-Core Version:    0.6.0
 */