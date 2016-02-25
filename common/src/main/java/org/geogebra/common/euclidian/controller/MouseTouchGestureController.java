package org.geogebra.common.euclidian.controller;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoSphereNDPointRadius;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.MyMath;

import java.util.ArrayList;


public class MouseTouchGestureController {

	/**
	 * Threshold for moving in case of a multitouch-event (pixel).
	 */
	public static int MIN_MOVE = 5;

	protected App app;

	protected EuclidianController ec;

	/**
	 * The mode of the multitouch-event.
	 */
	protected MultitouchMode multitouchMode;

	/**
	 * Actual scale of the axes (has to be saved during multitouch).
	 */
	protected double scale;
	
	/**
	 * Conic which's size is changed.
	 */
	protected GeoConic scaleConic;

	/**
	 * Midpoint of scaleConic: [0] ... x-coordinate [1] ... y-coordinate/
	 */
	protected double[] midpoint;

	/**
	 * X-coordinates of the points that define scaleConic/
	 */
	protected double[] originalPointX;

	/**
	 * Y-coordinates of the points that define scaleConic/
	 */
	protected double[] originalPointY;

	/**
	 * Coordinates of the center of the multitouch-event.
	 */
	protected int oldCenterX, oldCenterY;

	private double originalRadius;

	private GeoPoint firstFingerTouch;

	private GeoPoint secondFingerTouch;

	private GeoLine lineToMove;

	private boolean firstTouchIsAttachedToStartPoint;

	public MouseTouchGestureController(App app, EuclidianController ec) {
		this.app = app;
		this.ec = ec;
	}

	public void twoTouchMove(double x1d, double y1d, double x2d, double y2d) {
		int x1 = (int) x1d;
		int x2 = (int) x2d;
		int y1 = (int) y1d;
		int y2 = (int) y2d;

		if ((x1 == x2 && y1 == y2) || ec.oldDistance == 0) {
			return;
		}

		switch (multitouchMode) {
		case zoomY:
			if (scale == 0 || !app.isShiftDragZoomEnabled()) {
				return;
			}
			double newRatioY = this.scale * (y1 - y2) / ec.oldDistance;
			ec.view.setCoordSystem(ec.view.getXZero(), ec.view.getYZero(),
					ec.view.getXscale(), newRatioY);
			break;
		case zoomX:
			if (this.scale == 0 || !app.isShiftDragZoomEnabled()) {
				return;
			}
			double newRatioX = this.scale * (x1 - x2) / ec.oldDistance;
			ec.view.setCoordSystem(ec.view.getXZero(), ec.view.getYZero(),
					newRatioX, ec.view.getYscale());
			break;
		case circle3Points:
			double dist = MyMath.length(x1 - x2, y1 - y2);
			this.scale = dist / ec.oldDistance;
			int i = 0;

			for (GeoPointND p : scaleConic.getFreeInputPoints(ec.view)) {
				double newX = midpoint[0] + (originalPointX[i] - midpoint[0])
						* scale;
				double newY = midpoint[1] + (originalPointY[i] - midpoint[1])
						* scale;
				p.setCoords(newX, newY, 1.0);
				p.updateCascade();
				i++;
			}
			ec.kernel.notifyRepaint();
			break;
		case circle2Points:
			double dist2P = MyMath.length(x1 - x2, y1 - y2);
			this.scale = dist2P / ec.oldDistance;

			// index 0 is the midpoint, index 1 is the point on the circle
			GeoPointND p = scaleConic.getFreeInputPoints(ec.view).get(1);
			double newX = midpoint[0] + (originalPointX[1] - midpoint[0])
					* scale;
			double newY = midpoint[1] + (originalPointY[1] - midpoint[1])
					* scale;
			p.setCoords(newX, newY, 1.0);
			p.updateCascade();
			ec.kernel.notifyRepaint();
			break;
		case circleRadius:
			double distR = MyMath.length(x1 - x2, y1 - y2);
			this.scale = distR / ec.oldDistance;
			GeoNumeric newRadius = new GeoNumeric(ec.kernel.getConstruction(),
					this.scale * this.originalRadius);

			((AlgoSphereNDPointRadius) scaleConic.getParentAlgorithm())
					.setRadius(newRadius);
			scaleConic.updateCascade();
			ec.kernel.notifyUpdate(scaleConic);
			ec.kernel.notifyRepaint();
			break;
		case circleFormula:
			double distF = MyMath.length(x1 - x2, y1 - y2);
			this.scale = distF / ec.oldDistance;

			scaleConic.halfAxes[0] = this.scale * this.originalRadius;
			scaleConic.halfAxes[1] = this.scale * this.originalRadius;
			scaleConic.updateCascade();
			ec.kernel.notifyUpdate(scaleConic);
			ec.kernel.notifyRepaint();
			break;
		case moveLine:
			// ignore minimal changes of finger-movement
			if (onlyJitter(firstFingerTouch.getX(), firstFingerTouch.getY(),
					secondFingerTouch.getX(), secondFingerTouch.getY(), x1d,
					y1d, x2d, y2d)) {
				return;
			}

			Coords oldStart = firstFingerTouch.getCoords();
			Coords oldEnd = secondFingerTouch.getCoords();
			if (firstTouchIsAttachedToStartPoint) {
				firstFingerTouch.setCoords(ec.view.toRealWorldCoordX(x1d),
						ec.view.toRealWorldCoordY(y1d), 1);
				secondFingerTouch.setCoords(ec.view.toRealWorldCoordX(x2d),
						ec.view.toRealWorldCoordY(y2d), 1);
			} else {
				secondFingerTouch.setCoords(ec.view.toRealWorldCoordX(x1d),
						ec.view.toRealWorldCoordY(y1d), 1);
				firstFingerTouch.setCoords(ec.view.toRealWorldCoordX(x2d),
						ec.view.toRealWorldCoordY(y2d), 1);
			}

			// set line through the two finger touches
			Coords crossP = firstFingerTouch.getCoords().crossProduct(
					secondFingerTouch.getCoords());
			lineToMove.setCoords(crossP.getX(), crossP.getY(), crossP.getZ());
			lineToMove.updateCascade();

			// update coords of startPoint
			lineToMove.pointChanged(lineToMove.getStartPoint());
			lineToMove.getStartPoint().updateCoords();

			// update coords of endPoint
			lineToMove.pointChanged(lineToMove.getEndPoint());
			lineToMove.getEndPoint().updateCoords();

			// also move points along the line
			double newStartX = lineToMove.getStartPoint().getX()
					- (oldStart.getX() - firstFingerTouch.getX());
			double newStartY = lineToMove.getStartPoint().getY()
					- (oldStart.getY() - firstFingerTouch.getY());
			double newEndX = lineToMove.getEndPoint().getX()
					- (oldEnd.getX() - secondFingerTouch.getX());
			double newEndY = lineToMove.getEndPoint().getY()
					- (oldEnd.getY() - secondFingerTouch.getY());

			lineToMove.getStartPoint().setCoords(newStartX, newStartY, 1);
			lineToMove.getEndPoint().setCoords(newEndX, newEndY, 1);

			lineToMove.getStartPoint().updateCascade();
			lineToMove.getEndPoint().updateCascade();

			ec.kernel.notifyUpdate(lineToMove.getStartPoint());
			ec.kernel.notifyUpdate(lineToMove.getEndPoint());

			ec.kernel.notifyRepaint();

			break;
		default:
			if (!app.isShiftDragZoomEnabled()) {
				return;
			}
			// pinch
			ec.twoTouchMoveCommon(x1, y1, x2, y2);

			int centerX = (x1 + x2) / 2;
			int centerY = (y1 + y2) / 2;

			if (MyMath.length(oldCenterX - centerX, oldCenterY - centerY) > MIN_MOVE) {
				ec.view.rememberOrigins();
				ec.view.translateCoordSystemInPixels(centerX - oldCenterX,
						centerY - oldCenterY, 0,
						EuclidianConstants.MODE_TRANSLATEVIEW);

				oldCenterX = centerX;
				oldCenterY = centerY;
			}
		}
	}

	public void twoTouchStart(double x1, double y1, double x2, double y2) {
		this.scaleConic = null;

		ec.view.setHits(new GPoint((int) x1, (int) y1), PointerEventType.TOUCH);
		// needs to be copied, because the reference is changed in the next step
		Hits hits1 = new Hits();
		for (GeoElement geo : ec.view.getHits()) {
			hits1.add(geo);
		}

		ec.view.setHits(new GPoint((int) x2, (int) y2), PointerEventType.TOUCH);
		Hits hits2 = ec.view.getHits();

		oldCenterX = (int) (x1 + x2) / 2;
		oldCenterY = (int) (y1 + y2) / 2;

		if (hits1.hasYAxis() && hits2.hasYAxis()) {
			this.multitouchMode = MultitouchMode.zoomY;
			ec.oldDistance = y1 - y2;
			this.scale = ec.view.getYscale();
		} else if (hits1.hasXAxis() && hits2.hasXAxis()) {
			this.multitouchMode = MultitouchMode.zoomX;
			ec.oldDistance = x1 - x2;
			this.scale = ec.view.getXscale();
		} else if (hits1.size() > 0 && hits2.size() > 0
				&& hits1.get(0) == hits2.get(0)
				&& hits1.get(0) instanceof GeoConic
				// isClosedPath: true for circle and ellipse
				&& ((GeoConic) hits1.get(0)).isClosedPath()) {
			this.scaleConic = (GeoConic) hits1.get(0);
			// TODO: select scaleConic

			if (scaleConic.getFreeInputPoints(ec.view) == null
					&& scaleConic.isCircle()) {
				this.multitouchMode = MultitouchMode.circleFormula;
				this.originalRadius = scaleConic.getHalfAxis(0);
			} else if (scaleConic.getFreeInputPoints(ec.view).size() >= 3) {
				this.multitouchMode = MultitouchMode.circle3Points;
			} else if (scaleConic.getFreeInputPoints(ec.view).size() == 2) {
				this.multitouchMode = MultitouchMode.circle2Points;
			} else if (scaleConic.getParentAlgorithm() instanceof AlgoCirclePointRadius) {
				this.multitouchMode = MultitouchMode.circleRadius;
				AlgoElement algo = scaleConic.getParentAlgorithm();
				NumberValue radius = (NumberValue) algo.input[1];
				this.originalRadius = radius.getDouble();
			} else {
				// TODO scale other conic-types (e.g. ellipses with formula)
				scaleConic = null;
				ec.clearSelections();
				this.multitouchMode = MultitouchMode.view;
				ec.twoTouchStartCommon(x1, y1, x2, y2);
				return;
			}
			ec.twoTouchStartCommon(x1, y1, x2, y2);

			midpoint = new double[] { scaleConic.getMidpoint().getX(),
					scaleConic.getMidpoint().getY() };

			ArrayList<GeoPointND> points = scaleConic
					.getFreeInputPoints(ec.view);
			this.originalPointX = new double[points.size()];
			this.originalPointY = new double[points.size()];
			for (int i = 0; i < points.size(); i++) {
				this.originalPointX[i] = points.get(i).getCoords().getX();
				this.originalPointY[i] = points.get(i).getCoords().getY();
			}
		} else if (hits1.size() > 0 && hits2.size() > 0
				&& hits1.get(0) == hits2.get(0)
				&& hits1.get(0) instanceof GeoLine
				&& isMovableWithTwoFingers(hits1.get(0))) {
			this.multitouchMode = MultitouchMode.moveLine;
			lineToMove = (GeoLine) hits1.get(0);

			GeoPoint touch1 = new GeoPoint(ec.kernel.getConstruction(),
					ec.view.toRealWorldCoordX(x1),
					ec.view.toRealWorldCoordY(y1), 1);
			GeoPoint touch2 = new GeoPoint(ec.kernel.getConstruction(),
					ec.view.toRealWorldCoordX(x2),
					ec.view.toRealWorldCoordY(y2), 1);

			firstTouchIsAttachedToStartPoint = setFirstTouchToStartPoint(
					touch1, touch2);

			if (firstTouchIsAttachedToStartPoint) {
				firstFingerTouch = touch1;
				secondFingerTouch = touch2;
			} else {
				firstFingerTouch = touch2;
				secondFingerTouch = touch1;
			}
			ec.twoTouchStartCommon(x1, y1, x2, y2);
		} else {
			ec.clearSelections();
			this.multitouchMode = MultitouchMode.view;
			ec.twoTouchStartCommon(x1, y1, x2, y2);
		}
	}

	/**
	 * @param geoElement
	 *            {@link GeoElement}
	 * @return true if GeoElement should be movable with two fingers
	 */
	private boolean isMovableWithTwoFingers(GeoElement geoElement) {
		return geoElement.getParentAlgorithm().getRelatedModeID() == EuclidianConstants.MODE_JOIN
				|| geoElement.getParentAlgorithm().getRelatedModeID() == EuclidianConstants.MODE_SEGMENT
				|| geoElement.getParentAlgorithm().getRelatedModeID() == EuclidianConstants.MODE_RAY;
	}

	/**
	 * @param touch1
	 *            {@link GeoPoint}
	 * @param touch2
	 *            {@link GeoPoint}
	 * @return true if the first touch should be attached to the startPoint
	 */
	private boolean setFirstTouchToStartPoint(GeoPoint touch1, GeoPoint touch2) {
		if (lineToMove.getStartPoint().getX() < lineToMove.getEndPoint().getX()) {
			return touch1.getX() < touch2.getX();
		}
		return touch2.getX() < touch1.getX();
	}

	/**
	 * screen coordinates
	 * 
	 * @param oldStartX
	 * @param oldStartY
	 * @param oldEndX
	 * @param oldEndY
	 * @param newStartX
	 * @param newStartY
	 * @param newEndX
	 * @param newEndY
	 * @return true if there are only minimal changes of the two finger-touches
	 */
	private boolean onlyJitter(double oldStartX, double oldStartY,
			double oldEndX, double oldEndY, double newStartX, double newStartY,
			double newEndX, double newEndY) {
		double capThreshold = app.getCapturingThreshold(PointerEventType.TOUCH);
		return Math.abs(oldStartX - newStartX) < capThreshold
				&& Math.abs(oldStartY - newStartY) < capThreshold
				&& Math.abs(oldEndX - newEndX) < capThreshold
				&& Math.abs(oldEndY - newEndY) < capThreshold;
	}
}

