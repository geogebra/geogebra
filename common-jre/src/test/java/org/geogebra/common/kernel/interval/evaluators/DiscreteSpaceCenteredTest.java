package org.geogebra.common.kernel.interval.evaluators;

import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.interval.Interval;
import org.junit.Test;

public class DiscreteSpaceCenteredTest {
	@Test
	public void testValues() {
		double step = 0.05;
		DiscreteSpaceCentered space = new DiscreteSpaceCentered(0,
				10, 10, step);
		List<Interval> expected = createIntervalList(-0.5, step, 20);

		List<Interval> list = space.values().collect(Collectors.toList());
		assertEquals(expected, list);
	}

	private List<Interval> createIntervalList(double start, double step, int count) {
		List<Interval> list = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			list.add(interval(start + i * step, start + (i + 1) * step));
		}
		return list;
	}

	@Test
	public void testValuesWithBoundsLeft() {
		DiscreteSpaceCentered space = new DiscreteSpaceCentered(10,
				10, 10, 0.05);
		List<Interval> actual = space.values(9.5, 10).collect(Collectors.toList());
		assertEquals(createIntervalList(9.5, 0.05, 10), actual);
	}

	@Test
	public void testValuesWithBoundsRight() {
		DiscreteSpaceCentered space = new DiscreteSpaceCentered(0,
				10, 10, 0.05);
		List<Interval> actual = space.values(0.5, 1).collect(Collectors.toList());

		assertEquals(createIntervalList(0.5, 0.05, 10), actual);
	}

}