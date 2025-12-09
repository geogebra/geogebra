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

package org.geogebra.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CopyPasteTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.G3D);
	}

	@Test
	@Issue("APPS-5464")
	public void shouldCopyPointOnAxis() {
		evaluateGeoElement("A=(1,1)");
		evaluateGeoElement("B=(3,2)");
		evaluateGeoElement("C=Point(xAxis)");
		GeoElement poly = evaluateGeoElement("t=Polygon(A,B,C)");
		InternalClipboard.duplicate(getApp(), Collections.singletonList(poly));
		assertEquals("true",
				evaluateGeoElement("t==t_1").toValueString(StringTemplate.testTemplate));
	}

	@Test
	public void shouldCopyPointOnLine() {
		evaluateGeoElement("A=(1,1)");
		evaluateGeoElement("B=(3,2)");
		evaluateGeoElement("C=Point(x=0)");
		GeoElement poly = evaluateGeoElement("t=Polygon(A,B,C)");
		InternalClipboard.duplicate(getApp(), Collections.singletonList(poly));
		assertEquals("true",
				evaluateGeoElement("t==t_1").toValueString(StringTemplate.testTemplate));
	}

	@Test
	public void shouldCopyCone() {
		evaluateGeoElement("A=(1,1,0)");
		evaluateGeoElement("B=(3,2,0)");
		evaluateGeoElement("c:Circle(A,B,xOyPlane)");
		evaluateGeoElement("V=(x(A),y(A), 4)");
		evaluateGeoElement("a:Cone(c,Segment(A,V))");
		InternalClipboard.duplicate(getApp(), List.of(getKernel().lookupLabel("b")));
		assertEquals("true",
				evaluateGeoElement("a==a_1").toValueString(StringTemplate.testTemplate));
	}
}
