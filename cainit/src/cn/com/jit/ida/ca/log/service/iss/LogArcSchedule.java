package cn.com.jit.ida.ca.log.service.iss;

import cn.com.jit.ida.ca.config.AutoServiceConfig;
import java.util.Date;
import java.util.Timer;

public class LogArcSchedule
{
  private static LogArcSchedule logArcSchedule = null;
  private Timer timer;
  private static long period;
  private static final long DEFAULT_INTERVAL_TIME = 15552000000L;

  private static synchronized void setPeriod(long paramLong)
  {
    period = paramLong;
  }

  private LogArcSchedule()
  {
    period = getPeriodSet();
  }

  private static long getPeriodSet()
  {
    long l;
    try
    {
      AutoServiceConfig localAutoServiceConfig = AutoServiceConfig.getInstance();
      l = localAutoServiceConfig.getAutoLogArchiveInterval() * 24L * 60L * 60L * 1000L;
    }
    catch (Exception localException)
    {
      l = 15552000000L;
    }
    if (l < 0L)
      l = 15552000000L;
    return l;
  }

  public static LogArcSchedule getInstance()
  {
    if (logArcSchedule == null)
    {
      logArcSchedule = new LogArcSchedule();
      return logArcSchedule;
    }
    setPeriod(getPeriodSet());
    return logArcSchedule;
  }

  public void start()
  {
    if (period == 0L)
      return;
    this.timer = new Timer(true);
    this.timer.schedule(new LogArcTimerTask(), new Date(), period);
  }

  public void cancel()
  {
    this.timer.cancel();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.log.service.iss.LogArcSchedule
 * JD-Core Version:    0.6.0
 */