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
	protected GPoint dragStartPoint = new GPoint();
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
		dragStartPoint.setLocation(event.getX(), event.getY());
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
			updateRectangle(event, false);
			if (ec.getMode() == EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES) {
				view.setRounded(true);
			} else {
				view.setRounded(false);
			}
			view.setShapeRectangle(rectangle);
			view.repaintView();
		} else if (ec.getMode() == EuclidianConstants.MODE_SHAPE_SQUARE) {
			updateRectangle(event, true);
			view.setRounded(false);
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
	private GeoPointND[] getPointArray(AbstractEvent event, boolean isSquare) {
		GeoPointND[] points = new GeoPointND[4];

		double startPointX = view.toRealWorldCoordX(dragStartPoint.x);
		double startPointY = view.toRealWorldCoordY(dragStartPoint.y);
		double endPointX, endPointY;

		// for width of square take the width of rectangle
		if (isSquare) {
			double[] coords = getEndPointRealCoords(event);
			endPointX = coords[0];
			endPointY = coords[1];
		} else {
			endPointX = view.toRealWorldCoordX(event.getX());
			endPointY = view.toRealWorldCoordY(event.getY());
		}

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

	private double[] getEndPointRealCoords(AbstractEvent event) {
		double[] coords = new double[2];
		if (dragStartPoint.x > event.getX()
				&& dragStartPoint.y > event.getY()) {
			coords[0] = view
					.toRealWorldCoordX(dragStartPoint.x - rectangle.getWidth());
			coords[1] = view
					.toRealWorldCoordY(dragStartPoint.y - rectangle.getWidth());
		} else {
			coords[0] = view
					.toRealWorldCoordX(dragStartPoint.x + rectangle.getWidth());
			coords[1] = view
					.toRealWorldCoordY(dragStartPoint.y + rectangle.getWidth());
		}
		return coords;
	}

	/**
	 * with mouse release create geoElement
	 * 
	 * @param event
	 *            - mouse event
	 */
	public void handleMouseReleasedForShapeMode(AbstractEvent event) {
		if (ec.getMode() == EuclidianConstants.MODE_SHAPE_RECTANGLE || ec
				.getMode() == EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES
				|| ec.getMode() == EuclidianConstants.MODE_SHAPE_SQUARE) {
			if (ec.getMode() == EuclidianConstants.MODE_SHAPE_SQUARE) {
				algo = new AlgoPolygon(view.getKernel().getConstruction(), null,
						getPointArray(event, true), false);
			} else {
				algo = new AlgoPolygon(
					view.getKernel().getConstruction(),
				null, getPointArray(event,false), false);
			}
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
	 * @param isSquare
	 *            - true if we want square instead of rectangle
	 */
	protected void updateRectangle(AbstractEvent event, boolean isSquare) {
		if (rectangle == null) {
			rectangle = AwtFactory.getPrototype().newRectangle();
		}

		int dx = event.getX() - dragStartPoint.x;
		int dy = event.getY() - dragStartPoint.y;

		int width = dx;
		int height;
		if (isSquare) {
			height = dx;
		} else {
			height = dy;
		}

		if (height >= 0) {
			if (width >= 0) {
				rectangle.setLocation(dragStartPoint.x, dragStartPoint.y);
				rectangle.setSize(width, height);
			} else { // width < 0
				rectangle.setLocation(dragStartPoint.x + width,
						dragStartPoint.y);
				rectangle.setSize(-width, height);
			}
		} else { // height < 0
			if (width >= 0) {
				rectangle.setLocation(dragStartPoint.x,
						dragStartPoint.y + height);
				rectangle.setSize(width, -height);
			} else { // width < 0
				rectangle.setLocation(dragStartPoint.x + width,
						dragStartPoint.y + height);
				rectangle.setSize(-width, -height);
			}
		}
	}

}
