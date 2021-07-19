package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

import com.google.j2objc.annotations.Weak;

/**
 * TableValuesModel implementation. Uses caching to store values.
 */
class SimpleTableValuesModel implements TableValuesModel {

	@Weak
	private Kernel kernel;

	private List<TableValuesListener> listeners;
	private List<TableValuesColumn> columns;
	private GeoList values;

	private boolean batchUpdate;

	/**
	 * Construct a SimpleTableValuesModel.
	 *
	 * @param kernel kernel
	 */
	SimpleTableValuesModel(Kernel kernel) {
		this.kernel = kernel;
		this.listeners = new ArrayList<>();

		this.columns = new ArrayList<>();
		this.values = new GeoList(kernel.getConstruction());

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
		return values.size();
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public String getCellAt(int row, int column) {
		return columns.get(column).getCellAt(row);
	}

	/**
	 * @param row
	 *            row index
	 * @param column
	 *            column index
	 * @return function value
	 */
	double getValueAt(int row, int column) {
		return columns.get(column).getValueAt(row);
	}

	@Override
	public String getHeaderAt(int column) {
		if (column == 0) {
			return "x";
		}
		return columns.get(column).getHeaderName();
	}

	@Override
	public void setCell(int row, int column) {
		TableValuesColumn col = columns.get(column);
		if (col.isModifiable()) {
			col.setCell(row);
			if (column == 0) {
				updateAllEvaluatables();
			}
			col.updateRepaint();
			notifyDatasetChanged();
		}
	}

	@Override
	public boolean isColumnEditable(int col) {
		return columns.get(col).isModifiable();
	}

	/**
	 * Add an evaluatable to the model.
	 *
	 * @param evaluatable evaluatable
	 */
	void addEvaluatable(GeoEvaluatable evaluatable) {
		if (getEvaluatableIndex(evaluatable) == -1) {
			int idx = 0;
			while (idx < columns.size() && columns.get(idx)
					.getTableColumn() < evaluatable.getTableColumn()) {
				idx++;
			}
			columns.add(idx, new TableValuesColumn(evaluatable, values));
			ensureIncreasingIndices(idx);
			notifyColumnAdded(evaluatable, idx);
		}
	}

	private void ensureIncreasingIndices(int idx) {
		int lastColumn = columns.get(idx).getTableColumn();
		for (int i = idx + 1; i < columns.size(); i++) {
			if (columns.get(i).getTableColumn() <= lastColumn) {
				lastColumn++;
				columns.get(i).setTableColumn(lastColumn);
			}
		}
	}

	/**
	 * Remove an evaluatable from the model.
	 *
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
				columns.get(i).setTableColumn(i);
			}
			notifyColumnRemoved(evaluatable, index);
		}
	}

	private void updateAllEvaluatables() {
		for (TableValuesColumn column : columns) {
			updateEvaluatable(column.getEvaluatable());
		}
	}

	/**
	 * Update the column for the Evaluatable object.
	 *
	 * @param evaluatable object to update in table
	 */
	void updateEvaluatable(GeoEvaluatable evaluatable) {
		int index = getEvaluatableIndex(evaluatable);
		if (index > -1) {
			columns.get(index).clearCache();
			notifyColumnChanged(evaluatable, index);
		}
	}

	/**
	 * Returns the index of the evaluatable in the model
	 * or -1 if it's not in the model.
	 *
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
	 *
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
	 *
	 * @param evaluatable the evaluatable object
	 */
	void updateEvaluatableName(GeoEvaluatable evaluatable) {
		int index = getEvaluatableIndex(evaluatable);
		if (index > -1) {
			columns.get(index).updateHeaderName();
			notifyColumnHeaderChanged(evaluatable, index);
		}
	}

	/**
	 * Set the x-values of the model.
	 *
	 * @param values x-values
	 */
	void setValues(double[] values) {
		this.values.clear();
		for (double value : values) {
			this.values.add(new GeoNumeric(kernel.getConstruction(), value));
		}
		for (TableValuesColumn column : columns) {
			column.clearCache();
		}
		notifyDatasetChanged();
	}

	private void initializeModel() {
		columns.add(new TableValuesColumn(values, values));
	}

	/**
	 * Clears and initializes the model.
	 */
	void clearModel() {
		columns.clear();
		initializeModel();
	}

	/**
	 * Starts batch update.
	 * This batch update call cannot be nested.
	 */
	void startBatchUpdate() {
		batchUpdate = true;
	}

	/**
	 * Ends the batch update.
	 * Calls {@link TableValuesListener#notifyDatasetChanged(TableValuesModel)}.
	 */
	void endBatchUpdate() {
		batchUpdate = false;
		notifyDatasetChanged();
	}

	/**
	 * Get the x-values of the model.
	 *
	 * @return x-values
	 */
	GeoList getValues() {
		return values;
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

	private void notifyDatasetChanged() {
		if (!batchUpdate) {
			for (TableValuesListener listener : listeners) {
				listener.notifyDatasetChanged(this);
			}
		}
	}
}
