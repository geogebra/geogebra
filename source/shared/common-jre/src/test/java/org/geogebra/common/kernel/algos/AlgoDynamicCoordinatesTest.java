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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.Test;

public class AlgoDynamicCoordinatesTest extends BaseUnitTest  {

	@Test
	public void testDynamicCoordsCompute() {
		Construction cons = getConstruction();
		GeoPoint I = new GeoPoint(cons, "I", 6, -9, 1);
		GeoPoint J = new GeoPoint(cons, "J", 10, -6, 1);
		AlgoJoinPointsRay j = new AlgoJoinPointsRay(cons, "j", I, J);
		AlgoCirclePointRadius e = new AlgoCirclePointRadius(cons, I, new GeoNumeric(cons, 3));
		AlgoIntersectLineConic intersectionAlgo = new AlgoIntersectLineConic(cons, j.getRay(),
				e.getCircle());
		GeoPoint K = (GeoPoint) intersectionAlgo.getOutput(0);
		K.setLabel("K");
		GeoElementND[] dynCoords = cons.getKernel().getAlgebraProcessor().processAlgebraCommand(
				"DynamicCoordinates(J, x(K), y(K))", false);
		I.setCoords(10, -6, 1);
		cons.updateConstruction(false);
		assertThat(J, isDefined());
		assertThat(dynCoords[0], not(isDefined()));
	}
}
