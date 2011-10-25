package cn.com.jit.ida.ca.certmanager.service.response;

import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;

public class CertReqDownResponse extends CertDownloadResponse
{
  public CertReqDownResponse()
  {
    super.setOperation("CERTREQDOWN");
  }

  public CertReqDownResponse(byte[] paramArrayOfByte)
    throws CertException
  {
    super(paramArrayOfByte);
  }

  public CertReqDownResponse(Response paramResponse)
    throws CertException
  {
    super(paramResponse);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.response.CertReqDownResponse
 * JD-Core Version:    0.6.0
 */