package cn.com.jit.ida.ca.ctml.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException.REQUEST;

public class RACTMLUpdateRequest extends Request
{
  public RACTMLUpdateRequest()
  {
    super.setOperation("RACTMLUPDATE");
  }

  public RACTMLUpdateRequest(Request paramRequest)
    throws IDAException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.REQUEST.PARSE_REQUESTXMLDOC, localException);
      throw localCTMLServiceException;
    }
  }

  public RACTMLUpdateRequest(byte[] paramArrayOfByte)
    throws IDAException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.REQUEST.PARSE_REQUESTXMLDATA, localException);
      throw localCTMLServiceException;
    }
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.request.RACTMLUpdateRequest
 * JD-Core Version:    0.6.0
 */