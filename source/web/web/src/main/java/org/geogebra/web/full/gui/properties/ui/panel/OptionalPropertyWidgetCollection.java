package org.geogebra.web.full.gui.properties.ui.panel;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Widget;

/**
 * Collection of widget that may be hidden based on EV settings.
 */
public class OptionalPropertyWidgetCollection {

	private final Map<Property, Widget> conditionalWidgets = new HashMap<>();

	/**
	 * @param app application
	 */
	public OptionalPropertyWidgetCollection(AppW app) {
		EuclidianSettings evSettings3d = app.getSettings().getEuclidian(3);
		EuclidianSettings evSettings = app.getSettings().getEuclidian(1);
		evSettings3d.addListener(s -> updateVisibility());
		evSettings.addListener(s -> updateVisibility());
	}

	private void updateVisibility() {
		for (Map.Entry<Property, Widget> entry: conditionalWidgets.entrySet()) {
			entry.getValue().setVisible(entry.getKey().isEnabled());
		}
	}

	/**
	 * Add a widget that may be hidden based on EV settings.
	 */
	public void addOptionalWidget(Widget widget, Property property) {
		conditionalWidgets.put(property, widget);
		widget.setVisible(property.isEnabled());
	}
}
