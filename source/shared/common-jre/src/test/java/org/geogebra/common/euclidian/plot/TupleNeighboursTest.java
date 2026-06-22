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

package org.geogebra.common.euclidian.plot;

import static org.geogebra.common.kernel.interval.IntervalSet.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.inverted;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.kernel.interval.IntervalSetOps;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.junit.Test;

public class TupleNeighboursTest {
	private static final IntervalSet leftX = connected(1.0, 2.0);
	private static final IntervalSet leftY = connected(1.5, 2.5);
	private static final IntervalSet currentX = connected(3.0, 4.0);
	private static final IntervalSet currentY = connected(3.5, 4.5);
	private static final IntervalSet rightX = connected(5.0, 6.0);
	private static final IntervalSet rightY = connected(5.5, 6.5);
	private static final TupleNeighbours neighbours = new TupleNeighbours(
			new IntervalTuple(leftX, leftY),
			new IntervalTuple(currentX, currentY),
			new IntervalTuple(rightX, rightY)
	);

	@Test
	public void testLeftXLow() {
		assertEquals(neighbours.leftXLow(), connectedInterval(leftX).getLow(), 0);
	}

	@Test
	public void testLeftXHigh() {
		assertEquals(neighbours.leftXHigh(), connectedInterval(leftX).getHigh(), 0);
	}

	@Test
	public void testLeftYLow() {
		assertEquals(neighbours.leftYLow(), connectedInterval(leftY).getLow(), 0);
	}

	@Test
	public void testLeftYHigh() {
		assertEquals(neighbours.leftYHigh(), connectedInterval(leftY).getHigh(), 0);
	}

	@Test
	public void testCurrentXLow() {
		assertEquals(neighbours.currentXLow(), connectedInterval(currentX).getLow(), 0);
	}

	@Test
	public void testCurrentXHigh() {
		assertEquals(neighbours.currentXHigh(), connectedInterval(currentX).getHigh(), 0);
	}

	@Test
	public void testCurrentYLow() {
		assertEquals(neighbours.currentYLow(), connectedInterval(currentY).getLow(), 0);
	}

	@Test
	public void testCurrentYHigh() {
		assertEquals(neighbours.currentYHigh(), connectedInterval(currentY).getHigh(), 0);
	}

	@Test
	public void testRightXLow() {
		assertEquals(neighbours.rightXLow(), connectedInterval(rightX).getLow(), 0);
	}

	@Test
	public void testRightXHigh() {
		assertEquals(neighbours.rightXHigh(), connectedInterval(rightX).getHigh(), 0);
	}

	@Test
	public void testRightYLow() {
		assertEquals(neighbours.rightYLow(), connectedInterval(rightY).getLow(), 0);
	}

	@Test
	public void testRightYHigh() {
		assertEquals(neighbours.rightYHigh(), connectedInterval(rightY).getHigh(), 0);
	}

	@Test
	public void testLegacyConstructorPopulatesConnectedTopology() {
		assertEquals(leftY, neighbours.leftTopology());
		assertEquals(currentY, neighbours.currentTopology());
		assertEquals(rightY, neighbours.rightTopology());
	}

	@Test
	public void testHasLeftAndRightUseTopologyDefinedness() {
		TupleNeighbours topologyAware = new TupleNeighbours();
		topologyAware.set(new IntervalTuple(leftX, leftY), new IntervalTuple(currentX, currentY),
				new IntervalTuple(rightX, rightY));

		assertTrue(topologyAware.hasLeft());
		assertTrue(topologyAware.hasRight());
	}

	@Test
	public void testTopologyAccessorsDistinguishWholeAndInvertedNeighbours() {
		TupleNeighbours topologyAware = new TupleNeighbours(
				new IntervalTuple(leftX, IntervalSetOps.whole()),
				new IntervalTuple(currentX, currentY),
				new IntervalTuple(rightX, inverted(-1, 1)));

		assertTrue(topologyAware.isLeftWhole());
		assertFalse(topologyAware.isLeftInverted());
		assertTrue(topologyAware.isRightInverted());
		assertFalse(topologyAware.isRightWhole());
	}

	@Test
	public void testEmptyTopologyReportedExplicitly() {
		TupleNeighbours topologyAware = new TupleNeighbours(
				new IntervalTuple(leftX, empty()),
				new IntervalTuple(currentX, currentY),
				null);

		assertTrue(topologyAware.isLeftEmpty());
		assertTrue(topologyAware.isRightEmpty());
		assertFalse(topologyAware.hasLeft());
		assertFalse(topologyAware.hasRight());
	}
}
