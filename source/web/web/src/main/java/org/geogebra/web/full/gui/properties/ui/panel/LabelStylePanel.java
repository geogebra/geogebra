package org.geogebra.web.full.gui.properties.ui.panel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.IconAssociatedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.graphics.LabelStylePropertyCollection;
import org.geogebra.web.full.gui.properties.ui.PropertiesPanelAdapter;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class LabelStylePanel extends FlowPanel implements SetLabels {
	private final PropertiesPanelAdapter propertiesPanelAdapter;
	private Label label;
	private Property property;

	/**
	 * Creates a panel with label style property
	 * @param labelStylePropertyCollection {@link LabelStylePropertyCollection}
	 * @param propertiesPanelAdapter {@link PropertiesPanelAdapter}
	 */
	public LabelStylePanel(LabelStylePropertyCollection labelStylePropertyCollection,
			PropertiesPanelAdapter propertiesPanelAdapter) {
		this.propertiesPanelAdapter = propertiesPanelAdapter;
		addStyleName("labelStyle");
		buildGUI(labelStylePropertyCollection);
	}

	private void buildGUI(LabelStylePropertyCollection labelStylePropertyCollection) {
		property = labelStylePropertyCollection;
		add(label = BaseWidgetFactory.INSTANCE.newPrimaryText(property.getName()));
		for (IconAssociatedProperty prop : labelStylePropertyCollection.getProperties()) {
			add(propertiesPanelAdapter.getWidget(prop));
		}
	}

	@Override
	public void setLabels() {
		label.setText(property.getName());
	}
}
