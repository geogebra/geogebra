package org.geogebra.common.spreadsheet.kernel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.DoubleUtil;
import org.junit.Before;
import org.junit.Test;

public class SpreadsheetCellProcessorTest extends BaseUnitTest {
	private SpreadsheetCellProcessor processor;
	private final KernelDataSerializer serializer = new KernelDataSerializer();

	@Before
	public void setUp() {
		ErrorHandler errorHandler = getApp().getDefaultErrorHandler();
		processor =
				new SpreadsheetCellProcessor("A1", getKernel().getAlgebraProcessor(),
						errorHandler);
	}

	@Test
	public void testTextInput() {
		processor.process("(1, 1)");
		assertTrue(lookup("A1").isGeoText());
	}

	@Test
	public void testPointInput() {
		processor.process("=(1, 1)");
		assertTrue(lookup("A1").isGeoPoint());
	}

	@Test
	public void testComputation() {
		processor.process("=1 + 2");
		GeoElement a1 = lookup("A1");
		assertTrue(a1.isGeoNumeric()
				&& DoubleUtil.isEqual(((GeoNumeric) a1).getDouble(), 3.0));

	}

	@Test
	public void testSerializeText() {
		processor.process("(1, 1)");
		assertSerializedAs("(1, 1)");
	}

	private void assertSerializedAs(String value) {
		assertEquals(value, serializer.getStringForEditor(lookup("A1")));
	}

	@Test
	public void testSerializePoint() {
		processor.process("=(1,1)");
		assertSerializedAs("=(1, 1)");
	}

	@Test
	public void testSerializeComputation() {
		processor.process("=1+ 2");
		assertSerializedAs("=1 + 2");
	}
}
