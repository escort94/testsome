package cn.com.jit.ida.ca.issue.initschema;

import java.util.Vector;

public class LDAPObjectClass extends EntryBase
{
  public LDAPObjectClass()
  {
  }

  public LDAPObjectClass(String paramString)
  {
    super(LDAPSchema.OCROOT + "/" + paramString);
    setName(paramString);
    setOID(paramString);
    setStructrual(true);
  }

  public String getName()
  {
    Vector localVector = new Vector();
    localVector = super.getAttributeValues("NAME");
    String str = (String)localVector.get(0);
    return str;
  }

  public String getSuper()
  {
    Vector localVector = new Vector();
    localVector = super.getAttributeValues("SUP");
    String str = (String)localVector.get(0);
    return str;
  }

  public String[] getMust()
  {
    Vector localVector = new Vector();
    localVector = super.getAttributeValues("MUST");
    String[] arrayOfString = new String[localVector.size()];
    for (int i = 0; i < localVector.size(); i++)
      arrayOfString[i] = ((String)localVector.get(i));
    return arrayOfString;
  }

  public String[] getMay()
  {
    Vector localVector = new Vector();
    localVector = super.getAttributeValues("MAY");
    String[] arrayOfString = new String[localVector.size()];
    for (int i = 0; i < localVector.size(); i++)
      arrayOfString[i] = ((String)localVector.get(i));
    return arrayOfString;
  }

  public String getDesc()
  {
    Vector localVector = new Vector();
    localVector = super.getAttributeValues("DESC");
    String str = (String)localVector.get(0);
    return str;
  }

  private void setName(String paramString)
  {
    Vector localVector = new Vector();
    localVector.add(paramString);
    super.setAttribute("NAME", localVector);
  }

  public void setSuper(String paramString)
  {
    Vector localVector = new Vector();
    localVector.add(paramString);
    super.setAttribute("SUP", localVector);
  }

  public void setMust(String[] paramArrayOfString)
  {
    if (paramArrayOfString != null)
    {
      Vector localVector = new Vector();
      for (int i = 0; i < paramArrayOfString.length; i++)
        localVector.add(paramArrayOfString[i]);
      super.setAttribute("MUST", localVector);
    }
  }

  public void setMay(String[] paramArrayOfString)
  {
    if (paramArrayOfString != null)
    {
      Vector localVector = new Vector();
      for (int i = 0; i < paramArrayOfString.length; i++)
        localVector.add(paramArrayOfString[i]);
      super.setAttribute("MAY", localVector);
    }
  }

  public void setDesc(String paramString)
  {
    Vector localVector = new Vector();
    localVector.add(paramString);
    super.setAttribute("DESC", localVector);
  }

  private void setStructrual(boolean paramBoolean)
  {
    Vector localVector = new Vector();
    if (paramBoolean)
      localVector.add("true");
    else
      localVector.add("false");
    super.setAttribute("STRUCTRUAL", localVector);
  }

  public void setOID(String paramString)
  {
    Vector localVector = new Vector();
    localVector.add(paramString);
    super.setAttribute("NUMERICOID", localVector);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.initschema.LDAPObjectClass
 * JD-Core Version:    0.6.0
 */