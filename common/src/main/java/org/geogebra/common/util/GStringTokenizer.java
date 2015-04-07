package org.geogebra.common.util;

public class GStringTokenizer {

	// points to last position of split + 1
	private int cursor = 0;
	private String st;
	private char split;

	public GStringTokenizer(String st, char split) {
		this.st = st;
		this.split = split;
	}

	public boolean hasMoreTokens() {
		return cursor < st.length();
	}

	public String nextToken() {
		int oldCursor = cursor;
		cursor = st.indexOf(split, cursor) + 1;
		if (cursor == 0) {
			cursor = st.length();
		}
		return st.substring(oldCursor, cursor - 1);
	}

}
