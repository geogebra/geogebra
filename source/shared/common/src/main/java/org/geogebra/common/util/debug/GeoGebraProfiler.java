package org.geogebra.common.util.debug;

/**
 * Use GeoGebraProfiler's add methods to add the time it took to finish
 * an operation, such as a repaint or a cascade update.
 * The data can then be displayed using the print methods
 */
public class GeoGebraProfiler {

	private static volatile int repaints;
	private static volatile long repaintTime;
	private static volatile int drags;
	private static volatile long dragTime;

	private static volatile int algebra;
	private static volatile int event;
	private static volatile int cascades;

	private static volatile long algebraTime;
	private static volatile long eventTime;
	private static volatile long cascadeTime;

	private static final Object lock = new Object();

	/**
	 * Display performance data about drags and repaints
	 */
	public static void printDragMeasurementData() {
		if (drags > 0) {
			Log.debug("Profile Dragging: \nNumber of handled drag events: " + drags + "\n"
					+ "Average duration of one drag event: "
					+ ((float) dragTime / (float) drags) + " ms \n" + "Number of repaints: "
					+ repaints + "\n" + "Average duration of one repaint: "
					+ ((float) repaintTime / repaints) + " ms");
		}
	}

	/**
	 * Display performance data about algebra view updates
	 */
	public static void printAlgebraMeasurementData() {
		if (algebra > 0) {
			Log.debug("Profile Algebra: " + algebra + " x "
					+ (algebraTime / algebra) + " = " + algebraTime);
		}
	}

	/**
	 * Display performance data about event dispatches
	 */
	public static void printEventMeasurementData() {
		if (event > 0) {
			Log.debug("Profile EventDispatcher: " + event + " x "
					+ (eventTime / event) + " = " + eventTime);
		}
	}

	/**
	 * Display performance data about cascade update
	 */
	public static void printCascadeMeasurementData() {
		if (cascades > 0) {
			Log.debug("Profile Cascades: " + cascades + " x "
					+ (cascadeTime / cascades) + " = " + cascadeTime);
		}
	}

	/**
	 * Log a repaint
	 * 
	 * @param time
	 *            repaint duration
	 */
	public static void addRepaint(long time) {
		synchronized (lock) {
			repaints++;
			repaintTime += time;
		}
	}

	/**
	 * @param time
	 *            cascade duration
	 */
	public static void addUpdateCascade(long time) {
		synchronized (lock) {
			cascades++;
			cascadeTime += time;
		}
	}

	/**
	 * @param time
	 *            algebra update duration
	 */
	public static void addAlgebra(long time) {
		synchronized (lock) {
			algebra++;
			algebraTime += time;
		}
	}

	/**
	 * @param time
	 *            event handling duration
	 */
	public static void addEvent(long time) {
		synchronized (lock) {
			event++;
			eventTime += time;
		}
	}

	/**
	 * @param time
	 *            drag duration
	 */
	public static void addDrag(long time) {
		synchronized (lock) {
			drags++;
			dragTime += time;
		}
	}
}
