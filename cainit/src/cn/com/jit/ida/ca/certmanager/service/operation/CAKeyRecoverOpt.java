package cn.com.jit.ida.ca.certmanager.service.operation;

import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.OptLogger;

public class CAKeyRecoverOpt
{
  private DebugLogger debugLogger = LogManager.getDebugLogger("cn.com.jit.ida.ca.certmanager.service.operation.CAKeyRecoverOpt");
  private OptLogger optLogger = LogManager.getOPtLogger();
  private static DBManager db = null;

  public static CertInfo getCertInfo(String paramString)
    throws CertException
  {
    CertInfo localCertInfo = null;
    try
    {
      db = DBManager.getInstance();
      localCertInfo = db.getCertInfo(paramString);
    }
    catch (DBException localDBException)
    {
      throw new CertException(localDBException.getErrCode(), localDBException.getErrDesc(), localDBException);
    }
    return localCertInfo;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.operation.CAKeyRecoverOpt
 * JD-Core Version:    0.6.0
 */