package cn.com.jit.ida.ca.ctml;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.util.CTMLException;
import cn.com.jit.ida.ca.ctml.util.CTMLException.CTML;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTMLPolicy;

public class CTMLPolicyFactory
{
  public static CTMLPolicy newCTMLPolicyInstance(String paramString)
    throws IDAException
  {
    if (paramString.equalsIgnoreCase("X509V3"))
      return new X509V3CTMLPolicy();
    CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALIDCTMLTYPE);
    localCTMLException.appendMsg(paramString);
    throw localCTMLException;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.CTMLPolicyFactory
 * JD-Core Version:    0.6.0
 */