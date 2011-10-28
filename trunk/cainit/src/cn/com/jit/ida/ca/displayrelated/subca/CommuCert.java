package cn.com.jit.ida.ca.displayrelated.subca;

import java.util.Vector;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigFromXML;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cipher.JKey;

public class CommuCert {
	private ConfigFromXML config;
	private String commKeyStore = null;
	private String commKeyStorePWD = null;
	private X509Cert cert;
	private JKey priKey;
	private JKey pubKey;
	private Vector trustCerts;
	private String deviceId = null;
	private int hardKeyId = -1;
	private static CommuCert instance;
	private GenericKey gKey ;
	
	public CommuCert() throws IDAException {
		init();
	}

	public static CommuCert getInstance() throws IDAException {
		if (instance == null)
			instance = new CommuCert();
		return instance;
	}

	public void init() throws IDAException {
		this.config = new ConfigFromXML("keyStoreConfig",
				"./config/KMCConfig.xml");
		this.commKeyStore = this.config.getString("CommKeyStore");
		if (this.commKeyStorePWD == null) {
			String str = this.config.getString("CommKeyStorePWD");
			if (str.equalsIgnoreCase("")) {
				str = ConfigTool.getPassword("请输入通信证书密码", 0, 16);
				if (str == null)
					throw new IDAException("3002", "用户取消操作");
			}
			this.commKeyStorePWD = str;
		}
		try {
			gKey = new GenericKey(false, this.commKeyStore,
					this.commKeyStorePWD.toCharArray(), GenericKey.JKS);
		} catch (ConfigException localConfigException) {
			throw localConfigException;
		}
		Vector localVector = gKey.get_KeyStruct();
		if (localVector.size() == 0){
			throw new IDAException("2601", "通信证书KeyStore错误");
		}
		  this.trustCerts = gKey.getM_TrustCerts();
	}

	public JKey getPubKey() {
		return this.pubKey;
	}

	public X509Cert getCert() {
		return this.cert;
	}

	public JKey getPriKey() {
		return this.priKey;
	}

	public static void clear() {
		instance = null;
	}

	public Vector getTrustCerts() {
		return this.trustCerts;
	}

	public String getCommKeyStore() {
		return this.commKeyStore;
	}

	public void setCommKeyStore(String paramString) throws IDAException {
		this.config.setString("CommKeyStore", paramString);
		this.commKeyStore = paramString;
	}

	public String getCommKeyStorePWD() {
		return this.commKeyStorePWD;
	}

	public void setCommKeyStorePWD(String paramString) throws IDAException {
		this.config.setString("CommKeyStore", this.commKeyStore);
		this.commKeyStorePWD = paramString;
	}

	public GenericKey getGKey() {
		return gKey;
	}

	public void setGKey(GenericKey key) {
		gKey = key;
	}
}