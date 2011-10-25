package cn.com.jit.ida.globalconfig;

import cn.com.jit.ida.IDAException;

public class ConfigException extends IDAException {
	public static String DECRYPTO_DATA_ERROR = "0904";
	public static String ENCRYPTO_DATA_ERROR = "0905";
	public static String NOKEY_IN_KEYSTORE = "0907";
	public static String PARAM_ERROR = "0913";
	public static String CONFIG_ERROR = "0900";

	public static String CONFIG_ERROR_DES = "配置错误";
	public static String READ_FILE_ERROR = "0901";
	public static String READ_FILE_ERROR_DES = "读配置错误";
	public static String WRITE_FILE_ERROR = "0902";
	public static String WRITE_FILE_ERROR_DES = "写文件错误";
	public static String DATA_TYPE_ERROR = "0903";
	public static String DATA_TYPE_ERROR_DES = "数据类型错误";
	public static String CONFIG_STRUCT_ERROR = "0904";
	public static String CONFIG_STRUCT_ERROR_DES = "配置文件结构错误";
	public static String NOCERT_IN_KEYSTORE = "0905";
	public static String NOCERT_IN_KEYSTORE_DES = "证书存储中没有需要的证书";
	public static String ENCODE_ERROR = "0906";
	public static String ENCODE_ERROR_DES = "证书编码错误";
	public static String DECODE_ERROR = "0907";
	public static String DECODE_ERROR_DES = "证书码错误";
	public static String ADDCERT_ERROR = "0908";
	public static String ADDCERT_ERROR_DES = "添加证书失败";
	public static String ADDKEY_ERROR = "0909";
	public static String ADDKEY_ERROR_DES = "导入密钥错误";
	public static String KEY_STORE_PWD_ERROR = "0910";
	public static String KEY_STORE_PWD_ERROR_DES = "证书密码错误";
	public static String KEY_PAIR_CANOT_MATCHING_ERROR = "0911";
	public static String KEY_PAIR_CANOT_MATCHING_ERROR_DES = "密钥对不匹配，无法导入证书";
	public static String KEY_STORE_ERROR = "0912";
	public static String KEY_STORE_ERROR_DES = "证书存储错误";

	public ConfigException(String paramString) {
		super(paramString);
	}

	public ConfigException(String paramString, Exception paramException) {
		super(paramString, paramException);
	}

	public ConfigException(String paramString1, String paramString2) {
		super(paramString1, paramString2);
	}

	public ConfigException(String paramString1, String paramString2,
			Exception paramException) {
		super(paramString1, paramString2, paramException);
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.globalconfig.ConfigException JD-Core Version: 0.6.0
 */