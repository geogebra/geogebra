package geogebra.cas.view;

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
public class CASTableCellController extends geogebra.common.cas.view.CASTableCellController implements KeyListener, MouseListener{
	
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
			handleEnterKey(geogebra.euclidian.event.KeyEvent.wrapEvent(e),app);
			consumeEvent = true;
			// needUndo remains false because handleEnterKey handles Undo!
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
