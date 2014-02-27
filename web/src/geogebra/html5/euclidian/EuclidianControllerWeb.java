package geogebra.html5.euclidian;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.main.App;
import geogebra.common.util.MyMath;
import geogebra.html5.event.PointerEvent;
import geogebra.web.euclidian.event.ZeroOffset;

public abstract class EuclidianControllerWeb extends EuclidianController {

	/**
	 * threshold for moving in case of a multitouch-event (pixel)
	 */
	protected final static int MIN_MOVE = 5;

	/**
	 * true if the actual multitouch-zoom is a scaling of the axes
	 */
	protected boolean zoomX = false, zoomY = false;

	/**
	 * actual scale of the axes (has to be saved during multitouch)
	 */
	protected double scale;

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
		Hits hits1 = view.getHits();
		view.setHits(new GPoint((int) x2, (int) y2), PointerEventType.TOUCH);
		Hits hits2 = view.getHits();

		this.zoomY = hits1.hasYAxis() && hits2.hasYAxis();
		this.zoomX = hits1.hasXAxis() && hits2.hasXAxis();

		oldCenterX = (int) (x1 + x2) / 2;
		oldCenterY = (int) (y1 + y2) / 2;

		if (this.zoomY) {
			this.oldDistance = y1 - y2;
			this.scale = this.view.getYscale();
		} else if (this.zoomX) {
			this.oldDistance = x1 - x2;
			this.scale = this.view.getXscale();
		} else {
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

		if (this.zoomY) {
			double newRatio = this.scale * (y1 - y2) / this.oldDistance;
			this.view.setCoordSystem(this.view.getXZero(),
			        this.view.getYZero(), this.view.getXscale(), newRatio);
		} else if (this.zoomX) {
			double newRatio = this.scale * (x1 - x2) / this.oldDistance;
			this.view.setCoordSystem(this.view.getXZero(),
			        this.view.getYZero(), newRatio, this.view.getYscale());
		} else {
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
