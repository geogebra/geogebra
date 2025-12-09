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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.CheckForNull;

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

	private void assertRangeEquals(@CheckForNull TabularRange range,
			int anchorRow, int anchorColumn, int row2, int col2) {
		assertNotNull(range);
		assertEquals(range.getFromRow(), anchorRow);
		assertEquals(range.getFromColumn(), anchorColumn);
		assertEquals(range.getToRow(), row2);
		assertEquals(range.getToColumn(), col2);
	}
}
