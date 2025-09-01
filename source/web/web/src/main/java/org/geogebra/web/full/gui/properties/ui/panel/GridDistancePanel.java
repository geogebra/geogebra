package org.geogebra.web.full.gui.properties.ui.panel;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.NumericPropertyWithSuggestions;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.graphics.GridDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GridFixedDistanceProperty;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentComboBox;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;

public class GridDistancePanel extends FlowPanel {
	private final AppW appW;
	private final Localization localization;
	private GridFixedDistanceProperty gridDistProperty;
	private NumericPropertyWithSuggestions xGridDistProperty;
	private NumericPropertyWithSuggestions yGridDistProperty;
	private NumericPropertyWithSuggestions gridAngleProperty;
	private ComponentComboBox xDistComboBox;
	private ComponentComboBox yDistComboBox;
	private ComponentComboBox angleComboBox;

	/**
	 * Creates the panel with fixed distance check-box and x/y distance text field
	 * based on {@link GridDistancePropertyCollection}
	 * @param appW application
	 * @param propertyCollection see {@link GridDistancePropertyCollection}
	 */
	public GridDistancePanel(AppW appW, GridDistancePropertyCollection propertyCollection) {
		this.appW = appW;
		this.localization = appW.getLocalization();
		addStyleName("gridDistPanel");
		boolean correctProps = fillProperties(propertyCollection);
		if (correctProps) {
			buildGUI();
		}
	}

	private boolean fillProperties(GridDistancePropertyCollection propertyCollection) {
		Property[] properties = propertyCollection.getProperties();
		if (properties.length == 4
			&& properties[0] instanceof GridFixedDistanceProperty
			&& properties[1] instanceof NumericPropertyWithSuggestions
			&& properties[2] instanceof NumericPropertyWithSuggestions
			&& properties[3] instanceof NumericPropertyWithSuggestions) {
			gridDistProperty = (GridFixedDistanceProperty) properties[0];
			xGridDistProperty = (NumericPropertyWithSuggestions) properties[1];
			yGridDistProperty = (NumericPropertyWithSuggestions) properties[2];
			gridAngleProperty = (NumericPropertyWithSuggestions) properties[3];
			return true;
		}
		return false;
	}

	private void buildGUI() {
		ComponentCheckbox distCheckBox = new ComponentCheckbox(localization,
				gridDistProperty.getValue(), gridDistProperty.getName(),
				value -> {
					gridDistProperty.setValue(value);
					xDistComboBox.setDisabled(!value);
					yDistComboBox.setDisabled(!value);
					angleComboBox.setDisabled(!value);
				});
		add(distCheckBox);

		xDistComboBox = buildComboBox(xGridDistProperty, !gridDistProperty.getValue());
		yDistComboBox = buildComboBox(yGridDistProperty, !gridDistProperty.getValue());
		angleComboBox = buildComboBox(gridAngleProperty, !gridDistProperty.getValue());
		updateComboBoxes();
		appW.getActiveEuclidianView().getSettings().addListener(l -> updateComboBoxes());

		FlowPanel xyDistPanel = new FlowPanel();
		xyDistPanel.addStyleName("xyDistPanel");
		xyDistPanel.add(xDistComboBox);
		xyDistPanel.add(yDistComboBox);
		xyDistPanel.add(angleComboBox);
		add(xyDistPanel);
	}

	private ComponentComboBox buildComboBox(
			NumericPropertyWithSuggestions gridDistanceProperty, boolean disabled) {
		String defaultValue = gridDistanceProperty.getValue();
		ComponentComboBox comboBox = new ComponentComboBox(appW, gridDistanceProperty);
		comboBox.setValue(defaultValue);
		comboBox.addChangeHandler(() -> {
			gridDistanceProperty.setValue(comboBox.getSelectedText().trim());
		});
		if (disabled) {
			comboBox.setDisabled(true);
		}

		return comboBox;
	}

	private void updateComboBoxes() {
		boolean isPolar = gridAngleProperty.isEnabled();
		xDistComboBox.setLabel(isPolar ? "r" : "x");
		yDistComboBox.setVisible(!isPolar);
		angleComboBox.setVisible(isPolar);
	}
}
