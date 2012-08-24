package geogebra.cas.view;

import geogebra.common.cas.view.CASTableCellEditor;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

/**
 * Cell editor; handles keystrokes 
 */
public class CASTableCellEditorD extends CASTableCell implements TableCellEditor, KeyListener, CASTableCellEditor {
	
	private static final long serialVersionUID = 1L;
	
	private JTable table;
	private GeoCasCell cellValue;
	
	private boolean editing = false;
	private int editingRow;
	private String inputOnEditingStart;
		
	private ArrayList<CellEditorListener> listeners = new ArrayList<CellEditorListener>();

	/**
	 * @param view CAS view
	 */
	public CASTableCellEditorD(CASViewD view) {
		super(view);

		getInputArea().addKeyListener(this);
	}

	public Component getTableCellEditorComponent(JTable casTable, Object value, boolean isSelected, int row, int column) {
		if (value instanceof GeoCasCell) {						
			editing = true;
			editingRow = row;
									
			// set CASTableCell value
			this.cellValue = (GeoCasCell) value;
			this.table = casTable;
			inputOnEditingStart = cellValue.getInput(StringTemplate.defaultTemplate);
			setValue(cellValue);				
							
			// update font and row height
			setFont(view.getCASViewComponent().getFont());
			updateTableRowHeight(casTable, row);				
			
			// Set width of editor to the width of the table column.
			// This will allow scrolling of strings that are wider than the cell. 
			setInputPanelWidth(casTable.getParent().getWidth());				
		} 
		return this;
	}	
	
	/**
	 * @return input text
	 */
	public String getInputText() {	
		return getInputArea().getText();
	}
	
	public String getInputSelectedText() {	
		return getInputArea().getSelectedText();
	}
	
	public int getInputSelectionStart() {	
		return getInputArea().getSelectionStart();
	}
	
	public int getInputSelectionEnd() {	
		return getInputArea().getSelectionEnd();
	}	
	
	public void setInputSelectionStart(int pos) {	
		getInputArea().setSelectionStart(pos);
	}
	
	public void setInputSelectionEnd(int pos) {
		getInputArea().setSelectionEnd(pos);
	}	
	public int getCaretPosition(){
		return getInputArea().getCaretPosition();
	}
	public void setCaretPosition(int i){
		getInputArea().setCaretPosition(i);
	}
	
	/**
	 * Replaces selection with given text
	 * @param text text
	 */
	public void insertText(String text) {
		getInputArea().replaceSelection(text);
		//getInputArea().requestFocusInWindow();
	}
	
	/**
	 * Clears input area
	 */
	public void clearInputSelectionText() {
		getInputArea().setText(null);
	}
		
	public boolean stopCellEditing() {	
		// update cellValue's input using editor content
		if (editing && cellValue != null) {
			String newInput = getInput();
			if (!newInput.equals(inputOnEditingStart))
				cellValue.setInput(getInput());
			fireEditingStopped();
		}				
		
		return true;
	}
	
	public void cancelCellEditing() {
		// update cellValue's input using editor content
		if (editing && cellValue != null) {
			String newInput = getInput();
			if (!newInput.equals(inputOnEditingStart))
				cellValue.setInput(getInput());	
			fireEditingCanceled();
		}
	}
	
	/**
	 * @return whether this cell is being edited
	 */
	public boolean isEditing() {
		return editing; //&& hasFocus();
	}
	
	public Object getCellEditorValue() {		
		return cellValue;
	}
	/**
	 * Editing canceled
	 */
	protected void fireEditingCanceled() {				
		if (editing && editingRow < table.getRowCount()) {	
			ChangeEvent ce = new ChangeEvent(this);
			for (int i=0; i < listeners.size(); i++) {
				CellEditorListener l = listeners.get(i);
				l.editingCanceled(ce);
			}
		}
		
		editing = false;
	}
	/**
	 * Editing stopped
	 */
	protected void fireEditingStopped() {		
		if (editing && editingRow < table.getRowCount()) {	
			ChangeEvent ce = new ChangeEvent(this);
			for (int i=0; i < listeners.size(); i++) {
				CellEditorListener l = listeners.get(i);
				l.editingStopped(ce);
			}
		}
		
		editing = false;		
	}

	public boolean isCellEditable(EventObject anEvent) {	
		return true;
	}

	public void removeCellEditorListener(CellEditorListener l) {
		listeners.remove(l);
	}
	
	public void addCellEditorListener(CellEditorListener l) {
		if (!listeners.contains(l))
			listeners.add(l);
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}
	
	/**
	 * @return index of editing row
	 */
	public final int getEditingRow() {
		return editingRow;
	}
	
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {
			case KeyEvent.VK_ESCAPE:
				e.consume();
				getInputArea().setText("");				
				break;
				
//			case KeyEvent.VK_ENTER:
//				e.consume();
//				stopCellEditing();				
//				break;				
		}
	}

	public void keyReleased(KeyEvent arg0) {
		//do nothing
	}

	public void keyTyped(KeyEvent e) {
		char ch = e.getKeyChar();
		JTextComponent inputArea = getInputArea();
		String text = inputArea.getText();
		
		// if closing paranthesis is typed and there is no opening parenthesis for it
		// add one in the beginning
		switch (ch){				
			
			case ' ':
			case '|':
				// insert output of previous row (not in parentheses)
				if (editingRow > 0 && text.length() == 0) {
					GeoCasCell selCellValue = view.getConsoleTable().getGeoCasCell(editingRow - 1);				
					inputArea.setText(selCellValue.getOutput(StringTemplate.defaultTemplate) + " ");
				}
				break;
				
			case ')':
				// insert output of previous row in parentheses		
				if (editingRow > 0 && text.length() == 0) {
					GeoCasCell selCellValue = view.getConsoleTable().getGeoCasCell(editingRow - 1);				
					String prevOutput = selCellValue.getOutput(StringTemplate.defaultTemplate);
					inputArea.setText("(" +  prevOutput);
				}
				break;		
				
			case '=':
				// insert input of previous row
				if (editingRow > 0 && text.length() == 0) {
					GeoCasCell selCellValue = view.getConsoleTable().getGeoCasCell(editingRow - 1);				
					inputArea.setText(selCellValue.getInput(StringTemplate.defaultTemplate));
					e.consume();
				}
				break;
		}
	}
	
//	public void focusGained(FocusEvent arg0) {
//		getInputArea().requestFocusInWindow();
////		getInputArea().setCaretPosition(getInput().length());
////		getInputArea().setSelectionStart(getInput().length());
////		getInputArea().setSelectionEnd(getInput().length());
//		
//		// TODO: remove
//		System.out.println("focus gained, editor row " + editingRow);
//		lastFocusRow = editingRow;
//	}
//	
//	int lastFocusRow;
//
//	public void focusLost(FocusEvent arg0) {
//		//Application.printStacktrace("focus lost " + editingRow);
//		// TODO: remove
//		System.out.println("focus lost: lastFocusRow " + lastFocusRow + ", editingRow " + editingRow);
//	
//		if (editingRow == lastFocusRow) {
//			stopCellEditing();
//			casTable.updateRow(editingRow);
//		}
//	}	
}
