package geogebra.common.cas.view;

import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.App;

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
}
