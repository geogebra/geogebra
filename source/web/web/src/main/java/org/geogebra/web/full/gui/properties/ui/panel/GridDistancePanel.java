package org.geogebra.web.full.gui.properties.ui.panel;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.graphics.AxisDistanceProperty;
import org.geogebra.common.properties.impl.graphics.GridDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GridFixedDistanceProperty;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;

public class GridDistancePanel extends FlowPanel {
	private final AppW appW;
	private final Localization localization;
	private GridFixedDistanceProperty gridDistProperty;
	private AxisDistanceProperty xAxisDistProperty;
	private AxisDistanceProperty yAxisDistProperty;
	private ComponentInputField xDistTextField;
	private ComponentInputField yDistTextField;

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
		if (properties.length == 3
			&& properties[0] instanceof GridFixedDistanceProperty
			&& properties[1] instanceof AxisDistanceProperty
			&& properties[2] instanceof AxisDistanceProperty) {
			gridDistProperty = (GridFixedDistanceProperty) properties[0];
			xAxisDistProperty = (AxisDistanceProperty) properties[1];
			yAxisDistProperty = (AxisDistanceProperty) properties[2];
			return true;
		}
		return false;
	}

	private void buildGUI() {
		ComponentCheckbox distCheckBox = new ComponentCheckbox(localization,
				gridDistProperty.getValue(), gridDistProperty.getName(),
				value -> {
					gridDistProperty.setValue(value);
					xDistTextField.setDisabled(!value);
					yDistTextField.setDisabled(!value);
				});

		xDistTextField = buildInputField(xAxisDistProperty,
				!gridDistProperty.getValue());
		yDistTextField = buildInputField(yAxisDistProperty,
				!gridDistProperty.getValue());

		FlowPanel xyDistPanel = new FlowPanel();
		xyDistPanel.addStyleName("xyDistPanel");
		xyDistPanel.add(xDistTextField);
		xyDistPanel.add(yDistTextField);

		add(distCheckBox);
		add(xyDistPanel);
	}

	private ComponentInputField buildInputField(AxisDistanceProperty axisDistanceProperty,
			boolean disabled) {
		String defaultValue = axisDistanceProperty.getValue().isEmpty() ? "1"
				: axisDistanceProperty.getValue();
		ComponentInputField inputField = new ComponentInputField(appW, "",
				axisDistanceProperty.getName(), "", defaultValue);
		inputField.getTextField().getTextComponent().addBlurHandler(
				evt -> axisDistanceProperty.setValue(inputField.getText()));
		if (disabled) {
			inputField.setDisabled(true);
		}

		return inputField;
	}
}
