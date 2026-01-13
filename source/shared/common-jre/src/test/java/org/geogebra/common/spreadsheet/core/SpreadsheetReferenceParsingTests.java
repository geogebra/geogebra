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

package org.geogebra.common.spreadsheet.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.jre.util.UtilFactoryJre;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
		assertEquals(new SpreadsheetReference(
						new SpreadsheetCellReference(1, 1), null),
				SpreadsheetReferenceParsing.parseReference("B2:"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"A-B", "A1B3", "ASDF$A1", "A111111111111111111111", "AAAAAACNMKRDJ1",
			"", ":", ":A1", "::"
	})
	public void invalidReferenceParsingTest(String ref) {
		assertNull(SpreadsheetReferenceParsing.parseReference(ref));
	}
}
