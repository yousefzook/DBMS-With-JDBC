package jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import finePrint.Block;
import finePrint.Board;
import finePrint.TableView;
import json.Json;
import xml.Xml;

public class PrintResultSet {
	static Xml xml = new Xml();
	Json json = new Json();
	private static List<List<String>> selected = xml.getData();
	public static String[] headers = xml.getCol();
	private static int colCount = 0;

	public PrintResultSet() {

	}

	public int getColCount() {
		return colCount;
	}

	public List<List<String>> getSelected() {
		return selected;
	}

	public String[] getHeaders() {
		return headers;
	}

	public void finePrint_table(String dtbname, String tablename, String e[], String File_Type) {
		List<List<String>> rowsList = new ArrayList<List<String>>();
		switch (File_Type) {
		case "xml":
			rowsList = xml.TableToList(dtbname, tablename, e);

			break;
		case "json":
			rowsList = json.TableToList(dtbname, tablename, e);

			break;
		}

		finePrint_WithWhere(rowsList, e);

	}

	public void finePrint_WithWhere(List<List<String>> rows, String header[]) {
		selected = rows;
		headers = header;
		colCount = header.length;
		List<String> headersList = new ArrayList<String>();
		List<Integer> colAlignList = new ArrayList<Integer>();

		for (int i = 0; i < header.length; i++) {
			headersList.add(header[i]);
		}
		for (int j = 0; j < header.length; j++) {
			colAlignList.add(Block.DATA_CENTER);
		}
		Board board = new Board(75);
		if (rows.size() == 0) {
			 System.out.println(" No Result ");
			selected = null;
		} else {
			TableView table = new TableView(board, 75, headersList, rows);
			table.setColAlignsList(colAlignList);
			Block tableBlock = table.tableToBlocks();
			board.setInitialBlock(tableBlock);
			board.build();
			String tableString = board.getPreview();
			 System.out.println(tableString);
		}
	}

	public void print_selected_with_where(String dtbname, String tablename, String columnname[], String colname,
			String val, String sign, String File_Type) {
		List<List<String>> rowsList = new ArrayList<List<String>>();
		switch (File_Type) {
		case "xml":
			rowsList = xml.TableToList_WithWhere(dtbname, tablename, columnname, colname, val, sign);

			break;
		case "json":
			rowsList = json.TableToList_WithWhere(dtbname, tablename, columnname, colname, val, sign);

			break;
		}

		finePrint_WithWhere(rowsList, columnname);

	}

	public void PrintDistinct(String dtbname, String tablename, String[] colomns, String wherecol, String whereval,
			String sign, String File_Type) {

		List<List<String>> rowsList = new ArrayList<List<String>>();
		List<List<String>> distinctReversed = new ArrayList<List<String>>();
		List<List<String>> distinct = new ArrayList<List<String>>();

		if (tablename != null && wherecol == null && whereval == null && sign == null) {
			if (File_Type.equals("xml"))
				rowsList = xml.TableToList(dtbname, tablename, colomns);
			else if (File_Type.equals("json"))
				rowsList = json.TableToList(dtbname, tablename, colomns);
		} else if (tablename != null && wherecol != null && whereval != null && sign != null) {
			if (File_Type.equals("xml"))
				rowsList = xml.TableToList_WithWhere(dtbname, tablename, colomns, wherecol, whereval, sign);
			else if (File_Type.equals("json"))
				rowsList = json.TableToList_WithWhere(dtbname, tablename, colomns, wherecol, whereval, sign);
		}

		for (int i = rowsList.size() - 1; i > -1; i--) {
			boolean different = true;
			for (int j = i - 1; j > -1; j--) {
				if (rowsList.get(i).equals(rowsList.get(j))) {
					different = false;
					break;
				}
			}
			if (different) {
				distinctReversed.add(rowsList.get(i));
			}
		}
		int len = distinctReversed.size() - 1;
		for (int i = 0; i < distinctReversed.size(); i++) {
			distinct.add(distinctReversed.get(len - i));
		}

		finePrint_WithWhere(distinct, colomns);

	}

}
