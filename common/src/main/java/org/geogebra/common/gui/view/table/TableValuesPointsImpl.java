package org.geogebra.common.gui.view.table;

import java.util.*;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Implementation of TableValuesPoints
 */
public class TableValuesPointsImpl implements TableValuesPoints {

	private List<List<GeoPoint>> points;
	private Construction construction;
	private SimpleTableValuesModel model;

	/**
	 * Construct a new object of Table Values Points.
	 *
	 * @param construction the construction to add points to
	 * @param model model
	 */
	public TableValuesPointsImpl(Construction construction, TableValuesModel model) {
		this.points = new LinkedList<>();
		this.construction = construction;
		this.model = (SimpleTableValuesModel) model;
	}

	@Override
	public void notifyColumnRemoved(TableValuesModel model, int column) {
		removePointsFromList(column);
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, int column) {
		if (points.get(column) != null) {
			SimpleTableValuesModel simpleModel = (SimpleTableValuesModel) model;
			removePoints(column);
			addPointsToList(simpleModel, column);
		}
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, int column) {
		addPointsToList(model, column);
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, int column) {
		// Ignore header changed notification
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		for (int i = points.size() - 1; i >= 1; i--) {
			removePointsFromList(i);
		}
		for (int i = 1; i < model.getColumnCount(); i++) {
			addPointsToList(model, i);
		}
	}

	private GeoEvaluatable getEvaluatable(TableValuesModel model, int column) {
		SimpleTableValuesModel simpleModel = (SimpleTableValuesModel) model;
		return simpleModel.getEvaluatable(column - 1);
	}

	private void addPointsToList(TableValuesModel model, int column) {
		GeoEvaluatable evaluatable = getEvaluatable(model, column);
		if (evaluatable.isPointsVisible()) {
			SimpleTableValuesModel simpleModel = (SimpleTableValuesModel) model;
			createAndAddPoints(simpleModel, column);
		} else {
			setPoints(null, column);
		}
	}

	private void setPoints(List<GeoPoint> list, int column) {
		if (points.size() > column - 1) {
			points.set(column - 1, list);
		} else {
			points.add(column - 1, list);
		}
	}

	private void createAndAddPoints(SimpleTableValuesModel model, int column) {
		List<GeoPoint> list = createPoints(model, column);
		setPoints(list, column);
	}

	private List<GeoPoint> createPoints(SimpleTableValuesModel model, int column) {
		GeoEvaluatable evaluatable = getEvaluatable(model, column);
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
			point.setFixed(true);
			point.setLabelSimple("TableValuesPoints");
			point.setLabelSet(true);
			maybeSetPointColor(point, evaluatable);
			point.notifyAdd();
			list.add(point);
		}
		return list;
	}

	private void maybeSetPointColor(GeoPoint point, GeoEvaluatable evaluatable) {
		GColor color = evaluatable.getObjectColor();
		point.setObjColor(color);
	}

	private void removePoints(int column) {
		List<GeoPoint> list = points.get(column - 1);
		if (list != null) {
			for (GeoPoint point : list) {
				point.remove();
			}
			points.set(column - 1 , null);
		}
	}

	private void removePointsFromList(int column) {
		removePoints(column);
		points.remove(column - 1);
	}

	@Override
	public boolean arePointsVisible(int column) {
		return points.get(column - 1) != null;
	}

	@Override
	public void setPointsVisible(int column, boolean visible) {
		GeoEvaluatable geoEvaluatable = model.getEvaluatable(column - 1);
		geoEvaluatable.setPointsVisible(visible);
		if (visible && points.get(column - 1) == null) {
			createAndAddPoints(model, column);
		} else if (!visible && points.get(column - 1) != null) {
			removePoints(column);
		}
	}
}
