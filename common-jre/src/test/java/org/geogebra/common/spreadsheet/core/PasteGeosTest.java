package org.geogebra.common.spreadsheet.core;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.junit.Before;
import org.junit.Test;

public class PasteGeosTest extends BaseUnitTest {

	private CopyPasteCutTabularDataImpl<?> copyPasteCut;

	@Before
	public void setUp() throws Exception {
		KernelTabularDataAdapter tabularData = new KernelTabularDataAdapter(
				new SpreadsheetSettings(), null);
		tabularData.setContent(1, 1, add("1"));
		tabularData.setContent(1, 2, add("2"));
		tabularData.setContent(2, 1, add("Text(\"foo\")"));
		tabularData.setContent(2, 2, add("Button()"));

		copyPasteCut = new CopyPasteCutTabularDataImpl<>(tabularData, new TestClipboard(), null);
	}

	@Test
	public void checkInitialContent() {
		checkContent(1, 1);
	}

	private void checkContent(int row, int column) {
		assertThat(cell(row, column), hasValue("1"));
		assertThat(cell(row, column + 1), hasValue("2"));
		assertThat(cell(row + 1, column), hasValue("foo"));
		assertThat(cell(row + 1, column + 1), hasProperty("Caption",
				geo -> geo.getCaption(StringTemplate.defaultTemplate),
				GeoElementSpreadsheet.getSpreadsheetCellName(column + 1, row + 1)));
	}

	private GeoElement cell(int row, int column) {
		return lookup(GeoElementSpreadsheet.getSpreadsheetCellName(column, row));
	}

	@Test
	public void testPaste() {
		copyPasteCut.copyDeep(new TabularRange(1, 1, 2, 2));
		copyPasteCut.paste(new TabularRange(4, 4, 5, 5));
		checkContent(1, 1);
		checkContent(4, 4);
	}
}
