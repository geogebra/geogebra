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

import org.geogebra.common.AppCommonFactory;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

class SyntaxAdapterImplTest {

	SyntaxAdapterImpl syntaxAdapter = new SyntaxAdapterImpl(AppCommonFactory.create().getKernel());

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
		assertEquals("n ! ",
				syntaxAdapter.convert("<math><mrow><mi>n</mi><mo>!</mo></mrow> </math>"));
	}

	@Test
	@Issue("APPS-7697")
	void testConvertWithInvalidXML() {
		assertEquals("<3", syntaxAdapter.convert("<3"));
	}
}
