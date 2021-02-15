package org.geogebra.common.main;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.Provider;

public interface MaterialsManagerI {

	void openMaterial(Material material);

	/**
	 * Deletes the ggbFile and metaFile from the device. Updates the BrowseView.
	 *
	 * @param material
	 *            {@link Material}
	 */
	void delete(Material material, boolean permanent, Runnable onSuccess);

	void getUsersMaterials();

	void search(String query);

	void rename(String newTitle, Material mat);

	void rename(String newTitle, Material mat, Runnable callback);

	void setFileProvider(Provider google);

	Provider getFileProvider();

	void autoSave(int counter);

	String getAutosaveJSON();

	public void restoreAutoSavedFile(String json);

	public void deleteAutoSavedFile();

	boolean save(App app);

	void saveLoggedOut(App app);

	boolean shouldKeep(int i);

	void getFromTube(int id, boolean fromAnotherDevice);

	boolean isSyncing();

	void export(App app);

	void exportImage(String url, String string, String extension);

	boolean hasBase64(Material material);

	void nativeShare(String s, String string);

	void showExportAsPictureDialog(String url, String filename,
			String extension, String titleKey, App app);

	void refreshAutosaveTimestamp();

	/**
	 * Opens a new browser window. The "name" and "features" arguments are
	 * specified
	 * <a href= 'https://developer.mozilla.org/en-US/docs/Web/API/window.open'>
	 * here</a>.
	 *
	 * @param url
	 *            the URL that the new window will display
	 * @param features
	 *            the features to be enabled/disabled on this window
	 */
	public void open(String url, String features);

	/**
	 * Opens Link in a new window
	 * 
	 * @param url
	 *            that should be opened
	 */
	public void open(String url);
}
