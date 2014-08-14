package geogebra.html5.euclidian;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.kernel.algos.AlgoCirclePointRadius;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.util.MyMath;
import geogebra.web.euclidian.EuclidianPenFreehand;
import geogebra.web.euclidian.EuclidianPenFreehand.ShapeType;

import java.util.ArrayList;

public abstract class EuclidianControllerWeb extends EuclidianController {

	/**
	 * different modes of a multitouch-event
	 */
	protected enum scaleMode {
		/**
		 * scale x-axis (two TouchStartEvents on the x-axis)
		 */
		zoomX,
		/**
		 * scale y-axis (two TouchStartEvents on the y-axis)
		 */
		zoomY,
		/**
		 * scale a circle or ellipsis with three points or an ellipsis with 5
		 * points
		 */
		circle3Points,
		/**
		 * scale a circle with 2 points
		 */
		circle2Points,
		/**
		 * scale a circle given with midpoint and a number-input as radius
		 */
		circleRadius,
		/**
		 * zooming
		 */
		view;
	}

	/**
	 * threshold for moving in case of a multitouch-event (pixel)
	 */
	protected final static int MIN_MOVE = 5;

	/**
	 * the mode of the actual multitouch-event
	 */
	protected scaleMode multitouchMode;

	/**
	 * actual scale of the axes (has to be saved during multitouch)
	 */
	protected double scale;

	/**
	 * conic which's size is changed
	 */
	protected GeoConic scaleConic;

	/**
	 * midpoint of scaleConic: [0] ... x-coordinate [1] ... y-coordinate
	 */
	protected double[] midpoint;

	/**
	 * x-coordinates of the points that define scaleConic
	 */
	protected double[] originalPointX;

	/**
	 * y-coordinates of the points that define scaleConic
	 */
	protected double[] originalPointY;

	/**
	 * coordinates of the center of the multitouch-event
	 */
	protected int oldCenterX, oldCenterY;

	/**
	 * flag for blocking the scaling of the axes
	 */
	protected boolean moveAxesAllowed = true;

	private int previousMode = -1;

	private double originalRadius;

	public EuclidianControllerWeb(App app) {
		super(app);
	}

	@Override
	public void twoTouchStart(double x1, double y1, double x2, double y2) {
		view.setHits(new GPoint((int) x1, (int) y1), PointerEventType.TOUCH);
		// needs to be copied, because the reference is changed in the next step
		Hits hits1 = new Hits();
		for (GeoElement geo : view.getHits()) {
			hits1.add(geo);
		}

		view.setHits(new GPoint((int) x2, (int) y2), PointerEventType.TOUCH);
		Hits hits2 = view.getHits();

		oldCenterX = (int) (x1 + x2) / 2;
		oldCenterY = (int) (y1 + y2) / 2;

		if (hits1.hasYAxis() && hits2.hasYAxis()) {
			this.multitouchMode = scaleMode.zoomY;
			this.oldDistance = y1 - y2;
			this.scale = this.view.getYscale();
		} else if (hits1.hasXAxis() && hits2.hasXAxis()) {
			this.multitouchMode = scaleMode.zoomX;
			this.oldDistance = x1 - x2;
			this.scale = this.view.getXscale();
		} else if (hits1.size() > 0
		        && hits2.size() > 0
		        && hits1.get(0) == hits2.get(0)
		        && hits1.get(0) instanceof GeoConic
		        // isClosedPath: true for circle and ellipse
		        && ((GeoConic) hits1.get(0)).isClosedPath()
		        && (((GeoConic) hits1.get(0)).getFreeInputPoints(this.view)
		                .size() >= 2 || (hits1.get(0).getParentAlgorithm().input[1]
		                .isIndependent() && !(hits1.get(0).getParentAlgorithm().input[1].labelSet)))) {
			this.scaleConic = (GeoConic) hits1.get(0);
			// TODO: select scaleConic

			if (((GeoConic) hits1.get(0)).getFreeInputPoints(this.view).size() >= 3) {
				this.multitouchMode = scaleMode.circle3Points;
			} else if (((GeoConic) hits1.get(0)).getFreeInputPoints(this.view)
			        .size() == 2) {
				this.multitouchMode = scaleMode.circle2Points;
			} else {
				this.multitouchMode = scaleMode.circleRadius;
				AlgoElement algo = scaleConic.getParentAlgorithm();
				NumberValue radius = (NumberValue) algo.input[1];
				this.originalRadius = radius.getDouble();
			}
			super.twoTouchStart(x1, y1, x2, y2);

			midpoint = new double[] { scaleConic.getMidpoint().getX(),
			        scaleConic.getMidpoint().getY() };

			ArrayList<GeoPointND> points = scaleConic
			        .getFreeInputPoints(this.view);
			this.originalPointX = new double[points.size()];
			this.originalPointY = new double[points.size()];
			for (int i = 0; i < points.size(); i++) {
				this.originalPointX[i] = points.get(i).getCoords().getX();
				this.originalPointY[i] = points.get(i).getCoords().getY();
			}
		} else {
			this.multitouchMode = scaleMode.view;
			super.twoTouchStart(x1, y1, x2, y2);
		}
	}

	@Override
	public void twoTouchMove(double x1d, double y1d, double x2d, double y2d) {
		int x1 = (int) x1d;
		int x2 = (int) x2d;
		int y1 = (int) y1d;
		int y2 = (int) y2d;

		switch (this.multitouchMode) {
		case zoomY:
			double newRatioY = this.scale * (y1 - y2) / this.oldDistance;
			this.view.setCoordSystem(this.view.getXZero(),
			        this.view.getYZero(), this.view.getXscale(), newRatioY);
			break;
		case zoomX:
			double newRatioX = this.scale * (x1 - x2) / this.oldDistance;
			this.view.setCoordSystem(this.view.getXZero(),
			        this.view.getYZero(), newRatioX, this.view.getYscale());
			break;
		case circle3Points:
			double dist = MyMath.length(x1 - x2, y1 - y2);
			this.scale = dist / this.oldDistance;
			int i = 0;

			for (GeoPointND p : scaleConic.getFreeInputPoints(this.view)) {
				double newX = midpoint[0] + (originalPointX[i] - midpoint[0])
				        * scale;
				double newY = midpoint[1] + (originalPointY[i] - midpoint[1])
				        * scale;
				p.setCoords(newX, newY, 1.0);
				p.updateCascade();
				i++;
			}
			kernel.notifyRepaint();
			break;
		case circle2Points:
			double dist2P = MyMath.length(x1 - x2, y1 - y2);
			this.scale = dist2P / this.oldDistance;

			// index 0 is the midpoint, index 1 is the point on the circle
			GeoPointND p = scaleConic.getFreeInputPoints(this.view).get(1);
			double newX = midpoint[0] + (originalPointX[1] - midpoint[0])
			        * scale;
			double newY = midpoint[1] + (originalPointY[1] - midpoint[1])
			        * scale;
			p.setCoords(newX, newY, 1.0);
			p.updateCascade();
			kernel.notifyRepaint();
			break;
		case circleRadius:
			double distR = MyMath.length(x1 - x2, y1 - y2);
			this.scale = distR / this.oldDistance;

			GeoPoint center = (GeoPoint) this.scaleConic.getParentAlgorithm().input[0];
			GeoNumeric newRadius = new GeoNumeric(
			        this.kernel.getConstruction(), this.scale
			                * this.originalRadius);

			scaleConic.setParentAlgorithm(new AlgoCirclePointRadius(this.kernel
			        .getConstruction(), center, newRadius));
			scaleConic.setCircle(center, newRadius.getDouble());
			scaleConic.updateCascade();
			kernel.notifyUpdate(scaleConic);
			this.kernel.notifyRepaint();
			break;
		default:
			// pinch
			super.twoTouchMove(x1, y1, x2, y2);

			int centerX = (x1 + x2) / 2;
			int centerY = (y1 + y2) / 2;

			if (MyMath.length(oldCenterX - centerX, oldCenterY - centerY) > MIN_MOVE) {
				view.rememberOrigins();
				view.setCoordSystemFromMouseMove(centerX - oldCenterX, centerY
				        - oldCenterY, EuclidianConstants.MODE_TRANSLATEVIEW);

				oldCenterX = centerX;
				oldCenterY = centerY;
			}
		}
	}

	/**
	 * position of last mouseDown or touchStart
	 */
	protected GPoint startPosition;

	@Override
	protected void switchModeForMousePressed(AbstractEvent e) {
		startPosition = new GPoint(e.getX(), e.getY());

		super.switchModeForMousePressed(e);

		if (this.selPoints() == 0
		        && (this.mode == EuclidianConstants.MODE_JOIN
		                || this.mode == EuclidianConstants.MODE_SEGMENT
		                || this.mode == EuclidianConstants.MODE_RAY
		                || this.mode == EuclidianConstants.MODE_VECTOR
		                || this.mode == EuclidianConstants.MODE_CIRCLE_TWO_POINTS
		                || this.mode == EuclidianConstants.MODE_SEMICIRCLE || this.mode == EuclidianConstants.MODE_REGULAR_POLYGON)) {

			this.mouseLoc = new GPoint(e.getX(), e.getY());
			this.view.setHits(this.mouseLoc, e.getType());

			super.wrapMouseReleased(e);
			e.release();

			if (this.mode == EuclidianConstants.MODE_REGULAR_POLYGON
			        && this.view.getPreviewDrawable() == null) {
				this.view.setPreview(view.createPreviewSegment(selectedPoints));
			}

			this.updatePreview();
			this.view.updatePreviewableForProcessMode();
		}
	}

	@Override
	protected boolean createNewPoint(Hits hits, boolean onPathPossible,
	        boolean inRegionPossible, boolean intersectPossible,
	        boolean doSingleHighlighting, boolean complex) {
		boolean newPointCreated = super.createNewPoint(hits, onPathPossible,
		        inRegionPossible, intersectPossible, doSingleHighlighting,
		        complex);

		GeoElement point = this.view.getHits().getFirstHit(Test.GEOPOINT);
		if (!newPointCreated
		        && this.selPoints() == 1
		        && (this.mode == EuclidianConstants.MODE_JOIN
		                || this.mode == EuclidianConstants.MODE_SEGMENT
		                || this.mode == EuclidianConstants.MODE_RAY
		                || this.mode == EuclidianConstants.MODE_VECTOR
		                || this.mode == EuclidianConstants.MODE_CIRCLE_TWO_POINTS
		                || this.mode == EuclidianConstants.MODE_SEMICIRCLE || this.mode == EuclidianConstants.MODE_REGULAR_POLYGON)) {
			handleMovedElement(point, false, PointerEventType.MOUSE);
		}

		return newPointCreated;
	}

	@Override
	protected void wrapMouseDragged(AbstractEvent event) {
		super.wrapMouseDragged(event);
		if (movedGeoPoint != null
		        && (this.mode == EuclidianConstants.MODE_JOIN
		                || this.mode == EuclidianConstants.MODE_SEGMENT
		                || this.mode == EuclidianConstants.MODE_RAY
		                || this.mode == EuclidianConstants.MODE_VECTOR
		                || this.mode == EuclidianConstants.MODE_CIRCLE_TWO_POINTS
		                || this.mode == EuclidianConstants.MODE_SEMICIRCLE || this.mode == EuclidianConstants.MODE_REGULAR_POLYGON)) {
			// nothing was dragged
			super.wrapMouseMoved(event);
		}

		if (view.getPreviewDrawable() != null
		        && event.getType() == PointerEventType.TOUCH) {
			this.view.updatePreviewableForProcessMode();
		}
	}

	/**
	 * selects a GeoElement; no effect, if it is already selected
	 * 
	 * @param geo
	 *            the GeoElement to be selected
	 */
	public void select(GeoElement geo) {
		if (geo != null && !selectedGeos.contains(geo)) {
			Hits h = new Hits();
			h.add(geo);
			addSelectedGeo(h, 1, false);
		}
	}

	@Override
	public void wrapMouseReleased(AbstractEvent event) {
		// will be reset in wrapMouseReleased
		GeoPointND p = this.selPoints() == 1 ? selectedPoints.get(0) : null;

		if (this.mode == EuclidianConstants.MODE_JOIN
		        || this.mode == EuclidianConstants.MODE_SEGMENT
		        || this.mode == EuclidianConstants.MODE_RAY
		        || this.mode == EuclidianConstants.MODE_VECTOR
		        || this.mode == EuclidianConstants.MODE_CIRCLE_TWO_POINTS
		        || this.mode == EuclidianConstants.MODE_SEMICIRCLE
		        || this.mode == EuclidianConstants.MODE_REGULAR_POLYGON) {

			if (getDistance(startPosition,
			        new GPoint(event.getX(), event.getY())) < this.app
			        .getCapturingThreshold(event.getType())) {

				this.view.setHits(new GPoint(event.getX(), event.getY()),
				        event.getType());

				if (this.selPoints() == 1 && !view.getHits().contains(p)) {
					super.wrapMouseReleased(event);
				}

				return;
			}

			super.wrapMouseReleased(event);

			this.view.setHits(new GPoint(event.getX(), event.getY()),
			        event.getType());
			Hits hits = view.getHits();

			if (p != null && hits.getFirstHit(Test.GEOPOINTND) == null) {
				if (!selectedPoints.contains(p)) {
					this.selectedPoints.add(p);
				}
				createNewPointForModeOther(hits);
				this.view.setHits(new GPoint(event.getX(), event.getY()),
				        event.getType());
				hits = view.getHits();
				switchModeForProcessMode(hits, event.isControlDown(), null);
			}
		} else {
			super.wrapMouseReleased(event);
		}
	}

	@Override
	protected boolean moveAxesPossible() {
		return super.moveAxesPossible() && this.moveAxesAllowed;
	}

	/**
	 * sets the mode to freehand_shape with an expected shape depending on the
	 * actual mode (has no effect if no mode is set that can be turned into
	 * freehand_shape)
	 * 
	 * For some modes requires that view.setHits(...) has been called with the
	 * correct parameters or movedGeoPoint is set correct in order to use other
	 * GeoPoints (e.g. as the first point of a polygon). Also pointCreated needs
	 * to be set correctly.
	 * 
	 */
	protected void setModeToFreehand() {
		if (selectedPoints.size() != 0) {
			// make sure to switch only for the first point
			return;
		}

		// defined at the beginning, because it is modified for some modes
		GeoPoint point = (GeoPoint) this.view.getHits().getFirstHit(
		        Test.GEOPOINT);
		if (point == null && this.movedGeoPoint instanceof GeoPoint) {
			point = (GeoPoint) this.movedGeoPoint;
		}

		if (this.mode == EuclidianConstants.MODE_CIRCLE_THREE_POINTS) {
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen).setExpected(ShapeType.circle);

			// the point will be deleted if no circle can be built, therefore
			// make sure that only a newly created point is set
			point = (this.pointCreated != null)
			        && movedGeoPoint instanceof GeoPoint ? (GeoPoint) movedGeoPoint
			        : null;
		} else if (this.mode == EuclidianConstants.MODE_POLYGON) {
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen).setExpected(ShapeType.polygon);
		} else if (this.mode == EuclidianConstants.MODE_RIGID_POLYGON) {
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen).setExpected(ShapeType.rigidPolygon);
		} else if (this.mode == EuclidianConstants.MODE_VECTOR_POLYGON) {
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen).setExpected(ShapeType.vectorPolygon);
		} else {
			// if the current mode is not supported
			return;
		}

		((EuclidianPenFreehand) pen).setInitialPoint(point);

		// only executed if one of the specified modes is set
		this.previousMode = this.mode;
		this.mode = EuclidianConstants.MODE_FREEHAND_SHAPE;
		moveMode = MOVE_NONE;
	}

	/**
	 * rest all the settings that have been changed in setModeToFreehand().
	 * 
	 * no effect if setModeToFreehand() has not been called or had no effect
	 * (e.g. because the selected tool is not supported)
	 */
	protected void resetModeAfterFreehand() {
		if (previousMode != -1) {
			this.mode = previousMode;
			moveMode = MOVE_NONE;
			view.setPreview(switchPreviewableForInitNewMode(this.mode));
			this.previousMode = -1;
			this.pen = null;
			this.view.repaint();
		}
	}

	private static double getDistance(GPoint p, GPoint q) {
		if (p == null || q == null) {
			return 0;
		}
		return Math.sqrt((p.x - q.x) * (p.x - q.x) + (p.y - q.y) * (p.y - q.y));
	}

}
