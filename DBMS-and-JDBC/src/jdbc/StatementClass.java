package jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dbms.Table;
import parser.Parser;

public class StatementClass implements Statement {
	private final static Logger logger =LogManager.getLogger(StatementClass.class);

	private String commandsList;
	private static String sq = "";
	private Parser parse;
	private ResultSetClass rs;
	private Table table = new Table();
	private PrintResultSet p = new PrintResultSet();

	public StatementClass() {
		commandsList = "";
	}

	@Override
	public void addBatch(String sql) {
		// TODO Auto-generated method stub
		commandsList += sql + ";";
		logger.info("the addBatch function for "+sql+ " was successful");

	}

	@Override
	public void clearBatch() {
		// TODO Auto-generated method stub
		commandsList = "";
		logger.info("the clearBatch function was successful");

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		this.getConnection().setStatementToNull();
		logger.info("Closing Database connections");

	}

	public String getsq() {
		return sq;
	}

	@Override
	public boolean execute(String sql) throws SQLException {

//		try {
			parse = new Parser(sql);
			//logger.info("Successful SQL Statement Parsing for "+sql);
			//logger .error("Error in parsing the SQL Statement ::: Message :  "
			//		+e.getMessage());
			String[] sqls = sql.split(" ");
			if (sqls[0].toLowerCase().equals("select")) {
				if (p.getSelected() == null) {
					return false;
				} else {
					return true;
				}
			} else
				return false;
//		} catch (SQLException e) {
//			throw new SQLException("an sql exception has been catched");
//		}
	}

	@Override
	public int[] executeBatch() throws SQLException {
		String[] sqls = commandsList.split(";");
		int[] updateCount = new int[sqls.length];
		for (int i = 0; i < sqls.length; i++) {
			parse = new Parser(sqls[i]);
			int z = table.getUpdateCount();
			if (z == 0) {
				updateCount[i] = SUCCESS_NO_INFO;
			} else {
				updateCount[i] = table.getUpdateCount();
			}
		}
		logger.info("ExecuteBatch was successful");

		return updateCount;
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
//		try {			logger.info(" The query execution was successful");
//		logger.error("The query execution was unsuccessful : can't find 'select'");

			sql = sql.toLowerCase();
			String[] statmentWords = sql.split(" ");
				parse = new Parser(sql);
				rs = new ResultSetClass();
				return rs;
//		} catch (SQLException e) {
//			throw new SQLException();
//		}
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		sql = sql.toLowerCase();
		String[] statmentWords = sql.split(" ");
		if (!statmentWords[0].equals("create") && !statmentWords[0].equals("select")) {
			parse = new Parser(sql);
			logger.info(" The query execution was successful");/////////////////////////

			return table.getUpdateCount();
		}
		logger.error("The query execution was unsuccessful : found "+"'"+//////////////////
				statmentWords[0]+"'");
		return 0;
	}

	@Override
	public ConnectionClass getConnection() {
		// TODO Auto-generated method stub
		logger.info("Initiating Database connections");

		ConnectionClass con = new ConnectionClass();
		return con;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return null;
	}

	@Override
	public void cancel() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
	}

	@Override
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
	}

	@Override
	public boolean execute(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return false;
	}

	@Override
	public boolean execute(String arg0, int[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return false;
	}

	@Override
	public boolean execute(String arg0, String[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return false;
	}

	@Override
	public int executeUpdate(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return 0;
	}

	@Override
	public int executeUpdate(String arg0, int[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return 0;
	}

	@Override
	public int executeUpdate(String arg0, String[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return 0;
	}

	@Override
	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return 0;
	}

	@Override
	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return 0;
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return null;
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return 0;
	}

	@Override
	public int getMaxRows() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return 0;
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return false;
	}

	@Override
	public boolean getMoreResults(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return false;
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return 0;
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		// TODO Auto-generated method stub
		ResultSet rs = new ResultSetClass();
		return rs;
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return 0;
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return 0;
	}

	@Override
	public int getResultSetType() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return 0;
	}

	@Override
	public int getUpdateCount() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return 0;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return null;
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return false;
	}

	@Override
	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return false;
	}

	@Override
	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");
		return false;
	}

	@Override
	public void setCursorName(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");

	}

	@Override
	public void setEscapeProcessing(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");

	}

	@Override
	public void setFetchDirection(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");

	}

	@Override
	public void setFetchSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");

	}

	@Override
	public void setMaxFieldSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");

	}

	@Override
	public void setMaxRows(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");

	}

	@Override
	public void setPoolable(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");

	}

	@Override
	public void setQueryTimeout(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		logger.warn("NOT Supported");

	}

}
