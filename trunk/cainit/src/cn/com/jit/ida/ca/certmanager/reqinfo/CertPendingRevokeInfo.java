package cn.com.jit.ida.ca.certmanager.reqinfo;

public class CertPendingRevokeInfo
{
  private String taskID = null;
  private String certSN = null;
  private String subject = null;
  private String reasonDesc = null;
  private String optType = null;
  private String applicant = null;
  private String SIGN_SERVER = null;
  private String SIGN_CLIENT = null;
  private long CDPID = 0L;
  private long exectime = 0L;
  private int reasonID = 0;

  public String getApplicant()
  {
    return this.applicant;
  }

  public void setApplicant(String paramString)
  {
    this.applicant = paramString;
  }

  public String getTaskID()
  {
    return this.taskID;
  }

  public void setTaskID(String paramString)
  {
    this.taskID = paramString;
  }

  public long getCDPID()
  {
    return this.CDPID;
  }

  public void setCertSN(String paramString)
  {
    this.certSN = paramString;
  }

  public void setCDPID(long paramLong)
  {
    this.CDPID = paramLong;
  }

  public String getCertSN()
  {
    return this.certSN;
  }

  public long getExectime()
  {
    return this.exectime;
  }

  public String getOptType()
  {
    return this.optType;
  }

  public void setOptType(String paramString)
  {
    this.optType = paramString;
  }

  public void setExectime(long paramLong)
  {
    this.exectime = paramLong;
  }

  public String getReasonDesc()
  {
    return this.reasonDesc;
  }

  public int getReasonID()
  {
    return this.reasonID;
  }

  public String getSIGN_SERVER()
  {
    return this.SIGN_SERVER;
  }

  public String getSubject()
  {
    return this.subject;
  }

  public String getSIGN_CLIENT()
  {
    return this.SIGN_CLIENT;
  }

  public void setReasonID(int paramInt)
  {
    this.reasonID = paramInt;
  }

  public void setReasonDesc(String paramString)
  {
    this.reasonDesc = paramString;
  }

  public void setSIGN_CLIENT(String paramString)
  {
    this.SIGN_CLIENT = paramString;
  }

  public void setSIGN_SERVER(String paramString)
  {
    this.SIGN_SERVER = paramString;
  }

  public void setSubject(String paramString)
  {
    this.subject = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.reqinfo.CertPendingRevokeInfo
 * JD-Core Version:    0.6.0
 */