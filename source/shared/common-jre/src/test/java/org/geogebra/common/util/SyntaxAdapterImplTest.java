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

import static org.junit.Assert.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.junit.Test;

public class SyntaxAdapterImplTest {

	SyntaxAdapterImpl syntaxAdapter = new SyntaxAdapterImpl(AppCommonFactory.create().getKernel());

	@Test
	public void testConvertLaTeXtoGGB() {
		assertEquals("x^(2)", syntaxAdapter.convertLaTeXtoGGB("x^{2}"));
	}

	@Test
	public void testConvert() {
		assertEquals("x^{2}", syntaxAdapter.convert("x^{2}"));
	}
}