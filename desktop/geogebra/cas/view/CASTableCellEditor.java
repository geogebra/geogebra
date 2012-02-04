package geogebra.cas.view;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
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

//public class CASTableCellEditor extends DefaultCellEditor implements
//		TableCellEditor {
public class CASTableCellEditor extends CASTableCell implements TableCellEditor, KeyListener {
	
	private static final long serialVersionUID = 1L;
	
	private JTable table;
	private GeoCasCell cellValue;
	
	private boolean editing = false;
	private int editingRow;
	private String inputOnEditingStart;
		
	private ArrayList<CellEditorListener> listeners = new ArrayList<CellEditorListener>();

	public CASTableCellEditor(CASView view) {
		super(view);

		getInputArea().addKeyListener(this);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value instanceof GeoCasCell) {						
			editing = true;
			editingRow = row;
									
			// set CASTableCell value
			this.cellValue = (GeoCasCell) value;
			this.table = table;
			inputOnEditingStart = cellValue.getInput(StringTemplate.get(StringType.GEOGEBRA));
			setValue(cellValue);				
							
			// update font and row height
			setFont(view.getFont());
			updateTableRowHeight(table, row);				
			
			// Set width of editor to the width of the table column.
			// This will allow scrolling of strings that are wider than the cell. 
			setInputPanelWidth(table.getParent().getWidth());				
		} 
		return this;
	}	
	
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
	
	public void insertText(String text) {
		getInputArea().replaceSelection(text);
		//getInputArea().requestFocusInWindow();
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
	
	public boolean isEditing() {
		return editing; //&& hasFocus();
	}
	
	public Object getCellEditorValue() {		
		return cellValue;
	}

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
					inputArea.setText(selCellValue.getOutput() + " ");
				}
				break;
				
			case ')':
				// insert output of previous row in parentheses		
				if (editingRow > 0 && text.length() == 0) {
					GeoCasCell selCellValue = view.getConsoleTable().getGeoCasCell(editingRow - 1);				
					String prevOutput = selCellValue.getOutput();
					inputArea.setText("(" +  prevOutput);
				}
				break;		
				
			case '=':
				// insert input of previous row
				if (editingRow > 0 && text.length() == 0) {
					GeoCasCell selCellValue = view.getConsoleTable().getGeoCasCell(editingRow - 1);				
					inputArea.setText(selCellValue.getInput(StringTemplate.get(StringType.GEOGEBRA)));
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
