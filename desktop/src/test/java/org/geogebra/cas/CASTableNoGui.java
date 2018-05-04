package org.geogebra.cas;

import org.geogebra.common.cas.view.CASTable;
import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.App;

public class CASTableNoGui implements CASTable {

	private String[] inputs;
	private GeoCasCell[] cells;

	public CASTableNoGui(String[] inputs, App app) {
		this.inputs = inputs;
		this.cells = new GeoCasCell[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			this.cells[i] = new GeoCasCell(app.getKernel().getConstruction());
		}
	}

	public int getRowCount() {
		return inputs.length;
	}

	public void setLabels() {
		// not needed
	}

	public GeoCasCell getGeoCasCell(int n) {
		return cells[n];
	}

	public App getApplication() {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteAllRows() {
		// TODO Auto-generated method stub

	}

	public void insertRow(int rows, GeoCasCell casCell, boolean startEditing) {
		// TODO Auto-generated method stub

	}

	public int[] getSelectedRows() {
		// TODO Auto-generated method stub
		return new int[] { 0 };
	}

	public int getSelectedRow() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void stopEditing() {
		// TODO Auto-generated method stub

	}

	public void startEditingRow(int selectedRow) {
		// TODO Auto-generated method stub

	}

	public CASTableCellEditor getEditor() {
		return new CASEditorNoGui(inputs[0]);
	}

	public void deleteRow(int rowNumber) {
		// TODO Auto-generated method stub

	}

	public void setRow(int rowNumber, GeoCasCell casCell) {
		// TODO Auto-generated method stub

	}

	public boolean isEditing() {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetRowNumbers(int from) {
		// TODO Auto-generated method stub

	}

	public boolean hasEditor() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean keepEditing(boolean failure, int rowNum) {
		// TODO Auto-generated method stub
		return false;
	}

}
