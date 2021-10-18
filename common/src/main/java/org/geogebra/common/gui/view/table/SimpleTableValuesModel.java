package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.view.table.column.TableValuesColumn;
import org.geogebra.common.gui.view.table.column.TableValuesFunctionColumn;
import org.geogebra.common.gui.view.table.column.TableValuesListColumn;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

import com.google.j2objc.annotations.Weak;

/**
 * TableValuesModel implementation. Uses caching to store values.
 */
class SimpleTableValuesModel implements TableValuesModel {

	@Weak
	private final Kernel kernel;

	private final List<TableValuesListener> listeners;
	private final List<TableValuesColumn> columns;
	private final GeoList values;

	private boolean batchUpdate;

	/**
	 * Construct a SimpleTableValuesModel.
	 * @param kernel kernel
	 */
	SimpleTableValuesModel(Kernel kernel, GeoList values) {
		this.kernel = kernel;
		this.values = values;
		this.listeners = new ArrayList<>();
		this.columns = new ArrayList<>();
		this.batchUpdate = false;

		initializeModel();
	}

	@Override
	public void registerListener(TableValuesListener listener) {
		listeners.add(listener);
	}

	@Override
	public void unregisterListener(TableValuesListener listener) {
		listeners.remove(listener);
	}

	@Override
	public int getRowCount() {
		int rowCount = 0;
		for (TableValuesColumn column : columns) {
			GeoEvaluatable evaluatable = column.getEvaluatable();
			if (evaluatable instanceof GeoList) {
				GeoList list = (GeoList) evaluatable;
				rowCount = Math.max(rowCount, list.size());
			}
		}
		return rowCount;
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public TableValuesCell getCellAt(int row, int column) {
		return columns.get(column).getCellValue(row);
	}

	/**
	 * @param row row index
	 * @param column column index
	 * @return function value
	 */
	double getValueAt(int row, int column) {
		return columns.get(column).getDoubleValue(row);
	}

	@Override
	public String getHeaderAt(int column) {
		if (column == 0) {
			return "x";
		}
		return columns.get(column).getHeader();
	}

	/**
	 * Add an evaluatable to the model.
	 * @param evaluatable evaluatable
	 */
	void addEvaluatable(GeoEvaluatable evaluatable) {
		if (getEvaluatableIndex(evaluatable) == -1) {
			int idx = 0;
			while (idx < columns.size() && columns.get(idx)
					.getEvaluatable().getTableColumn() < evaluatable.getTableColumn()) {
				idx++;
			}
			TableValuesColumn column = createColumn(evaluatable);
			columns.add(idx, column);
			ensureIncreasingIndices(idx);
			notifyColumnAdded(evaluatable, idx);
		}
	}

	private TableValuesColumn createColumn(GeoEvaluatable evaluatable) {
		if (evaluatable.isGeoList()) {
			GeoList list = (GeoList) evaluatable;
			return new TableValuesListColumn(list, list.size());
		}
		return new TableValuesFunctionColumn(evaluatable, values, values.size());
	}

	private void ensureIncreasingIndices(int idx) {
		int lastColumn = columns.get(idx).getEvaluatable().getTableColumn();
		for (int i = idx + 1; i < columns.size(); i++) {
			if (columns.get(i).getEvaluatable().getTableColumn() <= lastColumn) {
				lastColumn++;
				columns.get(i).getEvaluatable().setTableColumn(lastColumn);
			}
		}
	}

	/**
	 * Remove an evaluatable from the model.
	 * @param evaluatable evaluatable
	 */
	void removeEvaluatable(GeoEvaluatable evaluatable) {
		int index = getEvaluatableIndex(evaluatable);
		if (index > -1) {
			if (!kernel.getConstruction().isRemovingGeoToReplaceIt()) {
				evaluatable.setTableColumn(-1);
			}
			columns.remove(index);
			for (int i = 0; i < columns.size(); i++) {
				columns.get(i).getEvaluatable().setTableColumn(i);
			}
			notifyColumnRemoved(evaluatable, index);
		}
	}

	/**
	 * Update the column for the Evaluatable object.
	 * @param evaluatable object to update in table
	 */
	void updateEvaluatable(GeoEvaluatable evaluatable) {
		int index = getEvaluatableIndex(evaluatable);
		if (evaluatable == values) {
			for (TableValuesColumn column : columns) {
				GeoEvaluatable e = column.getEvaluatable();
				int columnSize = e instanceof GeoList ? ((GeoList) e).size() : 0;
				column.invalidateValues(Math.max(columnSize, values.size()));
			}
		} else {
			if (index > -1) {
				int size = values.size();
				if (evaluatable instanceof GeoList) {
					size = ((GeoList) evaluatable).size();
				}
				columns.get(index).invalidateValues(size);
			}
		}
		if (index > -1) {
			notifyColumnChanged(evaluatable, index);
		}
	}

	/**
	 * Optionally updates a cell of a column
	 * @param element element that might be part of a list
	 */
	void maybeUpdateListElement(GeoElement element) {
		int updatedXRow = -1;
		for (int i = 0; i < columns.size(); i++) {
			GeoEvaluatable evaluatable = columns.get(i).getEvaluatable();
			if (evaluatable instanceof GeoList) {
				GeoList list = (GeoList) evaluatable;
				int index = list.find(element);
				if (index > -1) {
					updateCell(evaluatable, i, index);
					updatedXRow = i == 0 ? index : -1;
				}
			} else if (evaluatable instanceof GeoFunction && updatedXRow > -1) {
				updateCell(evaluatable, i, updatedXRow);
			}
		}
	}

	private void updateCell(GeoEvaluatable evaluatable, int col, int row) {
		columns.get(col).invalidateValue(row);
		notifyCellChanged(evaluatable, col, row);
	}

	/**
	 * Returns the index of the evaluatable in the model
	 * or -1 if it's not in the model.
	 * @param evaluatable object to check
	 * @return index of the object, -1 if it's not present
	 */
	int getEvaluatableIndex(GeoEvaluatable evaluatable) {
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).getEvaluatable() == evaluatable) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Get the evaluatable from the model.
	 * @param index index of the object
	 * @return evaluatable if present in the model
	 */
	GeoEvaluatable getEvaluatable(int index) {
		if (index < columns.size() && index > -1) {
			return columns.get(index).getEvaluatable();
		}
		return null;
	}

	/**
	 * Update the name of the Evaluatable object (if it has any)
	 * @param evaluatable the evaluatable object
	 */
	void updateEvaluatableName(GeoEvaluatable evaluatable) {
		int index = getEvaluatableIndex(evaluatable);
		if (index > -1) {
			columns.get(index).invalidateHeader();
			notifyColumnHeaderChanged(evaluatable, index);
		}
	}

	/**
	 * Set the x-values of the model.
	 * @param valuesArray x-values
	 */
	void setValues(double[] valuesArray) {
		values.clear();
		for (double value : valuesArray) {
			values.add(new GeoNumeric(kernel.getConstruction(), value));
		}
		updateEvaluatable(values);
		notifyDatasetChanged();
	}

	private void initializeModel() {
		columns.add(new TableValuesListColumn(values, values.size()));
	}

	/**
	 * Clears and initializes the model.
	 */
	void clearModel() {
		columns.clear();
		initializeModel();
	}

	@Override
	public void startBatchUpdate() {
		batchUpdate = true;
	}

	@Override
	public void endBatchUpdate() {
		batchUpdate = false;
		notifyDatasetChanged();
	}

	private void notifyColumnRemoved(GeoEvaluatable evaluatable, int column) {
		if (!batchUpdate) {
			for (TableValuesListener listener : listeners) {
				listener.notifyColumnRemoved(this, evaluatable, column);
			}
		}
	}

	private void notifyColumnAdded(GeoEvaluatable evaluatable, int column) {
		if (!batchUpdate) {
			for (TableValuesListener listener : listeners) {
				listener.notifyColumnAdded(this, evaluatable, column);
			}
		}
	}

	private void notifyColumnChanged(GeoEvaluatable evaluatable, int column) {
		if (!batchUpdate) {
			for (TableValuesListener listener : listeners) {
				listener.notifyColumnChanged(this, evaluatable, column);
			}
		}
	}

	private void notifyColumnHeaderChanged(GeoEvaluatable evaluatable, int column) {
		if (!batchUpdate) {
			for (TableValuesListener listener : listeners) {
				listener.notifyColumnHeaderChanged(this, evaluatable, column);
			}
		}
	}

	private void notifyCellChanged(GeoEvaluatable evaluatable, int column, int row) {
		if (!batchUpdate) {
			for (TableValuesListener listener : listeners) {
				listener.notifyCellChanged(this, evaluatable, column, row);
			}
		}
	}

	private void notifyRowRemoved(int row) {
		if (!batchUpdate) {
			for (TableValuesListener listener : listeners) {
				listener.notifyRowRemoved(this, row);
			}
		}
	}

	private void notifyRowAdded(int row) {
		if (!batchUpdate) {
			for (TableValuesListener listener : listeners) {
				listener.notifyRowAdded(this, row);
			}
		}
	}

	private void notifyRowChanged(int row) {
		if (!batchUpdate) {
			for (TableValuesListener listener : listeners) {
				listener.notifyRowChanged(this, row);
			}
		}
	}

	private void notifyDatasetChanged() {
		if (!batchUpdate) {
			for (TableValuesListener listener : listeners) {
				listener.notifyDatasetChanged(this);
			}
		}
	}

	@Override
	public void set(GeoElement element, GeoList column, int rowIndex) {
		int columnIndex = getEvaluatableIndex(column);
		if (columnIndex == -1) {
			return;
		}
		int oldRowCount = getRowCount();
		ensureCapacity(column, rowIndex);
		column.setListElement(rowIndex, element);
		column.setDefinition(null);
		columns.get(columnIndex).invalidateValue(rowIndex);
		if (isEmptyValue(element)) {
			removeEmptyColumnAndRows(column, rowIndex);
		} else if (rowIndex == oldRowCount && isOnlyValueInRow(column, rowIndex)) {
			notifyRowAdded(rowIndex);
		} else if (column == values) {
			notifyRowChanged(rowIndex);
		}
		if (getEvaluatableIndex(column) > -1 && column.listContains(element)) {
			element.notifyUpdate();
		}
	}

	private void ensureCapacity(GeoList list, int index) {
		boolean listWillChange = list.size() < index + 1;
		list.ensureCapacity(index + 1);
		for (int i = list.size(); i < index + 1; i++) {
			list.add(createEmptyValue());
		}
		if (listWillChange) {
			list.notifyUpdate();
		}
	}

	@Override
	public GeoElement createEmptyValue() {
		return new GeoText(kernel.getConstruction(), "");
	}

	@Override
	public boolean isEmptyValue(GeoElement element) {
		return element instanceof GeoText && "".equals(((GeoText) element).getTextString());
	}

	private void removeEmptyColumnAndRows(GeoList column, int index) {
		if (index == column.size() - 1) {
			removeEmptyRowsFromBottom();
		}
		removeColumnIfEmpty(column);
	}

	private void removeColumnIfEmpty(GeoList column) {
		if (column == values) {
			return;
		}
		for (int i = 0; i < column.size(); i++) {
			GeoElement element = column.get(i);
			if (!isEmptyValue(element)) {
				return;
			}
		}
		column.remove();
	}

	private void removeEmptyRowsFromBottom() {
		while (getRowCount() > 0 && isLastRowEmpty()) {
			removeLastRow();
		}
	}

	private boolean isLastRowEmpty() {
		int lastRowIndex = getRowCount() - 1;
		for (int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++) {
			if (!"".equals(getCellAt(lastRowIndex, columnIndex).getInput())) {
				return false;
			}
		}
		return true;
	}

	private void removeLastRow() {
		int lastRowIndex = getRowCount() - 1;
		List<GeoList> columnsToRemove = new ArrayList<>();
		for (int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++) {
			GeoEvaluatable evaluatable = columns.get(columnIndex).getEvaluatable();
			if (evaluatable instanceof GeoList) {
				GeoList column = (GeoList) evaluatable;
				if (lastRowIndex < column.size()) {
					column.remove(lastRowIndex);
				}
				if (columnIndex != 0 && column.size() == 0) {
					columnsToRemove.add(column);
				}
			}
		}
		notifyRowRemoved(lastRowIndex);
		for (GeoList column : columnsToRemove) {
			column.remove();
		}
	}

	private boolean isOnlyValueInRow(GeoList column, int rowIndex) {
		for (TableValuesColumn c : columns) {
			if (c.getEvaluatable() != column && !c.getCellValue(rowIndex).getInput().equals("")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * clears the first (x) column
	 */
	public void clearXColumn() {
		GeoEvaluatable evaluatable = getEvaluatable(0);
		if (evaluatable instanceof GeoList) {
			((GeoList) evaluatable).setZero();
			((GeoList) evaluatable).notifyUpdate();
			kernel.getApplication().storeUndoInfo();
		}
	}
}
