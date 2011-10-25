package cn.com.jit.ida.ca.log.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.db.DBManager;
import java.util.Properties;
import java.util.Vector;

public class LogOperation
{
  public static Vector queryOptArcLog(Properties paramProperties, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    return localDBManager.getOperationLogArcInfo(paramProperties, paramInt1, paramInt2, paramBoolean);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.log.service.operation.LogOperation
 * JD-Core Version:    0.6.0
 */