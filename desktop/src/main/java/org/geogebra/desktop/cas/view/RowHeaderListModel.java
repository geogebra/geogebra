package org.geogebra.desktop.cas.view;

import javax.swing.AbstractListModel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * List model for row headers
 */
public class RowHeaderListModel extends AbstractListModel implements
		TableModelListener {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private int size;

	/**
	 * @param table
	 *            creates new model
	 */
	public RowHeaderListModel(JTable table) {
		this.table = table;
		table.getModel().addTableModelListener(this);
		size = table.getRowCount();
	}

	public int getSize() {
		return size;
	}

	public Object getElementAt(int index) {
		return Integer.toString(index + 1);
	}

	public void tableChanged(TableModelEvent e) {
		int firstRow = e.getFirstRow();
		int lastRow = e.getLastRow();

		int oldSize = size;
		int rowCount = table.getRowCount();
		size = rowCount;

		if (rowCount > oldSize) {
			fireIntervalAdded(this, firstRow, lastRow);
		} else if (rowCount < oldSize) {
			fireIntervalRemoved(this, firstRow, lastRow);
		} else {
			fireContentsChanged(this, firstRow, lastRow);
		}
	}
}