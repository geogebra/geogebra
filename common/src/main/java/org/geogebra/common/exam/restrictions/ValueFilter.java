package org.geogebra.common.exam.restrictions;

import org.geogebra.common.properties.EnumeratedProperty;

/**
 * The {@code ValueFilter} interface defines a contract for filtering values and it is used by the
 * {@link PropertyRestriction} to decide whether the value is allowed for a
 * {@link EnumeratedProperty} or not during exam mode.
 */
public interface ValueFilter {
    /**
     * Evaluates whether the specified value is allowed by this filter.
     *
     * @param value the value to be evaluated
     * @return {@code true} if the value is allowed, {@code false} otherwise
     */
    boolean isValueAllowed(Object value);
}
