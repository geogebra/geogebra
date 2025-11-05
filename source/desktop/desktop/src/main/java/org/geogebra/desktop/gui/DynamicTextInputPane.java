package org.geogebra.desktop.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;

import org.geogebra.common.gui.inputfield.DynamicTextElement;
import org.geogebra.common.gui.inputfield.DynamicTextProcessor;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.dialog.TextInputDialogD;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.main.AppD;

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
	private final DefaultStyledDocument doc;
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
		DynamicTextElement.DynamicTextType mode = DynamicTextElement.DynamicTextType.VALUE;
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
					mode = DynamicTextElement.DynamicTextType.FORMULA_TEXT;
				}

			} else if (text.startsWith(
					s = app.getLocalization().getCommand("Name") + "(")) {

				// strip off outer command
				text = text.substring(s.length(), text.length() - 1);
				mode = DynamicTextElement.DynamicTextType.DEFINITION;
			}
		}

		return insertDynamicInput(text, mode, pos0, inputDialog);
	}

	private DynamicTextField insertDynamicInput(String text,
			DynamicTextElement.DynamicTextType mode, int pos0, TextInputDialogD inputDialog) {
		int pos = pos0;
		if (pos == -1) {
			pos = getDocument().getLength(); // insert at end
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
		StringBuilder sb = new StringBuilder();
		Element elem;
		List<DynamicTextElement> elements = new ArrayList<>();
		for (int i = 0; i < getDoc().getLength(); i++) {
			try {
				elem = getDoc().getCharacterElement(i);
				if (elem.getName().equals("component")) {
					if (sb.length() > 0) {
						elements.add(new DynamicTextElement(sb.toString(),
								DynamicTextElement.DynamicTextType.STATIC));
						sb.setLength(0);
					}
					DynamicTextField tf = (DynamicTextField) StyleConstants
							.getComponent(elem.getAttributes());
					elements.add(new DynamicTextElement(tf.getText(), tf.getMode()));

				} else if (elem.getName().equals("content")) {
					sb.append(getDoc().getText(i, 1));
				}

			} catch (BadLocationException e) {
				Log.debug(e);
			}
		}
		if (sb.length() > 0) {
			elements.add(new DynamicTextElement(sb.toString(),
					DynamicTextElement.DynamicTextType.STATIC));
		}

		return new DynamicTextProcessor(app).buildGeoGebraString(elements, latex);

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

		// parse the root and set the text content
		List<DynamicTextElement> parts = new DynamicTextProcessor(geo.getKernel()
				.getApplication()).buildDynamicTextList(geo);
		for (DynamicTextElement part: parts) {
			if (part.type == DynamicTextElement.DynamicTextType.STATIC) {
				insertString(-1, part.text, null);
			} else {
				insertDynamicInput(part.text, part.type, -1, id);
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
				offs = getDoc().getLength(); // insert at end
			}
			getDoc().insertString(offs, str, a);

		} catch (BadLocationException e) {
			Log.debug(e);
		}

	}

	/** @return document */
	public DefaultStyledDocument getDoc() {
		return doc;
	}

	/**
	 * Class for the dynamic text container.
	 * 
	 */
	public class DynamicTextField extends MyTextFieldD {

		private static final long serialVersionUID = 1L;

		DynamicTextElement.DynamicTextType mode = DynamicTextElement.DynamicTextType.VALUE;
		TextInputDialogD id;

		JPopupMenu contextMenu;

		/**
		 * @param app application
		 * @param id input dialog
		 */
		public DynamicTextField(AppD app, TextInputDialogD id) {
			super(app);
			this.id = id;
			// see ticket #1339
			this.enableColoring(false);

			// handle alt+arrow to exit the field
			addKeyListener(new ArrowKeyListener(this));

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
			float alignmentY = aboveBaseline / ((float) height);
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

		public DynamicTextElement.DynamicTextType getMode() {
			return mode;
		}

		public void setMode(DynamicTextElement.DynamicTextType mode) {
			this.mode = mode;
		}

		private class ArrowKeyListener extends KeyAdapter {

			private final DynamicTextField tf;

			public ArrowKeyListener(DynamicTextField tf) {
				this.tf = tf;
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isAltDown() || AppD.isAltDown(e)) {
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
			item.setSelected(mode == DynamicTextElement.DynamicTextType.VALUE);
			item.addActionListener(arg0 -> {
				mode = DynamicTextElement.DynamicTextType.VALUE;
				id.handleDocumentEvent();
			});
			contextMenu.add(item);

			item = new JCheckBoxMenuItem(
					app.getLocalization().getMenu("Definition"));
			item.setSelected(mode == DynamicTextElement.DynamicTextType.DEFINITION);
			item.addActionListener(arg0 -> {
				mode = DynamicTextElement.DynamicTextType.DEFINITION;
				id.handleDocumentEvent();
			});
			contextMenu.add(item);
			contextMenu.add(item);

			app.setComponentOrientation(contextMenu);
		}
	}

}
