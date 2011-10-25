package cn.com.jit.ida.ca.ctml;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
import java.io.PrintStream;

public class CTMLFactory
{
  public static CTML newCTMLInstance(CTMLInformation paramCTMLInformation)
  {
    try
    {
      if (paramCTMLInformation.getCTMLType().equalsIgnoreCase("X509V3"))
        return new X509V3CTML(paramCTMLInformation);
    }
    catch (IDAException localIDAException)
    {
      System.err.println(localIDAException);
      throw new RuntimeException(localIDAException);
    }
    return null;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.CTMLFactory
 * JD-Core Version:    0.6.0
 */