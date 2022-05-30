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
import org.geogebra.common.main.settings.TableSettings;

import com.google.j2objc.annotations.Weak;

/**
 * TableValuesModel implementation. Uses caching to store values.
 */
class SimpleTableValuesModel implements TableValuesModel {

	@Weak
	private final Kernel kernel;

	private final List<TableValuesListener> listeners;
	private final List<TableValuesColumn> columns;
	private final TableSettings settings;

	private ModelEventCollector collector;

	/**
	 * Construct a SimpleTableValuesModel.
	 * @param kernel kernel
	 */
	SimpleTableValuesModel(Kernel kernel, TableSettings settings) {
		this.kernel = kernel;
		this.settings = settings;
		this.listeners = new ArrayList<>();
		this.columns = new ArrayList<>();
		this.collector = new ModelEventCollector();

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
			collector.startCollection(this);
			int idx = 0;
			while (idx < columns.size() && columns.get(idx)
					.getEvaluatable().getTableColumn() < evaluatable.getTableColumn()) {
				idx++;
			}
			TableValuesColumn column = createColumn(evaluatable);
			column.notifyDatasetChanged(this);
			columns.add(idx, column);
			ensureIncreasingIndices(idx);
			collector.notifyColumnAdded(this, evaluatable, idx);
			collector.endCollection(this);
		}
	}

	private TableValuesColumn createColumn(GeoEvaluatable evaluatable) {
		if (evaluatable.isGeoList()) {
			GeoList list = (GeoList) evaluatable;
			return new TableValuesListColumn(list);
		}
		return new TableValuesFunctionColumn(evaluatable, getValueList());
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
	void removeEvaluatable(GeoEvaluatable evaluatable, boolean removedByUser) {
		int index = getEvaluatableIndex(evaluatable);
		if (index > -1) {
			collector.startCollection(this);
			if (!kernel.getConstruction().isRemovingGeoToReplaceIt()) {
				evaluatable.setTableColumn(-1);
			}
			columns.remove(index);
			for (int i = 0; i < columns.size(); i++) {
				columns.get(i).getEvaluatable().setTableColumn(i);
			}
			collector.notifyColumnRemoved(this, evaluatable, index);
			collector.endCollection(this);
		}
	}

	/**
	 * Update the column for the Evaluatable object.
	 * @param evaluatable object to update in table
	 */
	void updateEvaluatable(GeoEvaluatable evaluatable) {
		int index = getEvaluatableIndex(evaluatable);
		if (index > -1) {
			collector.startCollection(this);
			collector.notifyColumnChanged(this, evaluatable, index);
			collector.endCollection(this);
		}
	}

	/**
	 * Optionally updates a cell of a column
	 * @param element element that might be part of a list
	 */
	void maybeUpdateListElement(GeoElement element) {
		collector.startCollection(this);
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
			collector.notifyCellChanged(this, evaluatable, column, row);
		}
		collector.endCollection(this);
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
		collector.startCollection(this);
		GeoList values = getValueList();
		values.clear();
		for (double value : valuesArray) {
			values.add(new GeoNumeric(kernel.getConstruction(), value));
		}
		updateEvaluatable(values);
		collector.notifyDatasetChanged(this);
		collector.endCollection(this);
	}

	public GeoList getValueList() {
		if (settings.getValueList() == null) {
			settings.setValueList(new GeoList(kernel.getConstruction()));
		}
		return settings.getValueList();
	}

	private void initializeModel() {
		TableValuesColumn column = new TableValuesListColumn(getValueList());
		columns.add(column);
		column.notifyDatasetChanged(this);
	}

	/**
	 * Clears and initializes the model.
	 */
	void clearModel() {
		columns.clear();
		initializeModel();
		collector.notifyDatasetChanged(this);
	}

	@Override
	public void startBatchUpdate() {
		collector.startCollection(this);
	}

	@Override
	public void endBatchUpdate(boolean notifyDatasetChanged) {
		if (notifyDatasetChanged) {
			collector.notifyDatasetChanged(this);
		}
		collector.endCollection(this);
	}

	@Override
	public boolean isBatchUpdate() {
		return collector.isCollecting();
	}

	void notifyColumnRemoved(GeoEvaluatable evaluatable, int column) {
		forEachListener(listener -> listener.notifyColumnRemoved(this, evaluatable, column));
	}

	void notifyColumnAdded(GeoEvaluatable evaluatable, int column) {
		forEachListener(listener -> listener.notifyColumnAdded(this, evaluatable, column));
	}

	void notifyColumnChanged(GeoEvaluatable evaluatable, int column) {
		forEachListener(listener -> listener.notifyColumnChanged(this, evaluatable, column));
	}

	void notifyColumnHeaderChanged(GeoEvaluatable evaluatable, int column) {
		forEachListener(listener -> listener.notifyColumnHeaderChanged(this, evaluatable, column));
	}

	void notifyCellChanged(GeoEvaluatable evaluatable, int column, int row) {
		forEachListener(listener -> listener.notifyCellChanged(this, evaluatable, column, row));
	}

	void notifyRowsRemoved(int firstRow, int lastRow) {
		forEachListener(listener -> listener.notifyRowsRemoved(this, firstRow, lastRow));
	}

	void notifyRowsAdded(int firstRow, int lastRow) {
		forEachListener(listener -> listener.notifyRowsAdded(this, firstRow, lastRow));
	}

	void notifyRowChanged(int row) {
		forEachListener(listener -> listener.notifyRowChanged(this, row));
	}

	void notifyDatasetChanged() {
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
		collector.startCollection(this);
		ensureCapacity(column, rowIndex);
		column.setListElement(rowIndex, element);
		column.setDefinition(null);
		if (isEmptyValue(element)) {
			handleEmptyValue(column, columnIndex, rowIndex);
		} else if (column == getValueList()) {
			collector.notifyRowChanged(this, rowIndex);
		} else if (getEvaluatableIndex(column) > -1 && column.listContains(element)) {
			element.notifyUpdate();
		}
		collector.endCollection(this);
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
		if (rowIndex == column.size() - 1) {
			removeEmptyRows(columnIndex);
		} else if (column == getValueList()) {
			collector.notifyRowChanged(this, rowIndex);
		} else if (isColumnEmpty(column)) {
			column.remove();
		} else {
			collector.notifyCellChanged(this, column, columnIndex, rowIndex);
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

	private void removeEmptyRows(int columnIndex) {
		TableValuesColumn tableValuesColumn = columns.get(columnIndex);
		GeoEvaluatable evaluatable = tableValuesColumn.getEvaluatable();
		if (!(evaluatable instanceof GeoList)) {
			return;
		}
		GeoList column = (GeoList) evaluatable;
		while (column.size() > 0 && isEmptyValue(column.get(column.size() - 1))) {
			int row = column.size() - 1;
			column.remove(row);
			if (columnIndex == 0) {
				collector.notifyRowChanged(this, row);
			}
		}

		if (columnIndex != 0 && isColumnEmpty(column)) {
			column.remove();
		}
	}

	public boolean isEvaluatableEmptyList(int column) {
		return getEvaluatable(column).isGeoList()
				&& ((GeoList) getEvaluatable(column)).isEmptyList();
	}

	/**
	 * Update values column from the settings
	 */
	public void updateValuesColumn() {
		TableValuesListColumn element = new TableValuesListColumn(getValueList());
		columns.set(0, element);
		element.notifyColumnChanged(this, getValueList(), 0);
	}
}
