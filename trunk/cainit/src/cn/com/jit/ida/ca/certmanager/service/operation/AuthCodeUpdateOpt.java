package cn.com.jit.ida.ca.certmanager.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.db.DBManager;

public class AuthCodeUpdateOpt
{
  public static String updateAuthCode(String paramString1, String paramString2)
    throws IDAException
  {
    String str = CodeGenerator.generateAuthCode();
    DBManager localDBManager = null;
    localDBManager = DBManager.getInstance();
    int i = localDBManager.updateAuthCode(paramString1, str, paramString2);
    if (i == -1)
      throw new IDAException("0604", "执行业务操作 数据库更新授权码操作无效");
    return str;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.operation.AuthCodeUpdateOpt
 * JD-Core Version:    0.6.0
 */