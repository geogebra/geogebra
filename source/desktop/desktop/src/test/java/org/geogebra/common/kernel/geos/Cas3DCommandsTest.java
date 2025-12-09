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

import static org.geogebra.common.BaseUnitTest.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.junit.Before;
import org.junit.Test;

public class Cas3DCommandsTest extends BaseSymbolicTest {

	private GeoGebraCAS geoGebraCAS;

	@Before
	public void setUp() {
		geoGebraCAS = new GeoGebraCAS(kernel);
	}

	@Test
	public void testDistancePointAndLine3D() {
		GeoSymbolic point3D = add("A := (6, 7, -3)");
		GeoSymbolic line3D = add("g(t):=(2,1,4) + t*(3,0,-2)");
		add("a:=Distance(A, g)");
		GeoElement res = add("Extremum(a)");
		assertThat(res, hasValue("{(2, 7)}"));
		ArrayList<ExpressionNode> args = new ArrayList<>(Arrays.asList(
				point3D.wrap(), line3D.wrap()));

		assertEquals("[[[ggbdisans:=0/0],[ggbdisarg01:=point(6,7,-3)],[ggbdisarg11:=point("
						+ "(2)+((3)*(ggbtmpvart)),1,(4)-((2)*(ggbtmpvart)))],[ggbdisans:="
						+ "when(ggbdisarg01[0]!='pnt',undef,when(type(evalf(ggbdisarg11))"
						+ "==DOM\\_FLOAT,undef,regroup(distance(ggbdisarg01,"
						+ "when(ggbdisarg11[0]!='pnt'&&ggbdisarg11[0]!=equal,y=ggbdisarg11,"
						+ "when(count\\_eq(z,lname(ggbdisarg11))==0,"
						+ "ggbdisarg11,plane(ggbdisarg11)))))))]],when(lname(ggbdisans)=={},"
						+ "normal(ggbdisans),ggbdisans)][1]",
				geoGebraCAS.getCASCommand("Distance",
				args, false, StringTemplate.giacTemplate, SymbolicMode.NONE));
	}

	@Test
	public void testDistanceWithVector() {
		GeoSymbolic vec1 = add("Vector(1, 2)");
		GeoSymbolic vec2 = add("Vector(1, 2)");
		ArrayList<ExpressionNode> args =
				new ArrayList<>(Arrays.asList(vec1.wrap(), vec2.wrap()));
		String distCommand = geoGebraCAS.getCASCommand("Distance",
				args, false, StringTemplate.giacTemplate, SymbolicMode.NONE);
		assertThat(distCommand, containsString("ggbvect[1,2]"));
	}
}
