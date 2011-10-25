package cn.com.jit.ida.ca.config;

import java.util.Date;
import java.util.Timer;

public class LoadBalance
{
  private static LoadBalance instance = null;
  private Timer timer;
  static long period;
  private static final long DEFAULT_INTERVAL_TIME = 300000L;

  private static synchronized void setPeriod(long paramLong)
  {
    period = paramLong;
  }

  private LoadBalance()
  {
    period = getPeriodSet();
  }

  private static long getPeriodSet()
  {
    long l = 0L;
    try
    {
      CAConfig localCAConfig = CAConfig.getInstance();
      l = localCAConfig.getSynchrointerval();
    }
    catch (Exception localException)
    {
      l = 0L;
    }
    return l;
  }

  public static LoadBalance getInstance()
  {
    if ((period == 0L) && (getPeriodSet() == 0L))
      return null;
    if (instance == null)
    {
      instance = new LoadBalance();
      return instance;
    }
    setPeriod(getPeriodSet());
    return instance;
  }

  public void start()
  {
    if (this.timer != null)
      this.timer.cancel();
    this.timer = new Timer(true);
    this.timer.schedule(new LoadTimerTask(), new Date(), period);
  }

  public void cancel()
  {
    this.timer.cancel();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.LoadBalance
 * JD-Core Version:    0.6.0
 */