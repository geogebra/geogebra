package org.geogebra.common.kernel.arithmetic.filter;

import static org.junit.Assert.assertFalse;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.plugin.Operation;
import org.junit.Test;

public class ExpressionFilterTests extends BaseUnitTest {

	@Test
	public void testOperationExpression() throws Exception {
		ExpressionFilter filter = new OperationExpressionFilter(Operation.OR, Operation.AND);
		ValidExpression expression = parse("true || false");
		assertFalse(filter.isAllowed(expression));
	}

	@Test
	public void testComplexExpression() throws Exception {
		ExpressionFilter filter = new ComplexExpressionFilter();
		ValidExpression expression = parse("3i");
		assertFalse(filter.isAllowed(expression));
	}

	@Test
	public void testRadianExpression() throws Exception {
		ExpressionFilter filter = new RadianExpressionFilter();
		ValidExpression expression = parse("3rad");
		assertFalse(filter.isAllowed(expression));
	}

	private ValidExpression parse(String input) throws Exception {
		return getKernel().getParser().parseGeoGebraExpression(input);
	}
}
