package org.geogebra.common.util;

public class MatchedString {
	public final String content;
	public final int from;

	/**
	 * Note: This is neccessary for Objective-C.
	 * @return content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Note: This is neccessary for Objective-C.
	 * @return from
	 */
	public int getFrom() {
		return from;
	}

	/**
	 * @param content text content
	 * @param from match offset
	 */
	public MatchedString(String content, int from) {
		this.content = content;
		this.from = from;
	}
}
