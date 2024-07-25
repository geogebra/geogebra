package org.geogebra.common.spreadsheet.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellDataSerializer;
import org.junit.Test;

public class DefaultSpreadsheetCellDataSerializerTest extends BaseUnitTest {

	@Test
	public void testNull() {
		SpreadsheetCellDataSerializer serializer = new DefaultSpreadsheetCellDataSerializer();
		String result = serializer.getStringForEditor(null, false);
		assertEquals("", result);
	}

	@Test
	public void testString() {
		SpreadsheetCellDataSerializer serializer = new DefaultSpreadsheetCellDataSerializer();
		String result = serializer.getStringForEditor("hello", false);
		assertEquals("hello", result);
	}

	@Test
	public void testGeoText() {
		SpreadsheetCellDataSerializer serializer = new DefaultSpreadsheetCellDataSerializer();
		GeoElement text = new GeoText(getConstruction(), "hello");
		String result = serializer.getStringForEditor(text, false);
		assertEquals("hello", result);
	}

	@Test
	public void testGeoNumber() {
		SpreadsheetCellDataSerializer serializer = new DefaultSpreadsheetCellDataSerializer();
		GeoElement number = new GeoNumeric(getConstruction(), 3);
		String result = serializer.getStringForEditor(number, false);
		assertEquals("=3", result);
	}
}
