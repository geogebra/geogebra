package org.geogebra.web.full.main;

import org.geogebra.common.main.App;
import org.geogebra.common.main.MaterialsManager;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.common.move.ggtapi.models.MaterialFilter;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.browser.BrowseGUI;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.util.SaveCallback;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.bridge.GeoGebraJSNativeBridge;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.Window;

/**
 * Manager for local file saving
 *
 */
public abstract class FileManager extends MaterialsManager {
	/** application */
	protected AppW app;
	private Provider provider = Provider.TUBE;

	/**
	 * @param app
	 *            application
	 */
	public FileManager(final AppW app) {
		this.app = app;
	}

	@Override
	public abstract void delete(final Material mat, boolean permanent,
	        Runnable onSuccess);

	/**
	 * 
	 * @param base64
	 *            only a hint, we can send null and it will be resolved
	 * @param modified
	 *            modification timestamp
	 * @param cb
	 *            callback
	 */
	public abstract void saveFile(String base64, long modified,
	        final SaveCallback cb);

	/**
	 * loads every file of the device depending on the {@link MaterialFilter
	 * filter} into the BrowseView.
	 *
	 * @param materialFilter
	 *            filter
	 */
	protected abstract void getFiles(MaterialFilter materialFilter);

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
		final Material mat = new Material(0, MaterialType.ggb);

		// TODO check if we need to set timestamp / modified
		mat.setModified(modified);

		if (!StringUtil.emptyOrZero(app.getTubeId())) {
			try {
				mat.setId(Integer.parseInt(app.getTubeId()));
			} catch (NumberFormatException e) {
				mat.setSharingKey(app.getTubeId());
			}
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
			mat.setAuthorId(app.getLoginOperation().getModel().getUserId());
			mat.setAuthor(app.getLoginOperation().getUserName());
		}
		Material activeMaterial = app.getActiveMaterial();
		if (activeMaterial != null) {
			mat.setSharingKey(activeMaterial.getSharingKey());
			mat.setVisibility(activeMaterial.getVisibility());
			mat.setURL(activeMaterial.getURL());
		}
		return mat;
	}

	/**
	 * @param query
	 *            String
	 */
	@Override
	public void search(final String query) {
		getFiles(MaterialFilter.getSearchFilter(query));
	}

	/**
	 * adds the files from the current user to the {@link BrowseGUI}
	 */
	@Override
	public void getUsersMaterials() {
		getFiles(MaterialFilter.getAppNameFilter(app.getConfig().getAppCode()));
	}

	@Override
	protected final void showTooltip(Material mat) {
		ToolTipManagerW.sharedInstance().showBottomMessage(app.getLocalization()
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
	public void setFileProvider(Provider provider) {
		this.provider = provider;
	}

	@Override
	public Provider getFileProvider() {
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
		AppW appw = (AppW) app1;
		if (this.provider == Provider.LOCAL) {
			((DialogManagerW) appw.getDialogManager()).showSaveDialog();
		}
		// not logged in and can't log in
		else if (!appw.getLoginOperation().isLoggedIn()
				&& (!appw.getNetworkOperation().isOnline()
				|| !appw.getLoginOperation().mayLogIn())
				|| !appw.getNetworkOperation().isOnline()) {
			saveLoggedOut(appw);
			// not logged in and possible to log in
		} else if (!appw.getLoginOperation().isLoggedIn()) {
			appw.getGuiManager().listenToLogin();
			((AppWFull) app).getActivity().markSaveOpen();
			appw.getLoginOperation().showLoginDialog();
			// logged in
		} else {
			((DialogManagerW) appw.getDialogManager()).showSaveDialog();
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
		Window.open(url, "_blank", features);
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
	protected void showOfflineErrorTooltip(AppW appw) {
		if (!appw.getNetworkOperation().isOnline()) {
			ToolTipManagerW.sharedInstance().showBottomMessage(appw
					.getLocalization()
					.getMenu("phone_loading_materials_offline"), appw);
		} else if (!appw.getLoginOperation().isLoggedIn()) {
			ToolTipManagerW.sharedInstance().showBottomMessage(appw
					.getLocalization()
					.getMenu("SaveAccountFailed"), appw);
		}
	}
}
