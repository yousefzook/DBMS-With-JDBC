package dbms;

import java.io.File;
import java.sql.SQLException;

import IDbms.IEngine;

public class Engine implements IEngine {

	private static String lastDtb = "", path = System.getProperty("user.home");
	private static String DataBaseType = "xml";

	public void setDataBaseType(String dbType) throws SQLException {
		dbType = dbType.toLowerCase();

		if (dbType.equals("xmldb")) {
			DataBaseType = "xml";
		} else if (dbType.equals("jsondb")) {
			DataBaseType = "json";
		} else if (dbType.equals("altdb")) {
			if (DataBaseType.equals("xml"))
				DataBaseType = "json";
			else if (DataBaseType.equals("json"))
				DataBaseType = "xml";
		} else {
			System.out.println("unknowntype");
		}

	}

	public String getDataBaseType() {

		return DataBaseType;
	}

	public void setPath(String p) {
		this.path = p;
	}

	public String getDrPath() {

		return path;
	}

	public void setLastDbName(String lst) {
		lastDtb = lst;
	}

	public void createDataBase(String name) throws SQLException {
		File file = new File(getDrPath() + File.separator + name);
		if (!file.exists()) {
			if (file.mkdirs()) {
				System.out.println("Directory is created!");
				lastDtb = name;
			}

		} else {
			System.out.println("there's already a database named \"" + name + "\". \n");

		}
	}

	public void dropDataBase(String name) {

		File file = new File(getDrPath() + File.separator + name);
		// System.out.println(file1);
		if (file.exists()) {
			String[] entries = file.list();
			for (String s : entries) {
				File currentFile = new File(file.getPath(), s);
				currentFile.delete();
			}
			file.delete();
			System.out.println("delete done");
		} else {
			System.out.println(" there's no database named \"" + name + "\".\n");

		}
	}

	public String getLastDataBase() {

		return lastDtb;
	}

}
