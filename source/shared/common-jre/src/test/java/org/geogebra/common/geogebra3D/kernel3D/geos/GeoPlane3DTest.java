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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeoPlane3DTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.G3D);
	}

	@Test
	@Issue("APPS-6716")
	public void equationVectorShouldNotChange() {
		GeoPlane3D plane = evaluateGeoElement("Plane((0,0,0),(-1,0,5),(3,2,-1))");
		String expectedCoords = new Coords(-10.0, 14.0, -2.0, -0.0).toString();
		assertEquals(expectedCoords,
				plane.getCoordSys().getEquationVector().toString());
		assertEquals("-5x + 7y - z=0", plane.toValueString(StringTemplate.editorTemplate));
		assertEquals(expectedCoords, plane.getCoordSys().getEquationVector().toString());
	}
}
