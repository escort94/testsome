package cn.com.jit.ida.ca.audit.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.db.DBManager;
import java.util.Properties;
import java.util.Vector;

public class AuditOperation
{
  public static Vector queryOptLog(Properties paramProperties, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    return localDBManager.getOperationLogInfo(paramProperties, paramInt1, paramInt2, paramBoolean);
  }

  public static Vector queryCert(Properties paramProperties, String paramString, boolean paramBoolean)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    return localDBManager.getCertStatistic(paramProperties, paramString, paramBoolean);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.audit.service.operation.AuditOperation
 * JD-Core Version:    0.6.0
 */