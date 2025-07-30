package org.geogebra.common.spreadsheet.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.junit.Test;

public class SpreadsheetStyleBarModelTests {

	@Test
	public void testStateEqualitySame() {
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
	public void testStateEqualityDifferentFontTraits() {
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
	public void testStateEqualityDifferentBackgroundColors() {
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
