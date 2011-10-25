package cn.com.jit.ida.ca.ctml.x509v3.extension;

class BooleanString
  implements ExtensionValueFilter
{
  public boolean validate(String paramString)
  {
    if ("TRUE".equalsIgnoreCase(paramString))
      return true;
    return "FALSE".equalsIgnoreCase(paramString);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.BooleanString
 * JD-Core Version:    0.6.0
 */