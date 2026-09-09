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

package org.geogebra.common.geogebra3D.kernel3D.geos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeoConicPart3DTest extends BaseAppTestSetup {
	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.G3D);
	}

	@Test
	@Issue("APPS-7869")
	void dilatedArcLength() {
		evaluate("A=(0,-4,0)");
		evaluate("B=(0,0,2)");
		evaluate("C=(0,4,0)");
		evaluate("c=CircumcircularArc(A,B,C)");
		GeoElement dilatedArc = evaluateGeoElement("c'=Dilate(c,.6)");
		assertEquals("5.56377", dilatedArc.toValueString(StringTemplate.editTemplate));
		GeoElement dilatedArcLength = evaluateGeoElement("Length(c')");
		assertEquals("5.56377", dilatedArcLength.toValueString(StringTemplate.editTemplate));
	}
}
