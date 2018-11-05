package org.geogebra.common.kernel.geos;

/**
 * String builder wrapper for screen reader; avoids double spaces and dots.
 * 
 * @author Zbynek
 */
public class ScreenReaderBuilder {
	private StringBuilder sb = new StringBuilder();

	/**
	 * Append string, make sure . is followed by space.
	 * 
	 * @param o
	 *            string to be appended
	 */
	public void append(String o) {
		if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '.') {
			sb.append(" "); // ad space after each dot
		}
		sb.append(o);
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	/**
	 * Append space, avoid double space.
	 */
	public void appendSpace() {
		if (sb.length() > 1 && sb.charAt(sb.length() - 1) != ' ') {
			sb.append(" ");
		}
	}

	/**
	 * Remove trailing space and append dot; avoid double dots.
	 */
	public void appendDot() {
		if (sb.length() > 0) {
			int idx = sb.length() - 1;
			if (sb.charAt(idx) == ' ') {
				sb.setLength(idx--);
			}
			if (idx > 0 && sb.charAt(idx) != '.') {
				sb.append(".");
			}
		}
	}
}
