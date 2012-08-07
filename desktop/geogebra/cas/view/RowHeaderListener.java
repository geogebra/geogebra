package geogebra.cas.view;

import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.main.AppD;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Handles mouse and key events in row headers of the CAS table
 *
 */
public class RowHeaderListener extends MouseAdapter implements KeyListener, ListSelectionListener, MouseMotionListener {

	private final CASTableD table;
	private final JList rowHeader;
	private int mousePressedRow;
	private boolean rightClick;

	/**
	 * @param table CAS table
	 * @param rowHeader row headers
	 */
	public RowHeaderListener(CASTableD table, JList rowHeader) {
		this.table = table;
		this.rowHeader = rowHeader;
	}

	
	@Override
	public void mousePressed(MouseEvent e) {
		rightClick = AppD.isRightClick(e);
		table.stopEditing();
		mousePressedRow = rowHeader.locationToIndex(e.getPoint());
		rowHeader.requestFocus();
	}

	public void mouseDragged(MouseEvent e) {
		e.consume();

		// update selection
		int mouseDraggedRow = rowHeader.locationToIndex(e.getPoint());

		// make sure mouse pressed is initialized, this may not be the case
		// after closing the popup menu
		if (mousePressedRow < 0) {
			table.stopEditing();
			mousePressedRow = mouseDraggedRow;
		}
		if(AppD.isControlDown(e))
			rowHeader.addSelectionInterval(mousePressedRow, mouseDraggedRow);
		else
			rowHeader.setSelectionInterval(mousePressedRow, mouseDraggedRow);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		e.consume();
		mousePressedRow = -1;

		// int mouseReleasedRow = rowHeader.locationToIndex(e.getPoint());

		// update selection if:
		// mouseReleasedRow is not selected yet
		// or
		// we did a left click without drag
		// if (!rowHeader.isSelectedIndex(mouseReleasedRow) || !rightClick &&
		// !dragged) {
		// rowHeader.setSelectedIndex(mouseReleasedRow);
		// }

		// handle right click
		int releasedRow = table.rowAtPoint(e.getPoint());
		int totalHeight = 0;
		for(int i=0;i<=releasedRow;i++)
			totalHeight += table.getRowHeight(i);
		if(e.getPoint().y > totalHeight - 22){
			GeoCasCell clickedCell =  table.getGeoCasCell(table.rowAtPoint(e.getPoint()));		
			if(table.isEditing()){
				table.stopEditing();
			}
			clickedCell.toggleTwinGeoEuclidianVisible();	
		}
				

		if (rightClick && rowHeader.getSelectedIndices().length>0) {
			RowHeaderPopupMenu popupMenu = new RowHeaderPopupMenu(rowHeader,
					table);
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//not needed, we handle mouse events in mouseReleased
	}

	public void mouseMoved(MouseEvent e) {
		e.consume();
	}

	public void keyPressed(KeyEvent e) {
		boolean undoNeeded = false;

		switch (e.getKeyCode()) {
		case KeyEvent.VK_DELETE:
		case KeyEvent.VK_BACK_SPACE:
			int[] selRows = rowHeader.getSelectedIndices();
			undoNeeded = table.getCASView().deleteCasCells(selRows);
			break;
		}

		if (undoNeeded) {
			// store undo info
			table.getApplication().storeUndoInfo();
		}
	}

	public void keyReleased(KeyEvent e) {
		//not needed, we handle key events in keyPressed
	}

	public void keyTyped(KeyEvent e) {
		//not needed, we handle key events in keyPressed
	}


	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		int minIndex = lsm.getMinSelectionIndex();
        int maxIndex = lsm.getMaxSelectionIndex();
        if(minIndex == maxIndex)
        	table.startEditingRow(minIndex);
	}
}
