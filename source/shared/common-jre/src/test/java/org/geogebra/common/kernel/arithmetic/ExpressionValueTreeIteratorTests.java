package org.geogebra.common.kernel.arithmetic;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class ExpressionValueTreeIteratorTests extends BaseUnitTest {

	@Test
	public void testIteratorDepthFirst() {
		ValidExpression expression = parseExpression("1 + {(1, 2), 3} + Command(\"string\")");
		List<Class<? extends ExpressionValue>> expectedClasses = List.of(
				ExpressionNode.class, // Full expression
				ExpressionNode.class, // 1 + {(1, 2), 3}
				MySpecialDouble.class, // 1
				MyList.class, // {(1, 2), 3}
				ExpressionNode.class, // List arguments are wrapped
				MyVecNode.class, // (1, 2)
				ExpressionNode.class, // Vector arguments are wrapped
				MySpecialDouble.class, // 1
				ExpressionNode.class, // Vector arguments are wrapped
				MySpecialDouble.class, // 2
				ExpressionNode.class, // List arguments are wrapped
				MySpecialDouble.class, // 3
				ExpressionNode.class, // Command is wrapped
				Command.class, // Command("string")
				ExpressionNode.class, // Command arguments are wrapped
				MyStringBuffer.class // "string"
		);
		List<Class<? extends ExpressionValue>> actualClasses = new ArrayList<>();
		for (ExpressionValue child : expression) {
			actualClasses.add(child.getClass());
		}
		assertIterableEquals(expectedClasses, actualClasses);
	}
}
