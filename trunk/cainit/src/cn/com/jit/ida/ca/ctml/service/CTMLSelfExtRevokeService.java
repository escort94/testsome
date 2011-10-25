package cn.com.jit.ida.ca.ctml.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.ctml.SelfExtensionInformation;
import cn.com.jit.ida.ca.ctml.SelfExtensionManager;
import cn.com.jit.ida.ca.ctml.service.request.CTMLSelfExtRevokeRequest;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;

public class CTMLSelfExtRevokeService extends CTMLService
{
  private SelfExtensionInformation selfExtInfo;
  private SelfExtensionManager selfExtManager = SelfExtensionManager.getInstance();
  private String name;
  private String oid;

  public CTMLSelfExtRevokeService()
  {
    super("CTMLSELFEXTREVOKE");
    this.debuger = LogManager.getDebugLogger("cn.com.jit.ida.ca.ctml.CTMLSelfExtRevokeService");
    if (this.isDebug)
      this.debuger.appendMsg_L2("call CTMLSelfExtRevokeService construct function");
  }

  void analyseRequest(Request paramRequest)
    throws IDAException
  {
    super.analyseRequest(paramRequest);
    CTMLSelfExtRevokeRequest localCTMLSelfExtRevokeRequest = new CTMLSelfExtRevokeRequest(paramRequest);
    this.name = localCTMLSelfExtRevokeRequest.getName();
    this.oid = localCTMLSelfExtRevokeRequest.getOid();
    if ((this.name == null) && (this.oid == null))
    {
      this.debuger.appendMsg_L1("CTMLSelfExtRevokeService Class analyseRequest name = null or oid = null");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.SELFEXTNAMEANDOID_ISNULL);
    }
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("CTMLSelfExtRevokeService Class analyseRequest function request param");
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
    this.selfExtManager.updateSelfExtStatus(this.name, "REVOKED");
    if (this.isDebug)
    {
      this.debuger.appendMsg_L2("CTMLSelfExtRevokeService Class execute function update status finish!!!");
      this.debuger.doLog();
    }
  }

  public void checkStatus()
    throws IDAException
  {
    SelfExtensionInformation localSelfExtensionInformation = null;
    if (this.name != null)
      localSelfExtensionInformation = this.selfExtManager.getSelfExt(this.name);
    else
      localSelfExtensionInformation = this.selfExtManager.getSelfExtByOID(this.oid);
    if (!"USING".equals(localSelfExtensionInformation.getExtensionStatus()))
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.CHECKSTATUS.REVOKESELFEXT_BADSTATUS);
      this.debuger.doLog(localCTMLServiceException);
      throw localCTMLServiceException;
    }
    this.name = localSelfExtensionInformation.getExtensionName();
    if (this.isDebug)
    {
      this.debuger.appendMsg_L2("check SelfExt status finish,status valid!");
      this.debuger.doLog();
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.CTMLSelfExtRevokeService
 * JD-Core Version:    0.6.0
 */