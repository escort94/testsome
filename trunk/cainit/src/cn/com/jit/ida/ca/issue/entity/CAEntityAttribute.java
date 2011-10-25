package cn.com.jit.ida.ca.issue.entity;

public class CAEntityAttribute
{
  private String caObj = "certificationAuthority";
  private String idaPersonObj = "idaPerson";
  private String attriIdSN = "sn";
  private String attriIdSerialNumber = "serialNumber";
  private String attriIdCN = "cn";
  private String attriIdCACertificate = "cACertificate;binary";
  private String attriIdAuthRVKList = "authorityRevocationList;binary";
  private String attriIdCertRVKList = "certificateRevocationList;binary";
  private String attriIdCrossCertificatePair = "crossCertificatePair;binary";

  public String getAttriIdAuthRVKList()
  {
    return this.attriIdAuthRVKList;
  }

  public String getAttriIdCACertificate()
  {
    return this.attriIdCACertificate;
  }

  public String getAttriIdCertRVKList()
  {
    return this.attriIdCertRVKList;
  }

  public String getAttriIdCN()
  {
    return this.attriIdCN;
  }

  public String getAttriIdCrossCertificatePair()
  {
    return this.attriIdCrossCertificatePair;
  }

  public String getAttriIdSerialNumber()
  {
    return this.attriIdSerialNumber;
  }

  public String getAttriIdSN()
  {
    return this.attriIdSN;
  }

  public String getCaObj()
  {
    return this.caObj;
  }

  public String getIdaPersonObj()
  {
    return this.idaPersonObj;
  }

  public void setIdaPersonObj(String paramString)
  {
    this.idaPersonObj = paramString;
  }

  public void setCaObj(String paramString)
  {
    this.caObj = paramString;
  }

  public void setAttriIdSN(String paramString)
  {
    this.attriIdSN = paramString;
  }

  public void setAttriIdSerialNumber(String paramString)
  {
    this.attriIdSerialNumber = paramString;
  }

  public void setAttriIdCrossCertificatePair(String paramString)
  {
    this.attriIdCrossCertificatePair = paramString;
  }

  public void setAttriIdCN(String paramString)
  {
    this.attriIdCN = paramString;
  }

  public void setAttriIdCertRVKList(String paramString)
  {
    this.attriIdCertRVKList = paramString;
  }

  public void setAttriIdCACertificate(String paramString)
  {
    this.attriIdCACertificate = paramString;
  }

  public void setAttriIdAuthRVKList(String paramString)
  {
    this.attriIdAuthRVKList = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.entity.CAEntityAttribute
 * JD-Core Version:    0.6.0
 */