package org.geogebra.common.main;

import java.util.List;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Pagination;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.debug.Log;

public abstract class MaterialsManager implements MaterialsManagerI {

	/** prefix for autosave items in storage */
	public static final String AUTO_SAVE_KEY = "autosave";
	/** prefix for files in storage */
	public static final String FILE_PREFIX = "file_";
	/** characters not allowed in filename */
	public static final String reservedCharacters = "*/:<>?\\|+,.;=[]";

	/** files waiting for download */
	int notDownloadedFileCount;

	/**
	 * @param matID
	 *            local ID of material
	 * @param title
	 *            of material
	 * @return creates a key (String) for the stockStore
	 */
	public static String createKeyString(int matID, String title) {
		StringBuilder sb = new StringBuilder(title.length() + 12);
		sb.append(FILE_PREFIX);
		sb.append(matID);
		sb.append('_');
		appendTitleWithoutReservedCharacters(title, sb);
		return sb.toString();
	}

	/**
	 * Remove all reserved characters from a ggb file title
	 * 
	 * @param title
	 *            title for ggb file
	 * @return title without reserved characters
	 */
	public static String getTitleWithoutReservedCharacters(String title) {
		StringBuilder sb = new StringBuilder(title.length());
		appendTitleWithoutReservedCharacters(title, sb);
		return sb.toString();
	}

	private static void appendTitleWithoutReservedCharacters(String title,
			StringBuilder sb) {
		for (int i = 0; i < title.length(); i++) {
			if (reservedCharacters.indexOf(title.charAt(i)) == -1) {
				sb.append(title.charAt(i));
			}
		}
	}

	/**
	 * @param mat
	 *            material
	 * @return storage key based on id and title
	 */
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

	/**
	 * uploads the material and removes it from localStorage
	 * 
	 * @param mat
	 *            {@link Material}
	 */
	public void upload(final Material mat) {
		final String localKey = getFileKey(mat);
		mat.setTitle(getTitleFromKey(mat.getTitle()));
		getApp().getLoginOperation().getGeoGebraTubeAPI().uploadLocalMaterial(
				mat,
				new MaterialCallbackI() {

					@Override
					public void onLoaded(final List<Material> parseResponse,
							Pagination meta) {
						if (parseResponse.size() == 1) {
							mat.setTitle(getTitleFromKey(mat.getTitle()));
							mat.setLocalID(
									MaterialsManager.getIDFromKey(localKey));
							final Material newMat = parseResponse.get(0);
							if (mat.thumbnailIsBase64()) {
								newMat.setThumbnailBase64(mat.getThumbnail());
							} else {
								newMat.setThumbnailUrl(mat.getThumbnail());
							}
							newMat.setSyncStamp(newMat.getModified());
							if (!MaterialsManager.this
									.shouldKeep(mat.getLocalID())) {
								delete(mat, true, () -> {
									// nothing to do here
								});
							} else {
								// Meta may have changed (tube ID), sync
								// timestamp needs changing always
								MaterialsManager.this.setTubeID(localKey,
										newMat);

							}
							// TODO moved out of refresh material; do we need it
							// twice (see above)?
							newMat.setSyncStamp(newMat.getModified());
							refreshMaterial(newMat);
						}
					}

					@Override
					public void onError(final Throwable exception) {
						// TODO
					}
				});
	}

	@Override
	public void getFromTube(final int id, final boolean fromAnotherDevice) {
		getApp().getLoginOperation().getResourcesAPI().getItem(id + "",
				new MaterialCallbackI() {

					@Override
					public void onLoaded(final List<Material> parseResponse,
							Pagination meta) {
						MaterialsManager.this.notDownloadedFileCount--;
						// edited on Tube, not edited locally
						if (parseResponse.size() == 1) {
							Log.debug("SYNC downloading file:" + id);
							Material tubeMat = parseResponse.get(0);
							tubeMat.setSyncStamp(tubeMat.getModified());
							tubeMat.setFromAnotherDevice(fromAnotherDevice);
							MaterialsManager.this.updateFile(null,
									tubeMat.getModified(), tubeMat);
						}
					}

					@Override
					public void onError(final Throwable exception) {
						MaterialsManager.this.notDownloadedFileCount--;
						Log.debug("SYNC error loading from tube" + id);
					}
				});

	}

	/**
	 * Update loacl copy
	 * 
	 * @param title
	 *            new title
	 * @param modified
	 *            timestamp
	 * @param material
	 *            material
	 */
	protected abstract void updateFile(String title, long modified,
			Material material);

	protected abstract void showTooltip(Material mat);

	protected abstract App getApp();

	protected abstract void refreshMaterial(Material newMat);

	protected abstract void setTubeID(String localKey, Material newMat);

	@Override
	public boolean saveCurrentLocalIfPossible(App app, Runnable callback) {
		return false;
	}

	@Override
	public boolean isOnlineSavingPreferred() {
		return true;
	}
}
