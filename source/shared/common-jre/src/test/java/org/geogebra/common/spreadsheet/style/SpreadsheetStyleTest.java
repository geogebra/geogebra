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

public class SpreadsheetStyleTest extends BaseUnitTest {

	private SpreadsheetStyle style;
	private CellFormat cellFormat;

	@Before
	public void setupStyle() {
		style = new SpreadsheetStyle();
		cellFormat = style.getFormat();
	}

	@Test
	public void changeShouldApplyToAllCells() {
		TabularRange column = new TabularRange(-1, 1, -1, 1);
		cellFormat.setFormat(column, CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_CENTER);
		TabularRange row = new TabularRange(5, -1, 5, -1);
		cellFormat.setFormat(row, CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_RIGHT);
		// selected column is centered
		assertThat(style.getAlignment(0, 1), equalTo(CellFormat.ALIGN_CENTER));
		assertThat(style.getAlignment(10, 1), equalTo(CellFormat.ALIGN_CENTER));
		// selected row is right-aligned
		assertThat(style.getAlignment(5, 2), equalTo(CellFormat.ALIGN_RIGHT));
		// intersection of row/column is right-aligned
		assertThat(style.getAlignment(5, 1), equalTo(CellFormat.ALIGN_RIGHT));
		// other cells unaffected
		assertThat(style.getAlignment(0, 2), nullValue());
	}

	@Test
	public void shouldReload() {
		TabularRange column = new TabularRange(-1, 1, -1, 1);
		cellFormat.setFormat(column, CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_CENTER);
		TabularRange row = new TabularRange(5, -1, 5, -1);
		cellFormat.setFormat(row, CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_RIGHT);
		String forXML = cellFormat.encodeFormats().toString();
		assertThat(forXML, equalTo("1,-1,a,0:-1,5,a,4"));
		cellFormat.clearAll();
		assertThat(style.getAlignment(5, 1), nullValue());
		cellFormat.processXMLString(forXML);
		assertThat(style.getAlignment(5, 1), equalTo(CellFormat.ALIGN_RIGHT));
	}

	@Test
	public void testTextColor() {
		List<TabularRange> ranges = List.of(new TabularRange(1, 2));

		cellFormat.setFormat(ranges, CellFormat.FORMAT_FGCOLOR, GColor.RED);

		String actualFormat = cellFormat.encodeFormats().toString();
		assertEquals("2,1,t," + GColor.RED.getARGB(), actualFormat);

		cellFormat.clearAll();
		cellFormat.processXMLString(actualFormat);

		Object format = cellFormat.getCellFormat(ranges.get(0), CellFormat.FORMAT_FGCOLOR);
		assertEquals(GColor.RED, format);
	}
}
