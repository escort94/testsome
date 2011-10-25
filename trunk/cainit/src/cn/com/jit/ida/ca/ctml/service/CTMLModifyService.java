package cn.com.jit.ida.ca.ctml.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLInformation;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.CTMLPolicy;
import cn.com.jit.ida.ca.ctml.CTMLPolicyFactory;
import cn.com.jit.ida.ca.ctml.SelfExtensionInformation;
import cn.com.jit.ida.ca.ctml.SelfExtensionManager;
import cn.com.jit.ida.ca.ctml.service.request.CTMLModifyRequest;
import cn.com.jit.ida.ca.ctml.util.CTMLException;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTMLPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.ExtensionValueFilter;
import cn.com.jit.ida.ca.ctml.x509v3.extension.ExtensionValueFilterFactory;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.SelfExtensionPolicy;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import java.util.Vector;

public class CTMLModifyService extends CTMLService
{
  private CTMLInformation ctmlInfo;
  private CTMLPolicy ctmlPolicy;
  private CTML ctml;

  public CTMLModifyService()
  {
    super("CTMLMODIFY");
    this.debuger = LogManager.getDebugLogger("cn.com.jit.ida.ca.ctml.service.CTMLModifyService");
  }

  void analyseRequest(Request paramRequest)
    throws IDAException
  {
    if (this.isDebug)
      this.debuger.appendMsg_L2("analyse request ...");
    super.analyseRequest(paramRequest);
    CTMLModifyRequest localCTMLModifyRequest = new CTMLModifyRequest(paramRequest);
    String str1 = localCTMLModifyRequest.getName();
    String str2 = localCTMLModifyRequest.getId();
    if ((str1 == null) && (str2 == null))
    {
      if (this.isDebug)
        this.debuger.appendMsg_L1("invalid request data,name and id all null!");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.CTMLNAME_ISNULL);
    }
    String str3 = localCTMLModifyRequest.getDescription();
    byte[] arrayOfByte = localCTMLModifyRequest.getPolicy();
    if ((this.isLog) && (str1 != null))
      this.logInfo.setObjSubject(str1);
    if ((str3 == null) && (arrayOfByte == null))
    {
      if (this.isDebug)
        this.debuger.appendMsg_L1("invalid request data,description and policy all null");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.CTMLMODIFY_NODATA);
    }
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    if (str1 != null)
      this.ctml = localCTMLManager.getCTML(str1);
    else
      this.ctml = localCTMLManager.getCTMLByID(str2);
    Object localObject;
    if (arrayOfByte != null)
    {
      if (this.isLog)
        if (str1 != null)
          this.logInfo.setObjSubject(str1);
        else
          this.logInfo.setObjSubject("ID=" + str2);
      localObject = this.ctml.getCTMLType();
      this.ctmlPolicy = CTMLPolicyFactory.newCTMLPolicyInstance((String)localObject);
      if (this.ctmlPolicy == null)
      {
        if (this.isDebug)
          this.debuger.appendMsg_L1("invalid ctml type:" + (String)localObject);
        CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.CHECKPARAM.CTMLTYPE_INVALID);
        localCTMLServiceException.appendMsg((String)localObject);
        throw localCTMLServiceException;
      }
      try
      {
        this.ctmlPolicy.setCTMLPolicyDesc(arrayOfByte);
      }
      catch (CTMLException localCTMLException)
      {
        if (this.isDebug)
        {
          this.debuger.appendMsg_L1("invalid policy content:\n");
          this.debuger.appendMsg_L1(new String(arrayOfByte));
        }
        throw localCTMLException;
      }
      catch (Exception localException)
      {
        this.debuger.appendMsg_L1("invalid policy content:\n");
        this.debuger.appendMsg_L1(new String(arrayOfByte));
        throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.CTMLPOLICY_INVALID);
      }
      if ("UNUSED".equals(this.ctml.getCTMLStatus()))
      {
        checkExtension();
      }
      else
      {
        CTMLPolicy localCTMLPolicy = CTMLPolicyFactory.newCTMLPolicyInstance(this.ctml.getCTMLType());
        localCTMLPolicy.setCTMLPolicyDesc(this.ctml.getCTMLPolicyDesc());
        localCTMLPolicy.modifyCTMLPolicy(this.ctmlPolicy);
        this.ctmlPolicy = localCTMLPolicy;
      }
    }
    this.ctmlInfo = new CTMLInformation();
    this.ctmlInfo.setCTMLName(str1);
    this.ctmlInfo.setCTMLID(str2);
    this.ctmlInfo.setCTMLDesc(str3);
    this.ctmlInfo.setCTMLPolicy(this.ctmlPolicy);
    if (this.isDebug)
    {
      localObject = new StringBuffer();
      ((StringBuffer)localObject).append("request info:");
      ((StringBuffer)localObject).append("\n\t name = ");
      ((StringBuffer)localObject).append(str1 == null ? "null" : str1);
      ((StringBuffer)localObject).append("\n\t id = ");
      ((StringBuffer)localObject).append(str2 == null ? "null" : str2);
      ((StringBuffer)localObject).append("\n\t description = ");
      ((StringBuffer)localObject).append(str3 == null ? "null" : str3);
      ((StringBuffer)localObject).append("\n\t policy = ");
      ((StringBuffer)localObject).append(arrayOfByte == null ? "null" : new String(arrayOfByte));
      this.debuger.appendMsg_L2(((StringBuffer)localObject).toString());
      this.debuger.appendMsg_L2("analyse request finish!");
    }
  }

  void execute()
    throws IDAException
  {
    checkCTMLStatus();
    checkSystemLimit();
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    localCTMLManager.modifyCTML(this.ctmlInfo);
    if (this.isDebug)
      this.debuger.appendMsg_L2("modify ctml success!");
  }

  void checkExtension()
    throws IDAException
  {
    if (this.isDebug)
      this.debuger.appendMsg_L2("check extension...");
    if ((this.ctmlPolicy instanceof X509V3CTMLPolicy))
    {
      X509V3CTMLPolicy localX509V3CTMLPolicy = (X509V3CTMLPolicy)this.ctmlPolicy;
      SelfExtensionManager localSelfExtensionManager = SelfExtensionManager.getInstance();
      String[] arrayOfString = localSelfExtensionManager.getSelfExtList();
      Vector localVector1 = localX509V3CTMLPolicy.getSelfExtensionPolicys();
      SelfExtensionPolicy[] arrayOfSelfExtensionPolicy = new SelfExtensionPolicy[localVector1.size()];
      localVector1.copyInto(arrayOfSelfExtensionPolicy);
      Vector localVector2 = new Vector();
      for (int i = 0; i < arrayOfString.length; i++)
        localVector2.add(arrayOfString[i]);
      for (i = 0; i < arrayOfSelfExtensionPolicy.length; i++)
      {
        Object localObject2;
        if (localVector2.contains(arrayOfSelfExtensionPolicy[i].getName()))
        {
          localObject1 = arrayOfSelfExtensionPolicy[i].getUserProcessPolicy();
          localObject2 = localSelfExtensionManager.getSelfExt(arrayOfSelfExtensionPolicy[i].getName());
          arrayOfSelfExtensionPolicy[i].setEncoding(((SelfExtensionInformation)localObject2).getExtensionEncoding());
          arrayOfSelfExtensionPolicy[i].setOID(((SelfExtensionInformation)localObject2).getExtensionOID());
          if ("UNUSED".equals(((SelfExtensionInformation)localObject2).getExtensionStatus()))
            localSelfExtensionManager.updateSelfExtStatus(((SelfExtensionInformation)localObject2).getExtensionName(), "USING");
          if ("REVOKED".equals(((SelfExtensionInformation)localObject2).getExtensionStatus()))
          {
            localObject3 = new CTMLServiceException(CTMLServiceException.CHECKPARAM.EXTENSION_ISREVOKED);
            ((CTMLServiceException)localObject3).appendMsg(arrayOfSelfExtensionPolicy[i].getName());
            if (this.isDebug)
            {
              localObject4 = new StringBuffer();
              ((StringBuffer)localObject4).append("invalid extension policy information,extension is revoked\n");
              ((StringBuffer)localObject4).append("extension name is ");
              ((StringBuffer)localObject4).append(arrayOfSelfExtensionPolicy[i].getName());
              this.debuger.appendMsg_L1(((StringBuffer)localObject4).toString());
            }
            throw ((Throwable)localObject3);
          }
          if (!"DENY".equals(localObject1))
            continue;
          Object localObject3 = arrayOfSelfExtensionPolicy[i].getValue();
          if ((localObject3 == null) || (((String)localObject3).equals("")))
          {
            localObject4 = new CTMLServiceException(CTMLServiceException.CHECKPARAM.EXTENSIONVALUE_ISNULL);
            ((CTMLServiceException)localObject4).appendMsg(arrayOfSelfExtensionPolicy[i].getName());
            if (this.isDebug)
            {
              localObject5 = new StringBuffer();
              ((StringBuffer)localObject5).append("invalid extension policy information,value is null\n");
              ((StringBuffer)localObject5).append("extension name is ");
              ((StringBuffer)localObject5).append(arrayOfSelfExtensionPolicy[i].getName());
              this.debuger.appendMsg_L1(((StringBuffer)localObject5).toString());
            }
            throw ((Throwable)localObject4);
          }
          Object localObject4 = ExtensionValueFilterFactory.createFilter(((SelfExtensionInformation)localObject2).getExtensionEncoding());
          if ((((ExtensionValueFilter)localObject4).validate((String)localObject3)) && (((String)localObject3).length() <= 1024L))
            continue;
          Object localObject5 = new CTMLServiceException(CTMLServiceException.CHECKPARAM.EXTENSIONVALUE_INVALID);
          ((CTMLServiceException)localObject5).appendMsg(arrayOfSelfExtensionPolicy[i].getName());
          if (this.isDebug)
          {
            StringBuffer localStringBuffer = new StringBuffer();
            localStringBuffer.append("invalid extension value\n");
            localStringBuffer.append("extension name is ");
            localStringBuffer.append(arrayOfSelfExtensionPolicy[i].getName());
            this.debuger.appendMsg_L1(localStringBuffer.toString());
          }
          throw ((Throwable)localObject5);
        }
        Object localObject1 = new CTMLServiceException(CTMLServiceException.CHECKPARAM.EXTENSION_NOTSUPPORT);
        ((CTMLServiceException)localObject1).appendMsg(arrayOfSelfExtensionPolicy[i].getName());
        if (this.isDebug)
        {
          localObject2 = new StringBuffer();
          ((StringBuffer)localObject2).append("invalid extension policy information,not support this extension!\n");
          ((StringBuffer)localObject2).append("extension name is ");
          ((StringBuffer)localObject2).append(arrayOfSelfExtensionPolicy[i].getName());
          this.debuger.appendMsg_L1(((StringBuffer)localObject2).toString());
        }
        throw ((Throwable)localObject1);
      }
    }
    if (this.isDebug)
      this.debuger.appendMsg_L2("finish check extension");
  }

  private void checkCTMLStatus()
    throws IDAException
  {
    if ((!"UNUSED".equals(this.ctml.getCTMLStatus())) && (!"USING".equals(this.ctml.getCTMLStatus())))
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.CHECKSTATUS.CTMLSTATUS_NONMODIFY);
      localCTMLServiceException.appendMsg(this.ctml.getCTMLName() + "-" + this.ctml.getCTMLStatus());
      throw localCTMLServiceException;
    }
  }

  private void checkSystemLimit()
    throws IDAException
  {
    InternalConfig localInternalConfig = GlobalConfig.getInstance().getInternalConfig();
    String str = localInternalConfig.getAdminTemplateName();
    if (str.equals(this.ctml.getCTMLName()))
    {
      localObject = new CTMLServiceException(CTMLServiceException.CHECKSYSLIMIT.ADMINCTML_CANTMODIFY);
      throw ((Throwable)localObject);
    }
    Object localObject = localInternalConfig.getCommCertTemplateName();
    if (((String)localObject).equals(this.ctml.getCTMLName()))
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.CHECKSYSLIMIT.COMMCTML_CANTMODIFY);
      throw localCTMLServiceException;
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.CTMLModifyService
 * JD-Core Version:    0.6.0
 */