package IDbms;

import java.sql.SQLException;

public interface ITable {

	public void insertIntoTable(String dtbName, String tableName, String columnsNames, String columnsValues) throws SQLException;

	public void selectFromTable(String dtbName, String tableName, String columnsNames, String whereCol, String whereVal,
			String sign) throws SQLException;

	public void deleteFromTable(String dtbName,String c, String tableName, String whereCol, String whereVal,
			String sign) throws SQLException;

	public void updateTable(String dtbName, String tableName, String columnsNames, String columnsValues,
			String whereCol, String whereVal, String sign) throws SQLException;

}
