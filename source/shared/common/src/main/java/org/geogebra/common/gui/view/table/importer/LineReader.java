/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
