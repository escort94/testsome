package cn.com.jit.ida.ca.displayUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.security.KeyStoreException;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.init.InitAuditAdminPFX;
import cn.com.jit.ida.ca.init.InitSuperAdminPFX;
import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.ca.key.keyutils.Keytype;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.KeyPairException;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.privilege.Admin;

public class UpdateAdmin {
	private String path;
	private String password;
	private String validityDay;
	CAConfig localCAConfig = null;

	public void updateAdmin() throws Exception {
		if (!isBeenInit()) {
			return;
		}
		int i = 0;
		try {
			while (true) {
				System.out.println("请选择更新管理员类型:");
				System.out
						.println("************************************************");
				System.out.println("   1.  更新超级管理员证书                         ");
				System.out.println("   2.  更新审计管理员证书                         ");
				System.out
						.println("************************************************");
				System.out.print("请选择所需功能(quit退出):");
				BufferedReader inputBuffer = new BufferedReader(
						new InputStreamReader(System.in));
				String str = null;
				try {
					str = inputBuffer.readLine();
				} catch (Exception localException3) {
				}
				if ((str != null) && (str.trim().equalsIgnoreCase("1"))) {
					operateSuperAdmin();
					break;
				}
				if ((str != null) && (str.trim().equalsIgnoreCase("2"))) {
					operateAuditAdmin();
					break;
				}
				if ((str != null) && (str.trim().equalsIgnoreCase("quit")))
					return;
				System.out.println("请按菜单项选择");
				ConfigTool.waitToContinue((BufferedReader) inputBuffer);
			}
		} catch (IDAException localIDAException) {
			System.out.println("更新管理员操作失败:" + localIDAException.toString());
			return;
		}
	}

	public void operateSuperAdmin() throws Exception {
		if (!beforeOperate()) {
			return;
		}
		if (null == path) {
			path = localCAConfig.getAdminKeyStorePath();
		}
		if (this.validityDay == null) {
			// get input pfx's sn and dn ,and update to database.
			GenericKey gKey = new GenericKey(false, path, password
					.toCharArray(), GenericKey.PKCS12);
			gKey.updateAdmin(Admin.SUPER_ADMIN);
		} else {
			// make new pfx,in the future will be alter console input keytype
			// and keysize
			InitSuperAdminPFX makesuperadmin = new InitSuperAdminPFX(new ParseXML("./config/CAConfig.xml"));
			makesuperadmin.getInit().setString("AdminKeyStorePWD", password);
			makesuperadmin.makeSuperAdminPFX(Keytype.SOFT_VALUE, 1024);
		}

	}

	public void operateAuditAdmin() throws Exception {
		if (!beforeOperate()) {
			return;
		}
		if (null == path) {
			path = localCAConfig.getAdminKeyStorePath();
		}
		if (this.validityDay == null) {
			// get input pfx's sn and dn ,and update to database.
			GenericKey gKey = new GenericKey(false, path, password
					.toCharArray(), GenericKey.PKCS12);
			gKey.updateAdmin(Admin.AUDIT_ADMIN);
		} else {
			// make new pfx,in the future will be alter console input keytype
			// and keysize
			InitAuditAdminPFX makeauditdmin = new InitAuditAdminPFX(new ParseXML("./config/CAConfig.xml"));
			makeauditdmin.getInit().setString("AuditAdminKeyStorePath", password);
			makeauditdmin.makeAuditAdminPFX(Keytype.SOFT_VALUE, 1024);
		}
	}


	public boolean beforeOperate() {
		path = ConfigTool.readtoEnd("请输入申请书文件,按Enter键跳过这一步：");
		if (null != path && path.equalsIgnoreCase("quit")) {
			System.out.println("用户放弃");
			return false;
		}
		password = ConfigTool.getPassword("请输入证书密码:", 6, 16);
		if (password == null) {
			return false;
		}
		if (null == path) {
			validityDay = ConfigTool.getInteger("请输入有效期，如365:", 2147483647, 0,
					"天");
			if (validityDay == null) {
				return false;
			}
		}

		if (!ConfigTool.getYesOrNo("确定执行更新管理员操作？[Y/N]", "N")) {
			return false;
		}
		return true;
	}

	/**
	 * check wether have initialization database
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

	public static boolean isBeenInit_noPrint() {
		int i = 0;
		boolean bool = false;
		File localFile = new File("./config/CAConfig.xml");
		bool = localFile.exists();
		return bool;
	}
}
