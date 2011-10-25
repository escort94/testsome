package cn.com.jit.ida.ca.certmanager;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.Service;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.license.IDAFormula;

public abstract class CertService extends CertConstant
  implements Service
{
  public Operator operator;

  public abstract Response dealRequest(Request paramRequest);

  protected void licenceCheck()
    throws CertException
  {
    long l1 = 0L;
    long l2 = 0L;
    try
    {
      IDAFormula localIDAFormula1 = IDAFormula.getInstance();
      String localObject = localIDAFormula1.getSystemValue5();
      if (((String)localObject).equals("NULL"))
        return;
      l1 = Long.parseLong((String)localObject);
      DBManager localDBManager1 = DBManager.getInstance();
      l2 = localDBManager1.getCertAccount();
    }
    catch (Exception localException1)
    {
      Object localObject;
      if ((localException1 instanceof IDAException))
      {
        localObject = (IDAException)localException1;
        throw new CertException(((IDAException)localObject).getErrCode(), ((IDAException)localObject).getErrDesc(), ((IDAException)localObject).getHistory());
      }
      throw new CertException("0103", "Licence验证 获取Licence信息异常", localException1);
    }
    if (l2 >= l1)
      throw new CertException("0101", "Licence验证 超出Licence发证量限制");
    long l3 = 0L;
    long l4 = 0L;
    try
    {
      IDAFormula localIDAFormula2 = IDAFormula.getInstance();
      String str = localIDAFormula2.getCustomValue("有效证书量");
      if ((str == null) || (str.trim().equals("")) || (str.equalsIgnoreCase("NULL")))
        return;
      l3 = Long.parseLong(str);
      DBManager localDBManager2 = DBManager.getInstance();
      l4 = localDBManager2.getCertCountForIsvaild();
    }
    catch (Exception localException2)
    {
      return;
    }
    if (l4 >= l3)
      throw new CertException("0104", "Licence验证 超出Licence有效证书量限制");
  }

  protected abstract void policyCheck()
    throws CertException;

  protected abstract void validateCheck()
    throws CertException;

  protected abstract void certStatusCheck()
    throws CertException;

  protected abstract void CTMLCheck()
    throws CertException;
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.CertService
 * JD-Core Version:    0.6.0
 */