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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Set;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.junit.jupiter.api.Test;

class SpreadsheetStyleBarModelTests {

	@Test
	void testStateEqualitySame() {
		SpreadsheetStyleBarModel.State state1 = new SpreadsheetStyleBarModel.State(
				true,
				Set.of(SpreadsheetStyling.FontTrait.BOLD),
				SpreadsheetStyling.TextAlignment.LEFT,
				null,
				null);
		SpreadsheetStyleBarModel.State state2 = new SpreadsheetStyleBarModel.State(
				true,
				Set.of(SpreadsheetStyling.FontTrait.BOLD),
				SpreadsheetStyling.TextAlignment.LEFT,
				null,
				null);
		assertEquals(state1, state2);
	}

	@Test
	void testStateEqualityDifferentFontTraits() {
		SpreadsheetStyleBarModel.State state1 = new SpreadsheetStyleBarModel.State(
				true,
				Set.of(SpreadsheetStyling.FontTrait.BOLD),
				SpreadsheetStyling.TextAlignment.LEFT,
				null,
				null);
		SpreadsheetStyleBarModel.State state2 = new SpreadsheetStyleBarModel.State(
				true,
				Set.of(SpreadsheetStyling.FontTrait.ITALIC),
				SpreadsheetStyling.TextAlignment.LEFT,
				null,
				null);
		assertNotEquals(state1, state2);
	}

	@Test
	void testStateEqualityDifferentBackgroundColors() {
		SpreadsheetStyleBarModel.State state1 = new SpreadsheetStyleBarModel.State(
				true,
				null,
				SpreadsheetStyling.TextAlignment.LEFT,
				GColor.BLUE,
				null);
		SpreadsheetStyleBarModel.State state2 = new SpreadsheetStyleBarModel.State(
				true,
				null,
				SpreadsheetStyling.TextAlignment.LEFT,
				GColor.RED,
				null);
		assertNotEquals(state1, state2);
	}
}
