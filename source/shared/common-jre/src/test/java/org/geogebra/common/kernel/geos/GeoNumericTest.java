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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.util.StringUtil;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeoNumericTest extends BaseAppTestSetup {

	private StringTemplate scientificTemplate;
	private StringTemplate engineeringNotationTemplate;

	@BeforeEach
	public void setup() {
		setupClassicApp();
		scientificTemplate = StringTemplate.printFigures(StringType.GEOGEBRA, 3, false);
		engineeringNotationTemplate = StringTemplate.defaultTemplate
				.deriveWithEngineeringNotation();
	}

	@Test
	public void euclidianShowabilityOfOperationResult() {
		GeoNumeric numeric = addAvInput("4+6");
		assertThat(numeric.isEuclidianShowable(), is(false));
	}

	@Test
	public void testFractionRounding() {
		GeoNumeric numeric = addAvInput("6048 * (1/3)");
		assertThat(numeric.toValueString(StringTemplate.maxDecimals), is("2016"));
		GeoNumeric nearFraction = addAvInput("(0.99999999/2)+1");
		assertThat(nearFraction.toValueString(StringTemplate.maxDecimals), is("1.499999995"));
	}

	@Test
	public void testNumericIsNotDrawableInCas() {
		getApp().setConfig(new AppConfigCas());
		GeoNumeric numeric = addAvInput("2");
		assertThat(numeric.isEuclidianShowable(), is(false));
	}

	@Test
	public void testSliderIsVisibleInEv() {
		GeoNumeric numeric = new GeoNumeric(getKernel().getConstruction());
		GeoNumeric.setSliderFromDefault(numeric, false);
		assertThat(numeric.isEuclidianShowable(), is(true));
	}

	@Test
	public void getLaTeXDescriptionRHS() {
		GeoNumeric numeric = addAvInput("1/2");

		String descriptionRHS =
				numeric.getLaTeXDescriptionRHS(
						true, StringTemplate.latexTemplate);
		assertThat(descriptionRHS, equalTo("0.5"));

		descriptionRHS =
				numeric.getLaTeXDescriptionRHS(
						false, StringTemplate.latexTemplate);
		assertThat(descriptionRHS, equalTo("\\frac{1}{2}"));
	}

	@Test
	public void getLaTeXAlgebraDescription() {
		GeoNumeric numeric = addAvInput("a = 1/2");

		String description =
				numeric.getLaTeXAlgebraDescription(
						true, StringTemplate.latexTemplate);
		assertThat(description, equalTo("a\\, = \\,0.5"));

		description =
				numeric.getLaTeXAlgebraDescription(
						false, StringTemplate.latexTemplate);
		assertThat(description, equalTo("a\\, = \\,\\frac{1}{2}"));
	}

	@Test
	public void testCopy() {
		GeoNumeric numeric = addAvInput("1");
		numeric.setDrawable(true, false);
		GeoNumeric copy = numeric.copy();
		assertThat(copy.isDrawable, is(true));
	}

	@Test
	public void sliderTagShouldStayInXmlAfterSetUndefined() {
		GeoNumeric slider = evaluateGeoElement("sl=Slider(0,1,.1)");
		assertThat(slider.isEuclidianVisible(), is(true));
		evaluate("SetValue(sl,?)");
		slider.setDefinition(new ExpressionNode(getKernel(), Double.NaN));
		assertThat(slider.isEuclidianVisible(), is(false));
		String xml = slider.getXML();
		assertThat(xml, containsString("<slider"));
	}

	@Test
	public void undefinedSliderShouldBeSliderableAfterReload() {
		GeoNumeric slider = evaluateGeoElement("sl=Slider(0,1,.1)");
		evaluate("SetValue(sl,?)");
		slider.setDefinition(new ExpressionNode(getKernel(), Double.NaN));
		getApp().setXML(getApp().getXML(), true);
		GeoElement reloaded = lookup("sl");
		evaluate("SetValue(sl,.5)");
		assertThat(slider.isEuclidianVisible(), is(false));
		assertThat(((GeoNumeric) reloaded).isSliderable(), is(true));
	}

	@Test
	public void testRecurringSwitchSymbolic() {
		GeoNumeric recurring = addAvInput("1.2\u03053\u0305");
		assertThat(recurring.getDefinition().unwrap().isRecurringDecimal(), is(true));
		recurring.setSymbolicMode(false, true);
		assertThat(recurring.toValueString(StringTemplate.maxDecimals),
				is("1.232323232323232"));
		recurring.setSymbolicMode(true, true);
		assertThat(recurring.toValueString(StringTemplate.maxDecimals),
				is("122 / 99"));
	}

	@Test
	public void testIsRecurringDecimal() {
		assertThat(evaluateGeoElement("1.2\u03053\u0305").isRecurringDecimal(), is(true));
		assertThat(evaluateGeoElement("1.234").isRecurringDecimal(), is(false));
		assertThat(evaluateGeoElement("12 / 34").isRecurringDecimal(), is(false));
	}

	@Test
	public void testAsRecurringDecimal() {
		assertNotNull(evaluateGeoElement("1.02\u03053\u0305", GeoNumeric.class)
				.asRecurringDecimal());
		assertNull(evaluateGeoElement("1.234", GeoNumeric.class).asRecurringDecimal());
		assertNull(evaluateGeoElement("12 / 34", GeoNumeric.class).asRecurringDecimal());
	}

	@Test
	public void testFormulaString() {
		GeoNumeric recurring = evaluateGeoElement("1 + 0.3\u0305");
		StringTemplate tpl = StringTemplate.defaultTemplate;
		assertThat(recurring.getFormulaString(tpl, true), is("4 / 3"));
		assertThat(recurring.getFormulaString(tpl, false), is("1 + 0.3\u0305"));
		recurring.setSymbolicMode(false, true);
		assertThat(recurring.getFormulaString(tpl, true), is("1.33"));
		assertThat(recurring.getFormulaString(tpl, false), is("1 + 0.3\u0305"));
	}

	@Test
	@Issue("APPS-5531")
	public void shouldKeepTrailingZeros() {
		GeoNumeric withTrailing = addAvInput("1.20");
		assertThat(withTrailing.getXML(), containsString("1.20"));
		withTrailing.setEuclidianVisible(true);
		assertThat(withTrailing.getXML(), containsString("1.20"));
	}

	@Test
	@Issue("APPS-5699")
	public void shouldKeepTrailingZerosForIntegers() {
		GeoNumeric withTrailing = addAvInput("1.00");
		assertThat(withTrailing.getXML(), containsString("1.00"));
	}

	@Test
	@Issue("APPS-5531")
	public void shouldKeepENotation() {
		GeoNumeric withTrailing = addAvInput("1.20E3");
		assertThat(withTrailing.getXML(), containsString("1.20E3"));
	}

	@Test
	@Issue("APPS-1889")
	public void shouldNotStoreStyleIfNotInitialized() {
		addAvInput("a=3");
		getApp().setXML(getApp().getXML(), true);
		GeoElement slider = lookup("a");
		slider.setEuclidianVisible(true);
		slider.updateRepaint();
		assertThat(slider.getLineThickness(), is(10));
	}

	@Test
	public void testAutoCreatedSliderAlgebraVisibility() {
		assertThat(((GeoNumeric) evaluateWithSliders("a"))
				.isAVSliderOrCheckboxVisible(), equalTo(true));
		assertThat(((GeoNumeric) evaluateWithSliders("3"))
				.isAVSliderOrCheckboxVisible(), equalTo(false));
	}

	@Test
	public void shouldPrintUnicodePowerOf10() {
		GeoNumeric a = addAvInput("a=1E30+1E30");
		assertThat(a.toValueString(scientificTemplate),
				is("2.00 " + Unicode.CENTER_DOT + " 10" + StringUtil.numberToIndex(30)));
	}

	@Test
	public void shouldPrintUnicodeNegativePowerOf10() {
		GeoNumeric a = addAvInput("a=1E-30+1E-30");
		assertThat(a.toValueString(scientificTemplate),
				is("2.00 " + Unicode.CENTER_DOT + " 10" + StringUtil.numberToIndex(-30)));
	}

	@Test
	public void geoNumericShouldDisplayCorrectEngineeringNotation1() {
		GeoNumeric a = addAvInput("7344000");
		assertThat(a.toValueString(engineeringNotationTemplate),
				is("7.34 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_6));
	}

	@Test
	public void geoNumericShouldDisplayCorrectEngineeringNotation2() {
		GeoNumeric a = addAvInput("7 / 2");
		assertThat(a.toValueString(engineeringNotationTemplate),
				is("3.5 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_0));
	}

	@Test
	public void geoNumericShouldDisplayCorrectEngineeringNotation3() {
		GeoNumeric a = addAvInput("?");
		assertThat(a.toValueString(engineeringNotationTemplate), is("?"));
	}

	@Test
	public void shouldNotLoadValueFromXML() {
		getApp().getGgbApi().evalXML("<expression exp=\"2+2\" label=\"a\"/>"
				+ "<element label=\"a\" type=\"numeric\"><value val=\"5\"/></element>");
		assertEquals("4", lookup("a").toValueString(StringTemplate.testTemplate));
	}

	@Test
	public void sliderShouldSaveVariableToXMLWhenMinIsGreaterOrEqualToMax() {
		addAvInput("a = Slider(1, 10)");
		addAvInput("b = Slider(2, a)");
		assertThat(getApp().getXML(), containsString("<slider min=\"2\" max=\"a\""));

		addAvInput("a = 2");
		assertThat(getApp().getXML(), containsString("<slider min=\"2\" max=\"a\""));

		addAvInput("a = 1");
		assertThat(getApp().getXML(), containsString("<slider min=\"2\" max=\"a\""));
	}

	@Test
	public void sliderShouldBeUndefinedWhenMinIsGreaterOrEqualToMax() {
		addAvInput("a = Slider(1, 10)");
		GeoNumeric slider = addAvInput("b = Slider(2, a)");
		assertFalse(slider.isDefined(), "slider should not be defined");

		addAvInput("a = 2");
		assertTrue(slider.isDefined(), "slider should be defined");
		assertFalse(slider.isEuclidianVisible(), "slider should not be visible");

		addAvInput("a = 3");
		assertTrue(slider.isDefined(), "slider should be defined again");
	}

	private GeoNumeric addAvInput(String s) {
		return evaluateGeoElement(s);
	}

	private GeoElementND evaluateWithSliders(String expression) {
		EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForAV(getApp(), true);
		return getKernel().getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
				expression, false, TestErrorHandler.WITH_SLIDERS, evalInfo, null)[0];
	}
}