package org.geogebra.cas;

import org.geogebra.common.cas.view.CASTable;
import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.App;

public class CASTableNoGui implements CASTable {

	private String[] inputs;
	private GeoCasCell[] cells;
	private int selectedRow = 0;

	/**
	 * @param inputs table contents (inputs)
	 * @param app application
	 */
	public CASTableNoGui(String[] inputs, App app) {
		this.inputs = inputs;
		this.cells = new GeoCasCell[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			this.cells[i] = new GeoCasCell(app.getKernel().getConstruction());
		}
	}

	@Override
	public int getRowCount() {
		return inputs.length;
	}

	@Override
	public void setLabels() {
		// not needed
	}

	@Override
	public GeoCasCell getGeoCasCell(int n) {
		return cells[n];
	}

	@Override
	public App getApplication() {
		// mock implementation
		return null;
	}

	@Override
	public void deleteAllRows() {
		// mock implementation

	}

	@Override
	public void insertRow(int rows, GeoCasCell casCell, boolean startEditing) {
		// mock implementation

	}

	@Override
	public int[] getSelectedRows() {
		return new int[] { selectedRow };
	}

	@Override
	public int getSelectedRow() {
		return selectedRow;
	}

	@Override
	public void stopEditing() {
		// mock implementation
	}

	@Override
	public void startEditingRow(int selectedRow) {
		// mock implementation
	}

	@Override
	public CASTableCellEditor getEditor() {
		return new CASEditorNoGui(inputs[selectedRow]);
	}

	@Override
	public void deleteRow(int rowNumber) {
		// mock implementation
	}

	@Override
	public void setRow(int rowNumber, GeoCasCell casCell) {
		// mock implementation
	}

	@Override
	public boolean isEditing() {
		// mock implementation
		return false;
	}

	@Override
	public void resetRowNumbers(int from) {
		// mock implementation
	}

	@Override
	public boolean hasEditor() {
		// mock implementation
		return false;
	}

	@Override
	public boolean keepEditing(boolean failure, int rowNum) {
		// mock implementation
		return false;
	}

	public void setSelected(int row) {
		this.selectedRow = row;
	}

	public void setInput(int i, String s) {
		inputs[i] = s;
	}
}
