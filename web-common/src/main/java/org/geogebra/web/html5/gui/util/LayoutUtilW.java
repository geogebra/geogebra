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
	 * @param widgets
	 *            widgets
	 * @return widgets merged in a column
	 */
	public static FlowPanel panelRowVertical(IsWidget... widgets) {
		FlowPanel p = new FlowPanel();
		for (IsWidget widget : widgets) {
			p.add(widget);
		}
		p.setStyleName("panelRow rows");

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
