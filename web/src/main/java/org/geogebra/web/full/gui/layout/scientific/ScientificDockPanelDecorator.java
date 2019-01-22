package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Adds the scientific header to AV panel.
 */
public final class ScientificDockPanelDecorator implements DockPanelDecorator {

	private AppW app;
	private Panel header;
	private ScrollPanel algebraScrollPanel;

	@Override
	public Panel decorate(ScrollPanel algebraPanel, AppW appW) {
		this.app = appW;
		this.algebraScrollPanel = algebraPanel;

		return buildAndStylePanel();
	}

	private Panel buildAndStylePanel() {
		FlowPanel panel = new FlowPanel();
		stylePanel(panel);
		buildHeaderAndAddToPanel(panel);
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

	private void buildHeaderAndAddToPanel(Panel panel) {
		HeaderBuilder headerBuilder = new HeaderBuilder(app);
		header = headerBuilder
				.buildHeader();
		panel.add(header);
	}

	@Override
	public void onResize() {
		boolean smallScreen = app.isSmallScreen();
		header.setVisible(smallScreen);
		Dom.toggleClass(algebraScrollPanel, "algebraPanelScientificWithHeader",
				"algebraPanelScientificNoHeader", smallScreen);
	}
}