package geogebra.common.cas.view;

import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.App;

public interface CASTable {
	/** column of the table containing CAS cells */
	public final static int COL_CAS_CELLS = 0;

	int getRowCount();

	int getRowHeight(int i);

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

	boolean isRowEmpty(int i);

	void insertRow(GeoCasCell newRowValue, boolean b);

	void deleteRow(int rowNumber);

	void setRow(int rowNumber, GeoCasCell casCell);
}
