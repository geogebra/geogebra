package org.geogebra.common.kernel.interval.evaluators;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

import org.geogebra.common.kernel.interval.Interval;
import org.junit.Test;

public class DiscreteSpaceCenteredTest {

	@Test
	public void testCreationIntegerStep() {
		DiscreteSpace space = new DiscreteSpaceCentered(0, 5, 5, 1);
		valuesShouldBe(space, -5, 1, 10);
	}

	@Test
	public void testCreationDoubleStep() {
		DiscreteSpace space = new DiscreteSpaceCentered(0, 5, 5,
				0.25);
		valuesShouldBe(space, -1.25, 0.25, 10);
	}

	@Test
	public void testCreationDoubleMinus5To5() {
		DiscreteSpace space = new DiscreteSpaceCentered(0, 20, 20,
				0.25);
		valuesShouldBe(space, -5, 0.25, 40);
	}
	@Test
	public void testMoveLeftWith4Steps() {
		DiscreteSpace space = new DiscreteSpaceCentered(0, 20, 20,
				0.25);
		space.moveLeft();
		space.moveLeft();
		space.moveLeft();
		space.moveLeft();
		valuesShouldBe(space, -6, 0.25, 40);
	}

	@Test
	public void testMoveRightWith4Steps() {
		DiscreteSpace space = new DiscreteSpaceCentered(0, 20, 20,
				0.25);
		space.moveRight();
		space.moveRight();
		space.moveRight();
		space.moveRight();
		valuesShouldBe(space, -4, 0.25, 40);
	}

	private void valuesShouldBe(DiscreteSpace space, double start, double step, int limit) {
		List<Interval> list = new ArrayList<>();
		space.forEach(list::add);
		assertEquals(createIntervals(start, step, limit), list);
	}

	private List<Interval> createIntervals(double start, double step, int limit) {
		ArrayList<Interval> list = new ArrayList<>();
		DoubleStream.iterate(start, d -> d + step)
				.limit(limit)
				.forEach(d -> list.add(new Interval(d, d + step)));
		return list;
	}
}