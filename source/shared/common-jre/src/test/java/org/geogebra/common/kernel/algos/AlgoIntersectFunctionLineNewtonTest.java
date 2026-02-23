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
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link AlgoIntersectFunctionLineNewton}
 */
public class AlgoIntersectFunctionLineNewtonTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupClassicApp();
	}

	@Test
	@Issue("APPS-5509")
	public void highPrecisionRounding() {
		getApp().setRounding("13");
		GeoPoint pt = evaluateGeoElement("Intersect(sin(x deg),y=1)");
		assertEquals("(90, 1)", pt.toValueString(StringTemplate.defaultTemplate));
	}
}
