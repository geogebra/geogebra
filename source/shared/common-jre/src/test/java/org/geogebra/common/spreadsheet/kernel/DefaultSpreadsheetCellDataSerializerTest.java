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

package org.geogebra.common.spreadsheet.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellDataSerializer;
import org.geogebra.editor.share.util.Unicode;
import org.junit.Before;
import org.junit.Test;

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
