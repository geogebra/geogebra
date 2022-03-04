package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.negativeInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.positiveInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalDivide.next;
import static org.geogebra.common.kernel.interval.IntervalDivide.prev;
import static org.geogebra.common.kernel.interval.IntervalOperands.divide;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * For Cases, see https://www.math.kit.edu/ianm2/~kulisch/media/arjpkx.pdf
 */
public class IntervalDivideTest {

	@Test
	public void hasZeroByHasZeroShouldBeWhole() {
		// Table 1 Case 1.
		assertEquals(whole(), divide(interval(0), interval(-1, 0)));
		assertEquals(whole(), divide(interval(0), interval(-1, 1)));
		assertEquals(whole(), div(-2, 0, -1, 0));
		assertEquals(whole(), div(-2, 0, -1, 1));
		assertEquals(whole(), div(0, 2, -1, 0));
		assertEquals(whole(), div(0, 2, -1, 1));
		assertEquals(whole(), div(-2, 2, -1, 0));
		assertEquals(whole(), div(-2, 2, -1, 1));
	}

	@Test
	public void negativeByZeroShouldBeEmpty() {
 		// Table 1 Case 2.
		assertEquals(undefined(), divide(interval(-2.3, -1), zero()));
		assertEquals(undefined(), divide(interval(-25.73, -11), zero()));
	}

	@Test
	public void negativeByNegativeIncludingZeroAsHigh() {
		//  Table 1 Case 3.
		assertEquals(interval(1, Double.POSITIVE_INFINITY),
				div(-4, -2, -2, 0));
		assertEquals(interval(2, Double.POSITIVE_INFINITY),
				div(-4, -2, -1, 0));
	}

	@Test
	public void negativeByMixed() {
		//  Table 1 Case 4.
		assertEquals(invertedInterval(RMath.next(-2.0 / 2), RMath.next(-2.0 / -2)),
				div(-3, -2, -2, 2));
	}

	@Test
	public void negativeByNegativeIncludingZeroAsLow() {
		//  Table 1 Case 5.
		assertEquals(interval(Double.NEGATIVE_INFINITY, -0.5),
				div(-2, -2, 0, 4));

		assertEquals(interval(Double.NEGATIVE_INFINITY, -0.5),
				div(-2, -2, 0, 4));
	}

	@Test
	public void positiveByNegativeIncludingZeroAsHigh() {
		//  Table 1 Case 6
		assertEquals(interval(Double.NEGATIVE_INFINITY, -1),
				div(2, 4, -2, 0));
		assertEquals(interval(Double.NEGATIVE_INFINITY, -1),
				div(2, 4, -2, 0));
	}

	@Test
	public void positiveByMixed() {
		// Table 1 Case 7
		assertEquals(invertedInterval(2.0 / -1.0, 1.0),
				div(2, 4, -1, 2));
	}

	@Test
	public void positiveByPositiveIncludingZeroAsLow() {
		// Table 1 Case 8
		assertEquals(interval(0.5, Double.POSITIVE_INFINITY),
				div(2, 4, 0, 4));
		assertEquals(interval(1, Double.POSITIVE_INFINITY),
				div(2, 4, 0, 2));
	}

	@Test
	public void negativeByNegative() {
		// Table 7, row 1 column 1
		// [a1, a2] a2 <= 0, [b1, b2] b2 < 0
		assertEquals(interval(1, 4),
				div(-100, -50, -50, -25));
		assertEquals(interval(0, -4 / -3.0), div(-4, -2, Double.NEGATIVE_INFINITY, -3));
	}

	@Test
	public void negativeByPositive() {
		// Table 7, row 1 column 2
		divNegativeByPositive(-5, -3, 3, 4);
		divNegativeByPositive(-1.5, -1E-17, 10, 11.4);
		divNegativeByPositive(-100.5, -55.5555, 2000, 1E16);
		divNegativeByPositive(Double.NEGATIVE_INFINITY, -1.55, 2000, 1E16);
		divNegativeByPositive(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 2000, 1E16);
		divNegativeByPositive(-1000.5, -55.5555, 2000, Double.POSITIVE_INFINITY);
		divNegativeByPositive(-1000.5, -55.5555, Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY);
	}

	private void divNegativeByPositive(double a1, double a2, double b1, double b2) {
		assertEquals(interval(prev(a1 / b1), next(a2 / b2)),
				div(a1, a2, b1, b2));
	}

	private Interval div(double a1, double a2, double b1, double b2) {
		return divide(interval(a1, a2), interval(b1, b2));
	}

	@Test
	public void negativeAndZeroByPositive() {
		// Table 7, row1, col2 a2 = 0
		divNegativeByPositive(-20.5, 0, 1, 10);
		divNegativeByPositive(-100.5555, 0, 123, 456);
		divNegativeByPositive(-20.5, 0, 1, Double.POSITIVE_INFINITY);
		divNegativeByPositive(Double.NEGATIVE_INFINITY, 0, 1, 10);
	}

	@Test
	public void negativeByNegativeOpenToNegativeInfinity() {
		// Table 7, row 1 column 3
		divNegativeByOpenToNegativeInfinity(-2, -1, -1.234);
		divNegativeByOpenToNegativeInfinity(-1E17, -1E-12, -45.67);
		divNegativeByOpenToNegativeInfinity(Double.NEGATIVE_INFINITY, -1E-12, -1);
	}

	private void divNegativeByOpenToNegativeInfinity(double a1, double a2, double b2) {
		assertEquals(interval(0, next(a1 / b2)),
				div(a1, a2, Double.NEGATIVE_INFINITY, b2));
	}

	@Test
	public void negativeByPositiveOpenToPositiveInfinity() {
		// Table 7, row 1 column 4
		divNegativeByOpenToPositiveInfinity(-2, -1, 1);
		divNegativeByOpenToPositiveInfinity(-987.321, -123.456, 456.789);
		divNegativeByOpenToPositiveInfinity(-1E16, -1E10, 1E20);
		divNegativeByOpenToPositiveInfinity(Double.NEGATIVE_INFINITY, -1E10, 1E20);
	}

	private void divNegativeByOpenToPositiveInfinity(double a1, double a2, double b1) {
		assertEquals(interval(prev(a1 / b1), 0),
				div(a1, a2, b1, Double.POSITIVE_INFINITY));
	}

	@Test
	public void mixedByNegative() {
		// Table 7, row 2 column 1
		divMixedByNegative(-56.34, 1.23, -89.45, -82.34);
		divMixedByNegative(Double.NEGATIVE_INFINITY, 1.23, -33.45, -22.78);
		divMixedByNegative(-98.76, 78.23, Double.NEGATIVE_INFINITY, -12.34);
		divMixedByNegative(Double.NEGATIVE_INFINITY, 1.23, Double.NEGATIVE_INFINITY, -12.34);
	}

	private void divMixedByNegative(double a1, double a2, double b1, double b2) {
		// Table 7, row 2 column 1
		assertEquals(interval(prev(a2 / b2), next(a1 / b2)),
				div(a1, a2, b1, b2));
	}

	@Test
	public void mixedByPositive() {
		// Table 7, row 2 column 2
		divMixedByPositive(-56.34, 11.23, 1.234, 5.678);
		divMixedByPositive(-87.98, 1.234, 1234, 1E56);
	}

	private void divMixedByPositive(double a1, double a2, double b1, double b2) {
		assertEquals(interval(prev(a1 / b1), next(a2 / b1)),
				div(a1, a2, b1, b2));
	}

	@Test
	public void mixedByOpenToNegativeInfinity() {
		// Table 7, row 2 column 3
		divMixedByOpenToNegativeInfinity(-12.34, 56.78, -12.34);
		divMixedByOpenToNegativeInfinity(-78.67, 45.68, -1E-34);
	}

	private void divMixedByOpenToNegativeInfinity(double a1, double a2, double b2) {
		assertEquals(interval(prev(a2 / b2), next(a1 / b2)),
				div(a1, a2, Double.NEGATIVE_INFINITY, b2));
	}

	@Test
	public void mixedByOpenToPositiveInfinity() {
		// Table 7, row 2 column 4
		divMixedByOpenToPositiveInfinity(-98.76, 54.32, 2);
		divMixedByOpenToPositiveInfinity(-12.34, 23.32, Double.POSITIVE_INFINITY);
	}

	private void divMixedByOpenToPositiveInfinity(double a1, double a2, double b1) {
		assertEquals(interval(prev(a1 / b1), next(a2 / b1)),
				div(a1, a2, b1, Double.POSITIVE_INFINITY));
	}

	@Test
	public void positiveByNegative() {
		// Table7 row 3 column 1
		divPositiveByNegative(1.234, 5.678, -98.76, -31.43);
		divPositiveByNegative(0, 55.678, -55.76, -26.43);
		divPositiveByNegative(1.234, 55.678, Double.NEGATIVE_INFINITY, -65.43);
		divPositiveByNegative(0, 55.678, Double.NEGATIVE_INFINITY, -65.43);
	}

	private void divPositiveByNegative(double a1, double a2, double b1, double b2) {
		assertEquals(interval(prev(a2 / b2), next(a1 / b1)),
				div(a1, a2, b1, b2));
	}

	@Test
	public void positiveByPositive() {
		// Table7 row 3 column 2
		divPositiveByPositive(1.234, 5.678, 11.12, 33.44);
		divPositiveByPositive(0, 5.678, 121.12, 333.44);
		divPositiveByPositive(1.234, 1E15, 1561.12, 1E21);
	}

	private void divPositiveByPositive(double a1, double a2, double b1, double b2) {
		assertEquals(interval(prev(a1 / b2), next(a2 / b1)),
				div(a1, a2, b1, b2));
	}

	@Test
	public void positiveByOpenToNegativeInfinity() {
		// Table 7 row 3 column 3
		divPositiveByOpenToNegativeInfinity(1.234, 5.67, -22.33);
		divPositiveByOpenToNegativeInfinity(1E-243, 1E243, -22.33);
		divPositiveByOpenToNegativeInfinity(0, 124.3, -22.33);
		divPositiveByOpenToNegativeInfinity(0, 124.3, Double.NEGATIVE_INFINITY);
		divPositiveByOpenToNegativeInfinity(1, 124.3, Double.NEGATIVE_INFINITY);
	}

	private void divPositiveByOpenToNegativeInfinity(double a1, double a2, double b2) {
		assertEquals(interval(prev(a2 / b2), 0),
				div(a1, a2, Double.NEGATIVE_INFINITY, b2));
	}

	@Test
	public void positiveByOpenToPositiveInfinity() {
		divPositiveByOpenToPositiveInfinity(1.23, 4.56, 7.89);
		divPositiveByOpenToPositiveInfinity(0, 4798.876, 577.64);
		divPositiveByOpenToPositiveInfinity(1.56, Double.POSITIVE_INFINITY, 577.64);
		divPositiveByOpenToPositiveInfinity(0, Double.POSITIVE_INFINITY, 57.4);
		divPositiveByOpenToPositiveInfinity(1.56, 1E234, 32.1);
	}

	private void divPositiveByOpenToPositiveInfinity(double a1, double a2, double b1) {
		assertEquals(interval(0, next(a2 / b1)),
				div(a1, a2, b1, Double.POSITIVE_INFINITY));
	}

	@Test
	public void divOfZeroShouldBeZero() {
		assertEquals(zero(), divide(zero(), interval(-2, -1)));
		assertEquals(zero(), divide(zero(), interval(Double.NEGATIVE_INFINITY, -1)));
		assertEquals(zero(), divide(zero(), interval(1, 2)));
		assertEquals(zero(), divide(zero(), interval(1, Double.POSITIVE_INFINITY)));
	}

	@Test
	public void openToNegativeInfinityByNegative() {
		// Table 7 row 5 column 1
		divOpenToNegativeInfinityByNegative(-1.23, -98.76, -54.32);
		divOpenToNegativeInfinityByNegative(-1E12, -8.76, -4.32);
		divOpenToNegativeInfinityByNegative(-1E12, -8.76, -1E-234);
	}

	private void divOpenToNegativeInfinityByNegative(double a2, double b1, double b2) {
		assertEquals(interval(prev(a2 / b1), Double.POSITIVE_INFINITY),
				div(Double.NEGATIVE_INFINITY, a2, b1, b2));
	}

	@Test
	public void openToNegativeInfinityByPositive() {
		// Table 7 row 5 column 2
		divOpenToNegativeInfinityByPositive(-1.23, 1.23, 54.32);
		divOpenToNegativeInfinityByPositive(0, 1.23, 54.32);
		divOpenToNegativeInfinityByPositive(-1E-234, 55.66, Double.POSITIVE_INFINITY);
	}

	private void divOpenToNegativeInfinityByPositive(double a2, double b1, double b2) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, next(a2 / b2)),
				div(Double.NEGATIVE_INFINITY, a2, b1, b2));
	}

	@Test
	public void openToNegativeInfinityByOpenToNegativeInfinity() {
		// Table 7 row 5 column 3
		divOpenToNegativeInfinityByOpenToNegativeInfinity(-2, -3);
		divOpenToNegativeInfinityByOpenToNegativeInfinity(0, -3);
		divOpenToNegativeInfinityByOpenToNegativeInfinity(-1E234, -1E22);
		divOpenToNegativeInfinityByOpenToNegativeInfinity(-1E234, -1E-22);
		divOpenToNegativeInfinityByOpenToNegativeInfinity(Double.NEGATIVE_INFINITY, -1E-22);
	}

	private void divOpenToNegativeInfinityByOpenToNegativeInfinity(double a2, double b2) {
		assertEquals(interval(0, Double.POSITIVE_INFINITY),
				div(Double.NEGATIVE_INFINITY, a2, Double.NEGATIVE_INFINITY, b2));
	}

	@Test
	public void openToNegativeInfinityByOpenToPositiveInfinity() {
		// Table 7 row 5 column 4
		divOpenToNegativeInfinityByOpenToPositiveInfinity(-12.34, 34.56);
		divOpenToNegativeInfinityByOpenToPositiveInfinity(-1E234, 1E234);
	}

	private void divOpenToNegativeInfinityByOpenToPositiveInfinity(double a2, double b1) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, 0),
				div(Double.NEGATIVE_INFINITY, a2, b1, Double.POSITIVE_INFINITY));
	}

	@Test
	public void openToNegativeInfinityMixedByNegative() {
		// Table 7 row 6 column 1
		divOpenToNegativeInfinityMixedByNegative(1.23, -87.65, -43.21);
		divOpenToNegativeInfinityMixedByNegative(0, -87.65, -43.21);
		divOpenToNegativeInfinityMixedByNegative(1.23, Double.NEGATIVE_INFINITY, -43.21);
		divOpenToNegativeInfinityMixedByNegative(0, Double.NEGATIVE_INFINITY, -43.21);
		divOpenToNegativeInfinityMixedByNegative(1E234, Double.NEGATIVE_INFINITY, -43.21);
		divOpenToNegativeInfinityMixedByNegative(134, -56.78, -1E-32);
	}

	private void divOpenToNegativeInfinityMixedByNegative(double a2, double b1, double b2) {
		assertEquals(interval(prev(a2 / b2), Double.POSITIVE_INFINITY),
				div(Double.NEGATIVE_INFINITY, a2, b1, b2));
	}

	@Test
	public void openToNegativeInfinityMixedByPositive() {
		// Table 7 row 6 column 2
		divOpenToNegativeInfinityMixedByPositive(1.23, 1.23, 4.56);
		divOpenToNegativeInfinityMixedByPositive(0, 1.23, 4.56);
		divOpenToNegativeInfinityMixedByPositive(1E234, 1E-234, 677.89);
	}

	private void divOpenToNegativeInfinityMixedByPositive(double a2, double b1, double b2) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, next(a2 / b2)),
				div(Double.NEGATIVE_INFINITY, a2, b1, b2));
	}

	@Test
	public void openToNegativeInfinityMixedByOpenToNegativeInfinity() {
		// Table 7 row 6 column 3
		divOpenToNegativeInfinityMixedByOpenToNegativeInfinity(1.23, -4.56);
		divOpenToNegativeInfinityMixedByOpenToNegativeInfinity(1E-234, -1E234);
	}

	private void divOpenToNegativeInfinityMixedByOpenToNegativeInfinity(double a2, double b2) {
		assertEquals(interval(prev(a2 / b2), Double.POSITIVE_INFINITY),
				div(Double.NEGATIVE_INFINITY, a2, Double.NEGATIVE_INFINITY, b2));
	}

	@Test
	public void openToNegativeInfinityMixedByOpenToPositiveInfinity() {
		// Table 7 row 6 column 4
		divOpenToNegativeInfinityMixedByOpenToPositiveInfinity(12.34, 45.67);
		divOpenToNegativeInfinityMixedByOpenToPositiveInfinity(0, 45.67);
		divOpenToNegativeInfinityMixedByOpenToPositiveInfinity(0, 1E234);
		divOpenToNegativeInfinityMixedByOpenToPositiveInfinity(44.55, 1E234);
		divOpenToNegativeInfinityMixedByOpenToPositiveInfinity(0, 1E-234);
		divOpenToNegativeInfinityMixedByOpenToPositiveInfinity(656.78, 1E-234);
	}

	private void divOpenToNegativeInfinityMixedByOpenToPositiveInfinity(double a2, double b1) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, next(a2 / b1)),
				div(Double.NEGATIVE_INFINITY, a2, b1, Double.POSITIVE_INFINITY));
	}

	@Test
	public void mixedOpenToPositiveInfinityByNegative() {
		// Table 7 row 7 column 1
		divMixedOpenToPositiveInfinityByNegative(-1, -98.76, -54.32);
		divMixedOpenToPositiveInfinityByNegative(0, -98.76, -54.32);
		divMixedOpenToPositiveInfinityByNegative(-1E234, -98.76, -54.32);
		divMixedOpenToPositiveInfinityByNegative(-1E234, -1E234, -1E-4);
	}

	private void divMixedOpenToPositiveInfinityByNegative(double a1, double b1, double b2) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, next(a1 / b2)),
				div(a1, Double.POSITIVE_INFINITY, b1, b2));
	}

	@Test
	public void mixedOpenToPositiveInfinityByPositive() {
		// Table 7 row 7 column 2
		divMixedOpenToPositiveInfinityByPositive(-1.23, 22.33, 45.67);
		divMixedOpenToPositiveInfinityByPositive(0, 22.33, 45.67);
		divMixedOpenToPositiveInfinityByPositive(-34.56, 1E-4, 1E234);
		divMixedOpenToPositiveInfinityByPositive(0, 1E-4, 1E234);
	}

	private void divMixedOpenToPositiveInfinityByPositive(double a1, double b1, double b2) {
		assertEquals(interval(prev(a1 / b1), Double.POSITIVE_INFINITY),
				div(a1, Double.POSITIVE_INFINITY, b1, b2));
	}

	@Test
	public void mixedOpenToPositiveInfinityByOpenToNegativeInfinity() {
		// Table 7 row 7 column 3
		divMixedOpenToPositiveInfinityByOpenToNegativeInfinity(-12.34, -12.34);
		divMixedOpenToPositiveInfinityByOpenToNegativeInfinity(0, -12.34);
		divMixedOpenToPositiveInfinityByOpenToNegativeInfinity(-1E245, -1E-3);
		divMixedOpenToPositiveInfinityByOpenToNegativeInfinity(-1E245, -1E23);
	}

	private void divMixedOpenToPositiveInfinityByOpenToNegativeInfinity(double a1, double b2) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, next(a1 / b2)),
				div(a1, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, b2));
	}

	@Test
	public void openToPositiveInfinityByNegative() {
		// Table 7 row 8 column 1
		divOpenToPositiveInfinityMixedByNegative(2.34, -98.67, -23.45);
		divOpenToPositiveInfinityMixedByNegative(0, -98.67, -23.45);
		divOpenToPositiveInfinityMixedByNegative(1E234, -1E234, -1E-4);
	}

	private void divOpenToPositiveInfinityMixedByNegative(double a1, double b1, double b2) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, next(a1 / b1)),
				div(a1, Double.POSITIVE_INFINITY, b1, b2));
	}

	@Test
	public void openToPositiveInfinityByPositive() {
		// Table 7 row 8 column 2
		divOpenToPositiveInfinityByPositive(1.23, 2.34, 5.67);
		divOpenToPositiveInfinityByPositive(0, 2.34, 5.67);
		divOpenToPositiveInfinityByPositive(0, 1E-4, 1E234);
	}

	private void divOpenToPositiveInfinityByPositive(double a1, double b1, double b2) {
		assertEquals(interval(prev(a1 / b2), Double.POSITIVE_INFINITY),
				div(a1, Double.POSITIVE_INFINITY, b1, b2));
	}

	@Test
	public void openToPositiveInfinityByOpenToNegativeInfinity() {
		// Table 7 row 8 column 3
		divOpenToPositiveInfinityByOpenToNegativeInfinity(1.234, -1.234);
		divOpenToPositiveInfinityByOpenToNegativeInfinity(1E234, -1E234);
		divOpenToPositiveInfinityByOpenToNegativeInfinity(0, -1E234);
		divOpenToPositiveInfinityByOpenToNegativeInfinity(0, -1.234);
	}

	private void divOpenToPositiveInfinityByOpenToNegativeInfinity(double a1, double b2) {
		assertEquals(interval(Double.NEGATIVE_INFINITY, 0),
				div(a1, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, b2));
	}

	@Test
	public void openToPositiveInfinityByOpenToPositiveInfinity() {
		// Table 7 row 8 column 4
		divOpenToPositiveInfinityByOpenToPositiveInfinity(12.34, 56.78);
		divOpenToPositiveInfinityByOpenToPositiveInfinity(0, 56.78);
		divOpenToPositiveInfinityByOpenToPositiveInfinity(1E234, 1E8);
		divOpenToPositiveInfinityByOpenToPositiveInfinity(0, 1E8);
	}

	private void divOpenToPositiveInfinityByOpenToPositiveInfinity(double a1, double b1) {
		assertEquals(interval(0, Double.POSITIVE_INFINITY),
				div(a1, Double.POSITIVE_INFINITY, b1, Double.POSITIVE_INFINITY));
	}

	@Test
	public void divOfWholeShouldBeWhole() {
		// Table 7, row 9
		assertEquals(whole(), divWholeBy(-1.2, -3.4));
		assertEquals(whole(), divWholeBy(1.2, 3.4));
		assertEquals(whole(), divWholeBy(Double.NEGATIVE_INFINITY, 3563.654));
		assertEquals(whole(), divWholeBy(3563.654, Double.POSITIVE_INFINITY));
	}

	private Interval divWholeBy(double b1, double b2) {
		return divide(whole(), interval(b1, b2));
	}

	@Test
	public void divSingletonInfinityBy() {
		assertEquals(positiveInfinity(), divide(positiveInfinity(),
				interval(12.34, 56.78)));
		assertEquals(negativeInfinity(), divide(positiveInfinity(),
				interval(-985.654, -12.34)));
		assertEquals(whole(), divide(positiveInfinity(),
				interval(-985.654, 12.34)));
	}

	@Test
	public void divBySingletonPositiveInfinity() {
		assertEquals(zero(), divide(interval(-987.65, -12.34), positiveInfinity()));
		assertEquals(zero(), divide(interval(-987.65, 12.34), positiveInfinity()));
		assertEquals(zero(), divide(interval(12.34, 567.89), positiveInfinity()));
	}
	
	@Test
	public void divBySingletonNegativeInfinity() {
		assertEquals(zero(), divide(interval(-987.65, -12.34), negativeInfinity()));
		assertEquals(zero(), divide(interval(-987.65, 12.34), negativeInfinity()));
		assertEquals(zero(), divide(interval(12.34, 567.89), negativeInfinity()));
	}

	@Test
	public void divEmptyShouldBeEmpty() {
		assertEquals(undefined(), divide(undefined(), interval(12.34, 34.467)));
		assertEquals(undefined(), divide(undefined(), interval(-12.34, -34.467)));
		assertEquals(undefined(), divide(undefined(), interval(-46412.34, 6543.653)));
		assertEquals(undefined(), divide(undefined(), interval(-1E45, 1345)));
		assertEquals(undefined(), divide(undefined(), interval(Double.NEGATIVE_INFINITY, 1345)));
		assertEquals(undefined(), divide(undefined(), interval(-1E45, Double.POSITIVE_INFINITY)));
	}

	@Test
	public void divByEmptyShouldBeEmpty() {
		assertEquals(undefined(), divide(interval(12.34, 34.467), undefined()));
		assertEquals(undefined(), divide(interval(-12.34, -34.467), undefined()));
		assertEquals(undefined(), divide(interval(-46412.34, 6543.653), undefined()));
		assertEquals(undefined(), divide(interval(-1E45, 1345), undefined()));
		assertEquals(undefined(), divide(interval(Double.NEGATIVE_INFINITY, 1345), undefined()));
		assertEquals(undefined(), divide(interval(-1E45, Double.POSITIVE_INFINITY), undefined()));
	}

	@Test
	public void divNegativeByZeroShouldBeNegativeInfinity() {
		assertEquals(undefined(), divide(interval(-1), zero()));
	}

	@Test
	public void divPositiveWithInverted() {
		divByInverted(4, 14, -2, 2);
		divByInverted(4, 14, 	0, 2);
		divByInverted(2.4, 1E54, -1E2, 44.22);
	}

	@Test
	public void divNegativeWithInverted() {
		divByInverted(-14, 4, -2, 2);
	}

	private void divByInverted(double a1, double a2, double b1, double b2) {
		Interval u1 = interval(Double.NEGATIVE_INFINITY, b1);
		Interval u2 = interval(b2, Double.POSITIVE_INFINITY);
		Interval res1 = divide(interval(a1, a2), u1);
		Interval res2 = divide(interval(a1, a2), u2);
		Interval actual = divide(interval(a1, a2), invertedInterval(b1, b2));
		assertEquals(IntervalOperands.union(res1, res2), actual);
	}

	@Test
	public void divByZeroSingletonShouldBeUndefined() {
		assertEquals(undefined(), divByZeroSingleton(Double.NEGATIVE_INFINITY, -2));
		assertEquals(undefined(), divByZeroSingleton(-2.1, -2));
		assertEquals(undefined(), divByZeroSingleton(-2.1, 0));
		assertEquals(undefined(), divByZeroSingleton(0, 42.567));
		assertEquals(undefined(), divByZeroSingleton(12.34, 42.567));
		assertEquals(undefined(), divByZeroSingleton(12.34, Double.POSITIVE_INFINITY));

	}

	private Interval divByZeroSingleton(double a1, double a2) {
		return divide(new Interval(a1, a2), zero());
	}
}