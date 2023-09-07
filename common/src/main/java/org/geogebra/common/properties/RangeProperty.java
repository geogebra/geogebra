package org.geogebra.common.properties;

/**
 * A numeric property with min, max and step. {@link RangeProperty#setValue(Object)} will throw
 * a {@link RuntimeException} if the value is not between {@link RangeProperty#getMin()} and
 * {@link RangeProperty#getMax()}. The value must not always be of step size returned by
 * {@link RangeProperty#getStep()}.
 * <p>
 * For example, in the Integer case, if the value is between [0, 100] and step is 5,
 * the recommended values are [0, 5, 10, 15, ..., 100],
 * but they can be any number between 0 and 100.
 * @param <T> The type of the number (Integer, Double, etc.)
 */
public interface RangeProperty<T extends Number & Comparable<T>> extends ValuedProperty<T> {

	/**
	 * Returns the smallest possible value for this property inclusively.
	 * The parameter of {@link RangeProperty#setValue(Object)} must be greater than or equal to
	 * this value, otherwise a {@link RuntimeException} is thrown.
	 * @return minimal value
	 */
	T getMin();

	/**
	 * Returns the maximal possible value for this property inclusively.
	 * The parameter of {@link RangeProperty#setValue(Object)} must be less than or equal to
	 * this value, otherwise a {@link RuntimeException} is thrown.
	 * @return maximal value
	 */
	T getMax();

	/**
	 * Returns the preferred step size between the min and max values.
	 * This value is just a recommendation, it does not have any effect on the calling of
	 * {@link RangeProperty#setValue(Object)}.
	 * @return step
	 */
	T getStep();
}
