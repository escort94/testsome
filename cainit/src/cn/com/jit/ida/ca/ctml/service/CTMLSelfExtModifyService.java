package cn.com.jit.ida.ca.ctml.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.ctml.SelfExtensionInformation;
import cn.com.jit.ida.ca.ctml.SelfExtensionManager;
import cn.com.jit.ida.ca.ctml.service.request.CTMLSelfExtModifyRequest;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;

public class CTMLSelfExtModifyService extends CTMLService
{
  private SelfExtensionInformation selfExtInfo;
  private SelfExtensionManager selfExtManager;

  public CTMLSelfExtModifyService()
  {
    super("CTMLSELFEXTMODIFY");
    this.debuger = LogManager.getDebugLogger("cn.com.jit.ida.ca.ctml.CTMLSelfExtDeleteService");
    if (this.isDebug)
      this.debuger.appendMsg_L2("call CTMLSelfExtModifyService construct function");
    this.selfExtManager = SelfExtensionManager.getInstance();
  }

  void analyseRequest(Request paramRequest)
    throws IDAException
  {
    super.analyseRequest(paramRequest);
    CTMLSelfExtModifyRequest localCTMLSelfExtModifyRequest = new CTMLSelfExtModifyRequest(paramRequest);
    String str1 = localCTMLSelfExtModifyRequest.getName();
    String str2 = localCTMLSelfExtModifyRequest.getOid();
    if ((str1 == null) && (str2 == null))
    {
      this.debuger.appendMsg_L1("CTMLSelfExtModifyService Class analyseRequest name = null or oid = null");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.SELFEXTNAMEANDOID_ISNULL);
    }
    if ((str1 != null) && (this.isLog))
      this.logInfo.setObjSubject(str1);
    if ((str2 != null) && (this.isLog))
      this.logInfo.setObjSubject(str2);
    String str3 = localCTMLSelfExtModifyRequest.getEncoding();
    if (str3 == null)
    {
      this.debuger.appendMsg_L1("CTMLSelfExtModifyService Class analyseRequest encoding = null");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.SELFEXTENCODING_ISNULL);
    }
    String str4 = localCTMLSelfExtModifyRequest.getDescription();
    if (str4 == null)
    {
      this.debuger.appendMsg_L1("CTMLSelfExtModifyService Class analyseRequest desc = null");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.SELFEXTDESC_ISNULL);
    }
    this.selfExtInfo = new SelfExtensionInformation();
    this.selfExtInfo.setExtensionName(str1);
    this.selfExtInfo.setExtensionOID(str2);
    this.selfExtInfo.setExtensionEncoding(str3);
    this.selfExtInfo.setExtensionDesc(str4);
    if (this.isDebug)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append("CTMLSelfExtModifyService Class analyseRequest request param");
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
    checkStatus(this.selfExtInfo);
    this.selfExtManager.modifySelfExt(this.selfExtInfo);
    if (this.isDebug)
      this.debuger.appendMsg_L2("CTMLSelfExtModifyService Class execute function use SelfExtensionManager's modifySelfExt ");
  }

  private void checkStatus(SelfExtensionInformation paramSelfExtensionInformation)
    throws IDAException
  {
    SelfExtensionInformation localSelfExtensionInformation = null;
    if (paramSelfExtensionInformation.getExtensionName() != null)
      localSelfExtensionInformation = this.selfExtManager.getSelfExt(paramSelfExtensionInformation.getExtensionName());
    if (paramSelfExtensionInformation.getExtensionOID() != null)
      localSelfExtensionInformation = this.selfExtManager.getSelfExtByOID(paramSelfExtensionInformation.getExtensionOID());
    if (!"UNUSED".equalsIgnoreCase(localSelfExtensionInformation.getExtensionStatus()))
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.CHECKSTATUS.MODIFYSELFEXT_BADSTATUS);
      throw localCTMLServiceException;
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.CTMLSelfExtModifyService
 * JD-Core Version:    0.6.0
 */