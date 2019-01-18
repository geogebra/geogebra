package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Adds the scientific header to AV panel.
 */
public final class ScientificDockPanelDecorator implements DockPanelDecorator {

	/**
	 * Estimated scrollbar width in desktop browsers; can overestimate a bit
	 */
	protected static final int SCROLLBAR_WIDTH = 20;
	private AppW app;
	private Panel header;
	private ScrollPanel algebraPanel;

	@Override
	public Panel decorate(ScrollPanel algebraPanel, AppW appW) {
		this.app = appW;
		this.algebraPanel = algebraPanel;

		return buildAndStylePanel();
	}

	private Panel buildAndStylePanel() {
		final FlowPanel panel = new FlowPanel();
		stylePanel(panel);
		buildHeaderAndAddToPanel(panel);
		panel.add(algebraPanel);
		panel.addDomHandler(new MouseDownHandler() {

			public void onMouseDown(MouseDownEvent event) {
				if (event.getClientX() > panel.getOffsetWidth()
						- SCROLLBAR_WIDTH) {
				event.stopPropagation();
				}

			}
		}, MouseDownEvent.getType());
		return panel;
	}

	private void stylePanel(Panel panel) {
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
		boolean smallScreen = AppW.smallScreen(app.getArticleElement());
		header.setVisible(smallScreen);
		Dom.toggleClass(algebraPanel, "algebraPanelScientificWithHeader",
				"algebraPanelScientificNohHeader", smallScreen);
	}
}