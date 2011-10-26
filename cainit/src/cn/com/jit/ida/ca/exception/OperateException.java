package cn.com.jit.ida.ca.exception;

import cn.com.jit.ida.IDAException;

public class OperateException extends IDAException{

	public static String INIT_DEMOCA_CER_ERROR = "0001";
	public static String INIT_DEMOCA_CER_ERROR_DES = "生成CER形式根证书失败";
	public static String SAVE_DEMOCA_CER_ERROR = "0002";
	public static String SAVE_DEMOCA_CER_ERROR_DES = "保存CER形式根证书失败";
	public static String SAVE_DEMOCA_CER_PATH_ERROR = "0003";
	public static String SAVE_DEMOCA_CER_PATH_ERROR_DES = "保存CER形式根证书路径发生错误";
	public static String BYTE_DEMOCA_CER_PATH_ERROR = "0004";
	public static String BYTE_DEMOCA_CER_PATH_ERROR_DES = "不能获得CER形式根证书比特形式数据";
	
	public static String UPDATE_ADMIN_ERROR = "0005";
	public static String UPDATE_ADMIN_ERROR_DES = "存储管理员失败";
	public OperateException(String paramString1, String paramString2) {
		super(paramString1, paramString2);
	}

	public OperateException(String paramString1, String paramString2,
			Exception paramException) {
		super(paramString1, paramString2, paramException);
	}
}
