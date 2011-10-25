package cn.com.jit.ida.ca.certmanager.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.config.CRLConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.OptLogger;
import java.text.SimpleDateFormat;

public class CertReqOpt
{
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.ca.certmanager.service.operation.CertReqOpt");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private static DBManager db = null;
  private static GlobalConfig gcof = null;
  private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

  public static String[] requestCert(CertInfo paramCertInfo)
    throws CertException
  {
    String[] arrayOfString = new String[2];
    arrayOfString = CodeGenerator.generateCodes();
    paramCertInfo.setCertSN(arrayOfString[0]);
    paramCertInfo.setAuthCode(arrayOfString[1]);
    paramCertInfo.setCertStatus("Undown");
    try
    {
      db = DBManager.getInstance();
      long l1 = 0L;
      long l2 = db.getCertAccount();
      try
      {
        CRLConfig localCRLConfig = CRLConfig.getInstance();
        int j = localCRLConfig.getCertCount();
        l1 = l2 / j + 1L;
        paramCertInfo.setCdpid(l1);
      }
      catch (IDAException localIDAException1)
      {
        throw new CertException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1);
      }
      int i = db.saveCertReq(paramCertInfo);
      CTMLManager localCTMLManager = CTMLManager.getInstance();
      try
      {
        if (localCTMLManager.getCTML(paramCertInfo.getCtmlName()).getCTMLStatus().equals("UNUSED"))
          CTMLManager.initializeInstance();
      }
      catch (IDAException localIDAException2)
      {
        throw new CertException(localIDAException2.getErrCode(), localIDAException2.getErrDesc(), localIDAException2);
      }
    }
    catch (DBException localDBException)
    {
      throw new CertException(localDBException.getErrCode(), localDBException.getErrDesc(), localDBException);
    }
    return arrayOfString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.operation.CertReqOpt
 * JD-Core Version:    0.6.0
 */