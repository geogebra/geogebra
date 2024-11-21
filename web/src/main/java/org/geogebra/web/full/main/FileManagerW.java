package org.geogebra.web.full.main;

import java.util.TreeSet;

import org.geogebra.common.main.App;
import org.geogebra.common.main.MaterialsManager;
import org.geogebra.common.move.ggtapi.models.JSONParserGGT;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.util.SaveCallback;
import org.geogebra.web.full.util.SaveCallback.SaveState;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.StringConsumer;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Manager for files from localStorage
 * 
 * JSON including the base64 and metadata is stored under
 * "file_[local-id]_[title]" key. The id field inside JSON is for Tube id, is
 * not affected by local id. Local id can still be found inside title, hence we need
 * to extract title after we load file from LS.
 *
 */
public class FileManagerW extends FileManager {

	private static final String TIMESTAMP = "timestamp";
	/** locale storage */
	BrowserStorage stockStore = BrowserStorage.LOCAL;
	private int freeBytes = -1;
	private TreeSet<Integer> offlineIDs = new TreeSet<>();

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
		updateViewerId(mat);
		mat.setLocalID(id);
		try {
			mat.setAppName(app.getConfig().getAppCode());
			stockStore.setItem(key, mat.toJson().toString());
			cb.onSaved(mat, true);
		} catch (Exception e) {
			if (cb.getState() != SaveState.ERROR) {
				mat.setSyncStamp(-1);
				refreshMaterial(mat);
			}
			cb.onError();
		}

	}

	private void updateViewerId(Material mat) {
		if (app.getLoginOperation() != null
				&& app.getLoginOperation().getModel() != null) {
			mat.setViewerID(app.getLoginOperation().getModel().getUserId());
		}

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
			if (key.startsWith(MaterialsManager.FILE_PREFIX)) {
				int fileID = MaterialsManager.getIDFromKey(key);
				if (fileID >= nextFreeID) {
					nextFreeID = MaterialsManager.getIDFromKey(key) + 1;
				}
			}
		}
		return nextFreeID;
	}

	@Override
	public boolean shouldKeep(int id) {
		if (!isOfflinePlatform()) {
			return false;
		}
		if (offlineIDs.contains(id)) {
			return true;
		}
		if (freeBytes == -1) {
			countFreeBytes();
		}
		return freeBytes > 10e6;
	}

	private void countFreeBytes() {
		if (this.stockStore == null) {
			return;
		}
		this.freeBytes = 5000000;
	}
	
	@Override
	public void rename(String newTitle, Material mat) {
		rename(newTitle, mat, null);
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
		this.stockStore.setItem(
				MaterialsManager.createKeyString(newID, newTitle),
				mat.toJson().toString());
	}

	@Override
	public void autoSave(int counter) {
		if (this.stockStore == null || counter % 30 != 0) {
			return;
		}
		final StringConsumer base64saver = s -> {
			final Material mat = createMaterial(s,
					System.currentTimeMillis() / 1000);
			try {
				mat.setAppName(app.getConfig().getAppCode());
				stockStore.setItem(getAutosaveKey(),
						mat.toJson().toString());
			} catch (Exception e) {
				Log.warn("Autosave failed");
			}
		};

		getApp().getGgbApi().getBase64(true, base64saver);
	}

	/**
	 * @return local storage key for preferences
	 */
	protected String getAutosaveKey() {
		return AUTO_SAVE_KEY + app.getConfig().getPreferencesKey();
	}

	@Override
	public String getAutosaveJSON() {
		if (stockStore != null) {
			String timestamp = stockStore.getItem(TIMESTAMP);
			if (timestamp != null) {
				long l = 0;
				try {
					l = Long.parseLong(timestamp);
				} catch (Exception e) {
					Log.warn("Invalid timestamp.");
				}
				if (l > System.currentTimeMillis() - 2000) {
					Log.debug(
							"App still running, autosave timestamp: " + l);
					return null;
				}
			}
			return stockStore.getItem(getAutosaveKey());
		}

		return null;
	}

	/**
	 * opens the auto-saved file. call first {@code isAutoSavedFileAvailable}
	 */
	@Override
	public void restoreAutoSavedFile(String materialJSON) {
		Material autoSaved = JSONParserGGT
				.parseMaterial(materialJSON);
		// maybe another user restores the file, so reset
		// sensitive data
		autoSaved.setCreator(null);
		openMaterial(autoSaved);
	}

	@Override
	public void deleteAutoSavedFile() {
		if (this.stockStore == null) {
			return;
		}
		this.stockStore.removeItem(getAutosaveKey());
	}

	@Override
	public void export(App app1) {
		dialogEvent(app, "exportGGB");
		((AppW) app1).getGuiManager().exportGGB();
	}

	@Override
	public void setTubeID(String localID, Material mat) {
		if (this.stockStore == null) {
			return;
		}
		final Material oldMat = JSONParserGGT
				.parseMaterial(this.stockStore.getItem(localID));
		mat.setBase64(oldMat.getBase64());
		updateViewerId(mat);
		try {
			this.stockStore.setItem(localID, mat.toJson().toString());
		} catch (Exception e) {
			Log.warn("setting tube ID failed");
		}
		this.offlineIDs.add(mat.getLocalID());

	}

	@Override
	protected void updateFile(String localKey, long modified, Material material) {
		if (this.stockStore == null) {
			return;
		}
		material.setModified(modified);
		material.setSyncStamp(modified);
		String key = localKey;
		if (key == null) {
			key = MaterialsManager.createKeyString(this.createID(), material.getTitle());
		}
		try {
			this.stockStore.setItem(key, material.toJson().toString());
			this.offlineIDs.add(material.getLocalID());
		} catch (Exception e) {
			Log.warn("Updating local copy failed.");
		}
	}

	@Override
	public void showExportAsPictureDialog(final String url, String filename,
			String extension, String titleKey, final App app1) {
		final String extension2 = extension;
		DialogData data = new DialogData(titleKey, "Cancel", "Export");
		ComponentDialog dialog = new ComponentDialog(app, data, false, true);
		ComponentInputField inputTextField = new ComponentInputField(app, "", "", "",
				filename + "." + extension, -1, "");
		dialog.addDialogContent(inputTextField);
		dialog.setOnPositiveAction(() -> {
			exportImage(url, inputTextField.getText(), extension2);
			getApp().dispatchEvent(new Event(
					EventType.EXPORT, null,
					"[\"" + extension2 + "\"]"));
		});
		dialog.show();
		dialogEvent(app, "exportPNG");
	}

	@Override
	public void refreshAutosaveTimestamp() {
		if (stockStore != null) {
			stockStore.setItem(TIMESTAMP, "" + System.currentTimeMillis());
		}
	}

	@Override
	public boolean isOnlineSavingPreferred() {
		return getFileProvider() != Material.Provider.LOCAL
				&& app.getNetworkOperation().isOnline()
				&& (app.getLoginOperation().isLoggedIn() || mayLogIn());
	}

	private boolean mayLogIn() {
		return isOfflinePlatform()
				&& app.getLoginOperation().mayLogIn();
	}

	private static void dialogEvent(AppW app, String string) {
		app.dispatchEvent(new Event(EventType.OPEN_DIALOG, null, string));
	}

}
