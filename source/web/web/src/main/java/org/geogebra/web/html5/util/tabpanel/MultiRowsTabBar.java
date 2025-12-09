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

package org.geogebra.web.html5.util.tabpanel;

import org.gwtproject.event.logical.shared.HasSelectionHandlers;
import org.gwtproject.event.logical.shared.SelectionEvent;
import org.gwtproject.event.logical.shared.SelectionHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.UIObject;
import org.gwtproject.user.client.ui.Widget;

/**
 * Top bar of a tab panel.
 */
public class MultiRowsTabBar extends FlowPanel implements
		HasSelectionHandlers<Integer> {

	private int selectedTab;
	private MultiRowsTabPanel tabPanel;

	/**
	 * @param tabPanel2
	 *            tab panel
	 */
	public MultiRowsTabBar(MultiRowsTabPanel tabPanel2) {
		tabPanel = tabPanel2;
	}

	@Override
	public HandlerRegistration addSelectionHandler(
			SelectionHandler<Integer> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	/**
	 * @param index
	 *            tab index
	 * @param tabText
	 *            tab label
	 */
	public void setTabText(int index, String tabText) {
		assert (index >= 0) && (index < getTabCount()) : "Tab index out of bounds";
		((Label) this.getWidget(index)).setText(tabText);
	}

	private int getTabCount() {
		return getWidgetCount();
	}

	/**
	 * @return selected tab index
	 */
	public int getSelectedTab() {
		return selectedTab;
	}

	/**
	 * Select given tab.
	 * 
	 * @param index
	 *            tab index
	 */
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

	private static void setSelectionStyle(Widget item, boolean selected) {
		if (item != null) {
			if (selected) {
				item.addStyleName("gwt-TabBarItem-selected");
				UIObject.setStyleName(DOM.getParent(item.getElement()),
						"gwt-TabBarItem-wrapper-selected", true);
			} else {
				item.removeStyleName("gwt-TabBarItem-selected");
				UIObject.setStyleName(DOM.getParent(item.getElement()),
						"gwt-TabBarItem-wrapper-selected", false);
			}
		}
	}

	/*
	 * @deprecated Use {@link #addTab(String)} instead
	 */
	@Override
	@Deprecated
	public void add(Widget w) {
		// do nothing
	}

	/**
	 * @param label
	 *            tab label
	 */
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
			if (DOM.eventGetType(event) == Event.ONCLICK) {
				selectTabByTabWidget(this);
			}
			super.onBrowserEvent(event);
		}
	}

	/**
	 * @param tabWidget
	 *            tab label widget
	 */
	void selectTabByTabWidget(Widget tabWidget) {
		int numTabs = getWidgetCount();

		for (int i = 0; i < numTabs; ++i) {
			if (getWidget(i) == tabWidget) {
				selectTab(i);
				return;
			}
		}
	}

	/**
	 * @param index
	 *            tab index
	 * @param enabled
	 *            whether to enable tab
	 */
	public void setTabEnabled(int index, boolean enabled) {
		assert (index >= 0) && (index < getTabCount()) : "Tab index out of bounds";
		UIObject.setStyleName(getWidget(index).getElement(), "gwt-TabBarItem-disabled",
				!enabled);
		UIObject.setStyleName(getWidget(index).getElement().getParentElement(),
				"gwt-TabBarItem-wrapper-disabled", !enabled);
	}

}
