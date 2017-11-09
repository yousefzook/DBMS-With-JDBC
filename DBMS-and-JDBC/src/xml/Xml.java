package xml;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dbms.Engine;
import dtd.DTDGenerator;
import parser.Validate;

public class Xml {

	private int index;
	private String[] colomns, inputTypes;
	private List<List<String>> rowsList;
	private boolean[] Ch;
	Validate validate = new Validate();
	Engine e = new Engine();
	DTDGenerator dtd = new DTDGenerator();
	int updatecounter;

	public int getUpdatecounter() {
		return updatecounter;
	}

	public Xml() {

		index = 1;
	}

	public Document readDocument(String FilePath) {
		try {
			File fXmlFile = new File(FilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			return doc;
		}

		catch (Exception e) {
			return null;
		}
	}

	public void transform(Document doc, String path) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path + ".xml"));
			transformer.transform(source, result);
			// dtd.generateDtd(path);
		} catch (Exception e) {
		}
	}

	public void readTableData(String dtbname, String Tname) {
		ArrayList<String> Columns = new ArrayList<String>();
		ArrayList<String> Types = new ArrayList<String>();
		int i;
		File file = new File(e.getDrPath() + File.separator + dtbname + File.separator + Tname + ".xml");
		if (file.exists()) {
			Document doc = readDocument(e.getDrPath() + File.separator + dtbname + File.separator + Tname + ".xml");
			NodeList s = doc.getElementsByTagName(Tname).item(0).getChildNodes();
			int len = s.getLength();
			for (i = 1; i < len - 1; i += 2) {
				index = s.item(i).getChildNodes().getLength() / 2 + 1;
				Columns.add(s.item(i).getNodeName());
				Types.add(s.item(i).getAttributes().item(0).getNodeValue());

			}
			colomns = new String[Columns.size()];
			inputTypes = new String[Types.size()];
			for (i = 0; i < Columns.size(); i++) {
				colomns[i] = Columns.get(i);
				inputTypes[i] = Types.get(i);

			}
			// read data

			List<String> rowList = new ArrayList<String>();
			rowsList = new ArrayList<List<String>>();
			NodeList nlists[] = new NodeList[colomns.length];
			Node Node1[] = new Node[colomns.length];
			Element eElement1[] = new Element[colomns.length];
			for (i = 0; i < colomns.length; i++) {
				nlists[i] = doc.getElementsByTagName(colomns[i]);
				Node1[i] = nlists[i].item(0);
				eElement1[i] = (Element) Node1[i];
			}

			for (i = 1; i < index; i++) {
				for (int j = 0; j < colomns.length; j++) {
					rowList.add(
							eElement1[j].getElementsByTagName("Index" + Integer.toString(i)).item(0).getTextContent());
				}
				rowsList.add(rowList);
				rowList = new ArrayList<String>();
			}
		} else {
			 System.out.println("No XML File TO read");

		}
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

	public List<List<String>> getData() {
		return rowsList;

	}

	@SuppressWarnings("deprecation")
	public void updateWithWhere(String dtbname, String tablename, String colName[], String elements[], String wherecol,
			String whereval, String sign) {
		updatecounter = 0;
		readTableData(dtbname, tablename);
		Document doc = readDocument(e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".xml");
		NodeList nlist1 = doc.getElementsByTagName(wherecol);
		Node lNode1 = nlist1.item(0);
		Element eElement1 = (Element) lNode1;
		NodeList nlists[] = new NodeList[colomns.length];
		Node Node1[] = new Node[colomns.length];
		Element eElement2[] = new Element[colomns.length];
		int m = validate.indexOfColType(wherecol, colomns);
		boolean valid = true;

		for (int j = 0; j < colName.length; j++) {
			nlists[j] = doc.getElementsByTagName(colName[j]);
			Node1[j] = nlists[j].item(0);
			eElement2[j] = (Element) Node1[j];
		}

		boolean checking = false;
		for (int i = 1; i < index; i++) {
			String s = eElement1.getElementsByTagName("Index" + Integer.toString(i)).item(0).getTextContent();
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

			if (checking) {
				boolean notrepeated = true;
				for (int j = 0; j < colName.length; j++) {
					int n = validate.indexOfColType(colName[j], colomns);
					if ((inputTypes[n].equals("int") && elements[j].matches("-?\\d+"))
							|| (inputTypes[n].equals("varchar") && !elements[j].matches("\\d+"))
							|| (inputTypes[n].equals("float") && elements[j].matches("-?\\d+([.]\\d+)?"))
							|| (inputTypes[n].equals("date")
									&& elements[j].matches("\\d{4}-([0][0-9]|[1][0-2])-([0-2][0-9]|[3][0-1])"))) {
						eElement2[j].getElementsByTagName("Index" + Integer.toString(i)).item(0)
								.setTextContent(elements[j]);
						if (notrepeated) {
							updatecounter++;
							notrepeated = false;
						}
					} else {
						valid = false;
						 System.out.println(" Columns do not match values data Types. \n");
					}
				}
				checking = false;
			}
		}
		if (valid)
			transform(doc, e.getDrPath() + File.separator + dtbname + File.separator + tablename);
	}

	public void updateWithoutWhere(String dtbname, String tablename, String colName[], String elements[]) {
		readTableData(dtbname, tablename);
		boolean valid = true;
		updatecounter = 0;
		Document doc = readDocument(e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".xml");
		NodeList nlists[] = new NodeList[colName.length];
		Node Node1[] = new Node[colName.length];
		Element eElement1[] = new Element[colName.length];
		for (int i = 0; i < colName.length; i++) {
			nlists[i] = doc.getElementsByTagName(colName[i]);
			Node1[i] = nlists[i].item(0);
			eElement1[i] = (Element) Node1[i];
		}

		for (int i = 1; i < index; i++) {
			boolean notrepeated = true;
			for (int j = 0; j < colName.length; j++) {
				int n = validate.indexOfColType(colName[j], colomns);

				if ((inputTypes[n].equals("int") && elements[j].matches("-?\\d+"))
						|| (inputTypes[n].equals("varchar") && !elements[j].matches("\\d+"))
						|| (inputTypes[n].equals("float") && elements[j].matches("-?\\d+([.]\\d+)?"))
						|| (inputTypes[n].equals("date")
								&& elements[j].matches("\\d{4}-([0][0-9]|[1][0-2])-([0-2][0-9]|[3][0-1])"))) {
					eElement1[j].getElementsByTagName("Index" + Integer.toString(i)).item(0)
							.setTextContent(elements[j]);
					if (notrepeated) {
						updatecounter++;
						notrepeated = false;
					}
				} else {
					valid = false;
					 System.out.println(" Columns do not match values data Types. \n");
				}
			}
		}
		if (valid)
			transform(doc, e.getDrPath() + File.separator + dtbname + File.separator + tablename);
	}

	public void Delete_table(String dtbname, String tablename) {
		readTableData(dtbname, tablename);
		Document doc = readDocument(e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".xml");
		NodeList nlists[] = new NodeList[colomns.length];
		Node Node1[] = new Node[colomns.length];
		Element eElement1[] = new Element[colomns.length];
		for (int i = 0; i < colomns.length; i++) {
			nlists[i] = doc.getElementsByTagName(colomns[i]);
			Node1[i] = nlists[i].item(0);
			eElement1[i] = (Element) Node1[i];
		}
		for (int i = 1; i < index; i++) {
			for (int j = 0; j < colomns.length; j++) {
				eElement1[j].getElementsByTagName("Index" + Integer.toString(i)).item(0).getParentNode()
						.removeChild(eElement1[j].getElementsByTagName("Index" + Integer.toString(i)).item(0));
			}
		}
		updatecounter = index - 1;
		transform(doc, e.getDrPath() + File.separator + dtbname + File.separator + tablename);

	}

	public void Delete_Colomns(String dtbname, String tablename, String columnname[]) {
		updatecounter = 0;
		readTableData(dtbname, tablename);
		Document doc = readDocument(e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".xml");
		NodeList nlists[] = new NodeList[columnname.length];
		Node Node1[] = new Node[columnname.length];
		Element eElement1[] = new Element[columnname.length];
		for (int i = 0; i < columnname.length; i++) {
			nlists[i] = doc.getElementsByTagName(columnname[i]);
			Node1[i] = nlists[i].item(0);
			eElement1[i] = (Element) Node1[i];
		}
		for (int i = 1; i < index; i++) {
			for (int j = 0; j < columnname.length; j++) {
				eElement1[j].getElementsByTagName("Index" + Integer.toString(i)).item(0).setTextContent("null");
			}
		}
		updatecounter = index - 1;
		transform(doc, e.getDrPath() + File.separator + dtbname + File.separator + tablename);

	}

	@SuppressWarnings("deprecation")
	public void Delete_allselected_with_where(String dtbname, String tablename, String colname, String val,
			String sign) {
		updatecounter = 0;
		readTableData(dtbname, tablename);
		// System.out.println(dtbname + " " + tablename);
		Document doc = readDocument(e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".xml");
		boolean Deleted = false;
		int Delete = 0;
		// System.out.println(doc + " " + colname);
		NodeList nlist1 = doc.getElementsByTagName(colname);
		Node lNode1 = nlist1.item(0);
		Element eElement1 = (Element) lNode1;
		NodeList nlists[] = new NodeList[colomns.length];
		Node Node1[] = new Node[colomns.length];
		Element eElement2[] = new Element[colomns.length];
		int m = validate.indexOfColType(colname, colomns);
		for (int j = 0; j < colomns.length; j++) {
			nlists[j] = doc.getElementsByTagName(colomns[j]);
			Node1[j] = nlists[j].item(0);
			eElement2[j] = (Element) Node1[j];
		}
		boolean checking = false;

		for (int i = 1; i < index; i++) {
			String s = eElement1.getElementsByTagName("Index" + Integer.toString(i)).item(0).getTextContent();
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
				boolean notrepeated = true;
				for (int j = 0; j < colomns.length; j++) {
					eElement2[j].getElementsByTagName("Index" + Integer.toString(i)).item(0).getParentNode()
							.removeChild(eElement2[j].getElementsByTagName("Index" + Integer.toString(i)).item(0));
					if (notrepeated) {
						updatecounter++;
						notrepeated = false;
					}
				}
				checking = false;
				Delete++;
				Deleted = true;
			} else if (((!s.equals(val)) || !(s.compareTo(val) > 0) || (val.compareTo(s) > 0)) && Deleted == true) {
				boolean notrepeated = true;
				for (int j = 0; j < colomns.length; j++) {
					doc.renameNode(eElement2[j].getElementsByTagName("Index" + Integer.toString(i)).item(0), "",
							"Index" + Integer.toString(i - Delete));

				}
			}
		}

		index -= Delete;
		transform(doc, e.getDrPath() + File.separator + dtbname + File.separator + tablename);

	}

	@SuppressWarnings("deprecation")
	public void Delete_selected_with_where(String dtbname, String tablename, String columnname[], String colname,
			String val, String sign) {
		updatecounter = 0;
		readTableData(dtbname, tablename);
		Document doc = readDocument(e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".xml");
		NodeList nlist1 = doc.getElementsByTagName(colname);
		Node lNode1 = nlist1.item(0);
		Element eElement1 = (Element) lNode1;
		NodeList nlists[] = new NodeList[columnname.length];
		Node Node1[] = new Node[columnname.length];
		Element eElement2[] = new Element[columnname.length];
		int m = validate.indexOfColType(colname, colomns);
		for (int j = 0; j < columnname.length; j++) {
			nlists[j] = doc.getElementsByTagName(columnname[j]);
			Node1[j] = nlists[j].item(0);
			eElement2[j] = (Element) Node1[j];
		}
		boolean checking = false;
		for (int i = 1; i < index; i++) {
			String s = eElement1.getElementsByTagName("Index" + Integer.toString(i)).item(0).getTextContent();
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
				boolean notrepeated = true;
				for (int j = 0; j < columnname.length; j++) {
					eElement2[j].getElementsByTagName("Index" + Integer.toString(i)).item(0).setTextContent("null");
					if (notrepeated) {
						updatecounter++;
						notrepeated = false;
					}
				}
				checking = false;
			}
		}
		transform(doc, e.getDrPath() + File.separator + dtbname + File.separator + tablename);

	}

	public void insert_xml(String DB_name, String T_name, String Colomns, String Values) throws SQLException {
		updatecounter = 0;
		readTableData(DB_name, T_name);
		Document doc;
		doc = readDocument(e.getDrPath() + File.separator + DB_name + File.separator + T_name + ".xml");
		doc.getDocumentElement().normalize();
		String ins_val[] = Values.split(",");
		if (Colomns != null) {
			String ins_col[] = Colomns.split(" ");
			boolean check = true;
			for (int i = 0; i < ins_col.length; i++) {
				if (validate.indexOfColType(ins_col[i], colomns) == -1) {
					check = false;
					break;
				}
			}

			if (check) {
				Ch = validate.Check(ins_col, colomns);
				boolean valid = true;
				for (int i = 0; i < ins_col.length; i++) {
					int n = validate.indexOfColType(ins_col[i], colomns);
					if ((inputTypes[n].equals("int") && ins_val[i].matches("-?\\d+"))
							|| (inputTypes[n].equals("varchar") && !ins_val[i].matches("\\d+"))
							|| (inputTypes[n].equals("float") && ins_val[i].matches("-?\\d+([.]\\d+)?"))
							|| (inputTypes[n].equals("date")
									&& ins_val[i].matches("\\d{4}-([0][0-9]|[1][0-2])-([0-2][0-9]|[3][0-1])"))) {

						append_xml(DB_name, T_name, ins_col[i], ins_val[i], doc);
					} else {
						valid = false;
						 System.out.println(" Columns do not match values data Types. \n");
					}
				}
				if (valid) {
					for (int i = 0; i < colomns.length; i++) {
						if (Ch[i] == false) {
							append_xml(DB_name, T_name, colomns[i], "null", doc);

						}
					}
					updatecounter = 0;
					index++;
					updatecounter = 1;
					Ch = new boolean[150];
					transform(doc, e.getDrPath() + File.separator + DB_name + File.separator + T_name);
					 System.out.println("insertion Done with column(s)");
				}

			} else {
				 System.out.println("Error, none existing columns ! . \n");
			}
		} else {
			boolean valid = true;
			if (ins_val.length == colomns.length) {
				for (int i = 0; i < colomns.length; i++) {
					if ((inputTypes[i].equals("int") && ins_val[i].matches("-?\\d+"))
							|| (inputTypes[i].equals("varchar") && !ins_val[i].matches("\\d+"))
							|| (inputTypes[i].equals("float") && ins_val[i].matches("-?\\d+([.]\\d+)?"))
							|| (inputTypes[i].equals("date")
									&& ins_val[i].matches("\\d{4}-([0][0-9]|[1][0-2])-([0-2][0-9]|[3][0-1])"))) {
						append_xml(DB_name, T_name, colomns[i], ins_val[i], doc);
					} else {
						valid = false;
						 System.out.println("Columns do not match values data Types. \n");
					}
				}
				if (valid) {
					updatecounter = 0;
					index++;
					updatecounter = 1;
					Ch = new boolean[150];
					transform(doc, e.getDrPath() + File.separator + DB_name + File.separator + T_name);
					 System.out.println("insertion Done");
				}

			} else {
				 System.out.println("Error, none existing columns ! . \n");
			}

		}
	}

	public void append_xml(String DB_name, String T_name, String ins_col, String ins_val, Document doc) {

		Element Colomn1 = doc.createElement("Index" + Integer.toString(index));
		Colomn1.appendChild(doc.createTextNode(ins_val));
		doc.getElementsByTagName(ins_col).item(0).appendChild(Colomn1);

	}

	public void CreateTable_xml(String dtbname, String Tname, String[] colomns, String[] inputTypes)
			throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement(Tname);
		doc.appendChild(rootElement);
		for (int i = 0; i < colomns.length; i++) {
			Element Colomn1 = doc.createElement(colomns[i]);
			Colomn1.setAttribute("DataType", inputTypes[i]);
			rootElement.appendChild(Colomn1);
		}
		
		transform(doc, e.getDrPath() + File.separator + dtbname + File.separator + Tname);
		System.out.println("File saved!");
	}

	public List<List<String>> TableToList(String dtbname, String tablename, String[] elem) {
		List<String> rowList = new ArrayList<String>();
		List<List<String>> rowsList = new ArrayList<List<String>>();
		readTableData(dtbname, tablename);

		Document doc = readDocument(e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".xml");

		NodeList nlists[] = new NodeList[elem.length];
		Node Node1[] = new Node[elem.length];
		Element eElement1[] = new Element[elem.length];
		for (int i = 0; i < elem.length; i++) {
			nlists[i] = doc.getElementsByTagName(elem[i]);
			Node1[i] = nlists[i].item(0);
			eElement1[i] = (Element) Node1[i];
		}

		for (int i = 1; i < index; i++) {
			for (int j = 0; j < elem.length; j++) {
				rowList.add(eElement1[j].getElementsByTagName("Index" + Integer.toString(i)).item(0).getTextContent());
			}

			rowsList.add(rowList);
			rowList = new ArrayList<String>();

		}

		return rowsList;

	}

	public List<List<String>> TableToList_WithWhere(String dtbname, String tablename, String[] colomns, String colname,
			String val, String sign) {
		readTableData(dtbname, tablename);

		Document doc = readDocument(e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".xml");
		List<List<String>> rowsList = new ArrayList<List<String>>();
		List<String> row = new ArrayList<String>();

		NodeList nlist1 = doc.getElementsByTagName(colname);
		Node lNode1 = nlist1.item(0);
		Element eElement1 = (Element) lNode1;
		NodeList nlists[] = new NodeList[colomns.length];
		Node Node1[] = new Node[colomns.length];
		Element eElement2[] = new Element[colomns.length];
		int m = validate.indexOfColType(colname, getCol());
		for (int j = 0; j < colomns.length; j++) {
			nlists[j] = doc.getElementsByTagName(colomns[j]);
			Node1[j] = nlists[j].item(0);
			eElement2[j] = (Element) Node1[j];
		}
		boolean checking = false;
		for (int i = 1; i < index; i++) {
			String s = eElement1.getElementsByTagName("Index" + Integer.toString(i)).item(0).getTextContent();
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
				for (int j = 0; j < colomns.length; j++) {

					row.add(eElement2[j].getElementsByTagName("Index" + Integer.toString(i)).item(0).getTextContent());

				}
				rowsList.add(row);
				row = new ArrayList<String>();
				checking = false;
			}
		}
		return rowsList;
	}

	public void alterAdd_xml(String dtbname, String tablename, String colName, String colType) {

		readTableData(dtbname, tablename);
		Document doc = readDocument(e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".xml");
		Element Colomn = doc.createElement(colName);
		Colomn.setAttribute("DataType", colType);
		doc.getElementsByTagName(tablename).item(0).appendChild(Colomn);
		for (int i = 1; i < index; i++) {
			Element Colomn1 = doc.createElement("Index" + Integer.toString(i));
			Colomn1.appendChild(doc.createTextNode("null"));
			doc.getElementsByTagName(colName).item(0).appendChild(Colomn1);
		}
		updatecounter = index - 1;
		transform(doc, e.getDrPath() + File.separator + dtbname + File.separator + tablename);
	}

	public void alterDrop_xml(String dtbname, String tablename, String colName) {

		readTableData(dtbname, tablename);
		Document doc = readDocument(e.getDrPath() + File.separator + dtbname + File.separator + tablename + ".xml");

		NodeList nlist = doc.getElementsByTagName(tablename);
		Node lNode = nlist.item(0);
		Element rootElement = (Element) lNode;
		rootElement.getElementsByTagName(colName).item(0).getParentNode()
				.removeChild(rootElement.getElementsByTagName(colName).item(0));
		updatecounter = index - 1;
		transform(doc, e.getDrPath() + File.separator + dtbname + File.separator + tablename);
	}
}
