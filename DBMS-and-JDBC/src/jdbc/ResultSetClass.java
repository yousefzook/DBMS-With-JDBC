package jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import dbms.DataBase;
import dbms.Engine;
import jdbc.PrintResultSet;
import json.Json;
import xml.Xml;

public class ResultSetClass implements ResultSet {

	private int pointer;
	private static int index = 0;
	private String[] colomnNames, colomnTypes;
	private List<List<String>> data;
	private Json json = new Json();
	private Xml xml = new Xml();
	private Engine engine;
	private DataBase dataBase;
	private PrintResultSet printResultSet;

	public ResultSetClass() {
		// TODO Auto-generated constructor stub
		printResultSet = new PrintResultSet();
		engine = new Engine();
		dataBase = new DataBase();
		if (engine.getDataBaseType().equals("xml")) {
			xml.readTableData(engine.getLastDataBase(), dataBase.getLastTable());
			colomnTypes = xml.getInputTypes();
			// index = xml.getIndex();
		} else if (engine.getDataBaseType().equals("json")) {
			json.readColumns(engine.getLastDataBase(), dataBase.getLastTable());
			colomnTypes = json.getInputTypes();
			// index = json.getIndex();
		}
		colomnNames = printResultSet.getHeaders();
		data = printResultSet.getSelected();
		pointer = -1;
	}

	@Override
	public boolean absolute(int row) {
		row = Math.abs(row);
		if (data.size() == 0 || row > data.size() || row == 0) {
			return false;
		}
		pointer = row;

		return true;
	}

	@Override
	public void afterLast() {
		pointer = data.size();
	}

	@Override
	public void beforeFirst() {
		try {
			pointer = -1;
		} catch (Exception e) {

		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	@Override
	public int findColumn(String columnLabel) {
		for (int i = 0; i < colomnNames.length; i++) {
			if (columnLabel.equals(colomnNames[i])) {
				return i + 1;
			}
		}
		return -1;
	}

	@Override
	public boolean first() {
		if (data.size() == 0) {
			return false;
		}
		pointer = 0;
		return true;
	}

	@Override
	public int getInt(int columnIndex) {
		columnIndex--;
		Object indexData = data.get(pointer).get(columnIndex);
		if (indexData.equals(null)) {
			return 0;
		}
		return Integer.parseInt((String) indexData);
	}

	@Override
	public int getInt(String columnLabel) {
		int s = 0;
		for (int i = 0; i < colomnNames.length; i++) {
			if (columnLabel.equals(colomnNames[i])) {
				s = i;
				break;
			}
		}
		Object indexData = data.get(pointer).get(s);
		if (indexData.equals(null)) {
			return 0;
		}
		return Integer.parseInt((String) indexData);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Date getDate(int columnIndex) {
		columnIndex--;
		Object indexData = data.get(pointer).get(columnIndex);
		if (indexData.equals("null")) {
			return null;
		}
		String[] d = data.get(pointer).get(columnIndex).split("-");
		java.sql.Date date = new Date(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]));
		return date;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Date getDate(String columnLabel) {
		int s = 0;
		for (int i = 0; i < colomnNames.length; i++) {
			if (columnLabel.equals(colomnNames[i])) {
				s = i;
				break;
			}
		}
		// System.out.println(data.get(pointer).get(s));
		Object indexData = data.get(pointer).get(s);
		if (indexData.equals("null")) {
			return null;
		}
		String[] d = data.get(pointer).get(s).split("-");
		java.sql.Date date = new Date(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]));
		return date;
	}

	@Override
	public String getString(int columnIndex) {
		columnIndex--;
		Object indexData = data.get(pointer).get(columnIndex);
		if (indexData.equals(null)) {
			return null;
		}
		return indexData.toString();
	}

	@Override
	public String getString(String columnLabel) {
		int s = 0;
		for (int i = 0; i < colomnNames.length; i++) {
			if (columnLabel.equals(colomnNames[i])) {
				s = i;
				break;
			}
		}
		Object indexData = data.get(pointer).get(s);
		if (indexData.equals(null)) {
			return null;
		}
		return indexData.toString();
	}

	@Override
	public float getFloat(int columnIndex) {
		columnIndex--;
		Object indexData = data.get(pointer).get(columnIndex);
		if (indexData.equals(null)) {
			return (float) 0.0;
		}
		return Float.parseFloat((String) indexData);
	}

	@Override
	public float getFloat(String columnLabel) {
		int s = 0;
		for (int i = 0; i < colomnNames.length; i++) {
			if (columnLabel.equals(colomnNames[i])) {
				s = i;
				break;
			}
		}
		Object indexData = data.get(pointer).get(s);
		if (indexData.equals(null)) {
			return (float) 0.0;
		}
		return Float.parseFloat((String) indexData);
	}

	@Override
	public ResultSetMetaData getMetaData() {
		ResultSetMetaDt m = new ResultSetMetaDt();
		return m;
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		columnIndex--;
		String indexData = data.get(pointer).get(columnIndex);
		if (indexData.equals(null)) {
			return null;
		}
		switch (colomnTypes[columnIndex]) {
		case "int":
			int i = Integer.parseInt(indexData);
			return i;
			
		case "varchar":
			return indexData;
		case "Date":
			Date d = Date.valueOf(indexData);
			return d;
		case "Float":
			float f= Float.parseFloat(indexData);
			return f;
		default:
			break;
	}
		
		// indexData
//		throw new SQLException("Wrong Object" + printResultSet.getSelected());

		
		 return indexData;
	}

	@Override
	public Statement getStatement() {
		StatementClass S = new StatementClass();
		return S;
	}

	@Override
	public boolean isAfterLast() {
		if (pointer == data.size() && data.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isBeforeFirst() {
		try {
			if (pointer == -1) {
				return true;
			}
			return false;
		} catch (Exception e) {

		}
		return false;
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFirst() {
		if (pointer == 0 && data.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isLast() {
		if (pointer == data.size() - 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean last() {
		if (data.size() == 0) {
			return false;
		}
		pointer = data.size() - 1;
		return true;
	}

	@Override
	public boolean next() {
		if (pointer + 1 < data.size()) {
			pointer++;
			return true;
		}
		return false;
	}

	@Override
	public boolean previous() {
		if (pointer - 1 > -1) {
			pointer--;
			return true;
		}
		return false;
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
	public void cancelRowUpdates() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Array getArray(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Array getArray(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Blob getBlob(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Blob getBlob(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getBoolean(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getByte(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clob getClob(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clob getClob(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCursorName() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDouble(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLong(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ref getRef(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ref getRef(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRow() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getShort(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveToInsertRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean relative(int rows) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rowInserted() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateArray(int columnIndex, Array x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateArray(String columnLabel, Array x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateByte(String columnLabel, byte x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDate(String columnLabel, Date x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDouble(String columnLabel, double x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFloat(String columnLabel, float x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateInt(String columnLabel, int x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLong(String columnLabel, long x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNString(int columnIndex, String nString) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNString(String columnLabel, String nString) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNull(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateObject(String columnLabel, Object x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateShort(String columnLabel, short x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateString(String columnLabel, String x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTime(String columnLabel, Time x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean wasNull() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
