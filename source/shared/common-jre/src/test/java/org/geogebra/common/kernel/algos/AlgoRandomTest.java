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
 * See https://www.geogebra.org/license for full licensing details'
 */

package org.geogebra.common.kernel.algos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlgoRandomTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testOverflow() {
		GeoNumeric element = evaluateGeoElement("RandomBetween(0, 9999999999)");
		assertTrue(element.getValue() > 0, "Random number should be positive");
		element = evaluateGeoElement("RandomBetween(-9999999999, 9999999999)");
		assertTrue(Double.isFinite(element.getValue()), "Random number should be defined");
		element = evaluateGeoElement("RandomBetween(-9999999999, 0)");
		assertTrue(element.getValue() < 0, "Random number should be negative");
		element = evaluateGeoElement("RandomBetween(42, 42)");
		assertEquals(42, element.getValue(), 0.0);
	}
}
