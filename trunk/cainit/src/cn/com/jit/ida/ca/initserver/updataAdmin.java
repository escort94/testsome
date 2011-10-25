package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.TemplateAdmin;
import java.io.File;

public class updataAdmin
{
  GlobalConfig globalConfig = null;
  CAConfig caConfig = null;
  char[] pwd;
  String validity;
  String p10;
  String action;
  String adminType;
  public static final String SUPER_ADMIN = "SUPER_ADMIN";
  public static final String AUDIT_ADMIN = "AUDIT_ADMIN";
  public static final String ACTION_NON = "NON";
  public static final String ACTION_ADD_POLICY = "ADD";
  public static final String ACTION_REMOVE_POLICY = "REMOVE";

  public updataAdmin(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    this.pwd = paramString1.toCharArray();
    this.validity = paramString2;
    this.p10 = paramString3;
    this.adminType = paramString4;
    this.action = paramString5;
  }

  public void run()
    throws InitServerException
  {
    try
    {
      this.globalConfig = GlobalConfig.getInstance();
      this.caConfig = this.globalConfig.getCAConfig();
    }
    catch (IDAException localIDAException1)
    {
      new InitServerException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1);
    }
    updataCert localupdataCert = null;
    Object localObject;
    if (this.adminType.equalsIgnoreCase("SUPER_ADMIN"))
    {
      localupdataCert = new updataCert(this.caConfig.getCaAdminSN(), Integer.decode(this.validity).intValue(), this.p10);
      localupdataCert.doUpdata();
      localObject = this.caConfig.getAdminKeyStorePath();
      if ((localObject == null) || (((String)localObject).trim().equals("")))
        localObject = "./keystore/superAdminCert.pfx";
      if (this.p10.equalsIgnoreCase("NULL"))
        localupdataCert.savePKCS12((String)localObject, this.pwd);
      else
        localupdataCert.saveP7B(new File((String)localObject).getParent() + "/superAdmin.p7b");
    }
    else if (this.adminType.equalsIgnoreCase("AUDIT_ADMIN"))
    {
      localupdataCert = new updataCert(this.caConfig.getCAAuditAdminSN(), Integer.decode(this.validity).intValue(), this.p10);
      localupdataCert.doUpdata();
      localObject = this.caConfig.getAuditAdminKeyStorePath();
      if ((localObject == null) || (((String)localObject).trim().equals("")))
        localObject = "./keystore/auditAdminCert.pfx";
      if (this.p10.equalsIgnoreCase("NULL"))
        localupdataCert.savePKCS12((String)localObject, this.pwd);
      else
        localupdataCert.saveP7B(new File((String)localObject).getParent() + "/auditAdmin.p7b");
    }
    try
    {
      localObject = Privilege.getInstance();
      TemplateAdmin localTemplateAdmin = TemplateAdmin.getInstance();
      if (this.action.equalsIgnoreCase("NON"))
        return;
      if (this.action.equalsIgnoreCase("ADD"))
      {
        if (this.adminType.equalsIgnoreCase("SUPER_ADMIN"))
        {
          ((Privilege)localObject).setAuditAdmin(localupdataCert.getNewCertSN(), this.caConfig.getCaAdminDN());
        }
        else
        {
          localObject = Privilege.getInstance();
          ((Privilege)localObject).setSuperAdmin(localupdataCert.getNewCertSN(), this.caConfig.getCAAuditAdminDN());
          localTemplateAdmin.setSuperAdmin(localupdataCert.getNewCertSN());
        }
      }
      else if (this.action.equalsIgnoreCase("REMOVE"))
        if (this.adminType.equalsIgnoreCase("SUPER_ADMIN"))
        {
          localObject = Privilege.getInstance();
          ((Privilege)localObject).delAuditAdmin();
        }
        else
        {
          localObject = Privilege.getInstance();
          ((Privilege)localObject).delSuperAdmin();
        }
    }
    catch (IDAException localIDAException2)
    {
      throw new InitServerException(localIDAException2.getErrCode(), localIDAException2.getErrDesc(), localIDAException2.getHistory());
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.updataAdmin
 * JD-Core Version:    0.6.0
 */