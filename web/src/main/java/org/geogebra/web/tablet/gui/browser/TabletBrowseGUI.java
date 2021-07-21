package org.geogebra.web.tablet.gui.browser;

import org.geogebra.web.full.gui.browser.BrowseGUI;
import org.geogebra.web.html5.main.AppW;

/**
 * Browse UI for native tablet apps
 */
public class TabletBrowseGUI extends BrowseGUI {

	/**
	 * @param app
	 *            application
	 */
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
}
