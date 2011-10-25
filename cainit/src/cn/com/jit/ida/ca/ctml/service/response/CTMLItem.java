package cn.com.jit.ida.ca.ctml.service.response;

import java.util.Vector;

public class CTMLItem
{
  private String name;
  private String id;
  private String status;
  private String type;
  private String keytype;
  private String keySpec;
  private String updateReplace;
  private String updateTransPeriod;
  private int keysize;
  private int defaultValidate;
  private int maxValidate;
  private String notafterDate;
  private String keyGenLocation;
  private String description;
  private Vector baseDN;
  private Vector extention;
  private Vector standardExtention;

  public Vector getBaseDN()
  {
    return this.baseDN;
  }

  public int getDefaultValidate()
  {
    return this.defaultValidate;
  }

  public String getDescription()
  {
    return this.description;
  }

  public Vector getExtention()
  {
    return this.extention;
  }

  public String getId()
  {
    return this.id;
  }

  public String getKeyGenLocation()
  {
    return this.keyGenLocation;
  }

  public int getKeysize()
  {
    return this.keysize;
  }

  public String getKeytype()
  {
    return this.keytype;
  }

  public int getMaxValidate()
  {
    return this.maxValidate;
  }

  public String getName()
  {
    return this.name;
  }

  public String getNotafterDate()
  {
    return this.notafterDate;
  }

  public String getStatus()
  {
    return this.status;
  }

  public String getType()
  {
    return this.type;
  }

  public Vector getStandardExtention()
  {
    return this.standardExtention;
  }

  public String getKeySpec()
  {
    return this.keySpec;
  }

  public String getUpdateTransPeriod()
  {
    return this.updateTransPeriod;
  }

  public String getUpdateReplace()
  {
    return this.updateReplace;
  }

  public void setType(String paramString)
  {
    this.type = paramString;
  }

  public void setStatus(String paramString)
  {
    this.status = paramString;
  }

  public void setNotafterDate(String paramString)
  {
    this.notafterDate = paramString;
  }

  public void setName(String paramString)
  {
    this.name = paramString;
  }

  public void setMaxValidate(int paramInt)
  {
    this.maxValidate = paramInt;
  }

  public void setKeytype(String paramString)
  {
    this.keytype = paramString;
  }

  public void setKeysize(int paramInt)
  {
    this.keysize = paramInt;
  }

  public void setKeyGenLocation(String paramString)
  {
    this.keyGenLocation = paramString;
  }

  public void setId(String paramString)
  {
    this.id = paramString;
  }

  public void setExtention(Vector paramVector)
  {
    this.extention = paramVector;
  }

  public void setDescription(String paramString)
  {
    this.description = paramString;
  }

  public void setDefaultValidate(int paramInt)
  {
    this.defaultValidate = paramInt;
  }

  public void setBaseDN(Vector paramVector)
  {
    this.baseDN = paramVector;
  }

  public void setStandardExtention(Vector paramVector)
  {
    this.standardExtention = paramVector;
  }

  public void setKeySpec(String paramString)
  {
    this.keySpec = paramString;
  }

  public void setUpdateTransPeriod(String paramString)
  {
    this.updateTransPeriod = paramString;
  }

  public void setUpdateReplace(String paramString)
  {
    this.updateReplace = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.response.CTMLItem
 * JD-Core Version:    0.6.0
 */