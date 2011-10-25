package cn.com.jit.ida.ca.updataRootCert;

import cn.com.jit.ida.IDAException;

public class UpdataRootException extends IDAException
{
  public static final String MODAL_NAME = "80";
  public static final String FLOW_NAME = "66";
  public static final String ERROR_HEADER = "8066";
  public static final String SYSTEM_ERROR = "0899";
  public static final String Key_Type_ERROR = "0801";
  public static final String Device_Name_ERROR = "0802";
  public static final String GENERATE_KEY_STORE_ERROR = "0803";
  public static final String GENERATE_KEY_STORE_FILE_ERROR = "0804";
  public static final String SAVE_KEY_STORE_ERROR = "0805";

  public UpdataRootException(String paramString)
  {
    super("8066" + paramString);
  }

  public UpdataRootException(String paramString, Exception paramException)
  {
    super("8066" + paramString, paramException);
  }

  public UpdataRootException(String paramString1, String paramString2)
  {
    super("8066" + paramString1, paramString2);
  }

  public UpdataRootException(String paramString1, String paramString2, Exception paramException)
  {
    super("8066" + paramString1, paramString2, paramException);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.updataRootCert.UpdataRootException
 * JD-Core Version:    0.6.0
 */