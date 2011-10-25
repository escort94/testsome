package cn.com.jit.ida.ca.issue.initschema;

import java.util.Vector;

public class LDAPAttribute extends EntryBase
{
  public LDAPAttribute()
  {
  }

  public LDAPAttribute(String paramString)
  {
    super(LDAPSchema.ATTROOT + "/" + paramString);
    setName(paramString);
  }

  private void setName(String paramString)
  {
    Vector localVector = new Vector();
    localVector.add(paramString);
    super.setAttribute("NAME", localVector);
  }

  public void setDesc(String paramString)
  {
    Vector localVector = new Vector();
    localVector.add(paramString);
    super.setAttribute("DESC", localVector);
  }

  public void setOID(String paramString)
  {
    Vector localVector = new Vector();
    localVector.add(paramString);
    super.setAttribute("NUMERICOID", localVector);
  }

  public void setSyntax(String paramString)
  {
    Vector localVector = new Vector();
    localVector.add(paramString);
    super.setAttribute("SYNTAX", localVector);
  }

  public void setEquality(String paramString)
  {
    Vector localVector = new Vector();
    localVector.add(paramString);
    super.setAttribute("EQUALITY", localVector);
  }

  public void setOrdering(String paramString)
  {
    Vector localVector = new Vector();
    localVector.add(paramString);
    super.setAttribute("ORDERING", localVector);
  }

  public void setSingleValue(boolean paramBoolean)
  {
    Vector localVector = new Vector();
    if (paramBoolean == true)
    {
      localVector.add("true");
      super.setAttribute("SINGLE-VALUE", localVector);
    }
    else
    {
      localVector.add("false");
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.initschema.LDAPAttribute
 * JD-Core Version:    0.6.0
 */