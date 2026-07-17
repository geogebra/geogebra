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

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultSpreadsheetCellProcessorTest extends BaseUnitTest {
	private DefaultSpreadsheetCellProcessor processor;
	private DefaultSpreadsheetCellDataSerializer serializer;

	@BeforeEach
	void setAppConfig() {
		getApp().setConfig(new AppConfigGraphing());
		serializer = new DefaultSpreadsheetCellDataSerializer();
	}

	@BeforeEach
	void setUp() {
		processor = new DefaultSpreadsheetCellProcessor(getKernel().getAlgebraProcessor());
		getKernel().attach(new KernelTabularDataAdapter(getApp()));
	}

	@Test
	void testTextInput() {
		processor.process("(1, 1)", "A1");
		assertEquals(GeoClass.TEXT, lookup("A1").getGeoClassType());
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	void testTextInputWithQuotes() {
		processor.process("\"1+2\"", "A1");
		assertThat(lookup("A1"), hasValue("1+2"));
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	void testPointInput() {
		processor.process("=(1, 1)", "A1");
		assertTrue(lookup("A1").isGeoPoint());
		assertIsAuxiliary();
		assertTrue(lookup("A1").isEuclidianVisible(),
				"Points from spreadsheet should be visible.");
	}

	@Test
	void testComputation() {
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
	void testNumberInput() {
		shouldBeNumber("2");
		shouldBeNumber("2.3456");
		shouldBeNumber("-2.3456");
	}

	private void shouldBeNumber(String number) {
		processor.process(number, "A1");
		GeoElement a1 = lookup("A1");
		assertTrue(a1.isGeoNumeric(), "A1 is not a number");
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	@Test
	void testAddingNumbers() {
		processor.process("2", "A1");
		processor.process("3", "A2");
		processor.process("=A1 + A2", "A3");
		assertNumberCellValue("A3", 5);
	}

	@Test
	void testSerializeText() {
		processor.process("(1, 1)", "A1");
		assertSerializedAs("(1, 1)", "A1");
	}

	@Test
	void testSerializePoint() {
		processor.process("=(1,1)", "A1");
		assertSerializedAs("=(1,1)", "A1");
	}

	@Test
	void testSerializeComputation() {
		processor.process("=1+ 2", "A1");
		assertSerializedAs("=1+2", "A1");
		assertIsAuxiliary();
		assertIsEuclidianInvisible();
	}

	private void assertSerializedAs(String value, String cellName) {
		assertEquals(value, serializer.getStringForEditor(lookup(cellName)),
				"The values do not match!");
	}

	private void assertIsAuxiliary() {
		assertTrue(lookup("A1").isAuxiliaryObject(),
				"The created element is not auxiliary!");
	}

	private void assertIsEuclidianInvisible() {
		assertFalse(lookup("A1").isEuclidianVisible(),
				"The created element is visible within the EV!");
	}

	@Test
	void testErrorShouldBeTextWithOriginalInput() {
		processor.process("=1+@", "A1");
		GeoElement a1 = lookup("A1");
		assertEquals(GeoClass.NUMERIC, a1.getGeoClassType());
		assertSerializedAs("=1+@", "A1");
	}

	@Test
	void testNoOperationForTextMinus() {
		processor.process("7-2", "A1");
		assertEquals(GeoClass.TEXT, lookup("A1").getGeoClassType());
		processor.process("-7-2", "A1");
		assertEquals(GeoClass.TEXT, lookup("A1").getGeoClassType());
	}

	@Test
	void testNumericOrTextInputShouldHaveNoError() {
		processor.process("1", "A1");
		GeoElement a1 = lookup("A1");
		assertThat(getCommand(a1), nullValue());

		processor.process("Text(\"foo\")", "A2");
		GeoElement a2 = lookup("A2");
		assertEquals(GeoClass.TEXT, a2.getGeoClassType());
		assertThat(getCommand(a2), nullValue());
	}

	@Test
	void dependentObjectsShouldPropagateError() {
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
	void testInvalidInputShouldHaveError() {
		processor.process("=1+%", "A1");
		GeoElement a1 = lookup("A1");
		assertEquals(GeoClass.NUMERIC, a1.getGeoClassType());
		assertEquals(Commands.ParseToNumber, getCommand(a1));
	}

	@Test
	@Issue("APPS-7720")
	void testInvalidInputWithBrackets() {
		processor.process("=(", "A1");
		GeoElement a1 = lookup("A1");
		assertEquals(Commands.ParseToNumber, getCommand(a1));
	}

	@Test
	@Issue("APPS-7720")
	void testInvalidInputTextMode() {
		processor.process("(", "A1");
		GeoElement a1 = lookup("A1");
		assertEquals(GeoClass.TEXT, a1.getGeoClassType());
		assertEquals("(", a1.toValueString(StringTemplate.testTemplate));
	}

	@Test
	void handleCircularDefinitions() {
		add("A2=1");
		add("B2=2");
		add("B3=A2+B2");
		processor.process("=A2+B3", "B3");
		GeoElement b3 = lookup("B3");
		assertEquals(GeoClass.NUMERIC, b3.getGeoClassType());
		assertEquals(Commands.ParseToNumber, getCommand(b3));
	}

	@Test
	void handleSelfReferencingDefinitions() {
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
	void shouldAutoCreateZeroCells1() {
		processor.process("=A2+B2+1", "B3");
		assertThat(lookup("A2"), hasValue("0"));
		assertThat(lookup("B2"), hasValue("0"));
		assertThat(lookup("B3"), hasValue("1"));
		assertTrue(lookup("A2").isEmptySpreadsheetCell(), "A2 should be empty");
		assertFalse(lookup("B3").isEmptySpreadsheetCell(), "B3 should not be empty");
	}

	@Test
	@Issue("APPS-5983")
	void shouldAutoCreateZeroCells2() {
		processor.process("=1", "A1");
		processor.process("=A2", "B1");
		assertThat(lookup("A2"), hasValue("0"));
	}

	@Test
	@Issue("APPS-5983")
	void shouldAutoCreateZeroCells3() {
		processor.process("=3", "C3");
		processor.process("=C4", "D4");
		assertThat(lookup("D4"), hasValue("0"));
	}

	private GetCommand getCommand(GeoElement a1) {
		return a1.getParentAlgorithm() == null ? null
				: a1.getParentAlgorithm().getClassName();
	}

	@Test
	void handleTextReferences() {
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
	void emptyStringInEmptyCellShouldHaveNoEffect() {
		activateUndo();
		processor.process("", "A2");
		assertNull(lookup("A2"));
		assertFalse(getKernel().getConstruction().getUndoManager().undoPossible());
	}

	@Test
	@Issue("APPS-6628")
	void emptyStringInExistingCellShouldDelete() {
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
	void invalidCellReferenceShouldNotLeadToMultiplication() {
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
