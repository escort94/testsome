package cn.com.jit.ida.ca.certmanager.service.iss;

import java.util.TimerTask;

class CertTimerTask extends TimerTask
{
  public void run()
  {
    CertIss localCertIss = new CertIss();
    localCertIss.issCert();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.iss.CertTimerTask
 * JD-Core Version:    0.6.0
 */