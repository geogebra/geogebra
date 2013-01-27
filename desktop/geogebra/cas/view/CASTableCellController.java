package geogebra.cas.view;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.main.AppD;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Controller for CAS cell
 *
 */
public class CASTableCellController implements KeyListener, MouseListener{
	
	private CASViewD view;
	private CASTableD table;
	private AppD app;
	private CASTableCellEditorD tableCellEditor;
	private boolean rightClick = false;
	
	/**
	 * @param view CAS view
	 */
	public CASTableCellController(CASViewD view) {		
		this.view = view;
		this.app = view.getApplication();
		table = view.getConsoleTable();
		tableCellEditor = table.getEditor();		
	}

	public void keyPressed(KeyEvent e) {
		Object src = e.getSource();		
		boolean consumed = e.isConsumed();
		if (src == tableCellEditor.getInputArea())
			consumed = handleKeyPressedInputTextField(e);
		if(!consumed)
			view.getApplication().getGlobalKeyDispatcher().handleGeneralKeys(e);
	}

	private boolean handleKeyPressedInputTextField(final KeyEvent e) {
		if (e.isConsumed()) return true;
		
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
			else if (view.isRowEmpty(0)) {
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
				view.insertRow(null, true);
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
		return consumeEvent;
	}
	
	/**
	 * Handles pressing of Enter key after user input. The behaviour depends
	 * on the currently selected mode in the toolbar (Evaluate, Keep Input, Numeric).
	 */
	private synchronized void handleEnterKey(KeyEvent e) {
		AppD app = view.getApp();
		int mode = app.getMode();
		
		// Ctrl + Enter toggles between the modes Evaluate and Numeric
		if (AppD.isControlDown(e)) {
			if (mode == EuclidianConstants.MODE_CAS_NUMERIC) {
				app.setMode(EuclidianConstants.MODE_CAS_EVALUATE);
			} else {
				app.setMode(EuclidianConstants.MODE_CAS_NUMERIC);
			}
			app.setMode(mode);
			return;
		}
		
		// Alt + Enter toggles between the modes Evaluate and Keep Input
		if (AppD.isAltDown(e)) {
			if (mode == EuclidianConstants.MODE_CAS_KEEP_INPUT) {
				app.setMode(EuclidianConstants.MODE_CAS_EVALUATE);
			} else {
				app.setMode(EuclidianConstants.MODE_CAS_KEEP_INPUT);
			}
			app.setMode(mode);
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

	/**
	 * @return the rightClick
	 */
	public boolean isRightClick() {
		return rightClick;
	}

	/**
	 * @param rightClick
	 *            the rightClick to set
	 */
	public void setRightClick(boolean rightClick) {
		this.rightClick = rightClick;
	}


	public void mouseReleased(MouseEvent e) {
		setRightClick(AppD.isRightClickForceMetaDown(e));	
		if (isRightClick()) {
				RowContentPopupMenu popupMenu = new RowContentPopupMenu(app,
						(GeoCasCell)tableCellEditor.getCellEditorValue(), tableCellEditor, table, RowContentPopupMenu.Panel.INPUT);
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	public void keyReleased(KeyEvent arg0) {
		//do nothing; we use keyPressed
	}

	public void keyTyped(KeyEvent arg0) {
		//do nothing; we use keyPressed
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	


}
