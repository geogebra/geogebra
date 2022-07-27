package org.geogebra.common.kernel.interval.evaluators;

import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.interval.Interval;
import org.junit.Test;

public class DiscreteSpaceImpTest {

	@Test
	public void testValues() {
		DiscreteSpaceImp space = new DiscreteSpaceImp();
		space.update(new Interval(0, 5), 10);
		assertEquals(
				Arrays.asList(
						interval(0, 0.5),
						interval(0.5, 1.0),
						interval(1.0, 1.5),
						interval(1.5, 2.0),
						interval(2.0, 2.5),
						interval(2.5, 3.0),
						interval(3.0, 3.5),
						interval(3.5, 4.0),
						interval(4.0, 4.5),
						interval(4.5, 5.0)
				),
				space.values().collect(Collectors.toList()));
	}

	@Test
	public void testDiffMax() {
		DiscreteSpaceImp space = new DiscreteSpaceImp();
		space.update(new Interval(0, 5), 10);
		DiscreteSpace subSpace = space.difference(5, 7);
		assertEquals(
				Arrays.asList(
						interval(5.0, 5.5),
						interval(5.5, 6.0),
						interval(6.0, 6.5),
						interval(6.5, 7.0)
				),
				subSpace.values().collect(Collectors.toList()));
	}

	@Test
	public void testDiffMaxFractional() {
		DiscreteSpaceImp space = new DiscreteSpaceImp();
		space.update(new Interval(0, 5), 10);
		DiscreteSpace subSpace = space.difference(5, 7.4);
		assertEquals(
				Arrays.asList(
						interval(5.0, 5.5),
						interval(5.5, 6.0),
						interval(6.0, 6.5),
						interval(6.5, 7.0),
						interval(7.0, 7.5)
				),
				subSpace.values().collect(Collectors.toList()));
	}

	@Test
	public void testDiffMin() {
		DiscreteSpaceImp space = new DiscreteSpaceImp();
		space.update(new Interval(0, 5), 10);
		DiscreteSpace subSpace = space.difference(-2.0, 5);
		assertEquals(
				Arrays.asList(
						interval(-2.0, -1.5),
						interval(-1.5, -1.0),
						interval(-1.0, -0.5),
						interval(-0.5, 0)
				),
				subSpace.values().collect(Collectors.toList()));
	}

	@Test
	public void testDiffMinJoin() {
		DiscreteSpaceImp space = new DiscreteSpaceImp();
		space.update(new Interval(0, 5), 100);
		List<Interval> original = space.values().collect(Collectors.toList());
		DiscreteSpace subSpace = space.difference(-4.4112444, 0);
		List<Interval> list = subSpace.values().collect(Collectors.toList());
		assertEquals(original.get(0).getLow(), list.get(list.size() -1).getHigh(), 0);
	}

	@Test
	public void testDiffMinFractional() {
		DiscreteSpaceImp space = new DiscreteSpaceImp();
		space.update(new Interval(0, 5), 10);
		DiscreteSpace subSpace = space.difference(-2.3, 5);
		assertEquals(
				Arrays.asList(
						interval(-2.5, -2.0),
						interval(-2.0, -1.5),
						interval(-1.5, -1.0),
						interval(-1.0, -0.5),
						interval(-0.5, 0)
				),
				subSpace.values().collect(Collectors.toList()));
	}

	@Test
	public void testExtendFromLeft() {
		DiscreteSpace space = newSpace(100, 110, 10);
		space.extend(newSpace(95, 100, 5));
		assertEquals(newSpace(95, 105, 10), space);
	}

	@Test
	public void testExtendFromLeftNegative() {
		DiscreteSpace space = newSpace(0, 10, 10);
		space.extend(newSpace(-5, 0, 5));
		assertEquals(newSpace(-5, 5, 10), space);
	}

	@Test
	public void testExtendFromRight() {
		DiscreteSpace space = newSpace(10, 20, 10);
		space.extend(newSpace(20, 25, 5));
		assertEquals(newSpace(15, 25, 10), space);
	}

	@Test
	public void testExtendFromRightNegative() {
		DiscreteSpace space = newSpace(-20, -10, 10);
		space.extend(newSpace(-10, -5, 5));
		assertEquals(newSpace(-15, -5, 10), space);
	}

	private DiscreteSpaceImp newSpace(int low, int high, int count) {
		return new DiscreteSpaceImp(interval(low, high), count);
	}
}