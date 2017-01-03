package org.geogebra.common.euclidian.modes;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author csilla
 * 
 *         mouse handlers for shape tools
 */
public class ModeShape {

	private EuclidianView view;
	private EuclidianController ec;
	/**
	 * start point of dragging movement
	 */
	protected GPoint selectionStartPoint = new GPoint();
	/**
	 * preview for ShapeRectangle
	 */
	protected GRectangle rectangle = AwtFactory.getPrototype().newRectangle(0,
			0);
	private AlgoElement algo = null;

	/**
	 * @param view
	 *            - euclidianView
	 */
	public ModeShape(EuclidianView view) {
		this.ec = view.getEuclidianController();
		this.view = view;
	}

	/**
	 * get start point of dragging
	 * 
	 * @param event
	 *            - mouse event
	 */
	public void handleMousePressedForShapeMode(AbstractEvent event) {
		selectionStartPoint.setLocation(event.getX(), event.getY());
	}

	/**
	 * draw shape preview for mouse dragging
	 * 
	 * @param event
	 *            - mouse event
	 */
	public void handleMouseDraggedForShapeMode(AbstractEvent event) {
		if (ec.getMode() == EuclidianConstants.MODE_SHAPE_RECTANGLE || ec
				.getMode() == EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES) {
			updateRectangle(event); 
			if (ec.getMode() == EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES) {
				view.setRounded(true);
			} else {
				view.setRounded(false);
			}
			view.setShapeRectangle(rectangle);
			view.repaintView();
		}
	}

	/**
	 * array of points of shape
	 * 
	 * @param event
	 *            - mouse event
	 */
	private GeoPointND[] getPointArray(AbstractEvent event) {
		GeoPointND[] points = new GeoPointND[4];

		double startPointX = view.toRealWorldCoordX(selectionStartPoint.x);
		double startPointY = view.toRealWorldCoordY(selectionStartPoint.y);
		double endPointX = view.toRealWorldCoordX(event.getX());
		double endPointY = view.toRealWorldCoordY(event.getY());

		GeoPoint startPoint = new GeoPoint(view.getKernel().getConstruction(),
				startPointX, startPointY, 1);
		points[0] = startPoint;

		GeoPoint leftPoint = new GeoPoint(view.getKernel().getConstruction(),
				endPointX, startPointY, 1);
		points[1] = leftPoint;

		GeoPoint endPoint = new GeoPoint(view.getKernel().getConstruction(),
				endPointX, endPointY, 1);
		points[2] = endPoint;

		GeoPoint rightPoint = new GeoPoint(view.getKernel().getConstruction(),
				startPointX, endPointY, 1);
		points[3] = rightPoint;

		return points;
	}

	/**
	 * with mouse release create geoElement
	 * 
	 * @param event
	 *            - mouse event
	 */
	public void handleMouseReleasedForShapeMode(AbstractEvent event) {
		if (ec.getMode() == EuclidianConstants.MODE_SHAPE_RECTANGLE || ec
				.getMode() == EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES) {
			algo = new AlgoPolygon(
					view.getKernel().getConstruction(),
				null, getPointArray(event), false);
			GeoElement poly = algo.getOutput(0);
			poly.setAlphaValue(0);
			poly.setBackgroundColor(GColor.WHITE);
			poly.setObjColor(GColor.BLACK);
			poly.updateRepaint();
			view.setShapeRectangle(null);
			view.repaintView();
		}
	}

	/**
	 * update the coords of rectangle
	 * 
	 * @param event
	 *            - mouse event
	 */
	protected void updateRectangle(AbstractEvent event) {
		/*
		 * if (!shouldUpdateRectangle(event)) {
		 */

		if (rectangle == null) {
			rectangle = AwtFactory.getPrototype().newRectangle();
		}

		int dx = event.getX() - selectionStartPoint.x;
		int dy = event.getY() - selectionStartPoint.y;

		int width = dx;
		int height = dy;

		if (height >= 0) {
			if (width >= 0) {
				rectangle.setLocation(selectionStartPoint.x,
						selectionStartPoint.y);
				rectangle.setSize(width, height);
			} else { // width < 0
				rectangle.setLocation(selectionStartPoint.x + width,
						selectionStartPoint.y);
				rectangle.setSize(-width, height);
			}
		} else { // height < 0
			if (width >= 0) {
				rectangle.setLocation(selectionStartPoint.x,
						selectionStartPoint.y + height);
				rectangle.setSize(width, -height);
			} else { // width < 0
				rectangle.setLocation(selectionStartPoint.x + width,
						selectionStartPoint.y + height);
				rectangle.setSize(-width, -height);
			}
		}
	}

}
