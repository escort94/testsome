package cn.com.jit.ida.ca.certmanager.service.kmc;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;

public class KMCCertArcResponse extends Response
{
  public static final String KMC_CERTARC = "KMCCERTARC";

  public KMCCertArcResponse()
  {
    super.setOperation("KMCCERTARC");
  }

  public KMCCertArcResponse(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("0704", "其他错误 请求信息格式不合法", localException);
    }
  }

  public KMCCertArcResponse(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("0704", "其他错误 请求信息格式不合法", localException);
    }
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.kmc.KMCCertArcResponse
 * JD-Core Version:    0.6.0
 */