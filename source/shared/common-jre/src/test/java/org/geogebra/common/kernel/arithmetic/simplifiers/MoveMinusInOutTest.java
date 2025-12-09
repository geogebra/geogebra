/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class MoveMinusInOutTest extends BaseSimplifyTestSetup {
	@ParameterizedTest
	@CsvSource({
			"-(sqrt(5) - 6), 6 - sqrt(5)",
			"-(sqrt(5) + 6), -sqrt(5) - 6",
			"-((sqrt(5) - 6) / 5), (6 - sqrt(5)) / 5",
			"-((sqrt(5) + 6) / 5), -((sqrt(5) + 6) / 5)",
	})
	public void testApply(String definition, String expected) {
		GeoElementND geo = evaluateGeoElement(definition);
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
		GeoElementND geo = evaluateGeoElement(definition);
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
