package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

import com.google.j2objc.annotations.Weak;

/**
 * TableValuesModel implementation. Uses caching to store values.
 */
class SimpleTableValuesModel implements TableValuesModel {

	@Weak
	private Kernel kernel;

	private List<Double[]> doubleColumns;
	private List<String[]> columns;
	private List<String> header;

	private List<TableValuesListener> listeners;
	private ArrayList<GeoEvaluatable> evaluatables;
	private double[] values;
	private StringBuilder builder;

	private boolean batchUpdate;

	/**
	 * Construct a SimpleTableValuesModel.
	 *
	 * @param kernel kernel
	 */
	SimpleTableValuesModel(Kernel kernel) {
		this.kernel = kernel;
		this.evaluatables = new ArrayList<>();
		this.listeners = new ArrayList<>();
		this.builder = new StringBuilder();

		this.columns = new LinkedList<>();
		this.doubleColumns = new LinkedList<>();
		this.header = new LinkedList<>();
		this.values = new double[0];

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
		return values.length;
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public String getCellAt(int row, int column) {
		String[] valuesColumn = columns.get(column);
		String value = valuesColumn[row];
		if (value == null) {
			double doubleValue = getValueAt(row, column);
			value = format(doubleValue);
			valuesColumn[row] = value;
		}
		return value;
	}

	/**
	 * @param row
	 *            row index
	 * @param column
	 *            column index
	 * @return function value
	 */
	double getValueAt(int row, int column) {
		Double[] valuesColumn = doubleColumns.get(column);
		Double value = valuesColumn[row];
		if (value == null) {
			value = evaluateAt(row, column);
			valuesColumn[row] = value;
		}
		return value;
	}

	private double evaluateAt(int row, int column) {
		GeoEvaluatable evaluatable = evaluatables.get(column - 1);
		double x = values[row];
		return evaluatable.value(x);
	}

	private String format(double x) {
		return kernel.format(x, StringTemplate.defaultTemplate);
	}

	@Override
	public String getHeaderAt(int column) {
		return header.get(column);
	}

	/**
	 * Add an evaluatable to the model.
	 *
	 * @param evaluatable evaluatable
	 */
	void addEvaluatable(GeoEvaluatable evaluatable) {
		if (!evaluatables.contains(evaluatable)) {
			int idx = 0;
			while (idx < evaluatables.size() && evaluatables.get(idx)
					.getTableColumn() < evaluatable.getTableColumn()) {
				idx++;
			}
			evaluatables.add(idx, evaluatable);
			ensureIncreasingIndices(idx);
			int column = idx + 1;
			columns.add(column, new String[values.length]);
			doubleColumns.add(column, new Double[values.length]);
			header.add(column, getHeaderName(evaluatable));
			notifyColumnAdded(evaluatable, column);
		}
	}

	private void ensureIncreasingIndices(int idx) {
		int lastColumn = evaluatables.get(idx).getTableColumn();
		for (int i = idx + 1; i < evaluatables.size(); i++) {
			if (evaluatables.get(i).getTableColumn() <= lastColumn) {
				lastColumn++;
				evaluatables.get(i).setTableColumn(lastColumn);
			}
		}
	}

	private String getHeaderName(GeoEvaluatable evaluatable) {
		builder.setLength(0);
		builder.append(evaluatable.getLabelSimple());
		builder.append("(x)");

		return builder.toString();
	}

	/**
	 * Remove an evaluatable from the model.
	 *
	 * @param evaluatable evaluatable
	 */
	void removeEvaluatable(GeoEvaluatable evaluatable) {
		if (evaluatables.contains(evaluatable)) {
			if (!kernel.getConstruction().isRemovingGeoToReplaceIt()) {
				evaluatable.setTableColumn(-1);
			}
			int index = evaluatables.indexOf(evaluatable);
			evaluatables.remove(evaluatable);
			int column = index + 1;
			columns.remove(column);
			doubleColumns.remove(column);
			header.remove(column);
			for (int i = 0; i < evaluatables.size(); i++) {
				evaluatables.get(i).setTableColumn(i + 1);
			}
			notifyColumnRemoved(evaluatable, column);
		}
	}

	/**
	 * Update the column for the Evaluatable object.
	 *
	 * @param evaluatable object to update in table
	 */
	void updateEvaluatable(GeoEvaluatable evaluatable) {
		if (evaluatables.contains(evaluatable)) {
			int index = evaluatables.indexOf(evaluatable);
			columns.set(index + 1, new String[values.length]);
			doubleColumns.set(index + 1, new Double[values.length]);
			notifyColumnChanged(evaluatable, index + 1);
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
		return evaluatables.indexOf(evaluatable);
	}

	/**
	 * Get the evaluatable from the model.
	 *
	 * @param index index of the object
	 * @return evaluatable if present in the model
	 */
	GeoEvaluatable getEvaluatable(int index) {
		if (index < evaluatables.size() && index > -1) {
			return evaluatables.get(index);
		}
		return null;
	}

	/**
	 * Update the name of the Evaluatable object (if it has any)
	 *
	 * @param evaluatable the evaluatable object
	 */
	void updateEvaluatableName(GeoEvaluatable evaluatable) {
		if (evaluatables.contains(evaluatable)) {
			int index = evaluatables.indexOf(evaluatable);
			String newName = getHeaderName(evaluatable);
			header.set(index + 1, newName);
			notifyColumnHeaderChanged(evaluatable, index + 1);
		}
	}

	/**
	 * Set the x-values of the model.
	 *
	 * @param values x-values
	 */
	void setValues(double[] values) {
		this.values = values;
		for (int i = 0; i < columns.size(); i++) {
			columns.set(i, new String[values.length]);
		}
		Double[] valuesColumn = new Double[values.length];
		for (int i = 0; i < values.length; i++) {
			valuesColumn[i] = values[i];
		}
		doubleColumns.set(0, valuesColumn);
		for (int i = 1; i < doubleColumns.size(); i++) {
			doubleColumns.set(i, new Double[values.length]);
		}
		notifyDatasetChanged();
	}

	private void initializeModel() {
		columns.add(new String[0]);
		doubleColumns.add(new Double[0]);
		header.add("x");
	}

	/**
	 * Clears and initializes the model.
	 */
	void clearModel() {
		columns.clear();
		doubleColumns.clear();
		header.clear();
		evaluatables.clear();
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
	double[] getValues() {
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
