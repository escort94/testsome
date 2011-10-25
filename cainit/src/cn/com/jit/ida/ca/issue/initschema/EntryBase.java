package cn.com.jit.ida.ca.issue.initschema;

import java.util.Vector;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

public class EntryBase
{
  private Attributes myAttributes;
  private String myDN;

  public EntryBase()
  {
    this.myAttributes = null;
    this.myDN = null;
  }

  public EntryBase(String paramString)
  {
    this.myDN = paramString;
    this.myAttributes = new BasicAttributes(true);
  }

  public void setObjectClass(String[] paramArrayOfString)
  {
    Vector localVector = new Vector();
    for (int i = 0; i < paramArrayOfString.length; i++)
      localVector.add(paramArrayOfString[i]);
    setAttribute("objectClass", localVector);
  }

  public String getObjectClass()
  {
    Vector localVector = new Vector();
    localVector = getAttributeValues("objectClass");
    String str = (String)localVector.lastElement();
    return str;
  }

  protected boolean setAttribute(String paramString, Vector paramVector)
  {
    try
    {
      BasicAttribute localBasicAttribute = new BasicAttribute(paramString);
      for (int i = 0; i < paramVector.size(); i++)
        localBasicAttribute.add(paramVector.get(i));
      this.myAttributes.put(localBasicAttribute);
    }
    catch (Exception localException)
    {
      return false;
    }
    return true;
  }

  public void setAttributes(Attributes paramAttributes)
  {
    this.myAttributes = paramAttributes;
  }

  public Vector getAttributeName()
  {
    Vector localVector = new Vector();
    try
    {
      NamingEnumeration localNamingEnumeration = this.myAttributes.getIDs();
      while (localNamingEnumeration.hasMoreElements())
        localVector.add(localNamingEnumeration.next());
    }
    catch (Exception localException)
    {
      return null;
    }
    return localVector;
  }

  protected Vector getAttributeValues(String paramString)
  {
    Vector localVector = new Vector();
    try
    {
      Attribute localAttribute = this.myAttributes.get(paramString);
      if (localAttribute != null)
        for (int i = 0; i < localAttribute.size(); i++)
          localVector.add(localAttribute.get(i));
    }
    catch (Exception localException)
    {
      return null;
    }
    return localVector;
  }

  public String getDN()
  {
    return this.myDN;
  }

  public Attributes getAttributes()
  {
    return this.myAttributes;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.initschema.EntryBase
 * JD-Core Version:    0.6.0
 */