package cn.com.jit.ida;

public class IDAException extends Exception
{
  protected String errCode = null;
  protected String errDesc = null;
  protected Exception history = null;
  protected String errDescEx = null;

  public IDAException(String paramString)
  {
    this.errCode = paramString;
  }

  public IDAException(String paramString, Exception paramException)
  {
    this.errCode = paramString;
    this.history = paramException;
  }

  public IDAException(String paramString1, String paramString2)
  {
    this.errCode = paramString1;
    this.errDesc = paramString2;
  }

  public IDAException(String paramString1, String paramString2, Exception paramException)
  {
    this.errCode = paramString1;
    this.errDesc = paramString2;
    this.history = paramException;
  }

  public String getErrCode()
  {
    return this.errCode;
  }

  public String getErrDesc()
  {
    return this.errDesc;
  }

  public Exception getHistory()
  {
    return this.history;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(this.errCode + ": ");
    if (this.errDesc != null)
      localStringBuffer.append(getErrDescEx() + "\n");
    if (this.history != null)
      localStringBuffer.append(this.history.toString());
    return localStringBuffer.toString();
  }

  public void appendMsg(String paramString)
  {
    this.errDescEx = paramString;
  }

  public String getErrDescEx()
  {
    if (this.errDescEx == null)
      return this.errDesc;
    return this.errDesc + "  " + this.errDescEx;
  }

  public String getMessage()
  {
    StringBuffer localStringBuffer = new StringBuffer(4);
    localStringBuffer.append("错误码：");
    localStringBuffer.append(this.errCode);
    localStringBuffer.append("  错误信息：");
    localStringBuffer.append(this.errDesc);
    if (this.errDescEx != null)
      localStringBuffer.append("(" + this.errDescEx + ")");
    return localStringBuffer.toString();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.IDAException
 * JD-Core Version:    0.6.0
 */