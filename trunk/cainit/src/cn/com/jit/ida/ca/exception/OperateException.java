package cn.com.jit.ida.ca.exception;

import cn.com.jit.ida.IDAException;

public class OperateException extends IDAException {

	public static String INIT_DEMOCA_CER_ERROR = "0001";
	public static String INIT_DEMOCA_CER_ERROR_DES = "生成CER形式根证书失败";
	public static String SAVE_DEMOCA_CER_ERROR = "0002";
	public static String SAVE_DEMOCA_CER_ERROR_DES = "保存CER形式根证书失败";
	public static String SAVE_DEMOCA_CER_PATH_ERROR = "0003";
	public static String SAVE_DEMOCA_CER_PATH_ERROR_DES = "保存CER形式根证书路径发生错误";
	public static String BYTE_DEMOCA_CER_PATH_ERROR = "0004";
	public static String BYTE_DEMOCA_CER_PATH_ERROR_DES = "不能获得CER形式根证书比特形式数据";

	public static String UPDATE_ADMIN_TO_DATABASE_ERROR = "0005";
	public static String UPDATE_ADMIN_TO_DATABASE_ERROR_DES = "将管理员插入数据库失败";
	public static String UPDATE_ADMIN_TO_DATABASE_ROLLBACK_ERROR = "0006";
	public static String UPDATE_ADMIN_TO_DATABASE_ROLLBACK_ERROR_DES = "将管理员插入数据库异常导致回滚操作，但回滚操作失败";

	public static String GET_CERTIFICATECHAIN_FIRST_ERROR = "0007";
	public static String GET_CERTIFICATECHAIN_FIRST_ERROR_DES = "获得证书链中首个证书发生失败";
	
	public static String CERTIFICATE_NULLPOINTER_ERROR = "0008";
	public static String CERTIFICATE_NULLPOINTER_ERROR_DES = "导入证书数据不完备";
	
	public static String DEMOCA_CER_NULL_ERROR = "0009";
	public static String DEMOCA_CER_NULL_ERROR_DES = "根证书文件查找不到";
	
	public static String DEMOCA_FILE_ERROR = "0010";
	public static String DEMOCA_FILE_ERROR_DES = "读取根证书文件发生错误";
	
	public static String INSERT_SYSADMINPWD_ERROR = "0011";
	public static String INSERT_SYSADMINPWD_ERROR_DES = "创建系统管理员密码失败";
	public static String GET_SYSADMINPWD_ERROR = "0012";
	public static String GET_SYSADMINPWD_ERROR_DES = "获得系统管理员密码失败";
	public static String UPDATE_SYSADMINPWD_ERROR = "0013";
	public static String UPDATE_SYSADMINPWD_ERROR_DES = "修改系统管理员密码失败";
	
	public static String SYSADMINPWD_ERROR = "0014";
	public static String SYSADMINPWD_ERROR_DES = "系统管理员密码错误"; 
	
	public static String KEYPAIRTYPE_ERROR = "0015";
	public static String KEYPAIRTYPE_ERROR_DES = "配置文件密钥生成方式配置错误";
	public static String SET_ADMIN_AUTHENTICATION_ERROR = "0016";
	public static String SET_ADMIN_AUTHENTICATION_ERROR_DES = "任命管理员的证书处理失败";
	public static String SAVE_ADMIN_ERROR = "0017";
	public static String SAVE_ADMIN_ERROR_DES = "管理员存储失败";
	public OperateException(String paramString1, String paramString2) {
		super(paramString1, paramString2);
	}

	public OperateException(String paramString1, String paramString2,
			Exception paramException) {
		super(paramString1, paramString2, paramException);
	}
}
