package cn.com.jit.ida.ca.issue.entity;

public class CRLEntityAttribute
{
  private String crlDPObj = "crldistributionpoint";
  private String attriIdCRL = "certificateRevocationList;binary";

  public String getAttriIdCRL()
  {
    return this.attriIdCRL;
  }

  public String getCrlDPObj()
  {
    return this.crlDPObj;
  }

  public void setCrlDPObj(String paramString)
  {
    this.crlDPObj = paramString;
  }

  public void setAttriIdCRL(String paramString)
  {
    this.attriIdCRL = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.entity.CRLEntityAttribute
 * JD-Core Version:    0.6.0
 */