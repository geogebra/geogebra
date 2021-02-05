package org.geogebra.common.kernel.geos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.plugin.Operation;
import org.junit.Test;

public class GeoFunctionTest extends BaseUnitTest {

	@Test
	public void testEquals() {
		GeoFunction func1 = addAvInput("f(x)=x+2");
		GeoFunction func2 = addAvInput("g(x)=2+x");
		assertThat(func1.isEqual(func2), is(true));
		assertThat(func2.isEqual(func1), is(true));
		addAvInput("SetValue(f,?)");
		assertThat(func1.isEqual(func2), is(false));
		assertThat(func2.isEqual(func1), is(false));
	}

	@Test
	public void testIntervalsOnesided() {
		ExpressionNode less = new ExpressionNode(getKernel(), new FunctionVariable(getKernel()),
				Operation.LESS, new MyDouble(getKernel(), 4));
		ExpressionNode more = new ExpressionNode(getKernel(), new FunctionVariable(getKernel()),
				Operation.GREATER, new MyDouble(getKernel(), 3));
		double[] bounds = new double[2];
		GeoIntervalUtil.updateBoundaries(less, bounds);
		assertArrayEquals(new double[]{Double.NEGATIVE_INFINITY, 4}, bounds, .01);
		GeoIntervalUtil.updateBoundaries(more, bounds);
		assertArrayEquals(new double[]{3, Double.POSITIVE_INFINITY}, bounds, .01);
	}

	@Test
	public void testIntervals() {
		ExpressionNode less = new ExpressionNode(getKernel(), new FunctionVariable(getKernel()),
				Operation.LESS, new MyDouble(getKernel(), 4));
		ExpressionNode more = new ExpressionNode(getKernel(), new FunctionVariable(getKernel()),
				Operation.GREATER, new MyDouble(getKernel(), 3));
		ExpressionNode interval = new ExpressionNode(getKernel(), less, Operation.AND, more);
		double[] bounds = new double[2];
		GeoIntervalUtil.updateBoundaries(interval, bounds);
		assertArrayEquals(new double[]{3, 4}, bounds, .01);
	}
}