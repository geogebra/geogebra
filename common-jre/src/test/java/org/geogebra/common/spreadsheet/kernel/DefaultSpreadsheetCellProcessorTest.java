package org.geogebra.common.spreadsheet.kernel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.DoubleUtil;
import org.junit.Before;
import org.junit.Test;

public class DefaultSpreadsheetCellProcessorTest extends BaseUnitTest {
	private DefaultSpreadsheetCellProcessor processor;
	private final DefaultSpreadsheetCellDataSerializer
			serializer = new DefaultSpreadsheetCellDataSerializer();

	@Before
	public void setUp() {
		processor =
				new DefaultSpreadsheetCellProcessor(getKernel().getAlgebraProcessor());
		getKernel().attach(new KernelTabularDataAdapter(
				getSettings().getSpreadsheet(), getKernel()));
	}

	@Test
	public void testTextInput() {
		processor.process("(1, 1)", "A1");
		assertTrue(lookup("A1").isGeoText());
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	public void testTextInputWithQuotes() {
		processor.process("\"1+2\"", "A1");
		assertThat(lookup("A1"), hasValue("1+2"));
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	public void testPointInput() {
		processor.process("=(1, 1)", "A1");
		assertTrue(lookup("A1").isGeoPoint());
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	public void testComputation() {
		processor.process("=1 + 2", "A1");
		assertNumberCellValue("A1", 3.0);
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	private void assertNumberCellValue(String cellName, double value) {
		GeoElement a1 = lookup(cellName);
		assertTrue(a1.isGeoNumeric()
				&& DoubleUtil.isEqual(((GeoNumeric) a1).getDouble(), value));
	}

	@Test
	public void testNumberInput() {
		shouldBeNumber("2");
		shouldBeNumber("2.3456");
		shouldBeNumber("-2.3456");
	}

	private void shouldBeNumber(String number) {
		processor.process(number, "A1");
		GeoElement a1 = lookup("A1");
		assertTrue("A1 is not a number", a1.isGeoNumeric());
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	public void testAddingNumbers() {
		processor.process("2", "A1");
		processor.process("3", "A2");
		processor.process("=A1 + A2", "A3");
		assertNumberCellValue("A3", 5);
	}

	@Test
	public void testSerializeText() {
		processor.process("(1, 1)", "A1");
		assertSerializedAs("(1, 1)");
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	public void testSerializePoint() {
		processor.process("=(1,1)", "A1");
		assertSerializedAs("=(1, 1)");
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	public void testSerializeComputation() {
		processor.process("=1+ 2", "A1");
		assertSerializedAs("=1 + 2");
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	private void assertSerializedAs(String value) {
		assertEquals("The values do not match!", value,
				serializer.getStringForEditor(lookup("A1")));
	}

	private void assertIsAuxiliary() {
		assertTrue("The created element is not auxiliary!",
				lookup("A1").isAuxiliaryObject());
	}

	private void assertIsEuclidianInvisible() {
		assertFalse("The created element is visible within the EV!",
				lookup("A1").isEuclidianVisible());
	}

	@Test
	public void testErrorShouldBeTextWithOriginalInput() {
		processor.process("=1+@", "A1");
		GeoElement a1 = lookup("A1");
		assertTrue(a1.isGeoText());
		assertEquals(serializer.getStringForEditor(lookup("A1")),
				"=1+@");
	}

	@Test
	public void testNoOperationForTextMinus() {
		processor.process("7-2", "A1");
		assertTrue(lookup("A1").isGeoText());
		processor.process("-7-2", "A1");
		assertTrue(lookup("A1").isGeoText());
	}

	@Test
	public void testNumericOrTextInputShouldHaveNoError() {
		processor.process("1", "A1");
		GeoElement a1 = lookup("A1");
		assertFalse(a1.getXML().contains("hasSpreadsheetError"));

		processor.process("Text(\"foo\")", "A2");
		GeoElement a2 = lookup("A2");
		assertTrue(a2.isGeoText());
		assertFalse(((GeoText) a2).hasSpreadsheetError());
	}

	@Test
	public void testInvalidInputShouldHaveError() {
		processor.process("=1+%", "A1");
		GeoElement a1 = lookup("A1");
		assertTrue(a1.isGeoText());
		assertTrue(((GeoText) a1).hasSpreadsheetError());
	}
}
