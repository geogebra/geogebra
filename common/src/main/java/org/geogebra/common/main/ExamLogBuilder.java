package org.geogebra.common.main;

/**
 * Builds formatted exam log; default implementation is just a wrapper for
 * StringBuilder
 */
public class ExamLogBuilder {
	private StringBuilder sb = new StringBuilder();

	/**
	 * Add separator between header and events
	 */
	public void addHR() {
		// only needed in web
	}

	/**
	 * Append a line to the log.
	 * 
	 * @param line
	 *            log line
	 */
	public void addLine(StringBuilder line) {
		sb.append(line);
		sb.append('\n');
	}

	@Override
	public String toString() {
		return sb.toString();
	}

}
