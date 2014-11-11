package geogebra.tablet.gui;

import geogebra.html5.main.AppW;
import geogebra.tablet.gui.browser.TabletBrowseGUI;
import geogebra.touch.gui.GuiManagerT;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.main.GDevice;

public class TabletGuiManager extends GuiManagerT {

	public TabletGuiManager(final AppW app, GDevice device) {
	    super(app, device);
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
