package cn.com.jit.ida.net.server;

import cn.com.jit.ida.net.PeerInfo;

public abstract interface DealRequest
{
  public abstract byte[] process(byte[] paramArrayOfByte, PeerInfo paramPeerInfo);
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.net.server.DealRequest
 * JD-Core Version:    0.6.0
 */