package cn.com.jit.ida.log;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.db.DBManager;

public class OptLogger
{
  public void info(Operation paramOperation)
    throws IDAException
  {
    int i = 0;
    InternalConfig localInternalConfig = null;
    try
    {
      localInternalConfig = InternalConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
    }
    Object localObject;
    if (localInternalConfig != null)
    {
      localObject = localInternalConfig.getIsLog();
      if (((String)localObject).toUpperCase().equals("TRUE"))
        i = 1;
      else
        i = 0;
    }
    else
    {
      i = 1;
    }
    if (i != 0)
    {
      localObject = DBManager.getInstance();
      ((DBManager)localObject).saveOperationlog(paramOperation);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.log.OptLogger
 * JD-Core Version:    0.6.0
 */