package org.geogebra.web.web.main;

import org.geogebra.common.main.App;
import org.geogebra.common.main.MaterialsManager;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.common.move.ggtapi.models.MaterialFilter;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.browser.BrowseGUI;
import org.geogebra.web.web.gui.browser.SignInButton;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.util.SaveCallback;

import com.google.gwt.user.client.Window;

/**
 * Manager for local file saving
 *
 */
public abstract class FileManager extends MaterialsManager {
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

		if (app.getTubeId() != 0) {
			mat.setId(app.getTubeId());
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
		if (app.getActiveMaterial() != null) {
			mat.setSharingKey(app.getActiveMaterial().getSharingKey());
			mat.setVisibility(app.getActiveMaterial().getVisibility());
			mat.setURL(app.getActiveMaterial().getURL());
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
		getFiles(MaterialFilter.getUniversalFilter());
		// getFiles(MaterialFilter.getAuthorFilter(app.getLoginOperation().getUserName()));
	}

	@Override
	protected final void showTooltip(Material mat) {
		ToolTipManagerW.sharedInstance().showBottomMessage(app.getLocalization()
				.getPlain("SeveralVersionsOfA", mat.getTitle()), true, app);

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
			app.showError(app.getLocalization().getError("LoadFileFailed"));
			t.printStackTrace();
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
				&& (!appw.getNetworkOperation().isOnline() || !appw
		                .getLoginOperation().mayLogIn())) {
			saveLoggedOut(appw);
			// not logged in and possible to log in
		} else if (!appw.getLoginOperation().isLoggedIn()) {
			appw.getGuiManager().listenToLogin();
			((SignInButton) appw.getLAF().getSignInButton(appw)).login();
			// logged in
		} else {
			((DialogManagerW) appw.getDialogManager()).showSaveDialog();
		}
		return true;
	}



	@Override
	public native void nativeShare(String base64, String title)/*-{
		if ($wnd.android) {
			$wnd.android.share(base64, title, 'ggb');
		}
	}-*/;
	
	public void open(String url, String name, String features){
		Window.open(url, name, features);
	}

}
