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

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AlgoIterationListTest extends BaseAppTestSetup {

	@BeforeEach
	void setup() {
		setupClassicApp();
	}

	@Test
	void testDynamic() {
		evaluate("a=34");
		evaluate("b=21");
		GeoElement list = evaluateGeoElement("IterationList(Mod(p,q),p,q,{a,b},b)");
		GeoElement element = evaluateGeoElement("Iteration(Mod(p,q),p,q,{a,b},3)");
		assertEquals("{34, 21, 13, 8, 5, 3, 2, 1, 0, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?}",
				list.toValueString(StringTemplate.testTemplate));
		assertEquals("8",
				element.toValueString(StringTemplate.testTemplate));
		evaluate("SetValue(b, 13)");
		assertEquals("{34, 13, 8, 5, 3, 2, 1, 0, ?, ?, ?, ?, ?, ?}",
				list.toValueString(StringTemplate.testTemplate));
		assertEquals("5",
				element.toValueString(StringTemplate.testTemplate));
	}

}
