package org.geogebra.common.properties;

import javax.annotation.Nullable;

/**
 * A property whose values are constrained to a subset of all possible values.
 * Any possible value can be validated against this property
 * using {@link ConstrainedProperty#validateValue(Object)}.
 * @param <V> the type of the value
 */
public interface ConstrainedProperty<V> extends ValuedProperty<V> {

	/**
	 * Validates a value for this property. If the value is valid, it returns a null,
	 * otherwise it returns a localized error message.
	 * @param value value to validate for this property
	 * @return null if the value is valid, otherwise a localized error message
	 */
	@Nullable
	String validateValue(V value);

}
