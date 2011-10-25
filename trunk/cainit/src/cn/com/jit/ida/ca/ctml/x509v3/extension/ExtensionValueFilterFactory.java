package cn.com.jit.ida.ca.ctml.x509v3.extension;

public class ExtensionValueFilterFactory
{
  public static ExtensionValueFilter createFilter(String paramString)
  {
    if ("Boolean".equalsIgnoreCase(paramString))
      return new BooleanString();
    if ("Integer".equalsIgnoreCase(paramString))
      return new IntegerString();
    if ("Printable String".equalsIgnoreCase(paramString))
      return new PrintableString();
    if ("User Defined".equalsIgnoreCase(paramString))
      return new UserDefine();
    if ("IA5 String".equalsIgnoreCase(paramString))
      return new IA5String();
    return new NotLimit();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.ExtensionValueFilterFactory
 * JD-Core Version:    0.6.0
 */