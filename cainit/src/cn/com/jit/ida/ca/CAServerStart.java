package cn.com.jit.ida.ca;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.display.DisplayInitServer;
import cn.com.jit.ida.ca.display.DisplayLaterCommServerJKS;
import cn.com.jit.ida.ca.display.DisplaySubCAAll;
import cn.com.jit.ida.ca.display.DisplayUpdateAdmin;
import cn.com.jit.ida.ca.display.DisplayUpdateCommjks;
import cn.com.jit.ida.ca.display.DisplayUpdateSigningJKS;
import cn.com.jit.ida.ca.display.DisplayWebServer;
import cn.com.jit.ida.ca.displayrelated.DbUtils;
import cn.com.jit.ida.ca.exception.OperateException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;

public class CAServerStart {
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
	 * 某些功能在操作之前需要验证操作人员的身份 通过输入系统管理员密码完成验证
	 * 
	 * @return
	 */
	public boolean checkOperateIdentity() {
		String pwd = ConfigTool.getPassword("请输入系统管理员密码", 6, 16);
		LogManager.init();
		SysLogger logger = LogManager.getSysLogger();
		if (pwd == null) {
			logger.info("用户取消");
			return false;
		}
		try {
			String dbpwd = DbUtils.getSysPwd();
			if (pwd.equals(dbpwd)) {
				return true;
			}
		} catch (OperateException e) {
			logger.info("验证系统管理员密码发生异常");
		}
		logger.info("系统管理员密码错误");
		return false;
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
			System.out.println("   2.  停止服务器                              ");
			System.out.println("   3.  子CA身份的根证书申请                  ");
			System.out.println("   4.  初始化系统                              ");
			System.out.println("   5.  更新管理员证书                         ");
			System.out.println("   6.  更新服务器通信证书                        ");
			System.out.println("   7.  导入服务器通信证书根证书                     ");
			System.out.println("   8.  修改系统管理员密码                              ");
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
				if (isBeenInit() && checkOperateIdentity()) {// start server
					DisplayWebServer dws = new DisplayWebServer();
					dws.start();
				}
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("2")) {
				if (isBeenInit() && checkOperateIdentity()) {// stop server
					DisplayWebServer dws = new DisplayWebServer();
					dws.stop();
				}
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("3")) {// apply cert
					DisplaySubCAAll dca = new DisplaySubCAAll();
					dca.operate();
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("4")) {// init server
				DisplayInitServer dis = new DisplayInitServer();
				dis.operate();
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("5")) {// update admin cert
				// maybe need increase auth password
				if (isBeenInit() && checkOperateIdentity()) {
					DisplayUpdateAdmin dua = new DisplayUpdateAdmin();
					dua.operate();
				}
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("6")) {// update server comm cert
				if (isBeenInit()) {
					DisplayUpdateCommjks dcj = new DisplayUpdateCommjks();
					dcj.operate();
				}
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("7")) {// comm server jks import
				// cert or democa
				if (isBeenInit()) {
					DisplayLaterCommServerJKS dlcj = new DisplayLaterCommServerJKS();
					dlcj.operate();
				}
				ConfigTool.waitToContinue(localBufferedReader);
			}else if (str.equalsIgnoreCase("8")) {// 
				if (isBeenInit()) {
					LogManager.init();
					SysLogger logger = LogManager.getSysLogger();
					try {
						if(DbUtils.updateSysAdminPwd()){
							logger.info("修改系统管理员密码成功");
						}
					} catch (IDAException e) {
						logger.info(e.getMessage());
					}
				}
				ConfigTool.waitToContinue(localBufferedReader);
			}  else {
				if (str.equalsIgnoreCase("0"))
					return;
				System.out.println("请按菜单项选择");
				ConfigTool.waitToContinue(localBufferedReader);
			}
		}
	}

}
