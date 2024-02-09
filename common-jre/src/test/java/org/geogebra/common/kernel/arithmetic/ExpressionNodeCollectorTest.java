package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.junit.Test;

public class ExpressionNodeCollectorTest extends BaseUnitTest {
	@Test
	public void testCollectVariableNames() {
		GeoFunctionNVar function = add("2x+45+y+z");
		ExpressionNodeCollector<String> collector =
				new ExpressionNodeCollector<>(function.getFunctionExpression());
		List<String> actual = collector.filter(v -> v instanceof FunctionVariable)
				.mapTo(t -> ((FunctionVariable) t.unwrap()).getSetVarString());
		assertEquals(Arrays.asList("x", "y", "z"), actual);
	}

	@Test
	public void testCollectConstants() {
		GeoFunctionNVar function = add("2x+45+y+z+43");
		ExpressionNodeCollector<Double> collector =
				new ExpressionNodeCollector<>(function.getFunctionExpression());
		List<Double> actual = collector.filter(ExpressionValue::isConstant)
				.mapTo(t -> (t.unwrap().evaluateDouble()));
		assertEquals(Arrays.asList(2.0, 45.0, 43.0), actual);

	}
}
