package cn.com.jit.ida.privilege;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.service.operation.CertQueryOpt;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTMLPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTMLPolicy.Attribute;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

public class TemplateAdmin
{
  private static TemplateAdmin instance;
  private static boolean bTemplateAdmin = false;
  private Hashtable hashTable;

  private TemplateAdmin()
    throws PrivilegeException
  {
    DBManager localDBManager = null;
    try
    {
      localDBManager = DBManager.getInstance();
    }
    catch (DBException localDBException1)
    {
      throw new PrivilegeException("8049" + localDBException1.getErrCode(), "初始化证书业务管理员:" + localDBException1.getErrDesc(), localDBException1);
    }
    try
    {
      this.hashTable = localDBManager.getTemplateAdminsInfo();
    }
    catch (DBException localDBException2)
    {
      throw new PrivilegeException("8049" + localDBException2.getErrCode(), "初始化证书业务管理员:" + localDBException2.getErrDesc(), localDBException2);
    }
  }

  public static TemplateAdmin getInstance()
    throws PrivilegeException
  {
    if (instance == null)
      instance = new TemplateAdmin();
    return instance;
  }

  public void refreshAdminInfo()
    throws PrivilegeException
  {
    DBManager localDBManager = null;
    try
    {
      localDBManager = DBManager.getInstance();
    }
    catch (DBException localDBException1)
    {
      throw new PrivilegeException(localDBException1.getErrCode(), localDBException1.getErrDesc());
    }
    Hashtable localHashtable = null;
    try
    {
      localHashtable = localDBManager.getTemplateAdminsInfo();
    }
    catch (DBException localDBException2)
    {
      throw new PrivilegeException(localDBException2.getErrCode(), localDBException2.getErrDesc());
    }
    this.hashTable = localHashtable;
  }

  private Vector getTemplateAdminList()
  {
    if ((this.hashTable == null) || (this.hashTable.isEmpty()))
      return null;
    Set localSet = this.hashTable.keySet();
    Vector localVector = new Vector(localSet);
    return localVector;
  }

  public boolean isPass(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    if (bTemplateAdmin)
      return true;
    if ((paramString1 == null) || (paramString2 == null) || (paramString3 == null))
      return false;
    String str1 = null;
    try
    {
      str1 = CAConfig.getInstance().getDN();
    }
    catch (IDAException localIDAException1)
    {
      return false;
    }
    if (paramString3.equalsIgnoreCase(str1))
      return false;
    if (paramString1.equalsIgnoreCase("系统管理员"))
      return true;
    String str2 = null;
    try
    {
      str2 = InternalConfig.getInstance().getIsPrivilage();
    }
    catch (IDAException localIDAException2)
    {
      return false;
    }
    if (str2.equalsIgnoreCase("false"))
      return true;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    String str6 = null;
    try
    {
      str3 = CAConfig.getInstance().getCaAdminSN();
      str5 = CAConfig.getInstance().getCaAdminDN();
      str6 = CAConfig.getInstance().getCAAuditAdminDN();
      str4 = InternalConfig.getInstance().getAdminTemplateName();
    }
    catch (Exception localException)
    {
      return false;
    }
    if ((paramString2.equalsIgnoreCase(str4)) && (paramString3.equalsIgnoreCase(str5)))
      return false;
    if ((paramString2.equalsIgnoreCase(str4)) && (paramString3.equalsIgnoreCase(str6)))
      return false;
    String str7 = null;
    String str8 = null;
    try
    {
      str7 = InternalConfig.getInstance().getCommCertTemplateName();
      str8 = CAConfig.getInstance().getCommCertDN();
    }
    catch (IDAException localIDAException3)
    {
      return false;
    }
    if ((paramString2.equalsIgnoreCase(str7)) && (paramString3.equalsIgnoreCase(str8)))
      return false;
    if (str3 == null)
      return false;
    if (str3.equalsIgnoreCase(paramString1))
      return true;
    Vector localVector1 = getVectorFromItem(paramString2, paramString3, paramString4, true);
    if ((this.hashTable != null) && (this.hashTable.containsKey(paramString1)))
    {
      Vector localVector2 = (Vector)this.hashTable.get(paramString1);
      for (int i = 0; i < localVector2.size(); i++)
      {
        Vector localVector3 = (Vector)localVector2.get(i);
        if ((localVector3 == null) || (localVector3.isEmpty()))
          continue;
        Vector localVector4 = new Vector();
        for (int j = 0; j < localVector3.size(); j++)
        {
          String str9 = (String)localVector3.get(j);
          if (str9 == null)
            break;
          localVector4.add(str9.toLowerCase());
        }
        if (localVector1.containsAll(localVector4))
          return true;
      }
    }
    return false;
  }

  private void setTemplateAdmin(String paramString1, String paramString2, String paramString3, String paramString4)
    throws PrivilegeException
  {
    CertInfo localCertInfo = null;
    try
    {
      localCertInfo = CertQueryOpt.queryCertInfo(paramString1);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80510126", "获取该SN对应证书信息时出错", localException);
    }
    String str = localCertInfo.getCtmlName();
    if (!str.equals("ADMIN"))
      throw new PrivilegeException("80510246", "证书业务管理员授权:该SN对应证书不是管理员证书");
    DBManager localDBManager = null;
    try
    {
      localDBManager = DBManager.getInstance();
    }
    catch (DBException localDBException1)
    {
      throw new PrivilegeException("8051" + localDBException1.getErrCode(), "证书业务管理员授权:" + localDBException1.getErrDesc(), localDBException1);
    }
    int i = -1;
    try
    {
      i = localDBManager.setTemplateAdmin(paramString1, paramString2, paramString3, paramString4);
    }
    catch (DBException localDBException2)
    {
      throw new PrivilegeException("8051" + localDBException2.getErrCode(), "证书业务管理员授权:" + localDBException2.getErrDesc(), localDBException2);
    }
    if (i < 0)
      throw new PrivilegeException("80510241", "证书业务管理员授权:证书业务管理员授权失败");
    Vector localVector1 = getVectorFromItem(paramString2, paramString3, paramString4, false);
    Vector localVector2;
    if (this.hashTable == null)
    {
      this.hashTable = new Hashtable();
      localVector2 = new Vector();
      localVector2.add(localVector1);
      this.hashTable.put(paramString1, localVector2);
    }
    else if (this.hashTable.containsKey(paramString1))
    {
      localVector2 = (Vector)this.hashTable.get(paramString1);
      if (!localVector2.contains(localVector1))
      {
        this.hashTable.remove(paramString1);
        localVector2.add(localVector1);
        this.hashTable.put(paramString1, localVector2);
      }
    }
    else
    {
      localVector2 = new Vector();
      localVector2.add(localVector1);
      this.hashTable.put(paramString1, localVector2);
    }
  }

  public void setTemplateAdmin(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3)
    throws PrivilegeException
  {
    CertInfo localCertInfo = null;
    try
    {
      localCertInfo = CertQueryOpt.queryCertInfo(paramString);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80510126", "获取该SN对应证书信息时出错", localException);
    }
    if (localCertInfo == null)
      throw new PrivilegeException("0130", "没有该SN对应的证书");
    String str = localCertInfo.getCtmlName();
    if (!localCertInfo.getCtmlName().equals(str))
      throw new PrivilegeException("80510246", "证书业务管理员授权:该SN对应证书不是管理员证书");
    DBManager localDBManager = null;
    try
    {
      localDBManager = DBManager.getInstance();
    }
    catch (DBException localDBException1)
    {
      throw new PrivilegeException("8051" + localDBException1.getErrCode(), "证书业务管理员授权:" + localDBException1.getErrDesc(), localDBException1);
    }
    int i = -1;
    try
    {
      i = localDBManager.setTemplateAdmin(paramString, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3);
    }
    catch (DBException localDBException2)
    {
      throw new PrivilegeException("8051" + localDBException2.getErrCode(), "证书业务管理员授权:" + localDBException2.getErrDesc(), localDBException2);
    }
    if (i < 0)
      throw new PrivilegeException("80510241", "证书业务管理员授权:证书业务管理员授权失败");
    if (this.hashTable == null)
      this.hashTable = new Hashtable();
    if ((paramArrayOfString1 == null) || (paramArrayOfString2 == null))
    {
      if (this.hashTable.containsKey(paramString))
        this.hashTable.remove(paramString);
    }
    else
    {
      Vector localVector1 = new Vector();
      for (int j = 0; j < paramArrayOfString1.length; j++)
      {
        Vector localVector2 = getVectorFromItem(paramArrayOfString1[j], paramArrayOfString2[j], null, false);
        localVector1.add(localVector2);
      }
      if (this.hashTable.containsKey(paramString))
        this.hashTable.remove(paramString);
      this.hashTable.put(paramString, localVector1);
    }
  }

  private void setTemplateAdmin(String paramString, Vector paramVector)
  {
    if (this.hashTable == null)
      this.hashTable = new Hashtable();
    else if (this.hashTable.contains(paramString))
      this.hashTable.remove(paramString);
    this.hashTable.put(paramString, paramVector);
  }

  public void delTemplateAdmin(String paramString)
  {
    if ((this.hashTable != null) && (this.hashTable.containsKey(paramString)))
      this.hashTable.remove(paramString);
  }

  public void setSuperAdmin(String paramString)
    throws PrivilegeException
  {
    String str = null;
    try
    {
      str = CAConfig.getInstance().getBaseDN();
    }
    catch (IDAException localIDAException1)
    {
      throw new PrivilegeException("0507", "获取配置信息出错", localIDAException1);
    }
    String[] arrayOfString1 = null;
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    try
    {
      arrayOfString1 = localCTMLManager.getCTMLList();
    }
    catch (IDAException localIDAException2)
    {
      throw new PrivilegeException("0507", "获取模板列表出错", localIDAException2);
    }
    if (arrayOfString1 != null)
    {
      String[] arrayOfString2 = new String[arrayOfString1.length];
      for (int i = 0; i < arrayOfString1.length; i++)
        try
        {
          X509V3CTMLPolicy localX509V3CTMLPolicy = new X509V3CTMLPolicy(localCTMLManager.getCTML(arrayOfString1[i]).getCTMLPolicyDesc());
          if ((localX509V3CTMLPolicy.getAttribute().attribute & 1L) != 0L)
            arrayOfString2[i] = "";
          else
            arrayOfString2[i] = str;
        }
        catch (IDAException localIDAException3)
        {
        }
      setTemplateAdmin(paramString, arrayOfString1, arrayOfString2, null);
    }
  }

  private void delTemplateAdmin(String paramString1, String paramString2, String paramString3, String paramString4)
    throws PrivilegeException
  {
    DBManager localDBManager = null;
    try
    {
      localDBManager = DBManager.getInstance();
    }
    catch (DBException localDBException1)
    {
      throw new PrivilegeException("8051" + localDBException1.getErrCode(), "证书业务管理员授权:" + localDBException1.getErrDesc(), localDBException1);
    }
    int i = -1;
    try
    {
      i = localDBManager.deleteTemplateAdmin(paramString1);
    }
    catch (DBException localDBException2)
    {
      throw new PrivilegeException("8051" + localDBException2.getErrCode(), "证书业务管理员授权:" + localDBException2.getErrDesc(), localDBException2);
    }
    if (i == 0)
      throw new PrivilegeException("80510247", "证书业务管理员授权:删除的证书业务管理员部分权限不存在于数据库");
    if (i < 0)
      throw new PrivilegeException("80510248", "证书业务管理员授权:删除证书业务管理员权限出错");
    if ((this.hashTable != null) && (this.hashTable.containsKey(paramString1)))
    {
      Vector localVector1 = (Vector)this.hashTable.get(paramString1);
      this.hashTable.remove(paramString1);
      Vector localVector2 = getVectorFromItem(paramString2, paramString3, paramString4, false);
      if (localVector1.contains(localVector2))
        localVector1.remove(localVector2);
      this.hashTable.put(paramString1, localVector1);
    }
    else
    {
      try
      {
        this.hashTable = localDBManager.getTemplateAdminsInfo();
      }
      catch (DBException localDBException3)
      {
        throw new PrivilegeException("8051" + localDBException3.getErrCode(), "证书业务管理员授权:" + localDBException3.getErrDesc(), localDBException3);
      }
    }
  }

  public void updateTemplateAdmin(String paramString1, String paramString2)
  {
    if ((this.hashTable != null) && (this.hashTable.containsKey(paramString1)))
    {
      Vector localVector = (Vector)this.hashTable.get(paramString1);
      this.hashTable.remove(paramString1);
      this.hashTable.put(paramString2, localVector);
    }
  }

  public Vector getAdminInfo(String paramString)
  {
    if ((this.hashTable != null) && (this.hashTable.containsKey(paramString)))
    {
      Vector localVector = (Vector)this.hashTable.get(paramString);
      return localVector;
    }
    return null;
  }

  public void setTemplateAdminSpecial()
  {
    bTemplateAdmin = true;
  }

  public void setTemplateAdminNormal()
  {
    bTemplateAdmin = false;
  }

  private Vector getVectorFromItem(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    Vector localVector = new Vector();
    if (paramBoolean)
      localVector.add(paramString1.trim().toLowerCase());
    else
      localVector.add(paramString1.trim());
    String str1 = paramString2.trim();
    if (paramBoolean)
      str1 = str1.toLowerCase();
    if (!str1.equals(""))
    {
      int i = str1.length();
      for (int j = str1.length() - 1; j >= 0; j--)
      {
        if (str1.charAt(j) != ',')
          continue;
        String str3 = str1.substring(j + 1, i);
        i = j;
        localVector.add(str3.trim());
      }
      String str2 = str1.substring(0, i);
      localVector.add(str2.trim());
    }
    return localVector;
  }

  public Vector selectLegalCtml(String paramString, Vector paramVector)
  {
    Vector localVector1 = new Vector();
    if ((this.hashTable != null) && (this.hashTable.containsKey(paramString)))
    {
      Vector localVector2 = new Vector();
      Vector localVector3 = (Vector)this.hashTable.get(paramString);
      for (int i = 0; i < localVector3.size(); i++)
      {
        Vector localVector4 = (Vector)localVector3.get(i);
        String str = (String)localVector4.get(0);
        localVector2.add(str);
      }
      for (i = 0; i < paramVector.size(); i++)
      {
        if (!localVector2.contains((String)paramVector.get(i)))
          continue;
        localVector1.add((String)paramVector.get(i));
      }
    }
    return localVector1;
  }

  public String getRAAdminBasedn(String paramString)
    throws PrivilegeException
  {
    DBManager localDBManager = null;
    try
    {
      localDBManager = DBManager.getInstance();
    }
    catch (DBException localDBException1)
    {
      throw new PrivilegeException("8066" + localDBException1.getErrCode(), "获取RA的BaseDN:" + localDBException1.getErrDesc(), localDBException1);
    }
    String str = null;
    try
    {
      str = localDBManager.getRABaseDN(paramString);
    }
    catch (DBException localDBException2)
    {
      throw new PrivilegeException("8066" + localDBException2.getErrCode(), "获取RA的BaseDN:" + localDBException2.getErrDesc(), localDBException2);
    }
    return str;
  }

  public void setPendingTemplateAdmin(String paramString1, String paramString2)
  {
    if ((this.hashTable != null) && (this.hashTable.containsKey(paramString1)))
    {
      Vector localVector = (Vector)this.hashTable.get(paramString1);
      this.hashTable.put(paramString2, localVector);
    }
  }

  public void deletePendingTemplateAdmin(String paramString)
  {
    if ((this.hashTable != null) && (this.hashTable.containsKey(paramString)))
    {
      Vector localVector = (Vector)this.hashTable.get(paramString);
      this.hashTable.remove(paramString);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.TemplateAdmin
 * JD-Core Version:    0.6.0
 */