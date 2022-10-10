package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Adds the scientific header to AV panel.
 */
public final class ScientificDockPanelDecorator implements DockPanelDecorator {

	private ScrollPanel algebraScrollPanel;

	@Override
	public Panel decorate(ScrollPanel algebraPanel, AppW appW) {
		this.algebraScrollPanel = algebraPanel;
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
}