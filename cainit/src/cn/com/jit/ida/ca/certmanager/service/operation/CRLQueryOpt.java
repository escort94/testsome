package cn.com.jit.ida.ca.certmanager.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.db.DBManager;
import java.util.Vector;

public class CRLQueryOpt
{
  public static Vector queryCRLInfo()
    throws IDAException
  {
    DBManager localDBManager = DBManager.getInstance();
    Vector localVector = localDBManager.getCRLInfo();
    return localVector;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.operation.CRLQueryOpt
 * JD-Core Version:    0.6.0
 */