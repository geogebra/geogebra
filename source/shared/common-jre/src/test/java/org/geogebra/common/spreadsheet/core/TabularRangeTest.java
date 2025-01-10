package org.geogebra.common.spreadsheet.core;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Nullable;

import org.junit.Test;

public class TabularRangeTest {

	private final TabularRange cellA3 = new TabularRange(2, 0, 2, 0);
	private final TabularRange cellB3 = new TabularRange(2, 1, 2, 1);
	private final TabularRange cellA4 = new TabularRange(3, 0, 3, 0);

	@Test
	public void testGetRectangularUnionIdentical() {
		assertRangeEquals(cellA3.getRectangularUnion(cellA3), 2, 0, 2, 0);
		assertRangeEquals(cellB3.getRectangularUnion(cellB3), 2, 1, 2, 1);
	}

	@Test
	public void testGetRectangularUnionAdjacentHorizontal() {
		assertRangeEquals(cellA3.getRectangularUnion(cellB3), 2, 0, 2, 1);
		assertRangeEquals(cellB3.getRectangularUnion(cellA3), 2, 0, 2, 1);
	}

	@Test
	public void testGetRectangularUnionAdjacentVertical() {
		assertRangeEquals(cellA3.getRectangularUnion(cellA4), 2, 0, 3, 0);
		assertRangeEquals(cellA4.getRectangularUnion(cellA3), 2, 0, 3, 0);
	}

	@Test
	public void testMergeIndependent() {
		assertThat(cellA4.getRectangularUnion(cellB3), nullValue());
		assertThat(cellB3.getRectangularUnion(cellA4), nullValue());
	}

	private void assertRangeEquals(@Nullable TabularRange range, int anchorRow, int anchorColumn,
			int row2, int col2) {
		assertNotNull(range);
		assertEquals(range.getFromRow(), anchorRow);
		assertEquals(range.getFromColumn(), anchorColumn);
		assertEquals(range.getToRow(), row2);
		assertEquals(range.getToColumn(), col2);
	}
}
