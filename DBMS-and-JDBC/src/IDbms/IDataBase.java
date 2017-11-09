package IDbms;

import java.sql.SQLException;

public interface IDataBase {
	
	public void createTable(String dtbName, String tableName, String columnsNames, String colDataTypes) throws SQLException;
	
	public void dropTable(String dtbName, String tableName) throws SQLException;
	
	public void setDataTypes(String[] s);
	
	public boolean checkDataType(String t);

}
