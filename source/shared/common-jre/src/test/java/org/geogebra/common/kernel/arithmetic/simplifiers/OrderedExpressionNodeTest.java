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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class OrderedExpressionNodeTest extends BaseSimplifyTestSetup {

	@ParameterizedTest
	@CsvSource({
			"-3",
			"-sqrt(2)",
			"-3sqrt(2)",
			"-2-3",
			"-2-3-4-5-7sqrt(7)"
	})
	void testAllNegative(String definition) {
		assertTrue(createOrderedNode(definition).isAllNegative());
	}

	private OrderedExpressionNode createOrderedNode(String definition) {
		ExpressionValue value = evaluateGeoElement(definition).getDefinition();
		return new OrderedExpressionNode(value, utils);
	}

	@ParameterizedTest
	@CsvSource({
			"-3 / 2",
			"-sqrt(2) / 5",
			"(-5sqrt(7)) / -12",
			"(-2-3) / -4",
			"(-2-3-4-5-7sqrt(7)) / 6"
	})
	void testHasNumeratorNegativesOnly(String definition) {
		assertTrue(createOrderedNode(definition).hasNumeratorNegativesOnly());
	}

	@ParameterizedTest
	@CsvSource({
			"3",
			"sqrt(2)",
			"3sqrt(2)",
			"2+3",
			"-2+3",
			"2-3",
			"-2-3+4-5-7sqrt(7)",
			"-2-3-4+5-7sqrt(7)",
			"-2+3-4-5-7sqrt(7)",
			"sqrt(2) + sqrt(5) - sqrt(10) - 5"
	})
	void testNotAllNegative(String definition) {
		assertFalse(createOrderedNode(definition).isAllNegative());
	}
	
	@ParameterizedTest
	@CsvSource({
			"3 / 7",
			"sqrt(2) / 2",
			"(3sqrt(2)) / 77",
			"(2+3) / -2",
			"(-2+3) / 6",
			"(2-3) / 2",
			"(-2-3+4-5-7sqrt(7)) / 8",
			"(-2-3-4+5-7sqrt(7)) / 8",
			"(-2+3-4-5-7sqrt(7)) / 8",
			"(sqrt(2) + sqrt(5) - sqrt(10) - 5) / 12"
	})
	void testHasNumeratorNotOnlyNegatives(String definition) {
		assertFalse(createOrderedNode(definition).isAllNegative());
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		// Not used.
		return null;
	}
}
