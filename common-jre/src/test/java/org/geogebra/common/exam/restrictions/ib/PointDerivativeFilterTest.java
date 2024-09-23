package org.geogebra.common.exam.restrictions.ib;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

public class PointDerivativeFilterTest extends BaseUnitTest {

	private final PointDerivativeFilter filter = new PointDerivativeFilter();

	@Before
	public void setupTest() {
		add("f(x)=x^2");
	}

	@Test
	public void testFilterRestrictsDerivativesOverVariable() {
		assertFalse(filter.isAllowed(parse("f'(x)")));
		assertFalse(filter.isAllowed(parse("f'(x-1)")));
		assertFalse(filter.isAllowed(parse("f'")));
	}

	@Test
	public void testAllowsDerivativeAtPoint() {
		assertTrue(filter.isAllowed(parse("f'(1)")));
		add("a = 2");
		assertTrue(filter.isAllowed(parse("f'(a)")));
	}

	private ExpressionNode parse(String expression) {
		try {
			ExpressionNode node = getKernel().getParser().parseExpression(expression);
			node.resolveVariables(EvalInfoFactory.getEvalInfoForAV(getApp()));
			return node;
		} catch (ParseException e) {
			throw new AssertionError(e);
		}
	}
}
