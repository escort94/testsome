package cn.com.jit.ida.ca.db;

public class ValuePair
{
  private String value;
  private String type;

  public void setValue(String paramString)
  {
    this.value = paramString;
  }

  public void setType(String paramString)
  {
    this.type = paramString;
  }

  public String getValue()
  {
    return this.value;
  }

  public String getType()
  {
    return this.type;
  }

  public ValuePair(String paramString1, String paramString2)
  {
    this.value = paramString1;
    this.type = paramString2;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.db.ValuePair
 * JD-Core Version:    0.6.0
 */