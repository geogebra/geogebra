package org.geogebra.web.full.gui.properties.ui.settingsListener;

import java.util.Map;

import org.geogebra.common.properties.Property;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Widget;

public class VisibilitySettingsListener extends SettingsListenerPropertyWidgetCollection {
	/**
	 * Updates widgets based on EV settings.
	 * @param appW application
	 * @param viewIDs EV view ids
	 */
	public VisibilitySettingsListener(AppW appW, int... viewIDs) {
		super(appW, viewIDs);
	}

	@Override
	void update() {
		for (Map.Entry<Property, Widget> entry: propertyWidgetCollection.entrySet()) {
			entry.getValue().setVisible(entry.getKey().isEnabled());
		}
	}
}
