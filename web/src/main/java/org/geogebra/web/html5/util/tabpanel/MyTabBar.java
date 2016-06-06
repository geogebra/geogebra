package org.geogebra.web.html5.util.tabpanel;

import org.geogebra.common.util.debug.Log;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
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

		tabPanel.deck.showWidget(selectedTab);
		SelectionEvent.fire(tabPanel, index);

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


	/*
	 * @deprecated Use {@link #addTab(String)} instead
	 */
	@Deprecated
	public void add(Widget w) {
		// do nothing
	}

	public void addTab(String label) {
		Tab tab = new Tab(label);
		tab.addStyleName("gwt-TabBarItem");
		super.add(tab);
	}

	private class Tab extends Label {
		Tab(String label) {
			super(label);
			sinkEvents(Event.ONCLICK);
		}


		@Override
		public void onBrowserEvent(Event event) {
			Log.debug("onBrowserEvent: " + event.getType());

			if (DOM.eventGetType(event) == Event.ONCLICK) {
				selectTabByTabWidget(this);
			}
			super.onBrowserEvent(event);
		}
	}

	void selectTabByTabWidget(Widget tabWidget) {
		int numTabs = getWidgetCount();

		for (int i = 1; i < numTabs; ++i) {
			Log.debug(i + ": " + getWidget(i).getElement().toString());
			if (getWidget(i) == tabWidget) {
				selectTab(i);
				return;
			}
		}
	}

}
