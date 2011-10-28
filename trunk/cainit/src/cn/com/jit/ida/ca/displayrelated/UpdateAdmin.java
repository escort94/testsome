package cn.com.jit.ida.ca.displayrelated;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.security.KeyStoreException;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.displayrelated.initserver.InitAuditAdminPFX;
import cn.com.jit.ida.ca.displayrelated.initserver.InitSuperAdminPFX;
import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.ca.key.keyutils.Keytype;
import cn.com.jit.ida.globalconfig.ConfigFromXML;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.privilege.Admin;

/**
 * 用于更新管理员
 * 密钥算法与密钥长度后期将修改成控制台选择操作
 * @author kmc
 *
 */
public class UpdateAdmin {
	private String path;
	private String password;
	private String validityDay;
	private String dn;
	private String keyAlag;
	private int keySize;

	public void updateAdmin() throws IDAException {
		if (!isBeenInit()) {
			return;
		}
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
				if ((str != null) && (str.trim().equalsIgnoreCase("quit"))){
					return;
				}
				System.out.println("请按菜单项选择");
				ConfigTool.waitToContinue((BufferedReader) inputBuffer);
			}
		} catch (IDAException localIDAException) {
			System.out.println("更新管理员操作失败:" + localIDAException.toString());
			return;
		}
	}

	public void operateSuperAdmin() throws IDAException {
		if (!beforeOperate()) {
			return;
		}
		if (this.validityDay == null) {
			// get input pfx's sn and dn ,and update to database.
			GenericKey gKey = new GenericKey(false, path, password
					.toCharArray(), GenericKey.PKCS12);
			gKey.updateAdmin(Admin.SUPER_ADMIN);
		} else {
			// make new pfx,in the future will be alter console input keytype
			// and keysize
			InitSuperAdminPFX makesuperadmin = new InitSuperAdminPFX(keyAlag, password, path, Integer.parseInt(validityDay));
			makesuperadmin.setDN(dn);
			makesuperadmin.makeSuperAdminPFX(Keytype.SOFT_VALUE, keySize);
		}

	}

	public void operateAuditAdmin() throws IDAException {
		if (!beforeOperate()) {
			return;
		}
		if (this.validityDay == null) {
			// get input pfx's sn and dn ,and update to database.
			GenericKey gKey = new GenericKey(false, path, password
					.toCharArray(), GenericKey.PKCS12);
			gKey.updateAdmin(Admin.AUDIT_ADMIN);
		} else {
			// make new pfx,in the future will be alter console input keytype
			// and keysize
			InitAuditAdminPFX makeauditdmin = new InitAuditAdminPFX(keyAlag, password, path, Integer.parseInt(validityDay));
			makeauditdmin.setDN(dn);
			makeauditdmin.makeAuditAdminPFX(Keytype.SOFT_VALUE, keySize);
		}
	}


	public boolean beforeOperate() throws IDAException {
		path = ConfigTool.getFilePathFromUser("请输入申请书文件,按Enter键跳过这一步：", ConfigTool.FILE_TO_READ);
		if (null != path && path.equalsIgnoreCase("quit")) {
			System.out.println("用户放弃");
			return false;
		}
		if (null == path) {
			path = new ConfigFromXML("CAConfig", "./config/CAConfig.xml").getString("AdminKeyStorePath");
			int j = 0;
			j = ConfigTool.displayMenu(
					"**                     算法                     **", new String[] {
							"RSA(默认)", "SM2" }, 1);
			if (j == 1) {
				keyAlag = "RSA";
				keySize = ConfigTool.displayMenu(
						"**                   密钥长度                 **",
						new String[] { "1024(默认)", "2048" }, 1);
				if (keySize == 0) {
					return false;
				}
				keySize = (int) Math.pow(2.0D, keySize + 9);
			} else if (j == 2) {
				keyAlag = "SM2";
				keySize = ConfigTool.displayMenu(
						"**                   密钥长度                 **",
						new String[] { "256(默认)" }, 1);
				if (keySize == 0) {
					return false;
				}
				keySize = (int) Math.pow(2.0D, keySize + 7);
			} else {
				return false;
			}
			if (keySize < 256)
				return false;
			dn = ConfigTool.getDN("请输入管理员证书DN:");
			validityDay = ConfigTool.getInteger("请输入有效期，如365:", 2147483647, 0,
					"天");
			if (validityDay == null) {
				return false;
			}
		}
		password = ConfigTool.getPassword("请输入证书密码:", 1, 16);
		if (password == null) {
			return false;
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
