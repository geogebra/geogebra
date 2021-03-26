package org.geogebra.common.euclidian;

import org.geogebra.common.util.DoubleUtil;

/**
 * AbstractZoomer is responsible for animated zooming of Euclidian View
 *
 */
public abstract class CoordSystemAnimation {

	private enum AnimationMode {
		ZOOM, ZOOM_RW, AXES_X, AXES_Y, MOVE
	}

	private static final int MAX_STEPS = 15; // frames

	/**
	 * Tick of the timer
	 */
	public static final int DELAY = 10;

	private static final int MAX_TIME = 400; // millis

	private final EuclidianView view;
	private CoordSystemInfo coordSystemInfo;
	private AnimationMode mode;

	private double px; // zoom point
	private double py;

	private double factor;

	private int counter;
	private int steps;

	private double oldScale;
	private double newScale;
	private double add;
	private double dx;
	private double dy;

	private double x0;
	private double x1;
	private double y0;
	private double y1;
	private double xminOld;
	private double yminOld;
	private double ymaxOld;
	private double xmaxOld;

	private long startTime;

	private boolean storeUndo;

	private boolean setStandard = false;
	private double standardX;
	private double standardY;
	private boolean axisZoom = false;

	/**
	 * Creates new zoomer
	 *
	 * @param view
	 *            view
	 */
	public CoordSystemAnimation(EuclidianView view) {
		this.view = view;
		this.coordSystemInfo = view.getCoordSystemInfo();
	}

	/**
	 * Init this for axis ratio zooming. After zoom the axis ratio will be
	 * ratioX:ratioY, zooming is done along x-axis if ratioY is 1 and ratioX !=
	 * 1
	 * 
	 * @param ratioX
	 *            axis ratio numerator
	 * @param ratioY
	 *            axis ratio denominator
	 * @param doStoreUndo
	 *            true to store undo
	 */
	public synchronized void initAxes(double ratioX, double ratioY, boolean doStoreUndo) {
		// this.ratio = ratio;
		this.storeUndo = doStoreUndo;
		this.steps = MAX_STEPS;

		// check ratioY so that SetAxesRatio(1,1) keeps old behaviour
		if (ratioY == 1 && ratioX != 1) {

			oldScale = view.getXscale();
			newScale = view.getYscale() / ratioX; // new xscale
			mode = AnimationMode.AXES_X;
		} else {

			oldScale = view.getYscale();
			newScale = view.getXscale() * ratioX / ratioY; // new yscale
			mode = AnimationMode.AXES_Y;
		}
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
	public synchronized void init(double ptx, double pty, double zoomFactor, int noOfSteps,
			boolean doStoreUndo) {
		this.px = ptx;
		this.py = pty;
		this.storeUndo = doStoreUndo;

		oldScale = view.getXscale();
		newScale = view.getXscale() * zoomFactor;

		this.steps = Math.min(MAX_STEPS, noOfSteps);
		mode = AnimationMode.ZOOM;
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
	public synchronized void initRW(double rwx0, double rwx1, double rwy0,
			double rwy1, int noOfSteps, boolean doStoreUndo) {
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
		mode = AnimationMode.ZOOM_RW;
	}

	/**
	 * @param ox
	 *            x translation (pixels)
	 * @param oy
	 *            y translation (pixels)
	 * @param doStoreUndo
	 *            true to store undo info
	 */
	public synchronized void init(double ox, double oy, boolean doStoreUndo) {
		this.px = ox;
		this.py = oy;
		this.storeUndo = doStoreUndo;
		mode = AnimationMode.MOVE;
		this.steps = MAX_STEPS;
	}

	/**
	 * Perform one zoom step
	 */
	protected synchronized void step() {
		counter++;
		long time = System.currentTimeMillis() - startTime;
		if ((counter == steps) || (time > MAX_TIME)) { // end of animation
			stopAnimation();
		} else {
			coordSystemInfo.setInteractive(true);
			switch (mode) {
			case AXES_X:
				factor = 1.0 + ((counter * add) / oldScale);
				view.setCoordSystem(view.getXZero(), view.getYZero(),
						oldScale * factor, view.getYscale());
				coordSystemInfo.setXAxisZoom(true);
				break;
			case AXES_Y:
				factor = 1.0 + ((counter * add) / oldScale);
				view.setCoordSystem(view.getXZero(), view.getYZero(),
						view.getXscale(), oldScale * factor);
				coordSystemInfo.setXAxisZoom(false);
			break;
			case ZOOM:
				factor = 1.0 + ((counter * add) / oldScale);
				view.setCoordSystem(px + (dx * factor), py + (dy * factor),
						oldScale * factor,
						oldScale * factor * view.getScaleRatio());
				coordSystemInfo.setXAxisZoom(false);
				break;
			case ZOOM_RW:
				double i = counter;
				double j = steps - counter;
				view.setRealWorldCoordSystem(((x0 * i) + (xminOld * j)) / steps,
						((x1 * i) + (xmaxOld * j)) / steps,
						((y0 * i) + (yminOld * j)) / steps,
						((y1 * i) + (ymaxOld * j)) / steps);
				coordSystemInfo.setXAxisZoom(false);
				break;
			case MOVE:
				factor = 1.0 - (counter * add);
				view.setCoordSystem(px + (dx * factor), py + (dy * factor),
						view.getXscale(), view.getYscale());
				coordSystemInfo.setXAxisZoom(false);
			}
		}
	}

	private synchronized void stopAnimation() {
		stopTimer();
		// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
		EuclidianController controller = view.getEuclidianController();
		switch (mode) {
		case AXES_X:
			view.setCoordSystem(view.getXZero(), view.getYZero(), newScale,
					view.getYscale());
			onAxisZoomEnd(controller);
			break;
		case AXES_Y:
			view.setCoordSystem(view.getXZero(), view.getYZero(),
					view.getXscale(), newScale);
			onAxisZoomEnd(controller);
			break;
		case ZOOM:
			factor = newScale / oldScale;
			view.setCoordSystem(px + (dx * factor), py + (dy * factor),
					newScale, newScale * view.getScaleRatio());
			controller.notifyZoomerStopped();
			break;
		case ZOOM_RW:
			view.setRealWorldCoordSystem(x0, x1, y0, y1);
			controller.notifyZoomerStopped();
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
		axisZoom = false;
		controller.notifyCoordSystemListeners();
	}

	private void onAxisZoomEnd(EuclidianController controller) {
		coordSystemInfo.setXAxisZoom(false);
		controller.notifyZoomerStopped();
	}

	/**
	 * Chain current zoom with resetting standard view
	 * 
	 * @param xzero
	 *            standard xzero
	 * @param yzero
	 *            standard yzero
	 */
	public synchronized void setStandardViewAfter(double xzero, double yzero) {
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
		case AXES_X:
		case AXES_Y:
			add = (newScale - oldScale) / steps;
			coordSystemInfo.setXAxisZoom(true);
			break;
		case ZOOM:
			add = (newScale - oldScale) / steps;
			dx = view.getXZero() - px;
			dy = view.getYZero() - py;
			break;
		case MOVE:
			dx = view.getXZero() - px;
			dy = view.getYZero() - py;
			if (DoubleUtil.isZero(dx) && DoubleUtil.isZero(dy)) {
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
		view.getEuclidianController().notifyCoordSystemListeners();
	}

	/** stop timer */
	protected abstract void stopTimer();

	/** start timer */
	protected abstract void startTimer();

	/** @return true if timer is defined */
	protected abstract boolean hasTimer();

	/**
	 * @return true if there is a standard (i.e. no) zoom.
	 */
	public boolean isStandardZoom() {
		return DoubleUtil.checkInteger(newScale) == EuclidianView.SCALE_STANDARD;
	}

	/**
	 *
	 * @return if axis has zoomed.
	 */
	public boolean isAxisZoom() {
		return axisZoom;
	}
}
