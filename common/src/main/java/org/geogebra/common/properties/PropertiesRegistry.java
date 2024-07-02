package org.geogebra.common.properties;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.ownership.NonOwning;

/**
 * The PropertiesRegistry is a central lookup point for registered properties.
 */
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
	void addListener(@NonOwning @Nonnull PropertiesRegistryListener listener);

	/**
	 * Remove a listener.
	 *
	 * @param listener A listener.
	 */
	void removeListener(@Nonnull PropertiesRegistryListener listener);

	/**
	 * Set the current context.
	 *
	 * @param context The current context (may be `null`).
	 */
	void setCurrentContext(@NonOwning @Nullable Object context);

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
	 * @implNote Unfortunately, we cannot use `java.lang.ref.WeakReference`, because it's not
	 * supported by <a href="https://www.gwtproject.org/doc/latest/RefJreEmulation.html">GWT's JRE emulation</a>,
	 * so the property will be strongly referenced by the registry. See
	 * {@link #releaseProperties(Object)} on how to purge the registry of all properties for a
	 * certain context.
	 *
	 * @param property A property.
	 */
	void register(@Nonnull Property property);

	/**
	 * Same as {@link #register(Property)}, but using the provided context instead of the
	 * current context.
	 */
	void register(@Nonnull Property property, @Nullable Object context);

	/**
	 * Remove a property from the registry in the current context.
	 *
	 * @param property A property that has previously been registered.
	 */
	void unregister(@Nonnull Property property);

	/**
	 * Same as {@link #unregister(Property)}, but using the provided context instead of the
	 * current context.
	 */
	void unregister(@Nonnull Property property, @Nullable Object context);

	/**
	 * Look up a property by raw (unlocalized) name in the current context.
	 *
	 * @param rawName The raw (unlocalized) name of a property.
	 * @return The property if found, or null if no such property has been registered.
	 */
	@CheckForNull Property lookup(@Nonnull String rawName);

	/**
	 * Same as {@link #lookup(String)}, but using the provided context instead of the
	 * current context.
	 * @return The property with the given name in the given context, or null if no such
	 * property could be found.
	 */
	@CheckForNull Property lookup(@Nonnull String rawName, @Nullable Object context);

	/**
	 * "Release" (i.e., clear out strong references to) all properties registered for the
	 * given context.
	 *
	 * This method is a workaround for the unavailability of weak references in GWT's JRE
	 * emulation (see {@link #register(Property)}).
	 *
	 * @param context A context (may be null).
	 */
	void releaseProperties(@Nullable Object context);
}
