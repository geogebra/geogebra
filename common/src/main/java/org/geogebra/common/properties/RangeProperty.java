package org.geogebra.common.properties;

/**
 * A numeric property with min, max and step.
 * @param <T> The type of the number (Integer, Double, etc.)
 */
public interface RangeProperty<T extends Number & Comparable<T>> extends NumericProperty<T> {

    /**
     * @return step
     */
    T getStep();
}
