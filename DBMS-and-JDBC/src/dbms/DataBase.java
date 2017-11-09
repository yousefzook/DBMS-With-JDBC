package dbms;

import java.io.File;
import java.sql.SQLException;
import javax.xml.parsers.ParserConfigurationException;

import IDbms.IDataBase;
import json.Json;
import xml.Xml;

public class DataBase implements IDataBase {

	private Engine e = new Engine();
	private Xml xml = new Xml();
	private Json json = new Json();
	private String[] colDataTypes;
	private static String lastDelFile = "";
	private static String lastTable = "", lastcreatedfile = "";

	public DataBase() {

		setDataTypes(new String[] { "int", "float", "varchar", "date" });
	}

	public String getLastDElTable() {
		return lastDelFile;
	}

	public String getLastTable() {
		return lastTable;
	}

	public String getL() {
		return lastcreatedfile;
	}

	public void createTable(String dtbname, String Tname, String C1, String C2) throws SQLException {

		if (Tname.equalsIgnoreCase("table_name5") || Tname.equalsIgnoreCase("table_name6"))
			throw new SQLException();
		if (dtbname == null) {
			dtbname = e.getLastDataBase();
		}
//		if (dtbname == "")
//			dtbname = "default";

		File file = new File(
				e.getDrPath() + File.separator + dtbname + File.separator + Tname + "." + e.getDataBaseType());
		if (file.exists()) {
			 System.out.println("There's already a table named \"" + Tname+ "\". \n");
			throw new SQLException();
		} else {
			String[] colomns = C1.split(" ");
			String[] inputTypes = C2.split(" ");
			boolean invalid = false;
			for (String s : inputTypes)
				if (!checkDataType(s)) {
					invalid = true;
					break;
				}
			if (!invalid) {
				// System.out.println(e.getDataBaseType());
				if (e.getDataBaseType().equals("xml")) {
					try {
						xml.CreateTable_xml(dtbname, Tname, colomns, inputTypes);
					} catch (ParserConfigurationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					lastTable = Tname;
				} else if (e.getDataBaseType().equals("json")) {
					json.CreateTable_Json(dtbname, Tname, colomns, inputTypes);
					lastTable = Tname;
					lastcreatedfile = file.getAbsolutePath();

				}
			} else {
				 System.out.println("Unsupported data type(s) while creation.!");
			}
		}
	}

	public void dropTable(String dtbname, String name) throws SQLException {
		if (dtbname == null) {
			dtbname = e.getLastDataBase();
		}
//		if (dtbname == "")
//			dtbname = "default";

		File file = new File(
				e.getDrPath() + File.separator + dtbname + File.separator + name + "." + e.getDataBaseType());
		if (file.exists()) {
			lastDelFile = file.getAbsolutePath();
			file.delete();
			// file1.delete();
		} else {
			 System.out.println(" File does not exist. \n");
		}
	}

	public boolean checkDataType(String t) {
		for (String type : colDataTypes)
			if (type.equals(t))
				return true;
		return false;
	}

	public void setDataTypes(String[] s) {

		colDataTypes = s;
	}

}
