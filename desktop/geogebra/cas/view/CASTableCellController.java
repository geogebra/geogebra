package geogebra.cas.view;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.main.Application;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CASTableCellController implements KeyListener {
	
	private CASView view;
	private CASTable table;
	private CASTableCellEditor tableCellEditor;
	private Thread evalThread;

	public CASTableCellController(CASView view) {		
		this.view = view;
		table = view.getConsoleTable();
		tableCellEditor = table.getEditor();		
	}

	public void keyPressed(KeyEvent e) {
		Object src = e.getSource();		
		if (src == tableCellEditor.getInputArea())
			handleKeyPressedInputTextField(e);
	}

	private void handleKeyPressedInputTextField(final KeyEvent e) {
		if (e.isConsumed()) return;
		
		boolean consumeEvent = false;
		boolean needUndo = false;
		
		int selectedRow = table.getSelectedRow();
		int rowCount = table.getRowCount();

		switch (e.getKeyCode()) {				
		case KeyEvent.VK_ENTER:
			handleEnterKey(e);
			consumeEvent = true;
			// needUndo remains falls because handleEnterKey handles Undo!
			break;

		case KeyEvent.VK_UP:
			if (selectedRow >= 1) {
				table.startEditingRow(selectedRow - 1);
			} 
			else if (table.isRowEmpty(0)) {
				// insert empty row at beginning
				table.insertRow(0, null, true);
				needUndo = true;
			}			
			consumeEvent = true;
			break;
			
		case KeyEvent.VK_DOWN:
			if (selectedRow != rowCount - 1) {
				table.startEditingRow(selectedRow + 1);
			} 
			else {
				// insert empty row at end
				table.insertRow(null, true);
				needUndo = true;
			}	
			consumeEvent = true;
			break;
		}

		// consume keyboard event so the table
		// does not process it again
		if (consumeEvent) {
			e.consume();
		}
		
		if (needUndo) {
			// store undo info
			view.getApp().storeUndoInfo();
		}
	}
	
	/**
	 * Handles pressing of Enter key after user input. The behaviour depends
	 * on the currently selected mode in the toolbar (Evaluate, Keep Input, Numeric).
	 */
	private synchronized void handleEnterKey(KeyEvent e) {
		Application app = view.getApp();
		int mode = app.getMode();
		
		// Ctrl + Enter toggles between the modes Evaluate and Numeric
		if (Application.isControlDown(e)) {
			if (mode == EuclidianConstants.MODE_CAS_NUMERIC) {
				app.setMode(EuclidianConstants.MODE_CAS_EVALUATE);
			} else {
				app.setMode(EuclidianConstants.MODE_CAS_NUMERIC);
			}
			return;
		}
		
		// Alt + Enter toggles between the modes Evaluate and Keep Input
		if (Application.isAltDown(e)) {
			if (mode == EuclidianConstants.MODE_CAS_KEEP_INPUT) {
				app.setMode(EuclidianConstants.MODE_CAS_EVALUATE);
			} else {
				app.setMode(EuclidianConstants.MODE_CAS_KEEP_INPUT);
			}
			return;
		}
		
		// Enter depends on current mode
		switch (mode) {		
			case EuclidianConstants.MODE_CAS_EVALUATE:
			case EuclidianConstants.MODE_CAS_NUMERIC:
			case EuclidianConstants.MODE_CAS_KEEP_INPUT:
				// apply current tool again
				app.setMode(mode);
				break;
			
			default:
				// switch back to Evaluate
				app.setMode(EuclidianConstants.MODE_CAS_EVALUATE);
		}	
	}

	public void keyReleased(KeyEvent arg0) {
	
	}

	public void keyTyped(KeyEvent arg0) {
	
	}

}
