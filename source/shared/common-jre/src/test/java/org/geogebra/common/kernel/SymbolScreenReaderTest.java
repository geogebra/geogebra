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

package org.geogebra.common.kernel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.ScreenReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

class SymbolScreenReaderTest extends BaseUnitTest {

	/**
	 * Setup LaTeX
	 */
	@BeforeAll
	static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@Test
	void testDedicatedPrimes() {
		add("f = x");
		shouldRead("f'", readFunction("f prime"));
		shouldRead("f''", readFunction("f double prime"));
		shouldRead("f'''", readFunction("f triple prime"));
	}

	private void shouldRead(String definition, String expected) {
		GeoElement geo = add(definition);
		shouldRead(geo, expected);
	}

	private void shouldRead(GeoElement geo, String expected) {
		String actual = ScreenReader.getAuralText(geo,
				new ScreenReaderBuilder(geo.getKernel().getLocalization()));
		assertEquals(expected, actual);
	}

	private void shouldRead(GeoText geo, String expected) {
		String actual = ScreenReader.getAuralText(geo,
				new ScreenReaderBuilder(geo.getKernel().getLocalization()));
		assertEquals(expected, actual);
	}

	private String readFunction(String prime) {
		return "Function " + prime + " Press enter to edit Press tab to select next object";
	}

	@Test
	void testManyPrimes() {
		add("f = x");
		shouldRead("f''''", readFunction("f prime prime prime prime"));
	}

	@Test
	void testSliderDegreesPlural() {
		GeoNumeric slider = addSlider("α = 10°");
		shouldRead(slider, readSlider("Slider alpha = 10 degrees"));
	}

	private GeoNumeric addSlider(String definition) {
		EvalInfo info = EvalInfoFactory.getEvalInfoForAV(getApp(), true);
		GeoNumeric slider = add(definition, info);
		slider.createSlider();
		return slider;
	}

	@Test
	void testSliderDegreeSingle() {
		GeoNumeric slider = addSlider("α = 1°");
		shouldRead(slider, readSlider("Slider alpha = 1 degree"));
		slider.setValue(0);
		shouldRead(slider, readSliderUpOnly("Slider alpha = 0 degree"));
	}

	private String readSlider(String slider) {
		return slider
				+ " Press space to start animation Press up arrow to increase the value"
				+ " Press down arrow to decrease the value"
				+ " Press enter to edit Press tab to select next object";
	}

	private String readSliderUpOnly(String slider) {
		return slider
				+ " Press space to start animation"
				+ " Press up arrow to increase the value"
				+ " Press enter to edit Press tab to select next object";
	}

	@Test
	void testSliderDegreeCaption() {
		GeoNumeric slider = addSlider("a = 10");
		slider.setCaption("$β=%v°$");
		shouldRead(slider, readSlider("beta=10 degrees"));
		slider.setValue(-5);
		shouldRead(slider, readSliderUpOnly("beta= minus 5 degrees"));
	}

	@Test
	void testOneDegreeInGeoText() {
		shouldRead(this.<GeoText>add("\"sin(y)=1°\""),
				"sin open parenthesis y close parenthesis =1 degree"
						+ " Press enter to edit Press tab to select next object");
	}

	@Test
	void testMinusOneDegreeInGeoText() {
		shouldRead(this.<GeoText>add("\"sin(y)=-1°\""),
				"sin open parenthesis y close parenthesis = minus 1 degree"
				+ " Press enter to edit Press tab to select next object");
	}

	@Test
	void testDegreeInGeoTextPlurar() {
		shouldRead(this.<GeoText>add("\"sin(y)=35°\""),
				"sin open parenthesis y close parenthesis =35 degrees"
				+ " Press enter to edit Press tab to select next object");
	}

	@Test
	void testDegreeInLabel() {
		shouldRead((GeoText) add("\"15°=(1,1)\""),
				"15 degrees = open parenthesis 1 comma 1 close parenthesis"
						+ " Press enter to edit Press tab to select next object");
	}

	@Test
	void testDegreeAsLabel() {
		shouldRead((GeoText) add("\"°=(1,1)\""), " degree = open parenthesis"
				+ " 1 comma 1 close parenthesis Press enter to edit"
				+ " Press tab to select next object");
	}

	@Test
	void testDegreeInGeoTextWithEqPlurar() {
		GeoText text = add("text1 = \"sin(x)=75°\"");
		text.setLaTeX(true, false);
		shouldRead(text,
				"sin open parenthesis x close parenthesis =75 degrees"
				+ " Press enter to edit Press tab to select next object");
	}

	@Test
	void testArcSinLatex() {
		atMinus1ShouldReadAsArc("sin");
	}

	@Test
	void testArcCosLatex() {
		atMinus1ShouldReadAsArc("cos");
	}

	@Test
	void testArcTanLatex() {
		atMinus1ShouldReadAsArc("tan");
	}

	@Test
	void testArcCotLatex() {
		atMinus1ShouldReadAsArc("cot");
	}

	@Test
	void testArcCSCLatex() {
		atMinus1ShouldReadAsArc("sin");
	}

	@Test
	void testArcHyperbolicSinLatex() {
		atMinus1ShouldReadAsArcHyperbolic("sinh");
	}

	@Test
	void testArcHyperbolicCosLatex() {
		atMinus1ShouldReadAsArcHyperbolic("cosh");
	}

	@Test
	void testArcHyperbolicTanLatex() {
		atMinus1ShouldReadAsArcHyperbolic("tanh");
	}

	@Test
	void testArcHyperbolicCotLatex() {
		atMinus1ShouldReadAsArcHyperbolic("coth");
	}

	@Test
	void testArcHyperbolicSecLatex() {
		atMinus1ShouldReadAsArcHyperbolic("sech");
	}

	@Test
	void testArcHyperbolicCscLatex() {
		atMinus1ShouldReadAsArcHyperbolic("csch");
	}

	private void atMinus1ShouldReadAsArc(String trigonometric) {
		GeoText geo = add("\"\\" + trigonometric + "^{-1}(x)\"");
		geo.setLaTeX(true, true);
		shouldRead(geo, " arc " + trigonometric + " open parenthesis x close parenthesis"
				+ " Press enter to edit Press tab to select next object");
	}

	private void atMinus1ShouldReadAsArcHyperbolic(String trigonometric) {
		GeoText geo = add("\"\\" + trigonometric + "^{-1}(x)\"");
		geo.setLaTeX(true, true);
		shouldRead(geo, " arc hyperbolic " + trigonometric.replace("h", "")
				+ " open parenthesis x close parenthesis"
				+ " Press enter to edit Press tab to select next object");
	}

	@Test
	void testPointWithDegrees() {
		GeoPoint point = add("A=(1; 15°)");
		shouldRead(point, "Point A = open parenthesis 1 semicolon "
				+ " 15 degrees close parenthesis"
				+ " Press the arrow keys to move the object"
				+ " Press enter to edit Press tab to select next object");
	}

	@Test
	void testPointMoved() {
		GeoPoint point = add("A=(1, 1)");
		point.movePoint(new Coords(1, 1, 0), null);
		assertEquals("Point A  moved to  open parenthesis 2 comma 2 close parenthesis ",
				point.getAuralTextForMove());
	}

	@Test
	void testPointMovedWithCaption() {
		GeoPoint point = add("A=(1, 1)");
		point.setCaption("$%v\\%$");
		point.movePoint(new Coords(1, 1, 0), null);
		assertEquals(" open parenthesis 2 comma  2 close parenthesis % ",
				point.getAuralTextForMove());
	}

	@Test
	void testPointMovedWithDegrees() {
		GeoPoint point = add("A=(1; 15°)");
		GeoPoint endPosition = add("(2; 30°)");
		point.movePoint(null, endPosition.getCoords());
		assertEquals("Point A  moved to  open parenthesis 2 semicolon  30 degrees"
						+ " close parenthesis ", point.getAuralTextForMove());
	}

	@Test
	void testSinSquared() {
		GeoText text = add("\"sin^2\"");
		text.setLaTeX(true, true);
		shouldRead(text, "sin squared Press enter to edit Press tab to select next object");
	}

	@Test
	void testSinCubed() {
		GeoText text = add("\"sin^3\"");
		text.setLaTeX(true, true);
		shouldRead(text, "sin cubed Press enter to edit Press tab to select next object");
	}

	@Test
	void testSin4() {
		GeoText text = add("\"sin^4\"");
		text.setLaTeX(true, true);
		shouldRead(text, "sin to the power of 4 end power "
				+ "Press enter to edit Press tab to select next object");
	}
}
