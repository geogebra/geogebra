package org.geogebra.web.html5.gui.util;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

public class LayoutUtil {

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
}
