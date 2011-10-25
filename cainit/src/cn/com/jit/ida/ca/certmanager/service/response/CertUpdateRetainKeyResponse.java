package cn.com.jit.ida.ca.certmanager.service.response;

import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;

public class CertUpdateRetainKeyResponse extends CertDownloadResponse
{
  public CertUpdateRetainKeyResponse()
  {
    super.setOperation("CERTUPDDOWN");
  }

  public CertUpdateRetainKeyResponse(byte[] paramArrayOfByte)
    throws CertException
  {
    super(paramArrayOfByte);
  }

  public CertUpdateRetainKeyResponse(Response paramResponse)
    throws CertException
  {
    super(paramResponse);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.response.CertUpdateRetainKeyResponse
 * JD-Core Version:    0.6.0
 */