package geogebra.common.cas.view;

import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.AbstractApplication;

public interface CASTable {
	/** column of the table containing CAS cells */
	public final static int COL_CAS_CELLS = 0;

	public final static int COPY_OFF = 0;
	public final static int COPY_STATIC = 1;
	public final static int COPY_DYNAMIC = 2;
	public final static int COPY_PLOT = 3;

	int getRowCount();

	int getRowHeight(int i);

	void setLabels();

	GeoCasCell getGeoCasCell(int n);

	AbstractApplication getApplication();

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

	void setCopyMode(int i);

}
