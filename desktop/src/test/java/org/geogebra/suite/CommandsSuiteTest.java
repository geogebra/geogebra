package org.geogebra.suite;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class CommandsSuiteTest extends BaseSuiteTest {

	@Test
	public void testSolveEnabled() {
		GeoElement element = add("Solve(x)");
		assertThat(element.toString(StringTemplate.defaultTemplate), is("l1 = {x = 0}"));
	}

	@Test
	public void testIntegralEnabled() {
		GeoElement element = add("Integral(x)");
		assertThat(element.toString(StringTemplate.defaultTemplate), is("f(x) = 1 / 2 x²"));
	}
}
