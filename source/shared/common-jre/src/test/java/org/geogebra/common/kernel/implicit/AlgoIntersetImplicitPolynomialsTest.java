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
 
package org.geogebra.common.kernel.implicit;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class AlgoIntersetImplicitPolynomialsTest extends BaseUnitTest {

	@Test
	@Issue("APPS-6451")
	public void testIntersect() {
		add("f:y=-(x-8)^2+5");
		add("eq:abs(y)=4");
		assertThat(add("{Intersect(f,eq)}"), hasValue("{(5, -4), (11, -4), (7, 4), (9, 4)}"));
	}
}
