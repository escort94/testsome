package cn.com.jit.ida.log;

public class Operation
{
  public static final int SUCCESS = 1;
  public static final int FAILTURE = 0;
  private String id = null;
  private String operatorSN = null;
  private String operatorDN = null;
  private String objCertSN = null;
  private String objSubject = null;
  private String objCTMLName = null;
  private String optType = null;
  private long optTime = 0L;
  private int result = -1;
  private String optTimeBegin = null;
  private String optTimeEnd = null;
  private String signServer = null;
  private String signClient = null;
  private static final String OVERLEN = "#*#";

  public void setObjCertSN(String paramString)
  {
    if ((paramString != null) && (paramString.getBytes().length > 40))
      paramString = splitStr(paramString, 37) + "#*#";
    this.objCertSN = paramString;
  }

  public void setObjCTMLName(String paramString)
  {
    if ((paramString != null) && (paramString.getBytes().length > 255))
      paramString = splitStr(paramString, 252) + "#*#";
    this.objCTMLName = paramString;
  }

  public void setObjSubject(String paramString)
  {
    if ((paramString != null) && (paramString.getBytes().length > 255))
      paramString = splitStr(paramString, 252) + "#*#";
    this.objSubject = paramString;
  }

  public void setOptType(String paramString)
  {
    if ((paramString != null) && (paramString.getBytes().length > 30))
      paramString = splitStr(paramString, 27) + "#*#";
    this.optType = paramString;
  }

  public void setResult(int paramInt)
  {
    this.result = paramInt;
  }

  public String getObjCertSN()
  {
    return this.objCertSN;
  }

  public String getObjSubject()
  {
    return this.objSubject;
  }

  public String getOptType()
  {
    return this.optType;
  }

  public int getResult()
  {
    return this.result;
  }

  public String getObjCTMLName()
  {
    return this.objCTMLName;
  }

  public String getOperatorDN()
  {
    return this.operatorDN;
  }

  public String getOperatorSN()
  {
    return this.operatorSN;
  }

  public void setOperatorSN(String paramString)
  {
    if ((paramString != null) && (paramString.getBytes().length > 40))
      paramString = splitStr(paramString, 37) + "#*#";
    this.operatorSN = paramString;
  }

  public void setOperatorDN(String paramString)
  {
    if ((paramString != null) && (paramString.getBytes().length > 255))
      paramString = splitStr(paramString, 37) + "#*#";
    this.operatorDN = paramString;
  }

  public long getOptTime()
  {
    return this.optTime;
  }

  public String getOptTimeBegin()
  {
    return this.optTimeBegin;
  }

  public String getOptTimeEnd()
  {
    return this.optTimeEnd;
  }

  public String getId()
  {
    return this.id;
  }

  public String getSignClient()
  {
    return this.signClient;
  }

  public String getSignServer()
  {
    return this.signServer;
  }

  public void setOptTime(long paramLong)
  {
    this.optTime = paramLong;
  }

  public void setOptTimeBegin(String paramString)
  {
    this.optTimeBegin = paramString;
  }

  public void setOptTimeEnd(String paramString)
  {
    this.optTimeEnd = paramString;
  }

  public void setId(String paramString)
  {
    this.id = paramString;
  }

  public void setSignClient(String paramString)
  {
    this.signClient = paramString;
  }

  public void setSignServer(String paramString)
  {
    this.signServer = paramString;
  }

  private String splitStr(String paramString, int paramInt)
  {
    while (paramString.getBytes().length > paramInt)
    {
      int i = paramString.getBytes().length;
      int j = paramString.length();
      int k = (i - paramInt) / 2;
      if (k == 0)
        return paramString.substring(0, j - 1);
      paramString = paramString.substring(0, j - k);
    }
    return paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.log.Operation
 * JD-Core Version:    0.6.0
 */