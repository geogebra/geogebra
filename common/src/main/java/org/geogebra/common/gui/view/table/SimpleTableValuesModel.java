package org.geogebra.common.gui.view.table;

import org.geogebra.common.kernel.arithmetic.Evaluatable;

import java.util.ArrayList;
import java.util.List;

public class SimpleTableValuesModel implements TableValuesModel {

	private List<TableValuesListener> listeners;
	private List<Evaluatable> evaluatables;
	private float[] values;

	public SimpleTableValuesModel() {
		this.evaluatables = new ArrayList<>();
		this.listeners = new ArrayList<>();
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
		return 0;
	}

	@Override
	public String getCellAt(int row, int column) {
		return null;
	}

	public void addEvaluatable(Evaluatable evaluatable) {
		evaluatables.add(evaluatable);
		notifyColumnAdded(evaluatables.size());
	}

	public void removeEvaluatable(Evaluatable evaluatable) {
		if (evaluatables.contains(evaluatable)) {
			int index = evaluatables.indexOf(evaluatable);
			evaluatables.remove(evaluatable);
			notifyColumnRemoved(index);
		}
	}

	public void setValues(float[] values) {
		this.values = values;
		notifyDatasetChanged();
	}

	public void notifyColumnRemoved(int column) {
		for (TableValuesListener listener: listeners) {
			listener.notifyColumnRemoved(column);
		}
	}

	public void notifyColumnAdded(int column) {
		for (TableValuesListener listener: listeners) {
			listener.notifyColumnAdded(column);
		}
	}

	public void notifyDatasetChanged() {
		for (TableValuesListener listener: listeners) {
			listener.notifyDatasetChanged();
		}
	}
}
