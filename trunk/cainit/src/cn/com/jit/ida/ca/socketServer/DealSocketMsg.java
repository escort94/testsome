package cn.com.jit.ida.ca.socketServer;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.Dispenser;
import cn.com.jit.ida.net.PeerInfo;
import cn.com.jit.ida.net.server.DealRequest;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.cert.X509Cert;

public class DealSocketMsg
  implements DealRequest
{
  public byte[] process(byte[] paramArrayOfByte, PeerInfo paramPeerInfo)
  {
    Request localRequest = null;
    Operator localOperator = new Operator();
    localOperator.setOperatorCert(paramPeerInfo.getPeerCert());
    X509Cert localX509Cert = null;
    try
    {
      localX509Cert = new X509Cert(paramPeerInfo.getPeerCert());
    }
    catch (PKIException localPKIException)
    {
      return null;
    }
    localOperator.setOperatorDN(localX509Cert.getSubject());
    localOperator.setOperatorSN(localX509Cert.getStringSerialNumber());
    try
    {
      try
      {
        localRequest = new Request(paramArrayOfByte);
        localRequest.setOperator(localOperator);
      }
      catch (Exception localException1)
      {
        throw new IDAException("Bug!!!!!!!!!!!!!!!!!!!");
      }
    }
    catch (IDAException localIDAException)
    {
      Response localResponse2 = new Response();
      localResponse2.setErr(localIDAException.getErrCode());
      localResponse2.setMsg(localIDAException.getErrDesc());
      try
      {
        return localResponse2.getData();
      }
      catch (Exception localException3)
      {
        return null;
      }
    }
    Response localResponse1 = Dispenser.dealRequest(localRequest);
    try
    {
      return localResponse1.getData();
    }
    catch (Exception localException2)
    {
      Response localResponse3 = new Response();
      localResponse3.setErr("80009998");
      localResponse3.setMsg("应答包构建失败");
      try
      {
        return localResponse3.getData();
      }
      catch (Exception localException4)
      {
      }
    }
    return null;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.socketServer.DealSocketMsg
 * JD-Core Version:    0.6.0
 */