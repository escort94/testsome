package cn.com.jit.ida.log;

import org.apache.log4j.Logger;

public class DebugLogger
{
  private static final String NEWLINE = "\r\n";
  private Logger logger;
  private StringBuffer buffer1 = new StringBuffer();
  private StringBuffer buffer2 = new StringBuffer();

  public DebugLogger(String paramString)
  {
    this.logger = Logger.getLogger(paramString);
  }

  public void appendMsg_L1(String paramString)
  {
    if (LogManager.isL1())
    {
      if (this.buffer1.length() == 0)
        this.buffer1.append("\r\n");
      this.buffer1.append(paramString);
      this.buffer1.append("\r\n");
    }
    appendMsg_L2(paramString);
  }

  public void appendMsg_L2(String paramString)
  {
    if (LogManager.isL2())
    {
      if (this.buffer2.length() == 0)
        this.buffer2.append("\r\n");
      this.buffer2.append(paramString);
      this.buffer2.append("\r\n");
    }
  }

  public void doLog()
  {
    if (this.buffer1.length() != 0)
    {
      this.logger.info(this.buffer1.toString());
      this.buffer1 = new StringBuffer();
    }
    if (this.buffer2.length() != 0)
    {
      this.logger.debug(this.buffer2.toString());
      this.buffer2 = new StringBuffer();
    }
  }

  public void doLog(Throwable paramThrowable)
  {
    this.logger.info(this.buffer1.toString() + paramThrowable.toString());
    if (this.buffer1.length() != 0)
      this.buffer1 = new StringBuffer();
    this.logger.debug(this.buffer2.toString(), paramThrowable);
    if (this.buffer2.length() != 0)
      this.buffer2 = new StringBuffer();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.log.DebugLogger
 * JD-Core Version:    0.6.0
 */