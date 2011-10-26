package cn.com.jit.ida.ca;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.com.jit.ida.ca.config.CAConfigConstant;
import cn.com.jit.ida.ca.control.ControlServer;
import cn.com.jit.ida.ca.display.DisplayInitServer;
import cn.com.jit.ida.globalconfig.ConfigTool;

public class CAServerStart {
	StartServer startServer;
	ControlServer controlServer;
	public static final int Function_CANCEL = 0;
	public static final int Function_RETURN = 1;
	public static final String KEEP = "keep";
	public static final String REMOVE_SUPER = "removeSuper";
	public static final String REMOVE_AUDIT = "removeAudit";
	public static final String ADD_SUPER = "addSuper";
	public static final String ADD_AUDIT = "addAudit";

	/**
	 * 主函数
	 * 
	 * @param paramArrayOfString
	 */
	public static void main(String[] paramArrayOfString) {
		try {
			CAServerStart localCAServer = new CAServerStart();
			localCAServer.displayMemu();
		} catch (Exception localException) {
			System.out.println("发生系统错误，错误如下：");
			localException.printStackTrace();
		}
	}

	/**
	 * 检验是否含有CAConfig.xml配置文件
	 * 
	 * @return
	 */
	public static boolean isBeenInit_noPrint() {
		int i = 0;
		boolean bool = false;
		File localFile = new File("./config/CAConfig.xml");
		bool = localFile.exists();
		return bool;
	}

	/**
	 * 通过检验是否含有CAConfig.xml配置文件判断是否已经初始化
	 * 
	 * @return
	 */
	public static boolean isBeenInit() {
		boolean bool = false;
		bool = isBeenInit_noPrint();
		if (!bool)
			System.out.println("系统没有被初始化，或者无法连接数据库。");
		return bool;
	}

	/**
	 * 某些功能在操作之前需要验证操作人员的身份 通过输入服务器证书或者其他密码来验证
	 * 
	 * @return
	 */
	public boolean checkOperateIdentity() {
		/**
		 * 暂时未实现 后期补充
		 */
		return true;
	}

	/**
	 * 控制台主函数调用方法
	 * 
	 * @param paramCAServer
	 */
	public void displayMemu() {
		while (true) {
			System.out
					.println("************************************************");
			System.out
					.println("**            AISINO CAServer v1.0            **");
			System.out
					.println("************************************************");
			System.out.println("   1.  启动服务器                              ");
			System.out.println("   3.  停止服务器                              ");
			System.out.println("   4.  产生申请书                              ");
			System.out.println("   5.  初始化系统                              ");
			System.out.println("   6.  更新管理员证书                         ");
			System.out.println("   7.  更新服务器通信证书                        ");
			System.out.println("   8.  更新根证书(子CA更新,请使用 4 和 12 操作)     ");
			System.out.println("  11.  显示版本号                              ");
			System.out.println("  12.  导入证书                                ");
			System.out.println("  13.  导入KMC证书                             ");
			System.out.println("   0.  退出                                   ");
			System.out
					.println("************************************************");
			System.out.print("请选择所需功能:");
			BufferedReader localBufferedReader = new BufferedReader(
					new InputStreamReader(System.in));
			String str = null;
			try {
				str = localBufferedReader.readLine().trim();
			} catch (IOException localIOException) {
				return;
			}
			if (str.equalsIgnoreCase("1")) {
				if (isBeenInit() && checkOperateIdentity()) {//start server

				}
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("3")) {
				if (checkOperateIdentity()) {//stop server

				}
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("4")) {//apply  cert

				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("5")) {//init server
				DisplayInitServer dis = new DisplayInitServer();
				dis.operate();
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("6")) {//update admin cert
				if (checkOperateIdentity()) {

				}
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("7")) {// update server comm cert
				if (isBeenInit() && checkOperateIdentity()) {

				}
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("8")) {// update gen key cert
				if (isBeenInit() && checkOperateIdentity()) {

				}
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("11")) {//display version

				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("12")) {// import cert
				if (isBeenInit() && checkOperateIdentity()) {

				}
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("13")) {// import kmc cert
				if (isBeenInit() && checkOperateIdentity()) {

				}
				ConfigTool.waitToContinue(localBufferedReader);
			} else {
				if (str.equalsIgnoreCase("0"))
					return;
				System.out.println("请按菜单项选择");
				ConfigTool.waitToContinue(localBufferedReader);
				continue;
			}
			if (CAConfigConstant.isUserCancel.equals("0")) {
				FinalizeCryptoDevice localFinalizeCryptoDevice = new FinalizeCryptoDevice();
				localFinalizeCryptoDevice.doFinalize();
				continue;
			}
			CAConfigConstant.isUserCancel = "0";
		}
	}

}
