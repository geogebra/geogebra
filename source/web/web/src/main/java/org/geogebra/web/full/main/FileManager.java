/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.main;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MaterialsManager;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.UserPublic;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.FileSystemFileHandle;
import org.geogebra.web.full.util.SaveCallback;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.bridge.GeoGebraJSNativeBridge;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.main.AppW;

import elemental2.dom.DomGlobal;

/**
 * Manager for local file saving
 *
 */
public abstract class FileManager extends MaterialsManager {
	/** application */
	protected AppW app;
	private Material.Provider provider = Material.Provider.TUBE;
	private FileSystemFileHandle fileHandle;

	/**
	 * @param app
	 *            application
	 */
	public FileManager(final AppW app) {
		this.app = app;
	}

	@Override
	public abstract void delete(Material mat, boolean permanent, Runnable onSuccess);

	/**
	 * 
	 * @param base64
	 *            only a hint, we can send null and it will be resolved
	 * @param modified
	 *            modification timestamp
	 * @param cb
	 *            callback
	 */
	public abstract void saveFile(String base64, long modified, SaveCallback cb);

	/**
	 * Overwritten for phone
	 * 
	 * @param material
	 *            {@link Material}
	 */
	public void removeFile(final Material material) {
		app.getGuiManager().getBrowseView().removeMaterial(material);
	}

	/**
	 * Overwritten for phone
	 * 
	 * @param material
	 *            {@link Material}
	 */
	public void addMaterial(final Material material) {
		app.getGuiManager().getBrowseView().addMaterial(material);
	}

	/**
	 * @param base64
	 *            material base64
	 * @param modified
	 *            timestamp
	 * @return material
	 */
	public Material createMaterial(final String base64, long modified) {
		final Material mat = new Material(MaterialType.ggb);

		// TODO check if we need to set timestamp / modified
		mat.setModified(modified);

		if (!StringUtil.emptyOrZero(app.getTubeId())) {
			mat.setSharingKey(app.getTubeId());
			Log.debug("create material" + app.getSyncStamp());
			mat.setSyncStamp(app.getSyncStamp());
		}
		mat.setBase64(base64);
		mat.setTitle(app.getKernel().getConstruction().getTitle());
		mat.setDescription(app.getKernel().getConstruction()
				.getWorksheetText(0));
		mat.setThumbnailBase64(((EuclidianViewWInterface) app
				.getActiveEuclidianView())
				.getCanvasBase64WithTypeString());
		if (app.getLoginOperation() != null) {
			UserPublic user = new UserPublic(app.getLoginOperation().getModel().getUserId(),
					app.getLoginOperation().getUserName());
			mat.setCreator(user);
		}
		Material activeMaterial = app.getActiveMaterial();
		if (activeMaterial != null) {
			mat.setSharingKey(activeMaterial.getSharingKey());
			mat.setVisibility(activeMaterial.getVisibility());
			mat.setURL(activeMaterial.getURL());
		}
		return mat;
	}

	@Override
	protected final void showTooltip(Material mat) {
		app.getToolTipManager().showBottomMessage(app.getLocalization()
				.getPlain("SeveralVersionsOfA", mat.getTitle()), app);

	}

	/**
	 * Refresh material in browse view
	 * 
	 * @param newMat
	 *            uploaded material
	 */
	@Override
	protected void refreshMaterial(Material newMat) {
		app.getGuiManager().getBrowseView().refreshMaterial(newMat, false);

	}

	@Override
	public boolean shouldKeep(int id) {
		return true;
	}

	@Override
	public void setFileProvider(Material.Provider provider) {
		this.provider = provider;
	}

	@Override
	public Material.Provider getFileProvider() {
		return this.provider;
	}

	@Override
	public void openMaterial(final Material material) {
		try {
			final String base64 = material.getBase64();
			if (base64 == null) {
				return;
			}
			app.getGgbApi().setBase64(base64);
		} catch (final Throwable t) {
			app.showError(Errors.LoadFileFailed);
			Log.debug(t);
		}
	}

	/**
	 * only for FileManagerT and FileManagerW
	 * 
	 * @return {@link AppW}
	 */
	@Override
	public AppW getApp() {
		return this.app;
	}

	@Override
	public final boolean save(App app1) {
		if (this.saveCurrentLocalIfPossible(app, () -> {})) {
			return true;
		}
		AppW appw = (AppW) app1;

		if (!isOnlineSavingPreferred()) {
			// not logged in and can't log in
			app.getSaveController().showLocalSaveDialog(() -> {});
		} else if (!appw.getLoginOperation().isLoggedIn()) {
			// not logged in and possible to log in
			appw.getGuiManager().listenToLogin(appw.getDialogManager()::showSaveDialog);
			((AppWFull) app).getActivity().markSaveOpen();
			appw.getLoginOperation().showLoginDialog();
		} else {
			// logged in
			appw.getDialogManager().showSaveDialog();
		}
		return true;
	}

	@Override
	public void nativeShare(String base64, String title) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().share(base64, title, "ggb");
		}
	}

	@Override
	public void open(String url) {
		open(url, "");
	}

	@Override
	public void open(String url, String features) {
		DomGlobal.window.open(url, "_blank", features);
	}

	/**
	 * export image (or other filetype) either as a browser download, share
	 * intent iOS Safari: open in new tab (no download possible)
	 */
	@Override
	final public void exportImage(String url, String filename,
			String extension) {
		Browser.exportImage(url, filename);
	}

	/**
	 * Shows error tooltip when saving online fails.
	 * @param appw app
	 */
	public void showOfflineErrorTooltip(AppW appw) {
		if (!appw.getNetworkOperation().isOnline()) {
			app.getToolTipManager().showBottomMessage(appw
					.getLocalization()
					.getMenu("phone_loading_materials_offline"), appw);
		} else if (!appw.getLoginOperation().isLoggedIn()) {
			app.getToolTipManager().showBottomMessage(appw
					.getLocalization()
					.getMenu("SaveAccountFailed"), appw);
		}
	}

	private void setFileHandle(FileSystemFileHandle handle) {
		this.fileHandle = handle;
		this.provider = Material.Provider.LOCAL;
		handle.getFile().then(file -> {
			app.getKernel().getConstruction().setTitle(
					StringUtil.removeFolderName(StringUtil.removeFileExtension(file.name)));
			return null;
		});
	}

	/**
	 * Save file using a file handle
	 * @param handle target handle
	 * @param callback to run after file written
	 */
	public void saveAs(FileSystemFileHandle handle, Runnable callback) {
		setFileHandle(handle);
		handle.createWritable().then(stream -> {
			app.getGgbApi().getZippedGgbAsync(true, blob -> {
				stream.write(blob);
				stream.close();
				callback.run();
				String msg = app.getLocalization().getMenu("SavedSuccessfully");
				app.getToolTipManager().showBottomMessage(msg, app);
			});
			return null;
		});
	}

	@Override
	public boolean saveCurrentLocalIfPossible(App app, Runnable callback) {
		if (fileHandle != null) {
			saveAs(fileHandle, callback);
			return true;
		}
		return false;
	}

	/**
	 * Reset handle for local saving.
	 */
	public void resetFileHandle() {
		fileHandle = null;
	}

	public boolean isOfflinePlatform() {
		return app.getPlatform() == GeoGebraConstants.Platform.OFFLINE;
	}
}
