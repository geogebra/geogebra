package org.geogebra.common.properties;

import org.geogebra.common.ownership.NonOwning;

public interface PropertiesRegistry {

	/**
	 * Add a listener.
	 *
	 * @param listener A listener.
	 */
	void addListener(@NonOwning PropertiesRegistryListener listener);

	/**
	 * Remove a listener.
	 *
	 * @param listener A listener.
	 */
	void removeListener(PropertiesRegistryListener listener);

	/**
	 * Set the current context.
	 *
	 * @param context The current context (may be `null`).
	 */
	void setCurrentContext(@NonOwning Object context);

	/**
	 * Register a property with the registry in the current context.
	 *
	 * If a property with the same raw name has been registered previously, the old
	 * instance is unregistered before registering the new instance. This should prevent
	 * issues when properties are potentially created and registered several times
	 * (e.g., from UI that can be presented and dismissed multiple times).
	 *
	 * @param property A property.
	 *
	 * @implNote The registration key is the property's raw (unlocalized) name.
	 *
	 * @implNote The app may change (when switching calculators), and a new property
	 * of the same type as a previously registered property may get registered, so we
	 * probably need to register the (app, property) tuple, really.
	 */
	void register(Property property);

	/**
	 * Same as {@link #register(Property)}, but using the provided context instead of the
	 * current context.
	 */
	void register(Property property, Object context);

	/**
	 * Remove a property from the registry in the current context.
	 *
	 * @param property A property that has previously been registered.
	 */
	void unregister(Property property);
	/**
	 * Same as {@link #unregister(Property)}, but using the provided context instead of the
	 * current context.
	 */
	void unregister(Property property, Object context);

	/**
	 * Look up a property by raw (unlocalized) name in the current context.
	 *
	 * @param rawName The raw (unlocalized) name of a property.
	 * @return The property if found, or null if no such property has been registered.
	 */
	Property lookup(String rawName);
	/**
	 * Same as {@link #lookup(String)}, but using the provided context instead of the
	 * current context.
	 */
	Property lookup(String rawName, Object context);
}
