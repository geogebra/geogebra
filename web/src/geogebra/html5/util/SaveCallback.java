package geogebra.html5.util;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.GuiManagerW;

/**
 * @author geogebra
 *
 */
public class SaveCallback {
	
	private AppW app;
	
	/**
	 * @param app {@link AppW}
	 */
	public SaveCallback(AppW app) {
	    this.app = app;
    }

	/**
	 * shows info to user and sets app saved
	 * @param mat 
	 */
	public void onSaved(Material mat) {
		ToolTipManagerW.sharedInstance().showBottomMessage(app.getMenu("SavedSuccessfully"), true);
		app.setSaved();
		if (((GuiManagerW) app.getGuiManager()).browseGUIwasLoaded()) {
			((GuiManagerW) app.getGuiManager()).getBrowseGUI().refreshMaterial(mat, false);
		}
	}

	/**
	 * shows tooltip "save file failed"
	 */
	public void onError() {
		ToolTipManagerW.sharedInstance().showBottomMessage(app.getLocalization().getError("SaveFileFailed"), true);
    }
}
