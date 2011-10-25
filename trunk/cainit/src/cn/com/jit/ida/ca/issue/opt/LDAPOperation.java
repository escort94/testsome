package cn.com.jit.ida.ca.issue.opt;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.LDAPConfig;
import cn.com.jit.ida.ca.issue.ISSException;
import java.util.Hashtable;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;

public class LDAPOperation
{
  private DirContext ctx = null;
  private LDAPConfig ldapConfig = null;

  public LDAPOperation()
    throws ISSException
  {
    try
    {
      this.ldapConfig = LDAPConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
      new ISSException(localIDAException.getErrCode(), localIDAException.getErrDescEx() + ":" + localIDAException.toString(), localIDAException);
    }
    this.ctx = getConnection();
  }

  public DirContext getConnection()
    throws ISSException
  {
    InitialDirContext localInitialDirContext = null;
    Hashtable localHashtable = new Hashtable();
    String str1 = this.ldapConfig.getLDAPServerAddress();
    if ((str1 == null) || (str1.equals("")))
      throw new ISSException("8400", "LDAP服务器地址不能为空");
    int i = this.ldapConfig.getLDAPPort();
    localHashtable.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
    localHashtable.put("java.naming.provider.url", "ldap://" + this.ldapConfig.getLDAPServerAddress() + ":" + i);
    String str2 = this.ldapConfig.getLDAPUserDN();
    String str3 = this.ldapConfig.getLDAP_USER_Password();
    if ((str2 != null) && (str3 != null))
    {
      localHashtable.put("java.naming.security.authentication", "simple");
      localHashtable.put("java.naming.security.principal", str2);
      localHashtable.put("java.naming.security.credentials", str3);
    }
    try
    {
      localInitialDirContext = new InitialDirContext(localHashtable);
    }
    catch (NamingException localNamingException)
    {
      throw new ISSException("8401", "LDAP连接失败", localNamingException);
    }
    if (localInitialDirContext == null)
      throw new ISSException("8402", "LDAP连接为空");
    return localInitialDirContext;
  }

  protected void add(String paramString, Attributes paramAttributes)
    throws ISSException
  {
    if ((paramString == null) || (paramString.trim().equals("")))
      throw new ISSException("8406", "证书主题不能为空");
    if (paramAttributes == null)
      throw new ISSException("8409", "发布属性不能为空");
    try
    {
      this.ctx.createSubcontext(paramString, paramAttributes);
    }
    catch (NamingException localNamingException)
    {
      throw new ISSException("8410", "发布实体失败", localNamingException);
    }
  }

  protected void modify(String paramString, int paramInt, Attributes paramAttributes)
    throws ISSException
  {
    try
    {
      this.ctx.modifyAttributes(paramString, paramInt, paramAttributes);
    }
    catch (NamingException localNamingException)
    {
      throw new ISSException("8410", "发布实体失败", localNamingException);
    }
  }

  protected void closeConnection()
    throws ISSException
  {
    if (this.ctx != null)
      try
      {
        this.ctx.close();
      }
      catch (NamingException localNamingException)
      {
        throw new ISSException("8403", "LDAP关闭异常", localNamingException);
      }
  }

  protected void delete(String paramString)
    throws ISSException
  {
    if ((paramString == null) || (paramString.trim().equals("")))
      throw new ISSException("8406", "证书主题不能为空");
    try
    {
      this.ctx.destroySubcontext(paramString);
    }
    catch (NamingException localNamingException)
    {
      throw new ISSException("8412", "删除实体失败", localNamingException);
    }
  }

  protected NamingEnumeration IsExistEntry(String paramString)
  {
    NamingEnumeration localNamingEnumeration = null;
    if (paramString == null)
      return null;
    try
    {
      SearchControls localSearchControls = new SearchControls();
      localSearchControls.setSearchScope(0);
      localNamingEnumeration = this.ctx.search(paramString, "(objectclass=*)", localSearchControls);
    }
    catch (NamingException localNamingException)
    {
      return null;
    }
    return localNamingEnumeration;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.opt.LDAPOperation
 * JD-Core Version:    0.6.0
 */