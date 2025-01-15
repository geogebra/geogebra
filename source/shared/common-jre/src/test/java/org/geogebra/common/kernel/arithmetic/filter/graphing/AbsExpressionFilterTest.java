package org.geogebra.common.kernel.arithmetic.filter.graphing;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.junit.Test;

public class AbsExpressionFilterTest extends BaseUnitTest {

	final AbsExpressionFilter filter = new AbsExpressionFilter();

	@Test
	public void testPointsAreRestricted() {
		assertFalse(isAllowed("abs((1,2))"));
		assertFalse(isAllowed("abs((1,2) + (3,4))"));
		assertFalse(isAllowed("abs(Point({1,2}))"));
	}

	@Test
	public void testVectorsAreRestricted() {
		assertFalse(isAllowed("abs({{1},{2}})"));
		assertFalse(isAllowed("abs({{1},{2}} + {{3},{4}})"));
		assertFalse(isAllowed("abs(Vector((1,2)))"));
	}

	@Test
	public void testNumbersAreAllowed() {
		assertTrue(isAllowed("abs(-4)"));
		assertTrue(isAllowed("abs(1 + 2 * 8)"));
		assertTrue(isAllowed("abs(Length({{1},{2}}))"));
	}

	@Test
	public void testComplexNumbersAreRestricted() {
		assertFalse(isAllowed("abs(1 + i)"));
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
