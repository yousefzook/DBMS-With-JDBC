package dbmsGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;

import parser.Parser;

public class CommandLineGui extends JFrame implements DocumentListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea textArea;
	private JTextArea lineNumbers;
	private JScrollPane scrollPane;
	private Border border;

	public CommandLineGui() {

		super("DBMS");
		textArea = new JTextArea();
		textArea.setVisible(true);
		textArea.setFont(new Font("Consolas", Font.PLAIN, 25));
		textArea.setBackground(Color.black);
		textArea.setForeground(Color.cyan);
		textArea.setCaretColor(Color.WHITE);
		border = BorderFactory.createLineBorder(Color.black);
		textArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200, 600);
		setVisible(true);
		Document doc = textArea.getDocument();
		doc.addDocumentListener(this);
		textArea.addKeyListener(this);
		addLinNumbers();
	}

	public void addLinNumbers() {

		lineNumbers = new JTextArea(20, 2);
		lineNumbers
				.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 1)));
		lineNumbers.setBackground(Color.black);
		lineNumbers.setFont(new Font("Consolas", Font.PLAIN, 25));
		lineNumbers.setEnabled(false);
		add(lineNumbers, BorderLayout.WEST);

	}

	private String getLineNumbersText() {

		int caretPosition = textArea.getDocument().getLength();
		Element root = textArea.getDocument().getDefaultRootElement();
		StringBuilder lineNumbersTextBuilder = new StringBuilder();
		lineNumbersTextBuilder.append("1").append(System.lineSeparator());

		for (int elementIndex = 2; elementIndex < root.getElementIndex(caretPosition) + 2; elementIndex++) {
			lineNumbersTextBuilder.append(elementIndex).append(System.lineSeparator());
		}

		return lineNumbersTextBuilder.toString();
	}

	public void updateLineNumbers() {

		String lineNumbersText = getLineNumbersText();
		lineNumbers.setText(lineNumbersText);

	}


	@Override
	public void changedUpdate(DocumentEvent e) {

		updateLineNumbers();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {

		updateLineNumbers();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {

		updateLineNumbers();
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		
		if (evt.getKeyCode() == KeyEvent.VK_ENTER && (evt.getModifiers() & InputEvent.SHIFT_MASK) == 0) {

			try {
				new Parser(textArea.getText());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if (evt.getKeyCode() == KeyEvent.VK_ENTER && (evt.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
			
			int p = textArea.getCaretPosition();
			textArea.setText(textArea.getText().substring(0,p) + "\n" +textArea.getText().substring(p) );
			textArea.setCaretPosition(p+1);

		}
	} // run the query when press Enter.

	@Override
	public void keyReleased(KeyEvent evt) {

	}// unused method

	@Override
	public void keyTyped(KeyEvent evt) {

	}// unused method
}
