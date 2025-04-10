package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class MoveMinusInOutTest extends BaseSimplifyTest {
	@ParameterizedTest
	@CsvSource({
			"-(sqrt(5) - 6), 6 - sqrt(5)",
			"-(sqrt(5) + 6), -sqrt(5) - 6",
			"-((sqrt(5) - 6) / 5), (6 - sqrt(5)) / 5",
			"-((sqrt(5) + 6) / 5), -((sqrt(5) + 6) / 5)",
	})
	public void testApply(String definition, String expected) {
		GeoElementND geo = add(definition);
		ExpressionNode node = geo.getDefinition();
		ExpressionNode applied = getSimplifier().apply(node);
		assertAll(() -> assertEquals(expected, applied.toString(StringTemplate.defaultTemplate)),
				() -> assertEquals(node.evaluateDouble(), applied.evaluateDouble(), 0));

	}

	@ParameterizedTest
	@CsvSource({
			"-sqrt(15) + 6, 6 - sqrt(15)",
			"-sqrt(15) + 6 - sqrt(2), 6 - sqrt(15) - sqrt(2)",
			"-sqrt(15) + 2 - sqrt(2) + 2sqrt(2), 2sqrt(2) + 2 - sqrt(15) - sqrt(2)",
	})
	void testSortOperands(String definition, String expected) {
		GeoElementND geo = add(definition);
		ExpressionNode node = geo.getDefinition();
		ExpressionNode orderedNode = new OrderedExpressionNode(node, utils);
		assertAll(() -> assertEquals(expected,
						orderedNode.toString(StringTemplate.defaultTemplate)),
				() -> assertEquals(node.evaluateDouble(), orderedNode.evaluateDouble(), 0));
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return MoveMinusInOut.class;
	}
}
