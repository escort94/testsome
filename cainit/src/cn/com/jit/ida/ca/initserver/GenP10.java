package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.Parser;
import cn.com.jit.ida.util.pki.cipher.JKey;
import cn.com.jit.ida.util.pki.cipher.JKeyPair;
import cn.com.jit.ida.util.pki.cipher.Mechanism;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.cipher.param.GenKeyAttribute;
import cn.com.jit.ida.util.pki.pkcs.PKCS10;
import java.security.Key;

public class GenP10
{
  String DN;
  JKey pubKey;
  JKey priKey;
  Session session;
  JKeyPair keyPair;

  public GenP10(String paramString1, String paramString2, int paramInt, Session paramSession)
    throws InitServerException
  {
    this.DN = paramString1;
    this.session = paramSession;
    genKey(paramString2, paramInt);
  }

  public GenP10(String paramString1, String paramString2, int paramInt1, Session paramSession, int paramInt2)
    throws InitServerException
  {
    this.DN = paramString1;
    this.session = paramSession;
    genKey(paramString2, paramInt1, paramInt2);
  }

  private void genKey(String paramString, int paramInt)
    throws InitServerException
  {
    try
    {
      this.keyPair = this.session.generateKeyPair(new Mechanism(paramString), paramInt);
      this.pubKey = this.keyPair.getPublicKey();
      this.priKey = this.keyPair.getPrivateKey();
    }
    catch (PKIException localPKIException)
    {
      throw new InitServerException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
  }

  private void genKey(String paramString, int paramInt1, int paramInt2)
    throws InitServerException
  {
    try
    {
      GenKeyAttribute localGenKeyAttribute = new GenKeyAttribute();
      localGenKeyAttribute.setKeyNum(paramInt2);
      this.keyPair = this.session.generateKeyPair(new Mechanism(paramString, localGenKeyAttribute), paramInt1);
      this.pubKey = this.keyPair.getPublicKey();
      this.priKey = this.keyPair.getPrivateKey();
    }
    catch (PKIException localPKIException)
    {
      throw new InitServerException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
  }

  public String getP10String()
    throws InitServerException
  {
    String str = null;
    PKCS10 localPKCS10 = new PKCS10(this.session);
    try
    {
      str = new String(localPKCS10.generateCertificationRequestData_B64("SHA1withRSAEncryption", this.DN, this.pubKey, null, this.priKey));
    }
    catch (PKIException localPKIException)
    {
      throw new InitServerException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
    return str;
  }

  public Key getPrivateKey()
    throws InitServerException
  {
    Key localKey = null;
    try
    {
      localKey = Parser.convertKey(this.priKey);
    }
    catch (PKIException localPKIException)
    {
      throw new InitServerException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
    return localKey;
  }

  public JKeyPair getKeyPair()
  {
    return this.keyPair;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.GenP10
 * JD-Core Version:    0.6.0
 */