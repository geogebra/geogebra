package geogebra.gui.inputfield;

import geogebra.gui.SetLabels;
import geogebra.gui.VirtualKeyboardListener;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.main.Application;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.StyledEditorKit;

/**
 * Extends JTextField to add (1) dynamic coloring of bracket symbols and quote
 * enclosed text and (2) a popup symbol table for inserting special characters.
 * The popup is triggered by either an in-line button click or a ctrl-up key
 * press.
 * 
 */
public class MyTextField extends JTextField implements ActionListener,
		FocusListener, VirtualKeyboardListener, SetLabels {

	private static final long serialVersionUID = 1L;

	private Application app;

	// symbol table popup fields
	private SymbolTablePopup tablePopup;
	private MyTextField thisField = this;

	private ImageIcon icon = GeoGebraIcon.createSymbolTableIcon(this.getFont(),
			false);
	private ImageIcon rollOverIcon = GeoGebraIcon.createSymbolTableIcon(
			this.getFont(), true);
	private boolean showSymbolTableIcon = false;

	// border button fields
	private BorderButton borderBtn;
	private Border defaultBorder;

	private boolean enableColoring = true;

	protected StyledTextFieldDocument document;

	/************************************
	 * Construct an instance of MyTextField without a fixed column width
	 */
	public MyTextField(Application app) {
		super();
		this.app = app;
		initField();
	}

	/************************************
	 * Construct an instance of MyTextField with a fixed column width
	 * 
	 * @param columns
	 */
	public MyTextField(Application app, int columns) {
		super(columns);
		this.app = app;
		initField();
	}

	/**
	 * Initializes the field: registers listeners, creates and sets the
	 * BorderButton
	 */
	private void initField() {
		setOpaque(true);
		addFocusListener(this);
		addKeyListener(new MyKeyListener());

		JTextField dummy = new JTextField();
		defaultBorder = dummy.getBorder();
		borderBtn = new BorderButton(this);
		borderBtn.setBorderButton(0, icon, this);
		setDefaultBorder();

		if (enableColoring) {
			setupEditorKit();
		}

	}

	/**
	 * Replaces the JTextField editor kit with an editor kit that supports
	 * styledDocuments with multi-colored text.
	 */
	public void setupEditorKit() {

		// Replace the BasicTextFieldUI with our own customized subclass
		// TODO: this call should be made once at startup
		UIManager.put("TextFieldUI", StyledBasicTextFieldUI.class.getName());

		// Store our editor kit as a client property. This will be retrieved by
		// StyledBasicTextFieldUI.
		StyledEditorKit kit = new StyledTextFieldEditorKit();
		putClientProperty("editorKit", kit);

		// prepare a new document that can handle styles
		document = new StyledTextFieldDocument();
		document.setStyles(getFont());
		setDocument(document);
	}

	/**
	 * @return true if bracket coloring is enabled
	 */
	public boolean enableColoring() {
		return false;
	}

	/**
	 * sets the flag to enable bracket coloring
	 * 
	 * @param enableColoring
	 */
	public void enableColoring(boolean enableColoring) {
		this.enableColoring = enableColoring;
	}

	// ====================================================
	// BorderButton
	// ====================================================

	public BorderButton getBorderButton() {
		return borderBtn;
	}

	private void setDefaultBorder() {
		super.setBorder(BorderFactory.createCompoundBorder(defaultBorder,
				borderBtn));
	}

	protected void setBorderButton(int index, ImageIcon icon, ActionListener al) {
		borderBtn.setBorderButton(index, icon, al);
		setDefaultBorder();
	}

	protected void setBorderButtonVisible(int index, boolean isVisible) {
		borderBtn.setIconVisible(index, isVisible);
		setDefaultBorder();
	}

	protected boolean isBorderButtonVisible(int index) {
		return borderBtn.isIconVisible(index);
	}

	/**
	 * Overrides <code>setBorder</code> to prevent removal of the BorderButton
	 */
	@Override
	public void setBorder(Border border) {
		super.setBorder(BorderFactory.createCompoundBorder(border, borderBtn));
	}

	// ====================================================
	// Event Handlers, Listeners
	// ====================================================

	public void focusGained(FocusEvent e) {

		// TODO: can't remember why the caret position was reset like this,
		// a trick to keep the Mac OS from selecting the field?
		//
		// now removed - stops the text being highlighted #709
		// thisField.setCaretPosition(thisField.getCaretPosition());

		if (showSymbolTableIcon && hasFocus())
			borderBtn.setIconVisible(0, true);
		thisField.repaint();

		if (app.getGuiManager() != null)
			app.getGuiManager().setCurrentTextfield(
					(VirtualKeyboardListener) this, false);
	}

	public void focusLost(FocusEvent e) {

		if (showSymbolTableIcon)
			borderBtn.setIconVisible(0, false);
		thisField.repaint();

		if (app.getGuiManager() != null)
			app.getGuiManager().setCurrentTextfield(null,
					!(e.getOppositeComponent() instanceof VirtualKeyboard));
	}

	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();
		if (cmd.equals(0 + BorderButton.cmdSuffix)) {

			getTablePopup().showPopup(true);
		}
	}

	/**
	 * Inserts a string into the text at the current caret position
	 */
	public void insertString(String text) {

		int start = getSelectionStart();
		int end = getSelectionEnd();

		// clear selection if there is one
		if (start != end) {
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));
			setText(sb.toString());
			setCaretPosition(start);
		}

		int pos = getCaretPosition();
		String oldText = getText();
		StringBuilder sb = new StringBuilder();
		sb.append(oldText.substring(0, pos));
		sb.append(text);
		sb.append(oldText.substring(pos));
		setText(sb.toString());

		// setCaretPosition(pos + text.length());
		final int newPos = pos + text.length();

		// make sure AutoComplete works
		if (this instanceof AutoCompleteTextField) {
			AutoCompleteTextField tf = (AutoCompleteTextField) this;
			tf.updateCurrentWord(false);
			tf.startAutoCompletion();
		}

		// Under a Mac OS the string is always selected after the insert. A
		// runnable prevents
		// this by resetting the caret to cancel the selection.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setCaretPosition(newPos);
			}
		});

		// TODO: tried to keep the Mac OS from auto-selecting the field by
		// resetting the
		// caret, but not working yet
		// setCaret(new DefaultCaret());
		// setCaretPosition(newPos);
	}

	/**
	 * Sets a flag to show the symbol table icon when the field is focused
	 * 
	 * @param showSymbolTableIcon
	 */
	public void setShowSymbolTableIcon(boolean showSymbolTableIcon) {
		this.showSymbolTableIcon = showSymbolTableIcon;
	}

	private SymbolTablePopup getTablePopup() {
		if (tablePopup == null)
			tablePopup = new SymbolTablePopup(app, this);
		return tablePopup;
	}

	/**
	 * Overrides processKeyEvents so that the symbol table popup can be
	 * triggered by ctrl-up.
	 * */
	@Override
	public void processKeyEvent(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if ((e.isControlDown() || Application.isControlDown(e))
				&& keyCode == KeyEvent.VK_UP) {
			getTablePopup().showPopup(false);
			return;
		}

		super.processKeyEvent(e);
	}

	public void setLabels() {
		if (tablePopup != null)
			tablePopup.setLabels();
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (document != null) {
			document.setStyles(font);
		}
	}

	/**
	 * Key listener for updating colored brackets on left/right arrow key
	 * presses
	 */
	public class MyKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();

			if (keyCode == KeyEvent.VK_LEFT) {
				document.applyCharacterStyling(getCaretPosition(), true);
			} else if (keyCode == KeyEvent.VK_RIGHT) {
				document.applyCharacterStyling(getCaretPosition(), true);
			}
		}

	}
}
