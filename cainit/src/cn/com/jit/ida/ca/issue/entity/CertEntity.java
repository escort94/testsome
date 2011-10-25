package cn.com.jit.ida.ca.issue.entity;

import cn.com.jit.ida.ca.issue.ISSException;
import java.util.StringTokenizer;
import javax.naming.directory.Attribute;

public class CertEntity extends BaseEntity
{
  private Attribute attribute = null;
  private String certSN = null;
  private String certDN = null;
  private String certStatus = null;
  private String certType = null;
  private byte[] certContent = null;
  private String email = null;

  public CertEntity(String paramString)
  {
    super(paramString);
    this.entityType = "Cert";
  }

  public String getDistributionPath()
  {
    return this.certDN;
  }

  public String getEntityType()
  {
    return this.entityType;
  }

  public byte[] getCertContent()
  {
    return this.certContent;
  }

  public String getCertDN()
  {
    return this.certDN;
  }

  public String getCertSN()
  {
    return this.certSN;
  }

  public String getCertStatus()
  {
    return this.certStatus;
  }

  public String getCertType()
  {
    return this.certType;
  }

  public void setCertType(String paramString)
    throws ISSException
  {
    if ((paramString == null) || (paramString.equals("")))
      throw new ISSException("8407", "证书类型不能为空");
    this.certType = paramString;
  }

  public void setCertStatus(String paramString)
  {
    this.certStatus = paramString;
  }

  public void setCertSN(String paramString)
    throws ISSException
  {
    if ((paramString == null) || (paramString.equals("")))
      throw new ISSException("8405", "证书序列号不能为空");
    this.certSN = paramString;
  }

  public void setCertDN(String paramString)
    throws ISSException
  {
    if ((paramString == null) || (paramString.equals("")))
      throw new ISSException("8406", "证书主题不能为空");
    this.certDN = paramString;
  }

  public void setCertContent(byte[] paramArrayOfByte)
    throws ISSException
  {
    if (paramArrayOfByte == null)
      throw new ISSException("8408", "证书实体不能为空");
    this.certContent = paramArrayOfByte;
  }

  public void setAttribute(Attribute paramAttribute)
  {
    this.attribute = paramAttribute;
  }

  public String getEmail()
  {
    String str1 = null;
    String str2 = getDistributionPath();
    StringTokenizer localStringTokenizer = new StringTokenizer(str2, ",");
    while (localStringTokenizer.hasMoreTokens())
    {
      String str3 = localStringTokenizer.nextToken();
      if (str3.trim().toUpperCase().indexOf("E=") != 0)
        continue;
      str1 = str3.substring(str3.indexOf("=") + 1);
    }
    return str1;
  }

  public Attribute getAttribute()
  {
    return this.attribute;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.entity.CertEntity
 * JD-Core Version:    0.6.0
 */