package org.geogebra.common.util;

public class MatchedString {
	public final String content;
	public final int from;
	public final int to;

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

	public int getTo() {
		return to;
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
