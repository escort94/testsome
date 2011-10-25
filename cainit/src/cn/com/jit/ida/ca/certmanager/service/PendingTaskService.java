package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.ca.certmanager.reqinfo.CertPendingRevokeInfo;
import cn.com.jit.ida.ca.config.AutoServiceConfig;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

public class PendingTaskService extends Thread
{
  private long sleeptime = 0L;

  private void pendingTask()
  {
    try
    {
      this.sleeptime = AutoServiceConfig.getInstance().getCheckUpdateServiceInterval();
      if (this.sleeptime == 0L)
        return;
      pendingIfEnd();
      sleep(this.sleeptime * 60L * 1000L);
    }
    catch (Exception localException)
    {
    }
  }

  public void run()
  {
    while (true)
      pendingTask();
  }

  private void pendingIfEnd()
  {
    Vector localVector = new Vector();
    Enumeration localEnumeration = null;
    long l1 = 0L;
    try
    {
      localVector = DBManager.getInstance().pendingTaskVector();
      if (localVector.size() == 0)
        return;
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
      long l2 = Long.parseLong(localSimpleDateFormat.format(new Date(System.currentTimeMillis())));
      localEnumeration = localVector.elements();
      CertPendingRevokeInfo localCertPendingRevokeInfo = new CertPendingRevokeInfo();
      while (localEnumeration.hasMoreElements())
      {
        localCertPendingRevokeInfo = (CertPendingRevokeInfo)localEnumeration.nextElement();
        l1 = localCertPendingRevokeInfo.getExectime();
        if (l2 < l1)
          continue;
        DBManager.getInstance().pendingTaskCertRevoke(localCertPendingRevokeInfo);
      }
    }
    catch (DBException localDBException)
    {
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.PendingTaskService
 * JD-Core Version:    0.6.0
 */