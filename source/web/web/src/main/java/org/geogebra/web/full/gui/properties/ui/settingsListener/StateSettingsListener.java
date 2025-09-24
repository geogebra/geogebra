package org.geogebra.web.full.gui.properties.ui.settingsListener;

import java.util.Map;

import org.geogebra.common.properties.Property;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentComboBox;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Widget;

public class StateSettingsListener extends SettingsListenerPropertyWidgetCollection {
	/**
	 * Updates widgets based on EV settings.
	 * @param appW application
	 * @param viewIDs EV view ids
	 */
	public StateSettingsListener(AppW appW, int... viewIDs) {
		super(appW, viewIDs);
	}

	@Override
	void update() {
		for (Map.Entry<Property, Widget> entry: propertyWidgetCollection.entrySet()) {
			if (entry.getValue() instanceof ComponentCheckbox) {
				((ComponentCheckbox) entry.getValue()).setDisabled(!entry.getKey().isEnabled());
			}
			if (entry.getValue() instanceof ComponentComboBox) {
				((ComponentComboBox) entry.getValue()).setDisabled(!entry.getKey().isEnabled());
			}
			if (entry.getValue() instanceof ComponentInputField) {
				((ComponentInputField) entry.getValue()).setDisabled(!entry.getKey().isEnabled());
			}
		}
	}
}
