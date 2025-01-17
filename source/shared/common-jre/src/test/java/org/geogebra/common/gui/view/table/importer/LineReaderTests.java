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
