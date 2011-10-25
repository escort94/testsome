package cn.com.jit.ida.ca.ctml;

import cn.com.jit.ida.IDAException;

public abstract interface CTMLProcessor
{
  public abstract byte[] generateCertificate()
    throws IDAException;
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.CTMLProcessor
 * JD-Core Version:    0.6.0
 */