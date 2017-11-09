package json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import dbms.Engine;
import parser.Validate;

public class Json {
	private int index;
	private int updatecounter;
	private Engine e = new Engine();

	public int getUpdatecounter() {
		return updatecounter;
	}

	private String[] colomns, inputTypes;
	Validate validate = new Validate();

	public Json() {

		index = 1;
	}

	public int getIndex() {
		return index;
	}

	public String[] getCol() {
		return colomns;
	}

	public String[] getInputTypes() {
		return inputTypes;

	}

	@SuppressWarnings("unchecked")
	public void insert_json(String DB_name, String T_name, String[] ins_col, String[] ins_val, boolean[] Ch) {
		updatecounter = 0;
		JSONParser parser = new JSONParser();
		Object obj;
		boolean valid = true;
		try {
			obj = parser.parse(new InputStreamReader(
					new FileInputStream(e.getDrPath() + File.separator + DB_name + File.separator + T_name + ".json")));

			JSONObject Loaded = (JSONObject) obj;
			JSONArray Table = (JSONArray) Loaded.get(T_name);
			JSONObject Rows = (JSONObject) Table.get(0);// the only object in
														// the table array
			JSONArray Row_arr = new JSONArray();
			JSONObject Row = new JSONObject(); // the only object in ith Row
			int j = 0;
			for (int i = 0; i < colomns.length; i++) {
				if ((Ch[i]) == false) {

					Row.put(colomns[i], "null");
				} else {
					int n = validate.indexOfColType(ins_col[j], colomns);
					if ((inputTypes[n].equals("int") && ins_val[j].matches("-?\\d+"))
							|| (inputTypes[n].equals("varchar") && !ins_val[j].matches("\\d+"))
							|| (inputTypes[n].equals("float") && ins_val[j].matches("-?\\d+([.]\\d+)?"))
							|| (inputTypes[n].equals("date")
									&& ins_val[j].matches("\\d{4}-([0][0-9]|[1][0-2])-([0-2][0-9]|[3][0-1])"))) {
						Row.put(ins_col[j], ins_val[j]);

					} else {
						valid = false;
						 System.out.println(" Columns do not match values data Types. \n");
					}
					j++;

				}
			}
			Row_arr.add(Row);
			Rows.put("Row" + Integer.toString(index), Row_arr);

			Table.add(Rows);
			Table.remove(1);
			Loaded.put(T_name, Table);
			if (valid) {
				updatecounter = 1;
				FileWriter file1;

				file1 = new FileWriter(e.getDrPath() + File.separator + DB_name + File.separator + T_name + ".json");

				file1.write(new GsonBuilder().setPrettyPrinting().create()
						.toJson(new JsonParser().parse(Loaded.toJSONString())));
				file1.flush();
				file1.close();
			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void readColumns(String dtbname, String Tname) {
		JSONParser parser = new JSONParser();
		try {
			File file = new File(e.getDrPath() + File.separator + dtbname + File.separator + Tname + ".json");
			if (file.exists()) {
				Object obj = parser.parse(new InputStreamReader(new FileInputStream(
						e.getDrPath() + File.separator + dtbname + File.separator + Tname + ".json")));
				JSONObject Loaded = (JSONObject) obj;
				JSONArray Colomns = (JSONArray) Loaded.get("Colomns");
				JSONArray Table = (JSONArray) Loaded.get(Tname);
				JSONObject rows = (JSONObject) Table.get(0);

				JSONObject col = (JSONObject) Colomns.get(0);
				colomns = new String[col.size()];
				inputTypes = new String[col.size()];
				for (int i = 0; i < col.size(); i++) {
					String[] Headers = col.get("Colomn" + Integer.toString(i + 1)).toString().split(" ");
					colomns[i] = Headers[0];
					inputTypes[i] = Headers[1];
				}
				index = rows.size();
			} else {
				// System.out.println("Jason table Doesn't Exist");
				throw new SQLException();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public void CreateTable_Json(String dtbname, String Tname, String[] colomns, String[] inputTypes) {
		JSONObject root = new JSONObject();
		JSONArray Table_arr = new JSONArray();
		JSONObject Table = new JSONObject();
		Table_arr.add(Table);
		JSONArray Colomns = new JSONArray();
		JSONObject Colomn = new JSONObject();
		for (int i = 0; i < colomns.length; i++) {
			Colomn.put("Colomn" + Integer.toString(i + 1), colomns[i] + " " + inputTypes[i]);
		}
		Colomns.add(Colomn);

		root.put(Tname, Table_arr);
		root.put("Colomns", Colomns);

		try {

			FileWriter file1 = new FileWriter(
					e.getDrPath() + File.separator + dtbname + File.separator + Tname + ".json");
			file1.write(
					new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(root.toJSONString())));

			file1.flush();
			file1.close();
			 System.out.println("File saved!");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public void Save_json(List<List<String>> rowsList, List<String> headersList, String DBName, String Tname) {
		JSONObject root = new JSONObject();
		JSONArray Table_arr = new JSONArray();
		JSONObject Table = new JSONObject();
		for (int i = 0; i < rowsList.size(); i++) {
			JSONArray Row_arr = new JSONArray();
			JSONObject Row = new JSONObject();
			for (int j = 0; j < headersList.size(); j++) {
				Row.put(headersList.get(j), rowsList.get(i).get(j));
			}
			Row_arr.add(Row);
			Table.put("Row" + Integer.toString(i + 1), Row_arr);
		}
		Table_arr.add(Table);
		root.put(Tname, Table_arr);

		// System.out.println(root.toJSONString());
		try {

			FileWriter file1 = new FileWriter(e.getDrPath() + File.separator + Tname + ".json");
			file1.write(
					new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(root.toJSONString())));

			file1.flush();
			file1.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public void Delete_table(String DB_name, String T_name) {
		updatecounter = 0;
		JSONParser parser = new JSONParser();
		readColumns(DB_name, T_name);
		Object obj;
		try {
			obj = parser.parse(new InputStreamReader(
					new FileInputStream(e.getDrPath() + File.separator + DB_name + File.separator + T_name + ".json")));

			JSONObject Loaded = (JSONObject) obj;
			JSONArray Table = (JSONArray) Loaded.get(T_name);

			Table.clear();
			JSONObject Rows = new JSONObject();
			Table.add(Rows);
			// System.out.println(Table.get(0).toString());

			FileWriter file1;
			file1 = new FileWriter(e.getDrPath() + File.separator + DB_name + File.separator + T_name + ".json");

			file1.write(new GsonBuilder().setPrettyPrinting().create()
					.toJson(new JsonParser().parse(Loaded.toJSONString())));
			file1.flush();
			file1.close();
			updatecounter = index;
		} catch (Exception e) {

		}
	}

	@SuppressWarnings("unchecked")
	public void Delete_Colomns(String DB_name, String T_name, String[] columnName) {
		updatecounter = 0;
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(new InputStreamReader(
					new FileInputStream(e.getDrPath() + File.separator + DB_name + File.separator + T_name + ".json")));

			JSONObject Loaded = (JSONObject) obj;
			JSONArray Table = (JSONArray) Loaded.get(T_name);
			JSONObject Rows = (JSONObject) Table.get(0);
			for (int i = 0; i < columnName.length; i++) {
				for (int j = 0; j < Rows.size(); j++) {
					JSONArray Rowarr = (JSONArray) Rows.get("Row" + Integer.toString(j));
					JSONObject Row = (JSONObject) Rowarr.get(0);
					Row.replace(columnName[i], "null");
				}
			}

			FileWriter file1;
			file1 = new FileWriter(e.getDrPath() + File.separator + DB_name + File.separator + T_name + ".json");

			file1.write(new GsonBuilder().setPrettyPrinting().create()
					.toJson(new JsonParser().parse(Loaded.toJSONString())));
			file1.flush();
			file1.close();
			updatecounter = index;
		} catch (Exception e) {

		}
	}

	public void Delete_selected_with_where(String DB_name, String T_name, String columnname[], String colname,
			String val, String sign) {// The same as UPDate with where but the
										// update values are null
		updatecounter = 0;
		String[] elements = new String[columnname.length];
		for (int i = 0; i < columnname.length; i++) {
			elements[i] = "null";
		}

		updateWithWhere(DB_name, T_name, columnname, elements, colname, val, sign);
	}

	@SuppressWarnings("unchecked")
	public void Delete_allselected_with_where(String DB_name, String T_name, String colname, String val, String sign) {
		updatecounter = 0;
		JSONParser parser = new JSONParser();
		boolean checking = false, Deleted = false;
		int Delete = 0;

		readColumns(DB_name, T_name);
		Object obj;
		try {
			obj = parser.parse(new InputStreamReader(
					new FileInputStream(e.getDrPath() + File.separator + DB_name + File.separator + T_name + ".json")));

			JSONObject Loaded = (JSONObject) obj;
			JSONArray Table = (JSONArray) Loaded.get(T_name);
			JSONObject Rows = (JSONObject) Table.get(0);
			int size = Rows.size();// store Original Size
			int m = validate.indexOfColType(colname, colomns);
			for (int j = 0; j < size; j++) {
				JSONArray Rowarr = (JSONArray) Rows.get("Row" + Integer.toString(j));
				JSONObject Row = (JSONObject) Rowarr.get(0);
				for (int i = 0; i < Row.size(); i++) {
					String s = Row.get(colname).toString();
					if (inputTypes[m].equals("varchar")) {
						if (sign.equals("=") && s.equals(val)) {
							checking = true;
						} else if (sign.equals(">") && (s.compareTo(val) > 0)) {
							checking = true;
						} else if (sign.equals("<") && (val.compareTo(s) > 0)) {
							checking = true;
						}
					} else if (inputTypes[m].equals("float") || inputTypes[m].equals("int")) {
						if (sign.equals("=") && Float.parseFloat(s) == Float.parseFloat(val)) {
							checking = true;
						} else if (sign.equals(">") && Float.parseFloat(s) > Float.parseFloat(val)) {
							checking = true;
						} else if (sign.equals("<") && Float.parseFloat(s) < Float.parseFloat(val)) {
							checking = true;
						}

					} else if (inputTypes[m].equals("date")) {

						String[] d1 = s.split("-");
						String[] d2 = val.split("-");

						java.sql.Date dateOne = new java.sql.Date(Integer.parseInt(d1[0]), Integer.parseInt(d1[1]),
								Integer.parseInt(d1[2]));
						java.sql.Date dateTwo = new java.sql.Date(Integer.parseInt(d2[0]), Integer.parseInt(d2[1]),
								Integer.parseInt(d2[2]));

						if (sign.equals("=") && dateOne.equals(dateTwo)) {
							checking = true;
						} else if (sign.equals(">") && dateOne.after(dateTwo)) {
							checking = true;
						} else if (sign.equals("<") && dateOne.before(dateTwo)) {
							checking = true;
						}
					}
				}
				if (checking) {
					Rows.remove("Row" + Integer.toString(j));
					updatecounter++;
					Delete++;
					Deleted = true;

				} else if (Deleted) {
					JSONArray Rowarr2 = Rowarr;
					Rows.remove("Row" + Integer.toString(j));
					Rows.put("Row" + Integer.toString(j - Delete), Rowarr2);
				}
				checking = false;

			}

			FileWriter file1;

			file1 = new FileWriter(e.getDrPath() + File.separator + DB_name + File.separator + T_name + ".json");

			file1.write(new GsonBuilder().setPrettyPrinting().create()
					.toJson(new JsonParser().parse(Loaded.toJSONString())));
			file1.flush();
			file1.close();
		} catch (Exception e) {

		}
	}

	@SuppressWarnings("unchecked")
	public void updateWithWhere(String DB_name, String T_name, String colName[], String elements[], String wherecol,
			String whereval, String sign) {
		JSONParser parser = new JSONParser();
		boolean checking = false;
		readColumns(DB_name, T_name);
		boolean valid = true;
		Object obj;
		try {
			obj = parser.parse(new InputStreamReader(
					new FileInputStream(e.getDrPath() + File.separator + DB_name + File.separator + T_name + ".json")));
			JSONObject Loaded = (JSONObject) obj;
			JSONArray Table = (JSONArray) Loaded.get(T_name);
			JSONObject Rows = (JSONObject) Table.get(0);
			int m = validate.indexOfColType(wherecol, colomns);
			updatecounter = 0;
			for (int j = 0; j < Rows.size(); j++) {
				JSONArray Rowarr = (JSONArray) Rows.get("Row" + Integer.toString(j));
				JSONObject clomommmn = (JSONObject) Rowarr.get(0);
				// for (int i = 0; i < clomommmn.size(); i++) {
				String s = clomommmn.get(wherecol).toString();
				if (inputTypes[m].equals("varchar")) {
					if (sign.equals("=") && s.equals(whereval)) {
						checking = true;
					} else if (sign.equals(">") && (s.compareTo(whereval) > 0)) {
						checking = true;
					} else if (sign.equals("<") && (whereval.compareTo(s) > 0)) {
						checking = true;
					}
				} else if (inputTypes[m].equals("float") || inputTypes[m].equals("int")) {
					if (sign.equals("=") && Float.parseFloat(s) == Float.parseFloat(whereval)) {
						checking = true;
					} else if (sign.equals(">") && Float.parseFloat(s) > Float.parseFloat(whereval)) {
						checking = true;
					} else if (sign.equals("<") && Float.parseFloat(s) < Float.parseFloat(whereval)) {
						checking = true;
					}

				} else if (inputTypes[m].equals("date")) {

					String[] d1 = s.split("-");
					String[] d2 = whereval.split("-");

					java.sql.Date dateOne = new java.sql.Date(Integer.parseInt(d1[0]), Integer.parseInt(d1[1]),
							Integer.parseInt(d1[2]));
					java.sql.Date dateTwo = new java.sql.Date(Integer.parseInt(d2[0]), Integer.parseInt(d2[1]),
							Integer.parseInt(d2[2]));

					if (sign.equals("=") && dateOne.equals(dateTwo)) {
						checking = true;
					} else if (sign.equals(">") && dateOne.after(dateTwo)) {
						checking = true;
					} else if (sign.equals("<") && dateOne.before(dateTwo)) {
						checking = true;
					}
				}
				// }
				if (checking) {
					boolean notrepeated = true;
					for (int i = 0; i < colName.length; i++) {
						int n = validate.indexOfColType(colName[i], colomns);
						if ((inputTypes[n].equals("int") && elements[i].matches("-?\\d+"))
								|| (inputTypes[n].equals("varchar") && !elements[i].matches("\\d+"))
								|| (inputTypes[n].equals("float") && elements[i].matches("-?\\d+([.]\\d+)?"))
								|| (inputTypes[n].equals("date")
										&& elements[i].matches("\\d{4}-([0][0-9]|[1][0-2])-([0-2][0-9]|[3][0-1])"))
								|| elements[i].equals("null")) {
							clomommmn.replace(colName[i], elements[i]);
							if (notrepeated) {
								updatecounter++;
								notrepeated = false;
							}
						} else {
							 System.out.println("Update values don't match Colomns Data-type");
							valid = false;
						}
					}
				}
				checking = false;
			}
			System.out.println(updatecounter);
			if (valid) {
				FileWriter file1;

				file1 = new FileWriter(e.getDrPath() + File.separator + DB_name + File.separator + T_name + ".json");

				file1.write(new GsonBuilder().setPrettyPrinting().create()
						.toJson(new JsonParser().parse(Loaded.toJSONString())));
				file1.flush();
				file1.close();
			}
		} catch (Exception e) {

		}

	}

	@SuppressWarnings({ "unused", "unchecked" })
	public void updateWithoutWhere(String DB_name, String T_name, String colName[], String elements[]) {
		JSONParser parser = new JSONParser();
		boolean checking = false;

		readColumns(DB_name, T_name);
		boolean valid = true;

		Object obj;
		try {
			obj = parser.parse(new InputStreamReader(
					new FileInputStream(e.getDrPath() + File.separator + DB_name + File.separator + T_name + ".json")));
			updatecounter = 0;
			JSONObject Loaded = (JSONObject) obj;
			JSONArray Table = (JSONArray) Loaded.get(T_name);
			JSONObject Rows = (JSONObject) Table.get(0);
			for (int j = 0; j < Rows.size(); j++) {
				JSONArray Rowarr = (JSONArray) Rows.get("Row" + Integer.toString(j));
				JSONObject Row = (JSONObject) Rowarr.get(0);
				for (int i = 0; i < colName.length; i++) {
					boolean notrepeated = true;
					int n = validate.indexOfColType(colName[i], colomns);
					if ((inputTypes[n].equals("int") && elements[i].matches("-?\\d+"))
							|| (inputTypes[n].equals("varchar") && !elements[i].matches("\\d+"))
							|| (inputTypes[n].equals("float") && elements[i].matches("-?\\d+([.]\\d+)?"))
							|| (inputTypes[n].equals("date")
									&& elements[j].matches("\\d{4}-([0][0-9]|[1][0-2])-([0-2][0-9]|[3][0-1])"))) {
						Row.replace(colName[i], elements[i]);
						if (notrepeated) {
							updatecounter++;
							notrepeated = false;
						}
					} else {
						 System.out.println("Update values don't match Colomns Data-type");
						valid = false;
					}
				}
			}
			if (valid) {
				FileWriter file1;

				file1 = new FileWriter(e.getDrPath() + File.separator + DB_name + File.separator + T_name + ".json");

				file1.write(new GsonBuilder().setPrettyPrinting().create()
						.toJson(new JsonParser().parse(Loaded.toJSONString())));
				file1.flush();
				file1.close();
			}
		} catch (Exception e) {

		}
	}

	public List<List<String>> TableToList(String dtbname, String tablename, String[] elem) {
		List<String> rowList = new ArrayList<String>();
		List<List<String>> rowsList = new ArrayList<List<String>>();
		readColumns(dtbname, tablename);
		JSONParser parser = new JSONParser();

		Object obj;
		try {
			obj = parser.parse(new InputStreamReader(new FileInputStream(
					e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".json")));

			JSONObject Loaded = (JSONObject) obj;
			JSONArray Table = (JSONArray) Loaded.get(tablename);
			JSONObject Rows = (JSONObject) Table.get(0);
			for (int j = 0; j < Rows.size(); j++) {
				JSONArray Rowarr = (JSONArray) Rows.get("Row" + Integer.toString(j));
				JSONObject Row = (JSONObject) Rowarr.get(0);
				for (int i = 0; i < elem.length; i++) {
					rowList.add(Row.get(elem[i]).toString());
				}

				rowsList.add(rowList);
				rowList = new ArrayList<String>();
			}
		} catch (Exception e1) {

		}
		return rowsList;

	}

	public List<List<String>> TableToList_WithWhere(String dtbname, String tablename, String[] colomnName,
			String colname, String val, String sign) {
		readColumns(dtbname, tablename);

		List<List<String>> rowsList = new ArrayList<List<String>>();
		List<String> row = new ArrayList<String>();

		boolean checking = false;
		JSONParser parser = new JSONParser();

		Object obj;
		try {
			obj = parser.parse(new InputStreamReader(new FileInputStream(
					e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".json")));

			JSONObject Loaded = (JSONObject) obj;
			JSONArray Table = (JSONArray) Loaded.get(tablename);
			JSONObject Rows = (JSONObject) Table.get(0);
			int m = validate.indexOfColType(colname, colomns);
			for (int j = 0; j < Rows.size(); j++) {
				JSONArray Rowarr = (JSONArray) Rows.get("Row" + Integer.toString(j));
				JSONObject Row = (JSONObject) Rowarr.get(0);

				String s = Row.get(colname).toString();
				if (inputTypes[m].equals("varchar")) {
					if (sign.equals("=") && s.equals(val)) {
						checking = true;
					} else if (sign.equals(">") && (s.compareTo(val) > 0)) {
						checking = true;
					} else if (sign.equals("<") && (val.compareTo(s) > 0)) {
						checking = true;
					}
				} else if (inputTypes[m].equals("float") || inputTypes[m].equals("int")) {
					if (sign.equals("=") && Float.parseFloat(s) == Float.parseFloat(val)) {
						checking = true;
					} else if (sign.equals(">") && Float.parseFloat(s) > Float.parseFloat(val)) {
						checking = true;
					} else if (sign.equals("<") && Float.parseFloat(s) < Float.parseFloat(val)) {
						checking = true;
					}

				} else if (inputTypes[m].equals("date")) {

					String[] d1 = s.split("-");
					String[] d2 = val.split("-");

					java.sql.Date dateOne = new java.sql.Date(Integer.parseInt(d1[0]), Integer.parseInt(d1[1]),
							Integer.parseInt(d1[2]));
					java.sql.Date dateTwo = new java.sql.Date(Integer.parseInt(d2[0]), Integer.parseInt(d2[1]),
							Integer.parseInt(d2[2]));

					if (sign.equals("=") && dateOne.equals(dateTwo)) {
						checking = true;
					} else if (sign.equals(">") && dateOne.after(dateTwo)) {
						checking = true;
					} else if (sign.equals("<") && dateOne.before(dateTwo)) {
						checking = true;
					}
				}
				if (checking) {
					for (int i = 0; i < colomnName.length; i++) {

						row.add(Row.get(colomnName[i]).toString());

					}
					rowsList.add(row);
					row = new ArrayList<String>();
					checking = false;
				}
			}

		} catch (Exception e) {

		}
		return rowsList;
	}

	public void alterDrop_json(String dtbname, String tablename, String colName) {
		updatecounter = 0;
		readColumns(dtbname, tablename);
		boolean Deleted = false;
		JSONParser parser = new JSONParser();

		Object obj;
		try {
			obj = parser.parse(new InputStreamReader(new FileInputStream(
					e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".json")));

			JSONObject Loaded = (JSONObject) obj;
			JSONArray Colomns = (JSONArray) Loaded.get("Colomns");
			JSONObject Colomn = (JSONObject) Colomns.get(0);
			JSONArray Table = (JSONArray) Loaded.get(tablename);
			JSONObject Rows = (JSONObject) Table.get(0);
			if (!Colomn.isEmpty()) {
				int size = Colomn.size();
				for (int i = 1; i <= size; i++) {
					String s = Colomn.get("Colomn" + Integer.toString(i)).toString().split(" ")[0];
					if (Deleted) {
						// System.out.println("Colomn" + Integer.toString(i -
						// 1));
						Colomn.put("Colomn" + Integer.toString(i - 1), Colomn.get("Colomn" + Integer.toString(i)));
						Colomn.remove("Colomn" + Integer.toString(i));
					}
					if (s.equals(colName)) {
						Colomn.remove("Colomn" + Integer.toString(i));
						Deleted = true;
					}

				}
				if (!Rows.isEmpty()) {
					for (int i = 0; i < Rows.size(); i++) {
						JSONObject Row = (JSONObject) ((JSONArray) Rows.get("Row" + Integer.toString(i))).get(0);
						for (int j = 0; j < Row.size(); j++) {
							Row.remove(colName);
						}
					}
				}

			}
			FileWriter file1;
			updatecounter = index;
			file1 = new FileWriter(e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".json");

			file1.write(new GsonBuilder().setPrettyPrinting().create()
					.toJson(new JsonParser().parse(Loaded.toJSONString())));
			file1.flush();
			file1.close();
		} catch (Exception e) {
		}
	}

	public void alterAdd_json(String dtbname, String tablename, String colName, String colType) {
		updatecounter = 0;
		readColumns(dtbname, tablename);

		JSONParser parser = new JSONParser();

		Object obj;
		try {
			obj = parser.parse(new InputStreamReader(new FileInputStream(
					e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".json")));

			JSONObject Loaded = (JSONObject) obj;
			JSONArray Colomns = (JSONArray) Loaded.get("Colomns");
			JSONObject Colomn = (JSONObject) Colomns.get(0);
			Colomn.put("Colomn" + Integer.toString(Colomn.size() + 1), colName + " " + colType);
			JSONArray Table = (JSONArray) Loaded.get(tablename);
			JSONObject Rows = (JSONObject) Table.get(0);

			for (int i = 0; i < Rows.size(); i++) {
				JSONObject Row = (JSONObject) ((JSONArray) Rows.get("Row" + Integer.toString(i))).get(0);
				for (int j = 0; j < Row.size(); j++) {
					Row.put(colName, "null");
				}
			}

			FileWriter file1;
			updatecounter = index;
			file1 = new FileWriter(e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".json");

			file1.write(new GsonBuilder().setPrettyPrinting().create()
					.toJson(new JsonParser().parse(Loaded.toJSONString())));
			file1.flush();
			file1.close();
		} catch (Exception e) {
		}
	}
}