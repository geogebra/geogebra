package org.geogebra.web.full.gui.layout;

import org.geogebra.web.full.gui.toolbarpanel.MenuToggleButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Adds the scientific header to AV panel.
 */
public final class ScientificDockPanelDecorator implements DockPanelDecorator {
	private FlowPanel header;
	private AppW app;
	private ScrollPanel algebraPanel;

	@Override
	public Panel decorate(ScrollPanel algebrap, AppW appW) {
		this.app = appW;
		this.algebraPanel = algebrap;
		FlowPanel vp = new FlowPanel();
		vp.setHeight("100%");
		header = new FlowPanel();
		MenuToggleButton menuBtn = new MenuToggleButton(app);
		menuBtn.addStyleName("flatButtonHeader");
		menuBtn.addStyleName("menuBtnScientific");
		header.add(menuBtn);
		header.setStyleName("algebraHeaderScientific");
		vp.add(header);
		vp.add(algebrap);
		return vp;
	}

	@Override
	public void onResize() {
		boolean smallScreen = AppW.smallScreen(app.getArticleElement());
		header.setVisible(smallScreen);
		algebraPanel.getElement().getStyle().setProperty("height",
				smallScreen ? "calc(100% - 56px)" : "100%");
	}
}