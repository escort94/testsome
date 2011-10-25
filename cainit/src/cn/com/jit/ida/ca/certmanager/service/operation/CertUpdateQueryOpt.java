package cn.com.jit.ida.ca.certmanager.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertExtensions;
import cn.com.jit.ida.ca.db.DBManager;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class CertUpdateQueryOpt
{
  public static Vector queryCertInfo(Properties paramProperties, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    Vector localVector = localDBManager.getCertInfo(paramProperties, paramInt1, paramInt2, paramBoolean);
    return localVector;
  }

  public static CertExtensions querryCertExtensions(String paramString)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    CertExtensions localCertExtensions = localDBManager.getCertExtensions(paramString);
    return localCertExtensions;
  }

  public static Hashtable querryCertStandardExt(String paramString)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    Hashtable localHashtable = localDBManager.getCertStandardExt(paramString);
    return localHashtable;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.operation.CertUpdateQueryOpt
 * JD-Core Version:    0.6.0
 */