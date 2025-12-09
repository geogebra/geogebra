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

package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeoPolyhedronTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.G3D);
	}

	@Test
	public void pointOnPolyhedron() {
		evaluate("a=Cube(O,(0,1,0))");
		GeoNumeric pointX = evaluateGeoElement("x(Point(a,0.2))");
		Assertions.assertEquals(-0.4, pointX.getValue(), Kernel.STANDARD_PRECISION);
	}
}
