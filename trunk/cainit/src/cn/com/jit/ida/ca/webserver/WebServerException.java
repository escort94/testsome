package cn.com.jit.ida.ca.webserver;

import cn.com.jit.ida.IDAException;

public class WebServerException extends IDAException
{
  private static final String WEB_SERVER = "85";
  public static final String HTTPS_SERVER_START_ERR = "8500";
  public static final String HTTPS_SERVER_START_ERR_DES = "HTTPS 服务启动失败";
  public static final String HTTPS_SERVER_STOP_ERR = "8501";
  public static final String HTTPS_SERVER_STOP_ERR_DES = "HTTPS 服务关闭失败";
  public static final String HTTPS_SERVER_NOCLIENT_START_ERR = "8502";
  public static final String HTTPS_SERVER_NOCLIENT_START_ERR_DES = "无客户端验证模式的HTTPS 服务启动失败";
  public static final String HTTPS_SERVER_NOCLIENT_STOP_ERR = "8503";
  public static final String HTTPS_SERVER_NOCLIENT_STOP_ERR_DES = "无客户端验证模式的HTTPS 服务关闭失败";
  public static final String HTTP_SERVER_START_ERR = "8504";
  public static final String HTTP_SERVER_START_ERR_DES = "HTTP 服务启动失败";
  public static final String HTTP_SERVER_STOP_ERR = "8505";
  public static final String HTTP_SERVER_STOP_ERR_DES = "HTTP 服务关闭失败";

  public WebServerException(String paramString)
  {
    super(paramString);
  }

  public WebServerException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }

  public WebServerException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }

  public WebServerException(String paramString1, String paramString2, Exception paramException)
  {
    super(paramString1, paramString2, paramException);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.webserver.WebServerException
 * JD-Core Version:    0.6.0
 */