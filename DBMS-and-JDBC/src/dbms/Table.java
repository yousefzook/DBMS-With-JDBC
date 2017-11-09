package dbms;

import java.io.File;
import java.sql.SQLException;

import json.Json;
import parser.Validate;
import xml.Xml;
import IDbms.ITable;
import jdbc.PrintResultSet;

public class Table implements ITable {

	private static int updateCount;
	private int index;
	private String[] colomns, inputTypes;
	private boolean[] Ch;

	Engine e = new Engine();
	DataBase d = new DataBase();
	PrintResultSet p;
	Xml xml = new Xml();
	Json json = new Json();
	Validate validate = new Validate();

	public Table() {
		p = new PrintResultSet();
		Ch = new boolean[150];
		updateCount = 0;
	}

	public static int getUpdateCount() {
		return updateCount;
	}

	public void setXmlData() {
		colomns = xml.getCol();
		index = xml.getIndex();
		inputTypes = xml.getInputTypes();
	}

	public void setJsonData() {
		colomns = json.getCol();
		index = json.getIndex();
		inputTypes = json.getInputTypes();
	}


	@Override
	public void insertIntoTable(String dtbname, String T_name, String Colomns, String Values) throws SQLException {
		File file = null;
		try {
			if (dtbname == null) {
				dtbname = e.getLastDataBase();
			}
//			if (dtbname == "")
//				dtbname = "default";

			file = new File(
					e.getDrPath() + File.separator + dtbname + File.separator + T_name + "." + e.getDataBaseType());
			if (!file.exists()) {
				 System.out.println(" This table does not exist. \n");

			} else {
				if (e.getDataBaseType().equals("xml")) {
					xml.insert_xml(dtbname, T_name, Colomns, Values);
					updateCount = xml.getUpdatecounter();
				} else if (e.getDataBaseType().equals("json")) {
					String ins_val[] = Values.split(",");
					json.readColumns(dtbname, T_name);
					setJsonData();

					if (Colomns != null) {
						String ins_col[] = Colomns.split(" ");
						Ch = validate.Check(ins_col, colomns);
						json.insert_json(dtbname, T_name, ins_col, ins_val, Ch);
						updateCount = json.getUpdatecounter();
						Ch = new boolean[150];

					} else {
						Ch = validate.Check(colomns, colomns);
						json.insert_json(dtbname, T_name, colomns, ins_val, Ch);
						Ch = new boolean[150];
						updateCount = json.getUpdatecounter();
					}
				} else {
					System.out.println("type must be xml or json");
				}
			}
		} catch (Exception ex) {
			// System.out.println("insert cathces error");
			throw new SQLException("non existing file :  " + file.getAbsolutePath() + " > type: " + e.getDataBaseType()
					+ " last created json file : " + d.getL() + "last created table name " + d.getLastTable()
					+ "last deleted table " + d.getLastDElTable() + "does file exist? " + file.exists());
		}
	}

	@Override
	public void deleteFromTable(String dtbname, String tablename, String colName, String wherecol, String whereval,
			String sign) throws SQLException {
		if (dtbname == null) {
			dtbname = e.getLastDataBase();
		}
//		if (dtbname == "")
//			dtbname = "default";
		String[] columnName = null;
		File file = new File(
				e.getDrPath() + File.separator + dtbname + File.separator + tablename + "." + e.getDataBaseType());
		if (!file.exists()) {
			 System.out.println(" This table does not exist. \n");
		} else {

			if (colName != null) {
				columnName = colName.split(" ");
			}
			if (tablename != null && colName == null && wherecol == null && whereval == null && sign == null) {
				if (e.getDataBaseType().equals("xml")) {
					xml.Delete_table(dtbname, tablename);
					updateCount = xml.getUpdatecounter();
				} else if (e.getDataBaseType().equals("json")) {
					json.Delete_table(dtbname, tablename);
					updateCount = json.getUpdatecounter();
				}
			} else if (tablename != null && colName != null && wherecol == null && whereval == null && sign == null) {
				if (e.getDataBaseType().equals("xml")) {
					xml.Delete_Colomns(dtbname, tablename, columnName);
					updateCount = xml.getUpdatecounter();
				} else if (e.getDataBaseType().equals("json")) {
					json.Delete_Colomns(dtbname, tablename, columnName);
					updateCount = json.getUpdatecounter();
				}

			} else if (tablename != null && colName == null && wherecol != null && whereval != null && sign != null) {
				if (e.getDataBaseType().equals("xml")) {
					xml.Delete_allselected_with_where(dtbname, tablename, wherecol, whereval, sign);
					updateCount = xml.getUpdatecounter();
				} else if (e.getDataBaseType().equals("json")) {
					json.Delete_allselected_with_where(dtbname, tablename, wherecol, whereval, sign);
					updateCount = json.getUpdatecounter();
				}
			} else {
				if (e.getDataBaseType().equals("xml")) {
					xml.Delete_selected_with_where(dtbname, tablename, columnName, wherecol, whereval, sign);
					updateCount = xml.getUpdatecounter();
				} else if (e.getDataBaseType().equals("json")) {
					json.Delete_selected_with_where(dtbname, tablename, columnName, wherecol, whereval, sign);
					updateCount = json.getUpdatecounter();
				}
			}

		}
	}

	@Override
	public void updateTable(String dtbname, String tablename, String colName, String elements, String wherecol,
			String whereval, String sign) throws SQLException {
		if (dtbname == null) {
			dtbname = e.getLastDataBase();
		}
//		if (dtbname == "")
//			dtbname = "default";

		File file = new File(
				e.getDrPath() + File.separator + dtbname + File.separator + tablename + "." + e.getDataBaseType());
		if (!file.exists()) {
			 System.out.println(" This table does not exist. \n");

		} else {
			readData(dtbname, tablename);
			String[] columnName = null;
			String[] values = null;
			columnName = colName.split(" ");
			values = elements.split(",");
			boolean check = true;

			if (columnName.length != values.length) {
				check = false;
				 System.out.println("Num of colomns doesn't match number of values");
			}
			if (wherecol != null && validate.indexOfColType(wherecol, colomns) == -1)
				check = false;
			for (int i = 0; i < columnName.length; i++) {
				if (validate.indexOfColType(columnName[i], colomns) == -1) {
					check = false;
					break;
				}
			}
			if (check) {

				if (wherecol == null && whereval == null && sign == null) {
					if (e.getDataBaseType().equals("xml")) {
						xml.updateWithoutWhere(dtbname, tablename, columnName, values);
						updateCount = xml.getUpdatecounter();
					} else if (e.getDataBaseType().equals("json")) {
						json.updateWithoutWhere(dtbname, tablename, columnName, values);
						updateCount = json.getUpdatecounter();
					}
				} else {
					if (e.getDataBaseType().equals("xml")) {
						xml.updateWithWhere(dtbname, tablename, columnName, values, wherecol, whereval, sign);
						updateCount = xml.getUpdatecounter();
					} else if (e.getDataBaseType().equals("json")) {
						json.updateWithWhere(dtbname, tablename, columnName, values, wherecol, whereval, sign);
						updateCount = json.getUpdatecounter();
					}
				}
			} else {
				 System.out.println("Updating-Error . \n");
			}
		}

	}

	@Override
	public void selectFromTable(String dtbname, String tablename, String colName, String wherecol, String whereval,
			String sign) throws SQLException {
		if (dtbname == null) {
			dtbname = e.getLastDataBase();
		}
//		if (dtbname == "")
//			dtbname = "default";
		
		String[] columnName = null;

		File file = new File(
				e.getDrPath() + File.separator + dtbname + File.separator + tablename + "." + e.getDataBaseType());
		if (!file.exists()) {
			 System.out.println(" This table does not exist. \n");
		} else {
			readData(dtbname, tablename);
			boolean check = true;
			if (colName != null) {
				columnName = colName.split(" ");

				for (int i = 0; i < columnName.length; i++) {
					if (validate.indexOfColType(columnName[i], colomns) == -1) {
						check = false;
						break;
					}
				}
			} else {
				columnName = colomns;
			}
			if (wherecol != null && validate.indexOfColType(wherecol, colomns) == -1)
				check = false;

			if (check) {
				if (tablename != null && wherecol == null && whereval == null && sign == null) {
					p.finePrint_table(dtbname, tablename, columnName, e.getDataBaseType());
				} else if (tablename != null && wherecol != null && whereval != null && sign != null) {
					p.print_selected_with_where(dtbname, tablename, columnName, wherecol, whereval, sign,
							e.getDataBaseType());
				}
			} else {
				 System.out.println(" Error. none exsisting columns ! . \n");
			}
		}
	}

	public void selectDistinct(String dtbname, String tablename, String colName, String wherecol, String whereval,
			String sign) throws SQLException {
		if (dtbname == null) {
			dtbname = e.getLastDataBase();
		}
//		if (dtbname == "")
//			dtbname = "default";

		String[] columnName = null;
		File file = new File(
				e.getDrPath() + File.separator + dtbname + File.separator + tablename + "." + e.getDataBaseType());
		if (!file.exists()) {
			 System.out.println(" This table does not exist. \n");
		} else {
			readData(dtbname, tablename);
			boolean check = true;
			if (colName != null) {
				columnName = colName.split(" ");

				for (int i = 0; i < columnName.length; i++) {
					if (validate.indexOfColType(columnName[i], colomns) == -1) {
						check = false;
						break;
					}
				}
			} else {
				columnName = colomns;
			}
			if (wherecol != null && validate.indexOfColType(wherecol, colomns) == -1)
				check = false;

			if (check) {
				p.PrintDistinct(dtbname, tablename, columnName, wherecol, whereval, sign, e.getDataBaseType());
			} else {
				 System.out.println(" Error. none exsisting columns ! . \n");
			}
		}
	}

	public void alterTableAdd(String dtbname, String tablename, String colName, String colType) throws SQLException {

		if (dtbname == null) {
			dtbname = e.getLastDataBase();
		}
//		if (dtbname == "")
//			dtbname = "default";
		
		File file = new File(
				e.getDrPath() + File.separator + dtbname + File.separator + tablename + "." + e.getDataBaseType());
		if (!file.exists()) {
			System.out.println(" This table does not exist. \n");
		} else {
			readData(dtbname, tablename);
			if (validate.indexOfColType(colName, colomns) == -1) {// to check
																	// that name
																	// wasn't exist before
				boolean invalid = true;
				if (!d.checkDataType(colType)) {
					invalid = false;
				}
				if (invalid) {
					if (e.getDataBaseType().equals("xml")) {
						xml.alterAdd_xml(dtbname, tablename, colName, colType);
						updateCount = xml.getUpdatecounter();
					} else if (e.getDataBaseType().equals("json")) {
						json.alterAdd_json(dtbname, tablename, colName, colType);
						updateCount = json.getUpdatecounter();
					}
				} else {
					 System.out.println(" Error. unsupported dataType ! \n");
				}
			} else {
				 System.out.println(" Error. this umn is already exists ! .\n");
			}
		}
	}

	public void alterTableDrop(String dtbname, String tablename, String colName) throws SQLException {

		if (dtbname == null) {
			dtbname = e.getLastDataBase();
		}
//		if (dtbname == "")
//			dtbname = "default";

		File file = new File(
				e.getDrPath() + File.separator + dtbname + File.separator + tablename + "." + e.getDataBaseType());
		if (!file.exists()) {
			 System.out.println(" This table does not exist. \n");
		} else {
			readData(dtbname, tablename);

			if (validate.indexOfColType(colName, colomns) != -1) {

				if (e.getDataBaseType().equals("xml")) {
					xml.alterDrop_xml(dtbname, tablename, colName);
					updateCount = xml.getUpdatecounter();
				} else if (e.getDataBaseType().equals("json")) {
					json.alterDrop_json(dtbname, tablename, colName);
					updateCount = json.getUpdatecounter();
				}
			} else {
				 System.out.println(" Error. this umn is already exists ! .\n");
			}
		}

	}

	public void readData(String dtbname, String tablename) throws SQLException {
		if (e.getDataBaseType().equals("xml")) {
			xml.readTableData(dtbname, tablename);
			setXmlData();
		} else if (e.getDataBaseType().equals("json")) {
			json.readColumns(dtbname, tablename);
			setJsonData();
		}
	}

}
