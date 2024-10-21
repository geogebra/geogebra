package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.plugin.Operation;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class ExpressionNodeTest extends BaseUnitTest {

	@Test
	public void testCopyAttributesToForcesVectorPrintingMode() {
		MyVecNode vector = new MyVecNode(getKernel(),
				new MyDouble(getKernel(), 1), new MyDouble(getKernel(), 2));
		ExpressionNode originalNode = new ExpressionNode(getKernel(), vector);
		ExpressionNode copiedNode = originalNode.deepCopy(getKernel());

		originalNode.setForceVector();
		originalNode.copyAttributesTo(copiedNode);
		assertThat(copiedNode.toString(StringTemplate.editorTemplate), is("{{1}, {2}}"));
	}

	@Test
	@Issue("APPS-5662")
	public void testIntegralWithMixedNumbers() {
		Kernel k = getKernel();
		FunctionVariable x = new FunctionVariable(k, "x");
		ExpressionNode fraction =
				new ExpressionNode(k, new MyDouble(k, 2), Operation.DIVIDE, new MyDouble(k, 3));
		ExpressionNode en = new ExpressionNode(k, x, Operation.PLUS,
				new ExpressionNode(k, new MyDouble(k, 1), Operation.INVISIBLE_PLUS,
						fraction));
		assertThat(en.integral(x, k).toString(StringTemplate.testTemplate),
				is("x^(2) / 2 + 1 * x + (2 * x) / 3"));
	}
}
