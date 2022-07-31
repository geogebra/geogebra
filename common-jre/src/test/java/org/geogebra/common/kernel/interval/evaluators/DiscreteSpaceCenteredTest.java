package org.geogebra.common.kernel.interval.evaluators;

import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
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
		List<Interval> expected = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			double start = -0.5;
			expected.add(interval(start + i * step, start + (i + 1) * step));
		}

		List<Interval> list = space.values().collect(Collectors.toList());
		assertEquals(expected, list);
	}

	@Test
	public void testUpdate() {
		DiscreteSpaceCentered space = new DiscreteSpaceCentered(0,
				0, 0, 0.05);
		space.update(new Interval(-0.5, 0.5), 20);
		List<Interval> list = space.values().collect(Collectors.toList());

		DiscreteSpaceCentered reference = new DiscreteSpaceCentered(0,
				10, 10, 0.05);
		List<Interval> expected = reference.values().collect(Collectors.toList());
		assertEquals(expected, list);
	}


	@Test
	public void testDifference() {
		DiscreteSpaceCentered space = new DiscreteSpaceCentered(0,
				10, 10, 0.05);
		DiscreteSpace difference = space.difference(-7, 5);
		List<Interval> actual = difference.values().collect(Collectors.toList());
		assertEquals(Collections.emptyList(), actual);
	}

}