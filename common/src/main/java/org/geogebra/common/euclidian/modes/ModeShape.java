package org.geogebra.common.euclidian.modes;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.plugin.Operation;

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
	 * preview for ShapeRectangle/ShapeRectangleRoundEdges/ShapeSquare
	 */
	protected GRectangle rectangle = AwtFactory.getPrototype().newRectangle(0,
			0);
	/**
	 * preview for ShapeEllipse/ShapeCircle
	 */
	protected GEllipse2DDouble ellipse = AwtFactory.getPrototype()
			.newEllipse2DDouble(0, 0, 0, 0);
	/**
	 * preview for ShapeLine
	 */
	protected GLine2D line = AwtFactory.getPrototype().newLine2D();
	/**
	 * preview for ShapeTriangle
	 */
	protected GGeneralPath triangle = AwtFactory.getPrototype()
			.newGeneralPath();
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
		} else if (ec.getMode() == EuclidianConstants.MODE_SHAPE_ELLIPSE
				|| ec.getMode() == EuclidianConstants.MODE_SHAPE_CIRCLE) {
			if (ec.getMode() == EuclidianConstants.MODE_SHAPE_ELLIPSE) {
				updateEllipse(event, false);
			} else {
				updateEllipse(event, true);
			}
			view.setShapeEllipse(ellipse);
			view.repaintView();
		} else if (ec.getMode() == EuclidianConstants.MODE_SHAPE_LINE) {
			line.setLine(dragStartPoint.getX(), dragStartPoint.getY(),
					event.getX(), event.getY());
			view.setShapeLine(line);
			view.repaintView();
		} else if (ec.getMode() == EuclidianConstants.MODE_SHAPE_TRIANGLE) {
			updateTriangle(event);
			view.setShapeTriangle(triangle);
			view.repaintView();
		}
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

		double startPointX = view.toRealWorldCoordX(dragStartPoint.x);
		double startPointY = view.toRealWorldCoordY(dragStartPoint.y);
		double endPointX, endPointY;

		// for width of square take the width of rectangle
		if (isSquare) {
			double[] coords = getEndPointRealCoords(event, true);
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

	private double[] getEndPointRealCoords(AbstractEvent event,
			boolean isSquare) {
		double[] coords = new double[2];
		if (dragStartPoint.x > event.getX()
				&& dragStartPoint.y > event.getY()) {
			coords[0] = view
					.toRealWorldCoordX(
							dragStartPoint.x - (isSquare ? rectangle.getWidth()
									: ellipse.getBounds().getWidth()));
			coords[1] = view
					.toRealWorldCoordY(
							dragStartPoint.y - (isSquare ? rectangle.getWidth()
									: ellipse.getBounds().getWidth()));
		} else {
			coords[0] = view
					.toRealWorldCoordX(
							dragStartPoint.x + (isSquare ? rectangle.getWidth()
									: ellipse.getBounds().getWidth()));
			coords[1] = view
					.toRealWorldCoordY(
							dragStartPoint.y + (isSquare ? rectangle.getWidth()
									: ellipse.getBounds().getWidth()));
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
		} else if (ec.getMode() == EuclidianConstants.MODE_SHAPE_ELLIPSE
				|| ec.getMode() == EuclidianConstants.MODE_SHAPE_CIRCLE) {
			Equation conicEqu;
			if (ec.getMode() == EuclidianConstants.MODE_SHAPE_ELLIPSE) {
				conicEqu = getEquationOfConic(event, false);
			} else {
				conicEqu = getEquationOfConic(event, true);
			}
			conicEqu.initEquation();
			GeoElement[] geos = view.getKernel().getAlgebraProcessor()
					.processConic(conicEqu, conicEqu.wrap());
			geos[0].setLabelVisible(false);
			geos[0].updateRepaint();
			view.setShapeEllipse(null);
			view.repaintView();
		} else if (ec.getMode() == EuclidianConstants.MODE_SHAPE_LINE) {
			GeoPoint[] points = getRealPointsOfLine(event);
			algo = new AlgoJoinPointsSegment(view.getKernel().getConstruction(),
					null, points[0], points[1]);
			GeoElement segment = algo.getOutput(0);
			segment.setLabelVisible(false);
			segment.updateRepaint();
			view.setShapeLine(null);
			view.repaintView();
		} else if (ec.getMode() == EuclidianConstants.MODE_SHAPE_TRIANGLE) {
			GeoPoint[] points = getRealPointsOfTriangle(event);
			algo = new AlgoPolygon(view.getKernel().getConstruction(), null,
					points, false);
			// do not show edge points
			for (GeoPoint geoPoint : points) {
				geoPoint.setEuclidianVisible(false);
				geoPoint.updateRepaint();
			}
			// do not show segment labels
			GeoPolygon poly = (GeoPolygon) algo.getOutput(0);
			for (GeoSegmentND geoSeg : poly.getSegments()) {
				((GeoSegment) geoSeg).setLabelVisible(false);
			}
			poly.setAlphaValue(0);
			poly.setBackgroundColor(GColor.WHITE);
			poly.setObjColor(GColor.BLACK);
			poly.updateRepaint();
			view.setShapeTriangle(null);
			view.repaintView();
		}
	}

	private GeoPoint[] getRealPointsOfTriangle(AbstractEvent event) {
		GeoPoint[] points = new GeoPoint[3];

		int height = event.getY() - dragStartPoint.y;

		if (height >= 0) {
			points[0] = new GeoPoint(view.getKernel().getConstruction(), null,
					view.toRealWorldCoordX(dragStartPoint.x),
					view.toRealWorldCoordY(event.getY()), 1);
			points[1] = new GeoPoint(view.getKernel().getConstruction(), null,
					view.toRealWorldCoordX(event.getX()),
					view.toRealWorldCoordY(event.getY()), 1);
			points[2] = new GeoPoint(view.getKernel().getConstruction(), null,
					view.toRealWorldCoordX(
							(dragStartPoint.x + event.getX()) / 2),
					view.toRealWorldCoordY(dragStartPoint.y), 1);
		} else {
			points[0] = new GeoPoint(view.getKernel().getConstruction(), null,
					view.toRealWorldCoordX(
							(dragStartPoint.x + event.getX()) / 2),
					view.toRealWorldCoordY(event.getY()), 1);
			points[1] = new GeoPoint(view.getKernel().getConstruction(), null,
					view.toRealWorldCoordX(dragStartPoint.x),
					view.toRealWorldCoordY(dragStartPoint.y), 1);
			points[2] = new GeoPoint(view.getKernel().getConstruction(), null,
					view.toRealWorldCoordX(event.getX()),
					view.toRealWorldCoordY(dragStartPoint.y), 1);
		}

		return points;
	}

	private GeoPoint[] getRealPointsOfLine(AbstractEvent event) {
		GeoPoint[] points = new GeoPoint[2];

		double startX = view.toRealWorldCoordX(dragStartPoint.getX());
		double startY = view.toRealWorldCoordY(dragStartPoint.getY());
		GeoPoint startPoint = new GeoPoint(view.getKernel().getConstruction(),
				startX, startY, 1);

		double endX = view.toRealWorldCoordX(event.getX());
		double endY = view.toRealWorldCoordY(event.getY());
		GeoPoint endPoint = new GeoPoint(view.getKernel().getConstruction(),
				endX, endY, 1);

		points[0] = startPoint;
		points[1] = endPoint;
		return points;
	}

	private Equation getEquationOfConic(AbstractEvent event, boolean isCircle) {
		// real coords
		double startX = view.toRealWorldCoordX(dragStartPoint.x);
		double startY = view.toRealWorldCoordY(dragStartPoint.y);

		double endX, endY;
		if (isCircle) {
			double[] coords = getEndPointRealCoords(event, false);
			endX = coords[0];
			endY = coords[1];
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
		FunctionVariable xx = new FunctionVariable(view.getKernel(), "x");
		FunctionVariable yy = new FunctionVariable(view.getKernel(), "y");
		ExpressionNode rhs = new ExpressionNode(view.getKernel(), 1);
		ExpressionNode expCenterX = new ExpressionNode(view.getKernel(),
				centerX);
		ExpressionNode expCenterY = new ExpressionNode(view.getKernel(),
				centerY);
		ExpressionNode expA = new ExpressionNode(view.getKernel(),
				a);
		ExpressionNode expB = new ExpressionNode(view.getKernel(),
				b);

		ExpressionNode leftNumerator = new ExpressionNode(view.getKernel(), xx,
				Operation.MINUS, expCenterX);
		ExpressionNode leftNumeratorSqr = new ExpressionNode(view.getKernel(),
				leftNumerator, Operation.POWER,
				new ExpressionNode(view.getKernel(), 2));

		ExpressionNode leftDenom = new ExpressionNode(view.getKernel(), expB,
				Operation.POWER, new ExpressionNode(view.getKernel(), 2));

		ExpressionNode leftLhs = new ExpressionNode(view.getKernel(),
				leftNumeratorSqr, Operation.DIVIDE, leftDenom);

		ExpressionNode rightNumerator = new ExpressionNode(view.getKernel(), yy,
				Operation.MINUS, expCenterY);
		ExpressionNode rightNumeatorSqr = new ExpressionNode(view.getKernel(),
				rightNumerator, Operation.POWER,
				new ExpressionNode(view.getKernel(), 2));

		ExpressionNode rightDenom = new ExpressionNode(view.getKernel(), expA,
				Operation.POWER, new ExpressionNode(view.getKernel(), 2));

		ExpressionNode rightLhs = new ExpressionNode(view.getKernel(),
				rightNumeatorSqr, Operation.DIVIDE, rightDenom);

		ExpressionNode lhs = new ExpressionNode(view.getKernel(), leftLhs,
				Operation.PLUS, rightLhs);
		Equation equ = new Equation(view.getKernel(), lhs, rhs);

		return equ;
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

	/**
	 * update the coords of ellipse
	 * 
	 * @param event
	 *            - mouse event
	 * @param isCircle
	 *            - true if we want circle instead of ellipse
	 */
	protected void updateEllipse(AbstractEvent event, boolean isCircle) {
		if (ellipse == null) {
			ellipse = AwtFactory.getPrototype().newEllipse2DDouble(0, 0, 0, 0);
		}

		int dx = event.getX() - dragStartPoint.x;
		int dy = event.getY() - dragStartPoint.y;

		int width = dx;
		int height;
		if (isCircle) {
			height = dx;
		} else {
			height = dy;
		}

		if (height >= 0) {
			if (width >= 0) {
				ellipse.setFrame(dragStartPoint.x, dragStartPoint.y, width,
						height);
			} else { // width < 0
				ellipse.setFrame(dragStartPoint.x + width, dragStartPoint.y,
						-width, height);
			}
		} else { // height < 0
			if (width >= 0) {
				ellipse.setFrame(dragStartPoint.x, dragStartPoint.y + height,
						width, -height);
			} else { // width < 0
				ellipse.setFrame(dragStartPoint.x + width,
						dragStartPoint.y + height, -width, -height);
			}
		}
	}

	/**
	 * update the coords of triangle
	 * 
	 * @param event
	 *            - mouse event
	 */
	protected void updateTriangle(AbstractEvent event) {
		int pointsX[] = new int[3];
		int pointsY[] = new int[3];

		if (triangle == null) {
			triangle = AwtFactory.getPrototype().newGeneralPath();
		}

		triangle.reset();
		int height = event.getY() - dragStartPoint.y;

		if (height >= 0) {
				pointsX[0] = dragStartPoint.x;
				pointsX[1] = event.getX();
				pointsX[2] = (dragStartPoint.x + event.getX()) / 2;
				pointsY[0] = event.getY();
				pointsY[1] = event.getY();
				pointsY[2] = dragStartPoint.y;
		} else {
				pointsX[0] = (dragStartPoint.x + event.getX()) / 2;
				pointsX[1] = dragStartPoint.x;
				pointsX[2] = event.getX();
				pointsY[0] = event.getY();
				pointsY[1] = dragStartPoint.y;
				pointsY[2] = dragStartPoint.y;
		}

		triangle.moveTo(pointsX[0], pointsY[0]);
		for (int index = 1; index < pointsX.length; index++) {
			triangle.lineTo(pointsX[index], pointsY[index]);
		}
		triangle.closePath();
	}

}
