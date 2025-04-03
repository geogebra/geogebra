package org.geogebra.common.spreadsheet.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
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
				null,
				List.of(new Selection(new TabularRange(0, 0))),
				new SpreadsheetCoords(0, 0));
		SpreadsheetStyleBarModel.State state2 = new SpreadsheetStyleBarModel.State(
				true,
				Set.of(SpreadsheetStyle.FontTrait.BOLD),
				SpreadsheetStyle.TextAlignment.LEFT,
				null,
				null,
				List.of(new Selection(new TabularRange(0, 0))),
				new SpreadsheetCoords(0, 0));
		assertTrue(state1.equals(state2));
	}

	@Test
	public void testStateEqualityDifferentFontTraits() {
		SpreadsheetStyleBarModel.State state1 = new SpreadsheetStyleBarModel.State(
				true,
				Set.of(SpreadsheetStyle.FontTrait.BOLD),
				SpreadsheetStyle.TextAlignment.LEFT,
				null,
				null,
				List.of(new Selection(new TabularRange(0, 0))),
				new SpreadsheetCoords(0, 0));
		SpreadsheetStyleBarModel.State state2 = new SpreadsheetStyleBarModel.State(
				true,
				Set.of(SpreadsheetStyle.FontTrait.ITALIC),
				SpreadsheetStyle.TextAlignment.LEFT,
				null,
				null,
				List.of(new Selection(new TabularRange(0, 0))),
				new SpreadsheetCoords(0, 0));
		assertFalse(state1.equals(state2));
	}

	@Test
	public void testStateEqualityDifferentSelections() {
		SpreadsheetStyleBarModel.State state1 = new SpreadsheetStyleBarModel.State(
				true,
				Set.of(SpreadsheetStyle.FontTrait.BOLD),
				SpreadsheetStyle.TextAlignment.LEFT,
				null,
				null,
				List.of(new Selection(new TabularRange(0, 0))),
				new SpreadsheetCoords(0, 0));
		SpreadsheetStyleBarModel.State state2 = new SpreadsheetStyleBarModel.State(
				true,
				Set.of(SpreadsheetStyle.FontTrait.ITALIC),
				SpreadsheetStyle.TextAlignment.LEFT,
				null,
				null,
				List.of(new Selection(new TabularRange(0, 0, 1, 1))),
				new SpreadsheetCoords(0, 0));
		assertFalse(state1.equals(state2));
	}

	@Test
	public void testStateEqualityDifferentBackgroundColors() {
		SpreadsheetStyleBarModel.State state1 = new SpreadsheetStyleBarModel.State(
				true,
				null,
				SpreadsheetStyle.TextAlignment.LEFT,
				GColor.BLUE,
				null,
				List.of(new Selection(new TabularRange(0, 0, 1, 1))),
				new SpreadsheetCoords(0, 0));
		SpreadsheetStyleBarModel.State state2 = new SpreadsheetStyleBarModel.State(
				true,
				null,
				SpreadsheetStyle.TextAlignment.LEFT,
				GColor.RED,
				null,
				List.of(new Selection(new TabularRange(0, 0, 1, 1))),
				new SpreadsheetCoords(0, 0));
		assertFalse(state1.equals(state2));
	}
}
