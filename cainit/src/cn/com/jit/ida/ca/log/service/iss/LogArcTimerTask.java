package cn.com.jit.ida.ca.log.service.iss;

import cn.com.jit.ida.IDAException;
import java.util.TimerTask;

class LogArcTimerTask extends TimerTask
{
  public void run()
  {
    LogArcIss localLogArcIss = new LogArcIss();
    try
    {
      localLogArcIss.issArcLog();
    }
    catch (IDAException localIDAException)
    {
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.log.service.iss.LogArcTimerTask
 * JD-Core Version:    0.6.0
 */