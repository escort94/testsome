package cn.com.jit.ida.privilege;

import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class FunctionPoint
{
  private static FunctionPoint instance;
  Hashtable hashTable = null;

  private FunctionPoint()
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
    try
    {
      this.hashTable = localDBManager.getFuctionPointsInfo();
    }
    catch (DBException localDBException2)
    {
      throw new PrivilegeException(localDBException2.getErrCode(), localDBException2.getErrDesc());
    }
    if ((this.hashTable == null) || (this.hashTable.isEmpty()))
      throw new PrivilegeException("0481", "权限点集合为空");
  }

  static FunctionPoint getInstance()
    throws PrivilegeException
  {
    if (instance == null)
      instance = new FunctionPoint();
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
    catch (DBException localDBException1)
    {
      throw new PrivilegeException(localDBException1.getErrCode(), localDBException1.getErrDesc());
    }
    try
    {
      this.hashTable = localDBManager.getFuctionPointsInfo();
    }
    catch (DBException localDBException2)
    {
      throw new PrivilegeException(localDBException2.getErrCode(), localDBException2.getErrDesc());
    }
    if ((this.hashTable == null) || (this.hashTable.isEmpty()))
      throw new PrivilegeException("0481", "权限点集合为空");
  }

  HashSet getPrivilegeIDList()
    throws PrivilegeException
  {
    if ((this.hashTable == null) || (this.hashTable.isEmpty()))
      throw new PrivilegeException("0481", "权限点集合为空");
    Set localSet = this.hashTable.keySet();
    HashSet localHashSet = new HashSet(localSet);
    return localHashSet;
  }

  HashSet getPrivilegeNameList()
    throws PrivilegeException
  {
    if ((this.hashTable == null) || (this.hashTable.isEmpty()))
      throw new PrivilegeException("0481", "权限点集合为空");
    Collection localCollection = this.hashTable.values();
    HashSet localHashSet = new HashSet(localCollection);
    return localHashSet;
  }

  Vector getNameFromID(Vector paramVector)
  {
    if ((paramVector == null) || (paramVector.isEmpty()))
      return null;
    Vector localVector = new Vector();
    for (int i = 0; i < paramVector.size(); i++)
    {
      String str1 = (String)paramVector.get(i);
      if (!this.hashTable.containsKey(str1))
        continue;
      String str2 = (String)this.hashTable.get(str1);
      localVector.add(str2);
    }
    return localVector;
  }

  Vector getIDFromName(Vector paramVector)
  {
    if ((paramVector == null) || (paramVector.isEmpty()))
      return null;
    Hashtable localHashtable = new Hashtable();
    Set localSet = this.hashTable.keySet();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
    	String localObject = (String)localIterator.next();
      String str1 = (String)this.hashTable.get(localObject);
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
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.FunctionPoint
 * JD-Core Version:    0.6.0
 */