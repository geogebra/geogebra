/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
