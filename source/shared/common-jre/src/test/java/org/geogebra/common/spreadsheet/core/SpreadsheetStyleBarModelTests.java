package org.geogebra.common.spreadsheet.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyle;
import org.junit.Test;

public class SpreadsheetStyleBarModelTests {

	@Test
	public void testStateEqualitySame() {
		SpreadsheetStyleBarModel.State state1 = new SpreadsheetStyleBarModel.State(
				true,
				Set.of(SpreadsheetStyle.FontTrait.BOLD),
				SpreadsheetStyle.TextAlignment.LEFT,
				null,
				null);
		SpreadsheetStyleBarModel.State state2 = new SpreadsheetStyleBarModel.State(
				true,
				Set.of(SpreadsheetStyle.FontTrait.BOLD),
				SpreadsheetStyle.TextAlignment.LEFT,
				null,
				null);
		assertTrue(state1.equals(state2));
	}

	@Test
	public void testStateEqualityDifferentFontTraits() {
		SpreadsheetStyleBarModel.State state1 = new SpreadsheetStyleBarModel.State(
				true,
				Set.of(SpreadsheetStyle.FontTrait.BOLD),
				SpreadsheetStyle.TextAlignment.LEFT,
				null,
				null);
		SpreadsheetStyleBarModel.State state2 = new SpreadsheetStyleBarModel.State(
				true,
				Set.of(SpreadsheetStyle.FontTrait.ITALIC),
				SpreadsheetStyle.TextAlignment.LEFT,
				null,
				null);
		assertFalse(state1.equals(state2));
	}

	@Test
	public void testStateEqualityDifferentBackgroundColors() {
		SpreadsheetStyleBarModel.State state1 = new SpreadsheetStyleBarModel.State(
				true,
				null,
				SpreadsheetStyle.TextAlignment.LEFT,
				GColor.BLUE,
				null);
		SpreadsheetStyleBarModel.State state2 = new SpreadsheetStyleBarModel.State(
				true,
				null,
				SpreadsheetStyle.TextAlignment.LEFT,
				GColor.RED,
				null);
		assertFalse(state1.equals(state2));
	}
}
