package cn.com.jit.ida.ca.issue.entity;

public class CertEntityAttribute
{
  private String idaPersonObj = "idaPerson";
  private String attriIdSN = "sn";
  private String attriIdSerialNumber = "serialNumber";
  private String attriIdCN = "cn";
  private String attriIdEMail = "mail";
  private String attriIdUserCertificate = "userCertificate;binary";

  public String getAttriIdCN()
  {
    return this.attriIdCN;
  }

  public String getAttriIdEMail()
  {
    return this.attriIdEMail;
  }

  public String getAttriIdSN()
  {
    return this.attriIdSN;
  }

  public String getAttriIdSerialNumber()
  {
    return this.attriIdSerialNumber;
  }

  public String getAttriIdUserCertificate()
  {
    return this.attriIdUserCertificate;
  }

  public String getIdaPersonObj()
  {
    return this.idaPersonObj;
  }

  public void setIdaPersonObj(String paramString)
  {
    this.idaPersonObj = paramString;
  }

  public void setAttriIdUserCertificate(String paramString)
  {
    this.attriIdUserCertificate = paramString;
  }

  public void setAttriIdSN(String paramString)
  {
    this.attriIdSN = paramString;
  }

  public void setAttriIdSerialNumber(String paramString)
  {
    this.attriIdSerialNumber = paramString;
  }

  public void setAttriIdEMail(String paramString)
  {
    this.attriIdEMail = paramString;
  }

  public void setAttriIdCN(String paramString)
  {
    this.attriIdCN = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.entity.CertEntityAttribute
 * JD-Core Version:    0.6.0
 */