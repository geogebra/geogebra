package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.List;

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
	private SimpleTableValuesModel tableModel;

	/**
	 * Construct a new object of Table Values Points.
	 *
	 * @param construction the construction to add points to
	 * @param model model
	 */
	public TableValuesPointsImpl(Construction construction, TableValuesModel model) {
		this.points = new ArrayList<>(2);
		initPoints(0);
		this.construction = construction;
		this.tableModel = (SimpleTableValuesModel) model;
	}

	@Override
	public void notifyColumnRemoved(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		removePointsFromList(column);
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		if (points.size() > column && points.get(column) != null) {
			SimpleTableValuesModel simpleModel = (SimpleTableValuesModel) model;
			removePoints(column);
			addPointsToList(simpleModel, column);
		}
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		initPoints(column);
		addPointsToList(model, column);
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		// Ignore header changed notification
	}

	@Override
	public void notifyCellChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column,
			int row) {
		notifyColumnChanged(model, evaluatable, column);
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		for (int i = points.size() - 1; i >= 0; i--) {
			removePointsFromList(i);
		}
		for (int i = 0; i < model.getColumnCount(); i++) {
			addPointsToList(model, i);
		}
	}

	private static GeoEvaluatable getEvaluatable(TableValuesModel model,
			int column) {
		SimpleTableValuesModel simpleModel = (SimpleTableValuesModel) model;
		return simpleModel.getEvaluatable(column);
	}

	private void addPointsToList(TableValuesModel model, int column) {
		GeoEvaluatable evaluatable = getEvaluatable(model, column);
		if (evaluatable != null && evaluatable.isPointsVisible()) {
			SimpleTableValuesModel simpleModel = (SimpleTableValuesModel) model;
			createAndAddPoints(simpleModel, column);
		} else {
			setPoints(null, column);
		}
	}

	private void setPoints(List<GeoPoint> list, int column) {
		initPoints(column);
		points.add(column, list);
	}

	private void initPoints(int column) {
		for (int i = points.size(); i <= column; i++) {
			points.add(i, null);
		}
	}

	private void createAndAddPoints(SimpleTableValuesModel model, int column) {
		List<GeoPoint> list = createPoints(model, column);
		setPoints(list, column);
	}

	private List<GeoPoint> createPoints(SimpleTableValuesModel model, int column) {
		GeoEvaluatable evaluatable = getEvaluatable(model, column);
		ArrayList<GeoPoint> list = new ArrayList<>();
		for (int row = 0; row < model.getRowCount(); row++) {
			double value = model.getValueAt(row, column);
			GeoPoint point = new GeoPoint(construction,
					model.getValueAt(row, 0), value, 1.0);
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

	private static void maybeSetPointColor(GeoPoint point,
			GeoEvaluatable evaluatable) {
		GColor color = evaluatable.getObjectColor();
		point.setObjColor(color);
	}

	private void removePoints(int column) {
		List<GeoPoint> list = points.get(column);
		if (list != null) {
			for (GeoPoint point : list) {
				point.remove();
			}
			points.set(column, null);
		}
	}

	private void removePointsFromList(int column) {
		removePoints(column);
		points.remove(column);
	}

	@Override
	public boolean arePointsVisible(int column) {
		if (points.size() < column) {
			return false;
		}

		return points.get(column) != null;
	}

	@Override
	public void setPointsVisible(int column, boolean visible) {
		GeoEvaluatable geoEvaluatable = tableModel.getEvaluatable(column);
		geoEvaluatable.setPointsVisible(visible);
		if (visible && points.get(column) == null) {
			createAndAddPoints(tableModel, column);
		} else if (!visible && points.get(column) != null) {
			removePoints(column);
		}
		construction.getKernel().getApplication().storeUndoInfo();
	}
}
