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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AlgoJoinPointsTest extends BaseAppTestSetup {

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@ParameterizedTest
	@ValueSource(doubles = {1E4, 1E-4})
	@Issue("APPS-7424")
	void iteratedIntersectionShouldNotOverflow(double side) {
		evaluate("A=(0,0)");
		evaluate("B=(" + side + ",0)");
		evaluate("C=(0," + side + ")");
		evaluate("C1=C/2");
		evaluate("g:Line(B,C)");
		for (int step = 1; step < 50; step++) {
			evaluate("B" + step + "=Intersect(yAxis,Line(C" + step + ",g))");
			evaluate("A" + step + "=Intersect(g,Line(B" + step + ",xAxis))");
			GeoPoint next =
					evaluateGeoElement("C" + (step + 1) + "=Intersect(xAxis,Line(A" + step + ",yAxis))");
			assertTrue(
					Double.isFinite(next.getZ()), "Iteration " + step + ": coordinates should be finite");
		}
		GeoPolygon triangle = evaluateGeoElement("Polygon(A50,B50,C50)");
		assertEquals(side * side / 8, triangle.getArea(), Kernel.STANDARD_PRECISION);
	}
}
