package cn.com.jit.ida.ca.certmanager.service.iss;

import cn.com.jit.ida.ca.config.CRLConfig;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;
import cn.com.jit.ida.util.pki.crl.X509CRL;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.TimerTask;

class CRLTimerTask extends TimerTask
{
  SysLogger sysLogger = LogManager.getSysLogger();
  boolean checkDate = true;

  public CRLTimerTask(boolean paramBoolean)
  {
    this.checkDate = paramBoolean;
  }

  public void run()
  {
    Object localObject1 = null;
    Object localObject2;
    X509CRL localX509CRL;
    try
    {
      CRLConfig localCRLConfig = CRLConfig.getInstance();
      localObject2 = localCRLConfig.getCRLPubAddressForService();
      if (localObject2 != null)
        for (int i = 0; i < localObject2.length; i++)
        {
          localX509CRL = localObject2[i][0];
          if (!localX509CRL.equals("URI"))
            continue;
          Object localObject3 = localObject2[i][1];
          localObject1 = localObject3;
          break;
        }
    }
    catch (Exception localException1)
    {
      localObject1 = "./crl";
    }
    if ((localObject1 != null) && (!((String)localObject1).endsWith(File.separator)))
      localObject1 = (String)localObject1 + File.separator;
    try
    {
      if (this.checkDate)
      {
        File localFile = new File((String)localObject1 + "crl1.crl");
        if (localFile.exists())
        {
          localObject2 = new FileInputStream(localFile);
          byte[] arrayOfByte = new byte[((FileInputStream)localObject2).available()];
          ((FileInputStream)localObject2).read(arrayOfByte);
          ((FileInputStream)localObject2).close();
          localX509CRL = new X509CRL(arrayOfByte);
          long l1 = localX509CRL.getNextUpdate().getTime();
          long l2 = new Date().getTime();
          if (l2 < l1)
            Thread.sleep(l1 - l2);
        }
      }
    }
    catch (Exception localException2)
    {
    }
    CRLIss localCRLIss = new CRLIss();
    localCRLIss.issCRL();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.iss.CRLTimerTask
 * JD-Core Version:    0.6.0
 */