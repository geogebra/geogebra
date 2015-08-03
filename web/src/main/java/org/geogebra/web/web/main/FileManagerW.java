package org.geogebra.web.web.main;

import java.util.ArrayList;
import java.util.TreeSet;

import org.geogebra.common.main.Feature;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialFilter;
import org.geogebra.common.move.ggtapi.models.SyncEvent;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.javax.swing.GOptionPaneW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.html5.util.ggtapi.JSONparserGGT;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.util.SaveCallback;

import com.google.gwt.storage.client.Storage;

/**
 * Manager for files from {@link Storage localStorage}
 * 
 * JSON including the base64 and metadata is stored under
 * "file_<local id>_<title>" key. The id field inside JSON is for Tube id, is
 * not affected by local id. Local id can still be found inside title => we need
 * to extract title after we load file from LS.
 *
 */
public class FileManagerW extends FileManager {

	/** locale storage */
	Storage stockStore = Storage.getLocalStorageIfSupported();

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public FileManagerW(final AppW app) {
		super(app);
	}

	@Override
	public void delete(final Material mat, boolean permanent, Runnable onSuccess) {
		if (this.stockStore != null) {
			this.stockStore.removeItem(getFileKey(mat));
			removeFile(mat);
			onSuccess.run();
		}

	}

	@Override
	public void saveFile(String base64, long modified, final SaveCallback cb) {
		if (this.stockStore == null) {
			return;
		}

		final Material mat = createMaterial(base64, modified);
		int id;

		if (getApp().getLocalID() == -1) {
			id = createID();
			getApp().setLocalID(id);
		} else {
			id = getApp().getLocalID();
		}
		String key = createKeyString(id, getApp().getKernel().getConstruction()
		        .getTitle());
		mat.setLocalID(id);

		stockStore.setItem(key, mat.toJson().toString());
		cb.onSaved(mat, true);

	}

	/**
	 * creates a new ID
	 * 
	 * @return int ID
	 */
	int createID() {
		int nextFreeID = 1;
		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FileManager.FILE_PREFIX)) {
				int fileID = FileManager.getIDFromKey(key);
				if (fileID >= nextFreeID) {
					nextFreeID = FileManager.getIDFromKey(key) + 1;
				}
			}
		}
		return nextFreeID;
	}

	@Override
	protected void getFiles(final MaterialFilter filter) {
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return;
		}

		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FileManager.FILE_PREFIX)) {
				Material mat = JSONparserGGT.parseMaterial(this.stockStore
				        .getItem(key));
				if (mat == null) {
					mat = new Material(0, MaterialType.ggb);
					mat.setTitle(getTitleFromKey(key));
				}
				if (filter.check(mat)) {
					addMaterial(mat);
				}
			}
		}
	}

	@Override
	public void uploadUsersMaterials(final ArrayList<SyncEvent> events) {
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return;
		}
		setNotSyncedFileCount(this.stockStore.getLength(), events);
		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FILE_PREFIX)) {
				final Material mat = JSONparserGGT
				        .parseMaterial(this.stockStore.getItem(key));
				if (getApp().getLoginOperation().owns(mat)) {
						sync(mat, events);

				} else {
					ignoreNotSyncedFile(events);
				}
			} else {
				ignoreNotSyncedFile(events);
			}
		}
	}

	private int freeBytes = -1;
	private TreeSet<Integer> offlineIDs = new TreeSet<Integer>();
	@Override
	public boolean shouldKeep(int id) {
		if (!getApp().has(Feature.LOCALSTORAGE_FILES)) {
			return false;
		}
		if (offlineIDs.contains(id)) {
			return true;
		}
		if (freeBytes == -1) {
			countFreeBytes();
		}
		if (freeBytes > 10e6) {
			return true;
		}
		return false;
	}

	private void countFreeBytes() {
		if (this.stockStore == null) {
			return;
		}
		this.freeBytes = 5000000;
	}

	@Override
	public void rename(String newTitle, Material mat, Runnable callback) {
		if (this.stockStore == null) {
			return;
		}
		this.stockStore.removeItem(mat.getTitle());
		int newID = createID();
		mat.setLocalID(createID());
		mat.setTitle(newTitle);
		this.stockStore.setItem(FileManager.createKeyString(newID, newTitle),
		        mat.toJson().toString());
	}

	@Override
	public void autoSave() {
		if (this.stockStore == null) {
			return;
		}
		final StringHandler base64saver = new StringHandler() {
			@Override
			public void handle(final String s) {
				final Material mat = createMaterial(s,
				        System.currentTimeMillis() / 1000);
				stockStore.setItem(AUTO_SAVE_KEY, mat.toJson().toString());
			}
		};

		getApp().getGgbApi().getBase64(true, base64saver);
	}

	@Override
	public boolean isAutoSavedFileAvailable() {
		if (stockStore != null && stockStore.getItem(AUTO_SAVE_KEY) != null) {
			return true;
		}
		return false;
	}

	/**
	 * opens the auto-saved file. call first {@code isAutoSavedFileAvailable}
	 */
	@Override
	public void restoreAutoSavedFile() {
		Material autoSaved = JSONparserGGT.parseMaterial(stockStore
		        .getItem(AUTO_SAVE_KEY));
		// maybe another user restores the file, so reset
		// sensitive data
		autoSaved.setAuthor("");
		autoSaved.setAuthorId(0);
		autoSaved.setId(0);
		autoSaved.setGoogleID("");
		openMaterial(autoSaved);
	}

	@Override
	public void deleteAutoSavedFile() {
		if (this.stockStore == null) {
			return;
		}
		this.stockStore.removeItem(AUTO_SAVE_KEY);
	}

	public void saveLoggedOut(AppW app) {
		app.getGuiManager().openFilePicker();
	}
	
	public void export(AppW app) {
		app.getGuiManager().openFilePicker();
	}

	@Override
	public void setTubeID(String localID, Material mat) {
		if (this.stockStore == null) {
			return;
		}
		final Material oldMat = JSONparserGGT.parseMaterial(this.stockStore
		        .getItem(localID));
		mat.setBase64(oldMat.getBase64());
		this.stockStore.setItem(localID, mat.toJson().toString());
		this.offlineIDs.add(mat.getId());

	}

	@Override
	protected void updateFile(String localKey, long modified, Material material) {
		if (this.stockStore == null) {
			return;
		}
		String key = localKey;
		if (key == null) {
			key = FileManager.createKeyString(this.createID(),
			        material.getTitle());
		}
		this.stockStore.setItem(key, material.toJson().toString());
		this.offlineIDs.add(material.getId());
	}

	public final boolean createRemoteAnimGif(AppW app) {
		// // TODO: Login needed.
		// // not logged in and can't log in
		// if (!app.getLoginOperation().isLoggedIn()) {
		// App.debug("[ANIMGIF] Login needed.");
		// // app.getGuiManager().listenToLogin();
		// // ((SignInButton) app.getLAF().getSignInButton(app)).login();
		// // // logged in
		// } else {
			((DialogManagerW) app.getDialogManager()).showAnimGifExportDialog();
		// }
		return true;
	}
	
	public void showExportAsPictureDialog(final String url) {
		GOptionPaneW.INSTANCE.showSaveDialog(getApp(),
				getApp().getPlain("ExportAsPicture"), "export.png", null,
				new AsyncOperation() {

					@Override
					public void callback(Object obj) {
						String[] dialogResult = (String[]) obj;

						if (Integer.parseInt(dialogResult[0]) != 0) {
							return;
						}


						getApp().getFileManager().exportImage(url,
								dialogResult[1]);

					}
				}, getApp().getPlain("Export"));

	}
	public native void exportImage(String url,
	        String title) /*-{
		//idea from http://stackoverflow.com/questions/16245767/creating-a-blob-from-a-base64-string-in-javascript/16245768#16245768

		if ($wnd.navigator.msSaveBlob) {
			var sliceSize = 512;

			var byteCharacters = atob(url
					.substring("data:image/png;base64,".length));
			var byteArrays = [];

			for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
				var slice = byteCharacters.slice(offset, offset + sliceSize);

				var byteNumbers = new Array(slice.length);
				for (var i = 0; i < slice.length; i++) {
					byteNumbers[i] = slice.charCodeAt(i);
				}

				var byteArray = new Uint8Array(byteNumbers);

				byteArrays.push(byteArray);
			}

			var blob = new Blob(byteArrays, {
				type : "image/png"
			});

			//works for internet explorer

			$wnd.navigator.msSaveBlob(blob, title);
		} else {
			//works for firefox
			var a = $doc.createElement("a");
			$doc.body.appendChild(a);
			a.style = "display: none";
			a.href = url;
			a.download = title;
			$wnd.setTimeout(function() {
				a.click()
			}, 1000);
		}

	}-*/;

	public boolean hasBase64(Material material) {
		return material.getBase64() != null
				&& material.getBase64().length() > 0;
	}

}
