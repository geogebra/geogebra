package org.geogebra.web.shared.components;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ComponentOrDivider extends FlowPanel {

	/**
	 * component for ------- OR -------- divider
	 * @param or string on the middle
	 */
	public ComponentOrDivider(String or) {
		this.addStyleName("orDividerComponent");
		FlowPanel leftLine = new FlowPanel();
		leftLine.setStyleName("divider");

		Label orLbl = new Label(or);
		orLbl.setStyleName("orLbl");

		FlowPanel rightLine = new FlowPanel();
		rightLine.setStyleName("divider");

		this.add(leftLine);
		this.add(orLbl);
		this.add(rightLine);
	}
}
