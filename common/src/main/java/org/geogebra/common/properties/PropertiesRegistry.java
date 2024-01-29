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
	 * Register a property with the registry.
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
	 * Remove a property from the registry.
	 *
	 * @param property A property that has previously been registered.
	 */
	void unregister(Property property);

	/**
	 * Look up a property by raw (unlocalized) name.
	 *
	 * @param rawName The raw (unlocalized) name of a property.
	 * @return The property if found, or null if no such property has been registered.
	 */
	Property lookup(String rawName);
}
