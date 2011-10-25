package cn.com.jit.ida.ca.ctml;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;

public class CTML
  implements CTMLInterface
{
  protected CTMLInformation ctmlInfo;

  public CTML(CTMLInformation paramCTMLInformation)
  {
    if (paramCTMLInformation == null)
      throw new NullPointerException();
    this.ctmlInfo = paramCTMLInformation;
  }

  public String getCTMLName()
  {
    return this.ctmlInfo.getCTMLName();
  }

  public String getCTMLID()
  {
    return this.ctmlInfo.getCTMLID();
  }

  public String getCTMLStatus()
  {
    return this.ctmlInfo.getCTMLStatus();
  }

  public String getCTMLDesc()
  {
    return this.ctmlInfo.getCTMLDesc();
  }

  public String getCTMLType()
  {
    return this.ctmlInfo.getCTMLType();
  }

  public byte[] getCTMLPolicyDesc()
    throws IDAException
  {
    return this.ctmlInfo.getCTMLPolicy().getCTMLPolicyDesc();
  }

  public byte[] generateCertificate(CertInfo paramCertInfo)
    throws IDAException
  {
    return null;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.CTML
 * JD-Core Version:    0.6.0
 */