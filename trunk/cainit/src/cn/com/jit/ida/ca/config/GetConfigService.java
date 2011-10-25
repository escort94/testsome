package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.Service;

public class GetConfigService
  implements Service
{
  public Response dealRequest(Request paramRequest)
  {
    return new GetConfigResponse();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.GetConfigService
 * JD-Core Version:    0.6.0
 */