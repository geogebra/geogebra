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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class LineReaderTests {

	@Test
	public void testCarriageReturn() throws IOException {
		LineReader reader = new LineReader(new StringReader("abc\rdef"));
		String line1 = reader.readLine();
		assertEquals("abc", line1);
		String line2 = reader.readLine();
		assertEquals("def", line2);
		String line3 = reader.readLine();
		assertNull(line3);
	}

	@Test
	public void testCarriageReturnLineFeed() throws IOException {
		LineReader reader = new LineReader(new StringReader("abc\r\ndef"));
		String line1 = reader.readLine();
		assertEquals("abc", line1);
		String line2 = reader.readLine();
		assertEquals("def", line2);
		String line3 = reader.readLine();
		assertNull(line3);
	}

	@Test
	public void testTrailingNewlines() throws IOException {
		LineReader reader = new LineReader(new StringReader("abc\r\ndef\r\n\r\n"));
		String line1 = reader.readLine();
		assertEquals("abc", line1);
		String line2 = reader.readLine();
		assertEquals("def", line2);
		String line3 = reader.readLine();
		assertNull(line3);
	}
}
