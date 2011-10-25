package cn.com.jit.ida.ca.ctml;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.util.CTMLException;
import cn.com.jit.ida.ca.ctml.util.CTMLException.SELFEXT;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;
import java.util.Properties;
import java.util.Vector;

public class SelfExtensionManager
{
  private static SelfExtensionManager instance;
  private static Vector SelfExtInformationList;
  SysLogger logger = LogManager.getSysLogger();
  DebugLogger debuger = LogManager.getDebugLogger("cn.com.jit.ida.ca.ctml.SelfExtensionManager");

  private SelfExtensionManager()
  {
    this.debuger.appendMsg_L2("call SelfExtensionManager construct function");
    this.debuger.doLog();
  }

  private synchronized void initSelfExtInfoList()
    throws IDAException
  {
    this.debuger.appendMsg_L2("call SelfExtensionManager initSelfExtInfoList function");
    this.debuger.doLog();
    Properties localProperties = new Properties();
    Properties[] arrayOfProperties = null;
    try
    {
      DBManager localDBManager = DBManager.getInstance();
      arrayOfProperties = localDBManager.getSelfExt(localProperties);
    }
    catch (IDAException localIDAException)
    {
      this.debuger.doLog(localIDAException);
      throw localIDAException;
    }
    if (arrayOfProperties == null)
    {
      this.debuger.appendMsg_L2("getSelfExt form db Result is empty  !");
      this.debuger.doLog();
      return;
    }
    if (SelfExtInformationList != null)
      SelfExtInformationList = null;
    SelfExtInformationList = new Vector(arrayOfProperties.length);
    for (int i = 0; i < arrayOfProperties.length; i++)
    {
      SelfExtensionInformation localSelfExtensionInformation = null;
      localSelfExtensionInformation = new SelfExtensionInformation();
      localSelfExtensionInformation.setExtensionName(arrayOfProperties[i].getProperty("selfext_name"));
      localSelfExtensionInformation.setExtensionOID(arrayOfProperties[i].getProperty("selfext_oid"));
      localSelfExtensionInformation.setExtensionStatus(arrayOfProperties[i].getProperty("selfext_status"));
      localSelfExtensionInformation.setExtensionEncoding(arrayOfProperties[i].getProperty("selfext_encodetype"));
      localSelfExtensionInformation.setExtensionDesc(arrayOfProperties[i].getProperty("selfext_description"));
      SelfExtInformationList.add(i, localSelfExtensionInformation);
    }
  }

  public static synchronized void initializeInstance()
    throws IDAException
  {
    if (instance == null)
      instance = new SelfExtensionManager();
    instance.initSelfExtInfoList();
  }

  public static SelfExtensionManager getInstance()
  {
    return instance;
  }

  public void createSelfExt(SelfExtensionInformation paramSelfExtensionInformation)
    throws IDAException
  {
    checkParam(paramSelfExtensionInformation, CTMLException.SELFEXT.CREATESELFEXT_BADPARAM);
    if (getSelfExtInfo(paramSelfExtensionInformation.getExtensionName()) != null)
    {
      localObject = new CTMLException(CTMLException.SELFEXT.SELFEXT_EXISTED);
      ((CTMLException)localObject).appendMsg("自定义扩展域名称 =" + paramSelfExtensionInformation.getExtensionName());
      throw ((Throwable)localObject);
    }
    if (getSelfExtInfoByOID(paramSelfExtensionInformation.getExtensionOID()) != null)
    {
      localObject = new CTMLException(CTMLException.SELFEXT.SELFEXT_EXISTED);
      ((CTMLException)localObject).appendMsg("自定义扩展域OID =" + paramSelfExtensionInformation.getExtensionOID());
      throw ((Throwable)localObject);
    }
    Object localObject = new Properties();
    ((Properties)localObject).setProperty("selfext_name", paramSelfExtensionInformation.getExtensionName());
    ((Properties)localObject).setProperty("selfext_status", paramSelfExtensionInformation.getExtensionStatus());
    ((Properties)localObject).setProperty("selfext_oid", paramSelfExtensionInformation.getExtensionOID());
    ((Properties)localObject).setProperty("selfext_encodetype", paramSelfExtensionInformation.getExtensionEncoding());
    ((Properties)localObject).setProperty("selfext_description", paramSelfExtensionInformation.getExtensionDesc());
    try
    {
      DBManager localDBManager = DBManager.getInstance();
      localDBManager.saveSelfExt((Properties)localObject);
    }
    catch (IDAException localIDAException)
    {
      this.debuger.doLog(localIDAException);
      throw localIDAException;
    }
    initSelfExtInfoList();
  }

  public void deleteSelfExt(String paramString)
    throws IDAException
  {
    checkParam(paramString, CTMLException.SELFEXT.DELETESELFEXT_BADNAMEPARAM);
    SelfExtensionInformation localSelfExtensionInformation = getSelfExtInfo(paramString);
    checkParam(localSelfExtensionInformation, CTMLException.SELFEXT.GETSELFEXT_NOTFINDBYNAME);
    Properties localProperties = new Properties();
    localProperties.setProperty("selfext_name", paramString);
    try
    {
      DBManager localDBManager = DBManager.getInstance();
      localDBManager.deleteSelfExt(localProperties);
    }
    catch (IDAException localIDAException)
    {
      this.debuger.doLog(localIDAException);
      throw localIDAException;
    }
    initSelfExtInfoList();
  }

  public void deleteSelfExtByOID(String paramString)
    throws IDAException
  {
    checkParam(paramString, CTMLException.SELFEXT.DELETESELFEXT_BADOIDPARAM);
    SelfExtensionInformation localSelfExtensionInformation = getSelfExtInfoByOID(paramString);
    checkParam(localSelfExtensionInformation, CTMLException.SELFEXT.GETSELFEXT_NOTFINDBYOID);
    Properties localProperties = new Properties();
    localProperties.setProperty("selfext_oid", paramString);
    try
    {
      DBManager localDBManager = DBManager.getInstance();
      localDBManager.deleteSelfExt(localProperties);
    }
    catch (IDAException localIDAException)
    {
      this.debuger.doLog(localIDAException);
      throw localIDAException;
    }
    initSelfExtInfoList();
  }

  public void modifySelfExt(SelfExtensionInformation paramSelfExtensionInformation)
    throws IDAException
  {
    checkParam(paramSelfExtensionInformation, CTMLException.SELFEXT.MODIFYSELFEXT_BADPARAM);
    SelfExtensionInformation localSelfExtensionInformation = null;
    if (paramSelfExtensionInformation.getExtensionName() != null)
      localSelfExtensionInformation = getSelfExt(paramSelfExtensionInformation.getExtensionName());
    else
      localSelfExtensionInformation = getSelfExtByOID(paramSelfExtensionInformation.getExtensionOID());
    checkParam(localSelfExtensionInformation, CTMLException.SELFEXT.GETSELFEXT_NOTFIND);
    Properties localProperties1 = new Properties();
    localProperties1.setProperty("selfext_name", localSelfExtensionInformation.getExtensionName());
    Properties localProperties2 = new Properties();
    localProperties2.setProperty("selfext_encodetype", paramSelfExtensionInformation.getExtensionEncoding());
    localProperties2.setProperty("selfext_description", paramSelfExtensionInformation.getExtensionDesc());
    try
    {
      DBManager localDBManager = DBManager.getInstance();
      localDBManager.modifySelfExt(localProperties1, localProperties2);
    }
    catch (IDAException localIDAException)
    {
      this.debuger.doLog(localIDAException);
      throw localIDAException;
    }
    initSelfExtInfoList();
  }

  public void updateSelfExtStatus(String paramString1, String paramString2)
    throws IDAException
  {
    if ((paramString1 == null) || (paramString2 == null))
    {
      localObject = new CTMLException(CTMLException.SELFEXT.UPDATESELFEXTSTATUS_BADPARAM);
      this.debuger.doLog((Throwable)localObject);
      throw ((Throwable)localObject);
    }
    Object localObject = new Properties();
    ((Properties)localObject).setProperty("selfext_name", paramString1);
    Properties localProperties = new Properties();
    localProperties.setProperty("selfext_status", paramString2);
    try
    {
      DBManager localDBManager = DBManager.getInstance();
      localDBManager.modifySelfExt((Properties)localObject, localProperties);
    }
    catch (IDAException localIDAException)
    {
      this.debuger.doLog(localIDAException);
      throw localIDAException;
    }
    initSelfExtInfoList();
  }

  public SelfExtensionInformation getSelfExt(String paramString)
    throws IDAException
  {
    checkParam(paramString, CTMLException.SELFEXT.GETSELFEXT_BADNAMEPARAM);
    checkSelfExtContainer();
    SelfExtensionInformation localSelfExtensionInformation = null;
    for (int i = 0; i < SelfExtInformationList.size(); i++)
    {
      localSelfExtensionInformation = (SelfExtensionInformation)SelfExtInformationList.get(i);
      if (localSelfExtensionInformation.getExtensionName().equalsIgnoreCase(paramString))
        break;
      localSelfExtensionInformation = null;
    }
    checkParam(localSelfExtensionInformation, CTMLException.SELFEXT.GETSELFEXT_NOTFINDBYNAME);
    return localSelfExtensionInformation;
  }

  public String[] getSelfExtList()
    throws IDAException
  {
    checkSelfExtContainer();
    String[] arrayOfString = null;
    arrayOfString = new String[SelfExtInformationList.size()];
    for (int i = 0; i < SelfExtInformationList.size(); i++)
      arrayOfString[i] = ((SelfExtensionInformation)SelfExtInformationList.get(i)).getExtensionName();
    return arrayOfString;
  }

  public SelfExtensionInformation getSelfExtByOID(String paramString)
    throws IDAException
  {
    checkParam(paramString, CTMLException.SELFEXT.GETSELFEXT_BADOIDPARAM);
    checkSelfExtContainer();
    SelfExtensionInformation localSelfExtensionInformation = null;
    for (int i = 0; i < SelfExtInformationList.size(); i++)
    {
      localSelfExtensionInformation = (SelfExtensionInformation)SelfExtInformationList.get(i);
      if (localSelfExtensionInformation.getExtensionOID().equals(paramString))
        break;
      localSelfExtensionInformation = null;
    }
    checkParam(localSelfExtensionInformation, CTMLException.SELFEXT.GETSELFEXT_NOTFINDBYOID);
    return localSelfExtensionInformation;
  }

  private SelfExtensionInformation getSelfExtInfo(String paramString)
  {
    SelfExtensionInformation localSelfExtensionInformation = null;
    for (int i = 0; i < SelfExtInformationList.size(); i++)
    {
      localSelfExtensionInformation = (SelfExtensionInformation)SelfExtInformationList.get(i);
      if (localSelfExtensionInformation.getExtensionName().equalsIgnoreCase(paramString))
        break;
      localSelfExtensionInformation = null;
    }
    return localSelfExtensionInformation;
  }

  private SelfExtensionInformation getSelfExtInfoByOID(String paramString)
  {
    SelfExtensionInformation localSelfExtensionInformation = null;
    for (int i = 0; i < SelfExtInformationList.size(); i++)
    {
      localSelfExtensionInformation = (SelfExtensionInformation)SelfExtInformationList.get(i);
      if (localSelfExtensionInformation.getExtensionOID().equals(paramString))
        break;
      localSelfExtensionInformation = null;
    }
    return localSelfExtensionInformation;
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

  private void checkParam(SelfExtensionInformation paramSelfExtensionInformation, int paramInt)
    throws CTMLException
  {
    if (paramSelfExtensionInformation == null)
    {
      CTMLException localCTMLException = new CTMLException(paramInt);
      this.debuger.doLog(localCTMLException);
      throw localCTMLException;
    }
  }

  private void checkSelfExtContainer()
    throws CTMLException
  {
    if (SelfExtInformationList == null)
    {
      CTMLException localCTMLException = new CTMLException(CTMLException.SELFEXT.SELFEXTLIST_ISNULL);
      this.debuger.doLog(localCTMLException);
      throw localCTMLException;
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.SelfExtensionManager
 * JD-Core Version:    0.6.0
 */