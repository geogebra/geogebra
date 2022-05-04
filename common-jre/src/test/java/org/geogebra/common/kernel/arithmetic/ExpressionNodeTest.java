package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
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
}
