package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.plugin.Operation;
import org.junit.Test;

public class CoordMultiplyReplacerTest extends BaseUnitTest {

	@Test
	public void testReplacesXCoord() {
		FunctionVariable var = new FunctionVariable(getKernel(), "x");
		CoordMultiplyReplacer replacer = new CoordMultiplyReplacer(var, null, null);
		ExpressionNode node = null;
		try {
			node = getKernel().getParser().parseExpression("x(x+1)");
		} catch (ParseException e) {
			fail();
		}
		assumeThat(node.getOperation(), is(Operation.XCOORD));
		node = node.traverse(replacer).wrap();
		assertThat(node.getOperation(), is(Operation.MULTIPLY_OR_FUNCTION));
		assertThat(node.getOperation(), is(Operation.MULTIPLY_OR_FUNCTION));
	}

	@Test
	public void testCollectsUndefined() {
		FunctionVariable xVar = new FunctionVariable(getKernel(), "x");
		FunctionVariable yVar = new FunctionVariable(getKernel(), "y");
		CoordMultiplyReplacer replacer = new CoordMultiplyReplacer(xVar, yVar, null);
		ExpressionNode node = null;
		try {
			node = getKernel().getParser().parseExpression("x(x+1)+y(y+5)");
		} catch (ParseException e) {
			fail();
		}
		node.traverse(replacer).wrap();
		assertThat(replacer.getUndecided().size(), is(2));
	}
}
