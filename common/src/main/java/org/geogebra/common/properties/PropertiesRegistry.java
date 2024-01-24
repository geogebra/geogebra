package org.geogebra.common.properties;

public interface PropertiesRegistry {

	/**
	 * Add a listener.
	 *
	 * @param listener A listener.
	 */
	void addListener(PropertiesRegistryListener listener);

	/**
	 * Remove a listener.
	 *
	 * @param listener A listener.
	 */
	void removeListener(PropertiesRegistryListener listener);

	/**
	 * Register a property by raw (unlocalized) name.
	 *
	 * @param property A property.
	 *
	 * @implNote The app may change (when switching calculators), and a new property
	 * of the same type as a previously registered property may get registered, so we
	 * probably need to register the (app, property) tuple, really.
	 */
	void register(Property property);

	/**
	 * Look up a property by raw (unlocalized) name.
	 *
	 * @param rawName The raw (unlocalized) name of a property.
	 * @return The property if found, or null if no such property has been registered.
	 */
	Property lookup(String rawName);
}
