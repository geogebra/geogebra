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

package org.geogebra.common.euclidian.plot.interval;

import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.inverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.whole;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.euclidian.plot.TupleNeighbours;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.junit.jupiter.api.Test;

class QueryFunctionDataImplTest {

	@Test
	void yTopologyAtReturnsConnectedTopology() {
		QueryFunctionDataImpl data = queryWith(tuple(connected(0, 1), connected(1, 2)));

		assertEquals(IntervalSet.connected(1, 2), data.yTopologyAt(0));
	}

	@Test
	void yTopologyAtReturnsInvertedTopology() {
		QueryFunctionDataImpl data = queryWith(tuple(connected(0, 1), inverted(1, 2)));

		assertEquals(IntervalSet.inverted(1, 2), data.yTopologyAt(0));
	}

	@Test
	void yTopologyAtReturnsWholeTopology() {
		QueryFunctionDataImpl data = queryWith(tuple(connected(0, 1), whole()));

		assertEquals(whole(), data.yTopologyAt(0));
	}

	@Test
	void yTopologyAtReturnsEmptyTopologyForUndefinedValue() {
		QueryFunctionDataImpl data = queryWith(tuple(connected(0, 1), empty()));

		assertEquals(IntervalSet.empty(), data.yTopologyAt(0));
	}

	@Test
	void yTopologyAtReturnsEmptyTopologyOutOfRange() {
		QueryFunctionDataImpl data = queryWith(tuple(connected(0, 1), connected(1, 2)));

		assertEquals(IntervalSet.empty(), data.yTopologyAt(1));
	}

	@Test
	void yTopologyAtReturnsEmptyTopologyForNegativeIndex() {
		QueryFunctionDataImpl data = queryWith(tuple(connected(0, 1), connected(1, 2)));

		assertEquals(IntervalSet.empty(), data.yTopologyAt(-1));
	}

	@Test
	void isInvertedAtPreservesCompatibilityBehavior() {
		QueryFunctionDataImpl data = queryWith(
				tuple(connected(0, 1), connected(1, 2)),
				tuple(connected(1, 2), inverted(1, 2)));

		assertAll(
				() -> assertFalse(data.isInvertedAt(0)),
				() -> assertTrue(data.isInvertedAt(1)),
				() -> assertTrue(data.isInvertedAt(2)));
	}

	@Test
	void hasNextIsFalseForLastTuple() {
		QueryFunctionDataImpl data = queryWith(
				tuple(connected(0, 1), connected(1, 2)),
				tuple(connected(1, 2), inverted(2, 3)));

		assertAll(
				() -> assertTrue(data.hasNext(0)),
				() -> assertFalse(data.hasNext(1)));
	}

	@Test
	void isWholeAtPreservesCompatibilityBehavior() {
		QueryFunctionDataImpl data = queryWith(
				tuple(connected(0, 1), connected(1, 2)),
				tuple(connected(1, 2), whole()));

		assertAll(
				() -> assertFalse(data.isWholeAt(0)),
				() -> assertTrue(data.isWholeAt(1)),
				() -> assertTrue(data.isWholeAt(2)));
	}

	@Test
	void nonDegeneratedPreservesCompatibilityBehavior() {
		IntervalSet invertedPositiveInfinity = inverted(Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY);
		QueryFunctionDataImpl data = queryWith(
				tuple(connected(0, 1), connected(1, 2)),
				tuple(connected(1, 2), invertedPositiveInfinity));

		assertAll(
				() -> assertTrue(data.nonDegenerated(0)),
				() -> assertFalse(data.nonDegenerated(1)));
	}

	@Test
	void neighboursAtPopulatesExplicitTopologyForEdges() {
		IntervalSet inverted = inverted(2, 3);
		QueryFunctionDataImpl data = queryWith(
				tuple(connected(0, 1), connected(1, 2)),
				tuple(connected(1, 2), inverted));

		TupleNeighbours first = data.neighboursAt(0);
		assertAll(
				() -> assertFalse(first.hasLeft(), "Left should be empty"),
				() -> assertEquals(IntervalSet.connected(1, 2), first.currentTopology()),
				() -> assertEquals(IntervalSet.inverted(2, 3), first.rightTopology()));

		TupleNeighbours second = data.neighboursAt(1);

		assertAll(
				() -> assertEquals(IntervalSet.connected(1, 2), second.leftTopology()),
				() -> assertEquals(IntervalSet.inverted(2, 3), second.currentTopology()),
				() -> assertEquals(IntervalSet.empty(), second.rightTopology()));
	}

	private QueryFunctionDataImpl queryWith(IntervalTuple... tuples) {
		IntervalTupleList list = new IntervalTupleList();
		for (IntervalTuple tuple : tuples) {
			list.add(tuple);
		}
		return new QueryFunctionDataImpl(list);
	}

	private IntervalTuple tuple(IntervalSet x, IntervalSet y) {
		return new IntervalTuple(x, y);
	}
}
