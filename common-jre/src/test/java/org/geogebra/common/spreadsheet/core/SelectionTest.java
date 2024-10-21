package org.geogebra.common.spreadsheet.core;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

public class SelectionTest {

	Selection cells;
	Selection columns;
	Selection rows;

	@Before
	public void setup() {
		cells = new Selection(new TabularRange(3, 5, 4, 6));
		rows = new Selection(new TabularRange(3, -1, 4, -1));
		columns = new Selection(new TabularRange(-1, 5, -1, 6));
	}

	@Test
	public void testGetSelectionForMoveLeft() {
		assertThat(cells.getNextCellForMoveLeft(), equalToCell(3, 4));
		assertThat(rows.getNextCellForMoveLeft(), equalToCell(3, 0));
		assertThat(columns.getNextCellForMoveLeft(), equalToCell(0, 4));
	}

	@Test
	public void testGetSelectionForMoveRight() {
		assertThat(cells.getNextCellForMoveRight(100), equalToCell(3, 6));
		assertThat(rows.getNextCellForMoveRight(100), equalToCell(3, 1));
		assertThat(columns.getNextCellForMoveRight(100), equalToCell(0, 6));
	}

	@Test
	public void testGetSelectionForMoveUp() {
		assertThat(cells.getNextCellForMoveUp(), equalToCell(2, 5));
		assertThat(rows.getNextCellForMoveUp(), equalToCell(2, 0));
		assertThat(columns.getNextCellForMoveUp(), equalToCell(0, 5));
	}

	@Test
	public void testGetSelectionForMoveDown() {
		assertThat(cells.getNextCellForMoveDown(100), equalToCell(4, 5));
		assertThat(rows.getNextCellForMoveDown(100), equalToCell(4, 0));
		assertThat(columns.getNextCellForMoveDown(100), equalToCell(1, 5));
	}

	private TypeSafeMatcher<Selection> equalToCell(int row, int col) {
		return new TypeSafeMatcher<>() {
			@Override
			protected boolean matchesSafely(Selection selection) {
				return selection.getRange().equals(new TabularRange(row, col, row, col));
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(" single cell at " + row + "," + col);
			}
		};
	}
}
