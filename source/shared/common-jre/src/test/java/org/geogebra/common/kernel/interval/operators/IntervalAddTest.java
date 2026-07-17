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

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.inverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.leftRayFromInverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.rightRayFromInverted;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.LegacyIntervalAdapter.legacyInverted;
import static org.geogebra.common.kernel.interval.operators.IntervalDivide.next;
import static org.geogebra.common.kernel.interval.operators.IntervalDivide.prev;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.junit.jupiter.api.Test;

class IntervalAddTest {

	@Test
	void addNegativeInfinityWithFiniteToNegativeInfinityWithFinite() {
		assertEquals(negativeInf(4.68),
				negativeInf(1.23).add(negativeInf(3.45)));
		assertEquals(negativeInf(0),
				negativeInf(-1.23).add(negativeInf(1.23)));
	}

	private Interval negativeInf(double v) {
		return interval(Double.NEGATIVE_INFINITY, v);
	}

	@Test
	void addNegativeInfinityWithFiniteToFiniteInterval() {
		assertEquals(negativeInf(45.67 + 56.78),
				negativeInf(45.67).add(interval(12.34, 56.78)));
		assertEquals(negativeInf(91.34),
				negativeInf(45.67).add(interval(12.34, 45.67)));
	}

	@Test
	void addNegativeInfinityWithFiniteToFiniteOpenToPositiveInfinity() {
		assertEquals(whole(),
				negativeInf(45.67).add(interval(12.34, Double.POSITIVE_INFINITY)));
	}

	@Test
	void addWholeToAnything() {
		assertEquals(whole(), negativeInf(34.56).add(whole()));
		assertEquals(whole(), interval(-12.34, 1E32).add(whole()));
		assertEquals(whole(), interval(-12.34, Double.POSITIVE_INFINITY).add(whole()));
		assertEquals(whole(), whole().add(whole()));
	}

	@Test
	void addFiniteToNegativeInfinityAndFinite() {
		assertEquals(negativeInf(1E-2 + 1234.567),
				interval(0, 1E-2).add(negativeInf(1234.567)));
	}

	@Test
	void addFiniteToFinite() {
		addFiniteToFinite(12.34, 56.78, 78.97, 100.12);
		addFiniteToFinite(4, -56.78, 78.97, 100.12);
		addFiniteToFinite(4, -56.78, -1E-3, 1E-2);
	}

	private void addFiniteToFinite(double a1, double a2, double b1, double b2) {
		assertEquals(interval(prev(a1 + b1), next(a2 + b2)),
				interval(a1, a2).add(interval(b1, b2)));
	}

	@Test
	void addFiniteToFiniteOpenToPositiveInfinity() {
		addFiniteToFiniteOpenToPositiveInfinity(12.34, 56.78, 2.1);
		addFiniteToFiniteOpenToPositiveInfinity(-12.34, 56.78, 0);
		addFiniteToFiniteOpenToPositiveInfinity(-56.34, -12.78, 1E234);
	}

	private void addFiniteToFiniteOpenToPositiveInfinity(double a1, double a2, double b1) {
		assertEquals(interval(prev(a1 + b1), Double.POSITIVE_INFINITY),
				interval(a1, a2)
						.add(interval(b1, Double.POSITIVE_INFINITY)));
	}

	@Test
	void addToUndefinedShouldBeUndefined() {
		assertEquals(undefined(),
				undefined().add(interval(1E123, Double.POSITIVE_INFINITY)));
	}

	@Test
	void testAddToInvertedSet() {
		IntervalSet set = inverted(legacyInverted(-3.45, 78.97)
				.add(interval(12.34, 56.78)));
		assertEquals(connected(Double.NEGATIVE_INFINITY, 8.89),
				leftRayFromInverted(set));
		assertEquals(connected(135.75, Double.POSITIVE_INFINITY),
				rightRayFromInverted(set));
	}

	@Test
	void testAddInvertedSetTo() {
		IntervalSet set = inverted(interval(12.34, 56.78)
				.add(legacyInverted(-3.45, 78.97)));
		assertEquals(connected(Double.NEGATIVE_INFINITY, 8.89),
				leftRayFromInverted(set));
		assertEquals(connected(135.75, Double.POSITIVE_INFINITY),
				rightRayFromInverted(set));
	}

}
