package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.toolbarpanel.LogoAndName;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Adds the scientific header to AV panel.
 */
public final class ScientificDockPanelDecorator implements DockPanelDecorator {

	private static final int TOP_MARGIN = 40;
	private ScrollPanel algebraScrollPanel;
	private LogoAndName logo;

	@Override
	public Panel decorate(ScrollPanel algebraPanel, AppW appW) {
		this.algebraScrollPanel = algebraPanel;
		return buildAndStylePanel(appW);
	}

	@Override
	public void addLogo(FlowPanel wrapper, AppW app) {
		logo = new LogoAndName(app);
		wrapper.add(logo);
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
				"algebraPanelScientificDefaults", smallScreen);
		Dom.toggleClass(logo.asWidget(), "avNameLogoScientific", !smallScreen);
		if (offsetHeight > 0) {
			int margin = smallScreen ? 8 : TOP_MARGIN;
			Scheduler.get().scheduleDeferred(() ->
					logo.onResize(aView, offsetHeight - margin));
		}
	}

	@Override
	public void setLabels() {
		if (logo != null) {
			logo.setLabels();
		}
	}
}