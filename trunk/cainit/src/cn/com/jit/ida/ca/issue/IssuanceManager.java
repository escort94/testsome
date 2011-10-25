package cn.com.jit.ida.ca.issue;

import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.issue.entity.BaseEntity;
import cn.com.jit.ida.ca.issue.opt.Store;

public class IssuanceManager
{
  private Store ldapStore = null;
  private Store fileStore = null;
  private static IssuanceManager issuanceManager = null;

  public static IssuanceManager getInstance()
  {
    if (issuanceManager == null)
      return new IssuanceManager();
    return issuanceManager;
  }

  public void issue(BaseEntity paramBaseEntity)
    throws ISSException
  {
    if (paramBaseEntity == null)
      throw new ISSException("8404", "待颁发实体不能为空");
    String str1 = paramBaseEntity.getISSType();
    if (str1.equals("LDAP"))
    {
      try
      {
        InternalConfig localInternalConfig = InternalConfig.getInstance();
        String str2 = localInternalConfig.getLdapStoreClass();
        this.ldapStore = ((Store)Class.forName(str2).newInstance());
      }
      catch (Exception localException)
      {
        throw new ISSException("8422", "LDAP关闭异常", localException);
      }
      this.ldapStore.add(paramBaseEntity);
    }
    else if (str1.equals("FILE"))
    {
      this.fileStore.add(paramBaseEntity);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.IssuanceManager
 * JD-Core Version:    0.6.0
 */