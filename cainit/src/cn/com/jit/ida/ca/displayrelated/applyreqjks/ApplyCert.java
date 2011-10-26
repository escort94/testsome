package cn.com.jit.ida.ca.displayrelated.applyreqjks;

import java.sql.SQLException;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.ParseXML;

public class ApplyCert {
	private ParseXML caconfig;

	public ApplyCert() throws ConfigException {
		caconfig = new ParseXML("./config/CAConfig.xml");
	}

	public ApplyCert(ParseXML parseXml) {
		this.caconfig = parseXml;
	}
	public int dealAuthCert() {
		int i = 0;
		int j = ConfigTool.displayMenu(null, new String[] { "产生授权证书申请书",
				"产生更新授权证书的申请书", "导入授权证书", "导入根证书" });
		if (j == 0) {
			System.out.println("用户取消");
			return 0;
		}
		InitAuthCert localInitAuthCert;
		if ((j == 1) || (j == 2)) {
			localInitAuthCert = new InitAuthCert();
			if (j == 1) {
				try {
					i = localInitAuthCert.generalReq();
				} catch (IDAException e) {
					i = -1;
					System.out.println(e.getErrCode() + e.getErrDesc());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				try {
					i = localInitAuthCert.generalReqForUpdataSM2();
				} catch (IDAException e) {
					i = -1;
					System.out.println(e.getErrCode() + e.getErrDesc());
				}
			}
			if (i == 1) {
				System.out.println("产生授权证书申请书成功");
			} else if (i == -1) {
				System.out.println("产生授权证书申请书失败");
			} else {
				System.out.println("用户取消");
			}
		} else if (j == 3) {
			localInitAuthCert = new InitAuthCert();
			try {
				i = localInitAuthCert.importCertFromFileSM2(false);
			} catch (IDAException e) {
				i = -1;
				System.out.println(e.getErrCode() + e.getErrDesc());
			}
			if (i == 1) {
				System.out.println("导入授权证书成功");
			} else if (i == -1) {
				System.out.println("导入授权证书失败");
			} else {
				System.out.println("用户取消");
			}
		} else if (j == 4) {
//			try {
//				localInitAuthCert = new InitAuthCert();
//				i = localInitAuthCert.importGenCer();
//			} catch (IDAException e) {
//				i = -1;
//				System.out.println(e.getErrCode() + e.getErrDesc());
//			}
//			if (i == 1) {
//				System.out.println("导入根证书成功");
//			} else if (i == -1) {
//				System.out.println("导入根证书失败");
//			} else {
//				System.out.println("用户放弃");
//			}
		}
		return i;
	}
}
