package org.geogebra.common.properties;

import org.geogebra.common.ownership.NonOwning;

public interface PropertiesRegistry {

	/**
	 * Add a listener.
	 *
	 * @implNote Even though the argument is marked {@link NonOwning}, the PropertiesRegistry
	 * will hold a strong reference onto the listener, because we cannot use weak references
	 * (not supported by <a href="https://www.gwtproject.org/doc/latest/RefJreEmulation.html">GWT's JRE emulation</a>).
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
	 * Register a property with the registry in the current context. The registration key is the
	 * property's raw (unlocalized) name.
	 * <p/>
	 * If a property with the same raw name has been registered previously (in the current context),
	 * the old instance is overwritten with the new instance. This should prevent issues when
	 * properties are potentially created and registered several times
	 * (e.g., from UI that can be presented and dismissed multiple times while staying in the
	 * same context).
	 *
	 * @param property A property.
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
	 * @return The property with the given name in the given context, or null if no such
	 * property could be found.
	 */
	Property lookup(String rawName, Object context);
}
