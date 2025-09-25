package org.geogebra.web.full.gui.properties.ui.panel;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.impl.graphics.Dimension3DPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.DimensionMinMaxProperty;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.properties.ui.PropertiesPanelAdapter;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class Dimension3DPanel extends FlowPanel implements SetLabels {
	private final AppW appW;
	private Label label;
	private String labelTransKey;
	private final List<ComponentInputField> inputFieldList = new ArrayList<>();

	/**
	 * Dimension panel holding min and max values for x/y/z
	 * @param appW application
	 * @param propertiesPanelAdapter {@link PropertiesPanelAdapter}
	 * @param propertiesCollection {@link Dimension3DPropertiesCollection}
	 */
	public Dimension3DPanel(AppW appW, PropertiesPanelAdapter propertiesPanelAdapter,
			Dimension3DPropertiesCollection propertiesCollection) {
		this.appW = appW;
		buildGui(propertiesPanelAdapter, propertiesCollection);
	}

	private void buildGui(PropertiesPanelAdapter propertiesPanelAdapter,
			Dimension3DPropertiesCollection propertiesCollection) {
		labelTransKey = propertiesCollection.getRawName();
		label = new Label(appW.getLocalization().getMenu(labelTransKey));
		add(label);

		FlowPanel minMaxPanel = new FlowPanel();
		minMaxPanel.addStyleName("minMaxPanel");
		for (DimensionMinMaxProperty property : propertiesCollection.getProperties()) {
			Widget widget = propertiesPanelAdapter.getWidget(property);
			inputFieldList.add((ComponentInputField) widget);
			widget.addStyleName("minMaxItem");
			minMaxPanel.add(widget);
		}
		add(minMaxPanel);
	}

	@Override
	public void setLabels() {
		label.setText(appW.getLocalization().getMenu(labelTransKey));
		inputFieldList.forEach(SetLabels::setLabels);
	}
}
