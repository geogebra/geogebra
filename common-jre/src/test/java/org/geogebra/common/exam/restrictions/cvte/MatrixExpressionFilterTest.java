package org.geogebra.common.exam.restrictions.cvte;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.junit.Test;

public class MatrixExpressionFilterTest extends BaseUnitTest {

	private final MatrixExpressionFilter filter = new MatrixExpressionFilter();

	@Test
	public void testDirectMatrixInputIsNotAllowed() {
		assertFalse(isAllowed("{{1,2,3,4}}")); // row vector
		assertFalse(isAllowed("{{1},{2},{3},{4}}")); // column vector
		assertFalse(isAllowed("{{1,2},{3,4}}"));
		assertFalse(isAllowed("{{5,6,7},{1,2,3}}"));
	}

	@Test
	public void testIndirectMatrixInputIsNotAllowed() {
		assertFalse(isAllowed("{{1,2},{3,4}} * {{5,6},{7,8}}"));
		assertFalse(isAllowed("{1,2,3} + {{4,5,6}}"));
		assertFalse(isAllowed("{{1},{2},{3}} * {{4,5,6}}"));
		assertFalse(isAllowed("Invert({{1,2}, {3,4}})"));

	}

	private boolean isAllowed(String input) {
		ValidExpression expression = parse(input);
		return filter.isAllowed(expression);
	}

	private ValidExpression parse(String input) {
		try {
			return getKernel().getAlgebraProcessor().getValidExpressionNoExceptionHandling(input);
		} catch (Exception e) {
			fail("Exception thrown " + e);
		}
		return null;
	}
}
