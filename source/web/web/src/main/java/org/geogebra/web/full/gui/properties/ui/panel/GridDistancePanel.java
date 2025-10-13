package org.geogebra.web.full.gui.properties.ui.panel;

import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.graphics.GridDistancePropertyCollection;
import org.geogebra.web.full.gui.properties.ui.PropertiesPanelAdapter;
import org.gwtproject.user.client.ui.FlowPanel;

public class GridDistancePanel extends FlowPanel {

	/**
	 * Creates the panel with fixed distance check-box and x/y distance text field
	 * based on {@link GridDistancePropertyCollection}
	 * @param propertyCollection see {@link GridDistancePropertyCollection}
	 */
	public GridDistancePanel(GridDistancePropertyCollection propertyCollection,
			PropertiesPanelAdapter adapter) {
		addStyleName("gridDistPanel");
		buildGUI(adapter, propertyCollection.getProperties());
	}

	private void buildGUI(PropertiesPanelAdapter adapter, Property[] properties) {
		add(adapter.getWidget(properties[0]));
		FlowPanel xyDistPanel = new FlowPanel();
		xyDistPanel.addStyleName("xyDistPanel");
		for (int i = 1; i < properties.length; i++) {
			xyDistPanel.add(adapter.getWidget(properties[i]));
		}
		add(xyDistPanel);
	}
}
