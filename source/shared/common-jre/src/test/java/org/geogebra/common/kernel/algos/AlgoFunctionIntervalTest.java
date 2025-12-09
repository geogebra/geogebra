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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class AlgoFunctionIntervalTest extends BaseUnitTest {

	@Test
	public void legacyLimitedFunction() {
		getConstruction().setFileLoading(true);
		GeoFunction f = add("Function(x,1,2)");
		assertThat(f.getParentAlgorithm(), instanceOf(AlgoFunctionInterval.class));
		assertThat(f, hasValue("x"));
		assertThat(f.getFormulaString(StringTemplate.latexTemplate, true),
				is("x, \\;\\;\\;\\; \\left(1 \\leq x \\leq 2 \\right)"));
	}
}
