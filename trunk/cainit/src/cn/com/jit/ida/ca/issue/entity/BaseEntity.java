package cn.com.jit.ida.ca.issue.entity;

import java.util.StringTokenizer;
import javax.naming.directory.Attributes;

public abstract class BaseEntity
{
  public static final String TO_LDAP = "LDAP";
  public static final String TO_FILE = "FILE";
  public static final String CA_ROOT_CERT = "CACert";
  public static final String CERT = "Cert";
  public static final String CRL = "CRL";
  public static final String CROSS_CERT_PAIR = "crossCertPair";
  protected String issType = null;
  protected String entityType = null;
  protected Attributes attributes = null;
  protected String baseDN = null;

  public BaseEntity(String paramString)
  {
    this.issType = paramString;
  }

  public abstract String getDistributionPath();

  public String getISSType()
  {
    return this.issType;
  }

  public String getEntityType()
  {
    return this.entityType;
  }

  public boolean isContain(String paramString)
  {
    String str1 = getDistributionPath();
    StringTokenizer localStringTokenizer = new StringTokenizer(str1, ",");
    while (localStringTokenizer.hasMoreTokens())
    {
      String str2 = localStringTokenizer.nextToken();
      if (str2.trim().toUpperCase().indexOf(paramString.trim() + "=") == 0)
        return true;
    }
    return false;
  }

  public String getCN()
  {
    String str1 = null;
    String str2 = getDistributionPath();
    StringTokenizer localStringTokenizer = new StringTokenizer(str2, ",");
    while (localStringTokenizer.hasMoreTokens())
    {
      String str3 = localStringTokenizer.nextToken();
      if (str3.trim().toUpperCase().indexOf("CN=") != 0)
        continue;
      str1 = str3.substring(str3.indexOf("=") + 1);
    }
    if (str1 == null)
      str1 = str2.substring(str2.indexOf("=") + 1, str2.indexOf(","));
    return str1;
  }

  public String getSN()
  {
    String str1 = null;
    String str2 = getDistributionPath();
    StringTokenizer localStringTokenizer = new StringTokenizer(str2, ",");
    while (localStringTokenizer.hasMoreTokens())
    {
      String str3 = localStringTokenizer.nextToken();
      if (str3.trim().toUpperCase().indexOf("SN=") != 0)
        continue;
      str1 = str3.substring(str3.indexOf("=") + 1);
    }
    if (str1 == null)
      str1 = str2.substring(str2.indexOf("=") + 1, str2.indexOf(","));
    return str1;
  }

  public String getBaseDN()
  {
    return this.baseDN;
  }

  public void setBaseDN(String paramString)
  {
    this.baseDN = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.entity.BaseEntity
 * JD-Core Version:    0.6.0
 */