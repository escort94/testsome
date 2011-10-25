package cn.com.jit.ida.ca.ctml.x509v3.extension;

class PrintableString
  implements ExtensionValueFilter
{
  private static final String CHARSET = "0123456789ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz '+,-./:=?()";

  public boolean validate(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    for (int i = 0; i < arrayOfChar.length; i++)
      if ("0123456789ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz '+,-./:=?()".indexOf(arrayOfChar[i]) < 0)
        return false;
    return true;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.PrintableString
 * JD-Core Version:    0.6.0
 */