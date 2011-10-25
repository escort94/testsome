package cn.com.jit.ida.ca.ctml.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.ctml.SelfExtensionInformation;
import cn.com.jit.ida.ca.ctml.SelfExtensionManager;
import cn.com.jit.ida.ca.ctml.service.request.CTMLSelfExtCreateRequest;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;

public class CTMLSelfExtCreateService extends CTMLService
{
  private SelfExtensionInformation selfExtInfo;
  private SelfExtensionManager selfExtManager;

  public CTMLSelfExtCreateService()
  {
    super("CTMLSELFEXTCREATE");
    this.debuger = LogManager.getDebugLogger("cn.com.jit.ida.ca.ctml.CTMLSelfExtCreateService");
    if (this.isLog)
      this.debuger.appendMsg_L2("call CTMLSelfExtCreateService construct function");
  }

  void analyseRequest(Request paramRequest)
    throws IDAException
  {
    if (this.isDebug)
      this.debuger.appendMsg_L2("CTMLSelfExtCreateService Class analyseRequest");
    super.analyseRequest(paramRequest);
    CTMLSelfExtCreateRequest localCTMLSelfExtCreateRequest = new CTMLSelfExtCreateRequest(paramRequest);
    String str1 = localCTMLSelfExtCreateRequest.getName();
    if (str1 == null)
    {
      this.debuger.appendMsg_L1("CTMLSelfExtCreateService Class analyseRequest name = null");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.SELFEXTNAME_ISNULL);
    }
    if (this.isLog)
      this.logInfo.setObjSubject(str1);
    String str2 = localCTMLSelfExtCreateRequest.getOid();
    if (str2 == null)
    {
      this.debuger.appendMsg_L1("CTMLSelfExtCreateService Class analyseRequest oid = null");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.SELFEXTOID_ISNULL);
    }
    String str3 = localCTMLSelfExtCreateRequest.getEncoding();
    if (str3 == null)
    {
      this.debuger.appendMsg_L1("CTMLSelfExtCreateService Class analyseRequest encoding = null");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.SELFEXTENCODING_ISNULL);
    }
    String str4 = localCTMLSelfExtCreateRequest.getDescription();
    if (str4 == null)
    {
      this.debuger.appendMsg_L1("CTMLSelfExtCreateService Class analyseRequest desc = null");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.SELFEXTDESC_ISNULL);
    }
    this.selfExtInfo = new SelfExtensionInformation();
    this.selfExtInfo.setExtensionName(str1);
    this.selfExtInfo.setExtensionOID(str2);
    this.selfExtInfo.setExtensionStatus("UNUSED");
    this.selfExtInfo.setExtensionEncoding(str3);
    this.selfExtInfo.setExtensionDesc(str4);
    if (this.isDebug)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append("CTMLSelfExtCreateService Class analyseRequest request param");
      localStringBuffer.append("\n\t name =");
      localStringBuffer.append(str1);
      localStringBuffer.append("\n\t oid =");
      localStringBuffer.append(str2);
      localStringBuffer.append("\n\t status =");
      localStringBuffer.append("UNUSED");
      localStringBuffer.append("\n\t encoding =");
      localStringBuffer.append(str3);
      localStringBuffer.append("\n\t desc =");
      localStringBuffer.append(str4);
      this.debuger.appendMsg_L2(localStringBuffer.toString());
    }
  }

  void execute()
    throws IDAException
  {
    this.selfExtManager = SelfExtensionManager.getInstance();
    this.selfExtManager.createSelfExt(this.selfExtInfo);
    if (this.isDebug)
      this.debuger.appendMsg_L2("CTMLSelfExtCreateService Class execute function  use  SelfExtensionManager createSelfExt");
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.CTMLSelfExtCreateService
 * JD-Core Version:    0.6.0
 */