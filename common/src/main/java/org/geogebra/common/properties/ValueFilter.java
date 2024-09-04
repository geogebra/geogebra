package org.geogebra.common.properties;

import org.geogebra.common.exam.restrictions.PropertyRestriction;

/**
 * A filter for property values.
 * <p>
 * See {@link PropertyRestriction} for a use case.
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
