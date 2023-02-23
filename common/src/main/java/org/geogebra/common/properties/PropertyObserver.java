package org.geogebra.common.properties;

/**
 * This object can be registered to an ObservableProperty, and in turn will receive
 * changes to the property.
 */
public interface PropertyObserver {

	/**
	 * Called when the property has changed.
	 * @param property property
	 */
	void onChange(Property property);
}
