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
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.properties.ui.panel;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.ActionableProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.ActionablePropertyCollection;
import org.geogebra.common.properties.impl.general.RestoreSettingsAction;
import org.geogebra.common.properties.impl.general.SaveSettingsAction;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.gwtproject.user.client.ui.FlowPanel;

public class ActionableButtonPanel extends FlowPanel implements SetLabels {
	private final Map<StandardButton, Property> widgetLabelCollection = new HashMap<>();

	/**
	 * Create the actionable button panel
	 * @param actionablePropertyCollection {@link ActionablePropertyCollection}
	 */
	public ActionableButtonPanel(ActionablePropertyCollection<?> actionablePropertyCollection) {
		addStyleName("actionableButtonPanel");
		buildGUI(actionablePropertyCollection);
	}

	private void buildGUI(ActionablePropertyCollection<?> actionablePropertyCollection) {
		for (ActionableProperty actionableProperty
				: actionablePropertyCollection.getProperties()) {
			StandardButton button = new StandardButton(actionableProperty.getName());
			if (actionableProperty instanceof SaveSettingsAction) {
				button.addStyleName("dialogContainedButton");
			} else if (actionableProperty instanceof RestoreSettingsAction) {
				button.addStyleName("materialOutlinedButton");
			}
			button.addFastClickHandler(source -> actionableProperty.performAction());
			widgetLabelCollection.put(button, actionableProperty);
			add(button);
		}
	}

	@Override
	public void setLabels() {
		widgetLabelCollection.forEach(
				(button, key) -> button.setLabel(key.getName()));
	}
}
