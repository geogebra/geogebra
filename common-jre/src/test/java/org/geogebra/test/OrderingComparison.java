/*  Copyright (c) 2000-2009 hamcrest.org
 */
package org.geogebra.test;

import static java.lang.Integer.signum;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class OrderingComparison<T extends Comparable<T>>
		extends TypeSafeMatcher<T> {
	private static final int LESS_THAN = -1;
	private static final int GREATER_THAN = 1;
	private static final int EQUAL = 0;
	private final T expected;
	private final int minCompare, maxCompare;

	private static final String[] comparisonDescriptions = { "less than",
			"equal to", "greater than" };

	private OrderingComparison(T expected, int minCompare, int maxCompare) {
		this.expected = expected;
		this.minCompare = minCompare;
		this.maxCompare = maxCompare;
	}

	@Override
	public boolean matchesSafely(T actual) {
		int compare = signum(actual.compareTo(expected));
		return minCompare <= compare && compare <= maxCompare;
	}

	@Override
	public void describeMismatchSafely(T actual,
			Description mismatchDescription) {
		mismatchDescription.appendValue(actual).appendText(" was ")
				.appendText(asText(actual.compareTo(expected))).appendText(" ")
				.appendValue(expected);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("a value ").appendText(asText(minCompare));
		if (minCompare != maxCompare) {
			description.appendText(" or ").appendText(asText(maxCompare));
		}
		description.appendText(" ").appendValue(expected);
	}

	private static String asText(int comparison) {
		return comparisonDescriptions[signum(comparison) + 1];
	}

	/**
	 * Creates a matcher of {@link Comparable} object that matches when the
	 * examined object is equal to the specified value, as reported by the
	 * <code>compareTo</code> method of the <b>examined</b> object.
	 * <p/>
	 * For example:
	 * 
	 * <pre>
	 * assertThat(1, comparesEqualTo(1))
	 * </pre>
	 * 
	 * @param value
	 *            the value which, when passed to the compareTo method of the
	 *            examined object, should return zero
	 * 
	 */
	@Factory
	public static <T extends Comparable<T>> Matcher<T> comparesEqualTo(
			T value) {
		return new OrderingComparison<T>(value, EQUAL, EQUAL);
	}

	/**
	 * Creates a matcher of {@link Comparable} object that matches when the
	 * examined object is greater than the specified value, as reported by the
	 * <code>compareTo</code> method of the <b>examined</b> object.
	 * <p/>
	 * For example:
	 * 
	 * <pre>
	 * assertThat(2, greaterThan(1))
	 * </pre>
	 * 
	 * @param value
	 *            the value which, when passed to the compareTo method of the
	 *            examined object, should return greater than zero
	 * 
	 */
	@Factory
	public static <T extends Comparable<T>> Matcher<T> greaterThan(T value) {
		return new OrderingComparison<T>(value, GREATER_THAN, GREATER_THAN);
	}

	/**
	 * Creates a matcher of {@link Comparable} object that matches when the
	 * examined object is greater than or equal to the specified value, as
	 * reported by the <code>compareTo</code> method of the <b>examined</b>
	 * object.
	 * <p/>
	 * For example:
	 * 
	 * <pre>
	 * assertThat(1, greaterThanOrEqualTo(1))
	 * </pre>
	 * 
	 * @param value
	 *            the value which, when passed to the compareTo method of the
	 *            examined object, should return greater than or equal to zero
	 * 
	 */
	@Factory
	public static <T extends Comparable<T>> Matcher<T> greaterThanOrEqualTo(
			T value) {
		return new OrderingComparison<T>(value, EQUAL, GREATER_THAN);
	}

	/**
	 * Creates a matcher of {@link Comparable} object that matches when the
	 * examined object is less than the specified value, as reported by the
	 * <code>compareTo</code> method of the <b>examined</b> object.
	 * <p/>
	 * For example:
	 * 
	 * <pre>
	 * assertThat(1, lessThan(2))
	 * </pre>
	 * 
	 * @param value
	 *            the value which, when passed to the compareTo method of the
	 *            examined object, should return less than zero
	 * 
	 */
	@Factory
	public static <T extends Comparable<T>> Matcher<T> lessThan(T value) {
		return new OrderingComparison<T>(value, LESS_THAN, LESS_THAN);
	}

	/**
	 * Creates a matcher of {@link Comparable} object that matches when the
	 * examined object is less than or equal to the specified value, as reported
	 * by the <code>compareTo</code> method of the <b>examined</b> object.
	 * <p/>
	 * For example:
	 * 
	 * <pre>
	 * assertThat(1, lessThanOrEqualTo(1))
	 * </pre>
	 * 
	 * @param value
	 *            the value which, when passed to the compareTo method of the
	 *            examined object, should return less than or equal to zero
	 * 
	 */
	@Factory
	public static <T extends Comparable<T>> Matcher<T> lessThanOrEqualTo(
			T value) {
		return new OrderingComparison<T>(value, LESS_THAN, EQUAL);
	}
}
