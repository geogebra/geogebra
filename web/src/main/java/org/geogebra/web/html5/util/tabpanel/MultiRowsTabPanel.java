package org.geogebra.web.html5.util.tabpanel;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;


public class MultiRowsTabPanel extends Composite implements TabPanelInterface {

	MultiRowsTabBar tabBar;
	DeckPanel deck = new DeckPanel();

	public MultiRowsTabPanel() {
		tabBar = new MultiRowsTabBar(this);
		tabBar.addStyleName("gwt-TabBar");
		tabBar.addStyleName("ggb-MultiRowsTabPanel");
		FlowPanel panel = new FlowPanel();
		panel.add(tabBar);
		panel.add(deck);
		// panel.setCellHeight(deck, "100%");
		// tabBar.setWidth("100%");
		initWidget(panel);
	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<Integer> selectionHandler) {
		return addHandler(selectionHandler, SelectionEvent.getType());
	}

	public void selectTab(int index) {
		tabBar.selectTab(index);
	}

	private int getTabCount() {
		return tabBar.getWidgetCount();
	}

	public MultiRowsTabBar getTabBar() {
		return tabBar;
	}

	public void add(Widget w, String tabText) {
		tabBar.addTab(tabText);
		w.addStyleName("gwt-TabPanelBottom");
		deck.add(w);
	}

	public int getWidgetIndex(Widget widget) {
		return deck.getWidgetIndex(widget);
	}

	public int getWidgetCount() {
		deck.getWidgetCount();
		return 0;
	}

	public Widget getWidget(int i) {
		return deck.getWidget(i);
	}

}
