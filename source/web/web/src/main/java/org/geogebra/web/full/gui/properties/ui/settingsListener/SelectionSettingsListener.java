package org.geogebra.web.full.gui.properties.ui.settingsListener;

import java.util.Map;

import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Widget;

public class SelectionSettingsListener extends SettingsListenerPropertyWidgetCollection {
	/**
	 * @param app application
	 */
	public SelectionSettingsListener(AppW app) {
		super(app, 1);
	}

	@Override
	void update() {
		for (Map.Entry<Property, Widget> entry: propertyWidgetCollection.entrySet()) {
			if (entry.getKey() instanceof NamedEnumeratedProperty<?>
					&& entry.getValue() instanceof ComponentDropDown) {
				((ComponentDropDown) entry.getValue()).setSelectedIndex(
						((NamedEnumeratedProperty<?>) entry.getKey()).getIndex());
			}
		}
	}
}
