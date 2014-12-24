package geogebra.web.util;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import geogebra.html5.main.AppW;
import geogebra.web.gui.GuiManagerW;

/**
 * @author geogebra
 *
 */
public class SaveCallback {

	private final AppW app;

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public SaveCallback(final AppW app) {
		this.app = app;
	}

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public static void onSaved(AppW app) {
		app.setSaved();
		if (app.getActiveMaterial() != null
		        && !app.getActiveMaterial().getVisibility().equals("P")) {
			ToolTipManagerW.sharedInstance().setBlockToolTip(false);
			ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
			        "<p style='margin-top: 13px; margin-bottom: 0px'>"
			                + app.getMenu("SavedSuccessfully") + "</p>",
			        app.getActiveMaterial().getURL(),
			        ToolTipLinkType.ViewSavedFile, app);
		} else {
			ToolTipManagerW.sharedInstance().showBottomMessage(
			        app.getMenu("SavedSuccessfully"), true);
		}
	}

	/**
	 * shows info to user and sets app saved
	 * 
	 * @param mat
	 *            Material
	 * @param isLocal
	 *            boolean
	 */
	public void onSaved(final Material mat, final boolean isLocal) {
		app.setActiveMaterial(mat);
		onSaved(app);
		if (((GuiManagerW) app.getGuiManager()).browseGUIwasLoaded()) {
			((GuiManagerW) app.getGuiManager()).getBrowseGUI().refreshMaterial(
			        mat, isLocal);
		}
	}

	/**
	 * shows errorMessage "save file failed"
	 */
	public void onError() {
		app.showError(app.getLocalization().getError("SaveFileFailed"));
	}
}
