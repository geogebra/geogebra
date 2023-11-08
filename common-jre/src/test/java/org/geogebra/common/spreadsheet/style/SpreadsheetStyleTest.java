package org.geogebra.common.spreadsheet.style;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.junit.Before;
import org.junit.Test;

public class SpreadsheetStyleTest extends BaseUnitTest {

	private CellFormat cellFormat;
	private SpreadsheetStyle style;

	@Before
	public void setupStyle() {
		cellFormat = new CellFormat(null);
		style = new SpreadsheetStyle(cellFormat);
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
}
