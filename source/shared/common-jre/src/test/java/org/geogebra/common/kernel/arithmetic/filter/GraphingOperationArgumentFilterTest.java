package org.geogebra.common.kernel.arithmetic.filter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		assertNotAllowed(Operation.MULTIPLY, value, value);
	}

	@Test
	public void testFiltersVectorProduct() {
		ExpressionValue value = getVector();
		ExpressionValue list = new MyList(getKernel());
		assertNotAllowed(Operation.VECTORPRODUCT, value, value);
		assertNotAllowed(Operation.VECTORPRODUCT, list, list);
	}

	@Test
	public void testFiltersAbs() throws ParseException {
		ExpressionValue vector = getVector();
		assertNotAllowed(Operation.ABS, vector, null);

		ExpressionValue number = new MyDouble(getKernel());
		assertAllowed(Operation.ABS, number, null);

		ExpressionValue function = new Function(getKernel(),
				new ExpressionNode(getKernel(), 0));
		assertAllowed(Operation.ABS, function, null);
		assertNotAllowed(Operation.ABS, add("2+i"), null);
		ValidExpression complex = getKernel().getParser().parseGeoGebraExpression("1/i");
		assertNotAllowed(Operation.ABS, complex, null);
		ValidExpression origin = getKernel().getParser().parseGeoGebraExpression("O");
		assertNotAllowed(Operation.ABS, origin, null);
	}

	@Test
	public void testFilterPower() {
		assertAllowed(Operation.POWER, add("2+i"), add("2"));
		assertAllowed(Operation.POWER, add("2+i"), add("1+i"));
		assertAllowed(Operation.POWER, add("7"), add("2"));
		assertNotAllowed(Operation.POWER, add("(2,1)"), add("2"));
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

		assertAllowed(Operation.MULTIPLY, vectorA, vectorB);
	}

	private void assertAllowed(Operation op, ExpressionValue left, ExpressionValue right) {
		assertTrue(op + " should be allowed for " + left + ", " + right,
				filter.isAllowed(new ExpressionNode(getKernel(), left, op, right)));
	}

	private void assertNotAllowed(Operation op, ExpressionValue left, ExpressionValue right) {
		assertFalse(op + " should be allowed for " + left + ", " + right,
				filter.isAllowed(new ExpressionNode(getKernel(), left, op, right)));
	}

	private ExpressionValue getVector() {
		return new MyVecNode(getKernel(), new ExpressionNode(getKernel(), 0),
				new ExpressionNode(getKernel(), 0));
	}
}
