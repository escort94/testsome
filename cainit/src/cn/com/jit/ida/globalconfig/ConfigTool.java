package cn.com.jit.ida.globalconfig;

import cn.com.jit.ida.ca.certmanager.service.request.ReqCheck;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.StringTokenizer;

public class ConfigTool {
	static String COMMINUTER_1 = ";";
	static String COMMINUTER_2 = "||";
	public static int FILE_TO_READ = 1;
	public static int FILE_TO_WRITE = 2;

	public static String Array2String(String[][] paramArrayOfString) {
		int i = paramArrayOfString.length;
		if (i == 0)
			return "";
		int j = paramArrayOfString[0].length;
		if (j == 0)
			return "";
		StringBuffer localStringBuffer = new StringBuffer(i * j + i * 2 - 1);
		for (int k = 0; k < i; k++) {
			for (int m = 0; m < j; m++) {
				localStringBuffer.append(paramArrayOfString[k][m]);
				if (m == j - 1)
					continue;
				localStringBuffer.append(COMMINUTER_1);
			}
			if (k == i - 1)
				continue;
			localStringBuffer.append(COMMINUTER_2);
		}
		return localStringBuffer.toString();
	}

	public static String[][] String2Array(String paramString) {
		StringTokenizer localStringTokenizer1 = new StringTokenizer(
				paramString, COMMINUTER_2);
		int i = localStringTokenizer1.countTokens();
		String str = localStringTokenizer1.nextToken();
		StringTokenizer localStringTokenizer2 = new StringTokenizer(str,
				COMMINUTER_1);
		int j = localStringTokenizer2.countTokens();
		String[][] arrayOfString = new String[i][j];
		for (int k = 0; k < i - 1; k++) {
			for (int m = 0; m < j; m++)
				arrayOfString[k][m] = localStringTokenizer2.nextToken();
			str = localStringTokenizer1.nextToken();
			localStringTokenizer2 = new StringTokenizer(str, COMMINUTER_1);
		}
		for (int k = 0; k < j; k++)
			arrayOfString[(i - 1)][k] = localStringTokenizer2.nextToken();
		return arrayOfString;
	}

	public static String Array2String(String[] paramArrayOfString) {
		if (paramArrayOfString.length == 0)
			return "";
		StringBuffer localStringBuffer = new StringBuffer(
				paramArrayOfString.length * 2 - 1);
		for (int i = 0; i < paramArrayOfString.length; i++) {
			localStringBuffer.append(paramArrayOfString[i]);
			if (i >= paramArrayOfString.length - 1)
				continue;
			localStringBuffer.append(COMMINUTER_1);
		}
		return localStringBuffer.toString();
	}

	public static String[] SampleString2Array(String paramString) {
		StringTokenizer localStringTokenizer = new StringTokenizer(paramString,
				COMMINUTER_1);
		int i = localStringTokenizer.countTokens();
		String[] arrayOfString = new String[i];
		for (int j = 0; j < i; j++)
			arrayOfString[j] = localStringTokenizer.nextToken();
		return arrayOfString;
	}

	public static String getFilePathFromUser(String paramString1, int paramInt,
			String paramString2) {
		BufferedReader localBufferedReader = new BufferedReader(
				new InputStreamReader(System.in));
		String str = null;
		File localFile;
		do {
			do
				while (true) {
					System.out.print(paramString1 + "（quit退出）");
					try {
						str = localBufferedReader.readLine().trim();
						if ((str == null) || (str.equals("")))
							str = paramString2;
					} catch (IOException localIOException) {
					}
					if (str == null) {
						cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
						return null;
					}
					if (str.equalsIgnoreCase("")) {
						cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
						return null;
					}
					if (str.equalsIgnoreCase("quit")) {
						cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
						return null;
					}
					if (paramInt != FILE_TO_READ)
						break;
					localFile = new File(str);
					if (!localFile.exists()) {
						System.out.println("文件" + str + "找不到");
						continue;
					}
					if (!localFile.isFile()) {
						System.out.println(str + "不是文件");
						continue;
					}
					return localFile.getAbsolutePath();
				}
			while (paramInt != FILE_TO_WRITE);
			localFile = new File(str);
		} while ((localFile.exists())
				&& (!getYesOrNo("文件" + str + "已经存在，是否覆盖？[Y/N](默认:[Y]覆盖)", "Y")));
		try {
			new File(localFile.getParent()).mkdirs();
		} catch (Exception localException) {
		}
		return str;
	}

	public static String getFilePathFromUser(String paramString, int paramInt) {
		BufferedReader localBufferedReader = new BufferedReader(
				new InputStreamReader(System.in));
		String str = null;
		File localFile;
		do {
			do
				while (true) {
					System.out.print(paramString + "（quit退出）");
					try {
						str = localBufferedReader.readLine().trim();
					} catch (IOException localIOException) {
					}
					if (str == null) {
						cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
						return null;
					}
					if (str.equalsIgnoreCase("")) {
						cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
						return null;
					}
					if (str.equalsIgnoreCase("quit")) {
						cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
						return "quit";
					}
					if (paramInt != FILE_TO_READ)
						break;
					localFile = new File(str);
					if (!localFile.exists()) {
						System.out.println("文件" + str + "找不到");
						continue;
					}
					if (!localFile.isFile()) {
						System.out.println(str + "不是文件");
						continue;
					}
					return localFile.getAbsolutePath();
				}
			while (paramInt != FILE_TO_WRITE);
			localFile = new File(str);
		} while ((localFile.exists())
				&& (!getYesOrNo("文件" + str + "已经存在，是否覆盖？[Y/N](默认:[Y]覆盖)", "Y")));
		try {
			new File(localFile.getParent()).mkdirs();
		} catch (Exception localException) {
		}
		return localFile.getAbsolutePath();
	}

	public static void waitToContinue(BufferedReader paramBufferedReader) {
		System.out.print("请按回车（Enter）键继续……");
		try {
			paramBufferedReader.read();
		} catch (IOException localIOException) {
		}
	}

	public static void waitToContinue() {
		BufferedReader localBufferedReader = new BufferedReader(
				new InputStreamReader(System.in));
		System.out.print("请按回车（Enter）键继续……");
		try {
			localBufferedReader.read();
		} catch (IOException localIOException) {
		}
	}

	public static boolean getYesOrNo(String paramString1, String paramString2) {
		BufferedReader localBufferedReader = new BufferedReader(
				new InputStreamReader(System.in));
		System.out.print(paramString1);
		try {
			String str = localBufferedReader.readLine().trim();
			if (str.equalsIgnoreCase("Y"))
				return true;
			if (str.equals("")) {
				if (paramString2.equalsIgnoreCase("Y"))
					return true;
				cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
				return false;
			}
			cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
			return false;
		} catch (IOException localIOException) {
			cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
		}
		return false;
	}

	public static boolean getYesOrNo(String paramString) {
		BufferedReader localBufferedReader = new BufferedReader(
				new InputStreamReader(System.in));
		System.out.print(paramString);
		try {
			return localBufferedReader.readLine().trim().equalsIgnoreCase("Y");
		} catch (IOException localIOException) {
		}
		return false;
	}

	public static String getInteger(String paramString1, int paramInt1,
			int paramInt2, String paramString2) {
		BufferedReader localBufferedReader = new BufferedReader(
				new InputStreamReader(System.in));
		String str = null;
		while (true) {
			System.out.print(paramString1 + "(quit退出):");
			try {
				str = localBufferedReader.readLine().trim();
			} catch (IOException localIOException) {
			}
			if (str.equalsIgnoreCase("quit")) {
				System.out.println("用户取消操作");
				cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
				return null;
			}
			int i = 0;
			try {
				i = Integer.decode(str).intValue();
			} catch (NumberFormatException localNumberFormatException) {
				System.out.println("格式错误,请输入整数.");
			}
			if (i > paramInt1) {
				System.out.println("大于可用的最大值" + String.valueOf(paramInt1)
						+ paramString2 + ".");
				continue;
			}
			if (i >= paramInt2)
				break;
			System.out.println("小于可用的最小值" + String.valueOf(paramInt2)
					+ paramString2 + ".");
		}
		return str;
	}

	private static native String getStr();

	public static String getNewPassword(String paramString, int paramInt1,
			int paramInt2, boolean syspwd) {
		String str1 = null;
		String str2 = null;
		while (true) {
			if (syspwd) {
				str1 = getPasswordSys(paramString, paramInt1, paramInt2);
			} else {
				str1 = getPassword(paramString, paramInt1, paramInt2);
			}
			if (str1 == null)
				return str1;
			if (syspwd) {
				str2 = getPasswordSys("请重新输入", paramInt1, paramInt2);
			} else {
				str2 = getPassword("请重新输入", paramInt1, paramInt2);
			}
			if (str2 == null)
				return str2;
			if (str1.equals(str2))
				return str1;
			System.out.println("输入的密码不一致，请重新输入。");
		}
	}

	public static String getNewPassword(String paramString, int paramInt1,
			int paramInt2) {
		return getNewPassword(paramString, paramInt1, paramInt2, false);
	}
	public static String getPasswordSys(String paramString, int paramInt1,
			int paramInt2) {
		File localFile = new File("./show");
		if (paramInt1 < 1)
			paramInt1 = 1;
		BufferedReader localBufferedReader = new BufferedReader(
				new InputStreamReader(System.in));
		String str = null;
		while (true) {
			System.out.print(paramString + "(必填项):");
			if (localFile.exists()) {
				try {
					str = localBufferedReader.readLine().trim();
				} catch (IOException localIOException) {
				}
				continue;
			} else {
				// str = getStr();
				try {
					str = localBufferedReader.readLine().trim();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (str.length() > paramInt2) {
				System.out.println("密码长度不能大于" + paramInt2 + "位");
				continue;
			}
			if ((str == null) || (str.equals(""))) {
				System.out.println("密码不能为空");
				continue;
			}
			if (str.length() >= paramInt1)
				break;
			System.out.println("密码长度不能小于" + paramInt1 + "位");
		}
		return str;
	}
	public static String getPassword(String paramString, int paramInt1,
			int paramInt2) {
		File localFile = new File("./show");
		if (paramInt1 < 1)
			paramInt1 = 1;
		BufferedReader localBufferedReader = new BufferedReader(
				new InputStreamReader(System.in));
		String str = null;
		while (true) {
			System.out.print(paramString + "(quit退出):");
			if (localFile.exists()) {
				try {
					str = localBufferedReader.readLine().trim();
				} catch (IOException localIOException) {
				}
				continue;
			} else {
				// str = getStr();
				try {
					str = localBufferedReader.readLine().trim();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (str.equalsIgnoreCase("quit")) {
				cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
				return null;
			}
			if (str.length() > paramInt2) {
				System.out.println("密码长度不能大于" + paramInt2 + "位");
				continue;
			}
			if ((str == null) || (str.equals(""))) {
				System.out.println("密码不能为空");
				continue;
			}
			if (str.length() >= paramInt1)
				break;
			System.out.println("密码长度不能小于" + paramInt1 + "位");
		}
		return str;
	}

	public static String readtoEnd(String paramString) {
		String str1 = getFilePathFromUser(paramString, FILE_TO_READ);
		if (str1 == null)
			return null;
		if (str1.equalsIgnoreCase("quit"))
			return "quit";
		FileInputStream localFileInputStream = null;
		try {
			localFileInputStream = new FileInputStream(str1);
		} catch (FileNotFoundException localFileNotFoundException) {
			return null;
		}
		BufferedReader localBufferedReader = new BufferedReader(
				new InputStreamReader(localFileInputStream));
		char[] arrayOfChar = new char[1024];
		int i = 0;
		try {
			i = localBufferedReader.read(arrayOfChar);
		} catch (IOException localIOException1) {
			return null;
		}
		String str2 = "";
		while (i > 0) {
			str2 = str2.concat(new String(arrayOfChar).substring(0, i));
			if (i < 1024)
				try {
					Thread.sleep(20L);
				} catch (InterruptedException localInterruptedException) {
				}
			try {
				i = localBufferedReader.read(arrayOfChar);
			} catch (IOException localIOException2) {
				return null;
			}
		}
		return str2;
	}

	public static int displayMenu(String paramString,
			String[] paramArrayOfString) {
		int i = 0;
		while (true) {
			if (paramString != null) {
				System.out
						.println("************************************************");
				System.out.println(paramString);
			}
			System.out
					.println("************************************************");
			for (int j = 0; j < paramArrayOfString.length; j++)
				System.out.println(Integer.toString(j + 1) + ". "
						+ paramArrayOfString[j]);
			System.out.println("0. 退出");
			System.out
					.println("************************************************");
			System.out.print("请选择:");
			BufferedReader localBufferedReader = new BufferedReader(
					new InputStreamReader(System.in));
			String str = null;
			try {
				str = localBufferedReader.readLine().trim();
			} catch (IOException localIOException) {
				return 0;
			}
			try {
				i = Integer.parseInt(str);
			} catch (NumberFormatException localNumberFormatException) {
				System.out.println("请输入数字。");
				waitToContinue();
			}
			if ((i <= paramArrayOfString.length) && (i >= 0))
				break;
			System.out.println("请按菜单选择。");
			waitToContinue();
		}
		return i;
	}

	public static int displayMenu(String paramString,
			String[] paramArrayOfString, int paramInt) {
		int i = 0;
		while (true) {
			if (paramString != null) {
				System.out
						.println("************************************************");
				System.out.println(paramString);
			}
			System.out
					.println("************************************************");
			for (int j = 0; j < paramArrayOfString.length; j++)
				System.out.println(Integer.toString(j + 1) + ". "
						+ paramArrayOfString[j]);
			System.out.println("0. 退出");
			System.out
					.println("************************************************");
			System.out.print("请选择(直接回车使用默认值):");
			BufferedReader localBufferedReader = new BufferedReader(
					new InputStreamReader(System.in));
			String str = null;
			try {
				str = localBufferedReader.readLine().trim();
			} catch (IOException localIOException) {
				return 0;
			}
			if ((str == null) || (str.equals(""))) {
				i = paramInt;
				break;
			}
			try {
				i = Integer.parseInt(str);
			} catch (NumberFormatException localNumberFormatException) {
				System.out.println("请输入数字。");
				waitToContinue();
			}
			if ((i <= paramArrayOfString.length) && (i >= 0))
				break;
			System.out.println("请按菜单选择。");
			waitToContinue();
		}
		return i;
	}

	public static String getDN(String paramString) {
		BufferedReader localBufferedReader = new BufferedReader(
				new InputStreamReader(System.in));
		String str = null;
		while (true) {
			System.out.print(paramString + "(quit退出):");
			try {
				str = localBufferedReader.readLine().trim();
			} catch (IOException localIOException) {
			}
			if (str.equalsIgnoreCase("quit")) {
				System.out.println("用户取消操作");
				return null;
			}
			boolean bool = ReqCheck.checkDN(str);
			if (bool)
				break;
			System.out.println("DN格式不合法，请重新输入.");
		}
		return formatDN(str);
	}

	public static String formatDN(String paramString) {
		if ((paramString == null) || (paramString.trim().equals("")))
			return "";
		StringTokenizer localStringTokenizer = new StringTokenizer(paramString,
				",");
		String str1 = null;
		while (localStringTokenizer.hasMoreTokens()) {
			String str2 = localStringTokenizer.nextToken();
			if (str1 == null) {
				str1 = new String(str2.trim());
				continue;
			}
			str1 = str1 + "," + str2.trim();
		}
		return str1;
	}

	static {
		System.loadLibrary("str");
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.globalconfig.ConfigTool JD-Core Version: 0.6.0
 */