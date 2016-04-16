package org.geogebra.web.html5.gui.util;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

public class LayoutUtilW {

	public static FlowPanel panelRow(IsWidget... widgets) {
		FlowPanel p = new FlowPanel();
		for (IsWidget widget : widgets) {
			p.add(widget);
		}
		p.setStyleName("panelRow");

		return p;
	}

	public static FlowPanel panelRowVertical(IsWidget... widgets) {
		FlowPanel p = new FlowPanel();
		for (IsWidget widget : widgets) {
			p.add(widget);
		}
		p.setStyleName("panelRow rows");

		return p;
	}

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
}
