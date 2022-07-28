package org.geogebra.common.kernel.interval.evaluators;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.interval.Interval;
import org.junit.Test;

public class DiscreteSpaceCenteredTest {
	@Test
	public void testValues() {
		DiscreteSpaceCentered space = new DiscreteSpaceCentered(0,
				10, 10, 0.05);
		List<Interval> list = space.values().collect(Collectors.toList());
		assertEquals(Collections.emptyList(), list);
	}
}