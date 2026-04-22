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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class AlgoAngleLinePlaneTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	@Issue("APPS-7420")
	public void sequenceOfLinePlaneAnglesShouldProvideDrawCoords() {
		add("l: Line((0,0,0), (1,1,1))");
		GeoList list = add("Sequence(Angle(l, xOyPlane), i, 1, 1)");

		AlgoAngleLinePlane drawAlgo = (AlgoAngleLinePlane) list.get(0).getDrawAlgorithm();
		assertNotNull(drawAlgo);

		Coords[] drawCoords = {new Coords(4), new Coords(4), new Coords(4)};
		assertTrue(drawAlgo.getCoordsInD3(drawCoords));
	}
}
