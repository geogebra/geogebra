package org.geogebra.web.full.gui.util;

import org.gwtproject.dom.client.Style.Unit;
import org.gwtproject.user.client.ui.SimplePanel;

public class VerticalSeparator extends SimplePanel {
	
	/**
	 * @param height
	 *            separator height in px
	 */
	public VerticalSeparator(int height) {
		setStyleName("VerticalSeparator");
		getElement().getStyle().setHeight(height, Unit.PX);
	}
}
