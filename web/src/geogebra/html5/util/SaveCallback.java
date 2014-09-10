package geogebra.html5.util;

import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;

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
	 */
	public void onSaved() {
		ToolTipManagerW.sharedInstance().showBottomMessage(app.getMenu("SavedSuccessfully"), true);
		app.setSaved();
	}

	/**
	 * shows tooltip "save file failed"
	 */
	public void onError() {
		ToolTipManagerW.sharedInstance().showBottomMessage(app.getLocalization().getError("SaveFileFailed"), true);
    }
}
