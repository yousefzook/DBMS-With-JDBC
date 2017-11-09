package jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import dbms.DataBase;
import dbms.Engine;
import jdbc.PrintResultSet;
import json.Json;
import xml.Xml;

public class ResultSetMetaDt implements ResultSetMetaData {

	private Xml xml;
	private Json json;
	private Engine eng;
	private DataBase db;
	private PrintResultSet printResultSet;

	public ResultSetMetaDt() {
		// TODO Auto-generated constructor stub\
		eng = new Engine();
		db = new DataBase();
		if (eng.getDataBaseType().equals("xml")) {
			xml = new Xml();
			xml.readTableData(eng.getLastDataBase() , db.getLastTable());
		} else if (eng.getDataBaseType().equals("json")) {
			json = new Json();
			json.readColumns(eng.getLastDataBase() , db.getLastTable());
		}

		printResultSet = new PrintResultSet();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return printResultSet.getColCount();
	}

	@Override
	public String getColumnLabel(int column) {
		// TODO Auto-generated method stub
		if (eng.getDataBaseType().equals("xml")) {
			return xml.getCol()[column - 1];
		}
		return json.getCol()[column - 1];
	}

	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		if (eng.getDataBaseType().equals("xml")) {
			return xml.getCol()[column - 1];
		}
		return json.getCol()[column - 1];
	}

	@Override
	public int getColumnType(int column) {
		// TODO Auto-generated method stub
		if (eng.getDataBaseType().equals("xml")) {
			if (xml.getInputTypes()[column - 1] == "int") {
				return java.sql.Types.INTEGER;
			} else if (xml.getInputTypes()[column - 1] == "float") {
				return java.sql.Types.FLOAT;
			} else if (xml.getInputTypes()[column - 1] == "varchar") {
				return java.sql.Types.VARCHAR;
			} else {
				return java.sql.Types.DATE;
			}
		} else {
			if (json.getInputTypes()[column - 1] == "int") {
				return java.sql.Types.INTEGER;
			} else if (json.getInputTypes()[column - 1] == "float") {
				return java.sql.Types.FLOAT;
			} else if (json.getInputTypes()[column - 1] == "varchar") {
				return java.sql.Types.VARCHAR;
			} else {
				return java.sql.Types.DATE;
			}
		}
	}

	@Override
	public String getTableName(int column) {
		// TODO Auto-generated method stub
		if (eng.getDataBaseType().equals("xml")) {
			return xml.getCol()[column - 1];
		}
		return json.getCol()[column - 1];
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCatalogName(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnClassName(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColumnDisplaySize(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getColumnTypeName(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPrecision(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getScale(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSchemaName(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAutoIncrement(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCaseSensitive(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCurrency(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDefinitelyWritable(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int isNullable(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isReadOnly(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSearchable(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSigned(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWritable(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
