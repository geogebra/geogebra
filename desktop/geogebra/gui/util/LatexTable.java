package geogebra.gui.util;

import geogebra.gui.TextInputDialog;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;

public class LatexTable extends SelectionTable implements MenuElement{


	private Application app;
	private TextInputDialog inputDialog;
	private String[] latexArray;
	private PopupMenuButton popupButton;
	private int caretPosition = 0;
	private int mode;

	public LatexTable(Application app, TextInputDialog textInputDialog, PopupMenuButton popupButton, 
			String[] latexArray, int rows, int columns, int mode ){

		super(app, latexArray, rows,columns, new Dimension(24,24), mode);
		this.app = app;
		this.inputDialog = textInputDialog;
		this.latexArray = latexArray;
		this.popupButton = popupButton;
		this.mode = mode;
		//setShowGrid(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		setSelectedIndex(0);
		//	this.setUseColorSwatchBorder(true);
		this.setShowGrid(true);
		this.setGridColor(GeoGebraColorConstants.TABLE_GRID_COLOR);
		this.setBorder(BorderFactory.createLineBorder(MyTable.TABLE_GRID_COLOR));
		//this.setBorder(BorderFactory.createEmptyBorder());
		this.setShowSelection(false);
	}

	public void setCaretPosition(int caretPosition){
		this.caretPosition = caretPosition;
	}



	// support for MenuElement interface

	public Component getComponent() {
		return this;
	}

	public MenuElement[] getSubElements() {
		return new MenuElement[0];
	}

	public void menuSelectionChanged(boolean arg0) {
	}

	public void processKeyEvent(KeyEvent arg0, MenuElement[] arg1, MenuSelectionManager arg2) {
	}

	public void processMouseEvent(MouseEvent arg0, MenuElement[] arg1, MenuSelectionManager arg2) {

		if(this.getSelectedIndex() >= latexArray.length) return;

		if (arg0.getID()==MouseEvent.MOUSE_RELEASED){

			// get the selected string
			StringBuffer sb = new StringBuffer(latexArray[this.getSelectedIndex()]);
			// if LaTeX string, adjust the string to include selected text within braces
			if(mode == SelectionTable.MODE_LATEX){
				
				String selText = ((InputPanel)inputDialog.getInputPanel()).getSelectedText();		
				if (selText != null) {
					sb.deleteCharAt(sb.indexOf("{")+1);
					sb.insert(sb.indexOf("{")+1, selText);
				}
			}

			// now insert the string
			inputDialog.insertString(sb.toString(), inputDialog.isLaTeX());

			// if unicode string, add the string to the recent symbol list
			if(mode == SelectionTable.MODE_TEXT){
				inputDialog.addRecentSymbol(sb.toString());
			}
			
			// notify the popup button
			popupButton.handlePopupActionEvent();
		}	
	}

}
