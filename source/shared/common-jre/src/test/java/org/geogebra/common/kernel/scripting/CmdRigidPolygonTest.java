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

package org.geogebra.common.kernel.scripting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CmdRigidPolygonTest extends BaseAppTestSetup {

	@BeforeEach
	void setup() {
		setupClassicApp();
	}

	@Test
	void testWithDependencies() {
		evaluate("A=(0, 0)");
		evaluate("B=(1, 0)");
		evaluate("C=(1, 1)");
		evaluate("D=(0, 1)");
		// make sure all have a dependent point
		evaluate("A+B+C+D");
		evaluate("p=RigidPolygon(A, B, C, D)");
		GeoElement v = evaluateGeoElement("v={Vertex(p)}");
		evaluate("SetValue(A, (3, 0))");
		// check update after translation
		assertEquals("{(3, 0), (4, 0), (4, 1), (3, 1)}",
				v.toValueString(StringTemplate.testTemplate));
		// check update after rotation
		evaluate("SetValue(B, (2, 0))");
		assertEquals("{(3, 0), (2, 0), (2, -1), (3, -1)}",
				v.toValueString(StringTemplate.editTemplate));
	}
}
