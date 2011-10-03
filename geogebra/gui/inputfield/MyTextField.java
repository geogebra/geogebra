package geogebra.gui.inputfield;

import geogebra.gui.VirtualKeyboardListener;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.SymbolTable;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

/**
 * Extends JTextField to add these features:
 * 
 * 1) Custom text drawing with dynamic coloring of bracket symbols 
 * 2) Support for in-line icons 
 * 3) Popup symbol table to insert special characters where the popup 
 *    is triggered by either a mouse click or ctrl-up
 * 
 */
public class MyTextField extends JTextField implements ActionListener, FocusListener, VirtualKeyboardListener, CaretListener {

	private Application app;

	// symbol table popup fields
	private JPopupMenu popup;
	private MyTextField thisField = this;
	private SymbolTable symbolTable;
	private int caretPosition; // restores caret position when popup is done 
	private ImageIcon icon = GeoGebraIcon.createSymbolTableIcon(this.getFont(), false);
	private ImageIcon rollOverIcon = GeoGebraIcon.createSymbolTableIcon(this.getFont(), true);
	private boolean showSymbolTableIcon = false;

	// colored character rendering fields
	boolean caretUpdated = true;
	boolean caretShowing = true;


	// border button fields
	private BorderButton borderBtn;
	private Border defaultBorder;

	
	

	/************************************
	 * Construct an instance of MyTextField without a fixed column width
	 * @param guiManager
	 */
	public MyTextField(Application app) {
		super();
		this.app = app;
		initField();
	}

	/************************************
	 * Construct an instance of MyTextField with a fixed column width
	 * @param guiManager
	 * @param columns
	 */
	public MyTextField(Application app, int columns) {
		super(columns);
		this.app = app;
		initField();
	}


	
	/**
	 * Initializes the field: registers listeners, creates and sets the BorderButton
	 */
	private void initField(){
		setOpaque(true);
		addFocusListener(this);
		addCaretListener(this);

		JTextField dummy = new JTextField();		
		defaultBorder = dummy.getBorder();
		borderBtn = new BorderButton(this);
		borderBtn.setBorderButton(0, icon, this);
		setDefaultBorder();
	}


	//====================================================
	//     BorderButton
	//====================================================
	
	public BorderButton getBorderButton() {
		return borderBtn;
	}

	private void setDefaultBorder(){		
		super.setBorder(BorderFactory.createCompoundBorder(defaultBorder, borderBtn));
	}

	protected void setBorderButton(int index, ImageIcon icon, ActionListener al){
		borderBtn.setBorderButton(index, icon, al);
		setDefaultBorder();
	}

	protected void setBorderButtonVisible(int index, boolean isVisible){
		borderBtn.setIconVisible(index, isVisible);
		setDefaultBorder();
	}
	protected boolean isBorderButtonVisible(int index){
		return	borderBtn.isIconVisible(index);
	}

	/**
	 * Overrrides setBorder to prevent removal of the BorderButton
	 */
	public void setBorder(Border border){
		super.setBorder(BorderFactory.createCompoundBorder(border, borderBtn));
	}
	

	//====================================================
	//     Event Handlers, Listeners
	//====================================================
	
	public void focusGained(FocusEvent e) {

		//	TODO: can't remember why the caret position was reset like this,
		// a trick to keep the Mac OS from selecting the field?
		//
		// now removed - stops the text being highlighted #709
		//thisField.setCaretPosition(thisField.getCaretPosition());

		if(showSymbolTableIcon && hasFocus())
			borderBtn.setIconVisible(0, true);
		thisField.repaint();

		if (app.getGuiManager() != null)
			app.getGuiManager().setCurrentTextfield((VirtualKeyboardListener)this, false);
	}

	public void focusLost(FocusEvent e) {

		if(showSymbolTableIcon)
			borderBtn.setIconVisible(0, false);
		thisField.repaint();

		if (app.getGuiManager() != null)
			app.getGuiManager().setCurrentTextfield(null, !(e.getOppositeComponent() instanceof VirtualKeyboard));
	}

	

	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();
		if(cmd.equals(0 + BorderButton.cmdSuffix)){

			if(popup == null)
				createPopup();
			symbolTable.updateFonts();
			caretPosition = thisField.getCaretPosition();
			Dimension d  = popup.getPreferredSize();
			popup.show(thisField, thisField.getX() + thisField.getWidth() - d.width, - d.height);
		}
	}

	
	/**
	 * Caret update
	 */
	public void caretUpdate(CaretEvent e) {
		caretUpdated = true;
		repaint();
	}

	

	/**
	 * Inserts a string into the text at the current caret position
	 */
	public void insertString(String text) {

		int start = getSelectionStart();
		int end = getSelectionEnd();      

		//    clear selection if there is one
		if (start != end) {
			int pos = getCaretPosition();
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));            
			setText(sb.toString());
			if (pos < sb.length()) setCaretPosition(pos);
		}


		int pos = getCaretPosition();
		String oldText = getText();
		StringBuilder sb = new StringBuilder();
		sb.append(oldText.substring(0, pos));
		sb.append(text);
		sb.append(oldText.substring(pos));            
		setText(sb.toString());

		//setCaretPosition(pos + text.length());
		final int newPos = pos + text.length();


		// make sure AutoComplete works
		if (this instanceof AutoCompleteTextField) {
			AutoCompleteTextField tf = (AutoCompleteTextField)this;
			tf.updateCurrentWord(false);
			tf.startAutoCompletion();
		}

		// Under a Mac OS the string is always selected after the insert. A runnable prevents
		// this by resetting the caret to cancel the selection. 
		SwingUtilities.invokeLater(new Runnable() {  
			public void run() {  
				setCaretPosition(newPos); 
			}   
		}); 

		//TODO: tried to keep the Mac OS from auto-selecting the field by resetting the
		// caret, but not working yet
		//	setCaret(new DefaultCaret());
		//	setCaretPosition(newPos); 

	}



	/** 
	 * Creates an instance of JPopupMenu and adds a symbol table to it.
	 */
	private void createPopup(){
		popup = new JPopupMenu();
		popup.setFocusable(false);
		symbolTable = new SymbolTable(app, this);
		popup.add(symbolTable);
		popup.setBorder(BorderFactory.createLineBorder(SystemColor.controlShadow));
	}

	/**
	 * Sets a flag to show the symbol table icon when the field is focused
	 * @param showSymbolTableIcon
	 */
	public void setShowSymbolTableIcon(boolean showSymbolTableIcon) {
		this.showSymbolTableIcon = showSymbolTableIcon;
	}



	/** 
	 * Gets the pixel location of the caret. Used to locate the popup. 
	 * */
	private Point getCaretPixelPosition(){

		int position = thisField.getCaretPosition();  
		Rectangle r;
		try {
			r = thisField.modelToView(position);
		} catch (BadLocationException e) {
			return null;
		}  
		return new Point(r.x, r.y - popup.getPreferredSize().height-10);
	}


	/** 
	 * Hides the popup and inserts selected symbol. (Called by symbol table
	 * on Enter key press). 
	 * */
	public void handlePopupSelection(){	
		popup.setVisible(false);
		setCaretPosition(caretPosition);
		insertString((String) symbolTable.getSelectedValue());
	}

	/**
	 * Overrides processKeyEvents so that the symbol table popup can be
	 * triggered by ctrl-up.
	 * */
	public void processKeyEvent(KeyEvent e) {

		int keyCode = e.getKeyCode(); 

		if ((e.isControlDown()||Application.isControlDown(e)) && keyCode == KeyEvent.VK_UP){
			caretPosition = thisField.getCaretPosition();
			if(popup == null)
				createPopup();
			symbolTable.updateFonts();
			popup.show(thisField, getCaretPixelPosition().x, getCaretPixelPosition().y);
			return;
		}

		if(popup != null && popup.isShowing() && e.getID()==KeyEvent.KEY_PRESSED){

			switch(keyCode){

			case KeyEvent.VK_ENTER:
				handlePopupSelection();
				return;

			case KeyEvent.VK_ESCAPE:
				popup.setVisible(false);
				return;

			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:

				int row = symbolTable.getSelectedRow();
				int column = symbolTable.getSelectedColumn();
				if(keyCode == KeyEvent.VK_RIGHT && column != symbolTable.getColumnCount()-1) ++column;	
				if(keyCode == KeyEvent.VK_LEFT && column >= 0) --column;	
				if(keyCode == KeyEvent.VK_DOWN && row != symbolTable.getRowCount()-1) ++row;
				if(keyCode == KeyEvent.VK_UP && row >= 0) --row; 

				symbolTable.changeSelection(row, column, false, false);
				return;	

			default:
				popup.setVisible(false);
				return;
			}
		}
		super.processKeyEvent(e);
	}




	private float pos = 0;
	private int scrollOffset = 0;
	private int width = 0, height = 0, textBottom, fontHeight;
	private FontRenderContext frc;
	private Font font;
	private Graphics2D g2;
	private Insets insets;

	public void paintComponent(Graphics gr) {

		// moving caret doesn't work without this... why?
		super.paintComponent(gr);

		// flash caret if there's been no caret movement since last repaint
		if (caretUpdated) caretShowing = false;
		else caretShowing = !caretShowing;

		caretUpdated = false;

		g2 = (Graphics2D)gr;

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);

		insets = getInsets();

		String text = getText();

		width = getWidth();
		height = getHeight();

		//g2.setClip(0, 0, w, h);

		fontHeight = g2.getFontMetrics().getHeight();
		textBottom = (height - fontHeight) / 2 + fontHeight - 4;
		//int x = this.getInsets().left;

		g2.setColor(Color.white);
		g2.setClip(0, 0, width, height);
		g2.fillRect(0, 0, width, height);

		frc = ((Graphics2D) g2).getFontRenderContext();

		scrollOffset = getScrollOffset();

		font = g2.getFont();
		int caret = getCaretPosition();


		pos = 0;		

		// adjust if right-aligned
		if (getHorizontalAlignment() == JTextField.RIGHT) {
			pos = Math.max(0,getHorizontalVisibility().getExtent() - getLength(text));
		}

		int bracket1pos = -1;
		int bracket2pos = -1;

		int searchDirection = 0;
		int searchEnd = 0;

		char bracketToMatch = ' ';
		char oppositeBracketToMatch = ' ';

		if (getSelectionStart() == getSelectionEnd())
			if (caret > 0 && caret <= text.length()) {

				char c = text.charAt(caret-1);
				bracket1pos = caret - 1;
				switch (c) {
				case '(' :
					searchDirection = +1;
					searchEnd = text.length();
					oppositeBracketToMatch = '(';
					bracketToMatch = ')';
					break;
				case '{' :
					searchDirection = +1;
					searchEnd = text.length();
					oppositeBracketToMatch = '{';
					bracketToMatch = '}';
					break;
				case '[' :
					searchDirection = +1;
					searchEnd = text.length();
					oppositeBracketToMatch = '[';
					bracketToMatch = ']';
					break;
				case ')' :
					searchDirection = -1;
					searchEnd = -1;
					oppositeBracketToMatch = ')';
					bracketToMatch = '(';
					break;
				case '}' :
					searchDirection = -1;
					searchEnd = -1;
					oppositeBracketToMatch = '}';
					bracketToMatch = '{';
					break;
				case ']' :
					searchDirection = -1;
					searchEnd = -1;
					oppositeBracketToMatch = ']';
					bracketToMatch = '[';
					break;
				default:
					searchDirection = 0;
					bracket1pos = -1;
					bracket2pos = -1;
					break;

				}

			}
		//Lines containing  textMode by Zbynek Konecny, 2010-05-09
		boolean textMode = false;

		if (searchDirection != 0) {
			int count = 0;
			for (int i = caret - 1 ; i != searchEnd ; i += searchDirection) {
				if(text.charAt(i) == '\"') textMode = !textMode;
				if (!textMode && text.charAt(i) == bracketToMatch) count ++;
				else if (!textMode && text.charAt(i) == oppositeBracketToMatch) count --;

				if (count == 0) {
					bracket2pos = i;
					break;
				}
			}
		}


		int selStart = getSelectionStart();
		int selEnd = getSelectionEnd();



		float caretPos = -1;

		if (caret == 0) caretPos = pos;
		textMode = false;
		for (int i = 0 ; i < text.length() ; i++) {
			if(text.charAt(i) == '\"') textMode = !textMode;
			if (i == bracket1pos || i == bracket2pos) {
				if (bracket2pos > -1) g2.setColor(Color.RED); // matched
				else g2.setColor(Color.GREEN); // unmatched
			}
			else g2.setColor(Color.BLACK);
			if(textMode || text.charAt(i) == '\"')g2.setColor(Color.GRAY);
			drawText(text.charAt(i)+"", i >= selStart && i < selEnd);

			if (i + 1 == caret) caretPos = pos;
		}

		if (caretShowing && caretPos > -1 && hasFocus()) {
			g2.setColor(Color.black);
			g2.fillRect((int)caretPos - scrollOffset + insets.left, textBottom - fontHeight + 4 , 1, fontHeight);
			g2.setPaintMode();

		}
	}

	private float getLength(String text) {
		if (text == null || text.length() == 0) return 0;
		TextLayout layout = new TextLayout(text, font, frc);
		return layout.getAdvance();

	}

	private void drawText(String str, boolean selected) {
		if ("".equals(str)) return;
		TextLayout layout = new TextLayout(str, font, frc);
		g2.setFont(font);
		float advance = layout.getAdvance();

		if (selected) {
			g2.setColor(getSelectionColor());
			//g2.fillRect((int)pos - scrollOffset + insets.left, insets.bottom + 2 , (int)advance, height - insets.bottom - insets.top - 4);
			g2.fillRect((int)pos - scrollOffset + insets.left, textBottom - fontHeight + 4 , (int)advance, fontHeight);
			g2.setColor(getSelectedTextColor());
		} 
		g2.setClip(0, 0, width, height);
		if (pos - scrollOffset + advance + insets.left > 0 && pos - scrollOffset < width)
			g2.drawString(str, pos - scrollOffset + insets.left, textBottom);
		//g2.drawString(str, pos - scrollOffset + insets.left, height - insets.bottom - insets.top - 4);
		pos += layout.getAdvance();

	}



}
