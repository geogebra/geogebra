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

package org.geogebra.common.kernel.interval.evaluators;

import static org.geogebra.common.kernel.interval.IntervalSet.connected;
import static org.geogebra.common.kernel.interval.IntervalSet.empty;
import static org.geogebra.common.kernel.interval.IntervalSet.overflow;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.kernel.interval.operators.IntervalNodeEvaluator;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class IntervalEvaluatorOverflowTest {

	private final IntervalNodeEvaluator evaluator = new IntervalNodeEvaluator();

	private interface UnaryIntervalSetOperator {
		IntervalSet exec(IntervalSet set);

	}

	private interface BinaryIntervalSetOperator {
		IntervalSet exec(IntervalSet set1, IntervalSet set2);
	}

	@Test
	@Issue("APPS-7561")
	public void testOverflowOfUnaryOperators() {
		testOverflowUnary(evaluator::inverseSet);
		testOverflowUnary(evaluator::sqrtSet);
		testOverflowUnary(evaluator::expSet);
		testOverflowUnary(evaluator::logSet);
		testOverflowUnary(evaluator::log2Set);
		testOverflowUnary(evaluator::log10Set);
		testOverflowUnary(evaluator::absSet);
		testOverflowUnary(evaluator::asinSet);
		testOverflowUnary(evaluator::acosSet);
		testOverflowUnary(evaluator::atanSet);
		testOverflowUnary(evaluator::sinhSet);
		testOverflowUnary(evaluator::coshSet);
		testOverflowUnary(evaluator::tanhSet);
	}

	private void testOverflowUnary(UnaryIntervalSetOperator operator) {
		assertEquals(overflow(), operator.exec(overflow()));
	}

	@Test
	public void testOverflowOfBinaryOperations() {
		testOverflowMatrix(evaluator::plusSet);
		testOverflowMatrix(evaluator::minusSet);
		testOverflowMatrix(evaluator::multiplySet);
		testOverflowMatrix(evaluator::divideSet);
		testOverflowMatrix(evaluator::logBaseSet);
		testOverflowMatrix(evaluator::powSet);
		testOverflowMatrix(evaluator::fmodSet);
		testOverflowMatrix(evaluator::intersectSet);
		testOverflowMatrix(evaluator::unionSet, false);
	}

	private void testOverflowMatrix(BinaryIntervalSetOperator operator) {
		testOverflowMatrix(operator, true);
	}

	private void testOverflowMatrix(BinaryIntervalSetOperator operator,
			boolean emptyShouldBeEmpty) {
		assertEquals(overflow(), operator.exec(IntervalSet.connected(1, 2),
				overflow()));
		assertEquals(overflow(), operator.exec(overflow(),
				IntervalSet.connected(1, 2)));
		assertEquals(overflow(), operator.exec(IntervalSet.inverted(1, 2),
				overflow()));
		assertEquals(overflow(), operator.exec(overflow(), IntervalSet.inverted(1, 2)));
		assertEquals(overflow(), operator.exec(IntervalSet.whole(), overflow()));
		assertEquals(overflow(), operator.exec(overflow(), IntervalSet.whole()));
		if (emptyShouldBeEmpty) {
			assertEquals(empty(), operator.exec(overflow(), empty()));
			assertEquals(empty(), operator.exec(empty(), overflow()));
		} else {
			assertEquals(overflow(), operator.exec(overflow(), empty()));
			assertEquals(overflow(), operator.exec(empty(), overflow()));
		}
	}

	@Test
	@Issue("APPS-7561")
	public void directLogSetExpSetMayOverflowAfterExpressionStructureIsLost() {
		// Direct interval-set composition does not see the source expression tree;
		// expression-level ln(exp(x)) recovery is covered by interval plot tests.
		assertEquals(overflow(), lnExp(connected(746, 750)));
		assertNotEquals(overflow(), lnExp(connected(700, 709)));
		assertEquals(overflow(), lnExp(connected(-750, -746)));
	}

	@Test
	@Issue("APPS-7561")
	public void lnExpOfSubnormalExpShouldNotProduceNegativeInfinity() {
		Interval result = connectedInterval(lnExp(connected(-745, -745)));

		assertFalse(Double.isInfinite(result.getLow()));
		assertFalse(Double.isInfinite(result.getHigh()));
	}

	private IntervalSet lnExp(IntervalSet x) {
		return evaluator.logSet(evaluator.expSet(x));
	}

}
