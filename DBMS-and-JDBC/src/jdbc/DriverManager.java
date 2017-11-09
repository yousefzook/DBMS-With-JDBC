package jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import dbms.Engine;

public class DriverManager implements Driver {

	private String userName, pass;
	private Connection con;
	private Engine e = new Engine();

	public void setConnectionToNull() {
		this.con = null;
	}

	@Override
	public boolean acceptsURL(String url) {
		// TODO Auto-generated method stub
		if (url.matches("jdbc:(altdb|xmldb|jsondb)://\\w+"))
			return true;
		return false;
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		// TODO Auto-generated method stub
		if (!acceptsURL(url.toLowerCase()))
			return null;
		this.userName = (String) info.get("userName");
		this.pass = (String) info.get("password");
		e.setPath(info.get("path").toString());
//		System.out.println(e.getDrPath());
		e.setDataBaseType(url.split(":")[1]);
		con = new ConnectionClass();
		return con;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
		// TODO Auto-generated method stub
		String[] urlParts = url.split(":");
		if (!acceptsURL(url))
			return null;
		
		DriverPropertyInfo[] infoArr = new DriverPropertyInfo[2];
		infoArr[0] = new DriverPropertyInfo("userName", info.getProperty("userName"));
		infoArr[1] = new DriverPropertyInfo("pass", info.getProperty("password"));
		return infoArr;
	}

	@Override
	public int getMajorVersion() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMinorVersion() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean jdbcCompliant() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
