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

package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.PRECISION;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.geogebra.common.kernel.interval.operators.RMath;
import org.junit.jupiter.api.Test;

class IntervalTest {

	@Test
	void testValidInterval() {
		Interval interval = new Interval(1, 2);
		assertTrue(interval.getLow() == 1 && interval.getHigh() == 2);
	}

	@Test
	void testInvalidIntervals() {
		Interval interval = new Interval(2, 1);
		assertTrue(interval.isUndefined());
	}

	@Test
	void testAdd() {
		assertEquals(interval(1, 9),
				interval(-3, 2)
						.add(interval(4, 7)));
	}

	@Test
	void testSub() {
		assertEquals(interval(-5, 5),
				interval(-1, 3)
						.subtract(interval(-2, 4)));
	}

	@Test
	void isWhole() {
		Interval interval = new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		assertTrue(interval.isWhole());
	}

	@Test
	void testSingleton() {
		Interval interval = new Interval(2);
		assertTrue(interval.isSingleton());
	}

	@Test
	void testWholeIsNotSingleton() {
		assertFalse(IntervalConstants.whole().isSingleton());
	}

	@Test
	void testIsZeroWithinPrecision() {
		double delta = RMath.prev(PRECISION);
		assertTrue(interval(0, 0).isZero());
		assertTrue(interval(0, delta).isZero());
		assertTrue(interval(-delta, 0).isZero());
		assertTrue(interval(-delta, delta).isZero());
		assertTrue(interval(delta).isZero());
	}

	@Test
	void testHasZero() {
		assertTrue(new Interval(-1, 1).hasZero());
		assertTrue(new Interval(0, 1).hasZero());
		assertTrue(new Interval(-1, 0).hasZero());
		assertTrue(whole().hasZero());
		assertTrue(IntervalConstants.zero().hasZero());
	}

	@Test
	void testHasNotZero() {
		assertFalse(new Interval(2, 6).hasZero());
		assertFalse(new Interval(-2, -0.1).hasZero());
		assertFalse(new Interval(Double.NEGATIVE_INFINITY, -2).hasZero());
		assertFalse(new Interval(1, Double.POSITIVE_INFINITY).hasZero());
	}

	@Test
	void testEmpty() {
		assertTrue(new Interval(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY).isUndefined());
	}

	@Test
	void testEmptyConstant() {
		assertTrue(undefined().isUndefined());
	}

	@Test
	void testOverlap() {
		Interval a = new Interval(-1, 1);
		Interval b = new Interval(-0.5, 0.5);
		Interval c = new Interval(0.6, 1.5);
		assertTrue(a.isOverlap(b));
		assertTrue(a.isOverlap(c));
		assertFalse(b.isOverlap(c));
	}

	@Test
	void testNotOverlapWithEmptyInterval() {
		Interval a = new Interval(-1, 1);
		assertFalse(a.isOverlap(undefined()));
		assertFalse(undefined().isOverlap(a));
	}

	@Test
	void testNegative() {
		assertEquals(interval(-3, -2), interval(2, 3).negative());
		assertEquals(interval(-2, 1), interval(-1, 2).negative());
		assertEquals(interval(2, 3), interval(-3, -2).negative());
		assertTrue(whole().negative().isWhole());
	}

	@Test
	void testIntervalToString() {
		assertEquals("Interval [-1.0, 1.0]", interval(-1, 1).toString().trim());
	}

	@Test
	void testIntervalSingletonToString() {
		assertEquals("Interval [-1.0]", new Interval(-1).toString().trim());
	}

	@Test
	void testEmptyIntervalToString() {
		assertEquals("Interval []", undefined().toString().trim());
	}

	@Test
	void testWholeIntervalToString() {
		assertEquals("Interval [-Infinity, Infinity]", whole().toString().trim());
	}

	@Test
	void testHashCode() {
		assertEquals(Objects.hash(1d, 2d),
				interval(1, 2).hashCode());
	}
}
