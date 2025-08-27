package org.geogebra.web.full.gui.properties.ui;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.html5.main.AppW;

public class SettingsChangedListenerWidgetCollection {
	private final Map<NamedEnumeratedProperty<?>, ComponentDropDown> widgetCollection =
			new HashMap<>();

	/**
	 * @param app application
	 */
	public SettingsChangedListenerWidgetCollection(AppW app) {
		EuclidianSettings evSettings = app.getSettings().getEuclidian(1);
		evSettings.addListener(s -> updateSelection());
	}

	private void updateSelection() {
		for (Map.Entry<NamedEnumeratedProperty<?>, ComponentDropDown> entry
				: widgetCollection.entrySet()) {
			entry.getValue().setSelectedIndex(entry.getKey().getIndex());
		}
	}

	/**
	 * register widget to update on settings change
	 * @param property {@link NamedEnumeratedProperty}
	 * @param widget {@link ComponentDropDown}
	 */
	public void registerWidget(NamedEnumeratedProperty<?> property, ComponentDropDown widget) {
		widgetCollection.put(property, widget);
	}
}
