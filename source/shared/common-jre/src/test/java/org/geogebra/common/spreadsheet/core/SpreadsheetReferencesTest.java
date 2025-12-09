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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.jre.util.UtilFactoryJre;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SpreadsheetReferencesTest {

	@BeforeAll
	public static void setup() {
		UtilFactoryJre.setupRegexFactory();
	}

	@Test
	public void testDeduplicate() {
		SpreadsheetReference range1 =
				SpreadsheetReferenceParsing.parseReference("A1:A3");
		SpreadsheetReference range2 =
				SpreadsheetReferenceParsing.parseReference("$A$1:$A$3");
		SpreadsheetReference range3 =
				SpreadsheetReferenceParsing.parseReference("$A1:$A3");
		SpreadsheetReferences references =
				new SpreadsheetReferences(List.of(range1, range2, range3), range2);
		SpreadsheetReferences deduplicated = references.removingDuplicates();
		assertEquals(List.of(range1), deduplicated.cellReferences);
		assertTrue(deduplicated.currentCellReference.equalsIgnoringAbsolute(range3));
	}

	@Test
	public void testDontDeduplicate() {
		SpreadsheetReference range1 =
				SpreadsheetReferenceParsing.parseReference("A1:A3");
		SpreadsheetReference range2 =
				SpreadsheetReferenceParsing.parseReference("A1");
		SpreadsheetReferences references =
				new SpreadsheetReferences(List.of(range1, range2), range2);
		SpreadsheetReferences deduplicated = references.removingDuplicates();
		assertEquals(references.cellReferences, deduplicated.cellReferences);
	}

	@Test
	public void testEqualsIgnoringAbsolute() {
		SpreadsheetReference cell1 =
				SpreadsheetReferenceParsing.parseReference("A1");
		SpreadsheetReference range1 =
				SpreadsheetReferenceParsing.parseReference("A1:A3");
		assertFalse(cell1.equalsIgnoringAbsolute(range1));
	}
}
