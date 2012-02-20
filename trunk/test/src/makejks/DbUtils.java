package makejks;

import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sun.misc.BASE64Encoder;

public class DbUtils {
	
	public String url, driverclass,username, password;
	
	public DbUtils(){
		this.init();
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		DbUtils db = new DbUtils();
		System.out.println(db.getMaxId());
	}
	public void init(){
		DbConfigRegister dfg = new DbConfigRegister();
		DBConfig dbconfig = dfg.getConfig();
		this.url = dbconfig.getUrl();
		this.driverclass = dbconfig.getDriverClass();
		this.username = dbconfig.getUsername();
		this.password = dbconfig.getPasword();
	}
	
	public Connection getConnection() throws ClassNotFoundException, SQLException{
		Class.forName("oracle.jdbc.driver.OracleDriver");
		return DriverManager.getConnection(url, username, password);
	}
	
	public void insertData(String certsn, String dn, Certificate cert, int stauts) throws ClassNotFoundException, SQLException, CertificateEncodingException{
		Connection conn = this.getConnection();
		String sql = "insert into tca_ra_cert (CERT_SN, RA_ID, DN, CERT , STATUS)values (?, ?, ?, ?, ?)";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setString(1, certsn);
		statement.setInt(2, 102);
		statement.setString(3, dn);
		statement.setString(4, new BASE64Encoder().encode(cert.getEncoded()));
		statement.setInt(5, stauts);
		statement.execute();
		this.close(conn, statement);
	}
	public int getMaxId() throws ClassNotFoundException, SQLException{
		int result = 0;
		Connection conn = this.getConnection();
		String sql = "select max(ra_id) from tca_ra_cert";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.execute();
		ResultSet set = statement.getResultSet();
		while(set.next()){
			result = set.getInt(1);
		}
		this.close(conn, statement);
		return result;
	}
	public void close(Connection conn, Statement ment){
		this.close(conn);
		this.close(ment);
	}
	public void close(Connection conn){
		if(null != conn){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public void close(Statement ment){
		if(null != ment){
			try {
				ment.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
