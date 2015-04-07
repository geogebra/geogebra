package org.geogebra.common.cas.view;

import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.App;

public interface CASTable {

	int getRowCount();

	void setLabels();

	GeoCasCell getGeoCasCell(int n);

	App getApplication();

	void deleteAllRows();

	void insertRow(int rows, GeoCasCell casCell, boolean b);

	int[] getSelectedRows();

	int getSelectedRow();

	void stopEditing();

	void startEditingRow(int selectedRow);

	CASTableCellEditor getEditor();

	void deleteRow(int rowNumber);

	void setRow(int rowNumber, GeoCasCell casCell);

	boolean isEditing();

	/**
	 * On web, if we insert one or more new row, or delete some, needed change
	 * the row numbers manually after the inserted or deleted rows.
	 * 
	 * @param from
	 *            the first row number to change
	 */
	void resetRowNumbers(int from);
}
