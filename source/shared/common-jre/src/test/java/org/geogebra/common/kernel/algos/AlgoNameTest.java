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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlgoNameTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	@Issue("APPS-6919")
	public void testNameForElement() {
		evaluate("a=1");
		evaluate("P=Element(Sequence(5),1)");
		evaluate("Q=Element({a},1)");
		GeoText nameListOfObjects = evaluateGeoElement("Name(Q)");
		assertEquals("a", nameListOfObjects.getTextString());
		GeoText nameSequence = evaluateGeoElement("Name(P)");
		assertEquals("P", nameSequence.getTextString());
	}
}
