package geogebra.web.main;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.Material.Provider;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;
import geogebra.html5.main.FileManagerI;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.gui.browser.SignInButton;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;
import geogebra.web.util.SaveCallback;

import java.util.List;

public abstract class FileManager implements FileManagerI {
	private AppW app;
	private Provider provider;

	public static final String AUTO_SAVE_KEY = "autosave";
	public static final String FILE_PREFIX = "file_";

	/**
	 * @param matID
	 *            local ID of material
	 * @param title
	 *            of material
	 * @return creates a key (String) for the stockStore
	 */
	public static String createKeyString(int matID, String title) {
		return FILE_PREFIX + matID + "_" + title;
	}

	public static String getFileKey(Material mat) {
		return createKeyString(mat.getLocalID(), mat.getTitle());
	}

	/**
	 * returns the ID from the given key. (key is of form "file_ID_fileName")
	 * 
	 * @param key
	 *            String
	 * @return int ID
	 */
	public static int getIDFromKey(String key) {
		return Integer.parseInt(key.substring(FILE_PREFIX.length(),
		        key.indexOf("_", FILE_PREFIX.length())));
	}
	public FileManager(final AppW app) {
		this.app = app;
	}

	public abstract void delete(final Material mat);

	/**
	 * 
	 * @param base64
	 *            only a hint, we can send null and it will be resolved
	 * @param cb
	 */
	public abstract void saveFile(String base64, long modified,
	        final SaveCallback cb);

	public abstract void uploadUsersMaterials();

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

	public Material createMaterial(final String base64, long modified) {
		final Material mat = new Material(0, MaterialType.ggb);

		// TODO check if we need to set timestamp / modified
		mat.setModified(modified);

		if (app.getTubeId() != 0) {
			mat.setId(app.getTubeId());
			mat.setSyncStamp(app.getSyncStamp());
		}

		mat.setBase64(base64);
		mat.setTitle(app.getKernel().getConstruction().getTitle());
		mat.setDescription(app.getKernel().getConstruction()
		        .getWorksheetText(0));
		mat.setThumbnail(app.getEuclidianView1()
		        .getCanvasBase64WithTypeString());
		mat.setAuthor(app.getLoginOperation().getUserName());
		return mat;
	}

	/**
	 * @param query
	 *            String
	 */
	public void search(final String query) {
		getFiles(MaterialFilter.getSearchFilter(query));
	}

	/**
	 * adds the files from the current user to the {@link BrowseGUI}
	 */
	public void getUsersMaterials() {
		getFiles(MaterialFilter.getUniversalFilter());
		// getFiles(MaterialFilter.getAuthorFilter(app.getLoginOperation().getUserName()));
	}

	private int notSyncedFileCount;

	public void setNotSyncedFileCount(int count) {
		this.notSyncedFileCount = count;
	}
	public void sync(final Material mat) {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
		        .getItem(mat.getId() + "", new MaterialCallback() {

			        @Override
			        public void onLoaded(final List<Material> parseResponse) {
				        if (parseResponse.size() == 1
				                && parseResponse.get(0).getModified() > mat
				                        .getSyncStamp()) {
					        // edited on Tube, not edited locally
					        if (mat.getModified() <= mat.getSyncStamp()) {
						        App.debug("SYNC incomming changes:"
						                + mat.getId());
						        FileManager.this.updateFile(getFileKey(mat),
						                parseResponse.get(0).getModified(),
						                parseResponse.get(0));
					        } else {
						        ToolTipManagerW.sharedInstance()
						                .showBottomMessage(
						                        app.getLocalization().getPlain(
						                                "SeveralVersionsOfA",
						                                parseResponse.get(0)
						                                        .getTitle()),
						                        true);
						        App.debug("SYNC fork: " + mat.getId());
						        mat.setId(0);
						        upload(mat);

					        }


				        } else if (parseResponse.size() != 0) {

					        if (mat.getModified() <= mat.getSyncStamp()) {
						        App.debug("SYNC material up to date"
						                + mat.getId());
					        } else {
						        App.debug("SYNC outgoing changes:"
						                + mat.getId());
						        upload(mat);
					        }
				        } else {
					        App.debug("SYNC deletetd");
				        }

			        }

			        @Override
			        public void onError(final Throwable exception) {
				        // TODO
			        }
		        });
	}

	protected abstract void updateFile(String title, long modified,
	        Material material);

	/**
	 * uploads the material and removes it from localStorage
	 * 
	 * @param mat
	 *            {@link Material}
	 */
	public void upload(final Material mat) {
		final String localKey = getFileKey(mat);
		final int oldTubeID = mat.getId();
		mat.setTitle(getTitleFromKey(mat.getTitle()));
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
		        .uploadLocalMaterial(app, mat, new MaterialCallback() {

			        @Override
			        public void onLoaded(final List<Material> parseResponse) {
				        if (parseResponse.size() == 1) {
					        mat.setTitle(getTitleFromKey(mat.getTitle()));
					        mat.setLocalID(FileManager.getIDFromKey(localKey));
					        App.debug("GGG uploading" + localKey);
					        final Material newMat = parseResponse.get(0);
					        newMat.setThumbnail(mat.getThumbnail());
					        newMat.setSyncStamp(newMat.getModified());
					        if (!FileManager.this.shouldKeep(mat.getId())) {
						        delete(mat);
					        } else {
						        if (oldTubeID != newMat.getId()) {
							        FileManager.this
							                .setTubeID(localKey, newMat);
						        }
					        }
					        App.debug("GGG parse" + localKey);


					        app.getGuiManager().getBrowseView().refreshMaterial(newMat, false);
				        }
			        }

			        @Override
			        public void onError(final Throwable exception) {
				        // TODO
			        }
		        });
	}

	public abstract void setTubeID(String localKey, Material mat);

	public boolean shouldKeep(int id) {
		return true;
	}

	/**
	 * key is of form "file_ID_title"
	 * 
	 * @param key
	 *            file key
	 * @return the title
	 */
	public static String getTitleFromKey(String key) {
		return key.substring(key.indexOf("_", key.indexOf("_") + 1) + 1);
	}

	public void setFileProvider(Provider google) {
		this.provider = google;
	}

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
	public AppW getApp() {
		return this.app;
	}

	public final boolean save(AppW app) {
		// not logged in and can't log in
		if (!app.getLoginOperation().isLoggedIn()
		        && (!app.getNetworkOperation().isOnline() || !app
		                .getLoginOperation().mayLogIn())) {
			saveLoggedOut(app);
			// not logged in and possible to log in
		} else if (!app.getLoginOperation().isLoggedIn()) {
			app.getGuiManager().listenToLogin();
			((SignInButton) app.getLAF().getSignInButton(app)).login();
			// logged in
		} else {
			((DialogManagerW) app.getDialogManager()).showSaveDialog();
		}
		return true;
	}

}
