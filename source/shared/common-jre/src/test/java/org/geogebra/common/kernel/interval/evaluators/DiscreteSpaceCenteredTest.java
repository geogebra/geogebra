package org.geogebra.common.kernel.interval.evaluators;

import static org.geogebra.common.kernel.interval.IntervalTest.interval;
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
	public void testExtendLeftRight() {
		DiscreteSpace space = new DiscreteSpaceCentered(0, 2, 2,
				0.5);
		List<Interval> expected = new ArrayList<>();
		List<Interval> left = new ArrayList<>();
		List<Interval> right = new ArrayList<>();
		space.forEach(expected::add);
		space.extendLeft(interval(-1.2, 0.8), left::add);
		space.extendRight(interval(-1.1, 0.9), right::add);

		List<Interval> actual = new ArrayList<>();
		space.forEach(actual::add);
		assertEquals(expected, actual);
	}

	@Test
	public void testExtendLeftBetweenStep() {
		DiscreteSpace space = new DiscreteSpaceCentered(0, 2, 2,
				2);
		List<Interval> expected = createIntervals(-4, 2, 4);
		List<Interval> actual = new ArrayList<>();
		space.extendLeft(interval(-5, 3),  x -> {});
		space.forEach(actual::add);
		assertEquals(expected, actual);
	}

	@Test
	public void testExtendRightBetweenStep() {
		DiscreteSpace space = new DiscreteSpaceCentered(0, 2, 2,
				2);
		List<Interval> expected = createIntervals(-4, 2, 4);
		List<Interval> actual = new ArrayList<>();
		space.extendRight(interval(-3, 5),  x -> {});
		space.forEach(actual::add);
		assertEquals(expected, actual);
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