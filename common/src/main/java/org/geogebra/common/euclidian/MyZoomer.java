package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.Kernel;

/**
 * AbstractZoomer is responsible for animated zooming of Euclidian View
 *
 */
public abstract class MyZoomer {
	private enum ZoomerMode {
		ZOOM, ZOOM_RW, AXES, MOVE
	}

	private final EuclidianView view;

	private static final int MAX_STEPS = 15; // frames

	/**
	 * Tick of the timer
	 */
	public static final int DELAY = 10;

	private static final int MAX_TIME = 400; // millis

	private ZoomerMode mode;

	private double px, py; // zoom point

	private double factor;

	private int counter, steps;

	private double oldScale, newScale, add, dx, dy;

	private double x0, x1, y0, y1, xminOld, yminOld, ymaxOld, xmaxOld;

	private long startTime;

	private boolean storeUndo;

	/**
	 * Creates new zoomer
	 * 
	 * @param view
	 *            view
	 */
	public MyZoomer(EuclidianView view) {
		this.view = view;
	}

	/**
	 * Init this for axis ratio zooming
	 * 
	 * @param ratio
	 *            y-zoom
	 * @param doStoreUndo
	 *            true to store undo
	 */
	public void init(double ratio, boolean doStoreUndo) {
		// this.ratio = ratio;
		this.storeUndo = doStoreUndo;

		// zoomFactor = ratio / scaleRatio;
		oldScale = view.getYscale();
		newScale = view.getXscale() * ratio; // new yscale
		this.steps = MAX_STEPS;
		mode = ZoomerMode.AXES;
	}

	/**
	 * Init this for normal zoom
	 * 
	 * @param ptx
	 *            center x-coord
	 * @param pty
	 *            center y-coord
	 * @param zoomFactor
	 *            zoom factor
	 * @param noOfSteps
	 *            number of steps
	 * @param doStoreUndo
	 *            true to store undo info
	 */
	public void init(double ptx, double pty, double zoomFactor, int noOfSteps,
			boolean doStoreUndo) {
		this.px = ptx;
		this.py = pty;
		// this.zoomFactor = zoomFactor;
		this.storeUndo = doStoreUndo;

		oldScale = view.getXscale();
		newScale = view.getXscale() * zoomFactor;

		this.steps = Math.min(MAX_STEPS, noOfSteps);
		mode = ZoomerMode.ZOOM;
	}

	/**
	 * @param rwx0
	 *            left x
	 * @param rwx1
	 *            right x
	 * @param rwy0
	 *            bottom y
	 * @param rwy1
	 *            top y
	 * @param noOfSteps
	 *            number of steps
	 * @param doStoreUndo
	 *            true to store undo info
	 */
	public void initRW(double rwx0, double rwx1, double rwy0, double rwy1,
			int noOfSteps, boolean doStoreUndo) {
		this.x0 = rwx0;
		this.x1 = rwx1;
		this.y0 = rwy0;
		this.y1 = rwy1;

		xminOld = view.getXmin();
		xmaxOld = view.getXmax();
		yminOld = view.getYmin();
		ymaxOld = view.getYmax();
		// this.zoomFactor = zoomFactor;
		this.storeUndo = doStoreUndo;

		this.steps = Math.min(MAX_STEPS, noOfSteps);
		mode = ZoomerMode.ZOOM_RW;
	}

	/**
	 * @param ox
	 *            x translation (pixels)
	 * @param oy
	 *            y translation (pixels)
	 * @param doStoreUndo
	 *            true to store undo info
	 */
	public void init(double ox, double oy, boolean doStoreUndo) {
		this.px = ox;
		this.py = oy;
		this.storeUndo = doStoreUndo;
		mode = ZoomerMode.MOVE;
		this.steps = MAX_STEPS;
	}

	/**
	 * Perform one zoom step
	 */
	protected void step() {
		counter++;
		long time = System.currentTimeMillis() - startTime;
		if ((counter == steps) || (time > MAX_TIME)) { // end of animation
			stopAnimation();
		} else {
			switch (mode) {
			case AXES:
				factor = 1.0 + ((counter * add) / oldScale);
				view.setCoordSystem(view.getxZero(), view.getyZero(),
						view.getXscale(), oldScale * factor);
				break;
			case ZOOM:
				factor = 1.0 + ((counter * add) / oldScale);
				view.setCoordSystem(px + (dx * factor), py + (dy * factor),
						oldScale * factor,
						oldScale * factor * view.getScaleRatio());
				break;
			case ZOOM_RW:
				double i = counter;
				double j = steps - counter;
				view.setRealWorldCoordSystem(
						((x0 * i) + (xminOld * j)) / steps,
						((x1 * i) + (xmaxOld * j)) / steps,
						((y0 * i) + (yminOld * j)) / steps,
						((y1 * i) + (ymaxOld * j)) / steps);
				break;
			case MOVE:
				factor = 1.0 - (counter * add);
				view.setCoordSystem(px + (dx * factor), py + (dy * factor),
						view.getXscale(), view.getYscale());
			}
		}
	}

	private synchronized void stopAnimation() {
		stopTimer();
		// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
		switch (mode) {
		case AXES:
			view.setCoordSystem(view.getxZero(), view.getyZero(),
					view.getXscale(), newScale);
			break;
		case ZOOM:
			factor = newScale / oldScale;
			view.setCoordSystem(px + (dx * factor), py + (dy * factor),
					newScale, newScale * view.getScaleRatio());
			break;
		case ZOOM_RW:
			view.setRealWorldCoordSystem(x0, x1, y0, y1);
			break;
		case MOVE:
			view.setCoordSystem(px, py, view.getXscale(), view.getYscale());
			break;
		}
		if (setStandard) {
			setStandard = false;
			view.setAnimatedCoordSystem(standardX, standardY, 0,
					EuclidianView.SCALE_STANDARD, MAX_STEPS, storeUndo);
		}
		if (storeUndo) {
			view.getApplication().storeUndoInfo();
		}
	}

	private boolean setStandard = false;
	private double standardX, standardY;

	/**
	 * Chain current zoom with resetting standard view
	 * 
	 * @param xzero
	 *            standard xzero
	 * @param yzero
	 *            standard yzero
	 */
	public void setStandardViewAfter(double xzero, double yzero) {
		setStandard = true;
		standardX = xzero;
		standardY = yzero;

	}

	/**
	 * Starts the animation
	 */
	public synchronized void startAnimation() {
		if (!hasTimer()) {
			return;
		}
		switch (mode) {
		case AXES:
			add = (newScale - oldScale) / steps;
			break;
		case ZOOM:
			add = (newScale - oldScale) / steps;
			dx = view.getxZero() - px;
			dy = view.getyZero() - py;
			break;
		case MOVE:
			dx = view.getxZero() - px;
			dy = view.getyZero() - py;
			if (Kernel.isZero(dx) && Kernel.isZero(dy)) {
				return;
			}
			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = 1.0 / MAX_STEPS;
			break;
		case ZOOM_RW:
			break;
		}
		counter = 0;

		startTime = System.currentTimeMillis();
		startTimer();
	}

	/** stop timer */
	protected abstract void stopTimer();

	/** start timer */
	protected abstract void startTimer();

	/** @return true if timer is defined */
	protected abstract boolean hasTimer();

}
