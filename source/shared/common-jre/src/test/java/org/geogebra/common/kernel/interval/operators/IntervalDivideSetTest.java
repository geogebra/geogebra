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

import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.fromLegacy;
import static org.geogebra.common.kernel.interval.IntervalSetOps.inverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.whole;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.junit.jupiter.api.Test;

class IntervalDivideSetTest {

	private final IntervalNodeEvaluator evaluator = new IntervalNodeEvaluator();
	private final IntervalDivide divide = new IntervalDivide(evaluator);

	@Test
	void computeSetReturnsConnectedForFiniteNonZeroDivisor() {
		IntervalSet result = divide.computeSet(connected(2, 4), connected(2, 4));

		assertEquals(connected(0.5, 2), result);
	}

	@Test
	void computeSetMatchesLegacyDivideForConnectedDivisor() {
		Interval numerator = new Interval(2, 4);
		Interval divisor = new Interval(2, 4);

		IntervalSet result = divide.computeSet(fromLegacy(numerator), fromLegacy(divisor));

		assertEquals(fromLegacy(divide.compute(numerator, divisor)), result);
	}

	@Test
	void computeSetReturnsEmptyForEmptyNumerator() {
		IntervalSet result = divide.computeSet(empty(), connected(1, 2));

		assertEquals(empty(), result);
	}

	@Test
	void computeSetReturnsEmptyForEmptyDivisor() {
		IntervalSet result = divide.computeSet(connected(1, 2), empty());

		assertEquals(empty(), result);
	}

	@Test
	void computeSetReturnsEmptyForWholeByUndefined() {
		IntervalSet result = divide.computeSet(whole(), empty());

		assertEquals(empty(), result);
	}

	@Test
	void computeSetReturnsEmptyForWholeByEmpty() {
		IntervalSet result = divide.computeSet(whole(), empty());
		assertEquals(empty(), result);
	}

	@Test
	void computeSetReturnsEmptyForEmptyByWhole() {
		IntervalSet result = divide.computeSet(empty(), whole());

		assertEquals(empty(), result);
	}

	@Test
	void computeSetReturnsInvertedForDivisorContainingZero() {
		IntervalSet result = divide.computeSet(connected(2, 4), connected(-1, 2));

		assertTrue(result.isInverted());
	}

	@Test
	void computeSetReturnsWholeForZeroByZero() {
		IntervalSet result = divide.computeSet(connected(0, 0), connected(0, 0));

		assertEquals(whole(), result);
	}

	@Test
	void computeSetReturnsOverflowForPositiveByExactZero() {
		IntervalSet result = divide.computeSet(connected(2, 2), connected(0, 0));

		assertEquals(IntervalSet.overflow(), result);
	}

	@Test
	void computeSetReturnsOverflowForNegativeByExactZero() {
		IntervalSet result = divide.computeSet(connected(-2, -2), connected(0, 0));

		assertEquals(IntervalSet.overflow(), result);
	}

	@Test
	void computeSetHandlesInvertedNumeratorWithZeroContainingDivisor() {
		IntervalSet result = divide.computeSet(inverted(-1, 1), connected(-1, 1));

		assertEquals(inverted(-1, 1), result);
	}

}
