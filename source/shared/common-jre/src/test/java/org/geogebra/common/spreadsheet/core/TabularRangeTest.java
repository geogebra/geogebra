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

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.annotation.CheckForNull;

import org.junit.jupiter.api.Test;

class TabularRangeTest {

	private final TabularRange cellA3 = new TabularRange(2, 0, 2, 0);
	private final TabularRange cellB3 = new TabularRange(2, 1, 2, 1);
	private final TabularRange cellA4 = new TabularRange(3, 0, 3, 0);

	@Test
	void testFinite() {
		assertAll(
				// empty range
				() -> assertFalse(new TabularRange(-1, -1, -1, -1, -1, -1).isFinite()),
				// all cells
				() -> assertFalse(new TabularRange(-1, -1, -1, -1).isFinite()),
				// one full column
				() -> assertFalse(new TabularRange(-1, 2, -1, 2).isFinite()),
				// multiple full columns
				() -> assertFalse(new TabularRange(-1, 2, -1, 4).isFinite()),
				// one partial column
				() -> assertTrue(new TabularRange(1, 2, 3, 2).isFinite()),
				// one full row
				() -> assertFalse(new TabularRange(2, -1, 2, -1).isFinite()),
				// multiple full rows
				() -> assertFalse(new TabularRange(2, -1, 4, -1).isFinite()),
				// one partial row
				() -> assertTrue(new TabularRange(2, 1, 2, 3).isFinite()),
				// one cell
				() -> assertTrue(new TabularRange(2, 1).isFinite()),
				// rectangular block
				() -> assertTrue(new TabularRange(2, 1, 4, 3).isFinite())
		);
	}

	@Test
	void testSingleCellShape() {
		TabularRange range = new TabularRange(2, 1);
		assertAll(
				() -> assertTrue(range.isSingleCell()),
				() -> assertFalse(range.isEntireColumn()),
				() -> assertFalse(range.isContiguousColumns()),
				() -> assertFalse(range.isSingleColumn()),
				() -> assertFalse(range.isPartialColumn()),
				() -> assertFalse(range.isEntireRow()),
				() -> assertFalse(range.isContiguousRows()),
				() -> assertFalse(range.isSingleRow()),
				() -> assertFalse(range.isPartialRow()),
				() -> assertFalse(range.areAllCellsSelected())
		);
	}

	@Test
	void testPartialRowShape() {
		TabularRange range = new TabularRange(2, 1, 2, 3);
		assertAll(
				() -> assertFalse(range.isSingleCell()),
				() -> assertFalse(range.isEntireColumn()),
				() -> assertFalse(range.isContiguousColumns()),
				() -> assertFalse(range.isSingleColumn()),
				() -> assertFalse(range.isPartialColumn()),
				() -> assertFalse(range.isEntireRow()),
				() -> assertFalse(range.isContiguousRows()),
				() -> assertFalse(range.isSingleRow()),
				() -> assertTrue(range.isPartialRow()),
				() -> assertFalse(range.areAllCellsSelected())
		);
	}

	@Test
	void testPartialColumnShape() {
		TabularRange range = new TabularRange(2, 1, 4, 1);
		assertAll(
				() -> assertFalse(range.isSingleCell()),
				() -> assertFalse(range.isEntireColumn()),
				() -> assertFalse(range.isContiguousColumns()),
				() -> assertFalse(range.isSingleColumn()),
				() -> assertTrue(range.isPartialColumn()),
				() -> assertFalse(range.isEntireRow()),
				() -> assertFalse(range.isContiguousRows()),
				() -> assertFalse(range.isSingleRow()),
				() -> assertFalse(range.isPartialRow()),
				() -> assertFalse(range.areAllCellsSelected())
		);
	}

	@Test
	void testSingleRowShape() {
		TabularRange range = new TabularRange(2, -1, 2, -1);
		assertAll(
				() -> assertFalse(range.isSingleCell()),
				() -> assertFalse(range.isEntireColumn()),
				() -> assertFalse(range.isContiguousColumns()),
				() -> assertFalse(range.isSingleColumn()),
				() -> assertFalse(range.isPartialColumn()),
				() -> assertTrue(range.isEntireRow()),
				() -> assertTrue(range.isContiguousRows()),
				() -> assertTrue(range.isSingleRow()),
				() -> assertFalse(range.isPartialRow()),
				() -> assertFalse(range.areAllCellsSelected())
		);
	}

	@Test
	void testMultipleRowsShape() {
		TabularRange range = new TabularRange(2, -1, 4, -1);
		assertAll(
				() -> assertFalse(range.isSingleCell()),
				() -> assertFalse(range.isEntireColumn()),
				() -> assertFalse(range.isContiguousColumns()),
				() -> assertFalse(range.isSingleColumn()),
				() -> assertFalse(range.isPartialColumn()),
				() -> assertTrue(range.isEntireRow()),
				() -> assertTrue(range.isContiguousRows()),
				() -> assertFalse(range.isSingleRow()),
				() -> assertFalse(range.isPartialRow()),
				() -> assertFalse(range.areAllCellsSelected())
		);
	}

	@Test
	void testSingleColumnShape() {
		TabularRange range = new TabularRange(-1, 2, -1, 2);
		assertAll(
				() -> assertFalse(range.isSingleCell()),
				() -> assertTrue(range.isEntireColumn()),
				() -> assertTrue(range.isContiguousColumns()),
				() -> assertTrue(range.isSingleColumn()),
				() -> assertFalse(range.isPartialColumn()),
				() -> assertFalse(range.isEntireRow()),
				() -> assertFalse(range.isContiguousRows()),
				() -> assertFalse(range.isSingleRow()),
				() -> assertFalse(range.isPartialRow()),
				() -> assertFalse(range.areAllCellsSelected())
		);
	}

	@Test
	void testMultipleColumnsShape() {
		TabularRange range = new TabularRange(-1, 2, -1, 4);
		assertAll(
				() -> assertFalse(range.isSingleCell()),
				() -> assertTrue(range.isEntireColumn()),
				() -> assertTrue(range.isContiguousColumns()),
				() -> assertFalse(range.isSingleColumn()),
				() -> assertFalse(range.isPartialColumn()),
				() -> assertFalse(range.isEntireRow()),
				() -> assertFalse(range.isContiguousRows()),
				() -> assertFalse(range.isSingleRow()),
				() -> assertFalse(range.isPartialRow()),
				() -> assertFalse(range.areAllCellsSelected())
		);
	}

	@Test
	void testAllCellsShape() {
		TabularRange range = new TabularRange(-1, -1, -1, -1);
		assertAll(
				() -> assertFalse(range.isSingleCell()),
				() -> assertTrue(range.isEntireColumn()),
				() -> assertFalse(range.isContiguousColumns()),
				() -> assertFalse(range.isSingleColumn()),
				() -> assertFalse(range.isPartialColumn()),
				() -> assertTrue(range.isEntireRow()),
				() -> assertFalse(range.isContiguousRows()),
				() -> assertFalse(range.isSingleRow()),
				() -> assertFalse(range.isPartialRow()),
				() -> assertTrue(range.areAllCellsSelected())
		);
	}

	@Test
	void testGetRectangularUnionIdentical() {
		assertRangeEquals(cellA3.getRectangularUnion(cellA3), 2, 0, 2, 0);
		assertRangeEquals(cellB3.getRectangularUnion(cellB3), 2, 1, 2, 1);
	}

	@Test
	void testGetRectangularUnionAdjacentHorizontal() {
		assertRangeEquals(cellA3.getRectangularUnion(cellB3), 2, 0, 2, 1);
		assertRangeEquals(cellB3.getRectangularUnion(cellA3), 2, 0, 2, 1);
	}

	@Test
	void testGetRectangularUnionAdjacentVertical() {
		assertRangeEquals(cellA3.getRectangularUnion(cellA4), 2, 0, 3, 0);
		assertRangeEquals(cellA4.getRectangularUnion(cellA3), 2, 0, 3, 0);
	}

	@Test
	void testMergeIndependent() {
		assertThat(cellA4.getRectangularUnion(cellB3), nullValue());
		assertThat(cellB3.getRectangularUnion(cellA4), nullValue());
	}

	private void assertRangeEquals(@CheckForNull TabularRange range,
			int anchorRow, int anchorColumn, int row2, int col2) {
		assertNotNull(range);
		assertEquals(range.getFromRow(), anchorRow);
		assertEquals(range.getFromColumn(), anchorColumn);
		assertEquals(range.getToRow(), row2);
		assertEquals(range.getToColumn(), col2);
	}
}
