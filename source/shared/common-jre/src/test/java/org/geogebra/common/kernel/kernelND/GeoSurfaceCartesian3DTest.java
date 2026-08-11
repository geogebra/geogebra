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
 
package org.geogebra.common.kernel.kernelND;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSurfaceCartesian3D;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeoSurfaceCartesian3DTest extends BaseUnitTest {

	@BeforeEach
	void setUp() {
		getApp().set3dConfig();
	}

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	void testGeoSurfaceCartesianNDHasTwoFunctions() {
		getApp().set3dConfig();
		addAvInput("f(a,b)=(a+b,a-b)");
		GeoSurfaceCartesian3D g = addAvInput("g(u,v)=f(u,v)+(0,0,1)");
		g.setDerivatives();
	}

	@Test
	@Issue("APPS-5899")
	void testCopyOfUndefinedSurface() {
		add("f:Element({x},2)");
		t("Sequence(Surface(f(u),v+s,u+v,u,0,1,v,0,1),s,1,3)", "{?, ?, ?}");
	}

	@Test
	@Issue("APPS-7787")
	void dependentSurface() {
		add("c(t)=Spline({(1,0,0),(1,.5,0),(.5,1,0),(0,1,0)})");
		add("s=Surface(cos(v)*c(t)+(0,0,sin(v)),t,0,1,v,0,pi/2)");
		add("s1=Surface(2 * s(t,v),t,0,1,v,0,pi/2)");
		update3DView();
		assertEquals("(1, 0, 0)",
				add("A=s(0,0)").toValueString(StringTemplate.editTemplate));
		assertEquals("(0, 1, 0)",
				add("B=s(1,0)").toValueString(StringTemplate.editTemplate));
		reload();
		update3DView();
		assertEquals("(1, 0, 0)",
				lookup("A").toValueString(StringTemplate.editTemplate));
		assertEquals("(0, 1, 0)",
				lookup("B").toValueString(StringTemplate.editTemplate));
	}

	private void update3DView() {
		EuclidianView3D euclidianView3D = (EuclidianView3D) getApp().getEuclidianView3D();
		euclidianView3D.update();
		euclidianView3D.updateDrawables();
	}
}
