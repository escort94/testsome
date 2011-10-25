package cn.com.jit.ida.globalconfig;

import cn.com.jit.ida.ca.db.DBException;
import java.util.Vector;

public abstract interface DBInterface
{
  public abstract Vector getConfig(String paramString)
    throws DBException;

  public abstract int setConfig(String paramString, Information paramInformation)
    throws DBException;
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.globalconfig.DBInterface
 * JD-Core Version:    0.6.0
 */