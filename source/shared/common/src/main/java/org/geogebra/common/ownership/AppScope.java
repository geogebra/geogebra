/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details'
 */

package org.geogebra.common.ownership;

import javax.annotation.Nonnull;

import org.geogebra.common.main.App;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;
import org.geogebra.common.properties.impl.general.LanguageProperty;

import com.google.j2objc.annotations.Property;
import com.google.j2objc.annotations.Weak;

/**
 * Container for app-scoped dependencies and services.
 * <p>
 * Provides a dedicated scope for components like {@link PropertiesRegistry} that should be
 * associated with an {@link App} instance without adding to {@code App}'s responsibilities.
 */
public final class AppScope {

	/**
	 * <b>Note:</b> {@code AppScope} must not hold a strong reference on {@code App}, neither
	 * directly nor indirectly (through intermediate fields), as this would cause strong reference
	 * cycles (read: memory leaks) on iOS. To fix such reference cycles, add {@code @Weak} to any
	 * such fields holding a strong reference on {@link App}.
	 */
	@Weak
	private final App app;

	@Property("readonly")
	public final @Nonnull PropertiesRegistry propertiesRegistry = new DefaultPropertiesRegistry();

	/**
	 * Constructor
	 * @param app The app this {@code AppScope} is associated with.
	 */
	public AppScope(App app) {
		this.app = app;
	}

	/**
	 * Get the language property for an app. This property will be synchronized with language
	 * changes in other apps in the same {@link SuiteScope}.
	 * @return The language property for this app.
	 * @throws IllegalStateException if no {@code SuiteScope} has been set up for this app instance
	 */
	public @Nonnull LanguageProperty getLanguageProperty() {
		SuiteScope suiteScope = GlobalScope.getSuiteScope(app);
		if (suiteScope == null) {
			throw new IllegalStateException("suiteScope not set up");
		}
		return suiteScope.getLanguageProperty(app);
	}
}
