package cn.com.jit.ida.ca.issue.entity;

import java.util.Vector;

public class CrossCertPairEntity extends BaseEntity
{
  private Vector crossCertPairs = null;
  private String caSubject = null;
  private String caSerialNumber = null;

  public CrossCertPairEntity(String paramString)
  {
    super(paramString);
    this.entityType = "crossCertPair";
  }

  public String getDistributionPath()
  {
    return this.caSubject;
  }

  public Vector getCrossCertPairs()
  {
    return this.crossCertPairs;
  }

  public void setCrossCertPairs(Vector paramVector)
  {
    this.crossCertPairs = paramVector;
  }

  public String getCaSubject()
  {
    return this.caSubject;
  }

  public void setCaSubject(String paramString)
  {
    this.caSubject = paramString;
  }

  public String getCaSerialNumber()
  {
    return this.caSerialNumber;
  }

  public void setCaSerialNumber(String paramString)
  {
    this.caSerialNumber = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.entity.CrossCertPairEntity
 * JD-Core Version:    0.6.0
 */