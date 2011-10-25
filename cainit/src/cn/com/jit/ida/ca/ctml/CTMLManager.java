package cn.com.jit.ida.ca.ctml;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.util.CTMLException;
import cn.com.jit.ida.ca.ctml.util.CTMLException.CTML;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;
import java.util.Properties;
import java.util.Vector;

public class CTMLManager
{
  private static final String RESERVE_CTMLTYPE = "N/A";
  private static CTMLManager instance;
  private Vector ctmlInformationList = new Vector();
  private IDManager idmanager = new IDManager();
  SysLogger logger = LogManager.getSysLogger();
  DebugLogger debuger = LogManager.getDebugLogger("cn.com.jit.ida.ca.ctml.CTMLManager");

  private CTMLManager()
  {
    this.debuger.appendMsg_L2("call construct function");
    this.debuger.doLog();
  }

  public static CTMLManager getInstance()
  {
    return instance;
  }

  public static synchronized void initializeInstance()
    throws IDAException
  {
    if (instance == null)
      instance = new CTMLManager();
    instance.initCtmlInfoList();
  }

  private synchronized void initCtmlInfoList()
    throws IDAException
  {
    this.debuger.appendMsg_L2("call initCtmlInfoList");
    this.debuger.doLog();
    Properties localProperties = new Properties();
    Properties[] arrayOfProperties = null;
    try
    {
      DBManager localDBManager = DBManager.getInstance();
      arrayOfProperties = localDBManager.getCTML(localProperties);
    }
    catch (IDAException localIDAException1)
    {
      this.debuger.doLog(localIDAException1);
      throw localIDAException1;
    }
    if (arrayOfProperties == null)
    {
      this.debuger.appendMsg_L1("not find valid ctml record!");
      this.debuger.doLog();
      return;
    }
    Vector localVector = new Vector(arrayOfProperties.length);
    for (int i = 0; i < arrayOfProperties.length; i++)
      try
      {
        if (arrayOfProperties[i].getProperty("ctml_type").equals("N/A"))
          continue;
        CTMLInformation localCTMLInformation = null;
        localCTMLInformation = new CTMLInformation();
        localCTMLInformation.setCTMLName(arrayOfProperties[i].getProperty("ctml_name"));
        localCTMLInformation.setCTMLID(arrayOfProperties[i].getProperty("ctml_id"));
        localCTMLInformation.setCTMLStatus(arrayOfProperties[i].getProperty("ctml_status"));
        localCTMLInformation.setCTMLType(arrayOfProperties[i].getProperty("ctml_type"));
        localCTMLInformation.setCTMLPolicy(arrayOfProperties[i].getProperty("ctml_policyinfo").getBytes());
        localCTMLInformation.setCTMLDesc(arrayOfProperties[i].getProperty("ctml_description"));
        localVector.add(localCTMLInformation);
      }
      catch (IDAException localIDAException2)
      {
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append("find a invalid ctml record ");
        localStringBuffer.append("\n\t name = ");
        localStringBuffer.append(convert(arrayOfProperties[i].getProperty("ctml_name")));
        localStringBuffer.append("\n\t id = ");
        localStringBuffer.append(convert(arrayOfProperties[i].getProperty("ctml_id")));
        localStringBuffer.append("\n\t status = ");
        localStringBuffer.append(convert(arrayOfProperties[i].getProperty("ctml_status")));
        localStringBuffer.append("\n\t type = ");
        localStringBuffer.append(convert(arrayOfProperties[i].getProperty("ctml_type")));
        localStringBuffer.append("\n\t description = ");
        localStringBuffer.append(convert(arrayOfProperties[i].getProperty("ctml_description")));
        localStringBuffer.append("\n\t policyinfo = ");
        localStringBuffer.append(convert(arrayOfProperties[i].getProperty("ctml_policyinfo")));
        this.debuger.appendMsg_L1(localStringBuffer.toString());
        this.debuger.doLog(localIDAException2);
      }
    this.ctmlInformationList = localVector;
    if (localVector.size() == 0)
      this.debuger.appendMsg_L1("not find valid ctml record!");
    else
      this.debuger.appendMsg_L2("find " + localVector.size() + " valid ctml record!");
    this.debuger.doLog();
  }

  public CTML getCTML(String paramString)
    throws IDAException
  {
    checkParam(paramString, CTMLException.CTML.GETCTML_BADPARAM);
    CTMLInformation localCTMLInformation = getCTMLInfo(paramString);
    checkParam(localCTMLInformation, CTMLException.CTML.GETCTML_NOTFIND);
    return CTMLFactory.newCTMLInstance(localCTMLInformation);
  }

  public CTML getCTMLByID(String paramString)
    throws IDAException
  {
    checkParam(paramString, CTMLException.CTML.GETCTML_BADIDPARAM);
    CTMLInformation localCTMLInformation = getCTMLInfoByID(paramString);
    checkParam(localCTMLInformation, CTMLException.CTML.GETCTML_NOTFINDBYID);
    return CTMLFactory.newCTMLInstance(localCTMLInformation);
  }

  public String[] getCTMLList()
    throws IDAException
  {
    String[] arrayOfString = null;
    arrayOfString = new String[this.ctmlInformationList.size()];
    for (int i = 0; i < this.ctmlInformationList.size(); i++)
      arrayOfString[i] = ((CTMLInformation)this.ctmlInformationList.get(i)).getCTMLName();
    return arrayOfString;
  }

  public void createCTML(CTMLInformation paramCTMLInformation)
    throws IDAException
  {
    checkParam(paramCTMLInformation, CTMLException.CTML.CREATECTML_BADPARAM);
    if (getCTMLInfo(paramCTMLInformation.getCTMLName()) != null)
    {
      localObject = new CTMLException(CTMLException.CTML.CTML_EXISTED);
      ((CTMLException)localObject).appendMsg("ctmlName =" + paramCTMLInformation.getCTMLName());
      throw ((Throwable)localObject);
    }
    paramCTMLInformation.setCTMLStatus("UNUSED");
    Object localObject = new Properties();
    ((Properties)localObject).setProperty("ctml_name", paramCTMLInformation.getCTMLName());
    ((Properties)localObject).setProperty("ctml_status", paramCTMLInformation.getCTMLStatus());
    ((Properties)localObject).setProperty("ctml_id", paramCTMLInformation.getCTMLID());
    ((Properties)localObject).setProperty("ctml_type", paramCTMLInformation.getCTMLType());
    ((Properties)localObject).setProperty("ctml_policyinfo", new String(paramCTMLInformation.getCTMLPolicy().getCTMLPolicyDesc()));
    ((Properties)localObject).setProperty("ctml_description", paramCTMLInformation.getCTMLDesc());
    try
    {
      DBManager localDBManager = DBManager.getInstance();
      localDBManager.saveCTML((Properties)localObject);
    }
    catch (IDAException localIDAException)
    {
      this.debuger.doLog(localIDAException);
      throw localIDAException;
    }
    initCtmlInfoList();
  }

  public void modifyCTML(CTMLInformation paramCTMLInformation)
    throws IDAException
  {
    checkParam(paramCTMLInformation, CTMLException.CTML.MODIFYCTML_BADPARAM);
    CTMLInformation localCTMLInformation = null;
    if (paramCTMLInformation.getCTMLName() != null)
      localCTMLInformation = getCTMLInfo(paramCTMLInformation.getCTMLName());
    else
      localCTMLInformation = getCTMLInfoByID(paramCTMLInformation.getCTMLID());
    checkParam(localCTMLInformation, CTMLException.CTML.GETCTML_NOTFIND);
    Properties localProperties1 = new Properties();
    localProperties1.setProperty("ctml_name", localCTMLInformation.getCTMLName());
    Properties localProperties2 = new Properties();
    Object localObject;
    if (paramCTMLInformation.getCTMLPolicy() != null)
    {
      localObject = paramCTMLInformation.getCTMLPolicy().getCTMLPolicyDesc();
      localProperties2.setProperty("ctml_policyinfo", new String(localObject));
    }
    if (paramCTMLInformation.getCTMLDesc() != null)
      localProperties2.setProperty("ctml_description", paramCTMLInformation.getCTMLDesc());
    try
    {
      localObject = DBManager.getInstance();
      ((DBManager)localObject).modifyCTML(localProperties1, localProperties2);
    }
    catch (IDAException localIDAException)
    {
      this.debuger.doLog(localIDAException);
      throw localIDAException;
    }
    initCtmlInfoList();
  }

  public void updateCTMLStatus(String paramString1, String paramString2)
    throws IDAException
  {
    if ((paramString1 == null) || (paramString2 == null))
    {
    	CTMLException localObject = new CTMLException(CTMLException.CTML.UPDATECTMLSTATUS_BADPARAM);
      ((CTMLException)localObject).appendMsg("Class  CTMLManager function updateCTMLStatus paramter ctmlName or newStatus invalidation!");
      this.debuger.doLog((Throwable)localObject);
      throw localObject;
    }
    Object localObject = new Properties();
    ((Properties)localObject).setProperty("ctml_name", paramString1);
    Properties localProperties = new Properties();
    localProperties.setProperty("ctml_status", paramString2);
    try
    {
      DBManager localDBManager = DBManager.getInstance();
      localDBManager.modifyCTML((Properties)localObject, localProperties);
    }
    catch (IDAException localIDAException)
    {
      this.debuger.doLog(localIDAException);
      throw localIDAException;
    }
    initCtmlInfoList();
  }

  public void deleteCTML(String paramString)
    throws IDAException
  {
    checkParam(paramString, CTMLException.CTML.DELETECTML_BADNAMEPARAM);
    CTMLInformation localCTMLInformation = getCTMLInfo(paramString);
    checkParam(localCTMLInformation, CTMLException.CTML.GETCTML_NOTFINDBYNAME);
    Properties localProperties = new Properties();
    localProperties.setProperty("ctml_name", localCTMLInformation.getCTMLName());
    try
    {
      DBManager localDBManager = DBManager.getInstance();
      localDBManager.deleteCTML(localProperties);
    }
    catch (IDAException localIDAException)
    {
      this.debuger.doLog(localIDAException);
      throw localIDAException;
    }
    initCtmlInfoList();
  }

  public void deleteCTMLByID(String paramString)
    throws IDAException
  {
    checkParam(paramString, CTMLException.CTML.DELETECTML_BADIDPARAM);
    CTMLInformation localCTMLInformation = getCTMLInfoByID(paramString);
    checkParam(localCTMLInformation, CTMLException.CTML.GETCTML_NOTFINDBYID);
    Properties localProperties = new Properties();
    localProperties.setProperty("ctml_id", localCTMLInformation.getCTMLID());
    try
    {
      DBManager localDBManager = DBManager.getInstance();
      localDBManager.deleteCTML(localProperties);
    }
    catch (IDAException localIDAException)
    {
      this.debuger.doLog(localIDAException);
      throw localIDAException;
    }
    initCtmlInfoList();
  }

  private CTMLInformation getCTMLInfo(String paramString)
  {
    CTMLInformation localCTMLInformation = null;
    for (int i = 0; i < this.ctmlInformationList.size(); i++)
    {
      localCTMLInformation = (CTMLInformation)this.ctmlInformationList.get(i);
      if (localCTMLInformation.getCTMLName().equalsIgnoreCase(paramString))
        break;
      localCTMLInformation = null;
    }
    return localCTMLInformation;
  }

  private CTMLInformation getCTMLInfoByID(String paramString)
  {
    CTMLInformation localCTMLInformation = null;
    for (int i = 0; i < this.ctmlInformationList.size(); i++)
    {
      localCTMLInformation = (CTMLInformation)this.ctmlInformationList.get(i);
      if (localCTMLInformation.getCTMLID().equals(paramString))
        break;
      localCTMLInformation = null;
    }
    return localCTMLInformation;
  }

  public String generateCTMLID()
    throws IDAException
  {
    return this.idmanager.getNextID();
  }

  private String convert(String paramString)
  {
    if (paramString == null)
      return "null";
    return paramString;
  }

  private void checkParam(String paramString, int paramInt)
    throws CTMLException
  {
    if (paramString == null)
    {
      CTMLException localCTMLException = new CTMLException(paramInt);
      this.debuger.doLog(localCTMLException);
      throw localCTMLException;
    }
  }

  private void checkParam(CTMLInformation paramCTMLInformation, int paramInt)
    throws CTMLException
  {
    if (paramCTMLInformation == null)
    {
      CTMLException localCTMLException = new CTMLException(paramInt);
      this.debuger.doLog(localCTMLException);
      throw localCTMLException;
    }
  }

  class IDManager
  {
    private static final String RESERVE_ID = "0";
    private static final String NA = "N/A";
    private String nextID = "1";

    IDManager()
    {
    }

    synchronized String getNextID()
      throws IDAException
    {
      Object localObject;
      try
      {
        DBManager localDBManager = DBManager.getInstance();
        localObject = new Properties();
        ((Properties)localObject).setProperty("ctml_id", "0");
        Properties[] arrayOfProperties = localDBManager.getCTML((Properties)localObject);
        if ((arrayOfProperties == null) || (arrayOfProperties.length == 0))
        {
          initRecord();
        }
        else
        {
          this.nextID = arrayOfProperties[0].getProperty("reserve");
          updateRecord();
        }
        return this.nextID;
      }
      catch (Exception localException)
      {
        localObject = new CTMLException(CTMLException.CTML.GEN_CTMLID, localException);
        CTMLManager.this.debuger.doLog((Throwable)localObject);
      }
      throw ((Throwable)localObject);
    }

    private void updateRecord()
      throws Exception
    {
      String str = Integer.toString(Integer.parseInt(this.nextID) + 1);
      DBManager localDBManager = DBManager.getInstance();
      Properties localProperties1 = new Properties();
      Properties localProperties2 = new Properties();
      localProperties1.setProperty("ctml_id", "0");
      localProperties2.setProperty("reserve", str);
      int i = localDBManager.modifyCTML(localProperties1, localProperties2);
    }

    private void initRecord()
      throws Exception
    {
      String str = Integer.toString(Integer.parseInt(this.nextID) + 1);
      DBManager localDBManager = DBManager.getInstance();
      Properties localProperties = new Properties();
      localProperties.setProperty("ctml_name", "0");
      localProperties.setProperty("ctml_id", "0");
      localProperties.setProperty("ctml_status", "N/A");
      localProperties.setProperty("ctml_type", "N/A");
      localProperties.setProperty("ctml_policyinfo", "N/A");
      localProperties.setProperty("ctml_description", "N/A");
      localProperties.setProperty("reserve", str);
      int i = localDBManager.saveCTML(localProperties);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.CTMLManager
 * JD-Core Version:    0.6.0
 */