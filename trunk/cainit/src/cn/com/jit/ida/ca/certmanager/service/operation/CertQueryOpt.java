package cn.com.jit.ida.ca.certmanager.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.db.DBManager;
import java.util.Properties;
import java.util.Vector;

public class CertQueryOpt
{
  public static Vector queryCertInfo(Properties paramProperties, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    Vector localVector = localDBManager.getCertInfo(paramProperties, paramInt1, paramInt2, paramBoolean);
    return localVector;
  }

  public static byte[] queryCertEntity(String paramString)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    byte[] arrayOfByte = localDBManager.getCertEntity(paramString);
    return arrayOfByte;
  }

  public static CertInfo queryCertInfo(String paramString)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    CertInfo localCertInfo = localDBManager.getCertInfo(paramString);
    return localCertInfo;
  }

  public static CertInfo queryCertInfo(String paramString1, String paramString2)
    throws IDAException
  {
    CertInfo localCertInfo = null;
    DBManager localDBManager = DBManager.getInstance();
    localCertInfo = localDBManager.getCertInfo(paramString1, paramString2);
    return localCertInfo;
  }

  public static CertInfo queryCertReqInfo(String paramString1, String paramString2)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    CertInfo localCertInfo = localDBManager.getCertReqInfo(paramString1, paramString2);
    return localCertInfo;
  }

  public static int checkCertReq(String paramString1, String paramString2)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    int i = localDBManager.checkCertReq(paramString1, paramString2);
    return i;
  }

  public static CertInfo queryHoldCertInfo(String paramString1, String paramString2)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    CertInfo localCertInfo = localDBManager.getHoldCertInfo(paramString1, paramString2);
    return localCertInfo;
  }

  public static CertInfo queryUnRVKCertInfo(String paramString1, String paramString2)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    CertInfo localCertInfo = localDBManager.getUNRVKCertInfo(paramString1, paramString2);
    return localCertInfo;
  }

  public static Vector queryIsnotWaitingCertInfo(Properties paramProperties, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    Vector localVector = localDBManager.getCertInfoForIsnotWaiting(paramProperties, paramInt1, paramInt2, paramBoolean);
    return localVector;
  }

  public static CertInfo queryPendingCertInfo(String paramString1, String paramString2)
    throws IDAException
  {
    CertInfo localCertInfo = null;
    DBManager localDBManager = DBManager.getInstance();
    localCertInfo = localDBManager.getPendingCertInfo(paramString1, paramString2);
    return localCertInfo;
  }

  public static CertInfo queryPendingHoldCertInfo(String paramString1, String paramString2)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    CertInfo localCertInfo = localDBManager.getPendignHoldCertInfo(paramString1, paramString2);
    return localCertInfo;
  }

  public static CertInfo queryPendingUnRVKCertInfo(String paramString1, String paramString2)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    CertInfo localCertInfo = localDBManager.getPendingUNRVKCertInfo(paramString1, paramString2);
    return localCertInfo;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.operation.CertQueryOpt
 * JD-Core Version:    0.6.0
 */