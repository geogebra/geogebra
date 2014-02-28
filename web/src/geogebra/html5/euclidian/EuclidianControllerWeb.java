package geogebra.html5.euclidian;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.util.MyMath;
import geogebra.html5.event.PointerEvent;
import geogebra.web.euclidian.event.ZeroOffset;

import java.util.ArrayList;

public abstract class EuclidianControllerWeb extends EuclidianController {

	/**
	 * different modes of a multitouch-event
	 */
	protected enum scaleMode {
		zoomX, zoomY, circle3Points, circle2Points, view;
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
		                .size() == 3 || ((GeoConic) hits1.get(0))
		                .getFreeInputPoints(this.view).size() == 2)) {

			if (((GeoConic) hits1.get(0)).getFreeInputPoints(this.view).size() == 3) {
				this.multitouchMode = scaleMode.circle3Points;
			} else {
				this.multitouchMode = scaleMode.circle2Points;
			}
			super.twoTouchStart(x1, y1, x2, y2);
			this.scaleConic = (GeoConic) hits1.get(0); // TODO: select
			                                           // scaleConic

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
			touchStart(oldCenterX, oldCenterY);
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
		default:
			// pinch
			super.twoTouchMove(x1, y1, x2, y2);

			int centerX = (x1 + x2) / 2;
			int centerY = (y1 + y2) / 2;

			if (MyMath.length(oldCenterX - centerX, oldCenterY - centerY) > MIN_MOVE) {
				int m = this.mode;
				this.mode = EuclidianConstants.MODE_TRANSLATEVIEW;
				boolean allowed = moveAxesAllowed; // in case it was changed
				moveAxesAllowed = false;

				touchStart(oldCenterX, oldCenterY);
				touchMove(centerX, centerY);

				mode = m;
				moveAxesAllowed = allowed;

				oldCenterX = centerX;
				oldCenterY = centerY;
			}
		}
	}

	@Override
	protected boolean moveAxesPossible() {
		return super.moveAxesPossible() && this.moveAxesAllowed;
	}

	/**
	 * simulates a TouchStartEvent
	 * 
	 * @param x
	 *            x-coordinate of the simulated event
	 * @param y
	 *            y-coordinate of the simulated event
	 */
	protected void touchStart(int x, int y) {
		super.mouseLoc = new GPoint(x, y);
		switchModeForMousePressed(new PointerEvent(x, y,
		        PointerEventType.TOUCH, new ZeroOffset()));
	}

	/**
	 * simulates a TouchMoveEvent
	 * 
	 * @param x
	 *            x-coordinate of the simulated event
	 * @param y
	 *            y-coordinate of the simulated event
	 */
	protected void touchMove(int x, int y) {
		wrapMouseDragged(new PointerEvent(x, y, PointerEventType.TOUCH,
		        new ZeroOffset()));
	}
}
