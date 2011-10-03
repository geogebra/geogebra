package geogebra.gui;

import geogebra.gui.inputfield.MyTextField;
import geogebra.kernel.AlgoDependentText;
import geogebra.kernel.GeoText;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;

public class DynamicTextInputPane extends JTextPane {

	private Application app;
	private DynamicTextInputPane thisPane;
	public DefaultStyledDocument doc;

	public DynamicTextInputPane(Application app) {
		super();
		this.app = app;
		thisPane = this;
		setBackground(Color.white);
		doc = (DefaultStyledDocument) this.getDocument();
		//this.setCaret(new MyCaret());
	}


	/**
	 * Inserts dynamic text field at the current caret position and returns the text
	 * field's document
	 */
	public Document insertDynamicText(String text, TextInputDialog inputDialog) {
		return insertDynamicText(text, this.getCaretPosition(), inputDialog);
	}


	/**
	 * Inserts dynamic text field at a specified position and returns the text
	 * field's document
	 */
	public Document insertDynamicText(String text, int pos, TextInputDialog inputDialog) {

		if (pos == -1) pos = getDocument().getLength(); // insert at end

		int mode = DynamicTextField.MODE_VALUE;
		String s;

		if (text.endsWith("]")) {
			if (text.startsWith(s = app.getCommand("LaTeX")+"[")){

				// strip off outer command
				String temp = text.substring(s.length(), text.length() - 1);
				
				// check for second argument in LaTeX[str, false]
				int commaIndex = temp.lastIndexOf(',');
				int bracketCount = 0;
				for (int i = commaIndex + 1 ; i < temp.length() ; i++) {
					if (temp.charAt(i) == '[') bracketCount ++;
					else if (temp.charAt(i) == ']') bracketCount --;
				}
				if (bracketCount != 0 || commaIndex == -1) {
					// no second argument
					text = temp;
					mode = DynamicTextField.MODE_FORMULATEXT;
				}

			} else if (text.startsWith(s = app.getCommand("Name")+"[")) {

				// strip off outer command
				text = text.substring(s.length(), text.length() - 1);
				mode = DynamicTextField.MODE_DEFINITION;
			}
		}

		DynamicTextField tf = new DynamicTextField(app, inputDialog); 
		Document tfDoc = tf.getDocument();
		tf.setText(text);
		tf.setMode(mode);

		// insert the text field into the text pane
		setCaretPosition(pos);
		insertComponent(tf);

		return tfDoc;
	}

	StringBuilder sb = new StringBuilder();

	/**
	 * Converts the current editor content into a GeoText string.  
	 */
	public String buildGeoGebraString(boolean latex){

		sb.setLength(0);
		boolean containsQuotes = false;
		Element elem;
		for(int i = 0; i < doc.getLength(); i++){
			try {
				elem = doc.getCharacterElement(i);
				if(elem.getName().equals("component")){

					DynamicTextField tf = (DynamicTextField) StyleConstants.getComponent(elem.getAttributes());

					if (tf.getMode() == DynamicTextField.MODE_DEFINITION){
						sb.append("\"+");
						sb.append("Name[");
						sb.append(tf.getText());
						sb.append("]");
						sb.append("+\"");
					}
					else if (latex || tf.getMode() == DynamicTextField.MODE_FORMULATEXT){
						sb.append("\"+");
						sb.append("LaTeX["); // internal name for FormulaText[ ]
						sb.append(tf.getText());
						sb.append("]");
						sb.append("+\"");
					} else {
						//tf.getMode() == DynamicTextField.MODE_VALUE

						// brackets needed for eg "hello"+(a+3)
						sb.append("\"+(");
						sb.append(tf.getText());
						sb.append(")+\"");
					}



				}else if(elem.getName().equals("content")){
					
					String content = doc.getText(i, 1);
					sb.append(content);
					
					if (content.indexOf("\"") > -1) containsQuotes = true;
				}

			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		// removed - if just text is typed, we want to make a string, not dynamic text
		/*if (app.getKernel().lookupLabel(sb.toString()) != null) {
			sb.append("+\"\""); // add +"" to end
		} 
		else */
		if (!containsQuotes)
		{
			// add quotes at start and end unless it's an "old-style" dynamic text
			// eg "length = "+length
			sb.insert(0, '"');
			sb.append('"');
		}

		return sb.toString();

	}

	/**
	 * Builds and sets editor content to correspond with the text string of a GeoText
	 * @param geo
	 * @param text
	 */
	public void setText(GeoText geo, TextInputDialog id){

		super.setText("");

		if(geo == null) return;

		if(geo.isIndependent()){
			super.setText(geo.getTextString());
			return;
		}

		// if dependent text then get the root 
		ExpressionNode root = ((AlgoDependentText)geo.getParentAlgorithm()).getRoot(); 

		// parse the root and set the text content
		root.splitString(this, id);

	}



	/**
	 * Overrides insertString to allow option offs = -1 for inserting at end.
	 */
	public void insertString(int offs, String str, AttributeSet a) {
		try {
			if (offs == -1) offs = doc.getLength(); // insert at end
			doc.insertString(offs, str, a);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Custom caret with damage area set to a thin width. This allows the caret
	 * to appear next to a DynamicTextField with destroying the field's border.
	 */
	class MyCaret extends DefaultCaret {

		public MyCaret(){
			super();
			this.setBlinkRate(500);
		}
		protected synchronized void damage(Rectangle r){
			if (r == null) return;
			x = r.x;
			y = r.y;
			width = 4;
			height = r.height;
			repaint();

		}
	}


	/*********************************************************************
	 * Class for the dynamic text container.
	 * 
	 */
	private class DynamicTextField extends MyTextField{

		public static final int MODE_VALUE = 0;
		public static final int MODE_DEFINITION = 1;
		public static final int MODE_FORMULATEXT = 2;
		private int mode = MODE_VALUE;
		private TextInputDialog id;

		private JPopupMenu contextMenu;

		public DynamicTextField(Application app, TextInputDialog id) {
			super(app);
			this.id = id;

			// add a mouse listener to trigger the context menu
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent evt) {
					if (evt.isPopupTrigger()) {
						createContextMenu();
						contextMenu.show(evt.getComponent(), evt.getX(), evt.getY());
					}
				}
				public void mouseReleased(MouseEvent evt) {
					if (evt.isPopupTrigger()) {
						createContextMenu();
						contextMenu.show(evt.getComponent(), evt.getX(), evt.getY());
					}
				}
			});


			// special transparent border to show caret when next to the component
			setOpaque(false);
			setBorder( (Border) new CompoundBorder(new LineBorder(new Color(0, 0, 0, 0), 1), getBorder()));

			// make sure the field is aligned nicely in the text pane
			Font f = thisPane.getFont();

			setFont(f);
			FontMetrics fm = getFontMetrics(f);
			int maxAscent = fm.getMaxAscent();
			int height = (int)getPreferredSize().getHeight();
			int borderHeight = getBorder().getBorderInsets(this).top;
			int aboveBaseline = maxAscent + borderHeight;
			float alignmentY = (float)(aboveBaseline)/((float)(height));
			setAlignmentY(alignmentY);

			// add document listener that will update the text pane when this field is edited
			getDocument().addDocumentListener(new DocumentListener(){

				public void changedUpdate(DocumentEvent arg0) {}

				public void insertUpdate(DocumentEvent arg0) {
					thisPane.repaint();
				}
				public void removeUpdate(DocumentEvent arg0) {
					thisPane.repaint();

				}
			});

		}

		public Dimension getMaximumSize() {
			return this.getPreferredSize();
		}

		public int getMode() {
			return mode;
		}

		public void setMode(int mode) {
			this.mode = mode;
		}

		private void createContextMenu(){
			contextMenu = new JPopupMenu();

			JCheckBoxMenuItem item = new JCheckBoxMenuItem(app.getMenu("Value"));
			item.setSelected(mode == MODE_VALUE);
			item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					mode = MODE_VALUE;
					id.handleDocumentEvent(null);

				}
			});
			contextMenu.add(item);

			item = new JCheckBoxMenuItem(app.getPlain("Definition"));
			item.setSelected(mode == MODE_DEFINITION);
			item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					mode = MODE_DEFINITION;
					id.handleDocumentEvent(null);

				}
			});
			contextMenu.add(item);
			/*
			item = new JCheckBoxMenuItem(app.getMenu("Formula"));
			item.setSelected(mode == MODE_FORMULATEXT);
			item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					mode = MODE_FORMULATEXT;	
				}
			}); */
			contextMenu.add(item);
		}

	}


}

