package org.geogebra.common.euclidian.draw;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class PlotConditionalFunctionTest extends BaseUnitTest {
	@Test
	public void testGetLimitInequalities() {
		List<Double> expected = Collections.singletonList(5.0);
		limitShouldBe("x < 5", expected);
		limitShouldBe("5 < x", expected);
		limitShouldBe("x > 5", expected);
		limitShouldBe("5 > x", expected);
		limitShouldBe("1 + 4 > x", expected);
		limitShouldBe("x > 1 + 4", expected);
		limitShouldBe("1 + 4 < x", expected);
		limitShouldBe("x < 1 + 4", expected);

		limitShouldBe("x <= 5", expected);
		limitShouldBe("5 <= x", expected);
		limitShouldBe("x >= 5", expected);
		limitShouldBe("5 >= x", expected);
		limitShouldBe("1 + 4 >= x", expected);
		limitShouldBe("x >= 1 + 4", expected);
		limitShouldBe("1 + 4 <= x", expected);
		limitShouldBe("x <= 1 + 4", expected);
		limitShouldBe("x < 1 + 5 / 2.0", 3.5);
		limitShouldBe("1 + 5 / 2 < x", 3.5);
		limitShouldBe("x <= 1 + 5 / 2.0", 3.5);
		limitShouldBe("1 + 5 / 2 <= x", 3.5);
		limitShouldBe("x == 5", 5.0);
		limitShouldBe("x<sin(0.5)", Math.sin(0.5));
	}

	@Test
	public void testGetLimitAndIntervals() {
		List<Double> expected = Arrays.asList(-5.0, 5.0);
		limitShouldBe("-5 < x < 5", expected);
		limitShouldBe("-5 <= x <= 5", expected);
		limitShouldBe("5 < x < 2x", 5.0);
	}

	@Test
	public void testLimitInvalid() {
		limitShouldBe("x < x");
		limitShouldBe("f(x):=2 < 3");
		limitShouldBe("2x > 3");
		limitShouldBe("1 < 2x < 3");
		limitShouldBe("sin(x) < 0.5");
	}

	private void limitShouldBe(String command, List<Double> expected) {
		GeoFunction f = add(command);
		List<Double> limits = new ArrayList<>();
		PlotConditionalFunction.getConditionLimit(Objects.requireNonNull(
				f.getFunctionExpression()).wrap(), limits);
		assertEquals(expected, limits);
	}

	private void limitShouldBe(String command, Double... expected) {
		limitShouldBe(command, Arrays.asList(expected));
	}

}