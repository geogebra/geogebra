package org.geogebra.common.kernel.commands;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.junit.Test;

public class TrigExpandTest extends BaseSymbolicTest {

	@Test
	public void testTrigExpandWithBorweinIntegral() {
		add("n = 3");
		add("r1 = Sequence(((sin(x/p))/(x/p)), p, 1, n, 2)");
		add("r2 = Product(r1)");
		add("r3 = TrigExpand(r2)");
		add("r4 = Integral(r3)");
		GeoSymbolic limit = add("r5 = Limit(r4, infinity) - Limit(r4, 0)");

		assertThat(limit.toValueString(StringTemplate.defaultTemplate),
				is("1 / 2 π"));
	}

	@Test
	public void testTrigExpand() {
		t("TrigExpand(sin(x)sin(x/3))",
				"1 / 2 * cos(2 * x / 3) - 1 / 2 * cos(4 * x / 3)");

	}
}
