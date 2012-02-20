package makejks;

import java.io.InputStream;

import org.apache.commons.digester.Digester;

public class DbConfigRegister {
	public DBConfig dbconfig;
	
	public static void main(String[] args) {
		DbConfigRegister dfg = new DbConfigRegister();
		dfg.getConfig();
	}
	public DBConfig getConfig(){
		Digester digester = new Digester();
        digester.setValidating(false);
        digester.push(this);
        digester.addObjectCreate("Config/DBConfig",
        		DBConfig.class);
        digester.addBeanPropertySetter(
                "Config/DBConfig/Url", "url");
        digester.addBeanPropertySetter(
                "Config/DBConfig/DriverClass", "driverClass");
        digester.addBeanPropertySetter(
                "Config/DBConfig/Username", "username");
        digester.addBeanPropertySetter(
                "Config/DBConfig/Password", "pasword");
        digester.addSetNext("Config/DBConfig",
        "setDbconfig");
        // 解析配置文件
        try {
            ClassLoader loader = DBConfig.class.getClassLoader();
            InputStream inputStream = loader
                    .getResourceAsStream("dbconfig.xml");
            digester.parse(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDbconfig();
	}
	
	public DBConfig getDbconfig() {
		return dbconfig;
	}
	public void setDbconfig(DBConfig dbconfig) {
		this.dbconfig = dbconfig;
	}
}
