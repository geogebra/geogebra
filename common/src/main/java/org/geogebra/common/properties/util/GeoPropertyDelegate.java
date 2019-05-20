package org.geogebra.common.properties.util;

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Internal interface for properties that are related to GeoElements.
 *
 * @param <T> value type
 */
public interface GeoPropertyDelegate<T> {

	/**
	 * Get the property value for this element.
	 *
	 * @param element element
	 * @return value of the property
	 */
	T getPropertyValue(GeoElementND element);

	/**
	 * Set the value for this property.
	 *
	 * @param element element
	 * @param value   value of the property
	 */
	void setPropertyValue(GeoElementND element, T value);

	/**
	 * Check if this element has the property.
	 *
	 * @param element element
	 * @return true if the element has the property, false otherwise
	 */
	boolean hasProperty(GeoElementND element);
}
