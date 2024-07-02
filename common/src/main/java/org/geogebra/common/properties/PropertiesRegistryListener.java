package org.geogebra.common.properties;

import javax.annotation.Nonnull;

public interface PropertiesRegistryListener {

	/**
	 * Called when a property has been registered in a context.
	 *
	 * @param property The property.
	 * @param context The context (may be null).
	 */
	void propertyRegistered(@Nonnull Property property, Object context);

	/**
	 * Called when a property has been unregistered from a context.
	 *
	 * @param property The property.
	 * @param context The context (may be null).
	 */
	void propertyUnregistered(Property property, Object context);
}
