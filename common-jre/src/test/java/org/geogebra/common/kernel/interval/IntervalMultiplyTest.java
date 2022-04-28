package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalDivide.next;
import static org.geogebra.common.kernel.interval.IntervalDivide.prev;
import static org.geogebra.common.kernel.interval.IntervalOperands.multiply;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IntervalMultiplyTest {
	@Test
	public void negativeWithNegativeZeroHigh() {
		// Table 6, row 1 column 1
		mulNegativeWithNegativeZeroHigh(-12.34, -5.67, -1231.23, -54.32);
		mulNegativeWithNegativeZeroHigh(-2.34, 0, -71.23, -54.32);
		mulNegativeWithNegativeZeroHigh(-42.34, -1.24, -1231.23, 0);
	}

	private void mulNegativeWithNegativeZeroHigh(double a1, double a2, double b1, double b2) {
		assertEquals(interval(prev(a2 * b2), next(a1 * b1)),
				multiply(interval(a1, a2), interval(b1, b2)));
	}

	@Test
	public void negativeWithMixed() {
		// Table 6, row 1 column 2
		mulNegativeWithMixed(-34.56, -1.24, -12.354, 12.34);
		mulNegativeWithMixed(-1E234, -1E-4, -98.76, 1E234);
		mulNegativeWithMixed(-1E234, 0, -98.76, 1E234);
		mulNegativeWithMixed(-1E234, 0, -98.76, 0);
	}

	private void mulNegativeWithMixed(double a1, double a2, double b1, double b2) {
		assertEquals(interval(prev(a1 * b2), next(a1 * b1)),
				multiply(interval(a1, a2), interval(b1, b2)));
	}

	@Test
	public void negativeWithPositive() {
		// Table 6, row 1 column 3
		mulNegativeWithPositive(-98.67, -65.43, 12.34, 56.78);
		mulNegativeWithPositive(-98.67, 0, 12.34, 56.78);
		mulNegativeWithPositive(-98.67, -65.43, 0, 56.78);
		mulNegativeWithPositive(-98.67, 0, 0, 56.78);
		mulNegativeWithPositive(-1E234, -1e-4, 1E-4, 1E234);
	}

	private void mulNegativeWithPositive(double a1, double a2, double b1, double b2) {
		assertEquals(interval(prev(a1 * b2), next(a2 * b1)),
				multiply(interval(a1, a2), interval(b1, b2)));
	}

	@Test
	public void anyMultipliedByZeroShouldBeZero() {
		mulByZeroShouldBeZero(-12.34, -4.56);
		mulByZeroShouldBeZero(-12.34, 4.56);
		mulByZeroShouldBeZero(0, 4.56);
		mulByZeroShouldBeZero(123, 1E234);
		mulByZeroShouldBeZero(Double.NEGATIVE_INFINITY, -2.34);
		mulByZeroShouldBeZero(Double.NEGATIVE_INFINITY, 12432.34);
		mulByZeroShouldBeZero(-8765.432, Double.POSITIVE_INFINITY);
		mulByZeroShouldBeZero(0, Double.POSITIVE_INFINITY);
		mulByZeroShouldBeZero(1234.5678, Double.POSITIVE_INFINITY);
		mulByZeroShouldBeZero(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		mulByZeroShouldBeZero(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		mulByZeroShouldBeZero(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	private void mulByZeroShouldBeZero(double a1, double a2) {
		assertEquals(zero(), multiply(interval(a1, a2), zero()));
		assertEquals(zero(), multiply(zero(), interval(a1, a2)));
	}

	@Test
	public void negativeByNegativeOpenToNegativeInfinity() {
		// Table 6, row 1 column 5
		mulNegativeByNegativeOpenToNegativeInfinity(-98.76, -32.11, -43.23);
		mulNegativeByNegativeOpenToNegativeInfinity(-98.76, 0, -2543.23);
		mulNegativeByNegativeOpenToNegativeInfinity(-56.78, -1E-4, 0);
	}

	private void mulNegativeByNegativeOpenToNegativeInfinity(double a1, double a2, double b2) {
		assertEquals(interval(prev(a2 * b2), Double.POSITIVE_INFINITY),
				multiply(interval(a1, a2), interval(Double.NEGATIVE_INFINITY, b2)));
	}

	@Test
	public void negativeByMixedOpenToNegativeInfinity() {
		// Table 6, row 1 column 6
		mulNegativeByMixedOpenToNegativeInfinity(-98.7, -54.3, 12.3);
		mulNegativeByMixedOpenToNegativeInfinity(-98.7, 0, 12.3);
		mulNegativeByMixedOpenToNegativeInfinity(-1E234, -1E111, 1E234);
		mulNegativeByMixedOpenToNegativeInfinity(-1E234, -1E111, 0);
	}

	private void mulNegativeByMixedOpenToNegativeInfinity(double a1, double a2, double b2) {
		assertEquals(interval(prev(a1 * b2), Double.POSITIVE_INFINITY),
				multiply(interval(a1, a2), interval(Double.NEGATIVE_INFINITY, b2)));
	}

	@Test
	public void negativeByMixedOpenToPositiveInfinity() {
		// Table 6, row 1 column 7
		mulNegativeByMixedOpenToPositiveInfinity(-76.54, -12.54, -56.78);
		mulNegativeByMixedOpenToPositiveInfinity(-8836.17, -12.54, 0);
		mulNegativeByMixedOpenToPositiveInfinity(-96.43, 0, -56.78);
		mulNegativeByMixedOpenToPositiveInfinity(-96.43, 0, 0);
		mulNegativeByMixedOpenToPositiveInfinity(-1E234, -1E-2, -56.78);
	}

	private void mulNegativeByMixedOpenToPositiveInfinity(double a1, double a2, double b1) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, next(a1 * b1)),
				multiply(interval(a1, a2), interval(b1, Double.POSITIVE_INFINITY)));
	}

	@Test
	public void negativeByPositiveOpenToPositiveInfinity() {
		// Table 6, row 1 column 8
		mulNegativeByPositiveOpenToPositiveInfinity(-98.76, -65.43, 12.34);
		mulNegativeByPositiveOpenToPositiveInfinity(-1E6, 0, 12.34);
		mulNegativeByPositiveOpenToPositiveInfinity(-98.76, -65.43, 0);
		mulNegativeByPositiveOpenToPositiveInfinity(-98.76, 0, 0);
	}

	private void mulNegativeByPositiveOpenToPositiveInfinity(double a1, double a2, double b1) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, next(a2 * b1)),
				multiply(interval(a1, a2), interval(b1, Double.POSITIVE_INFINITY)));
	}

	@Test
	public void mulByWholeShouldBeWhole() {
		mulByWholeShouldBeWhole(-98.76, -76.54);
		mulByWholeShouldBeWhole(-98.76, 0);
		mulByWholeShouldBeWhole(-98.76, 76.54);
		mulByWholeShouldBeWhole(0, 76.54);
		mulByWholeShouldBeWhole(Double.NEGATIVE_INFINITY, -1.11);
		mulByWholeShouldBeWhole(Double.NEGATIVE_INFINITY, 1.11);
		mulByWholeShouldBeWhole(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		mulByWholeShouldBeWhole(-76.54, Double.POSITIVE_INFINITY);
		mulByWholeShouldBeWhole(0, Double.POSITIVE_INFINITY);
		mulByWholeShouldBeWhole(12 - 34, Double.POSITIVE_INFINITY);
	}

	private void mulByWholeShouldBeWhole(double a1, double a2) {
		assertEquals(whole(), multiply(interval(a1, a2), whole()));
		assertEquals(whole(), multiply(whole(), interval(a1, a2)));
	}

	@Test
	public void mixedByNegative() {
		// Table 6, row 2 column 1
		mulMixedByNegative(-122.33, 12.34, -87.64, -3.14);
		mulMixedByNegative(-122.33, 0, -87.64, -3.14);
		mulMixedByNegative(-122.33, 12.34, -87.64, 0);
		mulMixedByNegative(-122.33, 0, -87.64, 0);
		mulMixedByNegative(-1E234, 1E2, -1E64, -0.5);
	}

	private void mulMixedByNegative(double a1, double a2, double b1, double b2) {
		assertEquals(interval(prev(a2 * b1), next(a1 * b1)),
				multiply(interval(a1, a2), interval(b1, b2)));
	}

	@Test
	public void mixedByMixed() {
		mulMixedByMixed(-76.54, 12.34, -43.21, 23.45);
		mulMixedByMixed(-1E234, 1E234, -1E1, 1E4);
	}

	private void mulMixedByMixed(double a1, double a2, double b1, double b2) {
		// Table 6, row 2 column 2
		assertEquals(interval(Math.min(prev(a1 * b2), next(a2 * b1)),
						Math.max(next(a1 * b1), next(a2 * b2))),
				multiply(interval(a1, a2), interval(b1, b2)));
	}

	@Test
	public void mixedByPositive() {
		// Table 6, row 2 column 3
		mulMixedByPositive(-87.76, 12.34, 12.23, 67.89);
		mulMixedByPositive(-87.76, 12.34, 0, 67.89);
		mulMixedByPositive(-1E234, 1E234, 12.34, 1E234);
	}

	private void mulMixedByPositive(double a1, double a2, double b1, double b2) {
		assertEquals(interval(prev(a1 * b2), next(a2 * b2)),
				multiply(interval(a1, a2), interval(b1, b2)));
	}

	@Test
	public void positiveByNegative() {
		// Table 6, row 3 column 1
		mulPositiveByNegative(12.34, 45.67, -64.54, -32.21);
		mulPositiveByNegative(12.34, 45.67, -64.54, 0);
		mulPositiveByNegative(0, 45.67, -64.54, -32.21);
		mulPositiveByNegative(1E-4, 1E45, -1E234, -1E-4);
	}

	private void mulPositiveByNegative(double a1, double a2, double b1, double b2) {
		assertEquals(interval(prev(a2 * b1), next(a1 * b2)),
				multiply(interval(a1, a2), interval(b1, b2)));
	}

	@Test
	public void positiveByMixed() {
		mulPositiveByMixed(1.23, 4.56, -3.14, 3.14);
		mulPositiveByMixed(0, 4.56, -3.14, 3.14);
		mulPositiveByMixed(0, 1E234, -1E4, 1E-4);
	}

	private void mulPositiveByMixed(double a1, double a2, double b1, double b2) {
		assertEquals(interval(prev(a2 * b1), next(a2 * b2)),
				multiply(interval(a1, a2), interval(b1, b2)));
	}

	@Test
	public void positiveByPositive() {
		// Table 6, row 3 column 3
		mulPositiveByPositive(12.34, 56.78, 34.56, 78.98);
		mulPositiveByPositive(0, 56.78, 34.56, 78.98);
		mulPositiveByPositive(43.5, 56.78, 0, 78.98);
		mulPositiveByPositive(43.5, 56.78, 0, 1E234);
		mulPositiveByPositive(1E-4, 1E234, 1E-4, 1E234);
	}

	private void mulPositiveByPositive(double a1, double a2, double b1, double b2) {
		assertEquals(interval(prev(a1 * b1), next(a2 * b2)),
				multiply(interval(a1, a2), interval(b1, b2)));

	}

	@Test
	public void positiveByOpenToNegativeInfinityAndNegative() {
		// Table 6, row 3 column 5
		mulPositiveByOpenToNegativeInfinityAndNegative(12.34, 56.78, -99.8);
		mulPositiveByOpenToNegativeInfinityAndNegative(0, 56.78, -99.8);
		mulPositiveByOpenToNegativeInfinityAndNegative(12.34, 1356.78, 0);
		mulPositiveByOpenToNegativeInfinityAndNegative(1E-4, 1E234, -1E10);
	}

	private void mulPositiveByOpenToNegativeInfinityAndNegative(double a1, double a2, double b2) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, next(a1 * b2)),
				multiply(interval(a1, a2), interval(Double.NEGATIVE_INFINITY, b2)));
	}

	@Test
	public void positiveByOpenToNegativeInfinityAndPositive() {
		// Table 6, row 3 column 6
		mulPositiveByOpenToNegativeInfinityAndPositive(1.23, 4.56, 23.45);
		mulPositiveByOpenToNegativeInfinityAndPositive(1.23, 1E6, 0);
		mulPositiveByOpenToNegativeInfinityAndPositive(0, 1E6, 0);
		mulPositiveByOpenToNegativeInfinityAndPositive(0, 1E6, 1.3);
	}

	private void mulPositiveByOpenToNegativeInfinityAndPositive(double a1, double a2, double b2) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, next(a2 * b2)),
				multiply(interval(a1, a2), interval(Double.NEGATIVE_INFINITY, b2)));
	}

	@Test
	public void positiveByMixedOpenToPositiveInfinity() {
		// Table 6, row 3 column 7
		mulPositiveByMixedOpenToPositiveInfinity(12.34, 45.67, -87.65);
		mulPositiveByMixedOpenToPositiveInfinity(0, 453.67, -87.65);
		mulPositiveByMixedOpenToPositiveInfinity(12.34, 45.67, 0);
		mulPositiveByMixedOpenToPositiveInfinity(12.34, 1E5, -1E234);
	}

	private void mulPositiveByMixedOpenToPositiveInfinity(double a1, double a2, double b1) {
		assertEquals(interval(prev(a2 * b1), Double.POSITIVE_INFINITY),
				multiply(interval(a1, a2), interval(b1, Double.POSITIVE_INFINITY)));
	}

	@Test
	public void positiveByPositiveOpenToPositiveInfinity() {
		// Table 6, row 3 column 7
		mulPositiveByPositiveOpenToPositiveInfinity(12.34, 56.78, 43.21);
		mulPositiveByPositiveOpenToPositiveInfinity(0, 56.78, 43.21);
		mulPositiveByPositiveOpenToPositiveInfinity(12.34, 56.78, 0);
		mulPositiveByPositiveOpenToPositiveInfinity(1E34, 1E234, 0);
		mulPositiveByPositiveOpenToPositiveInfinity(1E24, 1E234, 1E34);
	}

	private void mulPositiveByPositiveOpenToPositiveInfinity(double a1, double a2, double b1) {
		assertEquals(interval(prev(a1 * b1), Double.POSITIVE_INFINITY),
				multiply(interval(a1, a2), interval(b1, Double.POSITIVE_INFINITY)));
	}

	@Test
	public void negativeOpenToNegativeInfinityByNegative() {
		// Table 6, row 5 column 1
		mulNegativeOpenToNegativeInfinityByNegative(-1.23, -98.76, -65.4);
		mulNegativeOpenToNegativeInfinityByNegative(0, -98.76, -65.4);
		mulNegativeOpenToNegativeInfinityByNegative(-1.23, -98.76, 0);
		mulNegativeOpenToNegativeInfinityByNegative(0, -67.89, 0);
		mulNegativeOpenToNegativeInfinityByNegative(-1E234, -123.45, -1E-4);
	}

	private void mulNegativeOpenToNegativeInfinityByNegative(double a2, double b1, double b2) {
		assertEquals(interval(prev(a2 * b2), Double.POSITIVE_INFINITY),
				multiply(interval(Double.NEGATIVE_INFINITY, a2), interval(b1, b2)));
	}

	@Test
	public void negativeOpenToNegativeInfinityByPositive() {
		// Table 6, row 5 column 3
		mulNegativeOpenToNegativeInfinityByPositive(-12.34, 1.23, 4.56);
		mulNegativeOpenToNegativeInfinityByPositive(0, 1.23, 4.56);
		mulNegativeOpenToNegativeInfinityByPositive(-12.34, 0, 487.56);
		mulNegativeOpenToNegativeInfinityByPositive(0, 0, 487.56);
		mulNegativeOpenToNegativeInfinityByPositive(-1E234, 1E-4, 1E4);
	}

	private void mulNegativeOpenToNegativeInfinityByPositive(double a2, double b1, double b2) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, next(a2 * b1)),
				multiply(interval(Double.NEGATIVE_INFINITY, a2), interval(b1, b2)));
	}

	@Test
	public void negativeOpenToNegativeInfinityByNegativeOpenToNegativeInfinity() {
		// Table 6, row 5 column 5
		mulNegativeOpenToNegativeInfinityByNegativeOpenToNegativeInfinity(-12.34, -56.78);
		mulNegativeOpenToNegativeInfinityByNegativeOpenToNegativeInfinity(0, -256.78);
		mulNegativeOpenToNegativeInfinityByNegativeOpenToNegativeInfinity(-12.34, 0);
		mulNegativeOpenToNegativeInfinityByNegativeOpenToNegativeInfinity(-1E234, -1E-4);
	}

	private void mulNegativeOpenToNegativeInfinityByNegativeOpenToNegativeInfinity(double a2,
			double b2) {
		assertEquals(interval(prev(a2 * b2), Double.POSITIVE_INFINITY),
				multiply(interval(Double.NEGATIVE_INFINITY, a2),
						interval(Double.NEGATIVE_INFINITY, b2)));
	}

	@Test
	public void negativeOpenToNegativeInfinityByPositiveOpenToPositiveInfinity() {
		// Table 6, row 5 column 8
		mulNegativeOpenToNegativeInfinityByPositiveOpenToPositiveInfinity(-12.34, 5.6);
		mulNegativeOpenToNegativeInfinityByPositiveOpenToPositiveInfinity(0, 5.6);
		mulNegativeOpenToNegativeInfinityByPositiveOpenToPositiveInfinity(-12.34, 0);
		mulNegativeOpenToNegativeInfinityByPositiveOpenToPositiveInfinity(-1E234, 1E234);
	}

	private void mulNegativeOpenToNegativeInfinityByPositiveOpenToPositiveInfinity(double a2,
			double b1) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, prev(a2 * b1)),
				multiply(interval(Double.NEGATIVE_INFINITY, a2),
						interval(b1, Double.POSITIVE_INFINITY)));
	}

	@Test
	public void mixedOpenToNegativeInfinityByNegative() {
		// Table 6, row 6 column 1
		divMixedOpenToNegativeInfinityByNegative(12.34, -12.34, -3.14);
		divMixedOpenToNegativeInfinityByNegative(0, -46.47, -3.14);
		divMixedOpenToNegativeInfinityByNegative(12.34, -2.34, 0);
		divMixedOpenToNegativeInfinityByNegative(1E234, -1E234, -1E-4);
	}

	private void divMixedOpenToNegativeInfinityByNegative(double a2, double b1, double b2) {
		assertEquals(interval(prev(a2 * b1), Double.POSITIVE_INFINITY),
				multiply(interval(Double.NEGATIVE_INFINITY, a2),
						interval(b1, b2)));
	}

	@Test
	public void mixedOpenToNegativeInfinityByPositive() {
		// Table 6, row 6 column 3
		divMixedOpenToNegativeInfinityByPositive(12.34, 2.34, 5.67);
		divMixedOpenToNegativeInfinityByPositive(12.34, 0, 4.53);
		divMixedOpenToNegativeInfinityByPositive(0, 1.34, 5.67);
		divMixedOpenToNegativeInfinityByPositive(0, 0, 5.67);
		divMixedOpenToNegativeInfinityByPositive(1E-4, 1E-4, 1E234);
	}

	private void divMixedOpenToNegativeInfinityByPositive(double a2, double b1, double b2) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, next(a2 * b2)),
				multiply(interval(Double.NEGATIVE_INFINITY, a2),
						interval(b1, b2)));

	}

	@Test
	public void multiplyInvertedWithZero() {
		assertEquals(IntervalConstants.whole().invert(), multiply(interval(0),
				invertedInterval(1, 2)));
	}
}