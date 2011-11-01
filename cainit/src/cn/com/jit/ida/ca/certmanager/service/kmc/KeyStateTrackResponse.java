package cn.com.jit.ida.ca.certmanager.service.kmc;

import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;

public class KeyStateTrackResponse extends Response
{
  private static final String KMC_KEYSTATETRACK = "KEYSTATETRACK";

  public KeyStateTrackResponse()
  {
    super.setOperation("KEYSTATETRACK");
  }

  public KeyStateTrackResponse(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("0705", "其他错误 应答信息格式不合法", localException);
    }
  }

  public KeyStateTrackResponse(Response paramResponse)
    throws CertException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("0705", "其他错误 应答信息格式不合法", localException);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.kmc.KeyStateTrackResponse
 * JD-Core Version:    0.6.0
 */