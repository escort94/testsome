package cn.com.jit.ida.ca.certmanager.service.iss;

import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.service.CertArchive;
import cn.com.jit.ida.ca.certmanager.service.request.CertArchiveRequest;
import cn.com.jit.ida.ca.config.AutoServiceConfig;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CertArcIss
{
  private SysLogger sysLogger = LogManager.getSysLogger();

  public void issArcCert()
  {
    try
    {
      CertArchiveRequest localCertArchiveRequest = new CertArchiveRequest();
      long l1;
      try
      {
        AutoServiceConfig localAutoServiceConfig = AutoServiceConfig.getInstance();
        l1 = localAutoServiceConfig.getCertAfterDays() * 24L * 60L * 60L * 1000L;
      }
      catch (Exception localException1)
      {
        l1 = 15552000000L;
      }
      Date localDate = new Date();
      long l2 = localDate.getTime();
      long l3 = l2 - l1;
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
      localCertArchiveRequest.setEndTime(localSimpleDateFormat.format(new Date(l3)));
      Operator localOperator = new Operator();
      localOperator.setOperatorDN("系统管理员");
      localOperator.setOperatorSN("系统管理员");
      localCertArchiveRequest.setOperator(localOperator);
      CertArchive localCertArchive = new CertArchive();
      Response localResponse = localCertArchive.dealRequest(localCertArchiveRequest);
      if ((localResponse.getErr() != null) && (localResponse.getErr().equals("0")))
        this.sysLogger.info("后台证书归档操作成功");
      else
        this.sysLogger.info("后台业务证书归档操作失败");
    }
    catch (Exception localException2)
    {
      this.sysLogger.info("后台证书归档操作异常 " + localException2.toString());
      return;
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.iss.CertArcIss
 * JD-Core Version:    0.6.0
 */