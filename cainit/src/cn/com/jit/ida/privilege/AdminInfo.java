package cn.com.jit.ida.privilege;


import java.util.Vector;

public class AdminInfo
{
  private String no;
  private String sn = null;
  private String dn = null;
  private String status = null;
  private String roleinfo = null;

  public String getNo()
  {
    return this.no;
  }

  public void setNo(String paramString)
  {
    this.no = paramString;
  }

  public String getSn()
  {
    return this.sn;
  }

  public void setSn(String paramString)
  {
    this.sn = paramString;
  }

  public String getDn()
  {
    return this.dn;
  }

  public void setDn(String paramString)
  {
    this.dn = paramString;
  }

  public String getStatus()
  {
    return this.status;
  }

  public void setStatus(String paramString)
  {
    this.status = paramString;
  }

  public String getRoleinfo()
  {
    return this.roleinfo;
  }

  public void setRoleinfo(String paramString)
  {
    this.roleinfo = paramString;
  }

  public void setRoleinfo(Vector paramVector)
  {
    if ((paramVector != null) && (paramVector.size() > 0))
    {
      String str = new String((String)paramVector.get(0));
      for (int i = 1; i < paramVector.size(); i++)
      {
        str = str + ";";
        str = str + (String)paramVector.get(i);
      }
      this.roleinfo = this.roleinfo;
    }
  }
}