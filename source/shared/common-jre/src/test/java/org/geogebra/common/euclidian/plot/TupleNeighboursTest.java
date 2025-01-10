package org.geogebra.common.euclidian.plot;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.junit.Test;

public class TupleNeighboursTest {
	private static final Interval leftX = new Interval(1.0, 2.0);
	private static final Interval leftY = new Interval(1.5, 2.5);
	private static final Interval currentX = new Interval(3.0, 4.0);
	private static final Interval currentY = new Interval(3.5, 4.5);
	private static final Interval rightX = new Interval(5.0, 6.0);
	private static final Interval rightY = new Interval(5.5, 6.5);
	private static final TupleNeighbours neighbours = new TupleNeighbours(
			new IntervalTuple(leftX, leftY),
			new IntervalTuple(currentX, currentY),
			new IntervalTuple(rightX, rightY)
	);

	@Test
	public void testLeftXLow() {
		assertEquals(neighbours.leftXLow(), leftX.getLow(), 0);
	}

	@Test
	public void testLeftXHigh() {
		assertEquals(neighbours.leftXHigh(), leftX.getHigh(), 0);
	}

	@Test
	public void testLeftYLow() {
		assertEquals(neighbours.leftYLow(), leftY.getLow(), 0);
	}

	@Test
	public void testLeftYHigh() {
		assertEquals(neighbours.leftYHigh(), leftY.getHigh(), 0);
	}

	@Test
	public void testCurrentXLow() {
		assertEquals(neighbours.currentXLow(), currentX.getLow(), 0);
	}

	@Test
	public void testCurrentXHigh() {
		assertEquals(neighbours.currentXHigh(), currentX.getHigh(), 0);
	}

	@Test
	public void testCurrentYLow() {
		assertEquals(neighbours.currentYLow(), currentY.getLow(), 0);
	}

	@Test
	public void testCurrentYHigh() {
		assertEquals(neighbours.currentYHigh(), currentY.getHigh(), 0);
	}

	@Test
	public void testRightXLow() {
		assertEquals(neighbours.rightXLow(), rightX.getLow(), 0);
	}

	@Test
	public void testRightXHigh() {
		assertEquals(neighbours.rightXHigh(), rightX.getHigh(), 0);
	}

	@Test
	public void testRightYLow() {
		assertEquals(neighbours.rightYLow(), rightY.getLow(), 0);
	}

	@Test
	public void testRightYHigh() {
		assertEquals(neighbours.rightYHigh(), rightY.getHigh(), 0);
	}

}