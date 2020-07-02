package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoInputBoxForComplexTest extends BaseUnitTest {

	public static final String IMAGINARY_UNIT = "i";

	@Test
	public void rootOfMinusOneShouldBeImaginaryWithComplexNumber() {
		add("z_1 = 3 + 2i");
		shouldBeUpdatedAs("sqrt(-1)", IMAGINARY_UNIT);
	}

	@Test
	public void imaginaryUnitShouldOverrideUserDefinedVarForPoints() {
		add("z_1 = 3 + 2i");
		add("i = 7");
		shouldBeUpdatedAs("2i", "2" + IMAGINARY_UNIT);
		assertEquals("0 + 2i",
				lookup("z_1").toValueString(StringTemplate.latexTemplate));
	}

	@Test
	public void userDefinedVarShouldOverrideImaginaryUnitForNumbers() {
		add("i = 7");
		add("z_1 = 3 + 2i");
		shouldBeUpdatedAs("2i", "2" + IMAGINARY_UNIT);
		assertEquals("14",
				lookup("z_1").toValueString(StringTemplate.latexTemplate));
	}

	@Test
	public void rootOfMinusOneShouldBeUsedInExpression() {
		add("z_1 = 1 + 6i");
		shouldBeUpdatedAs("2 + 3sqrt(-1)", "2 + 3" + IMAGINARY_UNIT);
	}

	@Test
	public void sinShouldBeTyped() {
		add("z_1 = 3+2i");
		shouldBeUpdatedAs("sin45", "sin(45)");
	}

	private void shouldBeUpdatedAs(String updatedText, String expected) {
		GeoInputBox inputBox = addAvInput("ib = InputBox(z_1)");
		inputBox.updateLinkedGeo(updatedText);
		assertEquals(expected, inputBox.getText());
	}

	@Test
	public void testImaginaryShouldRenderedAsRegularI() {
		GeoInputBox inputBox = withComplexLinkedGeo();
		assertEquals("3 + 2 \\; i", inputBox.getText());
	}

	protected GeoInputBox withComplexLinkedGeo() {
		add("z_1 = 3+2i");
		GeoInputBox inputBox = add("InputBox(z_1)");
		inputBox.setSymbolicMode(true);
		return inputBox;
	}

	@Test
	public void testImaginaryShouldEditedAsRegularI() {
		GeoInputBox inputBox = withComplexLinkedGeo();
		assertEquals("3+2 i", inputBox.getTextForEditor());
	}

	@Test
	public void testOnUpdateImaginaryShouldBeUsed() {
		GeoInputBox inputBox = withComplexLinkedGeo();
		inputBox.updateLinkedGeo("4 + 5" + Unicode.IMAGINARY);
		assertEquals("4 + 5 \\; i", inputBox.getText());
		assertEquals("4+5 i", inputBox.getTextForEditor());
	}

	@Test
	public void capitalIShouldBeSmallIWhenComplex() {
		GeoInputBox inputBox = withComplexLinkedGeo();
		inputBox.updateLinkedGeo("4+5I");
		assertEquals("4 + 5 \\; i", inputBox.getText());
	}

	@Test
	public void formulaTextShouldUseRegularIWhenComplex() {
		GeoInputBox inputBox = withComplexLinkedGeo();
		GeoText text = add("FormulaText[InputBox1]");
		assertEquals("3 + 2 \\; i", text.getTextString());
	}

	@Test
	public void inputBoxPlusStringShouldUseImaginaryWhenComplex() {
		GeoInputBox inputBox = withComplexLinkedGeo();
		GeoText text = add("InputBox1 + \"\"");
		assertEquals("3 + 2" + Unicode.IMAGINARY, text.getTextString());
	}

	@Test
	public void formulaTextOnePlusIShouldUseRegularI() {
		GeoText text = add("FormulaText(1+" + Unicode.IMAGINARY + ")");
		assertEquals("1 + i", text.getTextString());
	}

	@Test
	public void addOnePlusIShouldUseImaginary() {
		GeoText text = add("(1 + " + Unicode.IMAGINARY + ") + \"\"");
		assertEquals("(1 + " + Unicode.IMAGINARY + ")", text.getTextString());
	}

	@Test
	public void textOnePlusIShouldUseImaginary() {
		GeoText text = add("Text(1+" + Unicode.IMAGINARY + ")");
		assertEquals("1 + " + Unicode.IMAGINARY, text.getTextString());
	}
}
