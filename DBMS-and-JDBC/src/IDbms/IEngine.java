package IDbms;

import java.sql.SQLException;

public interface IEngine {
	
	public void createDataBase(String dtbName) throws SQLException;
	
	public void dropDataBase(String dtbName);

}
