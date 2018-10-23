package org.geogebra.common.gui.view.table;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoPoint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Creates points according to the table view model.
 */
class TableValuesPoints implements TableValuesListener {

	private List<List<GeoPoint>> points;
	private Construction construction;

	/**
	 * Construct a new object of Table Values Points.
	 *
	 * @param construction the construction to add points to
	 */
	TableValuesPoints(Construction construction) {
		this.points = new LinkedList<>();
		this.construction = construction;
	}

	@Override
	public void notifyColumnRemoved(TableValuesModel model, int column) {
		removePoints(column);
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, int column) {
		SimpleTableValuesModel simpleModel = (SimpleTableValuesModel) model;
		removePoints(column);
		addPoints(simpleModel, column);
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, int column) {
		SimpleTableValuesModel simpleModel = (SimpleTableValuesModel) model;
		addPoints(simpleModel, column);
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, int column) {
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		SimpleTableValuesModel simpleModel = (SimpleTableValuesModel) model;
		for (int i = points.size() - 1; i >= 1; i--) {
			removePoints(i);
		}
		for (int i = 1; i < simpleModel.getColumnCount(); i++) {
			addPoints(simpleModel, i);
		}
	}

	private void addPoints(SimpleTableValuesModel model, int column) {
		ArrayList<GeoPoint> list = new ArrayList<>();
		double[] values = model.getValues();
		for (int row = 0; row < model.getRowCount(); row++) {
			double value = model.getValueAt(row, column);
			GeoPoint point = new GeoPoint(construction, values[row], value, 1.0);
			list.add(point);
		}
		points.add(column - 1, list);
	}

	private void removePoints(int column) {
		List<GeoPoint> list = points.get(column - 1);
		for (GeoPoint point: list) {
			construction.removeFromConstructionList(point);
		}
		points.remove(column - 1);
	}
}
