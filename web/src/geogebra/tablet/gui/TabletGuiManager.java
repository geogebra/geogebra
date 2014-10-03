package geogebra.tablet.gui;

import geogebra.html5.main.AppW;
import geogebra.tablet.gui.browser.TabletBrowseGUI;
import geogebra.touch.gui.GuiManagerT;
import geogebra.web.gui.browser.BrowseGUI;

public class TabletGuiManager extends GuiManagerT {

	public TabletGuiManager(final AppW app) {
	    super(app);
    }

	/**
	 * @return {@link TabletBrowseGUI}
	 */
	@Override
	public BrowseGUI getBrowseGUI() {
		if (this.browseGUI == null) {
			this.browseGUI = new TabletBrowseGUI((AppW)this.app);
			this.browseGUI.loadAllMaterials();
		}
		return this.browseGUI;
	}
}
