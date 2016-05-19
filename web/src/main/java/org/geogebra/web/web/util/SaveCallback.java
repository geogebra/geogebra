package org.geogebra.web.web.util;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;

/**
 * @author geogebra
 *
 */
public class SaveCallback {

	private final AppW app;
	private SaveState state;

	public enum SaveState {
		OK, FORKED, ERROR
	};

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public SaveCallback(final AppW app, SaveState state) {
		this.app = app;
		this.state = state;
	}

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public static void onSaved(AppW app, SaveState state, boolean isMacro) {
		if (!isMacro) {
			app.setSaved();
			if (app.getActiveMaterial() != null
					&& !app.getActiveMaterial().getVisibility().equals("P")) {
				String msg = app.getMenu("SavedSuccessfully");
				if (state == state.FORKED) {
					msg += "<br/>";
					msg += app.getLocalization().getPlain("SeveralVersionsOf",
							app.getKernel().getConstruction().getTitle());
				}
				ToolTipManagerW.sharedInstance().setBlockToolTip(false);
				ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
						"<p style='margin-top: 13px; margin-bottom: 0px'>"
								+ msg + "</p>",
						app.getActiveMaterial().getURL(),
						ToolTipLinkType.ViewSavedFile, app,
						app.getAppletFrame().isKeyboardShowing());
			} else {
				ToolTipManagerW.sharedInstance().showBottomMessage(
						app.getMenu("SavedSuccessfully"), true, app);
			}
		} else {
			ToolTipManagerW.sharedInstance().showBottomMessage(
					app.getMenu("SavedSuccessfully"), true, app);
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
		if (mat.getType().equals(MaterialType.ggb)) {
			app.setActiveMaterial(mat);
			onSaved(app, state, false);
			if (((GuiManagerW) app.getGuiManager()).browseGUIwasLoaded()) {
				app.getGuiManager().getBrowseView()
						.refreshMaterial(mat, isLocal);
			}
		} else {
			onSaved(app, state, true);
		}
	}

	/**
	 * shows errorMessage "save file failed"
	 */
	public void onError() {
		app.showError(app.getLocalization().getError("SaveFileFailed"));
	}
}
