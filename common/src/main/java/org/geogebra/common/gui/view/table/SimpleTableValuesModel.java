package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.geogebra.common.gui.view.table.column.TableValuesColumn;
import org.geogebra.common.gui.view.table.column.TableValuesFunctionColumn;
import org.geogebra.common.gui.view.table.column.TableValuesListColumn;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
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

	@Override
	public double getValueAt(int row, int column) {
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
			int oldRowCount = getRowCount();
			TableValuesColumn column = createColumn(evaluatable);
			columns.add(idx, column);
			column.notifyDatasetChanged(this);
			ensureIncreasingIndices(idx);
			notifyColumnAdded(evaluatable, idx);
			int newRowCount = getRowCount();
			if (newRowCount > oldRowCount) {
				notifyRowsAdded(oldRowCount, newRowCount - 1);
			}
		}
	}

	private TableValuesColumn createColumn(GeoEvaluatable evaluatable) {
		if (evaluatable.isGeoList()) {
			GeoList list = (GeoList) evaluatable;
			return new TableValuesListColumn(list);
		}
		return new TableValuesFunctionColumn(evaluatable, values);
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
		if (index > -1) {
			notifyColumnChanged(evaluatable, index);
		}
	}

	/**
	 * Optionally updates a cell of a column
	 * @param element element that might be part of a list
	 */
	void maybeUpdateListElement(GeoElement element) {
		for (int column = 0; column < columns.size(); column++) {
			GeoEvaluatable evaluatable = columns.get(column).getEvaluatable();
			if (!(evaluatable instanceof GeoList)) {
				continue;
			}
			GeoList list = (GeoList) evaluatable;
			int row = list.find(element);
			if (row <= -1) {
				continue;
			}
			notifyCellChanged(evaluatable, column, row);
		}
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
		TableValuesColumn column = new TableValuesListColumn(values);
		columns.add(column);
		column.notifyDatasetChanged(this);
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
		if (batchUpdate) {
			return;
		}
		forEachListener(listener -> listener.notifyColumnRemoved(this, evaluatable, column));
	}

	private void notifyColumnAdded(GeoEvaluatable evaluatable, int column) {
		if (batchUpdate) {
			return;
		}
		forEachListener(listener -> listener.notifyColumnAdded(this, evaluatable, column));
	}

	private void notifyColumnChanged(GeoEvaluatable evaluatable, int column) {
		if (batchUpdate) {
			return;
		}
		forEachListener(listener -> listener.notifyColumnChanged(this, evaluatable, column));
	}

	private void notifyColumnHeaderChanged(GeoEvaluatable evaluatable, int column) {
		if (batchUpdate) {
			return;
		}
		forEachListener(listener -> listener.notifyColumnHeaderChanged(this, evaluatable, column));
	}

	private void notifyCellChanged(GeoEvaluatable evaluatable, int column, int row) {
		if (batchUpdate) {
			return;
		}
		forEachListener(listener -> listener.notifyCellChanged(this, evaluatable, column, row));
	}

	private void notifyRowRemoved(int row) {
		if (batchUpdate) {
			return;
		}
		forEachListener(listener -> listener.notifyRowRemoved(this, row));
	}

	private void notifyRowAdded(int row) {
		if (batchUpdate) {
			return;
		}
		forEachListener(listener -> listener.notifyRowAdded(this, row));
	}

	private void notifyRowChanged(int row) {
		if (batchUpdate) {
			return;
		}
		forEachListener(listener -> listener.notifyRowChanged(this, row));
	}

	private void notifyDatasetChanged() {
		if (batchUpdate) {
			return;
		}
		forEachListener(listener -> listener.notifyDatasetChanged(this));
	}

	private Stream<TableValuesListener> listenerStream() {
		return Stream.concat(columns.stream(), listeners.stream());
	}

	private void forEachListener(Consumer<? super TableValuesListener> action) {
		listenerStream().forEachOrdered(action);
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
		if (isEmptyValue(element)) {
			handleEmptyValue(column, columnIndex, rowIndex);
		} else if (rowIndex >= oldRowCount) {
			notifyRowsAdded(oldRowCount, rowIndex);
		} else if (column == values) {
			notifyRowChanged(rowIndex);
		} else if (getEvaluatableIndex(column) > -1 && column.listContains(element)) {
			element.notifyUpdate();
		}
	}

	private void notifyRowsAdded(int startRow, int endRow) {
		int rowIndex = startRow;
		while (rowIndex <= endRow) {
			notifyRowAdded(rowIndex++);
		}
	}

	private void ensureCapacity(GeoList list, int index) {
		list.ensureCapacity(index + 1);
		for (int i = list.size(); i < index + 1; i++) {
			list.add(createEmptyValue());
		}
	}

	@Override
	public boolean isEmptyValue(GeoElement element) {
		return element instanceof GeoText && "".equals(((GeoText) element).getTextString());
	}

	@Override
	public GeoElement createEmptyValue() {
		return new GeoText(kernel.getConstruction(), "");
	}

	@Override
	public GeoNumeric createValue(double value) {
		return new GeoNumeric(kernel.getConstruction(), value);
	}

	private void handleEmptyValue(GeoList column, int columnIndex, int rowIndex) {
		if (rowIndex == column.size() - 1 && isLastRowEmpty()) {
			removeEmptyRowsFromBottom();
		} else if (column == values) {
			notifyRowChanged(rowIndex);
		} else if (isColumnEmpty(column)) {
			column.remove();
		} else {
			notifyCellChanged(column, columnIndex, rowIndex);
		}
	}

	private boolean isColumnEmpty(GeoList column) {
		for (int i = 0; i < column.size(); i++) {
			GeoElement element = column.get(i);
			if (!isEmptyValue(element)) {
				return false;
			}
		}
		return true;
	}

	private void removeEmptyRowsFromBottom() {
		while (getRowCount() > 0 && isLastRowEmpty()) {
			removeLastRow();
		}
	}

	private boolean isLastRowEmpty() {
		int lastRowIndex = getRowCount() - 1;
		for (TableValuesColumn column : columns) {
			GeoEvaluatable element = column.getEvaluatable();
			if (!(element instanceof GeoList)) {
				continue;
			}
			GeoList list = (GeoList) element;
			if (list.size() <= lastRowIndex) {
				continue;
			}
			if (!isEmptyValue(list.get(lastRowIndex))) {
				return false;
			}
		}
		return true;
	}

	private void removeLastRow() {
		int lastRowIndex = getRowCount() - 1;
		List<GeoList> columnsToRemove = new ArrayList<>();
		for (int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++) {
			TableValuesColumn tableValuesColumn = columns.get(columnIndex);
			GeoEvaluatable evaluatable = tableValuesColumn.getEvaluatable();
			if (evaluatable instanceof GeoList) {
				GeoList column = (GeoList) evaluatable;
				if (lastRowIndex < column.size()) {
					column.remove(lastRowIndex);
				}
				if (columnIndex != 0 && isColumnEmpty(column)) {
					columnsToRemove.add(column);
				}
			}
		}
		notifyRowRemoved(lastRowIndex);
		for (GeoList column : columnsToRemove) {
			column.remove();
		}
	}
}
