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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.main.App;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.general.LanguageProperty;

/**
 * Every Suite app (or standalone app like Graphing, Geometry, etc) has one {@code SuiteScope},
 * which it sets up by calling {@link GlobalScope#registerNewSuiteScope()} early during host app
 * startup.
 *
 * The Suite app is expected to register any {@link App} instances it creates with its
 * {@link SuiteScope}. (Sub-)apps can then look up the {@link SuiteScope} via
 * {@link GlobalScope#getSuiteScope(App)}.
 */
public final class SuiteScope implements PropertyValueObserver {

	public final @Nonnull GeoElementPropertiesFactory geoElementPropertiesFactory =
			new GeoElementPropertiesFactory();
	public final @Nonnull ContextMenuFactory contextMenuFactory = new ContextMenuFactory();
	public final @Nonnull ExamController examController = new ExamController(
			geoElementPropertiesFactory, contextMenuFactory);

    final Set<App> apps = new HashSet<>();
	private final Map<App, LanguageProperty> languageProperties = new HashMap<>();

	/**
	 * Prevent instantiation outside package.
	 */
	SuiteScope() {
	}

	/**
	 * Register an app instance with this suite scope. It's Ok to register an app instance more
	 * than once, subsequent registrations of the same app instance will have no effect.
	 * @param app An app instance.
	 */
	public void registerApp(App app) {
		apps.add(app);
	}

	/**
	 * @return The list of enabled (not-disabled) {@link SuiteSubApp}s in case an exam is
	 * currently active, or a list of all {@code SuiteSubApp} values otherwise.
	 */
	public @Nonnull List<SuiteSubApp> getEnabledSubApps() {
		if (examController.isExamActive()) {
			return SuiteSubApp.availableValues().stream()
					.filter(subApp -> !examController.isDisabledSubApp(subApp))
					.collect(Collectors.toList());
		}
		return SuiteSubApp.availableValues();
	}

	/**
	 * Get the language property for an app. This property will be synchronized with language
	 * changes in other apps, to keep them all aligned.
	 * @param app An app.
	 * @return The language property for this app.
	 */
	public @Nonnull LanguageProperty getLanguageProperty(App app) {
		LanguageProperty languageProperty = languageProperties.get(app);
		if (languageProperty == null) {
			languageProperty = new LanguageProperty(app, app.getLocalization());
			languageProperty.addValueObserver(this);
			languageProperties.put(app, languageProperty);
		}
		return languageProperty;
	}

	// Getters for Swift (j2objc annotations not possible here, fail to compile in desktop project)

	public final @Nonnull GeoElementPropertiesFactory getGeoElementPropertiesFactory() {
		return geoElementPropertiesFactory;
	}

	public final @Nonnull ContextMenuFactory getContextMenuFactory() {
		return contextMenuFactory;
	}

	public final @Nonnull ExamController getExamController() {
		return examController;
	}

	// -- PropertyValueObserver --

	@Override
	public void onDidSetValue(ValuedProperty property) {
		if (property instanceof LanguageProperty incomingProperty) {
			// distribute new value to all other languageProperties
			for (LanguageProperty languageProperty : languageProperties.values()) {
				if (property != languageProperty) {
					languageProperty.setValue(incomingProperty.getValue());
				}
			}
		}
	}
}
