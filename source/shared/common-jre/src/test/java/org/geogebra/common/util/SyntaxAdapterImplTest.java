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

package org.geogebra.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

class SyntaxAdapterImplTest {
	private final AppCommon app = AppCommonFactory.create();
	private final SyntaxAdapterImpl syntaxAdapter = new SyntaxAdapterImpl(app.getKernel());

	@Test
	void testConvertLaTeXtoGGB() {
		assertEquals("x^(2)", syntaxAdapter.convertLaTeXtoGGB("x^{2}"));
	}

	@Test
	void testConvert() {
		assertEquals("x^{2}", syntaxAdapter.convert("x^{2}"));
	}

	@Test
	@Issue("APPS-7697")
	void testConvertWithMathML() {
		assertEquals("n!", syntaxAdapter.convert("<math><mrow><mi>n</mi><mo>!</mo></mrow> </math>"));
	}

	@Test
	@Issue("APPS-7697")
	void testConvertWithInvalidXML() {
		assertEquals("<3", syntaxAdapter.convert("<3"));
	}

	@Test
	@Issue("APPS-8094")
	void shouldNotAddParenthesesForMathML() {
		assertEquals(
				"sqrt(5²+2)",
				syntaxAdapter.convert(
						"<math xmlns=http://www.w3.org/1998/Math/MathML><msqrt><msup><mn>5</mn><mn>2</mn></msup><mo>+</mo><mn>2</mn></msqrt></math>"));
	}

	@Test
	@Issue("APPS-8094")
	void shouldNotLoseLabel() {
		assertEquals(
				"k: (5^2)+ 2",
				syntaxAdapter.convert(
						"<math xmlns=http://www.w3.org/1998/Math/MathML><mi>k:</mi><msup><mn>5</mn><mn>2</mn></msup><mo>+</mo><mn>2</mn></math>"));
	}

	@Test
	@Issue("APPS-8094")
	void shouldNotLosePrecision() {
		assertEquals("3.1415926 x", syntaxAdapter.convert("<math><mn>3.1415926</mn><mi>x</mi></math>"));
	}

	@Test
	@Issue("APPS-8094")
	void shouldHandleMissingParts() {
		assertEquals("( x", syntaxAdapter.convert("<math><mo>(</mo><mi>x</mi></math>"));
		assertEquals(
				"(() / 2)",
				syntaxAdapter.convert("<math><mfrac><mrow><mspace/></mrow><mn>2</mn></mfrac></math>"));
	}

	@Test
	@Issue("APPS-8094")
	void shouldNotTranslateNthRoot() {
		app.setLocale(new Locale("de"));
		assertEquals(
				"nroot(x,3)", syntaxAdapter.convert("<math><mroot><mi>x</mi><mn>3</mn></mroot></math>"));
		assertEquals("Gerade(A,B)", syntaxAdapter.convert("<math><mn>Gerade(A,B)</mn></math>"));
		assertEquals("Line(A,B)", syntaxAdapter.convert("<math><mn>Line(A,B)</mn></math>"));
	}

	@Test
	@Issue("APPS-8094")
	void shouldKeepParenthesesAroundMinus() {
		assertEquals(
				"(-2)²", syntaxAdapter.convert("<math><msup><mn>\u22122</mn><mn>2</mn></msup></math>"));
		assertEquals("(-2)²", syntaxAdapter.convert("<math><msup><mn>-2</mn><mn>2</mn></msup></math>"));
	}
}
