package cn.com.jit.ida.ca.issue.initschema;

import cn.com.jit.ida.ca.issue.ISSException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LDAPSchema
{
  private DirContext ctx;
  private DirContext schema;
  public static String OCROOT = "ClassDefinition";
  public static String ATTROOT = "AttributeDefinition";
  public static String BINARY = "1.3.6.1.4.1.1466.115.121.1.5";
  public static String DIRECTORYSTRING = "1.3.6.1.4.1.1466.115.121.1.15";
  public static String IA5STRING = "1.3.6.1.4.1.1466.115.121.1.26";

  public LDAPSchema()
  {
  }

  public LDAPSchema(DirContext paramDirContext)
    throws ISSException
  {
    this.ctx = paramDirContext;
    try
    {
      this.schema = paramDirContext.getSchema("");
    }
    catch (NamingException localNamingException)
    {
      throw new ISSException("8416", "获得LDAP语法失败", localNamingException);
    }
  }

  public LDAPObjectClass getObjectClass(String paramString)
  {
    String str1 = "(MUST=" + paramString + ")";
    try
    {
      SearchControls localSearchControls = new SearchControls();
      localSearchControls.setSearchScope(2);
      NamingEnumeration localNamingEnumeration = this.schema.search(OCROOT, str1, null, localSearchControls);
      while ((localNamingEnumeration != null) && (localNamingEnumeration.hasMore()))
      {
        SearchResult localSearchResult = (SearchResult)localNamingEnumeration.next();
        String str2 = localSearchResult.getName().trim();
        Attributes localAttributes = localSearchResult.getAttributes();
        Attribute localAttribute = localAttributes.get("SUP");
        String str3 = (String)localAttribute.get(0);
        if (str3.compareTo("top") != 0)
          continue;
        LDAPObjectClass localLDAPObjectClass = new LDAPObjectClass(str2);
        localLDAPObjectClass.setAttributes(localAttributes);
        return localLDAPObjectClass;
      }
      return null;
    }
    catch (NamingException localNamingException)
    {
    }
    return null;
  }

  public void addObjectClass(LDAPObjectClass paramLDAPObjectClass)
    throws ISSException
  {
    try
    {
      this.schema.createSubcontext(paramLDAPObjectClass.getDN(), paramLDAPObjectClass.getAttributes());
    }
    catch (NamingException localNamingException)
    {
      throw new ISSException("8419", "添加对象类失败", localNamingException);
    }
  }

  public void deleteObjectClass(String paramString)
    throws ISSException
  {
    try
    {
      this.schema.destroySubcontext(OCROOT + "/" + paramString);
    }
    catch (NamingException localNamingException)
    {
      throw new ISSException("8420", "删除对象类失败", localNamingException);
    }
  }

  public boolean isObjectClassExist(String paramString)
  {
    try
    {
      DirContext localDirContext = (DirContext)this.schema.lookup(OCROOT + "/" + paramString);
      Attributes localAttributes = localDirContext.getAttributes("");
      return localAttributes != null;
    }
    catch (NamingException localNamingException)
    {
    }
    return false;
  }

  public void addAttribute(LDAPAttribute paramLDAPAttribute)
    throws ISSException
  {
    try
    {
      this.schema.createSubcontext(paramLDAPAttribute.getDN(), paramLDAPAttribute.getAttributes());
    }
    catch (NamingException localNamingException)
    {
      throw new ISSException("8417", "添加属性失败", localNamingException);
    }
  }

  public void deleteAttribute(String paramString)
    throws ISSException
  {
    try
    {
      this.schema.destroySubcontext(ATTROOT + "/" + paramString);
    }
    catch (NamingException localNamingException)
    {
      throw new ISSException("8418", "删除属性失败", localNamingException);
    }
  }

  public boolean isAttributeExist(String paramString)
  {
    try
    {
      DirContext localDirContext = (DirContext)this.schema.lookup(ATTROOT + "/" + paramString);
      Attributes localAttributes = localDirContext.getAttributes("");
      return localAttributes != null;
    }
    catch (NamingException localNamingException)
    {
    }
    return false;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.initschema.LDAPSchema
 * JD-Core Version:    0.6.0
 */