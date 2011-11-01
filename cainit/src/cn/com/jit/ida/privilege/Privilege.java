package cn.com.jit.ida.privilege;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.InternalConfig;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

public class Privilege
{
  private static Privilege instance = null;
  Admin admin;
  Role role;
  FunctionPoint functionPoint;
  private static boolean bPrivilegeFlag = false;

  private Privilege()
    throws PrivilegeException
  {
    try
    {
      this.admin = Admin.getInstance();
    }
    catch (PrivilegeException localPrivilegeException1)
    {
      throw new PrivilegeException("8041" + localPrivilegeException1.getErrCode(), "初始化管理员:" + localPrivilegeException1.getErrDesc(), localPrivilegeException1);
    }
    try
    {
      this.role = Role.getInstance();
    }
    catch (PrivilegeException localPrivilegeException2)
    {
      throw new PrivilegeException("8057" + localPrivilegeException2.getErrCode(), "初始化角色:" + localPrivilegeException2.getErrDesc(), localPrivilegeException2);
    }
    try
    {
      this.functionPoint = FunctionPoint.getInstance();
    }
    catch (PrivilegeException localPrivilegeException3)
    {
      throw new PrivilegeException("8063" + localPrivilegeException3.getErrCode(), "初始化权限点:" + localPrivilegeException3.getErrDesc(), localPrivilegeException3);
    }
  }

  public static Privilege getInstance()
    throws PrivilegeException
  {
    if (instance == null)
      instance = new Privilege();
    return instance;
  }

  public boolean isPass(String paramString1, String paramString2)
  {
    if (bPrivilegeFlag)
      return true;
    if (paramString1.equalsIgnoreCase("系统管理员"))
      return true;
    if ((paramString2.equals("RACTMLUPDATE")) || (paramString2.equals("CRLQUERY")))
      return true;
    String str1 = null;
    try
    {
      str1 = InternalConfig.getInstance().getIsPrivilage();
    }
    catch (IDAException localIDAException)
    {
      return false;
    }
    if (str1.equalsIgnoreCase("false"))
      return true;
    if ((this.admin.hashTable != null) && (this.admin.hashTable.containsKey(paramString1)))
    {
      Vector localVector = this.admin.getAdmin(paramString1);
      if ((localVector == null) || (localVector.isEmpty()))
        return false;
      for (int i = 0; i < localVector.size(); i++)
      {
        String str2 = (String)localVector.get(i);
        HashSet localHashSet = this.role.getRole(str2);
        if ((localHashSet != null) && (localHashSet.contains(paramString2)))
          return true;
      }
    }
    return false;
  }

  public void refreshAdminInfo()
    throws PrivilegeException
  {
    try
    {
      this.admin.refresh();
      this.role.refresh();
    }
    catch (PrivilegeException localPrivilegeException)
    {
      throw new PrivilegeException(localPrivilegeException.getErrCode(), localPrivilegeException.getErrDesc());
    }
  }

  public Vector getAdminList()
  {
    return this.admin.getAdminList();
  }

  public Vector getAdminCertList()
    throws PrivilegeException
  {
    try
    {
      return this.admin.getAdminCertList();
    }
    catch (PrivilegeException localPrivilegeException)
    {
    	 throw new PrivilegeException("8055" + localPrivilegeException.getErrCode(), "获取管理员证书列表:" + localPrivilegeException.getErrDesc(), localPrivilegeException);
    }
   
  }

  public Vector getRoleList()
  {
    return this.role.getRoleNameList();
  }

  public Vector getAdminRole(String paramString)
  {
    Vector localVector1 = getAdminList();
    if ((localVector1 == null) || (!localVector1.contains(paramString)))
      return null;
    Vector localVector2 = this.admin.getAdmin(paramString);
    if (localVector2 == null)
      return null;
    return this.role.getNameFromID(localVector2);
  }

  public Vector getAdminFuncPoint(String paramString)
  {
    Vector localVector1 = getAdminList();
    if ((localVector1 == null) || (!localVector1.contains(paramString)))
      return null;
    Vector localVector2 = this.admin.getAdmin(paramString);
    if ((localVector2 == null) || (localVector2.isEmpty()))
      return null;
    HashSet localHashSet1 = new HashSet();
    for (int i = 0; i < localVector2.size(); i++)
    {
    	String localObject = (String)localVector2.get(i);
      HashSet localHashSet2 = this.role.getRole((String)localObject);
      if (localHashSet2 == null)
        continue;
      localHashSet1.addAll(localHashSet2);
    }
    Vector localVector3 = new Vector(localHashSet1);
    Object localObject = this.functionPoint.getNameFromID(localVector3);
    return (Vector)localObject;
  }

  private Vector getRoleDetail(String paramString)
    throws PrivilegeException
  {
    String str = this.role.getIDFromName(paramString);
    if (str == null)
      throw new PrivilegeException("80600363", "获取角色信息:没有查到此角色");
    HashSet localHashSet = this.role.getRole(str);
    if (localHashSet == null)
      return null;
    Vector localVector = new Vector(localHashSet);
    return this.functionPoint.getNameFromID(localVector);
  }

  private Vector getFunctionPointList()
    throws PrivilegeException
  {
    HashSet localHashSet = null;
    try
    {
      localHashSet = this.functionPoint.getPrivilegeNameList();
    }
    catch (PrivilegeException localPrivilegeException)
    {
      throw new PrivilegeException("8065" + localPrivilegeException.getErrCode(), "获取权限点列表:" + localPrivilegeException.getErrDesc(), localPrivilegeException);
    }
    Vector localVector = new Vector(localHashSet);
    return localVector;
  }

  private void setRole(String paramString, Collection paramCollection)
    throws PrivilegeException
  {
    if ((paramCollection == null) || (paramCollection.isEmpty()))
      throw new PrivilegeException("80590481", "设置角色:权限点集合为空");
    Vector localVector1 = new Vector(paramCollection);
    HashSet localHashSet = null;
    try
    {
      localHashSet = this.functionPoint.getPrivilegeNameList();
    }
    catch (PrivilegeException localPrivilegeException1)
    {
      throw new PrivilegeException("8059" + localPrivilegeException1.getErrCode(), "设置角色:" + localPrivilegeException1.getErrDesc(), localPrivilegeException1);
    }
    if (!localHashSet.containsAll(localVector1))
      throw new PrivilegeException("80590482", "设置角色:存在非法权限点");
    Vector localVector2 = this.functionPoint.getIDFromName(localVector1);
    if ((localVector2 == null) || (localVector2.isEmpty()))
      throw new PrivilegeException("80590483", "设置角色:权限点内部转换失败");
    try
    {
      this.role.setRole(paramString, localVector2);
    }
    catch (PrivilegeException localPrivilegeException2)
    {
      throw new PrivilegeException("8059" + localPrivilegeException2.getErrCode(), "设置角色:" + localPrivilegeException2.getErrDesc(), localPrivilegeException2);
    }
  }

  private void delRole(String paramString)
    throws PrivilegeException
  {
    Vector localVector = this.role.getRoleNameList();
    if ((localVector == null) || (!localVector.contains(paramString)))
      throw new PrivilegeException("80610363", "删除角色:没有查到此角色");
    String str = this.role.getIDFromName(paramString);
    if (str == null)
      throw new PrivilegeException("80610364", "删除角色:角色内部转换失败");
    try
    {
      this.role.delRole(str);
    }
    catch (PrivilegeException localPrivilegeException)
    {
      throw new PrivilegeException("8061" + localPrivilegeException.getErrCode(), "删除角色:" + localPrivilegeException.getErrDesc(), localPrivilegeException);
    }
  }


  private void setMeMAdmin(String paramString, Vector paramVector)
  {
    this.admin.setMeMAdmin(paramString, paramVector);
  }


  public void delAdmin(String paramString)
  {
    Vector localVector = getAdminList();
    if ((localVector != null) && (localVector.contains(paramString)))
      this.admin.delAdmin(paramString);
  }

  public void updateAdmin(String paramString1, String paramString2)
  {
    this.admin.updateAdmin(paramString1, paramString2);
  }

  public void setPrivilegeSpecial()
  {
    bPrivilegeFlag = true;
  }

  public void setPrivilegeNormal()
  {
    bPrivilegeFlag = false;
  }

  void refresh()
    throws PrivilegeException
  {
    try
    {
      this.admin.refresh();
    }
    catch (PrivilegeException localPrivilegeException1)
    {
      throw new PrivilegeException("8042" + localPrivilegeException1.getErrCode(), "刷新管理员:" + localPrivilegeException1.getErrDesc(), localPrivilegeException1);
    }
    try
    {
      this.role.refresh();
    }
    catch (PrivilegeException localPrivilegeException2)
    {
      throw new PrivilegeException("8058" + localPrivilegeException2.getErrCode(), "刷新角色:" + localPrivilegeException2.getErrDesc(), localPrivilegeException2);
    }
    try
    {
      this.functionPoint.refresh();
    }
    catch (PrivilegeException localPrivilegeException3)
    {
      throw new PrivilegeException("8064" + localPrivilegeException3.getErrCode(), "刷新权限点:" + localPrivilegeException3.getErrDesc(), localPrivilegeException3);
    }
  }

  public void setPendingAdmin(String paramString1, String paramString2)
  {
    this.admin.setPendingAdmin(paramString1, paramString2);
  }

  public void deletePendingAdmin(String paramString)
  {
    this.admin.deletePendingAdmin(paramString);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.Privilege
 * JD-Core Version:    0.6.0
 */