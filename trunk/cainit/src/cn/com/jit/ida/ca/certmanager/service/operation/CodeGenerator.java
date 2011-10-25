package cn.com.jit.ida.ca.certmanager.service.operation;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.globalconfig.ConfigFromXML;
import java.io.PrintStream;

public class CodeGenerator {
	private static final char[] REF_SRC = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	private static final char[] AUTH_SRC = { '2', '3', '4', '5', '6', '7', '8',
			'9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'L', 'M', 'N',
			'Q', 'R', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	public static final int DEFAULT_CODE_LEN = 16;

	public static String generateRefCode() {
		InternalConfig localInternalConfig = null;
		ConfigFromXML localConfigFromXML = null;
		int i = 0;
		if (CAConfig.isInit())
			try {
				i = CAConfig.getInstance().getCertSNLen();
			} catch (IDAException localIDAException) {
				i = 16;
			}
		else
			try {
				localInternalConfig = InternalConfig.getInstance();
				localConfigFromXML = new ConfigFromXML("CAConfig",
						"./config/CAConfig.xml");
				i = localConfigFromXML.getNumber("CertSNLength");
			} catch (Exception localException) {
				i = 16;
			}
		if (i == 0)
			i = 16;
		if (i > 40)
			i = 40;
		int j = 0;
		StringBuffer localStringBuffer = new StringBuffer();
		if (j == 0) {
			int k = REF_SRC.length;
			for (int m = 0; m < i; m++) {
				int n = (int) (Math.random() * k);
				if ((m == 0) && ((n > 7) || (n == 0)))
					m--;
				else
					localStringBuffer.append(REF_SRC[n]);
			}
		}
		return localStringBuffer.toString();
	}

	public static String generateAuthCode() {
		char[] arrayOfChar = null;
		int i = 0;
		InternalConfig localInternalConfig = null;
		CAConfig localCAConfig = null;
		try {
			localInternalConfig = InternalConfig.getInstance();
			String str = localInternalConfig.getAuthCharSet();
			localCAConfig = CAConfig.getInstance();
			i = localCAConfig.getAuthCodeLength();
			if ((str == null) || (str.trim().equals("")))
				throw new Exception();
			arrayOfChar = str.toCharArray();
		} catch (Exception localException) {
			arrayOfChar = AUTH_SRC;
		}
		if (i == 0)
			i = 16;
		if (i > 40)
			i = 40;
		StringBuffer localStringBuffer = new StringBuffer();
		int j = arrayOfChar.length;
		for (int k = 0; k < i; k++) {
			int m = (int) (Math.random() * j);
			localStringBuffer.append(arrayOfChar[m]);
		}
		return localStringBuffer.toString();
	}

	public static String[] generateCodes() {
		String[] arrayOfString = new String[2];
		String str1 = generateRefCode();
		String str2 = generateAuthCode();
		arrayOfString[0] = str1;
		arrayOfString[1] = str2;
		return arrayOfString;
	}

	public static void main(String[] paramArrayOfString) {
		for (int i = 0; i < 100; i++) {
			String str = generateRefCode();
			System.out.println(str);
			if ((!str.startsWith("0"))
					&& (Integer.parseInt(str.substring(0, 1)) <= 7))
				continue;
			System.out.println("ALERT");
			System.out.println(str);
		}
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.certmanager.service.operation.CodeGenerator JD-Core
 * Version: 0.6.0
 */