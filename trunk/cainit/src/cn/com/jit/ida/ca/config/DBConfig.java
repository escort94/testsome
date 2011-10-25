package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.globalconfig.ConfigFromXML;

public class DBConfig {
	private static DBConfig instance;
	ConfigFromXML config;
	int maximum_Connection_Count;

	private DBConfig() throws IDAException {
		init();
	}

	public static DBConfig getInstance() throws IDAException {
		if (instance == null)
			instance = new DBConfig();
		return instance;
	}

	public void init() throws IDAException {
		this.config = new ConfigFromXML("DBConfig", "./config/CAConfig.xml");
		this.maximum_Connection_Count = this.config
				.getNumber("Maximum_Connection_Count");
	}

	public String getURL() {
		return this.config.getString("Driver_URL");
	}

	public void setURL(String paramString) throws IDAException {
		this.config.setString("Driver_URL", paramString);
	}

	public String getDriverClass() {
		return this.config.getString("Driver_Class");
	}

	public void setDriverClass(String paramString) throws IDAException {
		this.config.setString("Driver_Class", paramString);
	}

	public String getUser() {
		return this.config.getString("DBUser");
	}

	public void setUser(String paramString) throws IDAException {
		this.config.setString("DBUser", paramString);
	}

	public String getPassword() {
		return this.config.getString("DBUser_PWD");
	}

	public void setPassword(String paramString) throws IDAException {
		this.config.setString("DBUser_PWD", paramString);
	}

	public int getMaxConnCount() {
		return this.maximum_Connection_Count;
	}

	public void setMaxConnCount(int paramInt) throws IDAException {
		this.config.setNumber("Maximum_Connection_Count", paramInt);
		this.maximum_Connection_Count = paramInt;
	}

	public String getTestSQL() {
		return this.config.getString("Keeping_Test_SQL");
	}

	public void setTestSQL(String paramString) throws IDAException {
		this.config.setString("Keeping_Test_SQL", paramString);
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.config.DBConfig JD-Core Version: 0.6.0
 */