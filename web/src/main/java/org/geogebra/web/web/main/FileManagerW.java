package org.geogebra.web.web.main;

import java.util.ArrayList;
import java.util.TreeSet;

import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.ggtapi.models.JSONParserGGT;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialFilter;
import org.geogebra.common.move.ggtapi.models.SyncEvent;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.StringHandler;
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

	private static final String TIMESTAMP = "timestamp";
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
				Material mat = JSONParserGGT.parseMaterial(this.stockStore
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
				final Material mat = JSONParserGGT
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
	public void autoSave(int counter) {
		if (this.stockStore == null || counter % 30 != 0) {
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
	public String getAutosaveJSON() {
		if (Browser.supportsSessionStorage()) {

			if (stockStore != null) {
				if (stockStore.getItem(TIMESTAMP) != null) {
					long l = 0;
					try{
						l = Long.parseLong(stockStore.getItem(TIMESTAMP));
					} catch (Exception e) {
						Log.warn("Invalid timestamp.");
					}
					if (l > System.currentTimeMillis() - 2000) {
						Log.debug(
								"App still running, autosave timestamp: " + l);
						return null;
					}
				}
				return stockStore.getItem(AUTO_SAVE_KEY);
			}
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
		app.getGuiManager().exportGGB();
	}
	
	public void export(AppW app) {
		app.getGuiManager().exportGGB();
	}

	@Override
	public void setTubeID(String localID, Material mat) {
		if (this.stockStore == null) {
			return;
		}
		final Material oldMat = JSONParserGGT
				.parseMaterial(this.stockStore
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

	
	public void showExportAsPictureDialog(final String url, String filename,
			final AppW app) {
		Localization loc = getApp().getLocalization();
		app.getGuiManager()
				.getOptionPane()
				.showSaveDialog(getApp(), loc.getMenu("ExportAsPicture"),
						filename + ".png", null,
						new AsyncOperation<String[]>() {

							@Override
							public void callback(String[] obj) {

								if (Integer.parseInt(obj[0]) != 0) {
									return;
								}

								exportImage(url, obj[1]);
								getApp().dispatchEvent(new Event(
										EventType.EXPORT, null, "[\"png\"]"));
							}
						}, loc.getMenu("Export"));

	}

	public boolean hasBase64(Material material) {
		return material.getBase64() != null
				&& material.getBase64().length() > 0;
	}

	public void exportImage(String url, String filename) {
		Browser.exportImage(url, filename);
	}

	public void refreshAutosaveTimestamp() {
		if (stockStore != null) {
			stockStore.setItem(TIMESTAMP, "" + System.currentTimeMillis());
		}

	}

}
