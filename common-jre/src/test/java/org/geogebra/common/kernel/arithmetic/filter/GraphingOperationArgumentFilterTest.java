package org.geogebra.common.kernel.arithmetic.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.geos.GeoVec2D;
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

	@Test
	public void testAllowsComplexNumbers() {
		GeoVec2D vectorA = new GeoVec2D(getKernel(), 1, 2);
		vectorA.setMode(Kernel.COORD_COMPLEX);
		GeoVec2D vectorB = new GeoVec2D(getKernel(), 1, 2);
		vectorB.setMode(Kernel.COORD_COMPLEX);

		assertThat(filter.isAllowed(Operation.VECTORPRODUCT, vectorA, vectorB), is(true));
	}
}
