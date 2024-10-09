package org.geogebra.web.full.util;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.html5.gui.tooltip.ComponentSnackbar;
import org.geogebra.web.html5.gui.tooltip.ToolTip;
import org.geogebra.web.html5.main.AppW;

/**
 * @author geogebra
 *
 */
public class SaveCallback {

	private final AppW app;
	private SaveState state;

	/** possible saving outcomes */
	public enum SaveState {
		/** online saving OK */
		OK,
		/** online saving caused a fork */
		FORKED,
		/** online saving failed */
		ERROR
	}

	/**
	 * @param app
	 *            {@link AppW}
	 * @param state
	 *            online saving state
	 */
	public SaveCallback(final AppW app, SaveState state) {
		this.app = app;
		this.state = state;
	}

	/**
	 * @param app
	 *            {@link AppW}
	 * @param state
	 *            online saving state
	 * @param isMacro
	 *            whether this is for GGT file
	 */
	public static void onSaved(AppW app, SaveState state, boolean isMacro) {
		Localization loc = app.getLocalization();
		if (!isMacro) {
			app.setSaved();
			String msg = state == SaveState.ERROR
					? app.getLocalization().getMenu("SaveAccountFailed")
					: loc.getMenu("SavedSuccessfully");
			Material activeMaterial = app.getActiveMaterial();
			if (activeMaterial != null
					&& !activeMaterial.getVisibility().equals("P")
					&& state != SaveState.ERROR) {
				if (state == SaveState.FORKED) {
					msg += loc.getPlain("SeveralVersionsOf",
							app.getKernel().getConstruction().getTitle());
				}
				app.getToolTipManager().setBlockToolTip(false);
				ToolTip toolTip = new ToolTip(msg, null, "Share",
						activeMaterial.getURL());
				app.getToolTipManager().showBottomInfoToolTip(toolTip, app,
						ComponentSnackbar.DEFAULT_TOOLTIP_DURATION);
			} else {
				app.getToolTipManager().showBottomMessage(
						msg, app);
			}
		} else {
			app.getToolTipManager().showBottomMessage(
					loc.getMenu("SavedSuccessfully"), app);
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
		if (mat.getType().equals(MaterialType.ggb)
				|| mat.getType().equals(MaterialType.ggs)) {
			app.setActiveMaterial(mat);
			onSaved(app, state, false);
			if (((GuiManagerW) app.getGuiManager()).isOpenFileViewLoaded()) {
				if (!isLocal) {
					mat.setSyncStamp(mat.getModified());
				}
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
		if (state == SaveState.OK) {
			app.getGgbApi().showTooltip(
					app.getLocalization().getMenu("SavedToAccountSuccessfully")
							+ "\n" + app.getLocalization()
									.getMenu("SaveLocalCopyFailed"));
		} else {
			app.showError(Errors.SaveFileFailed);
		}

	}

	/**
	 * @return state of saving
	 */
	public SaveState getState() {
		return state;
	}
}
