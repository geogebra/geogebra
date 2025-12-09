/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.util.profiler;

import org.geogebra.common.util.debug.Log;

public abstract class FpsProfiler {

	private static final int NOT_MEASURED_TIME_AT_BEGINNING = 64;
	private static final int SECOND = 1000;

	private long startTime;
	private int frameCount;

	private long startTimeForSecond;
	private int frameCountForSecond;

	private int minFps = Integer.MAX_VALUE;
	private int maxFps = -1;
	private boolean isEnabled;

	/**
	 * Enables or disables the FpsProfiler.
	 * @param enabled Whether the fps measuring should be enabled or disabled.
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
		startTime = now() + NOT_MEASURED_TIME_AT_BEGINNING;
		startTimeForSecond = startTime;
	}

	protected abstract long now();

	/**
	 * Calculates and logs the fps value.
	 */
	public void notifyTouchEnd() {
		if (!isEnabled) {
			return;
		}
		logIfMeasurable();
		reset();
	}

	private void logIfMeasurable() {
		long endTime = now();
		if (endTime > startTime) {
			log(endTime);
		}
	}

	private void log(long endTime) {
		double seconds = (double) (endTime - startTime) / SECOND;
		Log.debug("\n\n" + getAverageFpsText(seconds) + getMinMaxFpsText() + "\n\n");
	}

	private String getAverageFpsText(double seconds) {
		int averageFps = (int) (frameCount / seconds);
		return "Average FPS: " + averageFps;
	}

	private String getMinMaxFpsText() {
		if (maxFps >= 0) {
			return "\nMin FPS: " + minFps
					+ "\nMax FPS: " + maxFps;
		} else {
			return "";
		}
	}

	private void reset() {
		frameCount = 0;
		frameCountForSecond = 0;
		minFps = Integer.MAX_VALUE;
		maxFps = -1;
	}

	/**
	 * Counts the frames painted. Should be called on every repaint.
	 */
	public void notifyRepaint() {
		if (!isEnabled) {
			return;
		}
		long now = now();
		if (startTime <= now) {
			frameCount++;
			measureFpsForSecond(now);
		}
	}

	private void measureFpsForSecond(long now) {
		frameCountForSecond++;
		if (startTimeForSecond <= now - SECOND) {
			double seconds = (double) (now - startTimeForSecond) / SECOND;
			int fps = (int) (frameCountForSecond / seconds);
			if (minFps > fps) {
				minFps = fps;
			}
			if (maxFps < fps) {
				maxFps = fps;
			}
			frameCountForSecond = 0;
			startTimeForSecond = now;
		}
	}
}
