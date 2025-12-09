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
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.DeckPanel;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

public class MultiRowsTabPanel extends Composite
		implements HasSelectionHandlers<Integer> {

	MultiRowsTabBar tabBar;
	DeckPanel deck = new DeckPanel();

	public MultiRowsTabPanel() {
		this("propertiesPanel propView_deckPanel");
	}

	/**
	 * Create new tab panel.
	 */
	public MultiRowsTabPanel(String styleName) {
		tabBar = new MultiRowsTabBar(this);
		tabBar.addStyleName("gwt-TabBar");
		tabBar.addStyleName("ggb-MultiRowsTabPanel");
		FlowPanel panel = new FlowPanel();
		panel.addStyleName(styleName);
		panel.add(tabBar);
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

	/**
	 * Select given tab.
	 * @param index tab index
	 */
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
