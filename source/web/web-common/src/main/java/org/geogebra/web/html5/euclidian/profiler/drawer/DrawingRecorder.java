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
