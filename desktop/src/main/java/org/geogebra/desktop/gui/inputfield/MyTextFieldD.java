package org.geogebra.desktop.gui.inputfield;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultCaret;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.VirtualKeyboardListener;
import org.geogebra.common.gui.inputfield.ColorProvider;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.TextObject;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.gui.virtualkeyboard.VirtualKeyboardD;
import org.geogebra.desktop.main.AppD;

/**
 * Extends JTextField to add (1) dynamic coloring of bracket symbols and quote
 * enclosed text and (2) a popup symbol table for inserting special characters.
 * The popup is triggered by either an in-line button click or a ctrl-up key
 * press.
 * 
 */
public class MyTextFieldD extends JTextField
		implements ActionListener, FocusListener, VirtualKeyboardListener,
		CaretListener, SetLabels, TextObject {

	private static final long serialVersionUID = 1L;

	private AppD app;

	// symbol table popup fields
	private SymbolTablePopupD tablePopup;
	private MyTextFieldD thisField = this;

	private ImageIcon icon = GeoGebraIconD
			.createSymbolTableIcon(this.getFont());
	private boolean showSymbolTableIcon = false;

	// colored character rendering fields
	boolean caretUpdated = true;
	boolean caretShowing = true;

	// border button fields
	private BorderButtonD borderBtn;
	private Border defaultBorder;

	private boolean enableColoring = true;
	private boolean isColoringLabels;

	// matched brackets color = cyan (better contrast than "Light sea green")
	private static GColor COLOR_MATCHED = GeoGebraColorConstants.BALANCED_BRACKET_COLOR;

	// unmatched brackets color = red
	private static GColor COLOR_UNMATCHED = GeoGebraColorConstants.UNBALANCED_BRACKET_COLOR;

	// class for distinguishing graphically existing object
	private ColorProvider ip;

	/************************************
	 * Construct an instance of MyTextField without a fixed column width
	 */
	public MyTextFieldD(AppD app) {
		super();
		this.app = app;
		initField();
	}

	/************************************
	 * Construct an instance of MyTextField with a fixed column width
	 * 
	 * @param columns
	 */
	public MyTextFieldD(AppD app, int columns) {
		super(columns);
		this.app = app;
		initField();
	}

	/**
	 * Initializes the field: registers listeners, creates and sets the
	 * BorderButton
	 */
	private void initField() {

		// The OS X caret contains code that auto-selects text in undesirable
		// ways. So we replace the mac caret with a new default caret.
		if (app.isMacOS()) {
			DefaultCaret c = new DefaultCaret();
			int blinkRate = getCaret().getBlinkRate();
			c.setBlinkRate(blinkRate);
			setCaret(c);
		}

		setOpaque(true);
		addFocusListener(this);
		addCaretListener(this);

		JTextField dummy = new JTextField();
		defaultBorder = dummy.getBorder();
		borderBtn = new BorderButtonD(this);
		borderBtn.setBorderButton(0, icon, this);
		setDefaultBorder();

		setOrientation();
	}

	/**
	 * returns true if bracket coloring is enabled
	 * 
	 * @return
	 */
	public boolean enableColoring() {
		return enableColoring;
	}

	/**
	 * sets the flag to enable bracket coloring
	 * 
	 * @param enableColoring
	 */
	public void enableColoring(boolean enableColoring) {
		this.enableColoring = enableColoring;
	}

	/**
	 * enables coloring of labels
	 * 
	 * @param isCasInput
	 */
	public void enableLabelColoring(boolean isCasInput) {
		if (ip == null) {
			ip = new ColorProvider(app, isCasInput);
			return;
		}
		ip.setIsCasInput(isCasInput);
	}

	/**
	 * @param val
	 *            true if labels should be coloured
	 */
	public void setColoringLabels(boolean val) {
		this.isColoringLabels = val;
	}

	// ====================================================
	// BorderButton
	// ====================================================

	public BorderButtonD getBorderButton() {
		return borderBtn;
	}

	private void setDefaultBorder() {
		super.setBorder(
				BorderFactory.createCompoundBorder(defaultBorder, borderBtn));
	}

	protected void setBorderButton(int index, ImageIcon icon,
			ActionListener al) {
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

	boolean selectAllOnFocus = false;

	/**
	 * Sets a flag to force all text to be selected on focus (helpful for tabbed
	 * data entry)
	 * 
	 * @param selectAllOnFocus
	 */
	public void setSelectAllOnFocus(boolean selectAllOnFocus) {
		this.selectAllOnFocus = selectAllOnFocus;
	}

	int oldStyle = Font.PLAIN;

	@Override
	public void focusGained(FocusEvent e) {

		if (selectAllOnFocus) {
			thisField.setText(thisField.getText());
			thisField.selectAll();
		}

		if (showSymbolTableIcon && hasFocus()) {
			borderBtn.setIconVisible(0, true);
		}
		thisField.repaint();

		if (app.getGuiManager() != null) {
			((GuiManagerD) app.getGuiManager()).setCurrentTextfield(this,
					false);
		}
	}

	@Override
	public void focusLost(FocusEvent e) {

		if (showSymbolTableIcon) {
			borderBtn.setIconVisible(0, false);
		}
		thisField.repaint();

		if (app.getGuiManager() != null) {
			((GuiManagerD) app.getGuiManager()).setCurrentTextfield(null,
					!(e.getOppositeComponent() instanceof VirtualKeyboardD));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();
		if (cmd.equals(0 + BorderButtonD.cmdSuffix)) {

			getTablePopup().showPopup(true);
		}
	}

	/**
	 * Caret update
	 */
	@Override
	public void caretUpdate(CaretEvent e) {
		caretUpdated = true;
		repaint();
	}

	/**
	 * Inserts a string into the text at the current caret position
	 */
	@Override
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

		// insert the string
		int pos1 = getCaretPosition();
		String oldText = getText();
		StringBuilder sb = new StringBuilder();
		sb.append(oldText.substring(0, pos1));
		sb.append(text);
		sb.append(oldText.substring(pos1));
		setText(sb.toString());

		// reset the caret position
		setCaretPosition(pos1 + text.length());

		// make sure AutoComplete works
		if (this instanceof AutoCompleteTextFieldD) {
			AutoCompleteTextFieldD tf = (AutoCompleteTextFieldD) this;
			tf.updateCurrentWord(false);
			tf.startAutoCompletion();
		}

	}

	/**
	 * Sets a flag to show the symbol table icon when the field is focused
	 * 
	 * @param showSymbolTableIcon
	 */
	public void setShowSymbolTableIcon(boolean showSymbolTableIcon) {
		this.showSymbolTableIcon = showSymbolTableIcon;
	}

	public void setOpenSymbolTableUpwards(boolean openUpwards) {
		getTablePopup().setOpenUpwards(openUpwards);
	}

	private SymbolTablePopupD getTablePopup() {
		if (tablePopup == null) {
			tablePopup = new SymbolTablePopupD(app, this);
		}
		return tablePopup;
	}

	/**
	 * Overrides processKeyEvents so that the symbol table popup can be
	 * triggered by ctrl-up.
	 */
	@Override
	public void processKeyEvent(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if ((e.isControlDown() || AppD.isControlDown(e))
				&& keyCode == KeyEvent.VK_UP) {
			getTablePopup().showPopup(false);
			return;
		}

		super.processKeyEvent(e);
	}

	@Override
	public void setLabels() {
		if (tablePopup != null) {
			tablePopup.setLabels();
		}
	}

	public void setOrientation() {
		app.setComponentOrientation(this);
	}

	// fields for custom painting
	private float pos = 0; // start position of text (not pixel location)
	private int caret; // caret position
	private int scrollOffset = 0;
	private int width = 0, height = 0, textBottom, fontHeight;
	private FontRenderContext frc;
	private Font font;
	private Graphics2D g2;
	private Insets insets;

	@Override
	public void paintComponent(Graphics gr) {

		g2 = (Graphics2D) gr;
		super.paintComponent(g2);

		if (!enableColoring || !this.hasFocus()) {
			return;
		}

		// ===============================================================
		// prepare for custom drawing

		GGraphics2DD.setAntialiasing(g2);

		String text = getText();

		// get font info
		fontHeight = g2.getFontMetrics().getHeight();
		textBottom = (getHeight() - fontHeight) / 2 + fontHeight - 4;
		frc = g2.getFontRenderContext();
		font = g2.getFont();

		// get textField dimensions
		insets = getInsets();
		width = getWidth() - insets.right - insets.left;
		height = getHeight() - insets.top - insets.bottom;

		// get text position information
		scrollOffset = getScrollOffset();
		pos = 0; // text start position (not in pixels)
		if (getHorizontalAlignment() == SwingConstants.RIGHT) {
			pos = Math.max(0,
					getHorizontalVisibility().getExtent() - getLength(text));
		}
		int selStart = getSelectionStart();
		int selEnd = getSelectionEnd();

		// get caret position information
		caret = getCaretPosition();
		float caretPos = -1;
		if (caret == 0) {
			caretPos = pos;
		}

		// get the bracket positions
		String text2 = StringUtil.ignoreIndices(text);
		int[] brkPos = org.geogebra.common.gui.inputfield.MyTextField
				.getBracketPositions(text2, caret);
		int wrong = StringUtil.checkBracketsBackward(text2);
		int bracket1pos = brkPos[0];
		int bracket2pos = brkPos[1];

		// ===============================================================
		// perform custom drawing
		//
		// NOTE: using setClip was disabled because it causes the field to bleed
		// outside of bounds in some layouts
		// g2.setClip(insets.left, insets.top, width, height);

		// hide previously drawn text with a white rectangle
		g2.setColor(Color.WHITE);
		g2.fillRect(insets.left, insets.top, width, height);

		// set the text for checking labels
		if (ip != null && isColoringLabels) {
			ip.setText(text);
		}

		// redraw the text using color
		boolean textMode = false;
		for (int i = 0; i < text.length(); i++) {

			GColor fg = null;

			// determine the color
			if (text.charAt(i) == '\"') {
				textMode = !textMode;
			}
			if (i == wrong)
			 {
				fg = COLOR_UNMATCHED; // unmatched bracket
			}
			if (i == bracket1pos || i == bracket2pos) {
				if (bracket2pos > -1) {
					fg = COLOR_MATCHED; // matched bracket
				} else {
					fg = COLOR_UNMATCHED; // unmatched bracket
				}
			}

			if (fg == null) {
				if (textMode || text.charAt(i) == '\"') {
					fg = GeoGebraColorConstants.INPUT_TEXT_COLOR;
				} else if (ip != null && isColoringLabels) {
					fg = ip.getColor(i);
					// g2.setColor(GColorD.getAwtColor(ip.getColor(i)));
				} else {
					fg = GeoGebraColorConstants.INPUT_DEFAULT_COLOR;
				}
			}
			if (fg != null) {
				g2.setColor(GColorD.getAwtColor(fg));
			}
			// now draw the text
			drawText(text.charAt(i) + "", i >= selStart && i < selEnd);

			if (i + 1 == caret) {
				caretPos = pos;
			}
		}

		// draw caret if there's been no caret movement since last repaint
		if (caretUpdated) {
			caretShowing = false;
		} else {
			caretShowing = !caretShowing;
		}
		caretUpdated = false;

		if (caretShowing && caretPos > -1 && hasFocus()) {
			g2.setColor(Color.black);
			g2.fillRect((int) caretPos - scrollOffset + insets.left,
					textBottom - fontHeight + 4, 1, fontHeight);
			g2.setPaintMode();
		}

	}

	private float getLength(String text) {
		if (text == null || text.length() == 0) {
			return 0;
		}
		TextLayout layout = new TextLayout(text, font, frc);
		return layout.getAdvance();
	}

	private void drawText(String str, boolean selected/* , Color bg */) {
		if ("".equals(str)) {
			return;
		}

		// compute advance
		FontMetrics metrics = g2.getFontMetrics(font);
		float advance = metrics.stringWidth(str);

		if (selected) {
			g2.setColor(getSelectionColor());
			g2.fillRect((int) pos - scrollOffset + insets.left,
					textBottom - fontHeight + 4, (int) advance, fontHeight);
			g2.setColor(getSelectedTextColor());
		}
		// there is no background coloring now
		/*
		 * if (bg != null) { Color col = g2.getColor(); g2.setColor(bg);
		 * g2.fillRect((int) pos - scrollOffset + insets.left, textBottom -
		 * fontHeight + 4, (int) advance, fontHeight); g2.setColor(col); }
		 */

		// g2.setClip(0, 0, width, height);

		if (pos - scrollOffset + insets.left >= 0
				&& pos + advance - scrollOffset <= width) {
			g2.drawString(str, pos - scrollOffset + insets.left, textBottom);
		}

		pos += advance;
	}

	@Override
	public void paste() {
		super.paste();

		String text = getText();

		// make sure <TAB> can't get pasted into Input Bar
		if (text.indexOf('\t') > -1) {
			int pos2 = getCaretPosition();
			setText(text.replace('\t', ' '));
			setCaretPosition(pos2);
		}
	}


}
