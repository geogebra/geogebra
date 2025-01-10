package org.geogebra.common.kernel.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.geogebra.common.kernel.discrete.geom.Point2D;
import org.geogebra.common.kernel.discrete.geom.algorithms.ConvexHull;
import org.junit.Test;

/*
 * Convex hull algorithm - Test suite (Java)
 *
 * Copyright (c) 2017 Project Nayuki
 * https://www.nayuki.io/page/convex-hull-algorithm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (see COPYING.txt and COPYING.LESSER.txt).
 * If not, see <http://www.gnu.org/licenses/>.
 */

public final class ConvexHullTest {

	/*---- Fixed test vectors ----*/

	@Test
	public void testEmpty() {
		List<Point2D> points = Collections.emptyList();
		List<Point2D> actual = ConvexHull.makeHull(points);
		List<Point2D> expect = Collections.emptyList();
		assertEquals(expect, actual);
	}

	@Test
	public void testOne() {
		List<Point2D> points = Arrays.asList(new Point2D(3, 1));
		List<Point2D> actual = ConvexHull.makeHull(points);
		List<Point2D> expect = points;
		assertEquals(expect, actual);
	}

	@Test
	public void testTwoDuplicate() {
		List<Point2D> points = Arrays.asList(new Point2D(0, 0),
				new Point2D(0, 0));
		List<Point2D> actual = ConvexHull.makeHull(points);
		List<Point2D> expect = Arrays.asList(new Point2D(0, 0));
		assertEquals(expect, actual);
	}

	@Test
	public void testTwoHorizontal0() {
		List<Point2D> points = Arrays.asList(new Point2D(2, 0),
				new Point2D(5, 0));
		List<Point2D> actual = ConvexHull.makeHull(points);
		List<Point2D> expect = points;
		assertEquals(expect, actual);
	}

	@Test
	public void testTwoHorizontal1() {
		List<Point2D> points = Arrays.asList(new Point2D(-6, -3),
				new Point2D(-8, -3));
		List<Point2D> actual = ConvexHull.makeHull(points);
		List<Point2D> expect = Arrays.asList(new Point2D(-8, -3),
				new Point2D(-6, -3));
		assertEquals(expect, actual);
	}

	@Test
	public void testTwoVertical0() {
		List<Point2D> points = Arrays.asList(new Point2D(1, -4),
				new Point2D(1, 4));
		List<Point2D> actual = ConvexHull.makeHull(points);
		List<Point2D> expect = points;
		assertEquals(expect, actual);
	}

	@Test
	public void testTwoVertical1() {
		List<Point2D> points = Arrays.asList(new Point2D(-1, 2),
				new Point2D(-1, -3));
		List<Point2D> actual = ConvexHull.makeHull(points);
		List<Point2D> expect = Arrays.asList(new Point2D(-1, -3),
				new Point2D(-1, 2));
		assertEquals(expect, actual);
	}

	@Test
	public void testTwoDiagonal0() {
		List<Point2D> points = Arrays.asList(new Point2D(-2, -3),
				new Point2D(2, 0));
		List<Point2D> actual = ConvexHull.makeHull(points);
		List<Point2D> expect = points;
		assertEquals(expect, actual);
	}

	@Test
	public void testTwoDiagonal1() {
		List<Point2D> points = Arrays.asList(new Point2D(-2, 3),
				new Point2D(2, 0));
		List<Point2D> actual = ConvexHull.makeHull(points);
		List<Point2D> expect = points;
		assertEquals(expect, actual);
	}

	@Test
	public void testRectangle() {
		List<Point2D> points = Arrays.asList(new Point2D(-3, 2),
				new Point2D(1, 2), new Point2D(1, -4), new Point2D(-3, -4));
		List<Point2D> actual = ConvexHull.makeHull(points);
		List<Point2D> expect = Arrays.asList(new Point2D(-3, -4),
				new Point2D(-3, 2), new Point2D(1, 2), new Point2D(1, -4));
		assertEquals(expect, actual);
	}

	/*---- Randomized testing ----*/

	@Test
	public void testHorizontalRandomly() {
		final int TRIALS = 100000;
		for (int i = 0; i < TRIALS; i++) {
			int len = rand.nextInt(30) + 1;
			List<Point2D> points = new ArrayList<>();
			if (rand.nextBoolean()) {
				double y = rand.nextGaussian();
				for (int j = 0; j < len; j++)
					points.add(new Point2D(rand.nextGaussian(), y));
			} else {
				int y = rand.nextInt(20) - 10;
				for (int j = 0; j < len; j++)
					points.add(new Point2D(rand.nextInt(30), y));
			}
			List<Point2D> actual = ConvexHull.makeHull(points);
			List<Point2D> expected = new ArrayList<>();
			expected.add(Collections.min(points));
			if (!Collections.max(points).equals(expected.get(0)))
				expected.add(Collections.max(points));
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testVerticalRandomly() {
		final int TRIALS = 100000;
		for (int i = 0; i < TRIALS; i++) {
			int len = rand.nextInt(30) + 1;
			List<Point2D> points = new ArrayList<>();
			if (rand.nextBoolean()) {
				double x = rand.nextGaussian();
				for (int j = 0; j < len; j++)
					points.add(new Point2D(x, rand.nextGaussian()));
			} else {
				int x = rand.nextInt(20) - 10;
				for (int j = 0; j < len; j++)
					points.add(new Point2D(x, rand.nextInt(30)));
			}
			List<Point2D> actual = ConvexHull.makeHull(points);
			List<Point2D> expected = new ArrayList<>();
			expected.add(Collections.min(points));
			if (!Collections.max(points).equals(expected.get(0)))
				expected.add(Collections.max(points));
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testVsNaiveRandomly() {
		final int TRIALS = 100000;
		for (int i = 0; i < TRIALS; i++) {
			int len = rand.nextInt(100);
			List<Point2D> points = new ArrayList<>();
			if (rand.nextBoolean()) {
				for (int j = 0; j < len; j++)
					points.add(new Point2D(rand.nextGaussian(),
							rand.nextGaussian()));
			} else {
				for (int j = 0; j < len; j++)
					points.add(new Point2D(rand.nextInt(10), rand.nextInt(10)));
			}
			List<Point2D> actual = ConvexHull.makeHull(points);
			List<Point2D> expected = makeHullNaive(points);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testHullPropertiesRandomly() {
		final int TRIALS = 100000;
		for (int i = 0; i < TRIALS; i++) {

			// Generate random points
			int len = rand.nextInt(100);
			List<Point2D> points = new ArrayList<>();
			if (rand.nextBoolean()) {
				for (int j = 0; j < len; j++)
					points.add(new Point2D(rand.nextGaussian(),
							rand.nextGaussian()));
			} else {
				for (int j = 0; j < len; j++)
					points.add(new Point2D(rand.nextInt(10), rand.nextInt(10)));
			}

			// Compute hull and check properties
			List<Point2D> hull = ConvexHull.makeHull(points);
			assertTrue(isPolygonConvex(hull));
			for (Point2D p : points)
				assertTrue(isPointInConvexPolygon(hull, p));

			// Add duplicate points and check new hull
			if (!points.isEmpty()) {
				int dupe = rand.nextInt(10) + 1;
				for (int j = 0; j < dupe; j++)
					points.add(points.get(rand.nextInt(points.size())));
				List<Point2D> nextHull = ConvexHull.makeHull(points);
				assertEquals(hull, nextHull);
			}
		}
	}

	private static List<Point2D> makeHullNaive(List<Point2D> points) {
		if (points.size() <= 1)
			return new ArrayList<>(points);
		List<Point2D> result = new ArrayList<>();

		// Jarvis march / gift wrapping algorithm
		Point2D point = Collections.min(points);
		do {
			result.add(point);
			Point2D next = points.get(0);
			for (Point2D p : points) {
				double ax = next.x - point.x;
				double ay = next.y - point.y;
				double bx = p.x - point.x;
				double by = p.y - point.y;
				double cross = ax * by - ay * bx;
				if (cross > 0
						|| cross == 0 && bx * bx + by * by > ax * ax + ay * ay)
					next = p;
			}
			point = next;
		} while (!point.equals(result.get(0)));
		return result;
	}

	private static boolean isPolygonConvex(List<Point2D> points) {
		int signum = 0;
		for (int i = 0; i + 2 < points.size(); i++) {
			Point2D p = points.get(i + 0);
			Point2D q = points.get(i + 1);
			Point2D r = points.get(i + 2);
			int sign = signum(
					(q.x - p.x) * (r.y - q.y) - (q.y - p.y) * (r.x - q.x));
			if (sign == 0) {
				continue;
			} else if (signum == 0) {
				signum = sign;
			} else if (sign != signum) {
				return false;
			}
		}
		return true;
	}

	private static boolean isPointInConvexPolygon(List<Point2D> polygon,
			Point2D point) {
		int signum = 0;
		for (int i = 0; i < polygon.size(); i++) {
			Point2D p = polygon.get(i);
			Point2D q = polygon.get((i + 1) % polygon.size());
			int sign = signum((q.x - p.x) * (point.y - q.y)
					- (q.y - p.y) * (point.x - q.x));
			if (sign == 0) {
				continue;
			} else if (signum == 0) {
				signum = sign;
			} else if (sign != signum) {
				return false;
			}
		}
		return true;
	}

	private static int signum(double x) {
		if (x > 0) {
			return +1;
		} else if (x < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	private static final Random rand = new Random();

}
