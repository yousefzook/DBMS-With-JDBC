package test;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import jdbc.DriverManager;

public class JUnitTester {

	public Statement dataBaseCreation() throws SQLException {
		Properties info = new Properties();
		File dbDir = new File("java.io.tmpdir" + "/jdbc/" + Math.round((((float) Math.random()) * 100000)));
		info.put("path", dbDir.getAbsoluteFile());
		Driver driver = new DriverManager();
		Connection connection = driver.connect("jdbc:" + "xmldb" + "://localhost", info);
		Statement statement = connection.createStatement();
		statement.executeQuery("drop database data");
		statement.executeQuery("create database data");
		return statement;
	}

	@Test
	public void testCreation() throws SQLException {
		Statement statement = dataBaseCreation();
		statement.executeQuery("drop database data");
		boolean result = statement.execute("create database data");
		// assertFalse("A DataBase Valid Creation has error", result);
		try {
			statement.executeQuery("create database data");
			// Assert.fail("Created existing Data Base didn't give exception
			// !");
		} catch (SQLException e) {
			System.out.println("Created existing Data Base throw an exception !");
		}

		statement.executeQuery(
				"create table table1 (column_name1 varchar, column_name2 int, column_name3 varchar, column_name4 DATE)");
		try {
			statement.executeQuery(
					"create table table1 (column_name1 varchar, column_name2 int, column_name3 varchar, column_name4 DATE)");
			// Assert.fail("Created existing table didn't give exception !");
		} catch (SQLException e) {
			System.out.println("Created existing Table throw an exception !");
		}

	}

	@Test
	public void testExcuteBatch() throws SQLException {
		Statement statement = dataBaseCreation();
		statement.executeQuery("create table table1 (column_name1 varchar, column_name2 int, column_name3 varchar)");
		statement.addBatch(
				"INSERT INTO table1(column_name1,  column_name2, column_name3) VALUES ('value1', 1, 'value7' )");
		statement.addBatch(
				"INSERT INTO table1(column_name1,  column_name2, column_name3) VALUES ('value2', 2,'value8')");
		statement.addBatch(
				"INSERT INTO table1(column_name1,  column_name2, column_name3) VALUES ('value3', 3,'value9')");
		statement.addBatch(
				"INSERT INTO table1(column_name1,  column_name2, column_name3) VALUES ('value4', 4, 'value10' )");
		statement.addBatch(
				"INSERT INTO table1(column_name1,  column_name2, column_name3) VALUES ('value5', 5,'value11')");
		statement.addBatch(
				"INSERT INTO table1(column_name1,  column_name2, column_name3) VALUES ('value6', 6,'value12')");
		statement.addBatch("delete From table1 WHERE column_name2 > 4");
		try {
			int[] result = new int[7];
			result = statement.executeBatch();
			Assert.assertEquals("Delete returned wrong number", 2, result[6]);
			boolean res = statement.execute("SELECT * FROM table1 WHERE column_name2 < 4");
			int testSelect = statement.executeUpdate(
					"UPDATE table1 SET column_name1='new_value1', COLUMN_NAME2=7, column_name3='new_value2' where column_name2 > 4");
			Assert.assertEquals(0, testSelect);
			int testSelect2 = statement.executeUpdate(
					"UPDATE table1 SET column_name1='new_value1', COLUMN_NAME2=7, column_name3='new_value2' where column_name2 < 3");
			Assert.assertEquals(2, testSelect2);
			assertTrue("Statement excute returns false instead of true in select excute !", res);
			statement.addBatch(
					"INSERT INTO table2_Wrong(column_name1,  column_name2, column_name3) VALUES ('value6', 6,'value12')");
			result = statement.executeBatch();
			// Assert.fail("excuting invalid batch didn't give exception !");

		} catch (SQLException e) {
			System.out.println("An Exception was thrown in excuteBatch() test");
		}

	}

}
