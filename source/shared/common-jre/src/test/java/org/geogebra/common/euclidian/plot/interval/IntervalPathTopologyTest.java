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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.kernel.interval.IntervalSetOps;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.junit.jupiter.api.Test;

class IntervalPathTopologyTest {

	private final EuclidianViewBoundsMock bounds = new EuclidianViewBoundsMock(0, 10, 0, 3);
	private final IntervalPathPlotterMock gp = new IntervalPathPlotterMock(bounds);

	@Test
	void emptyYIntervalDoesNotDrawAndBreaksJoin() {
		IntervalPath path = pathWith(
				tuple(0, 1, 1, 2),
				new IntervalTuple(connected(1, 2), empty()),
				tuple(2, 3, 1, 2));

		path.update();

		assertAll(
				() -> assertEquals(5, gp.getLog().size()),
				() -> assertEquals(2, count(IntervalPathMockEntry.PathOperation.MOVE_TO)),
				() -> assertEquals(2, count(IntervalPathMockEntry.PathOperation.LINE_TO)));
	}

	@Test
	void connectedYIntervalUsesIndependentVerticalPath() {
		IntervalPath path = pathWith(tuple(0, 1, 1, 2));

		path.update();

		assertAll(
				() -> assertEquals(3, gp.getLog().size()),
				() -> assertEquals(
						new IntervalPathMockEntry(IntervalPathMockEntry.PathOperation.MOVE_TO, 1,
								1),
						gp.getLog().get(1)),
				() -> assertEquals(
						new IntervalPathMockEntry(IntervalPathMockEntry.PathOperation.LINE_TO, 1,
								2),
						gp.getLog().get(2)));
	}

	@Test
	void wholeYIntervalUsesWholeBranchWhenDrawnIndependently() {
		IntervalPath path = pathWith(new IntervalTuple(connected(0, 1), IntervalSetOps.whole()));

		path.update();

		assertAll(
				() -> assertEquals(3, gp.getLog().size()),
				() -> assertEquals(
						new IntervalPathMockEntry(IntervalPathMockEntry.PathOperation.MOVE_TO, 0,
								0),
						gp.getLog().get(1)),
				() -> assertEquals(
						new IntervalPathMockEntry(IntervalPathMockEntry.PathOperation.LINE_TO, 0,
								3),
						gp.getLog().get(2)));
	}

	@Test
	void invertedYIntervalUsesInvertedBranch() {
		IntervalPath path = pathWith(new IntervalTuple(connected(0, 1), inverted(1, 2
		)));

		path.update();

		assertAll(
				() -> assertEquals(5, gp.getLog().size()),
				() -> assertEquals(2, count(IntervalPathMockEntry.PathOperation.MOVE_TO)),
				() -> assertEquals(2, count(IntervalPathMockEntry.PathOperation.LINE_TO)),
				() -> assertEquals(
						new IntervalPathMockEntry(IntervalPathMockEntry.PathOperation.MOVE_TO, 1,
								3),
						gp.getLog().get(1)),
				() -> assertEquals(
						new IntervalPathMockEntry(IntervalPathMockEntry.PathOperation.LINE_TO, 1,
								2),
						gp.getLog().get(2)),
				() -> assertEquals(
						new IntervalPathMockEntry(IntervalPathMockEntry.PathOperation.MOVE_TO, 0,
								0),
						gp.getLog().get(3)),
				() -> assertEquals(
						new IntervalPathMockEntry(IntervalPathMockEntry.PathOperation.LINE_TO, 1,
								1),
						gp.getLog().get(4)));
	}

	@Test
	void wholeIntervalInSequenceResetsJoinForFollowingTuple() {
		IntervalPath path = pathWith(
				tuple(0, 1, 1, 2),
				new IntervalTuple(connected(1, 2), IntervalSetOps.whole()),
				tuple(2, 3, 1, 2));

		path.update();

		assertAll(
				() -> assertEquals(7, gp.getLog().size()),
				() -> assertEquals(3, count(IntervalPathMockEntry.PathOperation.MOVE_TO)),
				() -> assertEquals(3, count(IntervalPathMockEntry.PathOperation.LINE_TO)));
	}

	private IntervalPath pathWith(IntervalTuple... tuples) {
		IntervalTupleList list = new IntervalTupleList();
		for (IntervalTuple tuple : tuples) {
			list.add(tuple);
		}
		return new IntervalPath(gp, bounds, new QueryFunctionDataImpl(list));
	}

	private IntervalTuple tuple(double xLow, double xHigh, double yLow, double yHigh) {
		return new IntervalTuple(connected(xLow, xHigh), connected(yLow, yHigh));
	}

	private long count(IntervalPathMockEntry.PathOperation operation) {
		return gp.getLog().stream().filter(entry -> entry.operation() == operation).count();
	}
}
