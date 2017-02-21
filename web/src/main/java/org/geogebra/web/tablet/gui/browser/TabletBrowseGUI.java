package org.geogebra.web.tablet.gui.browser;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.browser.BrowseGUI;

import com.google.gwt.user.client.Window;

public class TabletBrowseGUI extends BrowseGUI {

	public TabletBrowseGUI(final AppW app) {
		super(app, null);
	}

	@Override
	protected void addContent() {
		this.container.add(this.materialListPanel);
		this.setContentWidget(this.container);
	}

	@Override
	protected void initMaterialListPanel() {
		this.materialListPanel = new TabletMaterialListPanel(app);
		this.addResizeListener(this.materialListPanel);
	}

	@Override
	protected void updateViewSizes() {
		this.resizeTo(Window.getClientWidth(), Window.getClientHeight());

	}
}
