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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class AlgoIsInRegionTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	@Issue("APPS-5354")
	public void testPointIsInRegionOfVerticalCircle() {
		add("C = (0, 0, 0.5)");
		add("D = (0, -1.5, 0)");
		add("E = (0, 1.5, 0)");
		add("p : Plane(C, D, E)");
		add("d : Circle(xAxis, C)");
		GeoPoint3D point = add("F = PointIn(p)");
		point.setCoordsFromPoint(add("(0, 0.3, 0.3)"));
		GeoBoolean isInRegion = add("IsInRegion(F, d)");
		assertTrue(isInRegion.getBoolean());
	}

	@Test
	@Issue("APPS-5354")
	public void testPointIsInRegionOfVerticalCircleUsesRealCoordinates() {
		add("C = (0, 0, 0.5)");
		add("D = (0, -1.5, 0)");
		add("E = (0, 1.5, 0)");
		add("p : Plane(C, D, E)");
		add("d : Circle(xAxis, C)");
		GeoPoint3D point = add("F = PointIn(p)");
		point.setWillingCoords(5, 5, 5, 5);
		GeoBoolean isInRegion = add("IsInRegion(F, d)");
		assertTrue(isInRegion.getBoolean());
	}

	@Test
	@Issue("APPS-5354")
	public void testPointIsInRegionOfVerticalCircleWithUndefinedWillingCords() {
		add("C = (0, 0, 0.5)");
		add("D = (0, -1.5, 0)");
		add("E = (0, 1.5, 0)");
		add("p : Plane(C, D, E)");
		add("d : Circle(xAxis, C)");
		GeoPoint3D point = add("F = PointIn(p)");
		point.setWillingCoordsUndefined();
		GeoBoolean isInRegion = add("IsInRegion(F, d)");
		assertTrue(isInRegion.getBoolean());
	}

	@Test
	@Issue("APPS-5354")
	public void testPointIsNotInRegionOfVerticalCircle() {
		add("C = (0, 0, 0.5)");
		add("D = (0, -1.5, 0)");
		add("E = (0, 1.5, 0)");
		add("p : Plane(C, D, E)");
		add("d : Circle(xAxis, C)");
		GeoPoint3D point = add("F = PointIn(p)");
		point.setCoordsFromPoint(add("(0, 0.3, 0.5)"));
		GeoBoolean isInRegion = add("IsInRegion(F, d)");
		assertFalse(isInRegion.getBoolean());
	}
}