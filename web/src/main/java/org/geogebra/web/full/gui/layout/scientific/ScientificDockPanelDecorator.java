package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.toolbarpanel.tableview.StickyValuesTable;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.util.CustomScrollbar;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Adds the scientific header to AV panel.
 */
public final class ScientificDockPanelDecorator implements DockPanelDecorator {

	private ScrollPanel algebraScrollPanel;

	@Override
	public Panel decorate(Panel wrapper, AppW appW) {
		algebraScrollPanel = new ScrollPanel();
		algebraScrollPanel.setWidth("100%");
		algebraScrollPanel.add(wrapper);
		algebraScrollPanel.addStyleName("algebraPanel");
		CustomScrollbar.apply(algebraScrollPanel);
		return buildAndStylePanel(appW);
	}

	private Panel buildAndStylePanel(AppW app) {
		FlowPanel panel = new FlowPanel();
		stylePanel(panel);
		panel.add(algebraScrollPanel);
		algebraScrollPanel.addStyleName("algebraPanelScientific");

		ScientificScrollHandler scrollController = new ScientificScrollHandler(
				app, panel);
		panel.addDomHandler(scrollController, MouseDownEvent.getType());
		panel.addBitlessDomHandler(scrollController, TouchStartEvent.getType());
		return panel;
	}

	private static void stylePanel(Panel panel) {
		panel.setHeight("100%");
	}

	@Override
	public void onResize(AlgebraViewW aView, int offsetHeight) {
		boolean smallScreen = aView.getApp().getAppletFrame()
				.shouldHaveSmallScreenLayout();
		Dom.toggleClass(algebraScrollPanel, "algebraPanelScientificSmallScreen",
				"panelScientificDefaults", smallScreen);
	}

	@Override
	public void decorateTableTab(Widget tab, StickyTable<?> table) {
		tab.addStyleName("panelScientificDefaults");
		disableShadedColumns((StickyValuesTable) table);
		table.addStyleName("scientific");
	}

	@Override
	public int getTabHeight(int tabHeight) {
		return tabHeight - 40;
	}

	private void disableShadedColumns(StickyValuesTable table) {
		table.disableShadedColumns();
	}
}