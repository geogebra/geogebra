package org.geogebra.common.util;

public class MatchedString {
	public final String content;
	public final int from;
	public final int to;

	/**
	 * Note: This is necessary for Objective-C.
	 * @return content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Note: This is necessary for Objective-C.
	 * @return from
	 */
	public int getFrom() {
		return from;
	}

	/**
	 * Note: This is necessary for Objective-C.
	 * @return to
	 */
	public int getTo() {
		return to;
	}

	/**
	 * @param content text content
	 * @param from index of first matched character
	 * @param to index of last matched character + 1
	 */
	public MatchedString(String content, int from, int to) {
		this.content = content;
		this.from = from;
		this.to = to;
	}

	/**
	 * @return [prefix, highlighted part, suffix]
	 */
	public String[] getParts() {
		return new String[] {content.substring(0, from),
				content.substring(from, to),
				content.substring(to)};
	}
}
