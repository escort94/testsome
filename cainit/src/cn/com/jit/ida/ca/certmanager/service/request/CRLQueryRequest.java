package cn.com.jit.ida.ca.certmanager.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;

public class CRLQueryRequest extends Request
{
  public CRLQueryRequest()
  {
    super.setOperation("CRLQUERY");
  }

  public CRLQueryRequest(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("81010704", "证书申请服务 其他错误 请求信息格式不合法", localException);
    }
  }

  public CRLQueryRequest(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("81010704", "证书申请服务 其他错误 请求信息格式不合法", localException);
    }
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.request.CRLQueryRequest
 * JD-Core Version:    0.6.0
 */