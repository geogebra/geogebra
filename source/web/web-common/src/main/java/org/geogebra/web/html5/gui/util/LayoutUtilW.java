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

package org.geogebra.web.html5.gui.util;

import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.IsWidget;

/**
 * Utility class for widget layout.
 */
public class LayoutUtilW {

	/**
	 * @param widgets
	 *            widgets
	 * @return widgets merged in a row
	 */
	public static FlowPanel panelRow(IsWidget... widgets) {
		FlowPanel p = new FlowPanel();
		for (IsWidget widget : widgets) {
			p.add(widget);
		}
		p.setStyleName("panelRow");

		return p;
	}

	/**
	 * Add widgets ito one row and add indentation CSS.
	 * 
	 * @param widgets
	 *            widgets
	 * @return widgets merged in a row
	 */
	public static FlowPanel panelRowIndent(IsWidget... widgets) {
		FlowPanel p = panelRow(widgets);
		p.setStyleName("panelRowIndent");

		return p;
	}

	/**
	 * Replaces widget old with w in p.
	 *
	 * @param p
	 *            The FlowPanel replace within.
	 * @param w
	 *            The new widget.
	 * @param old
	 *            The widget to be replaced.
	 * @return true if the replace was successful.
	 */
	public static boolean replace(FlowPanel p, IsWidget w, IsWidget old) {
		int idx = p.getWidgetIndex(old);
		if (w == null || idx == -1) {
			return false;
		}

		p.remove(idx);
		p.insert(w, idx);

		return true;
	}

	/**
	 * @param xscale
	 *            xscale
	 * @param yscale
	 *            yscale
	 * @return smaller scale
	 */
	public static double getDeviceScale(double xscale, double yscale,
			boolean allowUpscale) {
		if (xscale < 1 || yscale < 1 || !allowUpscale) {
			return Math.min(1d, Math.min(xscale, yscale));
		}
		return Math.max(1d, Math.min(xscale, yscale));
	}
}
