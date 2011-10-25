package cn.com.jit.ida.net;

public class NetException extends Exception
{
  public static String ModalName = "";
  private static final String CC = "85";
  public static final String ADDRESS_ERROR = "01";
  public static final String PORT_ERROR = "02";
  public static final String CONNECTREFUSE_ERROR = "03";
  public static final String TIMEOUT_ERROR = "04";
  public static final String GETPEERCERT_ERROR = "05";
  public static final String ACCEPT_ERROR = "06";
  public static final String READ_DATA_ERROR = "07";
  public static final String ALGORITHM_ERROR = "08";
  public static final String KEYSTORE_TYPE_ERROR = "09";
  public static final String KEYSTORE_NOTFOUND_ERROR = "10";
  public static final String KEYSTORE_FILE_ERROR = "11";
  public static final String GET_KEY_ERROR = "12";
  public static final String CREAT_SECURE_CON_ERROR = "13";
  public static final String PORT_HAVE_BE_BIND_ERROR = "14";
  public static final String KEYSTORE_PWD_ERROR = "15";
  public static final String WRITE_DATA_ERROR = "16";
  public static final String STOP_SERVER_ERROR = "17";
  public static final String PAUSE_SERVER_ERROR = "18";
  public static final String SERVER_HAVE_BEEN_CLOSE_ERROR = "19";
  public static final String PROVIDER_TYPE_ERROR = "98";
  public static final String SYSTEM_ERROR = "99";
  protected String errCode = null;
  protected String errDesc = null;
  protected Exception history = null;
  protected String errDescEx = null;

  public NetException(String paramString)
  {
    this.errCode = ("85" + paramString);
  }

  public NetException(String paramString, Exception paramException)
  {
    this.errCode = ("85" + paramString);
    this.history = paramException;
  }

  public NetException(String paramString1, String paramString2)
  {
    this.errCode = ("85" + paramString1);
    this.errDesc = paramString2;
  }

  public NetException(String paramString1, String paramString2, Exception paramException)
  {
    this.errCode = ("85" + paramString1);
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
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.net.NetException
 * JD-Core Version:    0.6.0
 */