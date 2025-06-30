package org.geogebra.common.spreadsheet.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.SpreadsheetTest;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.junit.Before;
import org.junit.Test;

public class SpreadsheetWithKernelTest extends BaseUnitTest {

	private Spreadsheet spreadsheet;
	private KernelTabularDataAdapter tabularData;

	@Before
	public void setupData() {
		tabularData = new KernelTabularDataAdapter(getApp());
		getKernel().attach(tabularData);

		spreadsheet = new Spreadsheet(tabularData,
				new SpreadsheetTest.TestCellRenderableFactory(),
				null);
	}

	@Test
	public void testDefaultTextAlignment() {
		tabularData.setContent(0, 0, new GeoText(getConstruction(), "GeoText"));
		tabularData.setContent(1, 0, new GeoNumeric(getConstruction(), 123));
		spreadsheet.getController().select(new TabularRange(0, 0), false, false);
		assertEquals(SpreadsheetStyling.TextAlignment.LEFT,
				spreadsheet.getStyleBarModel().getState().textAlignment);
		spreadsheet.getController().select(new TabularRange(2, 0), false, false);
		assertEquals(SpreadsheetStyling.TextAlignment.RIGHT,
				spreadsheet.getStyleBarModel().getState().textAlignment);
	}
}
