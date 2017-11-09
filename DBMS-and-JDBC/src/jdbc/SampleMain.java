package jdbc;

import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;
import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

public class SampleMain {
	public static void main(String args[]) {
		try {
			Class.forName("jdbc.DriverManager");
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
			System.exit(1);
		}
		// Contains Database name.

		Scanner input = new Scanner(System.in);
		// Enter the protocol
		System.out.println("Enter the protocol");
		String protocol = input.nextLine();
		String url = "jdbc:" + protocol + "://localhost";
		Properties info = new Properties();
		File dbDir = new File(System.getProperty("user.home"));
		info.put("path", dbDir.getAbsoluteFile());
		Driver driver;
		Connection con;
		Statement stmt;

		try {
			driver = (Driver) new DriverManager();
			con = driver.connect("jdbc:" + protocol + "://localhost", info);
			while (con == null) {
				System.out.println("Unsuppotreted protocol \nEnter the protocol (xmldb / josn / altdb)): ");
				protocol = input.nextLine();
				url = "jdbc:" + protocol + "://localhost";
				con = driver.connect("jdbc:" + protocol + "://localhost", info);
			}
			System.out.println("connection has been established ! \n");
			stmt = con.createStatement();
			while (true) {
				String sqlStatement = input.nextLine();
				String[] split = sqlStatement.split(" ");
				if (split[0].equals("insert") || split[0].equals("delete") || split[0].equals("update"))
					stmt.executeUpdate(sqlStatement); // Execute SQL queries.
				else
					stmt.execute(sqlStatement); // Execute SQL queries.

				stmt.close();
				//con.close();
			}
		} catch (SQLException ex) {
			System.out.println("SQLException: ");
		}

	}
}