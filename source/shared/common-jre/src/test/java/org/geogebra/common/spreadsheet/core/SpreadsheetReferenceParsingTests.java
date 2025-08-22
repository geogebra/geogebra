package org.geogebra.common.spreadsheet.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.jre.util.UtilFactoryJre;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SpreadsheetReferenceParsingTests {

	@BeforeAll
	public static void setup() {
		UtilFactoryJre.setupRegexFactory();
	}

	@Test
	public void testParseCellReferences() {
		assertEquals(new SpreadsheetReference(
				new SpreadsheetCellReference(0, 0), null),
				SpreadsheetReferenceParsing.parseReference("A1"));
		assertEquals(new SpreadsheetReference(
						new SpreadsheetCellReference(0, false, 0, true), null),
				SpreadsheetReferenceParsing.parseReference("$A1"));
		assertEquals(new SpreadsheetReference(
						new SpreadsheetCellReference(0, true, 0, false), null),
				SpreadsheetReferenceParsing.parseReference("A$1"));
		assertEquals(new SpreadsheetReference(
						new SpreadsheetCellReference(0, true, 0, true), null),
				SpreadsheetReferenceParsing.parseReference("$A$1"));
		assertEquals(new SpreadsheetReference(
						new SpreadsheetCellReference(0, true, 0, true), null),
				SpreadsheetReferenceParsing.parseReference("ASDF $A$1"));
		assertEquals(new SpreadsheetReference(
				new SpreadsheetCellReference(1, 1), null),
				SpreadsheetReferenceParsing.parseReference("B2"));
		assertEquals(new SpreadsheetReference(
				new SpreadsheetCellReference(1, 26),
				new SpreadsheetCellReference(9, 26)),
				SpreadsheetReferenceParsing.parseReference("AA2:AA10"));
		assertNull(SpreadsheetReferenceParsing.parseReference("A-B"));
		assertNull(SpreadsheetReferenceParsing.parseReference("A1B3"));
		assertNull(SpreadsheetReferenceParsing.parseReference("ASDF$A1"));
		assertNull(SpreadsheetReferenceParsing.parseReference("A111111111111111111111"));
		assertNull(SpreadsheetReferenceParsing.parseReference("AAAAAACNMKRDJ1"));
	}
}
