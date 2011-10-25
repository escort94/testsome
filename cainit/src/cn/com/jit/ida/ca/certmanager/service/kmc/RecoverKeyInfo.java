package cn.com.jit.ida.ca.certmanager.service.kmc;

public class RecoverKeyInfo
{
  private byte[] privateKey = null;
  private byte[] certificate = null;
  private byte[] encryptedSessionKey = null;
  private String sessionKeyAlg = null;
  private String sessionKeyPad = null;
  private String certSN = null;
  private byte[] encryptoCertEntity = null;

  public byte[] getCertificate()
  {
    return this.certificate;
  }

  public String getCertSN()
  {
    return this.certSN;
  }

  public byte[] getEncryptedSessionKey()
  {
    return this.encryptedSessionKey;
  }

  public byte[] getEncryptoCertEntity()
  {
    return this.encryptoCertEntity;
  }

  public byte[] getPrivateKey()
  {
    return this.privateKey;
  }

  public String getSessionKeyAlg()
  {
    return this.sessionKeyAlg;
  }

  public String getSessionKeyPad()
  {
    return this.sessionKeyPad;
  }

  public void setCertificate(byte[] paramArrayOfByte)
  {
    this.certificate = paramArrayOfByte;
  }

  public void setCertSN(String paramString)
  {
    this.certSN = paramString;
  }

  public void setEncryptedSessionKey(byte[] paramArrayOfByte)
  {
    this.encryptedSessionKey = paramArrayOfByte;
  }

  public void setEncryptoCertEntity(byte[] paramArrayOfByte)
  {
    this.encryptoCertEntity = paramArrayOfByte;
  }

  public void setPrivateKey(byte[] paramArrayOfByte)
  {
    this.privateKey = paramArrayOfByte;
  }

  public void setSessionKeyAlg(String paramString)
  {
    this.sessionKeyAlg = paramString;
  }

  public void setSessionKeyPad(String paramString)
  {
    this.sessionKeyPad = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.kmc.RecoverKeyInfo
 * JD-Core Version:    0.6.0
 */