package org.geogebra.web.html5.multiuser;

import org.geogebra.common.awt.GColor;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Label;

public class TooltipChip extends Label {
	public static final int LEFT_MARGIN = 8;

	/**
	 * Create a tooltip showing user interaction
	 * @param user name of the user to be shown
	 * @param color background color of the tooltip
	 */
	public TooltipChip(String user, GColor color) {
		addStyleName("tooltipChip");
		setText(user);
		Style style = getElement().getStyle();
		style.setBackgroundColor(color.toString());
	}

	public void hide() {
		getElement().addClassName("invisible");
	}

	/**
	 * Show the tooltip at the given coordinates
	 * @param x x pixel coordinate
	 * @param y y pixel coordinate
	 */
	public void show(double x, double y) {
		getElement().removeClassName("invisible");
		Style style = getElement().getStyle();
		style.setLeft(x + LEFT_MARGIN, Style.Unit.PX);
		style.setTop(y - (getOffsetHeight() / 2d), Style.Unit.PX);
	}
}
