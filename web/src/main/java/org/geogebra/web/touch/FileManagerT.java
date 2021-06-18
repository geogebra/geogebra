package org.geogebra.web.touch;

import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.main.FileManager;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

public abstract class FileManagerT extends FileManager {

	/**
	 * @param app
	 *            application
	 */
	public FileManagerT(final AppW app) {
		super(app);
	}

	@Override
	public void rename(final String newTitle, final Material mat) {
		rename(newTitle, mat, null);
	}

	@Override
	public void autoSave(int counter) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getAutosaveJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void restoreAutoSavedFile(String json) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteAutoSavedFile() {
		// TODO Auto-generated method stub
	}

	@Override
	public void saveLoggedOut(App app1) {
		if (!Browser.isiOS()) {
			((DialogManagerW) app1.getDialogManager()).showSaveDialog();
		} else {
			showOfflineErrorTooltip((AppW) app1);
		}
	}

	/**
	 * @param m {@link Material}
	 */
	protected void doOpenMaterial(Material m) {
		super.openMaterial(m);
	}
	
	/**
	 * @param m {@link Material}
	 */
	protected void doUpload(Material m) {
		super.upload(m);
	}

	@Override
	public void export(final App app1) {
		((AppW) app1).getGgbApi().getBase64(true,
				s -> nativeShare(s, app1.getExportTitle()));
	}

	@Override
	public void showExportAsPictureDialog(String url, String filename,
			String extension, String titleKey, App appW) {

		exportImage(url, filename, extension);
		// TODO check if it really happened
		appW.dispatchEvent(
				new Event(EventType.EXPORT, null, "[\"" + extension + "\"]"));
	}

	@Override
	public boolean hasBase64(Material material) {
		return true;
	}

	@Override
	public void refreshAutosaveTimestamp() {
		// TODO Auto-generated method stub

	}
}