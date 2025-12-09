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
 
package org.geogebra.common.kernel.geos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class GeoPolygonTest extends BaseUnitTest {

	@Test
	public void testMaskProperties() {
		add("A=(-1,1)");
		add("B=(-1,-1)");
		add("C=(1,-1)");
		add("D=(1,1)");
		GeoPolygon polygon = add("Polygon(A,B,C,D)");

		assertTrue(polygon.isFillable());
		assertTrue(polygon.isTraceable());
		assertTrue(polygon.showLineProperties());

		polygon.setIsMask(true);
		assertFalse(polygon.isFillable());
		assertFalse(polygon.isTraceable());
		assertFalse(polygon.showLineProperties());
	}

	@Test
	public void testPointOnPathWithoutSegments() {
		add("A=(0, 5)");
		add("B=(-5, 0)");
		add("C=(5, 2.5)");
		add("t1=Polygon(A, B, C)");
		add("l1=Sequence(Polygon(Translate(A,Vector(k (1,0))),"
				+ "Translate(B,Vector(k (1,0))),Translate(C,Vector(k (1,0)))),k,1,3)");
		add("D=Point(l1(1))");
		assertThat(lookup("D"), hasValue("(-0.24, 0.94)"));
	}
}
