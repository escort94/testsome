package cn.com.jit.ida.ca.ctml.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.ctml.CTMLInformation;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.CTMLPolicy;
import cn.com.jit.ida.ca.ctml.CTMLPolicyFactory;
import cn.com.jit.ida.ca.ctml.SelfExtensionInformation;
import cn.com.jit.ida.ca.ctml.SelfExtensionManager;
import cn.com.jit.ida.ca.ctml.service.request.CTMLCreateRequest;
import cn.com.jit.ida.ca.ctml.util.CTMLException;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTMLPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.ExtensionValueFilter;
import cn.com.jit.ida.ca.ctml.x509v3.extension.ExtensionValueFilterFactory;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.SelfExtensionPolicy;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.privilege.TemplateAdmin;
import java.util.Vector;

public class CTMLCreateService extends CTMLService
{
  private CTMLInformation ctmlInfo;
  private CTMLPolicy ctmlPolicy;
  private CTMLManager manager = CTMLManager.getInstance();

  public CTMLCreateService()
  {
    super("CTMLCREATE");
    this.debuger = LogManager.getDebugLogger("cn.com.jit.ida.ca.ctml.service.CTMLCreateService");
  }

  void analyseRequest(Request paramRequest)
    throws IDAException
  {
    if (this.isDebug)
      this.debuger.appendMsg_L2("ananlyseRequest");
    super.analyseRequest(paramRequest);
    CTMLCreateRequest localCTMLCreateRequest = new CTMLCreateRequest(paramRequest);
    String str1 = localCTMLCreateRequest.getName();
    if (str1 == null)
    {
      this.debuger.appendMsg_L1("name = null");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.CTMLNAME_ISNULL);
    }
    if (this.isLog)
      this.logInfo.setObjSubject(str1);
    String str2 = localCTMLCreateRequest.getType();
    if (str2 == null)
    {
      if (this.isDebug)
        this.debuger.appendMsg_L1("type = null");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.CTMLTYPE_ISNULL);
    }
    this.ctmlPolicy = CTMLPolicyFactory.newCTMLPolicyInstance(str2);
    if (this.ctmlPolicy == null)
    {
      if (this.isDebug)
        this.debuger.appendMsg_L1("invalid ctml type:" + str2);
      localObject = new CTMLServiceException(CTMLServiceException.CHECKPARAM.CTMLTYPE_INVALID);
      ((CTMLServiceException)localObject).appendMsg(str2);
      throw ((Throwable)localObject);
    }
    Object localObject = localCTMLCreateRequest.getDescription();
    byte[] arrayOfByte = localCTMLCreateRequest.getPolicy();
    if (arrayOfByte == null)
    {
      if (this.isDebug)
        this.debuger.appendMsg_L1("policy = null");
      throw new CTMLServiceException(CTMLServiceException.CHECKPARAM.CTMLPOLICY_ISNULL);
    }
    try
    {
      this.ctmlPolicy.setCTMLPolicyDesc(arrayOfByte);
    }
    catch (CTMLException localCTMLException)
    {
      if (this.isDebug)
      {
        this.debuger.appendMsg_L1("parse policy fail,policy content invalid!");
        this.debuger.appendMsg_L1(new String(arrayOfByte));
      }
      throw localCTMLException;
    }
    catch (Exception localException)
    {
      if (this.isDebug)
      {
        this.debuger.appendMsg_L1("parse policy fail,policy content invalid!");
        this.debuger.appendMsg_L1(new String(arrayOfByte));
      }
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.REQUEST.PARSE_REQUESTXMLDATA, localException);
      throw localCTMLServiceException;
    }
    checkExtension();
    this.ctmlInfo = new CTMLInformation();
    this.ctmlInfo.setCTMLName(str1);
    this.ctmlInfo.setCTMLType(str2);
    this.ctmlInfo.setCTMLDesc((String)localObject);
    this.ctmlInfo.setCTMLPolicy(this.ctmlPolicy);
    this.ctmlInfo.setCTMLID(this.manager.generateCTMLID());
    if (this.isDebug)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append("ctml info:");
      localStringBuffer.append("\n\t name = ");
      localStringBuffer.append(str1);
      localStringBuffer.append("\n\t type = ");
      localStringBuffer.append(str2);
      localStringBuffer.append("\n\t description = ");
      localStringBuffer.append((String)localObject);
      localStringBuffer.append("\n\t id = ");
      localStringBuffer.append(this.ctmlInfo.getCTMLID());
      this.debuger.appendMsg_L2(localStringBuffer.toString());
    }
  }

  void execute()
    throws IDAException
  {
    this.manager.createCTML(this.ctmlInfo);
    if (this.isDebug)
      this.debuger.appendMsg_L2("create ctml success!");
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

  protected void doOtherUpdate()
  {
    if (this.isDebug)
      this.debuger.appendMsg_L2("start refresh admin info...");
    try
    {
      TemplateAdmin.getInstance().refreshAdminInfo();
    }
    catch (Exception localException)
    {
      if (this.isDebug)
      {
        this.debuger.appendMsg_L1("refresh admin info failed!");
        this.debuger.doLog(localException);
      }
    }
    this.debuger.appendMsg_L2("start refresh admin finish.");
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.CTMLCreateService
 * JD-Core Version:    0.6.0
 */