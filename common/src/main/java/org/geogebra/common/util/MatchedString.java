package org.geogebra.common.util;

public class MatchedString {
	public final String content;
	public final int from;

	// neccessary for Objective-C
	public String getContent() {
		return content;
	}

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
