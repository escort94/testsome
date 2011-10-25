package cn.com.jit.ida.ca.ctml.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.ctml.SelfExtensionInformation;
import cn.com.jit.ida.ca.ctml.SelfExtensionManager;
import cn.com.jit.ida.ca.ctml.service.request.CTMLSelfExtDeleteRequest;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;

public class CTMLSelfExtDeleteService extends CTMLService
{
  private SelfExtensionInformation selfExtInfo;
  private SelfExtensionManager selfExtManager;
  private String name;
  private String oid;

  public CTMLSelfExtDeleteService()
  {
    super("CTMLSELFEXTDELETE");
    this.debuger = LogManager.getDebugLogger("cn.com.jit.ida.ca.ctml.CTMLSelfExtDeleteService");
    if (this.isDebug)
      this.debuger.appendMsg_L2("call CTMLSelfExtDeleteService construct function");
    this.selfExtManager = SelfExtensionManager.getInstance();
  }

  void analyseRequest(Request paramRequest)
    throws IDAException
  {
    super.analyseRequest(paramRequest);
    CTMLSelfExtDeleteRequest localCTMLSelfExtDeleteRequest = new CTMLSelfExtDeleteRequest(paramRequest);
    this.name = localCTMLSelfExtDeleteRequest.getName();
    this.oid = localCTMLSelfExtDeleteRequest.getOid();
    if ((this.name == null) && (this.oid == null))
    {
      this.debuger.appendMsg_L1("CTMLSelfExtCreateService Class analyseRequest name = null or oid = null");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.SELFEXTNAMEANDOID_ISNULL);
    }
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("CTMLSelfExtCreateService Class analyseRequest function request param");
    if (this.name != null)
    {
      if (this.isLog)
        this.logInfo.setObjSubject(this.name);
      localStringBuffer.append("\n\t name=");
      localStringBuffer.append(this.name);
    }
    if (this.oid != null)
    {
      if (this.isLog)
        this.logInfo.setObjSubject(this.name);
      localStringBuffer.append("\n\t oid=");
      localStringBuffer.append(this.oid);
    }
    if (this.isDebug)
      this.debuger.appendMsg_L1(localStringBuffer.toString());
  }

  void execute()
    throws IDAException
  {
    checkStatus();
    if (this.name != null)
    {
      this.selfExtManager.deleteSelfExt(this.name);
      if (this.isDebug)
        this.debuger.appendMsg_L2("CTMLSelfExtDeleteService Class execute function use SelfExtensionManager's deleteSelfExt ");
      return;
    }
    if (this.oid != null)
    {
      this.selfExtManager.deleteSelfExtByOID(this.oid);
      if (this.isDebug)
        this.debuger.appendMsg_L2("CTMLSelfExtDeleteService Class execute function use SelfExtensionManager's deleteSelfExtByOID ");
    }
  }

  private void checkStatus()
    throws IDAException
  {
    SelfExtensionInformation localSelfExtensionInformation = null;
    if (this.name != null)
      localSelfExtensionInformation = this.selfExtManager.getSelfExt(this.name);
    if (this.oid != null)
      localSelfExtensionInformation = this.selfExtManager.getSelfExtByOID(this.oid);
    if (!"UNUSED".equalsIgnoreCase(localSelfExtensionInformation.getExtensionStatus()))
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.CHECKSTATUS.DELETESELFEXT_BADSTATUS);
      throw localCTMLServiceException;
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.CTMLSelfExtDeleteService
 * JD-Core Version:    0.6.0
 */