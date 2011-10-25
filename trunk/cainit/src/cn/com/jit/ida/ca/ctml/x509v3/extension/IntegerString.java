package cn.com.jit.ida.ca.ctml.x509v3.extension;

import java.math.BigInteger;

class IntegerString
  implements ExtensionValueFilter
{
  public boolean validate(String paramString)
  {
    try
    {
      BigInteger localBigInteger = new BigInteger(paramString, 10);
    }
    catch (Exception localException)
    {
      return false;
    }
    return true;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.IntegerString
 * JD-Core Version:    0.6.0
 */