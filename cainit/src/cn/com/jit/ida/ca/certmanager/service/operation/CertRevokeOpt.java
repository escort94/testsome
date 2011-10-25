package cn.com.jit.ida.ca.certmanager.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertRevokeInfo;
import cn.com.jit.ida.ca.db.DBManager;

public class CertRevokeOpt
{
  public static int revokeCert(CertRevokeInfo paramCertRevokeInfo)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    int i = localDBManager.revokeCert(paramCertRevokeInfo);
    return i;
  }

  public static int holdCert(CertRevokeInfo paramCertRevokeInfo)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    int i = localDBManager.holdCert(paramCertRevokeInfo);
    return i;
  }

  public static int unHoldCert(String paramString1, String paramString2)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    int i = localDBManager.unHoldCert(paramString1, paramString2);
    return i;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.operation.CertRevokeOpt
 * JD-Core Version:    0.6.0
 */