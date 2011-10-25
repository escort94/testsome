package cn.com.jit.ida.ca.log.service.iss;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.config.AutoServiceConfig;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.log.service.LogArchive;
import cn.com.jit.ida.ca.log.service.request.LogArchiveRequest;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogArcIss
{
  private SysLogger sysLogger = LogManager.getSysLogger();

  public void issArcLog()
    throws IDAException
  {
    try
    {
      LogArchiveRequest localLogArchiveRequest = new LogArchiveRequest();
      AutoServiceConfig localAutoServiceConfig = AutoServiceConfig.getInstance();
      long l1;
      try
      {
        l1 = localAutoServiceConfig.getLogBeforeDays() * 24L * 60L * 60L * 1000L;
      }
      catch (Exception localException)
      {
        l1 = 15552000000L;
      }
      Date localDate = new Date();
      long l2 = localDate.getTime();
      long l3 = l2 - l1;
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
      Operator localOperator = new Operator();
      localOperator.setOperatorDN("系统管理员");
      localOperator.setOperatorSN("系统管理员");
      localLogArchiveRequest.setOperator(localOperator);
      localLogArchiveRequest.setEndTime(localSimpleDateFormat.format(new Date(l3)));
      LogArchive localLogArchive = new LogArchive();
      Response localResponse = localLogArchive.dealRequest(localLogArchiveRequest);
      if ((localResponse.getErr() != null) && (localResponse.getErr().equals("0")))
        this.sysLogger.info("后台业务日志归档操作成功");
      else
        this.sysLogger.info("后台业务日志归档操作失败");
    }
    catch (DBException localDBException)
    {
      this.sysLogger.info("后台业务日志归档操作异常 " + localDBException.toString());
      return;
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.log.service.iss.LogArcIss
 * JD-Core Version:    0.6.0
 */