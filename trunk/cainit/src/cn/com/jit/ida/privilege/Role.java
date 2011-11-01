package cn.com.jit.ida.privilege;

import cn.com.jit.ida.ca.db.DBManager;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class Role
{
  private static Role instance;
  Hashtable hashTable = null;
  Hashtable relationTable = null;

  private Role()
    throws PrivilegeException
  {
    DBManager localDBManager = null;
    try
    {
      localDBManager = DBManager.getInstance();
    }
    catch (Exception localException1)
    {
      throw new PrivilegeException("0501", "初始化数据库失败", localException1);
    }
    try
    {
      this.hashTable = localDBManager.getRolesInfo();
      this.relationTable = localDBManager.getRolesRelationInfo();
    }
    catch (Exception localException2)
    {
      throw new PrivilegeException("0502", "调用数据库接口失败", localException2);
    }
  }

  static Role getInstance()
    throws PrivilegeException
  {
    if (instance == null)
      instance = new Role();
    return instance;
  }

  void refresh()
    throws PrivilegeException
  {
    DBManager localDBManager = null;
    try
    {
      localDBManager = DBManager.getInstance();
    }
    catch (Exception localException1)
    {
      throw new PrivilegeException("0501", "初始化数据库失败", localException1);
    }
    try
    {
      this.hashTable = localDBManager.getRolesInfo();
      this.relationTable = localDBManager.getRolesRelationInfo();
    }
    catch (Exception localException2)
    {
      throw new PrivilegeException("0502", "调用数据库接口失败", localException2);
    }
  }

  boolean isPass(String paramString1, String paramString2)
  {
    if ((this.hashTable == null) || (this.hashTable.isEmpty()))
      return false;
    HashSet localHashSet = (HashSet)this.hashTable.get(paramString1);
    return localHashSet.contains(paramString2);
  }

  HashSet getRole(String paramString)
  {
    if ((this.hashTable == null) || (this.hashTable.isEmpty()))
      return null;
    if (this.hashTable.containsKey(paramString))
    {
      HashSet localHashSet = (HashSet)this.hashTable.get(paramString);
      return localHashSet;
    }
    return null;
  }

  Vector getRoleIDList()
  {
    if ((this.relationTable == null) || (this.relationTable.isEmpty()))
      return null;
    Set localSet = this.relationTable.keySet();
    Vector localVector = new Vector(localSet);
    return localVector;
  }

  Vector getRoleNameList()
  {
    if ((this.relationTable == null) || (this.relationTable.isEmpty()))
      return null;
    Collection localCollection = this.relationTable.values();
    Vector localVector1 = new Vector(localCollection);
    Vector localVector2 = new Vector();
    if (localVector1.contains("证书管理角色"))
    {
      localVector2.add("证书管理角色");
      localVector1.remove("证书管理角色");
    }
    if (localVector1.contains("模板管理角色"))
    {
      localVector2.add("模板管理角色");
      localVector1.remove("模板管理角色");
    }
    if (localVector1.contains("授权管理角色"))
    {
      localVector2.add("授权管理角色");
      localVector1.remove("授权管理角色");
    }
    if (localVector1.contains("审计管理角色"))
    {
      localVector2.add("审计管理角色");
      localVector1.remove("审计管理角色");
    }
    if (!localVector1.isEmpty())
      localVector2.addAll(localVector1);
    return localVector2;
  }

  void setRole(String paramString, Collection paramCollection)
    throws PrivilegeException
  {
    DBManager localDBManager = null;
    try
    {
      localDBManager = DBManager.getInstance();
    }
    catch (Exception localException1)
    {
      throw new PrivilegeException("0501", "初始化数据库失败", localException1);
    }
    String str = new String("");
    Vector localVector = new Vector(paramCollection);
    try
    {
      str = localDBManager.setRoleInfo(paramString, localVector);
    }
    catch (Exception localException2)
    {
      throw new PrivilegeException("0502", "调用数据库接口失败", localException2);
    }
    HashSet localHashSet = new HashSet(paramCollection);
    if (this.hashTable == null)
    {
      this.hashTable = new Hashtable();
      this.hashTable.put(str, localHashSet);
      this.relationTable.put(str, paramString);
    }
    else if (this.hashTable.containsKey(str))
    {
      this.hashTable.remove(str);
      this.hashTable.put(str, localHashSet);
    }
    else
    {
      this.hashTable.put(str, localHashSet);
      this.relationTable.put(str, paramString);
    }
  }

  void delRole(String paramString)
    throws PrivilegeException
  {
    DBManager localDBManager = null;
    try
    {
      localDBManager = DBManager.getInstance();
    }
    catch (Exception localException1)
    {
      throw new PrivilegeException("0501", "初始化数据库失败", localException1);
    }
    int i = -2;
    try
    {
      i = localDBManager.deleteRole(paramString);
    }
    catch (Exception localException2)
    {
      throw new PrivilegeException("0502", "调用数据库接口失败", localException2);
    }
    if (i < 0)
      throw new PrivilegeException("0362", "此角色不允许被删除");
    if (i == 0)
      throw new PrivilegeException("0361", "数据库中不存在此角色");
    if (this.hashTable.containsKey(paramString))
    {
      this.hashTable.remove(paramString);
      this.relationTable.remove(paramString);
    }
  }

  Vector getNameFromID(Vector paramVector)
  {
    if ((paramVector == null) || (paramVector.isEmpty()))
      return null;
    Vector localVector = new Vector();
    for (int i = 0; i < paramVector.size(); i++)
    {
      String str1 = (String)paramVector.get(i);
      if (!this.relationTable.containsKey(str1))
        continue;
      String str2 = (String)this.relationTable.get(str1);
      localVector.add(str2);
    }
    return localVector;
  }

  String getNameFromID(String paramString)
  {
    String str = null;
    if (this.relationTable.containsKey(paramString))
      str = (String)this.relationTable.get(paramString);
    return str;
  }

  Vector getIDFromName(Vector paramVector)
  {
    if ((paramVector == null) || (paramVector.isEmpty()))
      return null;
    Hashtable localHashtable = new Hashtable();
    Set localSet = this.relationTable.keySet();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
    	String localObject = (String)localIterator.next();
      String str1 = (String)this.relationTable.get(localObject);
      localHashtable.put(str1, localObject);
    }
    Object localObject = new Vector();
    for (int i = 0; i < paramVector.size(); i++)
    {
      String str2 = (String)paramVector.get(i);
      if (!localHashtable.containsKey(str2))
        continue;
      String str3 = (String)localHashtable.get(str2);
      ((Vector)localObject).add(str3);
    }
    return (Vector)localObject;
  }

  String getIDFromName(String paramString)
  {
	  String localObject = null;
    Set localSet = this.relationTable.keySet();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!this.relationTable.get(str).equals(paramString))
        continue;
      localObject = str;
    }
    return localObject;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.Role
 * JD-Core Version:    0.6.0
 */