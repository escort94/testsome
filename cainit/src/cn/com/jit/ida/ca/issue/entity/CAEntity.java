package cn.com.jit.ida.ca.issue.entity;

import cn.com.jit.ida.ca.issue.ISSException;
import java.util.Vector;
import javax.naming.directory.Attribute;

public class CAEntity extends BaseEntity
{
  private Attribute attribute = null;
  private String CASubject = null;
  private byte[] CACert = null;
  private String CACertSN = null;
  private Vector crossCertPairs = null;

  public CAEntity(String paramString)
  {
    super(paramString);
    this.entityType = "CACert";
  }

  public String getDistributionPath()
  {
    return this.CASubject;
  }

  public void setCACert(byte[] paramArrayOfByte)
    throws ISSException
  {
    if (paramArrayOfByte == null)
      throw new ISSException("8408", "证书实体不能为空");
    this.CACert = paramArrayOfByte;
  }

  public byte[] getCACert()
  {
    return this.CACert;
  }

  public void setCACertSN(String paramString)
    throws ISSException
  {
    if ((paramString == null) || (paramString.equals("")))
      throw new ISSException("8405", "证书序列号不能为空");
    this.CACertSN = paramString;
  }

  public String getCACertSN()
  {
    return this.CACertSN;
  }

  public void setCASubject(String paramString)
    throws ISSException
  {
    if ((paramString == null) || (paramString.equals("")))
      throw new ISSException("8406", "证书主题不能为空");
    this.CASubject = paramString;
  }

  public String getCASubject()
  {
    return this.CASubject;
  }

  public Vector getCrossCertPairs()
  {
    return this.crossCertPairs;
  }

  public Attribute getAttribute()
  {
    return this.attribute;
  }

  public void setCrossCertPairs(Vector paramVector)
  {
    this.crossCertPairs = paramVector;
  }

  public void setAttribute(Attribute paramAttribute)
  {
    this.attribute = paramAttribute;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.entity.CAEntity
 * JD-Core Version:    0.6.0
 */