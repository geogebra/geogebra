package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Evaluatable;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * TableValuesModel implementation. Uses caching to store values.
 */
class SimpleTableValuesModel implements TableValuesModel {

	private List<Double[]> doubleColumns;
	private List<String[]> columns;
	private List<String> header;

	private List<TableValuesListener> listeners;
	private List<Evaluatable> evaluatables;
	private double[] values;
	private Kernel kernel;
	private StringBuilder builder;

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
		Evaluatable evaluatable = evaluatables.get(column - 1);
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
	void addEvaluatable(Evaluatable evaluatable) {
		evaluatables.add(evaluatable);
		columns.add(new String[values.length]);
		doubleColumns.add(new Double[values.length]);
		addHeader(evaluatable);
		notifyColumnAdded(evaluatables.size());
	}

	private void addHeader(Evaluatable evaluatable) {
		String name = getHeaderName(evaluatable);
		header.add(name);
	}

	private String getHeaderName(Evaluatable evaluatable) {
		builder.setLength(0);
		if (evaluatable instanceof GeoElementND) {
			GeoElementND element = (GeoElementND) evaluatable;
			builder.append(element.getLabelSimple());
			builder.append("(");
			builder.append("x");
			builder.append(")");
		}

		return builder.toString();
	}

	/**
	 * Remove an evaluatable from the model.
	 *
	 * @param evaluatable evaluatable
	 */
	void removeEvaluatable(Evaluatable evaluatable) {
		if (evaluatables.contains(evaluatable)) {
			int index = evaluatables.indexOf(evaluatable);
			evaluatables.remove(evaluatable);
			int column = index + 1;
			columns.remove(column);
			doubleColumns.remove(column);
			header.remove(column);
			notifyColumnRemoved(column);
		}
	}

	/**
	 * Update the column for the Evaluatable object.
	 *
	 * @param evaluatable object to update in table
	 */
	void updateEvaluatable(Evaluatable evaluatable) {
		if (evaluatables.contains(evaluatable)) {
			int index = evaluatables.indexOf(evaluatable);
			columns.set(index, new String[values.length]);
			doubleColumns.set(index, new Double[values.length]);
			notifyColumnChanged(index + 1);
		}
	}

	/**
	 * Update the name of the Evaluatable object (if it has any)
	 *
	 * @param evaluatable the evaluatable object
	 */
	void updateEvaluatableName(Evaluatable evaluatable) {
		if (evaluatables.contains(evaluatable)) {
			int index = evaluatables.indexOf(evaluatable);
			String newName = getHeaderName(evaluatable);
			header.set(index, newName);
			notifyColumnHeaderChanged(index + 1);
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
	 *
	 *  @param values x-values
	 */
	void clearModel(double[] values) {
		columns.clear();
		doubleColumns.clear();
		header.clear();
		evaluatables.clear();
		initializeModel();
		setValues(values);
	}

	/**
	 * Get the x-values of the model.
	 *
	 * @return x-values
	 */
	double[] getValues() {
		return values;
	}

	private void notifyColumnRemoved(int column) {
		for (TableValuesListener listener: listeners) {
			listener.notifyColumnRemoved(this, column);
		}
	}

	private void notifyColumnAdded(int column) {
		for (TableValuesListener listener: listeners) {
			listener.notifyColumnAdded(this, column);
		}
	}

	private void notifyColumnChanged(int column) {
		for (TableValuesListener listener: listeners) {
			listener.notifyColumnChanged(this, column);
		}
	}

	private void notifyColumnHeaderChanged(int column) {
		for (TableValuesListener listener: listeners) {
			listener.notifyColumnHeaderChanged(this, column);
		}
	}

	private void notifyDatasetChanged() {
		for (TableValuesListener listener: listeners) {
			listener.notifyDatasetChanged(this);
		}
	}
}
