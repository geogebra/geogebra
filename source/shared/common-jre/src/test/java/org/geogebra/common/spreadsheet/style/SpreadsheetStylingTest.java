package org.geogebra.common.spreadsheet.style;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.junit.Before;
import org.junit.Test;

public class SpreadsheetStylingTest extends BaseUnitTest {

	private SpreadsheetStyling styling;

	@Before
	@Override
	public void setup() {
		styling = new SpreadsheetStyling();
	}

	@Test
	public void changeShouldApplyToAllCells() {
		TabularRange column = new TabularRange(-1, 1, -1, 1);
		styling.setTextAlignment(SpreadsheetStyling.TextAlignment.CENTERED, List.of(column));
		TabularRange row = new TabularRange(5, -1, 5, -1);
		styling.setTextAlignment(SpreadsheetStyling.TextAlignment.RIGHT, List.of(row));

		// selected column is centered
		assertThat(styling.getAlignment(0, 1), equalTo(CellFormat.ALIGN_CENTER));
		assertThat(styling.getAlignment(10, 1), equalTo(CellFormat.ALIGN_CENTER));
		// selected row is right-aligned
		assertThat(styling.getAlignment(5, 2), equalTo(CellFormat.ALIGN_RIGHT));
		// intersection of row/column is right-aligned
		assertThat(styling.getAlignment(5, 1), equalTo(CellFormat.ALIGN_RIGHT));
		// other cells unaffected
		assertThat(styling.getAlignment(0, 2), nullValue());
	}

	@Test
	public void testXml() {
		TabularRange column = new TabularRange(-1, 1, -1, 1);
		styling.setTextAlignment(SpreadsheetStyling.TextAlignment.CENTERED, List.of(column));

		TabularRange row = new TabularRange(5, -1, 5, -1);
		styling.setTextAlignment(SpreadsheetStyling.TextAlignment.RIGHT, List.of(row));

		assertThat(styling.getCellFormatXml(), equalTo("1,-1,a,0:-1,5,a,4"));
	}

	@Test
	public void testTextColor() {
		List<TabularRange> ranges = List.of(new TabularRange(1, 2));
		styling.setTextColor(GColor.RED, ranges);
		assertEquals("2,1,t," + GColor.RED.getARGB(), styling.getCellFormatXml());
	}
}
