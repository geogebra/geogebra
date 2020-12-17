package org.geogebra.common.euclidian.modes;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Mouse handlers for shape tools
 * 
 * @author csilla
 */
public class ModeShape {

	private static final double MAX_SNAP_DISTANCE = 20;
	private static final double MAX_SNAP_SLOPE = 0.1;
	private EuclidianView view;
	private EuclidianController ec;
	/**
	 * start point of dragging movement
	 */
	private GPoint dragStartPoint = new GPoint();
	private boolean dragPointSet = false;
	private boolean moveEnded = false;
	private boolean wasDragged = false;
	/**
	 * preview for ShapeRectangle/ShapeRectangleRoundEdges/ShapeSquare
	 */
	private final GRectangle rectangle = AwtFactory.getPrototype().newRectangle(0,
			0);
	/**
	 * preview for ShapeEllipse/ShapeCircle
	 */
	private final GEllipse2DDouble ellipse = AwtFactory.getPrototype()
			.newEllipse2DDouble(0, 0, 0, 0);
	/**
	 * preview for ShapeLine
	 */
	private final GLine2D line = AwtFactory.getPrototype().newLine2D();
	/**
	 * preview for ShapeTriangle
	 */
	private final GGeneralPath polygon = AwtFactory.getPrototype()
			.newGeneralPath();
	private ArrayList<GPoint> pointListFreePoly = new ArrayList<>();

	/**
	 * @param view
	 *            - euclidianView
	 */
	public ModeShape(EuclidianView view) {
		this.ec = view.getEuclidianController();
		this.view = view;
	}

	/**
	 * if tool was changed clear data points
	 */
	public void clearPointList() {
		pointListFreePoly.clear();
	}

	/**
	 * get start point of dragging
	 * 
	 * @param event
	 *            - mouse event
	 */
	public void handleMousePressedForShapeMode(AbstractEvent event) {
		moveEnded = true;
		if (!dragPointSet || (pointListFreePoly.isEmpty()
				&& ec.getMode() == EuclidianConstants.MODE_SHAPE_FREEFORM)) {
			dragStartPoint.setLocation(event.getX(), event.getY());
			view.resetBoundingBoxes();
			pointListFreePoly.clear();
			pointListFreePoly.add(new GPoint(event.getX(), event.getY()));
			dragPointSet = true;
			return;
		}
		if (ec.getMode() == EuclidianConstants.MODE_SHAPE_FREEFORM) {
			if (pointListFreePoly.get(0)
					.distance(new GPoint(event.getX(), event.getY())) < 15
					&& pointListFreePoly.size() > 1) {
				pointListFreePoly.add(pointListFreePoly.get(0));
				pointListFreePoly.add(pointListFreePoly.get(0));
			} else {
				pointListFreePoly.add(new GPoint(event.getX(), event.getY()));
			}
		}
	}

	/**
	 * draw shape preview for mouse dragging
	 * 
	 * @param event
	 *            - mouse event
	 */
	public void handleMouseDraggedForShapeMode(AbstractEvent event) {
		if (!ec.isDraggingBeyondThreshold()) {
			return;
		}

		int mode = ec.getMode();
		wasDragged = true;
		if (mode != EuclidianConstants.MODE_SHAPE_FREEFORM) {
			dragPointSet = false;
		}
		if (mode == EuclidianConstants.MODE_SHAPE_RECTANGLE
				|| mode == EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES) {
			updateRectangle(event, false);
			if (mode == EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES) {
				view.setRounded(true);
			} else {
				view.setRounded(false);
			}
			view.setShapeRectangle(rectangle);
			view.repaintView();
		} else if (mode == EuclidianConstants.MODE_SHAPE_SQUARE) {
			updateRectangle(event, true);
			view.setRounded(false);
			view.setShapeRectangle(rectangle);
			view.repaintView();
		} else if (mode == EuclidianConstants.MODE_SHAPE_ELLIPSE
				|| mode == EuclidianConstants.MODE_SHAPE_CIRCLE) {
			if (mode == EuclidianConstants.MODE_SHAPE_ELLIPSE) {
				updateEllipse(event, false);
			} else {
				updateEllipse(event, true);
			}
			view.setShapeEllipse(ellipse);
			view.repaintView();
		} else if (mode == EuclidianConstants.MODE_SHAPE_LINE) {
			GPoint2D snap = snapPoint(dragStartPoint.getX(), dragStartPoint.getY(), event.getX(),
					event.getY());
			line.setLine(dragStartPoint.getX(), dragStartPoint.getY(),
					snap.getX(), snap.getY());
			view.setShapeLine(line);
			view.repaintView();
		} else if (mode == EuclidianConstants.MODE_SHAPE_TRIANGLE) {
			updateTriangle(event);
			view.setShapePolygon(polygon);
			view.repaintView();
		} else if (mode == EuclidianConstants.MODE_SHAPE_PENTAGON) {
			updateRegularPolygon(event);
			view.setShapePolygon(polygon);
			view.repaintView();
		} else if (mode == EuclidianConstants.MODE_MASK) {
			updateRectangle(event, false);
			view.setMaskPreview(rectangle);
			view.repaintView();
		} else if (mode == EuclidianConstants.MODE_SHAPE_FREEFORM) {
			updateFreeFormPolygon(event, wasDragged);
		}
	}

	/**
	 * @param x1
	 *            anchor x-coord
	 * @param y1
	 *            anchor y-coord
	 * @param x2
	 *            moving point x-coord
	 * @param y2
	 *            moving point y-coord
	 * @return moving point adjusted to be on horizontal / vertical line with
	 *         anchor
	 */
	public static GPoint2D snapPoint(double x1, double y1, double x2, double y2) {
		return new GPoint2D(snap(x2, x1, Math.abs(y1 - y2)), snap(y2, y1, Math.abs(x1 - x2)));
	}

	private static double snap(double y2, double y1, double scale) {
		if (Math.abs(y2 - y1) < Math.min(MAX_SNAP_DISTANCE, MAX_SNAP_SLOPE * scale)) {
			return y1;
		}
		return y2;
	}

	/**
	 * array of points of shape
	 * 
	 * @param event
	 *            - mouse event
	 * @param isSquare
	 *            - true if we have square
	 */
	private GeoPointND[] getPointArray(AbstractEvent event, boolean isSquare) {
		GeoPointND[] points = new GeoPointND[4];

		double startPointX = dragStartPoint.x;
		double startPointY = dragStartPoint.y;
		double endPointX, endPointY;

		// for width of square take the width of rectangle
		if (isSquare) {
			double[] coords = getEndPointScreenCoords(event, true);
			endPointX = coords[0];
			endPointY = coords[1];
		} else {
			endPointX = event.getX();
			endPointY = event.getY();
		}

		points[0] = invisibleScreenPoint(startPointX, startPointY);
		points[1] = invisibleScreenPoint(endPointX, startPointY);
		points[2] = invisibleScreenPoint(endPointX, endPointY);
		points[3] = invisibleScreenPoint(startPointX, endPointY);

		view.repaintView();
		return points;
	}

	private double[] getEndPointScreenCoords(AbstractEvent event,
			boolean isSquare) {
		double[] coords = new double[2];
		double width = isSquare ? rectangle.getWidth()
				: ellipse.getBounds().getWidth();
		if (dragStartPoint.x >= event.getX()) {
			coords[0] = dragStartPoint.x - width;
		} else {
			coords[0] = dragStartPoint.x + width;
		}

		if (dragStartPoint.y >= event.getY()) {
			coords[1] = dragStartPoint.y - width;
		} else {
			coords[1] = dragStartPoint.y + width;
		}

		return coords;
	}

	/**
	 * with mouse release create geoElement
	 * 
	 * @param event
	 *            - mouse event
	 * @return geo was created
	 */
	public GeoElement handleMouseReleasedForShapeMode(AbstractEvent event) {
		view.setRounded(false);
		int mode = ec.getMode();
		// make sure we set new start point after ignoring simple click
		if (mode != EuclidianConstants.MODE_SHAPE_FREEFORM && !wasDragged) {
			dragPointSet = false;
			dragStartPoint = new GPoint();
			return null;
		}
		if (mode == EuclidianConstants.MODE_SHAPE_RECTANGLE || ec
				.getMode() == EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES
				|| mode == EuclidianConstants.MODE_SHAPE_SQUARE) {
			boolean square = mode == EuclidianConstants.MODE_SHAPE_SQUARE;
			AlgoPolygon algo = getPolyAlgo(getPointArray(event, square));

			createPolygon(algo);

			view.setShapeRectangle(null);
			view.repaintView();
			wasDragged = false;
			return algo.getOutput(0);
		} else if (mode == EuclidianConstants.MODE_MASK) {
			AlgoPolygon algo = getPolyAlgo(getPointArray(event, false));

			createMask(algo);

			view.setMaskPreview(null);
			view.repaintView();
			wasDragged = false;
			return algo.getOutput(0);
		} else if (mode == EuclidianConstants.MODE_SHAPE_ELLIPSE
				|| mode == EuclidianConstants.MODE_SHAPE_CIRCLE) {
			double[] conicEqu;
			if (mode == EuclidianConstants.MODE_SHAPE_ELLIPSE) {
				conicEqu = getEquationOfConic(event, false);
			} else {
				conicEqu = getEquationOfConic(event, true);
			}
			GeoConic conic = new GeoConic(view.getKernel().getConstruction(),
					conicEqu);
			conic.setLabelVisible(false);
			conic.setIsShape(true);
			conic.setLabel(null);
			view.setShapeEllipse(null);
			view.repaintView();
			wasDragged = false;
			return conic;
		} else if (mode == EuclidianConstants.MODE_SHAPE_LINE) {
			GeoPoint[] points = getRealPointsOfLine(event);
			AlgoJoinPointsSegment algo = new AlgoJoinPointsSegment(
					view.getKernel().getConstruction(),
					 points[0], points[1]);
			GeoSegment segment = algo.getSegment();
			segment.setLabelVisible(false);
			segment.setIsShape(true);
			segment.setLabel(null);
			view.setShapeLine(null);
			view.repaintView();
			wasDragged = false;
			return segment;
		} else if (mode == EuclidianConstants.MODE_SHAPE_TRIANGLE
				|| mode == EuclidianConstants.MODE_SHAPE_PENTAGON) {
			GeoPoint[] points = null;
			if (mode == EuclidianConstants.MODE_SHAPE_TRIANGLE) {
				points = getRealPointsOfTriangle(event);
			} else {
				points = getRealPointsOfPolygon(event);
			}
			AlgoPolygon algo = getPolyAlgo(points);
			createPolygon(algo);
			view.setShapePolygon(null);
			view.repaintView();
			wasDragged = false;
			return algo.getOutput(0);
		} else if (mode == EuclidianConstants.MODE_SHAPE_FREEFORM) {
			if (wasDragged) {
				if (pointListFreePoly.size() > 1
						&& pointListFreePoly.get(0).distance(
								new
				GPoint(event.getX(), event.getY())) < 15) {
					pointListFreePoly.add(pointListFreePoly.get(0));
					pointListFreePoly.add(pointListFreePoly.get(0));
				} else {
					pointListFreePoly
							.add(new GPoint(event.getX(), event.getY()));
				 }
			}
			updateFreeFormPolygon(event, false);
			// close with double click
			if (pointListFreePoly.size() > 2
					&& pointListFreePoly.get(pointListFreePoly.size() - 1)
							.distance(
									pointListFreePoly.get(
											pointListFreePoly.size()
													- 2)) == 0) {
				AlgoPolygon algo = getPolyAlgo(getRealPointsOfFreeFormPolygon());
				createPolygon(algo);
				pointListFreePoly.clear();
				dragPointSet = false;
				polygon.reset();
				view.setShapePolygon(null);
				view.repaintView();
				return algo.getOutput(0);
			}
		}
		// if was drag finished with release
		wasDragged = false;
		return null;
	}

	private AlgoPolygon getPolyAlgo(GeoPointND[] pointArray) {
		return new AlgoPolygon(view.getKernel().getConstruction(), pointArray,
				null, null, false, null, null);
	}

	private static void createPolygon(AlgoPolygon algo) {
		GeoPolygon poly = algo.getPoly();
		poly.setLineThickness(EuclidianStyleConstants.DEFAULT_LINE_THICKNESS);
		poly.setIsShape(true);
		poly.setLabelVisible(false);
		poly.setAlphaValue(0);
		poly.setBackgroundColor(GColor.WHITE);
		poly.setObjColor(GColor.BLACK);
		poly.setLabel(null);
	}

	private static void createMask(AlgoPolygon algo) {
		GeoPolygon polygon = algo.getPoly();
		polygon.setIsMask(true);
		polygon.setBackgroundColor(GeoGebraColorConstants.MEBIS_MASK);
		polygon.setObjColor(GeoGebraColorConstants.MEBIS_MASK);
		polygon.setLabel(null);
	}

	/**
	 * needed only for free form polygon
	 * 
	 * @param event
	 *            - mouse event
	 */
	public void handleMouseMoveForShapeMode(AbstractEvent event) {
		if (ec.getMode() == EuclidianConstants.MODE_SHAPE_FREEFORM) {
			if (dragPointSet) {
				if (moveEnded) {
					polygon.lineTo(event.getX(), event.getY());
					moveEnded = false;
				} else {
					if (pointListFreePoly.isEmpty()) {
						return;
					}
					polygon.reset();
					polygon.moveTo(pointListFreePoly.get(0).x,
							pointListFreePoly.get(0).y);
					for (int index = 1; index < pointListFreePoly
							.size(); index++) {
						polygon.lineTo(pointListFreePoly.get(index).x,
								pointListFreePoly.get(index).y);
					}
					polygon.lineTo(event.getX(), event.getY());
				}
				view.setShapePolygon(polygon);
				view.repaintView();
			}
		}
	}

	private GeoPoint[] getRealPointsOfFreeFormPolygon() {
		pointListFreePoly.remove(pointListFreePoly.size() - 1);
		GeoPoint[] realPoints = new GeoPoint[pointListFreePoly.size()];
		int i = 0;
		for (GPoint gPoint : pointListFreePoly) {
			realPoints[i] = invisibleScreenPoint(gPoint.getX(), gPoint.getY());
			i++;
		}
		return realPoints;
	}

	private GeoPoint invisibleScreenPoint(double startX, double startY) {
		GeoPoint pt = new GeoPoint(view.getKernel().getConstruction(),
				view.toRealWorldCoordX(startX), view.toRealWorldCoordY(startY), 1);
		pt.setEuclidianVisible(false);
		pt.updateRepaint();
		return pt;
	}

	private GeoPoint[] getRealPointsOfPolygon(AbstractEvent event) {
		GeoPoint[] points = new GeoPoint[5];
		int[] pointsX;
		int[] pointsY;

		int radius = Math.abs(event.getY() - (dragStartPoint.y + event.getY()) / 2);

		pointsX = getXCoordinates((dragStartPoint.x + event.getX()) / 2, radius, 5, -Math.PI / 2);
		pointsY = getYCoordinates((dragStartPoint.y + event.getY()) / 2, radius, 5, -Math.PI / 2);

		for (int i = 0; i < pointsX.length; i++) {
			points[i] = invisibleScreenPoint(pointsX[i], pointsY[i]);
		}

		return points;
	}

	private GeoPoint[] getRealPointsOfTriangle(AbstractEvent event) {
		GeoPoint[] points = new GeoPoint[3];

		int height = event.getY() - dragStartPoint.y;

		if (height >= 0) {
			points[0] = invisibleScreenPoint(dragStartPoint.x, event.getY());
			points[1] = invisibleScreenPoint(event.getX(), event.getY());
			points[2] = invisibleScreenPoint((dragStartPoint.x + event.getX()) / 2.0,
					dragStartPoint.y);
		} else {
			points[0] = invisibleScreenPoint((dragStartPoint.x + event.getX()) / 2.0, event.getY());
			points[1] = invisibleScreenPoint(dragStartPoint.x, dragStartPoint.y);
			points[2] = invisibleScreenPoint(event.getX(), dragStartPoint.y);
		}

		return points;
	}

	private GeoPoint[] getRealPointsOfLine(AbstractEvent event) {
		GeoPoint[] points = new GeoPoint[2];

		double startX = dragStartPoint.getX();
		double startY = dragStartPoint.getY();
		GeoPoint startPoint = invisibleScreenPoint(startX, startY);
		GPoint2D snap = snapPoint(dragStartPoint.getX(), dragStartPoint.getY(), event.getX(),
				event.getY());
		double endX = snap.getX();
		double endY = snap.getY();
		GeoPoint endPoint = invisibleScreenPoint(endX, endY);

		points[0] = startPoint;
		points[1] = endPoint;
		return points;
	}

	private double[] getEquationOfConic(AbstractEvent event, boolean isCircle) {
		// real coords
		double startX = view.toRealWorldCoordX(dragStartPoint.x);
		double startY = view.toRealWorldCoordY(dragStartPoint.y);

		double endX, endY;
		if (isCircle) {
			double[] coords = getEndPointScreenCoords(event, false);
			endX = view.toRealWorldCoordX(coords[0]);
			endY = view.toRealWorldCoordY(coords[1]);
		} else {
			endX = view.toRealWorldCoordX(event.getX());
			endY = view.toRealWorldCoordY(event.getY());
		}

		// coords of center
		double centerX = (startX + endX) / 2;
		double centerY = (startY + endY) / 2;
		// minor and major axis
		double a = Math.hypot(centerX - centerX,
				centerY - endY);
		double b = Math.hypot(centerX - endX,
				centerY - centerY);

		// construct equation (x-center_x)^2 / b^2 + (y-center_y)^2 / a^2 = 1

		return new double[] { sq(1 / b), 0, sq(1 / a), -2 * centerX / sq(b),
				-2 * centerY / sq(a), sq(centerX / b) + sq(centerY / a) - 1 };
	}

	private static double sq(double d) {
		return Math.pow(d, 2);
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
		int dx = event.getX() - dragStartPoint.x;
		int width = Math.abs(dx);
		int dy = event.getY() - dragStartPoint.y;
		if (isSquare) {
			dy = dy > 0 ? width : -width;
		}
		int left = Math.min(dragStartPoint.x, dragStartPoint.x + dx);
		int top = Math.min(dragStartPoint.y, dragStartPoint.y + dy);
		rectangle.setLocation(left, top);
		int height = Math.abs(dy);
		rectangle.setSize(width, height);
	}

	/**
	 * update the coords of ellipse
	 * 
	 * @param event
	 *            - mouse event
	 * @param isCircle
	 *            - true if we want circle instead of ellipse
	 */
	protected void updateEllipse(AbstractEvent event, boolean isCircle) {
		updateRectangle(event, isCircle);
		ellipse.setFrame(rectangle.getMinX(), rectangle.getMinY(), rectangle.getWidth(),
				rectangle.getHeight());
	}

	/**
	 * update the coords of triangle
	 * 
	 * @param event
	 *            - mouse event
	 */
	protected void updateTriangle(AbstractEvent event) {
		int[] pointsX = new int[3];
		int[] pointsY = new int[3];

		polygon.reset();
		int height = event.getY() - dragStartPoint.y;

		if (height >= 0) {
			pointsX[0] = dragStartPoint.x;
			pointsX[1] = event.getX();
			pointsX[2] = Math.round((dragStartPoint.x + event.getX()) / 2.0f);
			pointsY[0] = event.getY();
			pointsY[1] = event.getY();
			pointsY[2] = dragStartPoint.y;
		} else {
			pointsX[0] = Math.round((dragStartPoint.x + event.getX()) / 2.0f);
			pointsX[1] = dragStartPoint.x;
			pointsX[2] = event.getX();
			pointsY[0] = event.getY();
			pointsY[1] = dragStartPoint.y;
			pointsY[2] = dragStartPoint.y;
		}

		polygon.moveTo(pointsX[0], pointsY[0]);
		for (int index = 1; index < pointsX.length; index++) {
			polygon.lineTo(pointsX[index], pointsY[index]);
		}
		polygon.closePath();
	}

	/**
	 * update the coords of regular polygon
	 * 
	 * @param event
	 *            - mouse event
	 */
	protected void updateRegularPolygon(AbstractEvent event) {
		int[] pointsX;
		int[] pointsY;

		polygon.reset();
		int radius = Math.abs(event.getY() - (dragStartPoint.y + event.getY()) / 2);

		pointsX = getXCoordinates((dragStartPoint.x + event.getX()) / 2, radius, 5, -Math.PI / 2);
		pointsY = getYCoordinates((dragStartPoint.y + event.getY()) / 2, radius, 5, -Math.PI / 2);

		polygon.moveTo(pointsX[0], pointsY[0]);
		for (int index = 1; index < pointsX.length; index++) {
			polygon.lineTo(pointsX[index], pointsY[index]);
		}
		polygon.closePath();
	}

	/**
	 * update coords of free form polygon
	 * 
	 * @param event
	 *            - mouse event
	 * @param wasDrag
	 *            - true if mouse was dragged
	 */
	protected void updateFreeFormPolygon(AbstractEvent event, boolean wasDrag) {
		if (pointListFreePoly.isEmpty()) {
			return;
		}
		polygon.reset();
		polygon.moveTo(pointListFreePoly.get(0).x, pointListFreePoly.get(0).y);
		if (pointListFreePoly.size() < 2) {
			return;
		}
		for (int index = 1; index < pointListFreePoly.size(); index++) {
			polygon.lineTo(pointListFreePoly.get(index).x,
					pointListFreePoly.get(index).y);
		}
		if (wasDrag) {
			polygon.lineTo(event.getX(), event.getY());
		}
		view.setShapePolygon(polygon);
		view.repaintView();
	}

	private static int[] getXCoordinates(int centerX, int radius, int vertexNr, double startAngle) {
		int[] res = new int[vertexNr];
		double addAngle = 2 * Math.PI / vertexNr;
		double angle = startAngle;
		for (int i = 0; i < vertexNr; i++) {
			res[i] = (int) Math.round(radius * Math.cos(angle)) + centerX;
			angle += addAngle;
		}
		return res;
	}

	private static int[] getYCoordinates(int centerY, int radius, int vertexNr,
			double startAngle) {
		int[] res = new int[vertexNr];
		double addAngle = 2 * Math.PI / vertexNr;
		double angle = startAngle;
		for (int i = 0; i < vertexNr; i++) {
			res[i] = (int) Math.round(radius * Math.sin(angle)) + centerY;
			angle += addAngle;
		}
		return res;
	}
}
