package cn.com.jit.ida.ca.certmanager.service.iss;

import java.util.TimerTask;

class CertArcTimerTask extends TimerTask
{
  public void run()
  {
    CertArcIss localCertArcIss = new CertArcIss();
    localCertArcIss.issArcCert();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.iss.CertArcTimerTask
 * JD-Core Version:    0.6.0
 */