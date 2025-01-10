package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.PRECISION;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;

import java.util.Objects;

import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.interval.operators.RMath;
import org.geogebra.common.util.DoubleUtil;

/**
 * Class to implement interval arithmetic
 *
 */
public class Interval {
	private double low;
	private double high;
	private boolean inverted = false;
	private double precision = PRECISION;

	/**
	 * Creates a singleton interval [value, value]
	 *
	 * @param value for the singleton interval.
	 */
	public Interval(double value) {
		this(value, value);
	}

	/**
	 * Creates an interval with bounds [low, high]
	 *
	 * @param low lower bound of the interval
	 * @param high higher bound of the interval
	 */
	public Interval(double low, double high) {
		if (high < low) {
			setUndefined();
		} else {
			set(low, high);
		}
	}

	/**
	 * Creates an undefined interval.
	 */
	public Interval() {
		setUndefined();
	}

	/**
	 * Copy constructor
	 *
	 * @param other to copy.
	 */
	public Interval(Interval other) {
		this(other.low, other.high);
		inverted = other.inverted;
	}

	/**
	 *
	 * @param interval interval.
	 * @param other interval.
	 * @return the max of interval and other.
	 */
	public static Interval max(Interval interval, Interval other) {
		if (interval.isUndefined() && other.isUndefined()) {
			return undefined();
		} else if (interval.isUndefined()) {
			return other;
		} else if (other.isUndefined()) {
			return interval;
		}

		return new Interval(Math.max(interval.low, other.low),
				Math.max(interval.high, other.high));
	}

	/**
	 *
	 * @param interval interval.
	 * @param other interval.
	 * @return the min of interval and other.
	 */
	public static Interval min(Interval interval, Interval other) {
		if (interval.isUndefined() && other.isUndefined()) {
			return undefined();
		} else if (interval.isUndefined()) {
			return other;
		} else if (other.isUndefined()) {
			return interval;
		}

		return new Interval(Math.min(interval.low, other.low),
				Math.min(interval.high, other.high));
	}

	/**
	 * Makes interval undefined, which is represented by [inf, -inf]
	 */
	public void setUndefined() {
		set(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		inverted = false;
	}

	/**
	 * Interval addition
	 *
	 * @param other interval to add
	 * @return this as result
	 */
	public Interval add(Interval other) {
		if (isUndefined() || other.isUndefined()) {
			setUndefined();
			return this;
		}

		low += other.low;
		high += other.high;
		updateInversion(other.inverted);
		return this;
	}

	private void updateInversion(boolean otherInverted) {
		if (inverted && otherInverted) {
			setUndefined();
		} else {
			inverted = inverted || otherInverted;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o instanceof Interval) {
			return almostEqual((Interval) o, precision);
		}
		return false;

	}

	@Override
	public String toString() {
		if (isWhole()) {
			return "Interval [-Infinity, Infinity] " + invertedPostfix();
		}

		String result = "Interval [";
		if (!isUndefined()) {
			result += low;
			if (!isSingleton()) {
				result += ", " + high;
			}
		}

		result += "] " + invertedPostfix();
		return result;
	}

	private String invertedPostfix() {
		return inverted ? "Inverted" : "";
	}

	/**
	 * Interval subtraction
	 *
	 * @param other to subtract from this interval
	 * @return this as result.
	 */
	public Interval subtract(Interval other) {
		if (isUndefined() || other.isUndefined()) {
			setUndefined();
		} else {
			low -= other.high;
			high -= other.low;
			updateInversion(other.inverted);
		}
		return this;
	}

	/**
	 *
	 * @return if interval has zero in it.
	 */
	public boolean hasZero() {
		return contains(0);
	}

	/**
	 *
	 * @return if interval represents all the real numbers R.
	 */
	public boolean isWhole() {
		return DoubleUtil.isEqual(low, Double.NEGATIVE_INFINITY)
				&& DoubleUtil.isEqual(high, Double.POSITIVE_INFINITY);
	}

	/**
	 *
	 * @return if interval is in the form [n, n] where n is finite.
	 */
	public boolean isSingleton() {
		return Double.isFinite(low) && DoubleUtil.isEqual(high, low, 1E-7);
	}

	@Override
	public int hashCode() {
		return Objects.hash(low, high);
	}

	/**
	 *
	 * @return if interval is undefined.
	 */
	public boolean isUndefined() {
		return  low > high;
	}

	/**
	 *
	 * @return lower bound.
	 */
	public double getLow() {
		return low;
	}

	/**
	 *
	 * @return upper bound.
	 */
	public double getHigh() {
		return high;
	}

	/**
	 *
	 * @param other interval to check.
	 * @return if intervals are overlapping
	 */
	public boolean isOverlap(Interval other) {
		if (isUndefined() || other.isUndefined()) {
			return false;
		}
		return (low <= other.low && other.low <= high)
				|| (other.low <= low && low <= other.high);
	}

	/**
	 * @param other to compare
	 * @param precision with
	 * @return if the other interval is equal with a precision
	 */
	public boolean almostEqual(Interval other, double precision) {
		if (isUndefined() && other.isUndefined()) {
			return true;
		}

		return inverted == other.inverted
				&& DoubleUtil.isEqual(low, other.low, precision)
				&& DoubleUtil.isEqual(high, other.high, precision);
	}

	/**
	 *
	 * @param delta it might differ from zero.
	 * @return if interval is zero with a given torelance specifiedf by delta.
	 */
	public boolean isZeroWithDelta(double delta) {
		return DoubleUtil.isEqual(low, 0, delta)
				&& DoubleUtil.isEqual(high, 0, delta);

	}

	/**
	 * Make interval as whole.
	 */
	public void setWhole() {
		set(IntervalConstants.whole());
	}

	/**
	 * Sets the closed interval bounds as the other interval
	 * @param other interval to set bounds as the same.
	 */
	public void set(Interval other) {
		set(other.low, other.high);
		inverted = other.inverted;
	}

	/**
	 * Sets interval as singleton
	 * @param value to set.
	 */
	public void set(double value) {
		set(value, value);
	}

	/**
	 * Sets the closed interval bounds.
	 *
	 * @param low lower bound.
	 * @param high higher bound.
	 */
	public void set(double low, double high) {
		this.low = filterNegativeZero(low);
		this.high = filterNegativeZero(high);
	}

	private double filterNegativeZero(double value) {
		return DoubleUtil.isEqual(-0.0, value, 0) ? 0 : value;
	}

	/**
	 * [a, b] -&gt; (a, b]
	 * @return this as result
	 */
	public Interval halfOpenLeft() {
		low = RMath.next(low);
		return this;
	}

	/**
	 * [a, b] -&gt; (a, b]
	 *
	 * @param a low limit
	 * @param b high limit
	 * @return this as result
	 */
	public Interval halfOpenLeft(double a, double b) {
		set(RMath.next(a), b);
		return this;
	}

	/**
	 * [a, b] -&gt; [a, b)
	 * @return this as result
	 */
	public Interval halfOpenRight() {
		high = RMath.prev(high);
		return this;
	}

	/**
	 * [a, b] -&gt; [a, b)
	 *
	 * @param a low limit
	 * @param b high limit
	 * @return this as result
	 */
	public Interval halfOpenRight(double a, double b) {
		set(a, RMath.prev(b));
		return this;
	}

	/**
	 * Checks if the interval is
	 * either [-inf, -inf] or [inf, inf].
	 *
	 * @return true if infinite.
	 */
	public boolean isInfiniteSingleton() {
		return (isLowInfinite() || isHighInfinite()) && DoubleUtil.isEqual(high, low);
	}

	/**
	 *
	 * @return width of the interval.
	 */
	public double getWidth() {
		if (isUndefined()) {
			return 0;
		}
		return RMath.subHigh(high, low);
	}

	/**
	 * "Invert" the interval
	 * @return this as result.
	 */
	public Interval negative() {
		set(-high, -low);
		return this;
	}

	/**
	 *
	 * @return the bounds as array
	 */
	public double[] toArray() {
		return new double[] {low, high};
	}

	public void setZero() {
		set(IntervalConstants.zero());
	}

	/**
	 *
	 * @return if interval is [0].
	 */
	public boolean isZero() {
		return isZeroWithDelta(precision);
	}

	public boolean contains(double value) {
		return value >= low && value <= high;
	}

	public boolean containsExclusive(int value) {
		return value > low && value < high;
	}

	public boolean contains(Interval interval) {
		return interval.low > low && interval.high < high;
	}

	/**
	 *
	 * @param other to compare with.
	 * @return if this interval is greater than the other.
	 */
	public boolean isGreaterThan(Interval other) {
		if (isUndefined() || other == null || other.isUndefined()) {
			return false;
		}

		return high > other.high;
	}

	/**
	 *
	 * @param other to compare with.
	 * @return if this interval is less than the other.
	 */
	public boolean isLessThan(Interval other) {
		if (isUndefined() || other == null || other.isUndefined()) {
			return false;
		}

		return high < other.low;
	}

	/**
	 * Shift the interval (both low and high) by a given value.
	 * @param deltaX to shift by.
	 * @return the result interval;
	 */
	public Interval shiftBy(double deltaX) {
		set(low + deltaX, high + deltaX);
		return this;
	}

	/**
	 *
	 * @return shorten string form to compare and debug
	 */
	public String toShortString() {
		String result = "[";
		if (!isUndefined()) {
			result += low;
			if (!isSingleton()) {
				result += ", " + high;
			}
		}

		result += "]";
		if (inverted) {
			result += " Inverted";
		}
		return result;
	}

	/**
	 *
	 * @return the length of the interval
	 */
	public double getLength() {
		if (isUndefined()) {
			return 0;
		}
		return Math.abs(high - low);
	}

	/**
	 *
	 * @param low to set.
	 */
	public void setLow(double low) {
		this.low = low;
	}

	/**
	 *
	 * @param high to set.
	 */
	public void setHigh(double high) {
		this.high = high;
	}

	public boolean isHalfPositiveInfinity() {
		return DoubleUtil.isEqual(0, low, 1E-5) && high == Double.POSITIVE_INFINITY;
	}

	public boolean isHalfNegativeInfinity() {
		return low == Double.NEGATIVE_INFINITY && DoubleUtil.isEqual(high, 0, 1E-5);
	}

	/**
	 *
	 * @return true if interval one or both border is infinite.
	 */
	public boolean hasInfinity() {
		return !isUndefined() && isLowInfinite() || isHighInfinite();
	}

	/**
	 *
	 * @return true if high is infinite
	 */
	public boolean isHighInfinite() {
		return high == Double.POSITIVE_INFINITY;
	}

	/**
	 *
	 * @return true if low is infinite
	 */
	public boolean isLowInfinite() {
		return low == Double.NEGATIVE_INFINITY;
	}

	/**
	 *
	 * @return if the interval is the unit one.
	 */
	public boolean isOne() {
		return DoubleUtil.isEqual(low, 1, precision)
				&& DoubleUtil.isEqual(high, 1, precision);
	}

	/**
	 *
	 * @return if the interval is the negative unit one.
	 */
	public boolean isMinusOne() {
		return DoubleUtil.isEqual(low, -1, precision)
				&& DoubleUtil.isEqual(high, -1, precision);
	}

	public boolean isFinite() {
		return Double.isFinite(low) && Double.isFinite(high);
	}

	/**
	 *
	 * @return true if any of the bounds is infinite but not both.
	 */
	public boolean isSemiInfinite() {
		return (isLowInfinite() && !isHighInfinite())
				|| (!isLowInfinite() && isHighInfinite());
	}

	/**
	 *
	 * @return if interval is a positive infinite singleton.
	 */
	public boolean isPositiveInfinity() {
		return DoubleUtil.isEqual(low, Double.POSITIVE_INFINITY)
				&& DoubleUtil.isEqual(high, low);
	}

	/**
	 *
	 * @return if interval is a negative infinite singleton.
	 */
	public boolean isNegativeInfinity() {
		return DoubleUtil.isEqual(low, Double.NEGATIVE_INFINITY)
				&& DoubleUtil.isEqual(high, low);
	}

	/**
	 *
	 * @param other interval to check
	 * @return true if the bounds of the intervals has the same sign
	 * respectively, ie low (or high) &lt; 0 then other.low (or other.high)&lt; 0 or reverse.
	 */
	public boolean isSignEquals(Interval other) {
		return DoubleUtil.isEqual(Math.abs(low), other.low)
				&& DoubleUtil.isEqual(Math.abs(high), other.high);
	}

	/**
	 *
	 * @return if both bounds are positive.
	 */
	public boolean isPositive() {
		return low > 0;
	}

	/**
	 *
	 * @return if low and high is positive or low == 0 and high is positivr
	 */
	public boolean isPositiveWithZero() {
		return low >= 0;
	}

	/**
	 *
	 * @return if both bounds are &gt;= 0.
	 */
	public boolean isNatural() {
		return !isUndefined() && low > 0;
	}

	/**
	 *
	 * @return if both bounds are negative.
	 */
	public boolean isNegative() {
		return high < 0;
	}

	/**
	 *
	 * @return if low and high is negative, or low &lt; 0 and high is 0.
	 */
	public boolean isNegativeWithZero() {
		return high <= 0;
	}

	/**
	 *
	 * @return if interval is [n, n] where n is an integer .
	 */
	public boolean isSingletonInteger() {
		return isSingleton() && DoubleUtil.isEqual(low, Math.round(low));
	}

	/**
	 * Inverts interval
	 * @return this
	 */
	public Interval invert() {
		setInverted(true);
		return this;
	}

	/**
	 * Clears interval as inverted.
	 * @return this
	 */
	public Interval uninvert() {
		setInverted(false);
		return this;
	}

	/**
	 *
	 * @return if interval is inverted,
	 * ie equals [-inf, low] union [high, inf].
	 */
	public boolean isInverted() {
		return inverted;
	}

	/**
	 *
	 * @param low to check
	 * @return whether low bound is equal to a specific value.
	 */
	public boolean lowEquals(double low) {
		return DoubleUtil.isEqual(this.low, low, precision);
	}

	/**
	 *
	 * @param high to check
	 * @return whether high bound is equal to a specific value.
	 */
	public boolean highEquals(double high) {
		return DoubleUtil.isEqual(this.high, high, precision);
	}

	/**
	 *
	 * @return round to zero within the given precision
	 */
	public Interval round() {
		return new Interval(Math.abs(low) < precision ? 0 : low,
				Math.abs(high) < precision ? 0 : high);
	}

	/**
	 * Sets interval [low, high] inverted. This really means:
	 * [-inf, low] union [high, inf]
	 * @param inverted the flag to set.
	 */
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}

	/**
	 *
	 * @return [-inf, a] for inverted intervals, undefined() otherwise
	 */
	public Interval extractLow() {
		return isInverted() ? new Interval(Double.NEGATIVE_INFINITY, low) : undefined();
	}

	/**
	 *
	 * @return [high, inf] for inverted intervals, undefined otherwise
	 */
	public Interval extractHigh() {
		return isInverted() ? new Interval(high, Double.POSITIVE_INFINITY) : undefined();
	}

	public boolean isLessThanOrEqual(Interval y2) {
		return isLessThan(y2) || almostEqual(y2, 1E-7);
	}

	public double middle() {
		return low + getLength() / 2;
	}

	/**
	 * Set precision to determine zero interval
	 * @param precision to set.
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public void setDefaultPrecision() {
		precision = PRECISION;
	}

	public boolean isExactSingleton() {
		return MyDouble.exactEqual(low,  high);
	}
}