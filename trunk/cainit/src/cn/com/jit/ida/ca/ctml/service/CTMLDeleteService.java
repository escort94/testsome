package cn.com.jit.ida.ca.ctml.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.service.request.CTMLDeleteRequest;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.privilege.TemplateAdmin;

public class CTMLDeleteService extends CTMLService
{
  private String name;
  private String id;

  public CTMLDeleteService()
  {
    super("CTMLDELETE");
    this.debuger = LogManager.getDebugLogger("cn.com.jit.ida.ca.ctml.service.CTMLDeleteService");
  }

  void analyseRequest(Request paramRequest)
    throws IDAException
  {
    if (this.isDebug)
      this.debuger.appendMsg_L2("ananlyseRequest...");
    super.analyseRequest(paramRequest);
    CTMLDeleteRequest localCTMLDeleteRequest = new CTMLDeleteRequest(paramRequest);
    this.name = localCTMLDeleteRequest.getName();
    this.id = localCTMLDeleteRequest.getId();
    if ((this.name == null) && (this.id == null))
    {
      if (this.isDebug)
        this.debuger.appendMsg_L1("invalid request data,name and id all null!");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.CTMLNAME_ISNULL);
    }
    if (this.isLog)
      if (this.name != null)
        this.logInfo.setObjSubject(this.name);
      else
        this.logInfo.setObjSubject("ID=" + this.id);
    if (this.isDebug)
    {
      if (this.name != null)
        this.debuger.appendMsg_L2("name = " + this.name);
      else
        this.debuger.appendMsg_L2("id = " + this.id);
      this.debuger.appendMsg_L2("analyseRequest finish");
    }
  }

  void execute()
    throws IDAException
  {
    checkCTMLStatus();
    checkSystemLimit();
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    if (this.name != null)
      localCTMLManager.deleteCTML(this.name);
    else
      localCTMLManager.deleteCTMLByID(this.id);
    if (this.isDebug)
      this.debuger.appendMsg_L2("delete ctml success!");
  }

  private void checkCTMLStatus()
    throws IDAException
  {
    if (this.isDebug)
      this.debuger.appendMsg_L2("check ctml status");
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CTML localCTML = null;
    if (this.name != null)
      localCTML = localCTMLManager.getCTML(this.name);
    else
      localCTML = localCTMLManager.getCTMLByID(this.id);
    if (!"UNUSED".equals(localCTML.getCTMLStatus()))
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.CHECKSTATUS.CTMLSTATUS_NONDELETE);
      throw localCTMLServiceException;
    }
    if (this.isDebug)
      this.debuger.appendMsg_L2("check ctml status finish,status valid!");
  }

  protected void doOtherUpdate()
  {
    if (this.isDebug)
      this.debuger.appendMsg_L2("start refresh admin info...");
    try
    {
      TemplateAdmin.getInstance().refreshAdminInfo();
    }
    catch (Exception localException)
    {
      if (this.isDebug)
      {
        this.debuger.appendMsg_L1("refresh admin info failed!");
        this.debuger.doLog(localException);
      }
    }
    this.debuger.appendMsg_L2("start refresh admin finish.");
  }

  private void checkSystemLimit()
    throws IDAException
  {
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    CTML localCTML;
    if (this.name != null)
      localCTML = localCTMLManager.getCTML(this.name);
    else
      localCTML = localCTMLManager.getCTMLByID(this.id);
    InternalConfig localInternalConfig = GlobalConfig.getInstance().getInternalConfig();
    String str = localInternalConfig.getAdminTemplateName();
    if (str.equals(localCTML.getCTMLName()))
    {
      localObject = new CTMLServiceException(CTMLServiceException.CHECKSYSLIMIT.ADMINCTML_CANTDELETE);
      throw ((Throwable)localObject);
    }
    Object localObject = localInternalConfig.getCommCertTemplateName();
    if (((String)localObject).equals(localCTML.getCTMLName()))
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.CHECKSYSLIMIT.COMMCTML_CANTDELETE);
      throw localCTMLServiceException;
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.CTMLDeleteService
 * JD-Core Version:    0.6.0
 */