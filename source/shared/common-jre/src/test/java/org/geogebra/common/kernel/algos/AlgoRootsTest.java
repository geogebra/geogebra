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
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class AlgoRootsTest extends BaseAppTestSetup {
	@Test
	public void intersectLine() {
		setupApp(SuiteSubApp.G3D);
		GeoElementND element =
				evaluateGeoElement("Intersect(-2 x^(3)+7 x^(2)-2 x-3,4.5 x+y=15.75,-3,0)");
		assertEquals("(-1.5, 22.5)", element.toValueString(StringTemplate.testTemplate));
	}
}
