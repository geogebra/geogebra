package org.geogebra.web.full.gui.properties.ui.settingsListener;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Widget;

/**
 * Collection of widget that needs to be updates based on EV settings.
 */
public abstract class SettingsListenerPropertyWidgetCollection {
	protected Map<Property, Widget> propertyWidgetCollection = new HashMap<>();

	/**
	 * @param app application
	 * @param viewIDs view ID
	 */
	public SettingsListenerPropertyWidgetCollection(AppW app, int... viewIDs) {
		for (int id : viewIDs) {
			EuclidianSettings evSettings = app.getSettings().getEuclidian(id);
			evSettings.addListener(s -> update());
		}
	}

	abstract void update();

	/**
	 * Add a widget that needs to be updated based on EV settings.
	 */
	public void registerWidget(Widget widget, Property property) {
		propertyWidgetCollection.put(property, widget);
	}
}
