package geogebra.tablet.gui;

import geogebra.html5.main.AppW;
import geogebra.tablet.gui.browser.TabletBrowseGUI;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.browser.BrowseGUI;

public class TabletGuiManager extends GuiManagerW {

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
		}
		return this.browseGUI;
	}
//	
//	@Override
//	public void save(SaveCallback cb) {
//		if (((AppW) app).getNetworkOperation().getOnline() || "tablet".equals(GWT.getModuleName()) || "phone".equals(GWT.getModuleName())) {
//			SaveDialogW saveDialog = ((DialogManagerW) app.getDialogManager()).getSaveDialog();
//			saveDialog.center();
//			saveDialog.setCallback(cb);
//		} else {
//			((DialogManagerW) app.getDialogManager()).getSaveDialog().openFilePicker();
//		}
//	}
	
}
