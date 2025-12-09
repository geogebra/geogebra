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

package org.geogebra.common.kernel.commands;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.kernel.algos.AlgoRemovableDiscontinuity;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.suite.BaseSuiteTest;
import org.junit.Test;

public class AlgoRemovableDiscontinuityTest extends BaseSuiteTest {

	@Test
	public void testRegressionApps2348() {
		add("a = 1");
		GeoFunction function = add("1/(x+a)");
		AlgoRemovableDiscontinuity algo
				= new AlgoRemovableDiscontinuity(getConstruction(), function, null);
		assertThat(algo.getOutputLength(), is(1));

	}
}
