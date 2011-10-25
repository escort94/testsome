package cn.com.jit.ida.ca.ctml.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.ctml.SelfExtensionInformation;
import cn.com.jit.ida.ca.ctml.SelfExtensionManager;
import cn.com.jit.ida.ca.ctml.service.request.CTMLSelfExtGetRequest;
import cn.com.jit.ida.ca.ctml.service.response.CTMLSelfExtGetResponse;
import cn.com.jit.ida.ca.ctml.service.response.CTMLSelfExtGetResponse.ResultItem;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import java.util.Vector;

public class CTMLSelfExtGetService extends CTMLService
{
  private Vector results;
  private String name;
  private String oid;
  private String encoding;
  private int status;
  private int retflag;
  private SelfExtensionManager selfExtManager;

  public CTMLSelfExtGetService()
  {
    super("CTMLSELFEXTGET");
    this.debuger = LogManager.getDebugLogger("cn.com.jit.ida.ca.ctml.CTMLSelfExtGetService");
    if (this.isDebug)
      this.debuger.appendMsg_L2("call CTMLSelfExtGetService construct function");
  }

  void analyseRequest(Request paramRequest)
    throws IDAException
  {
    super.analyseRequest(paramRequest);
    CTMLSelfExtGetRequest localCTMLSelfExtGetRequest = new CTMLSelfExtGetRequest(paramRequest);
    this.name = localCTMLSelfExtGetRequest.getName();
    this.oid = localCTMLSelfExtGetRequest.getOid();
    this.encoding = localCTMLSelfExtGetRequest.getEncoding();
    this.status = localCTMLSelfExtGetRequest.getStatus();
    if (this.isLog)
      this.logInfo.setObjSubject(this.name);
    this.retflag = localCTMLSelfExtGetRequest.getContent();
    if (this.isDebug)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append("CTMLSelfExtGetService class analyseRequest function request param:");
      localStringBuffer.append("\n\t name = ");
      localStringBuffer.append(this.name == null ? "null" : this.name);
      localStringBuffer.append("\n\t oid = ");
      localStringBuffer.append(this.oid == null ? "null" : this.oid);
      localStringBuffer.append("\n\t encoding = ");
      localStringBuffer.append(this.encoding == null ? "null" : this.encoding);
      localStringBuffer.append("\n\t status = ");
      if ((this.status & 0x1) != 0)
        localStringBuffer.append("\t status = UNUSED");
      if ((this.status & 0x2) != 0)
        localStringBuffer.append("\t status = USING");
      if ((this.status & 0x4) != 0)
        localStringBuffer.append("\t status = REVOKED");
      this.debuger.appendMsg_L2(localStringBuffer.toString());
    }
  }

  void execute()
    throws IDAException
  {
    this.selfExtManager = SelfExtensionManager.getInstance();
    String[] arrayOfString = this.selfExtManager.getSelfExtList();
    this.results = new Vector();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      SelfExtensionInformation localSelfExtensionInformation = this.selfExtManager.getSelfExt(arrayOfString[i]);
      if (!agreeCondition(localSelfExtensionInformation))
        continue;
      CTMLSelfExtGetResponse.ResultItem localResultItem = new CTMLSelfExtGetResponse.ResultItem();
      localResultItem.name = localSelfExtensionInformation.getExtensionName();
      if ((this.retflag & 0x2) != 0)
        localResultItem.oid = localSelfExtensionInformation.getExtensionOID();
      if ((this.retflag & 0x4) != 0)
        localResultItem.status = localSelfExtensionInformation.getExtensionStatus();
      if ((this.retflag & 0x8) != 0)
        localResultItem.encoding = localSelfExtensionInformation.getExtensionEncoding();
      if ((this.retflag & 0x10) != 0)
        localResultItem.description = localSelfExtensionInformation.getExtensionDesc();
      this.results.add(localResultItem);
    }
    if (this.isDebug)
      this.debuger.appendMsg_L2("CTMLSelfExtGetService class execute function search result:" + arrayOfString.length);
  }

  Response constructResponse()
  {
    CTMLSelfExtGetResponse localCTMLSelfExtGetResponse = new CTMLSelfExtGetResponse();
    localCTMLSelfExtGetResponse.setErr("0");
    localCTMLSelfExtGetResponse.setMsg("SUCCESS");
    localCTMLSelfExtGetResponse.setResult(this.results.toArray());
    return localCTMLSelfExtGetResponse;
  }

  private boolean agreeCondition(SelfExtensionInformation paramSelfExtensionInformation)
  {
    if ((this.name != null) && (!this.name.equalsIgnoreCase(paramSelfExtensionInformation.getExtensionName())))
      return false;
    if ((this.oid != null) && (!this.oid.equalsIgnoreCase(paramSelfExtensionInformation.getExtensionOID())))
      return false;
    int i = 0;
    if (((this.status & 0x1) != 0) && ("UNUSED".equalsIgnoreCase(paramSelfExtensionInformation.getExtensionStatus())))
      i = 1;
    if (((this.status & 0x2) != 0) && ("USING".equalsIgnoreCase(paramSelfExtensionInformation.getExtensionStatus())))
      i = 1;
    if (((this.status & 0x4) != 0) && ("REVOKED".equalsIgnoreCase(paramSelfExtensionInformation.getExtensionStatus())))
      i = 1;
    if (((i == 0 ? 1 : 0) & (this.status != 0 ? 1 : 0)) != 0)
      return false;
    return (this.encoding == null) || (this.encoding.equalsIgnoreCase(paramSelfExtensionInformation.getExtensionEncoding()));
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.CTMLSelfExtGetService
 * JD-Core Version:    0.6.0
 */