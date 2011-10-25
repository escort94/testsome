package cn.com.jit.ida.log;

import org.apache.log4j.Logger;

public class SysLogger
{
  private Logger logger;

  public SysLogger(String paramString)
  {
    this.logger = Logger.getLogger(paramString);
  }

  public void info(Object paramObject)
  {
    if (this.logger.getAppender("IDA_SYS_LOG_APPENDER") == null)
    {
      LogManager.setIsInited(false);
      LogManager.init();
    }
    this.logger.warn(paramObject);
  }

  public void info(Object paramObject, Throwable paramThrowable)
  {
    if (this.logger.getAppender("IDA_SYS_LOG_APPENDER") == null)
    {
      LogManager.setIsInited(false);
      LogManager.init();
    }
    this.logger.warn(paramObject, paramThrowable);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.log.SysLogger
 * JD-Core Version:    0.6.0
 */