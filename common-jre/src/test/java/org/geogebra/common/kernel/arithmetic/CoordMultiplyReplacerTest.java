package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.traversing.SqrtMultiplyFixer;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.plugin.Operation;
import org.junit.Test;

public class CoordMultiplyReplacerTest extends BaseUnitTest {

	@Test
	public void testReplacesXCoord() {
		FunctionVariable var = new FunctionVariable(getKernel(), "x");
		CoordMultiplyReplacer replacer = new CoordMultiplyReplacer(var, null, null);
		ExpressionNode node = parse("x(x+1)");
		assertThat(node.getOperation(), is(Operation.XCOORD));
		node = node.traverse(replacer).wrap();
		assertThat(node.getOperation(), is(Operation.MULTIPLY_OR_FUNCTION));
	}

	@Test
	public void testReplacesXCoordNested() {
		FunctionVariable var = new FunctionVariable(getKernel(), "x");
		CoordMultiplyReplacer replacer = new CoordMultiplyReplacer(var, null, null);
		ExpressionNode node = parse("x(1+x(1+x))");
		assertThat(node.getOperation(), is(Operation.XCOORD));
		node = node.traverse(replacer).traverse(SqrtMultiplyFixer.INSTANCE).wrap();
		assertThat(node.getOperation(), is(Operation.MULTIPLY));
		assertThat(node.toString(StringTemplate.testTemplate), is("x * (1 + x * (1 + x))"));
	}

	@Test
	public void testReplacesXCoordPower() {
		FunctionVariable var = new FunctionVariable(getKernel(), "x");
		CoordMultiplyReplacer replacer = new CoordMultiplyReplacer(var, null, null);
		ExpressionNode node = parse("x(x+1)^2");
		assertThat(node.getOperation(), is(Operation.POWER));
		assertThat(node.getLeftTree().getOperation(), is(Operation.XCOORD));
		node = node.traverse(replacer).traverse(SqrtMultiplyFixer.INSTANCE).wrap();
		assertThat(node.getOperation(), is(Operation.MULTIPLY));
		assertThat(node.toString(StringTemplate.testTemplate), is("x * (x + 1)^(2)"));
	}

	private ExpressionNode parse(String s) {
		try {
			return getKernel().getParser().parseExpression(s);
		} catch (ParseException e) {
			throw new IllegalArgumentException(s);
		}
	}
}
