package org.geogebra.common.spreadsheet.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellDataSerializer;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class DefaultSpreadsheetCellDataSerializerTest extends BaseUnitTest {

	SpreadsheetCellDataSerializer serializer;

	@Before
	public void setupSerializer() {
		serializer = new DefaultSpreadsheetCellDataSerializer();
	}

	@Test
	public void testNull() {
		String result = serializer.getStringForEditor(null);
		assertEquals("", result);
	}

	@Test
	public void testString() {
		String result = serializer.getStringForEditor("hello");
		assertEquals("hello", result);
	}

	@Test
	public void testGeoText() {
		GeoElement text = new GeoText(getConstruction(), "hello");
		String result = serializer.getStringForEditor(text);
		assertEquals("hello", result);
	}

	@Test
	public void testGeoNumber() {
		GeoElement number = new GeoNumeric(getConstruction(), 3);
		String result = serializer.getStringForEditor(number);
		assertEquals("=3", result);
	}

	@Test
	public void testMixedNumber() {
		GeoNumeric number = add("1" + Unicode.INVISIBLE_PLUS + "2/3");
		String result = serializer.getStringForEditor(number);
		assertEquals("=1" + Unicode.INVISIBLE_PLUS + "(2)/(3)", result);
	}
}
