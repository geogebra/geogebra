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
