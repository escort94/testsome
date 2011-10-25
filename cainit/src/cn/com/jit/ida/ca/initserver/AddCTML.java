package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.Service;
import cn.com.jit.ida.ServiceFactory;
import cn.com.jit.ida.ca.ctml.CTMLInformation;
import cn.com.jit.ida.ca.ctml.service.request.CTMLCreateRequest;

public class AddCTML
{
  CTMLCreateRequest request;
  Response response;
  CTMLInformation ctmlInfo;

  public void run()
    throws IDAException
  {
    makeReq();
    Service localService = null;
    localService = ServiceFactory.newServiceInstance(this.request.getOperation());
    localService.dealRequest(this.request);
  }

  protected void makeReq()
    throws IDAException
  {
    this.request = new CTMLCreateRequest();
    this.ctmlInfo = new CTMLInformation();
    Operator localOperator = new Operator();
    localOperator.setOperatorDN("系统管理员");
    localOperator.setOperatorSN("系统管理员");
    this.request.setOperator(localOperator);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.AddCTML
 * JD-Core Version:    0.6.0
 */