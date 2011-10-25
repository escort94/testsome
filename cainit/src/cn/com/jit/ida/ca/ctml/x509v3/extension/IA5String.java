package cn.com.jit.ida.ca.ctml.x509v3.extension;

class IA5String
  implements ExtensionValueFilter
{
  public boolean validate(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    for (int i = 0; i < arrayOfChar.length; i++)
    {
      int j = arrayOfChar[i];
      if ((j < 0) || (j > 127))
        return false;
    }
    return true;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.IA5String
 * JD-Core Version:    0.6.0
 */