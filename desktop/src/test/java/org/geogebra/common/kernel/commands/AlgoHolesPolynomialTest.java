package org.geogebra.common.kernel.commands;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.kernel.algos.AlgoHolesPolynomial;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.suite.BaseSuiteTest;
import org.junit.Test;

public class AlgoHolesPolynomialTest extends BaseSuiteTest {

	@Test
	public void testRegressionApps2348() {
		add("a = 1");
		GeoFunction function = add("1/(x+a)");
		AlgoHolesPolynomial algo = new AlgoHolesPolynomial(getConstruction(), function, null);
		assertThat(algo.getOutputLength(), is(1));

	}
}
