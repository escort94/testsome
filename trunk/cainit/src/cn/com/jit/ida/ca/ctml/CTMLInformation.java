package cn.com.jit.ida.ca.ctml;

import cn.com.jit.ida.IDAException;

public class CTMLInformation
{
  private String name;
  private String status;
  private String id;
  private String type;
  private String description;
  private CTMLPolicy ctmlPolicy;

  public String getCTMLName()
  {
    return this.name;
  }

  public void setCTMLName(String paramString)
  {
    this.name = paramString;
  }

  public String getCTMLType()
  {
    return this.type;
  }

  public void setCTMLType(String paramString)
  {
    this.type = paramString;
  }

  public String getCTMLID()
  {
    return this.id;
  }

  public void setCTMLID(String paramString)
  {
    this.id = paramString;
  }

  public String getCTMLStatus()
  {
    return this.status;
  }

  public void setCTMLStatus(String paramString)
  {
    this.status = paramString;
  }

  public String getCTMLDesc()
  {
    return this.description;
  }

  public void setCTMLDesc(String paramString)
  {
    this.description = paramString;
  }

  public CTMLPolicy getCTMLPolicy()
  {
    return this.ctmlPolicy;
  }

  public void setCTMLPolicy(CTMLPolicy paramCTMLPolicy)
  {
    this.ctmlPolicy = paramCTMLPolicy;
  }

  public void setCTMLPolicy(byte[] paramArrayOfByte)
    throws IDAException
  {
    this.ctmlPolicy = CTMLPolicyFactory.newCTMLPolicyInstance(this.type);
    this.ctmlPolicy.setCTMLPolicyDesc(paramArrayOfByte);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.CTMLInformation
 * JD-Core Version:    0.6.0
 */