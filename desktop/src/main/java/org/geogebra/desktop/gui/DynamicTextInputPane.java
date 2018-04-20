package org.geogebra.desktop.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentText;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyStringBuffer;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.gui.dialog.TextInputDialogD;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.main.AppD;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Extended JTextPane for editing GeoText strings. Uses embedded text fields
 * (inner class DynamicTextField) to handle object references in a GeoText
 * string.
 * 
 * @author G. Sturr
 * 
 */
public class DynamicTextInputPane extends JTextPane implements FocusListener {

	private static final long serialVersionUID = 1L;
	/** application */
	AppD app;
	protected final DynamicTextInputPane thisPane;
	/** doc */
	public DefaultStyledDocument doc;
	private JTextComponent focusedTextComponent;

	/**************************************
	 * Constructs a DynamicTextInputPane
	 * 
	 * @param app
	 *            app
	 */
	public DynamicTextInputPane(AppD app) {
		super();
		this.app = app;
		thisPane = this;
		setBackground(Color.white);
		doc = (DefaultStyledDocument) this.getDocument();
		this.addKeyListener(new GeoGebraKeys());
		this.addFocusListener(this);
		focusedTextComponent = this;
		// this.setCaret(new MyCaret());
	}

	@Override
	public void replaceSelection(String content) {
		if (focusedTextComponent == this) {
			super.replaceSelection(content);
		} else {
			focusedTextComponent.replaceSelection(content);
		}
	}

	/**
	 * @return focusedTextComponent
	 */
	public JTextComponent getFocusedTextComponent() {
		return focusedTextComponent;
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() instanceof JTextComponent) {
			focusedTextComponent = (JTextComponent) e.getSource();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
	}

	/**
	 * Inserts dynamic text field at the current caret position and returns the
	 * text field's document
	 * 
	 * @param text
	 *            text to put in the dynamic field
	 * @param inputDialog
	 *            input dialog
	 * @return dynamic text field
	 */
	public DynamicTextField insertDynamicText(String text,
			TextInputDialogD inputDialog) {
		return insertDynamicText(text, this.getCaretPosition(), inputDialog);
	}

	/**
	 * Inserts dynamic text field at a specified position and returns the text
	 * field's document
	 * 
	 * @param text0
	 *            text to put in the dynamic field
	 * @param pos0
	 *            position of the dynamic text field
	 * @param inputDialog
	 *            input dialog
	 * @return dynamic text field
	 */
	public DynamicTextField insertDynamicText(String text0, int pos0,
			TextInputDialogD inputDialog) {
		String text = text0;
		int pos = pos0;
		if (pos == -1) {
			pos = getDocument().getLength(); // insert at end
		}

		int mode = DynamicTextField.MODE_VALUE;
		String s;

		if (text.endsWith(")")) {
			if (text.startsWith(
					s = app.getLocalization().getCommand("LaTeX") + "(")) {

				// strip off outer command
				String temp = text.substring(s.length(), text.length() - 1);

				// check for second argument in LaTeX[str, false]
				int commaIndex = temp.lastIndexOf(',');
				int bracketCount = 0;
				for (int i = commaIndex + 1; i < temp.length(); i++) {
					if (temp.charAt(i) == '(') {
						bracketCount++;
					} else if (temp.charAt(i) == ')') {
						bracketCount--;
					}
				}
				if (bracketCount != 0 || commaIndex == -1) {
					// no second argument
					text = temp;
					mode = DynamicTextField.MODE_FORMULATEXT;
				}

			} else if (text.startsWith(
					s = app.getLocalization().getCommand("Name") + "(")) {

				// strip off outer command
				text = text.substring(s.length(), text.length() - 1);
				mode = DynamicTextField.MODE_DEFINITION;
			}
		}

		DynamicTextField tf = new DynamicTextField(app, inputDialog);
		tf.setText(text);
		tf.setMode(mode);
		tf.addFocusListener(this);

		// insert the text field into the text pane
		setCaretPosition(pos);
		insertComponent(tf);

		return tf;
	}

	/**
	 * Converts the current editor content into a GeoText string.
	 * 
	 * @param latex
	 *            boolean
	 * @return String to convert to GeoText eg "value is "+a
	 */
	public String buildGeoGebraString(boolean latex) {

		char currentQuote = Unicode.OPEN_DOUBLE_QUOTE;

		StringBuilder sb = new StringBuilder();
		Element elem;
		for (int i = 0; i < doc.getLength(); i++) {
			try {
				elem = doc.getCharacterElement(i);
				if (elem.getName().equals("component")) {

					DynamicTextField tf = (DynamicTextField) StyleConstants
							.getComponent(elem.getAttributes());

					if (tf.getMode() == DynamicTextField.MODE_DEFINITION) {
						sb.append("\"+");
						sb.append("Name[");
						sb.append(tf.getText());
						sb.append(']');
						sb.append("+\"");
					} else if (latex || tf
							.getMode() == DynamicTextField.MODE_FORMULATEXT) {
						sb.append("\"+");
						sb.append("LaTeX["); // internal name for FormulaText[ ]
						sb.append(tf.getText());
						sb.append(']');
						sb.append("+\"");
					} else {
						// tf.getMode() == DynamicTextField.MODE_VALUE

						// brackets needed for eg "hello"+(a+3)
						sb.append("\"+(");
						sb.append(tf.getText());
						sb.append(")+\"");
					}

				} else if (elem.getName().equals("content")) {

					String content = doc.getText(i, 1);
					currentQuote = StringUtil.processQuotes(sb, content,
							currentQuote);

				}

			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		// add quotes at start and end so it parses to a text
		sb.insert(0, '"');
		sb.append('"');

		return sb.toString();

	}

	/**
	 * Builds and sets editor content to correspond with the text string of a
	 * GeoText
	 * 
	 * @param geo
	 *            GeoText
	 * @param id
	 *            id
	 */
	public void setText(GeoText geo, TextInputDialogD id) {

		super.setText("");

		if (geo == null) {
			return;
		}

		if (geo.isIndependent()) {
			super.setText(geo.getTextString());
			return;
		}

		// if dependent text then get the root
		ExpressionNode root = ((AlgoDependentText) geo.getParentAlgorithm())
				.getRoot();

		// parse the root and set the text content
		this.splitString(root, id);

	}

	/**
	 * @param en
	 *            en
	 * @param id
	 *            id
	 */
	public void splitString(ExpressionNode en, TextInputDialogD id) {
		ExpressionValue left = en.getLeft();
		ExpressionValue right = en.getRight();
		StringTemplate tpl = StringTemplate.editTemplate;
		if (en.isLeaf()) {

			if (left.isGeoElement()) {
				DynamicTextField d = insertDynamicText(
						((GeoElement) left).getLabel(tpl), -1, id);
				d.getDocument().addDocumentListener(id);
			} else if (left.isExpressionNode()) {
				splitString((ExpressionNode) left, id);
			} else if (left instanceof MyStringBuffer) {
				insertString(-1, left.toString(tpl).replaceAll("\"", ""), null);
			} else {
				insertDynamicText(left.toString(tpl), -1, id);
			}

		}

		// STANDARD case: no leaf
		else {

			if (right != null && !en.containsMyStringBuffer()) {
				// neither left nor right are free texts, eg a+3 in
				// (a+3)+"hello"
				// so no splitting needed
				insertDynamicText(en.toString(tpl), -1, id);
				return;
			}

			// expression node
			if (left.isGeoElement()) {
				DynamicTextField d = insertDynamicText(
						((GeoElement) left).getLabel(tpl), -1, id);
				d.getDocument().addDocumentListener(id);
			} else if (left.isExpressionNode()) {
				this.splitString((ExpressionNode) left, id);
			} else if (left instanceof MyStringBuffer) {
				insertString(-1, left.toString(tpl).replaceAll("\"", ""), null);
			} else {
				insertDynamicText(left.toString(tpl), -1, id);
			}

			if (right != null) {
				if (right.isGeoElement()) {
					DynamicTextField d = insertDynamicText(
							((GeoElement) right).getLabel(tpl), -1, id);
					d.getDocument().addDocumentListener(id);
				} else if (right.isExpressionNode()) {
					this.splitString((ExpressionNode) right, id);
				} else if (right instanceof MyStringBuffer) {
					insertString(-1, right.toString(tpl).replaceAll("\"", ""),
							null);
				} else {
					insertDynamicText(right.toString(tpl), -1, id);
				}
			}
		}

	}

	/**
	 * Overrides insertString to allow option offs = -1 for inserting at end.
	 * 
	 * @param offs0
	 *            offset
	 * @param str
	 *            string to insert
	 * @param a
	 *            attributes
	 */
	public void insertString(int offs0, String str, AttributeSet a) {
		try {
			int offs = offs0;
			if (offs == -1) {
				offs = doc.getLength(); // insert at end
			}
			doc.insertString(offs, str, a);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Custom caret with damage area set to a thin width. This allows the caret
	 * to appear next to a DynamicTextField without destroying the field's
	 * border.
	 */
	static class MyCaret extends DefaultCaret {

		private static final long serialVersionUID = 1L;

		/**
		 * 
		 */
		public MyCaret() {
			super();
			this.setBlinkRate(500);
		}

		@Override
		protected synchronized void damage(Rectangle r) {
			if (r == null) {
				return;
			}
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
	@SuppressWarnings("javadoc")
	public class DynamicTextField extends MyTextFieldD {

		private static final long serialVersionUID = 1L;

		public static final int MODE_VALUE = 0;
		public static final int MODE_DEFINITION = 1;
		public static final int MODE_FORMULATEXT = 2;
		int mode = MODE_VALUE;
		TextInputDialogD id;

		JPopupMenu contextMenu;

		/**
		 * @param app
		 * @param id
		 */
		public DynamicTextField(AppD app, TextInputDialogD id) {
			super(app);
			this.id = id;
			// see ticket #1339
			this.enableColoring(false);

			// handle alt+arrow to exit the field
			addKeyListener(new MyKeyListener(this));

			// add a mouse listener to trigger the context menu
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent evt) {
					if (evt.isPopupTrigger()) {
						createContextMenu();
						contextMenu.show(evt.getComponent(), evt.getX(),
								evt.getY());
					}
				}

				@Override
				public void mouseReleased(MouseEvent evt) {
					if (evt.isPopupTrigger()) {
						createContextMenu();
						contextMenu.show(evt.getComponent(), evt.getX(),
								evt.getY());
					}
				}
			});

			// special transparent border to show caret when next to the
			// component
			// setOpaque(false);
			// setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0,
			// 0),
			// 1), getBorder()));

			// make sure the field is aligned nicely in the text pane
			Font f = thisPane.getFont();

			setFont(f);
			FontMetrics fm = getFontMetrics(f);
			int maxAscent = fm.getMaxAscent();
			int height = (int) getPreferredSize().getHeight();
			int borderHeight = getBorder().getBorderInsets(this).top;
			int aboveBaseline = maxAscent + borderHeight;
			float alignmentY = (float) (aboveBaseline) / ((float) (height));
			setAlignmentY(alignmentY);

			// document listener to update enclosing text pane
			getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					// do nothing
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					thisPane.revalidate();
					thisPane.repaint();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					thisPane.revalidate();
					thisPane.repaint();
				}
			});

			// document listener for input dialog (updates preview pane)
			getDocument().addDocumentListener(id);
		}

		@Override
		public Dimension getMaximumSize() {
			return this.getPreferredSize();
		}

		public int getMode() {
			return mode;
		}

		public void setMode(int mode) {
			this.mode = mode;
		}

		private class MyKeyListener extends KeyAdapter {

			private DynamicTextField tf;

			public MyKeyListener(DynamicTextField tf) {
				this.tf = tf;
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.isAltDown() || AppD.isAltDown(e))) {
					switch (e.getKeyCode()) {
					default:
						// do nothing
						break;
					case KeyEvent.VK_LEFT:
						id.exitTextField(tf, true);
						break;
					case KeyEvent.VK_RIGHT:
						id.exitTextField(tf, false);
						break;
					}
				}
			}
		}

		void createContextMenu() {
			contextMenu = new JPopupMenu();

			JCheckBoxMenuItem item = new JCheckBoxMenuItem(
					app.getLocalization().getMenu("Value"));
			item.setSelected(mode == MODE_VALUE);
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					mode = MODE_VALUE;
					id.handleDocumentEvent();

				}
			});
			contextMenu.add(item);

			item = new JCheckBoxMenuItem(
					app.getLocalization().getMenu("Definition"));
			item.setSelected(mode == MODE_DEFINITION);
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					mode = MODE_DEFINITION;
					id.handleDocumentEvent();

				}
			});
			contextMenu.add(item);
			/*
			 * item = new JCheckBoxMenuItem(app.getMenu("Formula"));
			 * item.setSelected(mode == MODE_FORMULATEXT);
			 * item.addActionListener(new ActionListener(){ public void
			 * actionPerformed(ActionEvent arg0) { mode = MODE_FORMULATEXT; }
			 * });
			 */
			contextMenu.add(item);

			app.setComponentOrientation(contextMenu);

		}
	}

}
