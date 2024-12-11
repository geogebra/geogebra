package org.geogebra.common.kernel.arithmetic.filter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.plugin.Operation;
import org.hamcrest.Matcher;
import org.junit.Test;

public class GraphingOperationArgumentFilterTest extends BaseUnitTest {

	private final ExpressionFilter filter = GraphingOperationArgumentFilter.INSTANCE;

	@Test
	public void testFiltersCrossProduct() {
		ExpressionValue value = getVector();
		assertAllowed(Operation.MULTIPLY, value, value, is(false));
	}

	@Test
	public void testFiltersVectorProduct() {
		ExpressionValue value = getVector();
		ExpressionValue list = new MyList(getKernel());
		assertAllowed(Operation.VECTORPRODUCT, value, value, is(false));
		assertAllowed(Operation.VECTORPRODUCT, list, list, is(false));
	}

	@Test
	public void testFiltersAbs() throws ParseException {
		ExpressionValue vector = getVector();
		assertAllowed(Operation.ABS, vector, null, is(false));

		ExpressionValue number = new MyDouble(getKernel());
		assertAllowed(Operation.ABS, number, null, is(true));

		ExpressionValue function = new Function(getKernel(),
				new ExpressionNode(getKernel(), 0));
		assertAllowed(Operation.ABS, function, null, is(true));
		assertAllowed(Operation.ABS, add("2+i"), null, is(false));
		ValidExpression complex = getKernel().getParser().parseGeoGebraExpression("1/i");
		assertAllowed(Operation.ABS, complex, null, is(false));
		ValidExpression origin = getKernel().getParser().parseGeoGebraExpression("O");
		assertAllowed(Operation.ABS, origin, null, is(false));
	}

	@Test
	public void absFilterShouldWorkForExpressions() throws ParseException {
		add("A=(1,1)");
		add("B=(2,2)");
		ValidExpression node = getKernel().getParser().parseGeoGebraExpression("abs(A-B)");
		assertThat(filter.isAllowed(node), equalTo(false));
	}

	@Test
	public void testAllowsComplexNumbers() {
		GeoVec2D vectorA = new GeoVec2D(getKernel(), 1, 2);
		vectorA.setMode(Kernel.COORD_COMPLEX);
		GeoVec2D vectorB = new GeoVec2D(getKernel(), 1, 2);
		vectorB.setMode(Kernel.COORD_COMPLEX);

		assertAllowed(Operation.MULTIPLY, vectorA, vectorB, is(true));
	}

	private void assertAllowed(Operation op, ExpressionValue left, ExpressionValue right,
			Matcher<Boolean> check) {
		assertThat(op + " should be allowed for " + left + ", " + right,
				filter.isAllowed(new ExpressionNode(getKernel(), left, op, right)), check);
	}

	private ExpressionValue getVector() {
		return new MyVecNode(getKernel(), new ExpressionNode(getKernel(), 0),
				new ExpressionNode(getKernel(), 0));
	}
}
