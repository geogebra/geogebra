package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoLine;
import org.junit.Test;

public class MinusTest extends BaseUnitTest {

	@Test
	public void testFunctionMinusFunction() {
		add("f(x) = 3x - 1");
		add("g(x) = -2x + 4");

		add("eq1: f - g = 0");

		lookup("eq1").setToStringMode(GeoLine.EQUATION_USER);

		assertEquals("3x - 1 - (-2 x + 4) = 0",
				lookup("eq1").toValueString(StringTemplate.algebraTemplate));
	}

}
