package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.globalconfig.ConfigFromXML;
import java.util.ArrayList;
import java.util.List;

public class CRLConfig
{
  private static CRLConfig instance;
  private ConfigFromXML config;
  private long periods;
  private int certCount;
  private String CRLLDAPPath;
  private String CDP_URI;
  private boolean CDP_URI_Publish;
  private boolean CDP_DN_Publish;
  private boolean CRLFilePublish;
  private String CRLFilePath;
  private boolean CRLLDAPPublish;
  private CAConfig caConfig;
  private boolean CDP_LDAP_URI_Publish;
  private String CDP_LDAP_URI;
  private boolean PUBALLCRL = false;

  private CRLConfig()
    throws IDAException
  {
    init();
  }

  public static CRLConfig getInstance()
    throws IDAException
  {
    if (instance == null)
      instance = new CRLConfig();
    return instance;
  }

  public void init()
    throws IDAException
  {
    this.caConfig = CAConfig.getInstance();
    this.config = new ConfigFromXML("CRLConfig", "./config/CAConfig.xml");
    this.periods = this.config.getLong("CRLPeriods");
    if ((this.periods < 0L) || (this.periods > 525600L))
      throw new IDAException("0945", "CRL发布周期配置错误，发布周期应控制在 0--525600(1年) 范围内");
    this.certCount = this.config.getNumber("CertCountInCRL");
    if (this.certCount <= 0)
      throw new IDAException("0946", "每个CRL包含的证书数量错误");
    if (this.config.getString("CRLFilePublish").equalsIgnoreCase("true"))
      this.CRLFilePublish = true;
    else
      this.CRLFilePublish = false;
    this.CRLFilePath = this.config.getString("CRLFilePath");
    if (this.config.getString("CRLLDAPPublish").equalsIgnoreCase("true"))
      this.CRLLDAPPublish = true;
    else
      this.CRLLDAPPublish = false;
    if (this.config.getString("PUBALLCRL").equalsIgnoreCase("true"))
      this.PUBALLCRL = true;
    this.CRLLDAPPath = this.config.getString("CRLLDAPPath");
    this.CDP_URI = this.config.getString("CDP_URI");
    if (this.config.getString("CDP_URI_Publish").equalsIgnoreCase("true"))
      this.CDP_URI_Publish = true;
    else
      this.CDP_URI_Publish = false;
    if (this.config.getString("CDP_DN_Publish").equalsIgnoreCase("true"))
      this.CDP_DN_Publish = true;
    else
      this.CDP_DN_Publish = false;
    if (this.config.getString("CDP_LDAP_URI_Publish").equalsIgnoreCase("true"))
      this.CDP_LDAP_URI_Publish = true;
    else
      this.CDP_LDAP_URI_Publish = false;
    this.CDP_LDAP_URI = this.config.getString("CDP_LDAP_URI");
  }

  public long getPeriods()
  {
    return this.periods * 60000L;
  }

  public void setPeriods(long paramLong)
    throws IDAException
  {
    this.periods = (paramLong / 60000L);
    this.config.setLong("CRLPeriods", paramLong / 60000L);
  }

  public int getCertCount()
  {
    return this.certCount;
  }

  public void setCertCount(int paramInt)
    throws IDAException
  {
    this.certCount = paramInt;
    this.config.setNumber("CertCountInCRL", paramInt);
  }

  public String[][] getCRLPubAddress()
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    if (isCDP_DN_Publish())
      i++;
    if (isCDP_URI_Publish())
      i++;
    if (isCDP_DN_Publish())
    {
      localObject = new String[2];
      localObject[0] = "DN";
      localObject[1] = getCRLLDAPPath();
      localArrayList.add(localObject);
    }
    if (isCDP_LDAP_URI_Publish())
    {
      localObject = new String[2];
      localObject[0] = "LDAP_URI";
      localObject[1] = getCDP_LDAP_URI();
      localArrayList.add(localObject);
    }
    if (isCDP_URI_Publish())
    {
      localObject = new String[2];
      localObject[0] = "URI";
      localObject[1] = getCDP_URI();
      localArrayList.add(localObject);
    }
    Object localObject = new String[localArrayList.size()][2];
    localArrayList.toArray(localObject);
    return (String)localObject;
  }

  public void setCRLPubAddress(String[][] paramArrayOfString)
    throws IDAException
  {
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramArrayOfString.length; k++)
      if (paramArrayOfString[k][0].equalsIgnoreCase("DN"))
      {
        setCDP_DN_Publish(true);
        setCRLLDAPPath(paramArrayOfString[k][1]);
        i = 1;
      }
      else
      {
        if (!paramArrayOfString[k][0].equalsIgnoreCase("URI"))
          continue;
        setCDP_URI_Publish(true);
        setCRLFilePath(paramArrayOfString[k][1]);
        j = 1;
      }
    if (i == 0)
      setCDP_DN_Publish(false);
    if (j == 0)
      setCDP_URI_Publish(false);
  }

  public String[][] getCRLPubAddressForService()
  {
    int i = 0;
    if (isCRLFilePublish())
      i++;
    if (isCRLLDAPPublish())
      i++;
    String[][] arrayOfString; = new String[i][];
    if (isCRLLDAPPublish())
    {
      arrayOfString;[0] = new String[2];
      arrayOfString;[0][0] = "DN";
      arrayOfString;[0][1] = getCRLLDAPPath();
    }
    if (isCRLFilePublish())
    {
      arrayOfString;[(i - 1)] = new String[2];
      arrayOfString;[(i - 1)][0] = "URI";
      arrayOfString;[(i - 1)][1] = getCRLFilePath();
    }
    return arrayOfString;;
  }

  public void setCRLPubAddressForService(String[][] paramArrayOfString)
    throws IDAException
  {
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramArrayOfString.length; k++)
      if (paramArrayOfString[k][0].equalsIgnoreCase("DN"))
      {
        setCRLLDAPPublish(true);
        setCRLLDAPPath(paramArrayOfString[k][1]);
        i = 1;
      }
      else
      {
        if (!paramArrayOfString[k][0].equalsIgnoreCase("URI"))
          continue;
        setCRLLDAPPublish(true);
        setCRLFilePath(paramArrayOfString[k][1]);
        j = 1;
      }
    if (i == 0)
      setCRLLDAPPublish(false);
    if (j == 0)
      setCRLLDAPPublish(false);
  }

  public boolean isCRLFilePublish()
  {
    return this.CRLFilePublish;
  }

  public void setCRLFilePublish(boolean paramBoolean)
    throws IDAException
  {
    this.CRLFilePublish = paramBoolean;
    if (paramBoolean)
      this.config.setString("CRLFilePublish", "true");
    else
      this.config.setString("CRLFilePublish", "false");
  }

  public boolean isCRLLDAPPublish()
  {
    return this.CRLLDAPPublish;
  }

  public void setCRLLDAPPublish(boolean paramBoolean)
    throws IDAException
  {
    if (paramBoolean)
      this.config.setString("CRLLDAPPublish", "true");
    else
      this.config.setString("CRLLDAPPublish", "false");
    this.CRLLDAPPublish = paramBoolean;
  }

  public void setPUBALLCRL(boolean paramBoolean)
    throws IDAException
  {
    if (paramBoolean)
      this.config.setString("PUBALLCRL", "true");
    else
      this.config.setString("PUBALLCRL", "false");
    this.PUBALLCRL = paramBoolean;
  }

  public boolean getPUBALLCRL()
  {
    return this.PUBALLCRL;
  }

  public String getCRLFilePath()
  {
    return this.CRLFilePath;
  }

  public void setCRLFilePath(String paramString)
    throws IDAException
  {
    this.config.setString("CRLFilePath", paramString);
    this.CRLFilePath = paramString;
  }

  public String getCRLLDAPPath()
  {
    String str = null;
    str = this.caConfig.getBaseDN();
    if (this.CRLLDAPPath.trim().equals(""))
      return str;
    return this.CRLLDAPPath + "," + str;
  }

  public void setCRLLDAPPath(String paramString)
    throws IDAException
  {
    String str1 = this.caConfig.getBaseDN();
    String str2 = paramString.substring(0, paramString.indexOf(str1) - 1);
    this.config.setString("CRLLDAPPath", str2);
    this.CRLLDAPPath = str2;
  }

  public String getCDP_URI()
  {
    return this.CDP_URI;
  }

  public void setCDP_URI(String paramString)
    throws IDAException
  {
    this.config.setString("CDP_URI", paramString);
    this.CDP_URI = paramString;
  }

  public boolean isCDP_URI_Publish()
  {
    return this.CDP_URI_Publish;
  }

  public void setCDP_URI_Publish(boolean paramBoolean)
    throws IDAException
  {
    if (paramBoolean)
      this.config.setString("CDP_URI_Publish", "true");
    else
      this.config.setString("CDP_URI_Publish", "false");
    this.CDP_URI_Publish = paramBoolean;
  }

  public boolean isCDP_DN_Publish()
  {
    return this.CDP_DN_Publish;
  }

  public void setCDP_DN_Publish(boolean paramBoolean)
    throws IDAException
  {
    if (paramBoolean)
      this.config.setString("CDP_DN_Publish", "true");
    else
      this.config.setString("CDP_DN_Publish", "false");
    this.CDP_DN_Publish = paramBoolean;
  }

  public String getCDP_LDAP_URI()
  {
    if (!this.CDP_LDAP_URI.endsWith("/"))
      this.CDP_LDAP_URI += "/";
    return this.CDP_LDAP_URI;
  }

  public boolean isCDP_LDAP_URI_Publish()
  {
    return this.CDP_LDAP_URI_Publish;
  }

  public void setCDP_LDAP_URI(String paramString)
    throws IDAException
  {
    this.config.setString("CDP_LDAP_URI", paramString);
    this.CDP_LDAP_URI = paramString;
  }

  public void setCDP_LDAP_URI_Publish(boolean paramBoolean)
    throws IDAException
  {
    if (paramBoolean)
      this.config.setString("CDP_LDAP_URI_Publish", "true");
    else
      this.config.setString("CDP_LDAP_URI_Publish", "false");
    this.CDP_LDAP_URI_Publish = paramBoolean;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.CRLConfig
 * JD-Core Version:    0.6.0
 */