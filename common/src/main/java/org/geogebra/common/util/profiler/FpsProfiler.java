package org.geogebra.common.util.profiler;

import org.geogebra.common.util.debug.Log;

/**
 * Measures the frames painted per second (fps).
 */
public class FpsProfiler {

	private static FpsProfiler INSTANCE;

	private static final int NOT_MEASURED_TIME_AT_BEGINNING = 1000;

	private long measureStartTime;
	private int frameCount;
	private boolean isEnabled;
	private boolean isMeasuringStarted;

	private FpsProfiler() {}

	/**
	 * @return singleton instance
	 */
	public static FpsProfiler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FpsProfiler();
		}
		return INSTANCE;
	}

	/**
	 * Enables or disables the FpsProfiler.
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
	}

	/**
	 * Starts the measuring after NOT_MEASURED_TIME_AT_BEGINNING milliseconds.
	 */
	public void notifyTouchStart() {
		if (!isEnabled) {
			return;
		}
		measureStartTime = System.currentTimeMillis() + NOT_MEASURED_TIME_AT_BEGINNING;
	}

	/**
	 * Calculates and logs the fps value.
	 */
	public void notifyTouchEnd() {
		if (!isEnabled) {
			return;
		}
		long measureEndTime = System.currentTimeMillis();
		int seconds = (int) ((measureEndTime - measureStartTime) / 1000);
		Log.debug("FPS: " + frameCount / seconds);
		reset();
	}

	private void reset() {
		frameCount = 0;
		isMeasuringStarted = false;
	}

	/**
	 * Counts the frames painted. Should be called on every repaint.
	 */
	public void notifyRepaint() {
		if (!isEnabled) {
			return;
		}
		if (isMeasuringStarted) {
			frameCount++;
			return;
		}
		if (System.currentTimeMillis() >= measureStartTime) {
			isMeasuringStarted = true;
			frameCount++;
		}
	}
}
