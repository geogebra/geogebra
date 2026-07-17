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
 
package org.geogebra.common.kernel.interval.operators;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.aroundZero;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.around;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.geogebra.common.kernel.interval.IntervalSet.overflow;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.fromLegacy;
import static org.geogebra.common.kernel.interval.IntervalSetOps.inverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.toLegacy;
import static org.geogebra.common.kernel.interval.LegacyIntervalAdapter.legacyInverted;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.junit.jupiter.api.Test;

class IntervalMiscTest {

	private final IntervalNodeEvaluator evaluator = new IntervalNodeEvaluator();

	@Test
	void testExp() {
		assertEquals(interval(0.3678794411714423, 2.7182818284590455),
				evaluator.exp(interval(-1, 1)));
		assertEquals(interval(0.04978706836786394, 20.08553692318767),
				evaluator.exp(interval(-3, 3)));
		assertEquals(overflow(), evaluator.expSet(overflow()));
		assertTrue(evaluator.expSet(connected(711, 711)).isOverflow());
		assertTrue(evaluator.expSet(connected(711, 712)).isOverflow());
		assertTrue(evaluator.expSet(connected(700, 711)).isOverflow());
		assertTrue(evaluator.expSet(connected(-1, 711)).isOverflow());
		assertTrue(evaluator.expSet(connected(-711, 711)).isOverflow());
		assertTrue(evaluator.expSet(connected(Double.MAX_VALUE, Double.MAX_VALUE)).isOverflow());
		assertFalse(evaluator.expSet(connected(700, 700)).isOverflow());
		assertFalse(evaluator.expSet(connected(709, 709)).isOverflow());
	}

	@Test
	void name() {
		assertTrue(evaluator.expSet(connected(-800, -746)).isOverflow());
	}

	@Test
	void expLowerBoundShouldStayPositiveForSubnormalResult() {
		IntervalSet result = evaluator.expSet(connected(-745, -743.75));

		assertTrue(connectedInterval(result).getLow() > 0);
	}

	@Test
	void lnExpShouldNotProduceNegativeInfinityFromSubnormalExp() {
		IntervalSet result = evaluator.logSet(evaluator.expSet(connected(-745, -743.75)));

		assertFalse(Double.isInfinite(connectedInterval(result).getLow()));
	}

	@Test
	void expOfInvertedShouldNotOverflow() {
		assertFalse(evaluator.expSet(inverted(-711, 711)).isOverflow());
		assertFalse(evaluator.expSet(inverted(-1000, 1000)).isOverflow());
		assertFalse(evaluator.expSet(inverted(-Double.MAX_VALUE / 2,
				Double.MAX_VALUE / 2)).isOverflow());

	}

	@Test
	void expOfInvertedShouldKeepTrueUnboundedResultDistinctFromOverflow() {
		IntervalSet result = evaluator.expSet(inverted(-711, 711));
		assertFalse(result.isOverflow());
		assertTrue(result.isConnected());
		assertEquals(0, connectedInterval(result).getLow(), 0);
		assertTrue(Double.isInfinite(connectedInterval(result).getHigh()));

	}

	@Test
	void testExpWrapperMatchesExpSet() {
		Interval input = interval(-3, 3);
		assertEquals(evaluator.exp(input), toLegacy(evaluator.expSet(fromLegacy(input))));
	}

	@Test
	void testLog() {
		assertEquals(interval(0, 0), evaluator.log(interval(1, 1)));
		assertEquals(interval(0, 3), evaluator.log(interval(1, Math.exp(3))));
		assertEquals(IntervalConstants.undefined(),
				evaluator.log(interval(NEGATIVE_INFINITY, -1)));
		assertEquals(overflow(), evaluator.logSet(overflow()));
	}

	@Test
	void testLog10() {
		assertEquals(interval(0, 0), evaluator.log10(interval(1, 1)));
		assertEquals(interval(0, 1), evaluator.log10(interval(1, 10)));
		assertEquals(interval(0, 2), evaluator.log10(interval(1, 100)));
		assertEquals(overflow(), evaluator.log10Set(overflow()));

	}

	@Test
	void testLog2() {
		assertEquals(interval(0, 0), evaluator.log2(interval(1, 1)));
		assertEquals(interval(0, 1), evaluator.log2(interval(1, 2)));
		assertEquals(interval(0, 3), evaluator.log2(interval(1, 8)));
		assertEquals(overflow(), evaluator.log2Set(overflow()));
	}

	@Test
	void testLogB() {
		assertEquals(interval(0, 0), evaluator.logBase(interval(3, 3),
				interval(1, 1)));
		assertEquals(interval(2, 3), evaluator.logBase(interval(2, 2),
				interval(4, 8)));
		assertEquals(interval(2, 4), evaluator.logBase(interval(2, 4),
				interval(16, 16)));
		assertEquals(undefined(), evaluator.logBase(interval(1, 1),
				interval(3, 3)));
		assertEquals(overflow(), evaluator.logBaseSet(connected(1, 2),
				overflow()));
		assertEquals(overflow(), evaluator.logBaseSet(overflow(), connected(1, 2)));
		assertEquals(overflow(), evaluator.logBaseSet(overflow(), overflow()));

	}

	@Test
	void testIntersection() {
		assertTrue(evaluator.intersect(interval(-1, 1), interval(5, 7)).isUndefined());
		assertTrue(evaluator.intersect(interval(-1, 1), undefined()).isUndefined());
		assertEquals(interval(0, 1),
				evaluator.intersect(interval(-1, 1), interval(0, 7)));
		assertEquals(interval(-1, 1),
				evaluator.intersect(whole(), interval(-1, 1)));
		assertEquals(interval(2, 4),
				evaluator.intersect(legacyInverted(-1, 1), interval(2, 4)));

		assertEquals(overflow(), evaluator.intersectSet(connected(1, 2),
				overflow()));
		assertEquals(overflow(), evaluator.intersectSet(overflow(), connected(1, 2)));
		assertEquals(overflow(), evaluator.intersectSet(overflow(), overflow()));

	}

	@Test
	void testUnion() {
		assertEquals(interval(1, 4),
				evaluator.union(interval(1, 3), interval(2, 4)));
		assertEquals(whole(),
				evaluator.union(whole(), interval(1, 3)));
		assertEquals(whole(),
				evaluator.union(legacyInverted(-1, 1), interval(-2, 2)));
		assertEquals(overflow(),
				evaluator.unionSet(connected(-1, 1), overflow()));
		assertEquals(overflow(),
				evaluator.unionSet(overflow(), connected(-1, 1)));
		assertEquals(overflow(),
				evaluator.unionSet(overflow(), overflow()));
	}

	@Test
	void testNonOverlappingUnionShouldBeEmpty() {
		assertEquals(undefined(), evaluator.union(interval(1, 2), interval(3, 4)));
	}

	@Test
	void testAbs() {
		assertEquals(interval(0, 1), evaluator.abs(interval(-1, 1)));
		assertEquals(interval(2, 3), evaluator.abs(interval(-3, -2)));
		assertEquals(interval(2, 3), evaluator.abs(interval(2, 3)));
		assertEquals(overflow(), evaluator.absSet(overflow()));
	}

	@Test
	void testAbs1() {
		assertEquals(interval(4, POSITIVE_INFINITY), evaluator.abs(legacyInverted(-4, 5)));
		assertEquals(interval(5, POSITIVE_INFINITY), evaluator.abs(legacyInverted(-8, 5)));
	}

	@Test
	void testLogXInverseAzZero() {
		Interval x = aroundZero();
		Interval xInverse = evaluator.multiplicativeInverse(x);
		assertEquals(interval(9.210340371976182, POSITIVE_INFINITY), evaluator.log(xInverse));
	}

	@Test
	void testZeroDividedByLnAroundOne() {
		assertEquals(zero(), evaluator.divide(zero(), evaluator.log(around(0.985375))));
		assertEquals(zero(), evaluator.divide(zero(), evaluator.log(around(1.015625))));
	}

	// APPS-4683
	@Test
	void testZeroDivLnX() {
		Interval x1 = interval(0.9895833333333334, 1.0);
		Interval x2 = interval(0.9999999999999999, 1.0104166666666665);
		Interval log1 = evaluator.log(x1);
		Interval log2 = evaluator.log(x2);
		Interval div1 = evaluator.divide(zero(), log1);
		Interval div2 = evaluator.divide(zero(), log2);
		assertEquals(whole(), div1);
		assertEquals(whole(), div2);
	}

	@Test
	void lnInverseMultiplyZeroNegativeShouldBeUndefined() {
		Interval x = interval(NEGATIVE_INFINITY, -IntervalConstants.PRECISION);
		Interval log = evaluator.log(x);
		Interval inverse = evaluator.inverse(log);
		Interval multiply = evaluator.multiply(zero(), inverse);
		assertEquals(undefined(), multiply);
	}

	@Test
	void lnInverseMultiplyZeroPositiveShouldBeZero() {
		Interval x = interval(0, POSITIVE_INFINITY);
		Interval log = evaluator.log(x);
		Interval inverse = evaluator.inverse(log);
		Interval multiply = evaluator.multiply(zero(), inverse);
		assertEquals(zero(), multiply);
	}

	@Test
	void lnInverseMultiplyZeroAroundOneShouldBeZero() {
		Interval x = interval(1.0, 1.015625);
		Interval log = evaluator.log(x);
		Interval inverse = evaluator.inverse(log);
		Interval multiply = evaluator.multiply(zero(), inverse);
		assertEquals(zero(), multiply);
	}
}
