package cn.com.jit.ida.ca.ctml.x509v3.extension;

import cn.com.jit.ida.util.pki.Parser;

class UserDefine
  implements ExtensionValueFilter
{
  public boolean validate(String paramString)
  {
    return Parser.isBase64Encode(paramString.getBytes());
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.UserDefine
 * JD-Core Version:    0.6.0
 */