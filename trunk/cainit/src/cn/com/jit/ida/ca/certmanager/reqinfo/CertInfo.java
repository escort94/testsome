package cn.com.jit.ida.ca.certmanager.reqinfo;

import cn.com.jit.ida.util.pki.cipher.JKey;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class CertInfo
{
  private String certSN;
  private String authCode;
  private String ctmlName;
  private String subject;
  private String subjectUppercase;
  private long notBefore;
  private long notAfter;
  private int validity;
  private String applicant;
  private String applicantUppercase;
  private String email;
  private long cdpid;
  private Properties extensions = new Properties();
  private Vector standardExtensions = new Vector();
  private Hashtable standardExtensionsHT = new Hashtable();
  private String remark;
  private String certStatus;
  private long isValid;
  private long createTime;
  private long authCodeUpdateTime;
  private byte[] certEntity;
  private CertExtensions certExtensions;
  private JKey pubKey;
  private String revokeReason;
  private String revokeDesc;
  private String isRetainKey;
  private String signServer;
  private String signClient;
  private String iswaiting;
  private String oldSN;

  public String getIswaiting()
  {
    return this.iswaiting;
  }

  public String getApplicant()
  {
    return this.applicant;
  }

  public String getAuthCode()
  {
    return this.authCode;
  }

  public long getCdpid()
  {
    return this.cdpid;
  }

  public String getCertSN()
  {
    return this.certSN;
  }

  public String getCtmlName()
  {
    return this.ctmlName;
  }

  public String getEmail()
  {
    return this.email;
  }

  public Properties getExtensions()
  {
    return this.extensions;
  }

  public long getNotAfter()
  {
    return this.notAfter;
  }

  public long getNotBefore()
  {
    return this.notBefore;
  }

  public String getRemark()
  {
    return this.remark;
  }

  public String getSubject()
  {
    return this.subject;
  }

  public int getValidity()
  {
    return this.validity;
  }

  public void setValidity(int paramInt)
  {
    this.validity = paramInt;
  }

  public void setSubject(String paramString)
  {
    this.subject = paramString;
  }

  public void setRemark(String paramString)
  {
    this.remark = paramString;
  }

  public void setNotBefore(long paramLong)
  {
    this.notBefore = paramLong;
  }

  public void setNotAfter(long paramLong)
  {
    this.notAfter = paramLong;
  }

  public void setExtensions(Properties paramProperties)
  {
    this.extensions = paramProperties;
  }

  public void setEmail(String paramString)
  {
    this.email = paramString;
  }

  public void setCtmlName(String paramString)
  {
    this.ctmlName = paramString;
  }

  public void setCertSN(String paramString)
  {
    this.certSN = paramString;
  }

  public void setCdpid(long paramLong)
  {
    this.cdpid = paramLong;
  }

  public void setAuthCode(String paramString)
  {
    this.authCode = paramString;
  }

  public void setApplicant(String paramString)
  {
    this.applicant = paramString;
  }

  public String getCertStatus()
  {
    return this.certStatus;
  }

  public void setCertStatus(String paramString)
  {
    this.certStatus = paramString;
  }

  public long getIsValid()
  {
    return this.isValid;
  }

  public void setIsValid(long paramLong)
  {
    this.isValid = paramLong;
  }

  public long getCreateTime()
  {
    return this.createTime;
  }

  public void setCreateTime(long paramLong)
  {
    this.createTime = paramLong;
  }

  public byte[] getCertEntity()
  {
    return this.certEntity;
  }

  public void setCertEntity(byte[] paramArrayOfByte)
  {
    this.certEntity = paramArrayOfByte;
  }

  public CertExtensions getCertExtensions()
  {
    return this.certExtensions;
  }

  public void setCertExtensions(CertExtensions paramCertExtensions)
  {
    this.certExtensions = paramCertExtensions;
    for (int i = 0; i < paramCertExtensions.getExtensionsCount(); i++)
    {
      String str = paramCertExtensions.getExtension(i).getName();
      if (this.extensions.containsKey(str))
        continue;
      this.extensions.setProperty(str, paramCertExtensions.getExtension(i).getValue());
    }
  }

  public JKey getPubKey()
  {
    return this.pubKey;
  }

  public void setPubKey(JKey paramJKey)
  {
    this.pubKey = paramJKey;
  }

  public String getRevokeDesc()
  {
    return this.revokeDesc;
  }

  public String getRevokeReason()
  {
    return this.revokeReason;
  }

  public long getAuthCodeUpdateTime()
  {
    return this.authCodeUpdateTime;
  }

  public Vector getStandardExtensions()
  {
    return this.standardExtensions;
  }

  public void setRevokeReason(String paramString)
  {
    this.revokeReason = paramString;
  }

  public void setRevokeDesc(String paramString)
  {
    this.revokeDesc = paramString;
  }

  public void setAuthCodeUpdateTime(long paramLong)
  {
    this.authCodeUpdateTime = paramLong;
  }

  public void setStandardExtensions(Vector paramVector)
  {
    this.standardExtensions = paramVector;
  }

  public String getIsRetainKey()
  {
    return this.isRetainKey;
  }

  public void setIsRetainKey(String paramString)
  {
    this.isRetainKey = paramString;
  }

  public Hashtable getStandardExtensionsHT()
  {
    return this.standardExtensionsHT;
  }

  public String getApplicantUppercase()
  {
    return this.applicantUppercase;
  }

  public String getSignClient()
  {
    return this.signClient;
  }

  public String getSignServer()
  {
    return this.signServer;
  }

  public String getSubjectUppercase()
  {
    return this.subjectUppercase;
  }

  public String getOldSN()
  {
    return this.oldSN;
  }

  public void setStandardExtensionsHT(Hashtable paramHashtable)
  {
    this.standardExtensionsHT = paramHashtable;
  }

  public void setApplicantUppercase(String paramString)
  {
    this.applicantUppercase = paramString;
  }

  public void setSubjectUppercase(String paramString)
  {
    this.subjectUppercase = paramString;
  }

  public void setSignServer(String paramString)
  {
    this.signServer = paramString;
  }

  public void setSignClient(String paramString)
  {
    this.signClient = paramString;
  }

  public void setIswaiting(String paramString)
  {
    this.iswaiting = paramString;
  }

  public void setOldSN(String paramString)
  {
    this.oldSN = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo
 * JD-Core Version:    0.6.0
 */