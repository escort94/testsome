package cn.com.jit.ida.ca.certmanager.service.iss;

import cn.com.jit.ida.ca.config.CAConfig;
import java.util.Date;
import java.util.Timer;

public class CertSchedule
{
  private static CertSchedule certSchedule = null;
  private Timer timer;
  private static long period;
  private static final long DEFAULT_INTERVAL_TIME = 86400000L;

  private static synchronized void setPeriod(long paramLong)
  {
    period = paramLong;
  }

  private CertSchedule()
  {
    period = getPeriodSet();
  }

  private static long getPeriodSet()
  {
    long l;
    try
    {
      CAConfig localCAConfig = CAConfig.getInstance();
      l = localCAConfig.getCertPubPeriods();
    }
    catch (Exception localException)
    {
      l = 86400000L;
    }
    if (l < 0L)
      l = 86400000L;
    return l;
  }

  public static CertSchedule getInstance()
  {
    if (certSchedule == null)
    {
      certSchedule = new CertSchedule();
      return certSchedule;
    }
    setPeriod(getPeriodSet());
    return certSchedule;
  }

  public void start()
  {
    if (period == 0L)
      return;
    this.timer = new Timer(true);
    this.timer.schedule(new CertTimerTask(), new Date(), period);
  }

  public void cancel()
  {
    this.timer.cancel();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.iss.CertSchedule
 * JD-Core Version:    0.6.0
 */