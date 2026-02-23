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

package org.geogebra.common.kernel.algos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link AlgoRootsPolynomial}.
 */
public class AlgoRootsPolynomialTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupClassicApp();
	}

	@Test
	public void rationalFunctions() {
		getApp().setRounding("13");
		GeoElementND[] pt = evaluate("Intersect(2.5-x, 1/x)");
		assertEquals("(0.5, 2)", pt[0].toValueString(StringTemplate.defaultTemplate));
		assertEquals("(2, 0.5)", pt[1].toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	public void rationalFunctionsDiscontinuity() {
		getApp().setRounding("13");
		GeoElementND[] pt = evaluate("Intersect(x/x, x+1)");
		assertEquals("(?, ?)", pt[0].toValueString(StringTemplate.defaultTemplate));
		pt = evaluate("Intersect((x^2-1)/(x-1), 0x)");
		assertEquals(1, pt.length);
		assertEquals("(-1, 0)", pt[0].toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	public void rationalFunctionLine() {
		getApp().setRounding("13");
		GeoElementND[] pt = evaluate("Intersect(x+y=2.5, 1/x)");
		assertEquals("(0.5, 2)", pt[0].toValueString(StringTemplate.defaultTemplate));
		assertEquals("(2, 0.5)", pt[1].toValueString(StringTemplate.defaultTemplate));
	}
}
