package org.geogebra.common.main.exam;

/**
 * Builds formatted exam log; default implementation is just a wrapper for
 * StringBuilder
 */
@Deprecated // use ExamSummary instead
public class ExamLogBuilder {
	private StringBuilder sb = new StringBuilder();

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

	/**
	 * @param description
	 *            field name
	 * @param value
	 *            field value
	 */
	public void addField(String description, String value) {
		addLine(new StringBuilder(description).append(": ").append(value));
	}

}
