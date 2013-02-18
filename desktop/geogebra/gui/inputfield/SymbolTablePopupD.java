package geogebra.gui.inputfield;

import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import geogebra.common.gui.util.TableSymbols;
import geogebra.gui.util.SelectionTable;
import geogebra.main.AppD;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;

/**
 * Prepares and shows a JPopupMenu containing a symbol table for MyTextField.
 * @author G. Sturr
 *
 */
public class SymbolTablePopupD {

	private MyTextField textField;
	private JPopupMenu popup;

	private KeyListener keyListener;
	private KeyListener[] textFieldKeyListeners;

	private SelectionTable symbolTable;
	private AppD app;
	private Locale locale;
	private boolean openUpwards = true;



	/******************************************************
	 * Constructs a symbol table popup.
	 * @param app
	 * @param textField
	 */
	public SymbolTablePopupD(AppD app, MyTextField textField){

		this.app = app;
		this.textField = textField;
		
		popup = new JPopupMenu();
		popup.setFocusable(false);
		popup.setBorder(BorderFactory.createLineBorder(SystemColor.controlShadow));

		createSymbolTable();
		registerListeners();
		locale = app.getLocale();
		
		app.setComponentOrientation(popup);
	}



	private void createSymbolTable() {

		symbolTable = new SelectionTable(app, 
				TableSymbols.basicSymbols(app.getLocalization()), 
				-1,10, 
				new Dimension(24,24), 
				geogebra.common.gui.util.SelectionTable.MODE_TEXT);

		symbolTable.setShowGrid(true);
		symbolTable.setHorizontalAlignment(SwingConstants.CENTER);
		symbolTable.setSelectedIndex(1);
		symbolTable.setFocusable(false);
		symbolTable.setToolTipArray(TableSymbols.basicSymbolsToolTips(app.getLocalization()));
		
		symbolTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { handleMouseClick(e); }
		});
				
		popup.removeAll();
		popup.add(symbolTable);		
	}


	public void setLabels(){
		createSymbolTable();
	}



	private class PopupListener implements PopupMenuListener {

		public void popupMenuCanceled(PopupMenuEvent e) {
		}

		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			
			// remove text field key listeners and replace with our own;
			textFieldKeyListeners = textField.getKeyListeners();
			for (KeyListener listener: textFieldKeyListeners) {
				textField.removeKeyListener(listener);
			}
			textField.addKeyListener(keyListener);
		}
		
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			// return old key listeners to the text field
			textField.removeKeyListener(keyListener);
			for (KeyListener listener: textFieldKeyListeners) {
				textField.addKeyListener(listener);
			}				
		}
		
	}

	

	private void registerListeners() {

		// create key listener (will be added by PopupListener)
		keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) { handleSpecialKeys(e); }
		};

		popup.addPopupMenuListener(new PopupListener());
	}


	
	
	//=======================================================
	//     Handle popup visiblilty
	//=======================================================

	
	/** 
	 * Gets the pixel location of the caret. Used to locate the popup. 
	 * */
	private Point getCaretPixelPosition(){
		int position = textField.getCaretPosition();  
		Rectangle r;
		try {
			r = textField.modelToView(position);
		} catch (BadLocationException e) {
			return null;
		}  
		return new Point(r.x, r.y - popup.getPreferredSize().height-10);
	}
	
	public void showPopup(boolean locateAtFieldEnd) {		

		symbolTable.updateFonts();
		if(locale != app.getLocale()){
			locale = app.getLocale();
			setLabels();
		}

		if(locateAtFieldEnd){
			Dimension d  = popup.getPreferredSize();
			if (openUpwards){
				popup.show(textField, textField.getX() + textField.getWidth() - d.width, - d.height);
			}else{
				popup.show(textField, textField.getX() + textField.getWidth() - d.width, 
						textField.getY() + textField.getHeight());
			}
		}else{
			if (openUpwards){
				popup.show(textField, getCaretPixelPosition().x, getCaretPixelPosition().y);
			}else{
				popup.show(textField, getCaretPixelPosition().x, 
						textField.getY() + textField.getHeight());
			}
		}
	}

	private boolean isPopupVisible() {
		return popup.isVisible();
	}

	private void hidePopup() {
		if (!isPopupVisible()) {
			return;
		}
		popup.setVisible(false);
	}


	
	/**
	 * @param openUpwards
	 *            true => popup opens sitting above the textfield, 
	 *            false => popup opens below
	 */
	public void setOpenUpwards(boolean openUpwards) {
		this.openUpwards = openUpwards;
	}



	//=======================================================
	//     Handle key and mouse events
	//=======================================================


	public void handleMouseClick(MouseEvent e){
		handlePopupSelection();
		hidePopup();
	}

	public void handlePopupSelection(){	
		if(symbolTable.getSelectedValue() != null)
			textField.insertString((String) symbolTable.getSelectedValue());
	}


	public void handleSpecialKeys(KeyEvent keyEvent) {
		if (!isPopupVisible()) {
			return;
		}

		int keyCode = keyEvent.getKeyCode();

		switch(keyCode) {
		case VK_ESCAPE:			// [ESC] cancel the popup and undo any changes
			hidePopup();
			keyEvent.consume();
			break;

		case VK_ENTER:
			handlePopupSelection();
			hidePopup();
			keyEvent.consume();
			break;

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
			keyEvent.consume();
			break;

		default:
			hidePopup();
		}
	}

}
