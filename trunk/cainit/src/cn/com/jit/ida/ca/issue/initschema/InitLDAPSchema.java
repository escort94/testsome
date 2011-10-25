package cn.com.jit.ida.ca.issue.initschema;

import cn.com.jit.ida.ca.issue.ISSException;
import cn.com.jit.ida.ca.issue.opt.LDAPOperation;
import javax.naming.directory.DirContext;

public class InitLDAPSchema
{
  private static DirContext ctx = null;
  private static LDAPSchema schema = null;

  private void connect()
    throws ISSException
  {
    LDAPOperation localLDAPOperation = new LDAPOperation();
    ctx = localLDAPOperation.getConnection();
  }

  public void init()
    throws ISSException
  {
    connect();
    schema = new LDAPSchema(ctx);
    addattribute("e", "1.2.2.1.17", "caseIgnoreMatch", true, "self definiens", "1.3.6.1.4.1.1466.115.121.1.15");
    addattribute("t", "1.2.2.1.18", "caseIgnoreMatch", true, "self definiens", "1.3.6.1.4.1.1466.115.121.1.15");
    String[] arrayOfString1 = { "e" };
    addobjectclass("email", "2.5.6.27", "user defined", "top", arrayOfString1, null);
    String[] arrayOfString2 = { "t" };
    addobjectclass("titleObject", "2.5.6.28", "user defined", "top", arrayOfString2, null);
    String[] arrayOfString3 = { "serialNumber", "certificateRevocationList" };
    addobjectclass("idaPerson", "2.16.840.1.113730.3.2.2.1008", "user defined", "inetOrgPerson", null, arrayOfString3);
  }

  public static void addattribute(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean, String paramString5, String paramString6)
    throws ISSException
  {
    LDAPAttribute localLDAPAttribute = new LDAPAttribute(paramString1);
    localLDAPAttribute.setOID(paramString2);
    localLDAPAttribute.setSingleValue(paramBoolean);
    localLDAPAttribute.setDesc(paramString5);
    localLDAPAttribute.setSyntax(paramString6);
    localLDAPAttribute.setEquality(paramString3);
    localLDAPAttribute.setOrdering(paramString4);
    if (!schema.isAttributeExist(paramString1))
      schema.addAttribute(localLDAPAttribute);
  }

  public static void addattribute(String paramString1, String paramString2, String paramString3, boolean paramBoolean, String paramString4, String paramString5)
    throws ISSException
  {
    LDAPAttribute localLDAPAttribute = new LDAPAttribute(paramString1);
    localLDAPAttribute.setOID(paramString2);
    localLDAPAttribute.setSingleValue(paramBoolean);
    localLDAPAttribute.setDesc(paramString4);
    localLDAPAttribute.setSyntax(paramString5);
    localLDAPAttribute.setEquality(paramString3);
    if (!schema.isAttributeExist(paramString1))
      schema.addAttribute(localLDAPAttribute);
  }

  public static void addattribute(String paramString1, String paramString2, boolean paramBoolean, String paramString3, String paramString4)
    throws ISSException
  {
    LDAPAttribute localLDAPAttribute = new LDAPAttribute(paramString1);
    localLDAPAttribute.setOID(paramString2);
    localLDAPAttribute.setSingleValue(paramBoolean);
    localLDAPAttribute.setDesc(paramString3);
    localLDAPAttribute.setSyntax(paramString4);
    if (!schema.isAttributeExist(paramString1))
      schema.addAttribute(localLDAPAttribute);
  }

  public static void addobjectclass(String paramString1, String paramString2, String paramString3, String paramString4, String[] paramArrayOfString1, String[] paramArrayOfString2)
    throws ISSException
  {
    LDAPObjectClass localLDAPObjectClass = new LDAPObjectClass(paramString1);
    localLDAPObjectClass.setDesc(paramString3);
    localLDAPObjectClass.setSuper(paramString4);
    localLDAPObjectClass.setMust(paramArrayOfString1);
    localLDAPObjectClass.setMay(paramArrayOfString2);
    localLDAPObjectClass.setOID(paramString2);
    if (!schema.isObjectClassExist(paramString1))
      schema.addObjectClass(localLDAPObjectClass);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.initschema.InitLDAPSchema
 * JD-Core Version:    0.6.0
 */