package cn.com.jit.ida.globalconfig;

import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.JKey;
import cn.com.jit.ida.util.pki.cipher.Mechanism;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.encoders.Base64;

public class ProtectConfig
{
  private static ProtectConfig instance;
  private JCrypto jCrypto = JCrypto.getInstance();
  private Session session;
  private JKey key = new JKey("RC4", "iloverc4".getBytes());
  private Mechanism mech = new Mechanism("RC4");

  private ProtectConfig()
    throws ConfigException
  {
    try
    {
      this.jCrypto.initialize("JSOFT_LIB", null);
      this.session = this.jCrypto.openSession("JSOFT_LIB");
    }
    catch (PKIException localPKIException)
    {
      throw new ConfigException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
  }

  public static ProtectConfig getInstance()
    throws ConfigException
  {
    if (instance == null)
      instance = new ProtectConfig();
    return instance;
  }

  public String Encrypto(String paramString)
    throws ConfigException
  {
    String str;
    try
    {
      str = new String(Base64.encode(this.session.encrypt(this.mech, this.key, paramString.getBytes())));
    }
    catch (PKIException localPKIException)
    {
      throw new ConfigException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
    return str;
  }

  public String Decrypto(String paramString)
    throws ConfigException
  {
    String str;
    try
    {
      byte[] arrayOfByte1 = Base64.decode(paramString);
      byte[] arrayOfByte2 = this.session.decrypt(this.mech, this.key, arrayOfByte1);
      str = new String(arrayOfByte2);
    }
    catch (PKIException localPKIException)
    {
      throw new ConfigException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
    return str;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.globalconfig.ProtectConfig
 * JD-Core Version:    0.6.0
 */