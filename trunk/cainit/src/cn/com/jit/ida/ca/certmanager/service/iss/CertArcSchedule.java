package cn.com.jit.ida.ca.certmanager.service.iss;

import cn.com.jit.ida.ca.config.AutoServiceConfig;
import java.util.Date;
import java.util.Timer;

public class CertArcSchedule
{
  private static CertArcSchedule certArcSchedule = null;
  private Timer timer;
  private static long period;
  private static final long DEFAULT_INTERVAL_TIME = 15552000000L;

  private static synchronized void setPeriod(long paramLong)
  {
    period = paramLong;
  }

  private CertArcSchedule()
  {
    period = getPeriodSet();
  }

  private static long getPeriodSet()
  {
    long l;
    try
    {
      AutoServiceConfig localAutoServiceConfig = AutoServiceConfig.getInstance();
      l = localAutoServiceConfig.getAutoCertArchiveInterval() * 24L * 60L * 60L * 1000L;
    }
    catch (Exception localException)
    {
      l = 15552000000L;
    }
    if (l < 0L)
      l = 15552000000L;
    return l;
  }

  public static CertArcSchedule getInstance()
  {
    if (certArcSchedule == null)
    {
      certArcSchedule = new CertArcSchedule();
      return certArcSchedule;
    }
    setPeriod(getPeriodSet());
    return certArcSchedule;
  }

  public void start()
  {
    if (period == 0L)
      return;
    this.timer = new Timer(true);
    this.timer.schedule(new CertArcTimerTask(), new Date(), period);
  }

  public void cancel()
  {
    this.timer.cancel();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.iss.CertArcSchedule
 * JD-Core Version:    0.6.0
 */