package org.geogebra.common.gui.view.table;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.Evaluatable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.EuclidianStyleConstants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Creates points according to the table view model.
 */
public class TableValuesPoints implements TableValuesListener {

	private List<List<GeoPoint>> points;
	private Construction construction;

	/**
	 * Construct a new object of Table Values Points.
	 *
	 * @param construction the construction to add points to
	 */
	public TableValuesPoints(Construction construction) {
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
		// Ignore header changed notification
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
			point.setPointStyle(EuclidianStyleConstants.POINT_STYLE_NO_OUTLINE);
			point.setAlgebraVisible(false);
			point.setEuclidianVisible(true);
			point.setLabelVisible(false);
			point.setHasPreviewPopup(true);
			point.setLabel("TableValuesPoint");

			Evaluatable evaluatable = model.getEvaluatable(column - 1);
			if (evaluatable instanceof GeoElementND) {
				GeoElementND element = (GeoElementND) evaluatable;
				GColor color = element.getObjectColor();
				point.setObjColor(color);
			}
			list.add(point);
		}
		points.add(column - 1, list);
	}

	private void removePoints(int column) {
		List<GeoPoint> list = points.get(column - 1);
		for (GeoPoint point: list) {
			point.remove();
		}
		points.remove(column - 1);
	}
}
