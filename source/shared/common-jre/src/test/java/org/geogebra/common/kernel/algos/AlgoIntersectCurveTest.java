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

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class AlgoIntersectCurveTest extends BaseUnitTest {
	@Test
	public void intersectShouldNotMissPoints() {
		add("L=(1.09397,0.2794)");
		add("M=(1.09295,0.15536)");
		add("N=(0.67609,-0.44451)");
		add("O=(0.88147,-0.79426)");
		add("g=Spline({L,M,N,O},3)");
		add("f=Segment((0,y(L)),(0,y(O)))");
		add("pp = 0.301");
		add("C = Point(f, pp)");
		add("h = PerpendicularLine(C,f)");
		GeoElement T = add("Intersect(h,g)");
		getConstruction().updateAllAlgorithms();
		T.update(false);
		assertThat(T, isDefined());
	}

	@Test
	public void intersectShouldNotMissPointsSimplified() {
		add("L=(1.09397,0.2794)");
		add("M=(1.09295,0.15536)");
		add("N=(0.67609,-0.44451)");
		add("O=(0.88147,-0.79426)");
		add("g=Spline({L,M,N,O},3)");
		add("h: y = -0.0438");
		GeoElement T = add("Intersect(h,g)");
		assertThat(T, isDefined());
	}

	@Test
	public void intersectShouldBeOnSpline() {
		add("L=(1.09397,0.2794)");
		add("M=(1.09295,0.15536)");
		add("N=(0.67609,-0.44451)");
		add("O=(0.88147,-0.79426)");
		add("g=Spline({L,M,N,O},3)");
		add("h: y = -0.398");
		GeoElement T = add("Intersect(h,g)");
		assertThat(T, hasValue("(0.68, -0.4)"));
	}

	@Test
	public void testSplineWithOneLine() {
		add("a = Spline({(1,5),(2,4),(1,3),(2,2)},3)");
		add("f:y=3.26");
		add("Intersect(a, f)");
		assertThat(lookup("A"), hasValue("(1.2, 3.26)"));

	}

	@Test
	public void testSplineLineIntersectionAtEndPoint0() {
		add("a = Spline({(1,5),(2,4),(1,3),(1,2)},3)");
		add("f:y=5");
		add("Intersect(a, f)");
		assertThat(lookup("A"), hasValue("(1, 5)"));
	}

	@Test
	public void testSplineLineIntersectionAtEndPoint1() {
		add("a = Spline({(1,5),(2,4),(1,3),(1,2)},3)");
		add("f:y=2");
		add("Intersect(a, f)");
		assertThat(lookup("A"), hasValue("(1, 2)"));
	}

}
