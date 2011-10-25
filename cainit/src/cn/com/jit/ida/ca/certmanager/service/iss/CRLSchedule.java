package cn.com.jit.ida.ca.certmanager.service.iss;

import cn.com.jit.ida.ca.config.CRLConfig;
import java.io.PrintStream;
import java.util.Date;
import java.util.Timer;

public class CRLSchedule
{
  private static CRLSchedule crlSchedule = null;
  private Timer timer = new Timer(true);
  private static long period;
  public static final long DEFAULT_INTERVAL_TIME = 86400000L;

  private static synchronized void setPeriod(long paramLong)
  {
    period = paramLong;
  }

  private CRLSchedule()
  {
    period = getPeriodSet();
  }

  private static long getPeriodSet()
  {
    long l;
    try
    {
      CRLConfig localCRLConfig = CRLConfig.getInstance();
      l = localCRLConfig.getPeriods();
    }
    catch (Exception localException)
    {
      l = 86400000L;
    }
    if (l < 0L)
      l = 86400000L;
    return l;
  }

  public static CRLSchedule getInstance()
  {
    if (crlSchedule == null)
    {
      crlSchedule = new CRLSchedule();
      return crlSchedule;
    }
    setPeriod(getPeriodSet());
    return crlSchedule;
  }

  public void start()
  {
    if (period == 0L)
      return;
    this.timer.schedule(new CRLTimerTask(true), new Date(), period);
  }

  public void restart(long paramLong)
  {
    if (period == 0L)
      return;
    this.timer.scheduleAtFixedRate(new CRLTimerTask(false), paramLong, paramLong);
  }

  public void cancel()
  {
    this.timer.cancel();
  }

  public static void main(String[] paramArrayOfString)
  {
    CRLSchedule localCRLSchedule = getInstance();
    localCRLSchedule.start();
    CertSchedule localCertSchedule = CertSchedule.getInstance();
    localCertSchedule.start();
    System.out.println("OK");
    label24: break label24;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.iss.CRLSchedule
 * JD-Core Version:    0.6.0
 */