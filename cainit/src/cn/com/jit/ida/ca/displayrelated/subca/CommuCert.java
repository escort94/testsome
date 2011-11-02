package cn.com.jit.ida.ca.displayrelated.subca;

import java.util.Vector;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.ParseXML;

public class CommuCert {
	private String commKeyStore = null;
	private String commKeyStorePWD = null;
	private static CommuCert instance;
	private GenericKey gKey;

	public CommuCert() throws IDAException {
		init();
	}

	public static CommuCert getInstance() throws IDAException {
		if (instance == null)
			instance = new CommuCert();
		return instance;
	}

	public void init() throws IDAException {
		// if (InitCommuCert.SUB_CA_JKS.equals(InitCommuCert.identity)) {
		// this.commKeyStore = "./keystore/subCAKeystore.jks";
		// }else if (InitCommuCert.COMM_JKS.equals(InitCommuCert.identity)){
		// this.commKeyStore = "./keystore/commCert.jks";
		// }
		// this.commKeyStore = "./keystore/commCert.jks";
		this.commKeyStore = new ParseXML("./config/CAConfig.xml")
				.getString("CommKeyStore");
		if (this.commKeyStorePWD == null) {
			commKeyStorePWD = ConfigTool.getPassword("请输入通信证书密码", 0, 16);
			if (commKeyStorePWD == null)
				throw new IDAException("3002", "用户取消操作");
		}
		try {
			gKey = new GenericKey(false, this.commKeyStore,
					this.commKeyStorePWD.toCharArray(), GenericKey.JKS);
		} catch (ConfigException localConfigException) {
			throw localConfigException;
		}
		Vector localVector = gKey.get_KeyStruct();
		if (localVector.size() == 0) {
			throw new IDAException("2601", "通信证书KeyStore错误");
		}
	}

	public static void clear() {
		instance = null;
	}

	public String getCommKeyStore() {
		return this.commKeyStore;
	}

	public GenericKey getGKey() {
		return gKey;
	}

	public String getCommKeyStorePWD() {
		return commKeyStorePWD;
	}

	public void setCommKeyStorePWD(String commKeyStorePWD) {
		this.commKeyStorePWD = commKeyStorePWD;
	}

	public void setCommKeyStore(String commKeyStore) {
		this.commKeyStore = commKeyStore;
	}
}