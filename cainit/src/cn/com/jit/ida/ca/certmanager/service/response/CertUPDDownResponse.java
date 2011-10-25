package cn.com.jit.ida.ca.certmanager.service.response;

import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;

public class CertUPDDownResponse extends CertDownloadResponse
{
  public CertUPDDownResponse()
  {
    super.setOperation("CERTUPDDOWN");
  }

  public CertUPDDownResponse(byte[] paramArrayOfByte)
    throws CertException
  {
    super(paramArrayOfByte);
  }

  public CertUPDDownResponse(Response paramResponse)
    throws CertException
  {
    super(paramResponse);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.response.CertUPDDownResponse
 * JD-Core Version:    0.6.0
 */