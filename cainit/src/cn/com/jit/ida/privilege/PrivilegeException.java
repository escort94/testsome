package cn.com.jit.ida.privilege;

import cn.com.jit.ida.IDAException;

public class PrivilegeException extends IDAException
{
  public static final String CA = "80";
  public static final String PRIVILEGE_INITADMIN = "8041";
  public static final String PRIVILEGE_INITADMIN_DES = "初始化管理员";
  public static final String PRIVILEGE_REFRESHADMIN = "8042";
  public static final String PRIVILEGE_REFRESHADMIN_DES = "刷新管理员";
  public static final String PRIVILEGE_SETADMIN = "8043";
  public static final String PRIVILEGE_SETADMIN_DES = "管理员授权";
  public static final String PRIVILEGE_GETADMINROLE = "8044";
  public static final String PRIVILEGE_GETADMINROLE_DES = "获取管理员角色信息";
  public static final String PRIVILEGE_GETADMIN = "8045";
  public static final String PRIVILEGE_GETADMIN_DES = "获取管理员权限点信息";
  public static final String PRIVILEGE_DELADMIN = "8046";
  public static final String PRIVILEGE_DELADMIN_DES = "删除管理员";
  public static final String PRIVILEGE_UPDATEADMIN = "8047";
  public static final String PRIVILEGE_UPDATEADMIN_DES = "更新管理员";
  public static final String PRIVILEGE_ADMINLIST = "8048";
  public static final String PRIVILEGE_ADMINLIST_DES = "获取管理员列表";
  public static final String PRIVILEGE_INITTEMADMIN = "8049";
  public static final String PRIVILEGE_INITTEMADMIN_DES = "初始化证书业务管理员";
  public static final String PRIVILEGE_REFRESHTEMADMIN = "8050";
  public static final String PRIVILEGE_REFRESHTEMADMIN_DES = "刷新证书业务管理员";
  public static final String PRIVILEGE_SETTEMADMIN = "8051";
  public static final String PRIVILEGE_SETTEMADMIN_DES = "证书业务管理员授权";
  public static final String PRIVILEGE_GETTEMADMIN = "8052";
  public static final String PRIVILEGE_GETTEMADMIN_DES = "获取证书业务管理员信息";
  public static final String PRIVILEGE_DELTEMADMIN = "8053";
  public static final String PRIVILEGE_DELTEMADMIN_DES = "删除证书业务管理员";
  public static final String PRIVILEGE_UPDATETEMADMIN = "8054";
  public static final String PRIVILEGE_UPDATETEMADMIN_DES = "更新证书业务管理员";
  public static final String PRIVILEGE_ADMINCERTLIST = "8055";
  public static final String PRIVILEGE_ADMINCERTLIST_DES = "获取管理员证书列表";
  public static final String PRIVILEGE_TEMADMINLIST = "8056";
  public static final String PRIVILEGE_TEMADMINLIST_DES = "获取证书业务管理员列表";
  public static final String PRIVILEGE_INITROLE = "8057";
  public static final String PRIVILEGE_INITROLE_DES = "初始化角色";
  public static final String PRIVILEGE_REFRESHROLE = "8058";
  public static final String PRIVILEGE_REFRESHROLE_DES = "刷新角色";
  public static final String PRIVILEGE_SETROLE = "8059";
  public static final String PRIVILEGE_SETROLE_DES = "设置角色";
  public static final String PRIVILEGE_GETROLE = "8060";
  public static final String PRIVILEGE_GETROLE_DES = "获取角色信息";
  public static final String PRIVILEGE_DELROLE = "8061";
  public static final String PRIVILEGE_DELROLE_DES = "删除角色";
  public static final String PRIVILEGE_ROLELIST = "8062";
  public static final String PRIVILEGE_ROLELIST_DES = "获取角色列表";
  public static final String PRIVILEGE_INITFUNCPOINT = "8063";
  public static final String PRIVILEGE_INITFUNCPOINT_DES = "初始化权限点";
  public static final String PRIVILEGE_REFRESHFUNCPOINT = "8064";
  public static final String PRIVILEGE_REFRESHFUNCPOINT_DES = "刷新权限点";
  public static final String PRIVILEGE_FUNCPOINTLIST = "8065";
  public static final String PRIVILEGE_FUNCPOINTLIST_DES = "获取权限点列表";
  public static final String PRIVILEGE_GETRABASEDN = "8066";
  public static final String PRIVILEGE_GETRABASEDN_DES = "获取RA的BaseDN";
  public static final String PART_ADMIN = "01";
  public static final String PART_TEMADMIN = "02";
  public static final String PART_ROLE = "03";
  public static final String PART_FUNCPOINT = "04";
  public static final String PART_OPR = "05";
  public static final String DB_MANAGER = "80";
  public static final String KEY_MANAGER = "81";
  public static final String PRIVILEGE_MANAGER = "82";
  public static final String CTML_MANAGER = "83";
  public static final String ATTRIBUTE_MANAGER = "84";
  public static final String DBERROR_INIT = "0501";
  public static final String DBERROR_INIT_DES = "初始化数据库失败";
  public static final String DBERROR_EXEC = "0502";
  public static final String DBERROR_EXEC_DES = "调用数据库接口失败";
  public static final String OPTLOG_ERR = "0503";
  public static final String OPTLOG_ERR_DES = "记录业务日志失败";
  public static final String OPTOR_NULL = "0504";
  public static final String OPTOR_NULL_DES = "操作员无效";
  public static final String REQ_FORMAT_ERR = "0505";
  public static final String REQ_FORMAT_ERR_DES = "请求信息格式不合法";
  public static final String REP_FORMAT_ERR = "0506";
  public static final String REP_FORMAT_ERR_DES = "应答信息格式不合法";
  public static final String GETCONFIG_ERR = "0507";
  public static final String GETCONFIG_ERR_DES = "获取配置信息出错";
  public static final String GETCTMLLIST_ERR = "0507";
  public static final String GETCTMLLIST_ERR_DES = "获取模板列表出错";
  public static final String GETSUPERADMINSN_ERR = "0508";
  public static final String GETSUPERADMINSN_ERR_DES = "获取超级管理员序列号失败";
  public static final String GETRACTMLNAME_ERR = "0509";
  public static final String GETRACTMLNAME_ERR_DES = "获取RA管理员模板名称失败";
  public static final String ADMIN_SETFAIL = "0121";
  public static final String ADMIN_SETFAIL_DES = "管理员授权失败";
  public static final String ADMIN_DELFAIL = "0122";
  public static final String ADMIN_DELFAIL_DES = "删除管理员失败";
  public static final String ADMIN_UPDATEFAIL = "0123";
  public static final String ADMIN_UPDATEFAIL_DES = "更新管理员失败";
  public static final String ADMIN_NOTEXIST = "0124";
  public static final String ADMIN_NOTEXIST_DES = "数据库中不存在此管理员";
  public static final String ADMIN_NOADMIN = "0125";
  public static final String ADMIN_NOADMIN_DES = "没有查到此管理员";
  public static final String ADMIN_GETSNINFO = "0126";
  public static final String ADMIN_GETSNINFO_DES = "获取该SN对应证书信息时出错";
  public static final String ADMIN_SNNULL = "0127";
  public static final String ADMIN_SNNULL_DES = "管理员SN为空";
  public static final String ADMIN_SELFERR = "0128";
  public static final String ADMIN_SELFERR_DES = "管理员不能授权自己";
  public static final String ADMIN_NOTSUPER = "0129";
  public static final String ADMIN_NOTSUPER_DES = "不能对超级管理员进行授权";
  public static final String ADMIN_NOTCERT = "0130";
  public static final String ADMIN_NOTCERT_DES = "没有该SN对应的证书";
  public static final String SUPERADMIN_DELFAIL = "0131";
  public static final String SUPERADMIN_DELFAIL_DES = "删除超级管理员对应的证书业务权限出错";
  public static final String TEMADMIN_SETFAIL = "0241";
  public static final String TEMADMIN_SETFAIL_DES = "证书业务管理员授权失败";
  public static final String TEMADMIN_DELFAIL = "0242";
  public static final String TEMADMIN_DELFAIL_DES = "删除证书业务管理员失败";
  public static final String TEMADMIN_UPDATEFAIL = "0243";
  public static final String TEMADMIN_UPDATEFAIL_DES = "更新证书业务管理员失败";
  public static final String TEMADMIN_NOTEXIST = "0244";
  public static final String TEMADMIN_NOTEXIST_DES = "数据库中不存在此证书业务管理员";
  public static final String TEMADMIN_NOADMIN = "0245";
  public static final String TEMADMIN_NOADMIN_DES = "没有查到此证书业务管理员";
  public static final String ADMIN_SNNOTADMIN = "0246";
  public static final String ADMIN_SNNOTADMIN_DES = "该SN对应证书不是管理员证书";
  public static final String TEMADMIN_NOTEXIST2 = "0247";
  public static final String TEMADMIN_NOTEXIST2_DES = "删除的证书业务管理员部分权限不存在于数据库";
  public static final String TEMADMIN_DELFAIL2 = "0248";
  public static final String TEMADMIN_DELFAIL2_DES = "删除证书业务管理员权限出错";
  public static final String ROLE_NOTEXIST = "0361";
  public static final String ROLE_NOTEXIST_DES = "数据库中不存在此角色";
  public static final String ROLE_NOTDEL = "0362";
  public static final String ROLE_NOTDEL_DES = "此角色不允许被删除";
  public static final String ROLE_NOROLE = "0363";
  public static final String ROLE_NOROLE_DES = "没有查到此角色";
  public static final String ROLE_TRANSFAIL = "0364";
  public static final String ROLE_TRANSFAIL_DES = "角色内部转换失败";
  public static final String ROLE_ILLEGAL = "0365";
  public static final String ROLE_ILLEGAL_DES = "存在非法权角色";
  public static final String ROLE_NULL = "0326";
  public static final String ROLE_NULL_DES = "角色集合为空";
  public static final String FUNCPOINT_NULL = "0481";
  public static final String FUNCPOINT_NULL_DES = "权限点集合为空";
  public static final String FUNCPOINT_ILLEGAL = "0482";
  public static final String FUNCPOINT_ILLEGAL_DES = "存在非法权限点";
  public static final String FUNCPOINT_TRANSFAIL = "0483";
  public static final String FUNCPOINT_TRANSFAIL_DES = "权限点内部转换失败";

  public PrivilegeException(String paramString)
  {
    super(paramString);
  }

  public PrivilegeException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }

  public PrivilegeException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }

  public PrivilegeException(String paramString1, String paramString2, Exception paramException)
  {
    super(paramString1, paramString2, paramException);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.PrivilegeException
 * JD-Core Version:    0.6.0
 */