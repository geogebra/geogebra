package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Implementation of TableValuesPoints
 */
public class TableValuesPointsImpl implements TableValuesPoints {

	private List<List<GeoPoint>> points;
	private Construction construction;
	private TableValues view;
	private TableValuesModel model;

	/**
	 * Construct a new object of Table Values Points.
	 * @param construction the construction to add points to
	 * @param model model
	 */
	public TableValuesPointsImpl(Construction construction, TableValues view,
			TableValuesModel model) {
		this.points = new ArrayList<>(2);
		initPoints(0);
		this.construction = construction;
		this.view = view;
		this.model = model;
	}

	/**
	 * Construct and register a new object of Table Values Points.
	 * @param construction the construction to add points to
	 * @param model model
	 * @return points model
	 */
	public static TableValuesPointsImpl create(Construction construction, TableValues view,
			TableValuesModel model) {
		TableValuesPointsImpl instance = new TableValuesPointsImpl(construction, view, model);
		model.registerListener(instance);
		return instance;
	}

	@Override
	public void notifyColumnRemoved(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		removePointsFromList(column);
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		if (column == 0 || points.size() <= column || points.get(column) == null) {
			return;
		}
		removePoints(column);
		addPointsToList(evaluatable, column);
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		if (column == 0) {
			return;
		}
		initPoints(column);
		addPointsToList(evaluatable, column);
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		// Ignore header changed notification
	}

	@Override
	public void notifyCellChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column,
			int row) {
		if (column == 0 || column >= points.size()) {
			return;
		}
		List<GeoPoint> columnPoints = points.get(column);
		if (columnPoints == null || columnPoints.size() <= row) {
			return;
		}
		GeoPoint point = columnPoints.get(row);
		setupPoint(point, evaluatable, row, column);
		point.updateRepaint();
	}

	@Override
	public void notifyRowsRemoved(TableValuesModel model, int firstRow, int lastRow) {
		for (int row = lastRow; row >= firstRow; row--) {
			notifyRowRemoved(row);
		}
	}

	private void notifyRowRemoved(int row) {
		for (List<GeoPoint> geoPoints : points) {
			if (geoPoints == null || geoPoints.size() <= row) {
				continue;
			}
			GeoPoint point = geoPoints.remove(row);
			point.remove();
		}
	}

	@Override
	public void notifyRowChanged(TableValuesModel model, int row) {
		for (int column = 1; column < points.size(); column++) {
			List<GeoPoint> geoPoints = points.get(column);
			if (geoPoints == null || geoPoints.size() <= row) {
				continue;
			}
			GeoPoint point = geoPoints.get(row);
			setupPoint(point, view.getEvaluatable(column), row, column);
			point.updateRepaint();
		}
	}

	@Override
	public void notifyRowsAdded(TableValuesModel model, int firstRow, int lastRow) {
		for (int row = firstRow; row <= lastRow; row++) {
			notifyRowAdded(row);
		}
	}

	private void notifyRowAdded(int row) {
		for (int column = 1; column < points.size(); column++) {
			List<GeoPoint> geoPoints = points.get(column);
			if (geoPoints == null) {
				continue;
			}
			GeoPoint point = createNewPoint();
			setupPoint(point, view.getEvaluatable(column), row, column);
			geoPoints.add(row, point);
			point.notifyAdd();
		}
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		for (int i = points.size() - 1; i >= 0; i--) {
			removePointsFromList(i);
		}
		for (int column = 1; column < model.getColumnCount(); column++) {
			GeoEvaluatable evaluatable = view.getEvaluatable(column);
			addPointsToList(evaluatable, column);
		}
	}

	private void addPointsToList(GeoEvaluatable evaluatable, int column) {
		if (evaluatable != null && evaluatable.isPointsVisible()) {
			createAndAddPoints(evaluatable, column);
		} else {
			setPoints(null, column, true);
		}
	}

	private void setPoints(List<GeoPoint> list, int column, boolean addColumn) {
		initPoints(column);
		if (addColumn) {
			points.add(column, list);
		} else {
			points.set(column, list);
		}
	}

	private void initPoints(int column) {
		int size = Math.max(column - points.size() + 1, 0);
		points.addAll(Collections.nCopies(size, null));
	}

	private void createAndAddPoints(GeoEvaluatable evaluatable, int column) {
		List<GeoPoint> list = createPoints(evaluatable, column);
		setPoints(list, column, false);
	}

	private List<GeoPoint> createPoints(GeoEvaluatable evaluatable, int column) {
		ArrayList<GeoPoint> list = new ArrayList<>(model.getRowCount());
		for (int row = 0; row < model.getRowCount(); row++) {
			GeoPoint point = createNewPoint();
			setupPoint(point, evaluatable, row, column);
			point.notifyAdd();
			list.add(point);
		}
		return list;
	}

	private void setupPoint(GeoPoint point, GeoEvaluatable evaluatable, int row, int column) {
		double x = model.getValueAt(row, 0);
		double y = model.getValueAt(row, column);
		point.setX(x);
		point.setY(y);
		point.setZ(1.0);
		point.updateCoords();
		point.setPointStyle(EuclidianStyleConstants.POINT_STYLE_NO_OUTLINE);
		point.setAlgebraVisible(false);
		point.setEuclidianVisible(true);
		point.setLabelVisible(false);
		point.setHasPreviewPopup(true);
		point.setFixed(true);
		point.setLabelSimple("TableValuesPoints");
		point.setLabelSet(true);
		maybeSetPointColor(point, evaluatable);
	}

	private GeoPoint createNewPoint() {
		return new GeoPoint(construction, 0.0, 0.0, 1.0);
	}

	private static void maybeSetPointColor(GeoPoint point,
			GeoEvaluatable evaluatable) {
		GColor color = evaluatable instanceof GeoFunctionable ? evaluatable.getObjectColor()
				: GColor.Y_POINT_COLOR;
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
		if (points.size() > column) {
			removePoints(column);
			points.remove(column);
		}
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
		GeoEvaluatable geoEvaluatable = view.getEvaluatable(column);
		geoEvaluatable.setPointsVisible(visible);
		if (visible && points.get(column) == null) {
			createAndAddPoints(geoEvaluatable, column);
		} else if (!visible && points.get(column) != null) {
			removePoints(column);
		}
		construction.getKernel().getApplication().storeUndoInfo();
	}
}
