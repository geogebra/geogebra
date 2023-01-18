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
