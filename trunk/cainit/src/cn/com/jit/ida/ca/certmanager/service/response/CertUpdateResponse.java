package cn.com.jit.ida.ca.certmanager.service.response;

import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;

public class CertUpdateResponse extends CertReqResponse
{
  public CertUpdateResponse()
  {
    super.setOperation("CERTUPDATE");
  }

  public CertUpdateResponse(byte[] paramArrayOfByte)
    throws CertException
  {
    super(paramArrayOfByte);
  }

  public CertUpdateResponse(Response paramResponse)
    throws CertException
  {
    super(paramResponse);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.response.CertUpdateResponse
 * JD-Core Version:    0.6.0
 */