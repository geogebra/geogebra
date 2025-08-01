package org.geogebra.common.spreadsheet.kernel;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.test.annotation.Issue;
import org.junit.Before;
import org.junit.Test;

public class DefaultSpreadsheetCellProcessorTest extends BaseUnitTest {
	private DefaultSpreadsheetCellProcessor processor;
	private DefaultSpreadsheetCellDataSerializer serializer;

	@Before
	public void setAppConfig() {
		getApp().setConfig(new AppConfigGraphing());
		serializer = new DefaultSpreadsheetCellDataSerializer();
	}

	@Before
	public void setUp() {
		processor = new DefaultSpreadsheetCellProcessor(getKernel().getAlgebraProcessor());
		getKernel().attach(new KernelTabularDataAdapter(getApp()));
	}

	@Test
	public void testTextInput() {
		processor.process("(1, 1)", "A1");
		assertEquals(GeoClass.TEXT, lookup("A1").getGeoClassType());
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
		assertTrue("Points from spreadsheet should be visible.",
				lookup("A1").isEuclidianVisible());
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
		assertSerializedAs("(1, 1)", "A1");
	}

	@Test
	public void testSerializePoint() {
		processor.process("=(1,1)", "A1");
		assertSerializedAs("=(1,1)", "A1");
	}

	@Test
	public void testSerializeComputation() {
		processor.process("=1+ 2", "A1");
		assertSerializedAs("=1+2", "A1");
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	private void assertSerializedAs(String value, String cellName) {
		assertEquals("The values do not match!", value,
				serializer.getStringForEditor(lookup(cellName)));
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
		assertEquals(GeoClass.NUMERIC, a1.getGeoClassType());
		assertSerializedAs("=1+@", "A1");
	}

	@Test
	public void testNoOperationForTextMinus() {
		processor.process("7-2", "A1");
		assertEquals(GeoClass.TEXT, lookup("A1").getGeoClassType());
		processor.process("-7-2", "A1");
		assertEquals(GeoClass.TEXT, lookup("A1").getGeoClassType());
	}

	@Test
	public void testNumericOrTextInputShouldHaveNoError() {
		processor.process("1", "A1");
		GeoElement a1 = lookup("A1");
		assertThat(getCommand(a1), nullValue());

		processor.process("Text(\"foo\")", "A2");
		GeoElement a2 = lookup("A2");
		assertEquals(GeoClass.TEXT, a2.getGeoClassType());
		assertThat(getCommand(a2), nullValue());
	}

	@Test
	public void dependentObjectsShouldPropagateError() {
		processor.process("=1+", "A1");
		GeoElement a1 = lookup("A1");
		assertEquals(Commands.ParseToNumber, getCommand(a1));

		processor.process("=A1+A1", "A2");
		GeoElement a2 = lookup("A2");
		assertEquals(GeoClass.NUMERIC, a2.getGeoClassType());
		assertEquals(Algos.Expression, getCommand(a2));
		assertThat(a2, not(isDefined()));

		processor.process("=Length(A1)", "A3");
		GeoElement a3 = lookup("A3");
		assertEquals(GeoClass.NUMERIC, a3.getGeoClassType());
		assertEquals(Commands.ParseToNumber, getCommand(a3));
		assertThat(a3, not(isDefined()));
	}

	@Test
	public void testInvalidInputShouldHaveError() {
		processor.process("=1+%", "A1");
		GeoElement a1 = lookup("A1");
		assertEquals(GeoClass.NUMERIC, a1.getGeoClassType());
		assertEquals(Commands.ParseToNumber, getCommand(a1));
	}

	@Test
	public void handleCircularDefinitions() {
		add("A2=1");
		add("B2=2");
		add("B3=A2+B2");
		processor.process("=A2+B3", "B3");
		GeoElement b3 = lookup("B3");
		assertEquals(GeoClass.NUMERIC, b3.getGeoClassType());
		assertEquals(Commands.ParseToNumber, getCommand(b3));
	}

	@Test
	public void handleSelfReferencingDefinitions() {
		processor.process("=A1", "A1");
		assertEquals(Commands.ParseToNumber, getCommand(lookup("A1")));
		assertThat(lookup("A1"), hasValue("?"));

		processor.process("=A2+B3", "B3");
		assertEquals(Commands.ParseToNumber, getCommand(lookup("B3")));
		assertThat(lookup("B3"), hasValue("?"));

		processor.process("=A3+1", "A3");
		assertEquals(Commands.ParseToNumber, getCommand(lookup("A3")));
		assertThat(lookup("A3"), hasValue("?"));

		assertEquals("A1,B3,A3",
				String.join(",", getApp().getGgbApi().getAllObjectNames()));
	}

	@Test
	public void shouldAutoCreateZeroCells1() {
		processor.process("=A2+B2+1", "B3");
		assertThat(lookup("A2"), hasValue("0"));
		assertThat(lookup("B2"), hasValue("0"));
		assertThat(lookup("B3"), hasValue("1"));
		assertTrue("A2 should be empty", lookup("A2").isEmptySpreadsheetCell());
		assertFalse("B3 should not be empty", lookup("B3").isEmptySpreadsheetCell());
	}

	@Test
	@Issue("APPS-5983")
	public void shouldAutoCreateZeroCells2() {
		processor.process("=1", "A1");
		processor.process("=A2", "B1");
		assertThat(lookup("A2"), hasValue("0"));
	}

	@Test
	@Issue("APPS-5983")
	public void shouldAutoCreateZeroCells3() {
		processor.process("=3", "C3");
		processor.process("=C4", "D4");
		assertThat(lookup("D4"), hasValue("0"));
	}

	private GetCommand getCommand(GeoElement a1) {
		return a1.getParentAlgorithm() == null ? null
				: a1.getParentAlgorithm().getClassName();
	}

	@Test
	public void handleTextReferences() {
		add("A1=\"foobar\"");
		processor.process("=A1", "B2");
		assertSerializedAs("=A1", "B2");
		processor.process("=$A1", "B3");
		assertSerializedAs("=$A1", "B3");
		processor.process("=$A$1", "B4");
		assertSerializedAs("=$A$1", "B4");
		processor.process("=First(A1,3)", "B5");
		assertSerializedAs("=First(A1,3)", "B5");
	}

	@Test
	@Issue("APPS-6628")
	public void emptyStringInEmptyCellShouldHaveNoEffect() {
		activateUndo();
		processor.process("", "A2");
		assertNull(lookup("A2"));
		assertFalse(getKernel().getConstruction().getUndoManager().undoPossible());
	}

	@Test
	@Issue("APPS-6628")
	public void emptyStringInExistingCellShouldDelete() {
		activateUndo();
		add("A2=42");
		getApp().storeUndoInfo();
		processor.process("", "A2");
		assertNull(lookup("A2"));
		assertTrue(getKernel().getConstruction().getUndoManager().undoPossible());
		getKernel().undo();
		assertThat(lookup("A2"), hasValue("42"));
	}

	@Test
	@Issue("APPS-6761")
	public void invalidCellReferenceShouldNotLeadToMultiplication() {
		processor.process("=2", "A1");
		// Valid cell reference
		processor.process("=A1111111111", "B1");
		assertSerializedAs("=A1111111111", "B1");
		// Invalid cell reference should not become A1 * 1111111111
		processor.process("=A11111111111", "C1");
		assertSerializedAs("=A11111111111", "C1");
		assertThat(lookup("C1"), not(isDefined()));
	}
}