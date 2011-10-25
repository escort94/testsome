package cn.com.jit.ida;

public class Operator
{
  private String operatorSN = null;
  private String operatorDN = null;
  private byte[] operatorCert = null;

  public void setOperatorDN(String paramString)
  {
    this.operatorDN = paramString;
  }

  public String getOperatorDN()
  {
    return this.operatorDN;
  }

  public String getOperatorSN()
  {
    return this.operatorSN;
  }

  public void setOperatorSN(String paramString)
  {
    this.operatorSN = paramString;
  }

  public void setOperatorCert(byte[] paramArrayOfByte)
  {
    this.operatorCert = paramArrayOfByte;
  }

  public byte[] getOperatorCert()
  {
    return this.operatorCert;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.Operator
 * JD-Core Version:    0.6.0
 */