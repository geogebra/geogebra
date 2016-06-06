package org.geogebra.web.html5.util.tabpanel;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MyTabBar extends FlowPanel implements
		HasSelectionHandlers<Integer> {

	private int selectedTab;
	private MultiRowsTabPanel tabPanel;

	public MyTabBar(MultiRowsTabPanel tabPanel2) {
		tabPanel = tabPanel2;
	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<Integer> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	public void setTabText(int index, String tabText) {
		assert (index >= 0) && (index < getTabCount()) : "Tab index out of bounds";

		((Label) this.getWidget(index)).setText(tabText);
	}

	private int getTabCount() {
		return getWidgetCount();
	}

	public int getSelectedTab() {
		return selectedTab;
	}

	public void selectTab(int index) {
		if ((index < -1) || (index >= getTabCount())) {
			throw new IndexOutOfBoundsException();
		}

		setSelectionStyle(this.getWidget(selectedTab), false);
		selectedTab = index;
		setSelectionStyle(this.getWidget(selectedTab), true);

		//SelectionEvent.fire(this, index);
		SelectionEvent.fire(tabPanel,
				index);

	}

	private void setSelectionStyle(Widget item, boolean selected) {
		if (item != null) {
			if (selected) {
				item.addStyleName("gwt-TabBarItem-selected");
				setStyleName(DOM.getParent(item.getElement()),
						"gwt-TabBarItem-wrapper-selected", true);
			} else {
				item.removeStyleName("gwt-TabBarItem-selected");
				setStyleName(DOM.getParent(item.getElement()),
						"gwt-TabBarItem-wrapper-selected", false);
			}
		}
	}

	public void add(Widget w) {
		super.add(w);
		w.addStyleName("gwt-TabBarItem");
	}

}
