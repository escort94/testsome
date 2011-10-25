package cn.com.jit.ida.ca.certmanager.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.db.DBManager;
import java.util.Properties;
import java.util.Vector;

public class CertArcSearchOpt
{
  public static Vector queryCertInfo(Properties paramProperties, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    Vector localVector = localDBManager.getCertArcInfo(paramProperties, paramInt1, paramInt2, paramBoolean);
    return localVector;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.operation.CertArcSearchOpt
 * JD-Core Version:    0.6.0
 */