package cn.com.jit.ida.ca;

import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.util.pki.cipher.JCrypto;

class FinalizeCryptoDevice extends Thread
{
  public void doFinalize()
  {
    try
    {
      CryptoConfig localCryptoConfig = CryptoConfig.getInstance();
      if (localCryptoConfig == null)
        return;
      String str = localCryptoConfig.getDeviceID();
      JCrypto localJCrypto = JCrypto.getInstance();
      boolean bool = localJCrypto.finalize(str, null);
    }
    catch (Exception localException)
    {
    }
  }

  public void run()
  {
    doFinalize();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.FinalizeCryptoDevice
 * JD-Core Version:    0.6.0
 */