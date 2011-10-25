package cn.com.jit.ida.ca.db;

import cn.com.jit.ida.IDAException;

public class DBException extends IDAException
{
  public static final String DB = "80";
  public static final String DB_2 = "86";
  public static final String DBException = "数据库异常：";
  public static final String DB_CONFIG = "8001";
  public static final String DB_CONFIG_DES = "数据库连接信息不正确";
  public static final String INIT_POOL_CLASS = "8002";
  public static final String INIT_POOL_CLASS_DES = "连接池类找不到";
  public static final String INIT_POOL = "8003";
  public static final String INIT_POOL_DES = "连接池初始化失败";
  public static final String SAVE_CTML = "8004";
  public static final String SAVE_CTML_DES = "增加模板失败";
  public static final String SAVE_CTML_X509_DES = "调用X509转换失败";
  public static final String SAVE_CERT_REQ = "8005";
  public static final String SAVE_CERT_REQ_DES = "证书申请失败";
  public static final String SAVE_CERT_ENTITY = "8006";
  public static final String SAVE_CERT_ENTITY_DES = "保存证书实体失败";
  public static final String SAVE_SELFEXT = "8007";
  public static final String SAVE_SELFEXT_DES = "保存自定义扩展失败";
  public static final String GET_CTML = "8008";
  public static final String GET_CTML_DES = "获取模板失败";
  public static final String REFRESH_CERT_ACCOUNT = "8009";
  public static final String REFRESH_CERT_ACCOUNT_DES = "获取证书总数失败";
  public static final String GET_CERT_ENTITY = "8010";
  public static final String GET_CERT_ENTITY_DES = "获取证书实体失败";
  public static final String GET_CERT_REQ_INFO = "8011";
  public static final String GET_CERT_REQ_INFO_DES = "获取证书申请信息失败";
  public static final String GET_CERT_INFO_SN = "8012";
  public static final String GET_CERT_INFO_SN_DES = "按序列号获取证书信息失败";
  public static final String GET_CERT_INFO_DN = "8013";
  public static final String GET_CERT_INFO_DN_DES = "按主题获取证书信息失败";
  public static final String GET_CERT_INFO_ALL = "8014";
  public static final String GET_CERT_INFO_ALL_DES = "获取证书信息失败";
  public static final String GET_SELFEXT = "8015";
  public static final String GET_SELFEXT_DES = "获取自定义扩展失败";
  public static final String MODITY_CTML = "8016";
  public static final String MODITY_CTML_DES = "修改模板失败";
  public static final String MODIFY_SELFEXT = "8017";
  public static final String MODIFY_SELFEXT_DES = "修改自定义扩展失败";
  public static final String HOLD_CERT = "8018";
  public static final String HOLD_CERT_DES = "冻结证书失败";
  public static final String UNHOLD_CERT = "8019";
  public static final String UNHOLD_CERT_DES = "解冻证书失败";
  public static final String SET_CONFIG = "8020";
  public static final String SET_CONFIG_DES = "保存配置信息失败";
  public static final String GET_CONFIG = "8021";
  public static final String GET_CONFIG_DES = "获取配置信息失败";
  public static final String SET_ROLE_INFO = "8022";
  public static final String SET_ROLE_INFO_DES = "保存角色信息失败";
  public static final String SET_ADMIN_INFO = "8023";
  public static final String SET_ADMIN_INFO_DES = "设置管理员角色失败";
  public static final String SET_TEMPLATE_ADMIN = "8024";
  public static final String SET_TEMPLATE_ADMIN_DES = "设置业务管理员权限失败";
  public static final String GET_ROLES_INFO = "8025";
  public static final String GET_ROLES_INFO_DES = "获取角色信息失败";
  public static final String GET_ADMIN_INFO = "8026";
  public static final String GET_ADMIN_INFO_DES = "获取管理员信息失败";
  public static final String GET_FUNCTION_POINTS_INFO = "8027";
  public static final String GET_FUNCTION_POINTS_INFO_DES = "获取权限点信息失败";
  public static final String GET_TEMPLATE_ADMINS_INFO = "8028";
  public static final String GET_TEMPLATE_ADMINS_INFO_DES = "获取业务管理员信息失败";
  public static final String GET_ROLES_RELATION_INFO = "8029";
  public static final String GET_ROLES_RELATION_INFO_DES = "获取角色关系信息失败";
  public static final String UPDATE_AUTH_CODE = "8030";
  public static final String UPDATE_AUTH_CODE_DES = "更新授权码失败";
  public static final String UPDATE_ADMIN = "8031";
  public static final String UPDATE_ADMIN_DES = "更新管理员证书失败";
  public static final String UPDATE_TEMPLATE_ADMIN = "8032";
  public static final String UPDATE_TEMPLATE_ADMIN_DES = "更新业务管理员证书失败";
  public static final String ROLLBACK_CERT_DOWNLOAD = "8033";
  public static final String ROLLBACK_CERT_DOWNLOAD_DES = "保存待处理证书失败";
  public static final String UPDATE_CERT = "8034";
  public static final String UPDATE_CERT_DES = "更新证书失败";
  public static final String REQ_AND_DOWN_CERT = "8035";
  public static final String REQ_AND_DOWN_CERT_DES = "申请并下载证书失败";
  public static final String UPD_AND_DOWN_CERT = "8036";
  public static final String UPD_AND_DOWN_CERT_DES = "更新并下载证书失败";
  public static final String DELETE_ROLE = "8037";
  public static final String DELETE_ROLE_DES = "删除角色失败";
  public static final String DELETE_SELFEXT = "8038";
  public static final String DELETE_SELFEXT_DES = "删除自定义扩展";
  public static final String DELETE_ADMIN = "8039";
  public static final String DELETE_ADMIN_DES = "删除管理员失败";
  public static final String DELETE_TEMPLATE_ADMIN = "8040";
  public static final String DELETE_TEMPLATE_ADMIN_DES = "删除业务管理员失败";
  public static final String DELETE_CTML = "8041";
  public static final String DELETE_CTML_DES = "删除模板失败";
  public static final String DELETE_CTML_CHILD = "8042";
  public static final String DELETE_CTML_CHILD_DES = "删除模板失败：模板已被使用";
  public static final String REVOKE_CERT = "8043";
  public static final String REVOKE_CERT_DES = "注销证书失败";
  public static final String UPD_AND_DOWN_ADMIN_CERT = "8044";
  public static final String UPD_AND_DOWN_ADMIN_CERT_DES = "更新并下载管理员证书失败";
  public static final String UPDATE_ADMIN_CERT = "8045";
  public static final String UPDATE_ADMIN_CERT_DES = "更新管理员证书失败";
  public static final String SET_TEMPLATE_ADMINS = "8046";
  public static final String SET_TEMPLATE_ADMINS_DES = "设置业务管理员权限失败";
  public static final String REVOKE_ROOT_CERT = "8047";
  public static final String REVOKE_ROOT_CERT_DES = "注销证书失败";
  public static final String GET_CERT_EXTENSIONS = "8048";
  public static final String GET_CERT_EXTENSIONS_DES = "获取证书扩展失败";
  public static final String DELETE_SELFEXT_CHILD = "8049";
  public static final String DELETE_SELFEXT_CHILD_DES = "删除自定义扩展失败：此记录仍被使用";
  public static final String GET_OPERATIONLOG_INFO = "8050";
  public static final String GET_OPERATIONLOG_INFO_DES = "获取操作日志信息失败";
  public static final String GET_CERT_STATISTIC = "8051";
  public static final String GET_CERT_STATISTIC_DES = "获取证书统计信息失败";
  public static final String DB_CONFIG_URL = "8052";
  public static final String DB_CONFIG_URL_DES = "连接数据库的URL不正确";
  public static final String GET_MAX_CDPID = "8053";
  public static final String GET_MAX_CDPID_DES = "获取最大的CDPID失败";
  public static final String DELETE_CERT_TBP = "8054";
  public static final String DELETE_CERT_TBP_DES = "删除待发布证书失败";
  public static final String SAVE_OPERATIONLOG = "8055";
  public static final String SAVE_OPERATIONLOG_DES = "保存操作日志失败";
  public static final String GET_CERT_TBP_INFO = "8056";
  public static final String GET_CERT_TBP_INFO_DES = "获取待发布证书信息失败";
  public static final String SET_TEMPLATE_ADMIN_ARRAY_LENGTH = "8057";
  public static final String SET_TEMPLATE_ADMIN_ARRAY_LENGTH_DES = "设置业务管理员权限失败:模板和主题数量不相等";
  public static final String GET_REVOKED_CERTS = "8058";
  public static final String GET_REVOKED_CERTS_DES = "获取注销证书失败";
  public static final String CHECK_CERT_REQ = "8059";
  public static final String CHECK_CERT_REQ_DES = "检查证书申请失败";
  public static final String GET_HOLD_CERT_INFO_DN = "8060";
  public static final String GET_HOLD_CERT_INFO_DN_DES = "获取冻结证书失败";
  public static final String GET_UNRVK_CERT_INFO_DN = "8061";
  public static final String GET_UNRVK_CERT_INFO_DN_DES = "获取未注销证书失败";
  public static final String CONNECT_FAIL = "8062";
  public static final String CONNECT_FAIL_DES = "连接数据库失败";
  public static final String INTERNAL_CONFIG = "8063";
  public static final String INTERNAL_CONFIG_DES = "获取全局配置信息失败";
  public static final String GET_RA_BASEDN = "8064";
  public static final String GET_RA_BASEDN_DES = "获取RA管理员的BaseDN失败";
  public static final String UPDATE_RA_BASEDN = "8064";
  public static final String UPDATE_RA_BASEDN_DES = "修改RA管理员的BaseDN失败";
  public static final String GET_CRL_INFO = "8065";
  public static final String GET_CRL_INFO_DES = "获取证书注销列表信息失败";
  public static final String DELETE_CRL = "8066";
  public static final String DELETE_CRL_DES = "删除证书注销列表失败";
  public static final String INSERT_BATCH_CRL = "8066";
  public static final String INSERT_BATCH_CRL_DES = "批量增加证书注销列表失败";
  public static final String GET_CERTSTANDARDEXT = "8067";
  public static final String GET_CERTSTANDARDEXT_DES = "获取证书标准扩展信息失败";
  public static final String GET_CERT_COUNT_BY_CTMLNAMECERTSTATUS = "8068";
  public static final String GET_CERT_COUNT_BY_CTMLNAMECERTSTATUS_DES = "依据模板获取证书总数失败";
  public static final String GET_CERTS_BY_CTMLNAMECERTSTATUS = "8069";
  public static final String GET_CERTS_BY_CTMLNAMECERTSTATUS_DES = "依据模板获取证书失败";
  public static final String GET_ADMIN_DN = "8070";
  public static final String GET_ADMIN_DN_DES = "获取DN信息失败";
  public static final String MOVE_LOG_TO_ARC = "8071";
  public static final String MOVE_LOG_TO_ARC_DES = "归档操作日志失败";
  public static final String MOVE_CERT_TO_ARC = "8072";
  public static final String MOVE_CERT_TO_ARC_DES = "归档证书失败";
  public static final String GET_CERTARC_INFO_SN = "8073";
  public static final String GET_CERTARC_INFO_SN_DES = "按序列号获取归档证书信息失败";
  public static final String GET_CERTARC_INFO_DN = "8074";
  public static final String GET_CERTARC_INFO_DN_DES = "按主题获取归档证书信息失败";
  public static final String GET_CERTARC_INFO_ALL = "8075";
  public static final String GET_CERTARC_INFO_ALL_DES = "获取归档证书信息失败";
  public static final String GET_OPERATIONLOGARC_INFO = "8076";
  public static final String GET_OPERATIONLOGARC_INFO_DES = "获取归档操作日志信息失败";
  public static final String GET_CERT_INFO_FOR_ISNOTWAITING = "8077";
  public static final String GET_CERT_INFO_FOR_ISNOTWAITING_DES = "获得非待操作状态证书信息失败";
  public static final String GET_CERTARCFORKMC = "8078";
  public static final String GET_CERTARCFORKMC_DES = "获取已过期加密证书的CERTSN信息失败";
  public static final String DELETE_CERTARCFORKMC = "8079";
  public static final String DELETE_CERTARCFORKMC_DES = "删除已过期加密证书信息表失败";
  public static final String INSERT_CERTARCFORKMC = "8601";
  public static final String INSERT_CERTARCFORKMC_DES = "增加已过期加密证书信息表失败";
  public static final String DELETE_PENDINGTASK = "8602";
  public static final String DELETE_PENDINGTASK_DES = "删除非待操作状态证书信息失败";
  public static final String DELETE_CERT = "8603";
  public static final String DELETE_CERT_DES = "删除状态证书信息失败";
  public static final String INSERT_ADMIN = "8604";
  public static final String INSERT_ADMIN_DES = "增加管理员证书失败";
  public static final String INSERT_TEMPLATE_ADMIN = "8605";
  public static final String INSERT_TEMPLATE_ADMIN_DES = "增加业务管理员证书失败";
  public static final String GET_CERT_COUNT_BY_ISVALID = "8606";
  public static final String GET_CERT_COUNT_BY_ISVALID_DES = "依据证书是否有效获取证书总数失败";
  public static final String REVOKE_PENDING_TASK_CERT = "8607";
  public static final String REVOKE_PENDING_TASK_CERT_DES = "注销待操作任务表对应的证书失败";
  public static final String MODIFY_CERT_PENDING_STATUS = "8608";
  public static final String MODIFY_CERT_PENDING_STATUS_DES = "更改证书待处理状态失败";
  public static final String ADD_PENDINGTASK = "8609";
  public static final String ADD_PENDINGTASK_DES = "增加证书待处理表失败";
  public static final String GET_PENDINGTASK = "8610";
  public static final String GET_PENDINGTASK_DES = "获取证书待处理表失败";
  public static final String DELETE_REVOKEDCERT = "8611";
  public static final String DELETE_REVOKEDCERT_DES = "删除已过期注销证书表失败";
  public static final String DB2_GET_DB_VERSION = "DB2数据库版本是:";
  public static final String MYSQL_GET_DB_VERSION = "MySQL数据库版本是:";
  public static final String GET_DB_VERSION_FAIL = "返回数据库版本失败:数据库访问异常";
  public static final String DB2_TABLESPACE_PAGESIZE = "默认表空间页大小不够";
  public static final String DB_NOT_SUPPORT = "本系统目前不支持此数据库类型或配置文件中数据库连接配置的URL不正确";
  public static final String DB_NOT_SUPPORT_CONVERT = "下面将按标准的SQL来处理，但无法保证所有功能能正常使用。或者修改成支持的URL再重新初始化。";
  public static final String INIT_GET_INSTANCE = "8080";
  public static final String CREATE_DB = "8081";
  public static final String SAVE_CA_CERT = "8082";
  public static final String GET_CA_CERT = "8083";
  public static final String INIT_SET_CONFIG = "8084";
  public static final String INIT_PRIVILEGE = "8085";
  public static final String CREATE_DB_FILE_NOT_FOUND = "8086";
  public static final String CREATE_DB_FILE_NOT_FOUND_DES = "下列文件不存在：";
  public static final String INIT_ROLE = "8087";
  public static final String INIT_CLASS = "8088";
  public static final String INIT_CLASS_DES = "数据库驱动类找不到";
  public static final String INIT_CONFIG = "8089";
  public static final String INIT_CONFIG_DES = "获取配置信息失败";
  public static final String FILE_IO_FAIL_DES = "建库脚本文件读取失败";
  public static final String CA_USERNAME = "CAUSERNAME";
  public static final String CA_PASSWORD = "CAPASSWORD";

  public DBException(String paramString)
  {
    super(paramString);
  }

  public DBException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }

  public DBException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }

  public DBException(String paramString1, String paramString2, Exception paramException)
  {
    super(paramString1, paramString2, paramException);
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.db.DBException
 * JD-Core Version:    0.6.0
 */