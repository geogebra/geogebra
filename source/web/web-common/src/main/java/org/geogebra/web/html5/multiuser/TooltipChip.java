/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.multiuser;

import org.geogebra.common.awt.GColor;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.Label;

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

	/**
	 * Hide the chip.
	 */
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
		style.setLeft(x + LEFT_MARGIN, Unit.PX);
		style.setTop(y - (getOffsetHeight() / 2d), Unit.PX);
	}
}
