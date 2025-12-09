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

package org.geogebra.web.html5.euclidian.profiler.drawer;

/**
 * Records the drawing into a json string.
 */
public class DrawingRecorder {

	private StringBuilder events;

	/**
	 * Constructor
	 */
	public DrawingRecorder() {
		events = new StringBuilder();
	}

	/**
	 * Saves the coordinate into the json string.
	 * @param x x
	 * @param y y
	 * @param time time
	 */
	public void recordCoordinate(int x, int y, long time) {
		events
				.append("{\"x\":").append(x)
				.append(", \"y\":").append(y)
				.append(", \"time\":").append(time)
				.append("},\n");
	}

	/**
	 * Saves the touchEnd event into the json string.
	 */
	public void recordTouchEnd() {
		events.append("{\"touchEnd\":1},\n");
	}

	/**
	 * Empties the recordings.
	 */
	public void reset() {
		events.setLength(0);
	}

	@Override
	public String toString() {
		String eventsWithoutLastComma =
				events.length() > 2 ? events.substring(0, events.length() - 2) : "";
		return "{\"coords\":[" + eventsWithoutLastComma + "]}";
	}
}
