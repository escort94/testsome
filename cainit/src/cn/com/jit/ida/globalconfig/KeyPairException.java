package cn.com.jit.ida.globalconfig;

import cn.com.jit.ida.IDAException;

public class KeyPairException extends IDAException{
	public static String CREATE_KEYPAIR_ERROR = "0301";
	public static String CREATE_KEYPAIR_ERROR_DES = "生成密钥对失败";
	public static String CREATE_P10_ERROR = "0302";
	public static String CREATE_P10_ERROR_DES = "生成p10请求失败";
	public static String GET_KEYSTORE_ERROR = "0303";
	public static String GET_KEYSTORE_ERROR_DES = "获得KeyStore失败";
	public static String INIT_CER_ERROR = "0304";
	public static String INIT_CER_ERROR_DES = "初始化导入cer格式证书失败";
	public static String DN_NOT_SAME_ERROR = "0305";
	public static String DN_NOT_SAME_ERROR_DES = "导入证书与所申请证书主题不相同";
	public static String INSERT_GEN_CER_ERROR = "0306";
	public static String INSERT_GEN_CER_ERROR_DES = "导入根证书失败";
	public static String CREATE_JKS_ERROR = "0307";
	public static String CREATE_JKS_ERROR_DES = "生成jks证书失败";
	public static String STORE_JKS_ERROR = "0308";
	public static String STORE_JKS_ERROR_DES = "存储jks证书失败";
	public static String SET_ADMIN_AUTHENTICATION_ERROR = "0309";
	public static String SET_ADMIN_AUTHENTICATION_ERROR_DES = "任命管理员的证书处理失败";
	public static String EXPORT_REQ_ERROR = "0310";
	public static String EXPORT_REQ_ERROR_DES = "导出req证书请求失败";
	public static String GET_PRIVATE_KEY_ERROR = "0311";
	public static String GET_PRIVATE_KEY_ERROR_DES = "获取证书私钥失败";
	public static String GET_DN_ERROR = "0312";
	public static String GET_DN_ERROR_DES = "获取证书主题失败";
	public KeyPairException(String paramString) {
		super(paramString);
	}

	public KeyPairException(String paramString, Exception paramException) {
		super(paramString, paramException);
	}

	public KeyPairException(String paramString1, String paramString2) {
		super(paramString1, paramString2);
	}

	public KeyPairException(String paramString1, String paramString2,
			Exception paramException) {
		super(paramString1, paramString2, paramException);
	}
}
