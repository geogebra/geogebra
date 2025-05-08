package org.geogebra.common.main;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.Provider;

/**
 * Material manager.
 */
public interface MaterialsManagerI {

	/**
	 * Open a resource
	 * @param material resource to open
	 */
	void openMaterial(Material material);

	/**
	 * Deletes the ggbFile and metaFile from the device. Updates the BrowseView.
	 *
	 * @param material
	 *            {@link Material}
	 */
	void delete(Material material, boolean permanent, Runnable onSuccess);

	/**
	 * Rename a resource.
	 * @param newTitle new title
	 * @param mat resource
	 */
	void rename(String newTitle, Material mat);

	/**
	 * Rename a resource.
	 * @param newTitle new title
	 * @param mat resource
	 * @param callback runs after rename
	 */
	void rename(String newTitle, Material mat, Runnable callback);

	/**
	 * Set file provider.
	 * @param provider file provider
	 */
	void setFileProvider(Provider provider);

	/**
	 * @return file provider
	 */
	Provider getFileProvider();

	/**
	 * Maybe store auto-save file in local storage.
	 * @param counter tick counter, actual save only happens after certain number of ticks
	 */
	void autoSave(int counter);

	/**
	 * @return JSON representation of the current file
	 */
	String getAutosaveJSON();

	/**
	 * Restore auto-saved file
	 * @param json JSON representation of the archive
	 */
	void restoreAutoSavedFile(String json);

	/**
	 * Delete auto-saved file.
	 */
	void deleteAutoSavedFile();

	/**
	 * Save current file.
	 * @param app application
	 * @return success
	 */
	boolean save(App app);

	/**
	 * TODO relates to offline saving, may need fixing or removing
	 * @param i material id
	 * @return Whether to keep offline copy
	 */
	boolean shouldKeep(int i);

	/**
	 * Export as .ggb file
	 * @param app application
	 */
	void export(App app);

	/**
	 * @param url data URL
	 * @param string preferred name
	 * @param extension file extension
	 */
	void exportImage(String url, String string, String extension);

	/**
	 * Share using native dialog.
	 * @param base64 file base 64
	 * @param title file name without extension
	 */
	void nativeShare(String base64, String title);

	/**
	 * @param url data URL
	 * @param filename preferred name
	 * @param extension file extension
	 * @param titleKey dialog title key
	 * @param app application
	 */
	void showExportAsPictureDialog(String url, String filename,
			String extension, String titleKey, App app);

	@MissingDoc
	void refreshAutosaveTimestamp();

	/**
	 * Opens a new browser window. The "name" and "features" arguments are
	 * specified
	 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/window.open">
	 * here</a>.
	 *
	 * @param url
	 *            the URL that the new window will display
	 * @param features
	 *            the features to be enabled/disabled on this window
	 */
	void open(String url, String features);

	/**
	 * Opens Link in a new window
	 * 
	 * @param url
	 *            that should be opened
	 */
	void open(String url);

	/**
	 * Save current file to local filesystem if possible
	 * @param app application
	 * @param callback callback
	 * @return success
	 */
	boolean saveCurrentLocalIfPossible(App app, Runnable callback);

	/**
	 * @return whether online saving is preferred
	 */
	boolean isOnlineSavingPreferred();
}
