package cn.com.jit.ida.ca.control;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.ca.initserver.InitServerException;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.JKey;
import cn.com.jit.ida.util.pki.cipher.Mechanism;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.encoders.Base64;
import java.io.PrintStream;
import java.util.Date;
import java.util.StringTokenizer;

public class securePackage
{
  String command;
  String secureCommand;
  CryptoConfig cryptoConfig = null;
  CAConfig caConfig = null;
  Session session;
  Session cryptoSession;
  private static String DELIM = "||";

  public securePackage(String paramString1, String paramString2)
    throws InitServerException
  {
    this.command = paramString1;
    this.secureCommand = paramString2;
    try
    {
      JCrypto localJCrypto;
      String str;
      try
      {
        this.cryptoConfig = CryptoConfig.getInstance();
        localJCrypto = JCrypto.getInstance();
        str = this.cryptoConfig.getDeviceID();
        if (str.equalsIgnoreCase("JSOFT_LIB"))
        {
          localJCrypto.initialize(str, null);
        }
        else
        {
          localJCrypto.initialize("JSOFT_LIB", null);
          localJCrypto.initialize(str, new Integer(2));
        }
        this.caConfig = CAConfig.getInstance();
      }
      catch (IDAException localIDAException)
      {
        throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDescEx(), localIDAException);
      }
      this.session = localJCrypto.openSession(str);
      this.cryptoSession = localJCrypto.openSession("JSOFT_LIB");
    }
    catch (PKIException localPKIException)
    {
      throw new InitServerException(localPKIException.getErrCode(), localPKIException.getErrDesc(), localPKIException);
    }
  }

  public String getCommand()
  {
    if (this.command != null)
      return this.command;
    if (this.secureCommand == null)
      return null;
    try
    {
      this.command = Decode(this.secureCommand);
    }
    catch (InitServerException localInitServerException)
    {
      System.out.println(localInitServerException.toString());
    }
    return this.command;
  }

  public String getSecureCommand()
    throws InitServerException
  {
    if (this.secureCommand != null)
      return this.secureCommand;
    if (this.command == null)
      return null;
    this.secureCommand = Encode(this.command);
    return this.secureCommand;
  }

  private String Decode(String paramString)
    throws InitServerException
  {
    String str1 = null;
    byte[] arrayOfByte1 = null;
    StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString, DELIM);
    if (localStringTokenizer1.hasMoreElements())
      str1 = new String(Base64.decode(localStringTokenizer1.nextToken()));
    else
      return null;
    if (localStringTokenizer1.hasMoreTokens())
    {
      String str2 = localStringTokenizer1.nextToken();
      arrayOfByte1 = Base64.decode(str2);
    }
    else
    {
      return null;
    }
    try
    {
      if (!this.session.verifySign(new Mechanism("SHA1withRSAEncryption"), this.caConfig.getRootCert().getPublicKey(), str1.getBytes(), arrayOfByte1))
        return null;
    }
    catch (PKIException localPKIException1)
    {
      throw new InitServerException(localPKIException1.getErrCode(), localPKIException1.getErrDesc(), localPKIException1);
    }
    localStringTokenizer1 = new StringTokenizer(str1, DELIM);
    String str3 = null;
    if (localStringTokenizer1.hasMoreTokens())
      str3 = localStringTokenizer1.nextToken();
    else
      return null;
    String str4 = null;
    if (localStringTokenizer1.hasMoreTokens())
      str4 = localStringTokenizer1.nextToken();
    else
      return null;
    byte[] arrayOfByte2;
    try
    {
      arrayOfByte2 = this.session.decrypt(new Mechanism("RSA/ECB/PKCS1PADDING"), this.caConfig.getPrivateKey(), Base64.decode(str4));
    }
    catch (PKIException localPKIException2)
    {
      throw new InitServerException(localPKIException2.getErrCode(), localPKIException2.getErrDesc(), localPKIException2);
    }
    String str5 = null;
    Object localObject;
    try
    {
      JKey localJKey = new JKey("RC4", arrayOfByte2);
      localObject = this.cryptoSession.decrypt(new Mechanism("RC4"), localJKey, Base64.decode(str3));
      str5 = new String(localObject);
    }
    catch (PKIException localPKIException3)
    {
      throw new InitServerException(localPKIException3.getErrCode(), localPKIException3.getErrDesc(), localPKIException3);
    }
    StringTokenizer localStringTokenizer2 = new StringTokenizer(str5, DELIM);
    if (localStringTokenizer2.hasMoreTokens())
      localObject = localStringTokenizer2.nextToken();
    else
      return null;
    if (localStringTokenizer2.hasMoreTokens())
      long l1 = Long.parseLong(localStringTokenizer2.nextToken());
    else
      return null;
    Date localDate = new Date();
    long l2 = localDate.getTime();
    return (String)new String(Base64.decode((String)localObject));
  }

  private String Encode(String paramString)
    throws InitServerException
  {
    StringBuffer localStringBuffer1 = new StringBuffer(3);
    StringBuffer localStringBuffer2 = new StringBuffer(3);
    byte[] arrayOfByte1 = Base64.encode(paramString.getBytes());
    localStringBuffer1.append(new String(arrayOfByte1));
    localStringBuffer1.append(DELIM);
    Date localDate = new Date();
    long l = localDate.getTime();
    localStringBuffer1.append(Long.toString(l));
    JKey localJKey = null;
    try
    {
      localJKey = this.cryptoSession.generateKey(new Mechanism("RC4"), 56);
      byte[] arrayOfByte2 = this.cryptoSession.encrypt(new Mechanism("RC4"), localJKey, localStringBuffer1.toString().getBytes());
      localObject = this.session.encrypt(new Mechanism("RSA/ECB/PKCS1PADDING"), this.caConfig.getRootCert().getPublicKey(), localJKey.getKey());
      localStringBuffer2.append(new String(Base64.encode(arrayOfByte2)));
      localStringBuffer2.append(DELIM);
      localStringBuffer2.append(new String(Base64.encode(localObject)));
    }
    catch (PKIException localPKIException1)
    {
      throw new InitServerException(localPKIException1.getErrCode(), localPKIException1.getErrDesc(), localPKIException1);
    }
    String str = localStringBuffer2.toString();
    Object localObject = new StringBuffer(3);
    byte[] arrayOfByte3 = null;
    try
    {
      arrayOfByte3 = this.session.sign(new Mechanism("SHA1withRSAEncryption"), this.caConfig.getPrivateKey(), str.getBytes());
    }
    catch (PKIException localPKIException2)
    {
      throw new InitServerException(localPKIException2.getErrCode(), localPKIException2.getErrDesc(), localPKIException2);
    }
    ((StringBuffer)localObject).append(new String(Base64.encode(str.getBytes())));
    ((StringBuffer)localObject).append(DELIM);
    ((StringBuffer)localObject).append(new String(Base64.encode(arrayOfByte3)));
    return (String)((StringBuffer)localObject).toString();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.control.securePackage
 * JD-Core Version:    0.6.0
 */