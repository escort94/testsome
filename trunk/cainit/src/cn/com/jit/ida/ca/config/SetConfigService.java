package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.Service;
import cn.com.jit.ida.ca.initserver.InitServerException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import java.io.PrintStream;
import java.util.Properties;

public class SetConfigService
  implements Service
{
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.ca.config.SetConfigService");

  public Response dealRequest(Request paramRequest)
  {
    SetConfigResponse localSetConfigResponse = new SetConfigResponse();
    try
    {
      setConfig(paramRequest);
      localSetConfigResponse.setErr("0");
      localSetConfigResponse.setMsg("success");
      return localSetConfigResponse;
    }
    catch (IDAException localIDAException)
    {
      this.debugLogger.appendMsg_L1(localIDAException.toString());
      InitServerException localInitServerException = new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc());
      localSetConfigResponse.setErr(localInitServerException.getErrCode());
      localSetConfigResponse.setMsg(localInitServerException.getErrDesc());
      System.err.println(localInitServerException.toString());
      this.debugLogger.doLog();
    }
    return localSetConfigResponse;
  }

  private void setConfig(Request paramRequest)
    throws IDAException
  {
    SetConfigRequest localSetConfigRequest = (SetConfigRequest)paramRequest;
    Properties localProperties = localSetConfigRequest.getProperties();
    GlobalConfig localGlobalConfig = null;
    localGlobalConfig = GlobalConfig.getInstance();
    CAConfig localCAConfig = localGlobalConfig.getCAConfig();
    String str = localProperties.getProperty("AuthorityInfoAccess");
    if (!localCAConfig.getAuthorityInformation().equalsIgnoreCase(str))
      localCAConfig.setAuthorityInformation(str);
    int i = Integer.parseInt(localProperties.getProperty("TimeDifAllow")) * 60000;
    if (localCAConfig.getTimeDifAllow() != i)
      localCAConfig.setTimeDifAllow(i);
    str = localProperties.getProperty("isSendAuthCode");
    if (!localCAConfig.getIsSendAuthCode().equalsIgnoreCase(str))
      localCAConfig.setIsSendAuthCode(str);
    CRLConfig localCRLConfig = localGlobalConfig.getCrlConfig();
    long l = Long.parseLong(localProperties.getProperty("CRLPeriods")) * 60000L;
    if (localCRLConfig.getPeriods() != l)
      localCRLConfig.setPeriods(l);
    str = localProperties.getProperty("CRLPubAddress");
    if (!ConfigTool.Array2String(localCRLConfig.getCRLPubAddress()).equalsIgnoreCase(str))
      localCRLConfig.setCRLPubAddress(ConfigTool.String2Array(str));
    LOGConfig localLOGConfig = localGlobalConfig.getLOGConfig();
    str = localProperties.getProperty("isSendAuthCode");
    if (!localLOGConfig.getLOG_Path().equalsIgnoreCase(str))
      localLOGConfig.setLOG_Path(str);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.SetConfigService
 * JD-Core Version:    0.6.0
 */