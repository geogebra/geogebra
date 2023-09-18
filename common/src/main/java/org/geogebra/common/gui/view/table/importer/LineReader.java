package org.geogebra.common.gui.view.table.importer;

import java.io.IOException;
import java.io.Reader;

final class LineReader {

	private Reader reader;
	private int nextChar = -1;

	LineReader(Reader reader) {
		this.reader = reader;
	}

	String readLine() throws IOException {
		StringBuilder sb = new StringBuilder();
		if (nextChar != -1) {
			sb.append((char) nextChar);
			nextChar = -1;
		}
		int ch;
		while ((ch = reader.read()) != -1) {
			if (ch == '\r' || ch == '\n') {
				nextChar = reader.read();
				if (nextChar == '\n') { // \r followed by \n?
					nextChar = -1; // swallow \n
				}
				break;
			}
			sb.append((char) ch);
		}
		return sb.length() == 0 ? null : sb.toString();
	}
}
