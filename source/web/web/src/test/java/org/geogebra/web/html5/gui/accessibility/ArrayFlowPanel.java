package org.geogebra.web.html5.gui.accessibility;

import java.util.ArrayList;

import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Widget;

final class ArrayFlowPanel extends FlowPanel {
	private ArrayList<Widget> contents = new ArrayList<>();

	@Override
	public void clear() {
		contents.clear();
	}

	@Override
	public void insert(Widget widget, int pos) {
		contents.add(pos, widget);
	}

	@Override
	public void insert(IsWidget widget, int pos) {
		contents.add(pos, widget.asWidget());
	}

	@Override
	public int getWidgetCount() {
		return contents.size();
	}

	@Override
	public Widget getWidget(int i) {
		return contents.get(i);
	}
}