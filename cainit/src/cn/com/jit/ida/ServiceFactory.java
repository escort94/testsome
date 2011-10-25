package cn.com.jit.ida;

import cn.com.jit.ida.ca.audit.service.AduitCertService;
import cn.com.jit.ida.ca.audit.service.AuditOptLogService;
import cn.com.jit.ida.ca.certmanager.service.AuthCodeUpdate;
import cn.com.jit.ida.ca.certmanager.service.CRLQuery;
import cn.com.jit.ida.ca.certmanager.service.CertArcSearch;
import cn.com.jit.ida.ca.certmanager.service.CertArchive;
import cn.com.jit.ida.ca.certmanager.service.CertDownload;
import cn.com.jit.ida.ca.certmanager.service.CertEntityQuery;
import cn.com.jit.ida.ca.certmanager.service.CertHold;
import cn.com.jit.ida.ca.certmanager.service.CertQuery;
import cn.com.jit.ida.ca.certmanager.service.CertReqDown;
import cn.com.jit.ida.ca.certmanager.service.CertRequest;
import cn.com.jit.ida.ca.certmanager.service.CertRevoke;
import cn.com.jit.ida.ca.certmanager.service.CertUnHold;
import cn.com.jit.ida.ca.certmanager.service.CertUpdDown;
import cn.com.jit.ida.ca.certmanager.service.CertUpdate;
import cn.com.jit.ida.ca.certmanager.service.CertUpdateQuery;
import cn.com.jit.ida.ca.certmanager.service.CertUpdateRetainKey;
import cn.com.jit.ida.ca.ctml.service.CTMLCreateService;
import cn.com.jit.ida.ca.ctml.service.CTMLDeleteService;
import cn.com.jit.ida.ca.ctml.service.CTMLGetService;
import cn.com.jit.ida.ca.ctml.service.CTMLModifyService;
import cn.com.jit.ida.ca.ctml.service.CTMLRevokeService;
import cn.com.jit.ida.ca.ctml.service.CTMLSelfExtCreateService;
import cn.com.jit.ida.ca.ctml.service.CTMLSelfExtDeleteService;
import cn.com.jit.ida.ca.ctml.service.CTMLSelfExtGetService;
import cn.com.jit.ida.ca.ctml.service.CTMLSelfExtModifyService;
import cn.com.jit.ida.ca.ctml.service.CTMLSelfExtRevokeService;
import cn.com.jit.ida.ca.ctml.service.RACTMLUpdateService;
import cn.com.jit.ida.ca.log.service.LogArchive;
import cn.com.jit.ida.ca.log.service.OptLogSearch;
import cn.com.jit.ida.privilege.service.GetAdminListService;
import cn.com.jit.ida.privilege.service.GetAdminRoleService;
import cn.com.jit.ida.privilege.service.GetRoleListService;
import cn.com.jit.ida.privilege.service.GetTemAdminService;
import cn.com.jit.ida.privilege.service.SetAdminService;
import cn.com.jit.ida.privilege.service.SetTemplateAdminService;

public class ServiceFactory
{
  public static Service newServiceInstance(String paramString)
    throws IDAException
  {
    if (paramString.equals("CERTREQUST"))
      return new CertRequest();
    if (paramString.equals("CERTREVOKE"))
      return new CertRevoke();
    if (paramString.equals("CERTQUERY"))
      return new CertQuery();
    if (paramString.equals("CERTHOLD"))
      return new CertHold();
    if (paramString.equals("CERTUNHOLD"))
      return new CertUnHold();
    if (paramString.equals("CERTDOWNLOAD"))
      return new CertDownload();
    if (paramString.equals("CERTREQDOWN"))
      return new CertReqDown();
    if (paramString.equals("CERTUPDATE"))
      return new CertUpdate();
    if (paramString.equals("CERTUPDDOWN"))
      return new CertUpdDown();
    if (paramString.equals("AUTHCODEUPDATE"))
      return new AuthCodeUpdate();
    if (paramString.equals("CERTENTITYQUERY"))
      return new CertEntityQuery();
    if (paramString.equals("CERTUPDATERETAINKEY"))
      return new CertUpdateRetainKey();
    if (paramString.equals("CERTUPDATEQUERY"))
      return new CertUpdateQuery();
    if (paramString.equals("CTMLCREATE"))
      return new CTMLCreateService();
    if (paramString.equals("CTMLDELETE"))
      return new CTMLDeleteService();
    if (paramString.equals("CTMLREVOKE"))
      return new CTMLRevokeService();
    if (paramString.equals("CTMLGET"))
      return new CTMLGetService();
    if (paramString.equals("CTMLMODIFY"))
      return new CTMLModifyService();
    if (paramString.equals("CTMLSELFEXTCREATE"))
      return new CTMLSelfExtCreateService();
    if (paramString.equals("CTMLSELFEXTDELETE"))
      return new CTMLSelfExtDeleteService();
    if (paramString.equals("CTMLSELFEXTREVOKE"))
      return new CTMLSelfExtRevokeService();
    if (paramString.equals("CTMLSELFEXTGET"))
      return new CTMLSelfExtGetService();
    if (paramString.equals("CTMLSELFEXTMODIFY"))
      return new CTMLSelfExtModifyService();
    if (paramString.equals("RACTMLUPDATE"))
      return new RACTMLUpdateService();
    if (paramString.equals("PRIVILEGEGETADMINLIST"))
      return new GetAdminListService();
    if (paramString.equals("PRIVILEGEGETADMINROLES"))
      return new GetAdminRoleService();
    if (paramString.equals("PRIVILEGEGETROLELIST"))
      return new GetRoleListService();
    if (paramString.equals("PRIVILEGEGETTEMPLATEADMIN"))
      return new GetTemAdminService();
    if (paramString.equals("PRIVILEGESETADMIN"))
      return new SetAdminService();
    if (paramString.equals("PRIVILEGESETTEMPLATEADMIN"))
      return new SetTemplateAdminService();
    if (paramString.equals("AUDITCOUNTCERT"))
      return new AduitCertService();
    if (paramString.equals("AUDITQUERYLOG"))
      return new AuditOptLogService();
    if (paramString.equals("AUDITARCHIVELOG"))
      return new LogArchive();
    if (paramString.equals("AUDITQUERYARCHIVELOG"))
      return new OptLogSearch();
    if (paramString.equals("SUPERARCHIVECERT"))
      return new CertArchive();
    if (paramString.equals("SUPERQUERYARCHIVECERT"))
      return new CertArcSearch();
    if (paramString.equals("CRLQUERY"))
      return new CRLQuery();
    throw new IDAException("错误码", "错误描述");
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ServiceFactory
 * JD-Core Version:    0.6.0
 */