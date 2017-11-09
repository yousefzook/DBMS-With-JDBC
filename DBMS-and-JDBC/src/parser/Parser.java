package parser;

import java.sql.SQLException;

import dbms.DataBase;
import dbms.Engine;
import dbms.Table;

public class Parser {

	private String query;
	private String[] statementWords;
	private boolean continueReading;
	private int wordsLength;
	Engine engine = new Engine();
	DataBase dtb = new DataBase();
	Table table = new Table();

	public Parser(String s) throws SQLException {
		this.continueReading = true;
		wordsLength = 0;
		statementWords = new String[30];
		this.query = s;
		validate();
	}

	public String getquery() {
		return query;
	}

	public void validate() throws SQLException {
		if (!query.isEmpty()) {
			query = query.replace("\n", " ");
			query = query.trim();
			String[] statementsArray = query.split(";");
			for (String statement : statementsArray) {
				if (continueReading) {
					statement = statement.replace("(", " ( ");
					statement = statement.replace(")", " ) ");
					statement = statement.replace(",", " , ");
					statement = statement.replace("=", " = ");
					statement = statement.replace(">", " > ");
					statement = statement.replace("<", " < ");
					statement = statement.replaceAll("\\s+", " ");
					statement = statement.trim();
					splitStatement(statement);
					statementValidation(statement.toLowerCase(), statementWords);
				} else {
					break;
				}

			} // end statements arrays for loop.
		} // end if
	}

	public void splitStatement(String st) {
		int j = 0, start = 0;
		boolean closeSingleQuote = false;
		boolean closeDoubleQuote = false;
		wordsLength = 0;
		for (int i = 0; i <= st.length(); i++) {
			if ((i == st.length() || st.charAt(i) == ' ') && !closeSingleQuote && !closeDoubleQuote) {
				statementWords[j] = st.substring(start, i);
				if (!(statementWords[j].charAt(statementWords[j].length() - 1) == '\''
						|| statementWords[j].charAt(statementWords[j].length() - 1) == '\"'))
					statementWords[j] = statementWords[j].toLowerCase();
				else if (statementWords[j].charAt(statementWords[j].length() - 1) == '\''
						|| statementWords[j].charAt(statementWords[j].length() - 1) == '\"')
					statementWords[j] = statementWords[j].substring(0, statementWords[j].length() - 1);

				j++;
				start = i + 1;
			} else if (i != st.length() && st.charAt(i) == '\'') {
				if (closeSingleQuote)
					start++;
				closeSingleQuote = !closeSingleQuote;
			} else if (i != st.length() && st.charAt(i) == '\"') {
				if (closeDoubleQuote)
					start++;
				closeDoubleQuote = !closeDoubleQuote;
			} // end else if.
		} // end for statement.
		wordsLength = j;
	}// end function.

	private void statementValidation(String statement, String statementWords[]) throws SQLException {
		switch (statementWords[0]) {
		case "create":
			createValidation(statement, statementWords);
			break;
		case "insert":
			insertValidation(statement, statementWords);
			break;
		case "delete":
			deleteValidation(statement, statementWords);
			break;
		case "drop":
			dropValidation(statement, statementWords);
			break;
		case "select":
			selectValidation(statement, statementWords);
			break;
		case "update":
			updateValidation(statement, statementWords);
			break;
		case "alter":
			alterValidation(statement, statementWords);
			break;
		case "use":
			if (checkTableName(statementWords[1]))
				engine.setLastDbName(statementWords[1]);
			else {
				System.out.println("wrong query");
				// throw new SQLException("error query");
			}
			break;
		default:
			System.out.println("No such query exists");
			// throw new SQLException("No such query exists");
		}// end switch arrays for loop.
	}

	private boolean checkTableName(String name) {

		return name.matches("([a-zA-Z])([a-zA-Z0-9_])*([.][a-zA-Z][a-zA-Z0-9_]*)?");
	}

	private void createValidation(String statement, String statementWords[]) throws SQLException {
		String names = "";
		String types = "";
		String x = "(create{1})(\\s+table\\s+\\w+([.]\\w+)?)(\\s+[(])(\\s+\\w+\\s+\\w+\\s+)(,\\s+\\w+\\s+\\w+\\s+)*([)])\\s*";
		if (statement.matches("(create{1})(\\s+database\\s+\\w+)\\s*")) {
			if (checkTableName(statementWords[2])) {
				engine.createDataBase(statementWords[2]);
			} else {
				System.out.println("Invalid table name.");
				// throw new SQLException("Invalid table name.");
			}
		} else if (statement.matches(x)) {
			if (checkTableName(statementWords[2])) {
				for (int i = 4; i < wordsLength; i += 3) {
					names = names + statementWords[i] + " ";
					types = types + statementWords[i + 1] + " ";
				}
				String[] nameArray = statementWords[2].split("\\.");
				if (nameArray.length == 1) {
					dtb.createTable(null, nameArray[0], names, types);
				} else
					dtb.createTable(nameArray[0], nameArray[1], names, types);
			} else {
				System.out.println("Invalid table name.");
				// throw new SQLException("invalid table name");
			}
		} else {
			System.out.println(" Syntax error in CREATE query.");
			// throw new SQLException(" Syntax error in CREATE query.");
		}
	}

	private void insertValidation(String statement, String statementWords[]) throws SQLException {
		int valStartIndex = 0, valuesLength = 0, colsLength = 0;
		String cols = "", values = "";
		if (statement.matches(
				"(insert{1})(\\s+into{1}\\s+\\w+([.]\\w+)?\\s+values{1})(\\s+[(])(\\s+(('\\w+(\\s*\\w*)*')|(\"\\w+(\\s*\\w*)*\")|\\d+([.]\\d+)?|'\\d{4}-([0][0-9]|[1][0-2])-([0-2][0-9]|[3][0-1])')\\s+)"
						+ "(,\\s+(('\\w+(\\s*\\w*)*')|(\"\\w+(\\s*\\w*)*\")|(-)?\\d+([.]\\d+)?|'\\d{4}-([0][1-9]|[1][0-2])-([0][1-9]|[1-2][0-9]|[3][0-1])')\\s+)*([)])\\s*")) {
			if (checkTableName(statementWords[2])) {
				for (int i = 5; i < wordsLength - 1; i += 2) {
					values = values + statementWords[i] + ",";
				}
				String[] nameArray = statementWords[2].split("\\.");
				values = values.substring(0, values.length() - 1);
				if (nameArray.length == 1) {
					table.insertIntoTable(null, nameArray[0], null, values);
				} else {
					table.insertIntoTable(nameArray[0], nameArray[1], null, values);
				}
			} else
				throw new SQLException(" Invalid table name.");
		} else if (statement.matches(
				"(insert{1})(\\s+into{1}\\s+\\w+([.]\\w+)?\\s+(([(])(\\s+\\w+\\s+)(,\\s+\\w+\\s+)*([)])\\s+)values{1})(\\s+[(])(\\s+(('\\w+(\\s*\\w*)*')|(\"\\w+(\\s*\\w*)*\")|(-)?\\d+([.]\\d+)?|'\\d{4}-([0][0-9]|[1][0-2])-([0-2][0-9]|[3][0-1])')\\s+)"
						+ "(,\\s+(('\\w+(\\s*\\w*)*')|(\"\\w+(\\s*\\w*)*\")|(-)?\\d+([.]\\d+)?|'\\d{4}-([0][1-9]|[1][0-2])-([0][1-9]|[1-2][0-9]|[3][0-1])')\\s+)*([)])\\s*")) {
			if (checkTableName(statementWords[2])) {
				for (int i = 4; i < wordsLength - 1; i += 2) {
					if (statementWords[i + 1].equals(")")) {
						cols = cols + statementWords[i] + " ";
						valStartIndex = i + 4;
						colsLength++;
						break;
					}
					colsLength++;
					cols = cols + statementWords[i] + " ";
				}
				for (int i = valStartIndex; i < wordsLength - 1; i += 2) {
					valuesLength++;
					values = values + statementWords[i] + ",";
				}
				if (colsLength == valuesLength) {
					values = values.substring(0, values.length() - 1);
					String[] nameArray = statementWords[2].split("\\.");
					if (nameArray.length == 1) {
						table.insertIntoTable(null, nameArray[0], cols, values);
					} else {
						table.insertIntoTable(nameArray[0], nameArray[1], cols, values);
					}
				} else {
					{
						System.out.println("Number of columns dosen't match number of values.");
						// throw new SQLException("Number of columns dosen't
						// match number of values.");
					}
				}
			} else {
				System.out.println(" Invalid table name.");
				// throw new SQLException(" Invalid table name.");
			}
		} else {
			System.out.println("Syntax error in INSERT query.");
			// throw new SQLException("Syntax error in INSERT query.");
		}
	}

	private void deleteValidation(String statement, String statementWords[]) throws SQLException {

		if (statement.matches("(delete{1})\\s([*]?\\s*from\\s+\\w+([.]\\w+)?)\\s*")) {

			if (checkTableName(statementWords[wordsLength - 1])) {
				String[] nameArray = statementWords[wordsLength - 1].split("\\.");
				if (nameArray.length == 1) {
					table.deleteFromTable(null, nameArray[0], null, null, null, null);
				} else {
					table.deleteFromTable(nameArray[0], nameArray[1], null, null, null, null);
				}
			} else {
				System.out.println("Invalid table name.");
				// throw new SQLException();
			}

		} else if (statement.matches(
				"(delete{1})(\\s+from\\s+\\w+([.]\\w+)?)\\s+(where{1})\\s+((\\w+)(\\s*[=><]\\s*(('\\w+(\\s*\\w*)*')|(\"\\w+(\\s*\\w*)*\")|-?\\d+([.]\\d+)?|'\\d{4}-([0][1-9]|[1][0-2])-([0][1-9]|[1-2][0-9]|[3][0-1])'))|true)\\s*")) {
			if (checkTableName(statementWords[2])) {
				String[] nameArray = statementWords[2].split("\\.");
				if (nameArray.length == 1) {
					if (statementWords[wordsLength - 1].equals("true"))
						table.deleteFromTable(null, nameArray[0], null, null, null, null);
					else
						table.deleteFromTable(null, nameArray[0], null, statementWords[4], statementWords[6],
								statementWords[5]);

				} else {
					if (statementWords[wordsLength - 1].equals("true"))
						table.deleteFromTable(nameArray[0], nameArray[1], null, null, null, null);
					else {
						// System.out.println(statementWords[4] + "" +
						// statementWords[6] + "" + statementWords[5]);
						table.deleteFromTable(nameArray[0], nameArray[1], null, statementWords[4], statementWords[6],
								statementWords[5]);
					}
				}

			} else {
				System.out.println("Invalid table name.");
				// throw new SQLException("Invalid table name.");
			}
		} else {
			System.out.println("Syntax error in DELETE query.");
			// throw new SQLException();
		}
	}

	private void dropValidation(String statement, String statementWords[]) throws SQLException {
		if (statement.matches("(drop)\\s+(\\w+\\s+\\w+([.]\\w+)?)\\s*")) {
			if (statementWords[1].equals("database")) {
				if (checkTableName(statementWords[1]))
					engine.dropDataBase(statementWords[2]);
				else {
					System.out.println("Invalid table name.");
					// throw new SQLException();
				}
			} else if (statementWords[1].equals("table")) {
				if (checkTableName(statementWords[1])) {
					String[] nameArray = statementWords[2].split("\\.");
					if (nameArray.length == 1) {
						dtb.dropTable(null, nameArray[0]);
					} else {
						dtb.dropTable(nameArray[0], nameArray[1]);
					}
				} else {
					System.out.println("Invalid table name.");
					// throw new SQLException("Invalid table name.");
				}
			} else {
				{
					System.out.println("Syntax error in DROP query.");
					// throw new SQLException("Syntax error in DROP query.");
				}
			}
		} else {
			System.out.println("Uncompleted DROP query.");
			// throw new SQLException("Uncompleted DROP query.");
		}
	}

	private void selectValidation(String statement, String statementWords[]) throws SQLException {

		if (statement.matches("(select{1})(\\s+[*])(\\s+from\\s+\\w+([.]\\w+)?)\\s*")) {
			if (checkTableName(statementWords[3])) {
				String[] nameArray = statementWords[3].split("\\.");
				if (nameArray.length == 1) {
					table.selectFromTable(null, nameArray[0], null, null, null, null);
				} else {
					table.selectFromTable(nameArray[0], nameArray[1], null, null, null, null);
				}
			} else {
				System.out.println("Invalid table name.");
				// throw new SQLException("Invalid table name.");
			}
		} else if (statement.matches(
				"(select{1})(\\s+[*])(\\s+from\\s+\\w+([.]\\w+)?)\\s+(where{1})\\s+((\\w+)(\\s*[=><]\\s*(('\\w+(\\s*\\w*)*')|(\"\\w+(\\s*\\w*)*\")|-?\\d+([.]\\d+)?|('\\d{4}-([0][1-9]|[1][0-2])-([0][1-9]|[1-2][0-9]|[3][0-1])')))|true)\\s*")) {
			// select all with where
			if (checkTableName(statementWords[3])) {
				String[] nameArray = statementWords[3].split("\\.");
				if (nameArray.length == 1) {
					table.selectFromTable(null, nameArray[0], null, statementWords[5], statementWords[7],
							statementWords[6]);
				} else {
					table.selectFromTable(nameArray[0], nameArray[1], null, statementWords[5], statementWords[7],
							statementWords[6]);
				}
			} else {
				System.out.println("Invalid table name.");
				// throw new SQLException("Invalid table name.");
			}
		} else if (statement.matches("(select{1})(\\s+\\w+)(\\s*,\\s*\\w+)*\\s+(from{1})(\\s+\\w+([.]\\w+)?)\\s*")) {
			// select without where
			selectWtihoutWhere(statement, statementWords);
		} else if (statement.matches(
				"(select{1})(\\s+\\w+)(\\s*,\\s*\\w+)*\\s+(from{1})(\\s+\\w+([.]\\w+)?)\\s+(where{1})(\\s+\\w+)(\\s*[=><]\\s*"
						+ "(('\\w+(\\s*\\w*)*')|(\"\\w+(\\s*\\w*)*\")|-?\\d+([.]\\d+)?|'\\d{4}-([0][1-9]|[1][0-2])-([0][1-9]|[1-2][0-9]|[3][0-1])'))\\s*")) {
			// select with where
			selectWtihWhere(statement, statementWords);
		} else if (statement.contains("distinct")) {
			selectDistinct(statement, statementWords);
		} else {
			System.out.println("Syntax error in SELECT query.");
			// throw new SQLException("Syntax error in SELECT query.");
		}
	}

	private void updateValidation(String statement, String statementWords[]) throws SQLException {
		String colNames = "";
		String values = "";
		if (statement.matches(
				"(update{1})\\s+(\\w+([.]\\w+)?\\s+set\\s+)(\\w+\\s*[=]\\s*(('\\w+\\s*\\w*')|(\"\\w+(\\s*\\w*)*\")|-?\\d+([.]\\d+)?|'\\d{4}-([0][0-9]|[1][0-2])-([0-2][0-9]|[3][0-1])'))"
						+ "(\\s*,\\s*\\w+\\s*[=]\\s*(('\\w+(\\s*\\w*)*')|-?\\d+([.]\\d+)?|'\\d{4}-([0][1-9]|[1][0-2])-([0][1-9]|[1-2][0-9]|[3][0-1])'))*\\s*")) {
			if (checkTableName(statementWords[1])) {
				for (int i = 3; i < wordsLength - 2; i += 4) {
					colNames = colNames + statementWords[i] + " ";
					values = values + statementWords[i + 2] + ",";
				}
				String[] nameArray = statementWords[1].split("\\.");
				values = values.substring(0, values.length() - 1);
				if (nameArray.length == 1) {
					table.updateTable(null, nameArray[0], colNames, values, null, null, null);
				} else {
					table.updateTable(nameArray[0], nameArray[1], colNames, values, null, null, null);
				}
			} else {
				System.out.println("Invalid table name.");
				// throw new SQLException("Invalid table name.");
			}

		} else if (statement.matches(
				"(update{1})\\s+(\\w+([.]\\w+)?\\s+set\\s+)(\\w+\\s*[=]\\s*(('\\w+\\s*\\w*')|(\"\\w+(\\s*\\w*)*\")|-?\\d+([.]\\d+)?|'\\d{4}-([0][0-9]|[1][0-2])-([0-2][0-9]|[3][0-1])'))(\\s*,\\s*\\w+\\s*[=]\\s*"
						+ "(('\\w+\\s*\\w*')|(\"\\w+(\\s*\\w*)*\")|-?\\d+([.]\\d+)?|'\\d{4}-([0][0-9]|[1][0-2])-([0-2][0-9]|[3][0-1])'))*\\s*(where{1})(\\s+\\w+)(\\s*[=><]\\s*(('\\w+(\\s*\\w*)*')|(\"\\w+(\\s*\\w*)*\")|-?\\d+([.]\\d+)?|'\\d{4}-([0][1-9]|[1][0-2])-([0][1-9]|[1-2][0-9]|[3][0-1])'))\\s*")) {
			if (checkTableName(statementWords[1])) {
				for (int i = 3; i < wordsLength - 6; i += 4) {
					colNames = colNames + statementWords[i] + " ";
					values = values + statementWords[i + 2] + ",";
				}
				String[] nameArray = statementWords[1].split("\\.");
				values = values.substring(0, values.length() - 1);
				if (nameArray.length == 1) {
					table.updateTable(null, nameArray[0], colNames, values, statementWords[wordsLength - 3],
							statementWords[wordsLength - 1], statementWords[wordsLength - 2]);
				} else {
					table.updateTable(nameArray[0], nameArray[1], colNames, values, statementWords[wordsLength - 3],
							statementWords[wordsLength - 1], statementWords[wordsLength - 2]);
				}
			} else {
				System.out.println("Invalid table name.");
				// throw new SQLException("Invalid table name.");
			}
		} else {
			System.out.println("Syntax error in UPDATE query.");
			// throw new SQLException("Syntax error in UPDATE query.");
		}
	}

	private void selectWtihWhere(String statement, String statementWords[]) throws SQLException {

		if (checkTableName(statementWords[wordsLength - 5])) {

			String colNames = "";
			for (int i = 1; i < wordsLength - 6; i += 2)
				colNames = colNames + statementWords[i] + " ";

			String[] nameArray = statementWords[wordsLength - 5].split("\\.");
			if (nameArray.length == 1) {

				table.selectFromTable(null, nameArray[0], colNames, statementWords[wordsLength - 3],
						statementWords[wordsLength - 1], statementWords[wordsLength - 2]);
			} else {

				table.selectFromTable(nameArray[0], nameArray[1], colNames, statementWords[wordsLength - 3],
						statementWords[wordsLength - 1], statementWords[wordsLength - 2]);
			}

		} else {
			System.out.println("Invalid table name.");
			// throw new SQLException("Invalid table name.");
		}
	}

	private void selectWtihoutWhere(String statement, String statementWords[]) throws SQLException {

		if (checkTableName(statementWords[wordsLength - 1])) {
			String colNames = "";
			for (int i = 1; i < wordsLength - 2; i += 2)
				colNames = colNames + statementWords[i] + " ";
			String[] nameArray = statementWords[wordsLength - 1].split("\\.");
			if (nameArray.length == 1) {
				table.selectFromTable(null, nameArray[0], colNames, null, null, null);
			} else {
				table.selectFromTable(nameArray[0], nameArray[1], colNames, null, null, null);
			}
		} else {
			System.out.println("Invalid table name.");
			// throw new SQLException("Invalid table name.");
		}
	}

	private void alterValidation(String statement, String statementWords[]) throws SQLException {

		if (statement.matches("alter\\s+table\\s+\\w+([.]\\w+)?\\s+add\\s+\\w+\\s+\\w+\\s*")) {
			if (checkTableName(statementWords[2])) {

				String[] nameArray = statementWords[2].split("\\.");
				if (nameArray.length == 1) {
					table.alterTableAdd(null, nameArray[0], statementWords[wordsLength - 2],
							statementWords[wordsLength - 1]);
				} else {
					table.alterTableAdd(nameArray[0], nameArray[1], statementWords[wordsLength - 2],
							statementWords[wordsLength - 1]);
				}
			} else {
				System.out.println("invalid table name !. ");
				// throw new SQLException("invalid table name !. ");
			}

		} else if (statement.matches("alter\\s+table\\s+\\w+([.]\\w+)?\\s+drop\\s+column\\s+\\w+\\s*")) {
			if (checkTableName(statementWords[2])) {

				String[] nameArray = statementWords[2].split("\\.");
				if (nameArray.length == 1) {
					table.alterTableDrop(null, nameArray[0], statementWords[wordsLength - 1]);
				} else {
					table.alterTableDrop(nameArray[0], nameArray[1], statementWords[wordsLength - 1]);
				}
			} else {
				System.out.println("invalid table name !. ");
				// throw new SQLException("invalid table name !. ");
			}
		} else {
			{
				System.out.println("Syntax error in Alter query.");
				// throw new SQLException("Syntax error in Alter query.");
			}
		}

	}

	public void selectDistinct(String statement, String statementWords[]) throws SQLException {

		if (statement.matches(
				"(select{1}\\s+distinct)(\\s+)(((\\w+)(\\s*,\\s*\\w+)*)|[*])\\s+(from{1})(\\s+\\w+([.]\\w+)?)\\s+(where{1})(\\s+\\w+)(\\s*[=><]\\s*"
						+ "(('\\w+(\\s*\\w*)*')|(\"\\w+(\\s*\\w*)*\")|-?\\d+([.]\\d+)?|'\\d{4}-([0][1-9]|[1][0-2])-([0][1-9]|[1-2][0-9]|[3][0-1])'))\\s*")) {
			// with where
			if (statement.contains("*")) {
				selectDistinctWithWhere(statement, statementWords, true);
			} else
				selectDistinctWithWhere(statement, statementWords, false);
		} else if (statement.matches(
				"(select{1}\\s+distinct)(\\s+)(((\\w+)(\\s*,\\s*\\w+)*)|[*])\\s+(from{1})(\\s+\\w+([.]\\w+)?)\\s*")) {
			// without where
			if (statement.contains("*")) {
				selectDistinctWithoutWhere(statement, statementWords, true);
			} else {
				selectDistinctWithoutWhere(statement, statementWords, false);
			}
		}
	}

	private void selectDistinctWithWhere(String statement, String[] statementWords, boolean isUniversal)
			throws SQLException {

		if (checkTableName(statementWords[wordsLength - 5])) {
			String colNames = "";

			if (isUniversal)
				colNames = null;
			else {
				for (int i = 2; i < wordsLength - 6; i += 2)
					colNames = colNames + statementWords[i] + " ";
			}
			String[] nameArray = statementWords[wordsLength - 5].split("\\.");
			if (nameArray.length == 1) {
				table.selectDistinct(null, nameArray[0], colNames, statementWords[wordsLength - 3],
						statementWords[wordsLength - 1], statementWords[wordsLength - 2]);
			} else {
				table.selectDistinct(nameArray[0], nameArray[1], colNames, statementWords[wordsLength - 3],
						statementWords[wordsLength - 1], statementWords[wordsLength - 2]);
			}

		} else {
			System.out.println("Invalid table name.");
			// throw new SQLException("Invalid table name.");
		}

	}

	private void selectDistinctWithoutWhere(String statement, String[] statementWords, boolean isUniversal)
			throws SQLException {

		if (checkTableName(statementWords[wordsLength - 1])) {
			String colNames = "";
			if (isUniversal)
				colNames = null;
			else {
				for (int i = 2; i < wordsLength - 2; i += 2)
					colNames = colNames + statementWords[i] + " ";
			}
			String[] nameArray = statementWords[wordsLength - 1].split("\\.");
			if (nameArray.length == 1) {
				table.selectDistinct(null, nameArray[0], colNames, null, null, null);
			} else {
				table.selectDistinct(nameArray[0], nameArray[1], colNames, null, null, null);
			}

		} else {
			System.out.println("Invalid table name.");
			// throw new SQLException("Invalid table name.");
		}
	}

}
