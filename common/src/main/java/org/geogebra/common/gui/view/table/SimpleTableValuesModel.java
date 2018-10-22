package org.geogebra.common.gui.view.table;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Evaluatable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * TableValuesModel implementation. Uses caching to store values.
 */
class SimpleTableValuesModel implements TableValuesModel {

	private List<String[]> columns;

	private List<TableValuesListener> listeners;
	private List<Evaluatable> evaluatables;
	private float[] values;
	private Kernel kernel;

	/**
	 * Construct a SimpleTableValuesModel.
	 *
	 * @param kernel kernel
	 */
	SimpleTableValuesModel(Kernel kernel) {
		this.kernel = kernel;
		this.evaluatables = new ArrayList<>();
		this.listeners = new ArrayList<>();
		this.columns = new LinkedList<>();
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
			Evaluatable evaluatable = evaluatables.get(column);
			double x = values[row];
			value = getValue(evaluatable, x);
			valuesColumn[row] = value;
		}
		return value;
	}

	private String getValue(Evaluatable evaluatable, double x) {
		double value = evaluatable.value(x);
		return kernel.format(value, StringTemplate.defaultTemplate);
	}

	/**
	 * Add an evaluatable to the model.
	 *
	 * @param evaluatable evaluatable
	 */
	void addEvaluatable(Evaluatable evaluatable) {
		evaluatables.add(evaluatable);
		columns.add(new String[values.length]);
		notifyColumnAdded(evaluatables.size());
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
			columns.remove(index);
			notifyColumnRemoved(index);
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
			notifyColumnChanged(index);
		}
	}

	/**
	 * Set the x-values of the model.
	 *
	 * @param values x-values
	 */
	void setValues(float[] values) {
		this.values = values;
		ListIterator<String[]> iterator = columns.listIterator();
		while (iterator.hasNext()) {
			iterator.next();
			iterator.set(new String[values.length]);
		}
		notifyDatasetChanged();
	}


	private void notifyColumnRemoved(int column) {
		for (TableValuesListener listener: listeners) {
			listener.notifyColumnRemoved(column);
		}
	}

	private void notifyColumnAdded(int column) {
		for (TableValuesListener listener: listeners) {
			listener.notifyColumnAdded(column);
		}
	}

	private void notifyColumnChanged(int column) {
		for (TableValuesListener listener: listeners) {
			listener.notifyColumnChanged(column);
		}
	}

	private void notifyDatasetChanged() {
		for (TableValuesListener listener: listeners) {
			listener.notifyDatasetChanged();
		}
	}
}
