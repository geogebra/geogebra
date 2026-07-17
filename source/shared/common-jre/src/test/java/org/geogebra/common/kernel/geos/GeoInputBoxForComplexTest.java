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

package org.geogebra.common.kernel.geos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

class GeoInputBoxForComplexTest extends BaseUnitTest {

	@Test
	@Issue("WLY-28")
	void rootOfMinusOneShouldBeImaginaryWithComplexNumber() {
		add("z_1 = 3 + 2i");
		shouldBeUpdatedAs("sqrt(-1)", "i");
	}

	@Test
	void imaginaryUnitShouldOverrideUserDefinedVarForPoints() {
		add("z_1 = 3 + 2i");
		add("i = 7");
		shouldBeUpdatedAs("2i", "2 i");
		assertEquals("2i",
				lookup("z_1").toValueString(StringTemplate.latexTemplate));
	}

	@Test
	void userDefinedVarShouldOverrideImaginaryUnitForNumbers() {
		add("i = 7");
		add("z_1 = 3 + 2i");
		shouldBeUpdatedAs("2i", "2 i");
		assertEquals("14",
				lookup("z_1").toValueString(StringTemplate.latexTemplate));
	}

	@Test
	void rootOfMinusOneShouldBeUsedInExpression() {
		add("z_1 = 1 + 6i");
		shouldBeUpdatedAs("2 + 3sqrt(-1)", "2+3 i");
	}

	@Test
	void sinShouldBeTyped() {
		add("z_1 = 3+2i");
		shouldBeUpdatedAs("sin45", "sin(45" + Unicode.DEGREE_CHAR + ")");
	}

	private void shouldBeUpdatedAs(String updatedText, String expected) {
		GeoInputBox inputBox = addAvInput("ib = InputBox(z_1)");
		inputBox.updateLinkedGeo(updatedText);
		assertEquals(expected, inputBox.getTextForEditor());
	}

	@Test
	void testImaginaryShouldRenderedAsRegularI() {
		GeoInputBox inputBox = withComplexLinkedGeo();
		assertEquals("3 + 2 \\; i", inputBox.getText());
		assertEquals("3+2 i", inputBox.getTextForEditor());
	}

	@Test
	void testImaginaryShouldRenderedAsRegularIForFunctions() {
		GeoInputBox inputBox = withLinkedGeo("f", "x + i");
		assertEquals("x + i", inputBox.getText());
		assertEquals("x+i", inputBox.getTextForEditor());
	}

	protected GeoInputBox withLinkedGeo(String definition, String label, String value) {
		add(definition + " = " + value);
		GeoInputBox inputBox = add("InputBox(" + label + ")");
		inputBox.setSymbolicMode(true);
		return inputBox;
	}

	protected GeoInputBox withLinkedGeo(String label, String value) {
		return withLinkedGeo(label, label, value);
	}

	protected GeoInputBox withComplexLinkedGeo() {
		return withLinkedGeo("z_1", "3+2i");
	}

	@Test
	void testImaginaryShouldEditedAsRegularI() {
		GeoInputBox inputBox = withComplexLinkedGeo();
		assertEquals("3+2 i", inputBox.getTextForEditor());
	}

	@Test
	void testOnUpdateImaginaryShouldBeUsed() {
		GeoInputBox inputBox = withComplexLinkedGeo();
		inputBox.updateLinkedGeo("4 + 5" + Unicode.IMAGINARY);
		assertEquals("4 + 5 \\; i", inputBox.getText());
		assertEquals("4+5 i", inputBox.getTextForEditor());
	}

	@Test
	void capitalIShouldBeSmallIWhenComplex() {
		GeoInputBox inputBox = withComplexLinkedGeo();
		inputBox.updateLinkedGeo("4+5I");
		assertEquals("4 + 5 \\; i", inputBox.getText());
	}

	@Test
	void formulaTextShouldUseRegularIWhenComplex() {
		withComplexLinkedGeo();
		GeoText text = add("FormulaText[InputBox1]");
		assertEquals("3 + 2 \\; i", text.getTextString());
	}

	@Test
	void inputBoxPlusStringShouldUseImaginaryWhenComplex() {
		withComplexLinkedGeo();
		GeoText text = add("InputBox1 + \"\"");
		assertEquals("3 + 2" + Unicode.IMAGINARY, text.getTextString());
	}

	@Test
	void formulaTextOnePlusIShouldUseRegularI() {
		GeoText text = add("FormulaText(1+" + Unicode.IMAGINARY + ")");
		assertEquals("1 + i", text.getTextString());
	}

	@Test
	void addOnePlusIShouldUseImaginary() {
		GeoText text = add("(1 + " + Unicode.IMAGINARY + ") + \"\"");
		assertEquals("(1 + " + Unicode.IMAGINARY + ")", text.getTextString());
	}

	@Test
	void textOnePlusIShouldUseImaginary() {
		GeoText text = add("Text(1+" + Unicode.IMAGINARY + ")");
		assertEquals("1 + " + Unicode.IMAGINARY, text.getTextString());
	}

	@Test
	void functionVariableEShouldStayAsVariable() {
		GeoInputBox inputBox = withLinkedGeo("g(e)", "g", "?");
		GeoNumeric a = add("a = g(1)");
		inputBox.updateLinkedGeo("e");
		assertEquals(1, a.getValue(), 0);
	}

	@Test
	void functionMultiVarIAndEShouldStayVariables() {
		GeoInputBox inputBox = withLinkedGeo("g(e, i, v)", "g", "?");
		GeoNumeric a = add("a = g(1, 2, 3)");
		inputBox.updateLinkedGeo("2e + 3i - 4v");
		assertEquals(-4, a.getValue(), 0);
	}

	@Test
	void functionVariableIShouldStayAsVariable() {
		GeoInputBox inputBox = withLinkedGeo("g(i)", "g", "?");
		GeoNumeric a = add("a = g(1)");
		inputBox.updateLinkedGeo("3i/2");
		assertEquals(1.5, a.getValue(), 0);
	}

	@Test
	@Issue("APPS-7630")
	void rootOfNegativeNumberShouldBeImaginary() {
		add("z_1 = 3 + 2i");
		shouldBeUpdatedAs("sqrt(-25)", "sqrt(-25)");
		assertEquals("5" + Unicode.IMAGINARY,
				lookup("z_1").toValueString(StringTemplate.testTemplate));
	}

	@Test
	@Issue("APPS-7630")
	void rootOfNegativeNumberShouldBeImaginaryInSum() {
		add("z_1 = 3 + 2i");
		shouldBeUpdatedAs("sqrt(-25)+i", "sqrt(-25)+i");
		assertEquals("6" + Unicode.IMAGINARY,
				lookup("z_1").toValueString(StringTemplate.testTemplate));
	}
}
