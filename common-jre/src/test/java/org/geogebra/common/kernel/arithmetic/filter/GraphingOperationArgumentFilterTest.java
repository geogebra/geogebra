package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.plugin.Operation;
import org.junit.Assert;
import org.junit.Test;

public class GraphingOperationArgumentFilterTest extends BaseUnitTest {

	private OperationArgumentFilter filter = new GraphingOperationArgumentFilter();

	@Test
	public void testFiltersCrossProduct() {
		ExpressionValue value = new MyVecNode(getKernel());
		Assert.assertFalse(filter.isAllowed(Operation.MULTIPLY, value, value));
	}

	@Test
	public void testFiltersVectorProduct() {
		ExpressionValue value = new MyVecNode(getKernel());
		Assert.assertFalse(filter.isAllowed(Operation.VECTORPRODUCT, value, value));
	}

	@Test
	public void testFiltersAbs() {
		ExpressionValue vector = new MyVecNode(getKernel());
		Assert.assertFalse(filter.isAllowed(Operation.ABS, vector, null));

		ExpressionValue number = new MyDouble(getKernel());
		Assert.assertTrue(filter.isAllowed(Operation.ABS, number, null));

		ExpressionValue function = new Function(getKernel());
		Assert.assertTrue(filter.isAllowed(Operation.ABS, function, null));
	}
}
