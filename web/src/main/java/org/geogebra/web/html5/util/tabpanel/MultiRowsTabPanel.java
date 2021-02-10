package org.geogebra.web.html5.util.tabpanel;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class MultiRowsTabPanel extends Composite
		implements HasSelectionHandlers<Integer> {

	MultiRowsTabBar tabBar;
	DeckPanel deck = new DeckPanel();

	/**
	 * Create new tab panel.
	 */
	public MultiRowsTabPanel() {
		tabBar = new MultiRowsTabBar(this);
		tabBar.addStyleName("gwt-TabBar");
		tabBar.addStyleName("ggb-MultiRowsTabPanel");
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("propertiesPanel");
		panel.add(tabBar);
		deck.addStyleName("propView_deckPanel");
		panel.add(deck);
		// panel.setCellHeight(deck, "100%");
		// tabBar.setWidth("100%");
		initWidget(panel);
	}

	@Override
	public HandlerRegistration addSelectionHandler(
			SelectionHandler<Integer> selectionHandler) {
		return addHandler(selectionHandler, SelectionEvent.getType());
	}

	public void selectTab(int index) {
		tabBar.selectTab(index);
	}

	public MultiRowsTabBar getTabBar() {
		return tabBar;
	}

	/**
	 * @param w
	 *            tab content
	 * @param tabText
	 *            tab label
	 */
	public void add(Widget w, String tabText) {
		tabBar.addTab(tabText);
		w.addStyleName("gwt-TabPanelBottom");
		deck.add(w);
	}

	/**
	 * @param widget
	 *            tab
	 * @return tab index
	 */
	public int getWidgetIndex(Widget widget) {
		return deck.getWidgetIndex(widget);
	}

	/**
	 * @return number of tabs
	 */
	public int getWidgetCount() {
		return deck.getWidgetCount();
	}

	/**
	 * @param i
	 *            index
	 * @return tab for index
	 */
	public Widget getWidget(int i) {
		return deck.getWidget(i);
	}

}
