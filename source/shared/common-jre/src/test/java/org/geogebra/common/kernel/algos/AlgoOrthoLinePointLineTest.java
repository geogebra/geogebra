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
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class AlgoOrthoLinePointLineTest extends BaseUnitTest {

	@Test
	@Issue("APPS-5523")
	public void repeatOrthogonalShouldNotUnderflow() {
		add("eq1: 0.1 x-0.1 y=0");
		add("eq2: 2 y=x");
		add("A=(1.31,1.31)");
		add("l1=IterationList(Intersect(eq1,"
				+ "PerpendicularLine(Intersect(eq2,PerpendicularLine(V,xAxis)),yAxis)),V,{A},15)");
		GeoLine last = add("PerpendicularLine(Element(l1,16),eq1)");
		assertThat(last, isDefined());
	}
}
