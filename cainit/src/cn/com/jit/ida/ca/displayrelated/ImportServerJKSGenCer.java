package cn.com.jit.ida.ca.displayrelated;

import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.KeyPairException;
import cn.com.jit.ida.globalconfig.ParseXML;

public class ImportServerJKSGenCer {
	private ParseXML caconfig;

	public ImportServerJKSGenCer() throws ConfigException {
		caconfig = new ParseXML("./config/CAConfig.xml");
	}

	public ImportServerJKSGenCer(ParseXML parseXml) {
		this.caconfig = parseXml;
	}
	
	public void importGenCer() throws ConfigException, KeyPairException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException{
		String path = caconfig.getString("CommKeyStore");
		String password = caconfig.getString("CommKeyStorePWD");
		GenericKey gKey = new GenericKey(false, path, password.toCharArray(), GenericKey.JKS);
		Key key = gKey.m_keyStore.getKey("s1as", password.toCharArray());
		gKey.addDemoCA(key, password.toCharArray());
	}
}
