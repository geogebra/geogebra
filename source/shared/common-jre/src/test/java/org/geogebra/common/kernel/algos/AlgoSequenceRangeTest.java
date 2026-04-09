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

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlgoSequenceRangeTest extends BaseAppTestSetup {

	@BeforeEach
	public void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testRounding() {
		getApp().setRounding("2d");
		assertEquals("{-0.1, -0.1, -0.09, -0.09, -0.08, -0.08, -0.07, -0.07, -0.06, -0.06,"
				+ " -0.05, -0.05, -0.04, -0.04, -0.03, -0.03, -0.02, -0.02, -0.01, -0.01, 0, 0.01,"
				+ " 0.01, 0.02, 0.02, 0.03, 0.03, 0.04, 0.04, 0.05, 0.05, 0.06, 0.06, 0.07, 0.07,"
				+ " 0.08, 0.08, 0.09, 0.09, 0.1, 0.1}",
				evaluateGeoElement("Sequence(-0.1,0.1,.005)").toValueString(
						StringTemplate.defaultTemplate));
		assertEquals("{0.1, 0.1, 0.09, 0.09, 0.08, 0.08, 0.07, 0.07, 0.06, 0.06, 0.05,"
				+ " 0.05, 0.04, 0.04, 0.03, 0.03, 0.02, 0.02, 0.01, 0.01, 0, -0.01, -0.01, -0.02,"
				+ " -0.02, -0.03, -0.03, -0.04, -0.04, -0.05, -0.05, -0.06, -0.06, -0.07, -0.07,"
				+ " -0.08, -0.08, -0.09, -0.09, -0.1, -0.1}",
				evaluateGeoElement("Sequence(0.1,-0.1,-.005)").toValueString(
						StringTemplate.defaultTemplate));
	}

	@Test
	public void testDegenerateCases() {
		assertEquals("{1}", evaluateGeoElement("Sequence(1,1,1)")
				.toValueString(StringTemplate.testTemplate));
		assertEquals("?", evaluateGeoElement("Sequence(1,1,-1)")
				.toValueString(StringTemplate.testTemplate));
	}
}
