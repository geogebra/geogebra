package org.geogebra.common.util;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class DoubleUtilTest {
	private final static double PRECISION = 1E-18;

	@Test
	public void testRange() {
		assertArrayEquals(
				new double[] {42},
				DoubleUtil.range(42, 42, 0.002),
				PRECISION
		);
		assertArrayEquals(
				new double[] {-3, -1, 1, 2},
				DoubleUtil.range(-3, 2, 2),
				PRECISION
		);
		assertArrayEquals(
				new double[] {-1, -0.5, 0, 0.5, 1},
				DoubleUtil.range(-1, 1, 0.5),
				PRECISION
		);
		assertArrayEquals(
				new double[] {1, 1.999, 2.998, 3},
				DoubleUtil.range(1, 3, 0.999),
				PRECISION
		);
		assertArrayEquals(
				new double[] {0, 0.2, 0.4, 0.6, 0.8, 1, 1.2, 1.4},
				DoubleUtil.range(0, 1.4, 0.2),
				PRECISION
		);
		assertArrayEquals(
				new double[] {0.3, 0.5, 0.7},
				DoubleUtil.range(0.3, 0.7, 0.2),
				PRECISION
		);
		assertArrayEquals(
				new double[] {0.3, 0.5},
				DoubleUtil.range(0.3, 0.5, 0.2),
				PRECISION
		);
	}

	@Test
	public void testRangeInvalid() {
		assertArrayEquals(
				new double[] {0.3},
				DoubleUtil.range(0.3, -0.5, 1),
				PRECISION
		);
		assertArrayEquals(
				new double[] {0.3},
				DoubleUtil.range(0.3, -0.5, 0.8),
				PRECISION
		);
		assertArrayEquals(
				new double[] {0.3},
				DoubleUtil.range(0.3, -0.5, 0.2),
				PRECISION
		);
		assertArrayEquals(
				new double[] {0.3},
				DoubleUtil.range(0.3, 0.5, -0.2),
				PRECISION
		);
	}
}
