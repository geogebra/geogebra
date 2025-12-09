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

package org.geogebra.common.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class CoordsTest {

	@Test
	public void testProduct() {
		Coords v1 = new Coords(2);
		v1.val[0] = 3.0;
		v1.val[1] = 4.0;

		assertEquals(v1.dotproduct(v1), 25, 1E-8);
	}

	@Test
	public void testToString() {
		FormatFactory.setPrototypeIfNull(new FormatFactoryJre());
		Coords v1 = new Coords(4);
		v1.set(.5, .31, -.17);
		assertEquals(v1.toString(2), "(+0.50  +0.31  -0.17  +0.00)");
	}

	@Test
	public void testProjectPlaneToHorizontalShouldKeepZCoord() {
		Coords origin = new Coords(1, 2, 3, 1);
		Coords output = new Coords(4);
		origin.projectPlaneThruV(CoordMatrix4x4.identity(),
				new Coords(89.13, 14.19, 17.21),
				output);

		assertEquals(0, output.getZ(), 1E-18);
	}
}
