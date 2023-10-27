package org.geogebra.web.shared.components;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class ComponentOrDivider extends FlowPanel {

	/**
	 * component for ------- OR -------- divider
	 * @param or string on the middle
	 */
	public ComponentOrDivider(String or) {
		this.addStyleName("orDividerComponent");
		FlowPanel leftLine = new FlowPanel();
		leftLine.setStyleName("divider");

		Label orLbl = BaseWidgetFactory.INSTANCE.newDisabledText(or, "orLbl");

		FlowPanel rightLine = new FlowPanel();
		rightLine.setStyleName("divider");

		this.add(leftLine);
		this.add(orLbl);
		this.add(rightLine);
	}
}
