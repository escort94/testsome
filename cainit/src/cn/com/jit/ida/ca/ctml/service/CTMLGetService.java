package cn.com.jit.ida.ca.ctml.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.service.request.CTMLGetRequest;
import cn.com.jit.ida.ca.ctml.service.response.CTMLGetResponse;
import cn.com.jit.ida.ca.ctml.service.response.CTMLGetResponse.ResultItem;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import java.util.Vector;

public class CTMLGetService extends CTMLService
{
  private Vector results;
  private String name;
  private String type;
  private String id;
  private int status;
  private int retflag;

  public CTMLGetService()
  {
    super("CTMLGET");
    this.debuger = LogManager.getDebugLogger("cn.com.jit.ida.ca.ctml.service.CTMLGetService");
  }

  void analyseRequest(Request paramRequest)
    throws IDAException
  {
    if (this.isDebug)
      this.debuger.appendMsg_L2("analyse request...");
    super.analyseRequest(paramRequest);
    CTMLGetRequest localCTMLGetRequest = new CTMLGetRequest(paramRequest);
    this.name = localCTMLGetRequest.getName();
    this.type = localCTMLGetRequest.getType();
    this.status = localCTMLGetRequest.getStatus();
    this.id = localCTMLGetRequest.getId();
    this.retflag = localCTMLGetRequest.getContent();
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("condition content:");
    localStringBuffer.append("\n\t name = ");
    localStringBuffer.append(this.name == null ? "null" : this.name);
    localStringBuffer.append("\n\t id = ");
    localStringBuffer.append(this.id == null ? "null" : this.id);
    localStringBuffer.append("\n\t type = ");
    localStringBuffer.append(this.type == null ? "null" : this.type);
    localStringBuffer.append("\n\t status = " + this.status);
    if (this.isDebug)
    {
      this.debuger.appendMsg_L2(localStringBuffer.toString());
      this.debuger.appendMsg_L2("analyse request finish");
    }
  }

  void execute()
    throws IDAException
  {
    this.results = new Vector();
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    String[] arrayOfString = localCTMLManager.getCTMLList();
    InternalConfig localInternalConfig = GlobalConfig.getInstance().getInternalConfig();
    String str = localInternalConfig.getAdminTemplateName();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      CTML localCTML = localCTMLManager.getCTML(arrayOfString[i]);
      if (!agreeCondition(localCTML))
        continue;
      CTMLGetResponse.ResultItem localResultItem = new CTMLGetResponse.ResultItem();
      localResultItem.name = localCTML.getCTMLName();
      if ((this.retflag & 0x2) != 0)
        localResultItem.id = localCTML.getCTMLID();
      if ((this.retflag & 0x4) != 0)
        localResultItem.type = localCTML.getCTMLType();
      if ((this.retflag & 0x8) != 0)
        localResultItem.status = localCTML.getCTMLStatus();
      if ((this.retflag & 0x10) != 0)
        localResultItem.description = localCTML.getCTMLDesc();
      if ((this.retflag & 0x20) != 0)
        localResultItem.policy = localCTML.getCTMLPolicyDesc();
      this.results.add(localResultItem);
    }
    if (this.isDebug)
      this.debuger.appendMsg_L2("get ctml success,ctml count:" + arrayOfString.length);
  }

  Response constructResponse()
  {
    CTMLGetResponse localCTMLGetResponse = new CTMLGetResponse();
    localCTMLGetResponse.setErr("0");
    localCTMLGetResponse.setMsg("SUCCESS");
    localCTMLGetResponse.setResult(this.results.toArray());
    return localCTMLGetResponse;
  }

  private boolean agreeCondition(CTML paramCTML)
  {
    if ((this.name != null) && (!this.name.equalsIgnoreCase(paramCTML.getCTMLName())))
      return false;
    if ((this.type != null) && (!this.type.equalsIgnoreCase(paramCTML.getCTMLType())))
      return false;
    if ((this.status > 0) && (!agreeStatusCondition(paramCTML.getCTMLStatus())))
      return false;
    return (this.id == null) || (this.id.equalsIgnoreCase(paramCTML.getCTMLID()));
  }

  private boolean agreeStatusCondition(String paramString)
  {
    if (((this.status & 0x1) != 0) && ("UNUSED".equalsIgnoreCase(paramString)))
      return true;
    if (((this.status & 0x2) != 0) && ("USING".equalsIgnoreCase(paramString)))
      return true;
    return ((this.status & 0x4) != 0) && ("REVOKED".equalsIgnoreCase(paramString));
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.CTMLGetService
 * JD-Core Version:    0.6.0
 */