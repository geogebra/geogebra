package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.geogebra.common.kernel.commands.CmdGetTime;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.SyncEvent;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

public abstract class MaterialsManager implements MaterialsManagerI {

	/** prefix for autosave items in storage */
	public static final String AUTO_SAVE_KEY = "autosave";
	/** prefix for files in storage */
	public static final String FILE_PREFIX = "file_";
	/** characters not allowed in filename */
	public static final String reservedCharacters = "*/:<>?\\|+,.;=[]";

	private int notSyncedFileCount;
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
							ArrayList<Chapter> meta) {
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
									.shouldKeep(mat.getId())) {
								delete(mat, true, new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub

									}
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

	/**
	 * @param count
	 *            number of files waiting for sync
	 * @param events
	 *            sync events
	 */
	public void setNotSyncedFileCount(int count, ArrayList<SyncEvent> events) {
		this.notSyncedFileCount = count;
		checkMaterialsToDownload(events);
	}

	/**
	 * @param events
	 *            sync events
	 */
	public void ignoreNotSyncedFile(ArrayList<SyncEvent> events) {
		this.notSyncedFileCount--;
		checkMaterialsToDownload(events);
	}

	@Override
	public void getFromTube(final int id, final boolean fromAnotherDevice) {
		getApp().getLoginOperation().getGeoGebraTubeAPI().getItem(id + "",
				new MaterialCallbackI() {

					@Override
					public void onLoaded(final List<Material> parseResponse,
							ArrayList<Chapter> meta) {
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

	private void getFromTube(final Material mat) {
		getApp().getLoginOperation().getGeoGebraTubeAPI()
				.getItem(mat.getId() + "", new MaterialCallbackI() {

					@Override
					public void onLoaded(final List<Material> parseResponse,
							ArrayList<Chapter> meta) {

						// edited on Tube, not edited locally
						if (mat.getModified() <= mat.getSyncStamp()) {
							Log.debug("SYNC incomming changes:" + mat.getId());
							MaterialsManager.this.updateFile(getFileKey(mat),
									parseResponse.get(0).getModified(),
									parseResponse.get(0));
						}

					}

					@Override
					public void onError(final Throwable exception) {
						Log.debug("SYNC error loading from tube" + mat.getId());
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

	@Override
	public boolean isSyncing() {
		return this.notDownloadedFileCount > 0 || this.notSyncedFileCount > 0;
	}

	private void checkMaterialsToDownload(ArrayList<SyncEvent> events) {
		if (notSyncedFileCount == 0) {
			for (SyncEvent event : events) {
				if (event.isFavorite() && !event.isZapped()
						&& this.shouldKeep(event.getID())) {
					this.notDownloadedFileCount++;
					getFromTube(event.getID(), true);
				}
			}
		}
	}

	private void deleteFromTube(final Material mat, final Runnable onDelete) {
		if (!getApp().getLoginOperation().getGeoGebraTubeAPI().owns(mat)) {
			delete(mat, true, onDelete);
			return;
		}
		getApp().getLoginOperation().getGeoGebraTubeAPI().deleteMaterial(mat,
				new MaterialCallbackI() {

					@Override
					public void onLoaded(final List<Material> parseResponse,
							ArrayList<Chapter> meta) {

						// edited on Tube, not edited locally
						delete(mat, true, onDelete);

					}

					@Override
					public void onError(final Throwable exception) {
						Log.debug(
								"SYNC error deleting from tube" + mat.getId());
					}
				});

	}

	private void sync(final Material mat, SyncEvent event) {
		long tubeTimestamp = event.getTimestamp();
		Runnable dummyCallback = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

			}
		};
		// First check for conflict
		if (mat.getSyncStamp() < mat.getModified()
				&& (tubeTimestamp != 0 && tubeTimestamp > mat.getSyncStamp())) {
			fork(mat);
			return;
		}

		if (event.isDelete()) {
			delete(mat, true, dummyCallback);
		} else if (event.isUnfavorite() && mat.isFromAnotherDevice()) {
			// remove from local device
			delete(mat, true, dummyCallback);

		} else if (tubeTimestamp != 0 && tubeTimestamp > mat.getSyncStamp()) {

			getFromTube(mat);

		} else {
			// no changes in Tube
			if (mat.isDeleted()) {
				Log.debug("SYNC outgoing delete:" + mat.getId());
				deleteFromTube(mat, dummyCallback);
			} else if (mat.getId() > 0
					&& mat.getModified() <= mat.getSyncStamp()) {
				Log.debug("SYNC material up to date" + mat.getId());
			} else {
				Log.debug("SYNC outgoing changes:" + mat.getId());
				upload(mat);
			}
		}

	}

	private void fork(final Material mat) {
		showTooltip(mat);
		Log.debug("SYNC fork: " + mat.getId() + "," + mat.getSyncStamp() + ","
				+ mat.getTimestamp());
		final String format = getApp().getLocalization()
				.isRightToLeftReadingOrder()
				? "\\Y " + Unicode.LEFT_TO_RIGHT_MARK + "\\F"
						+ Unicode.LEFT_TO_RIGHT_MARK + " \\j"
				: "\\j \\F \\Y";
		String newTitle = mat.getTitle();

		// make sure there's room to add the date
		String suffix = " (" + CmdGetTime.buildLocalizedDate(format, new Date(),
				getApp().getLocalization()) + ")";
		if (newTitle.length() + suffix.length() > 60) {
			newTitle = newTitle.substring(0, 60 - suffix.length());
		}

		// put date on end so the filename is different for the fork
		newTitle = newTitle + suffix;

		final String newTitle2 = newTitle;

		this.rename(newTitle2, mat, new Runnable() {

			@Override
			public void run() {
				mat.setTitle(newTitle2);
				mat.setId(0);
				upload(mat);
			}
		});

	}

	protected abstract void showTooltip(Material mat);

	protected abstract App getApp();

	protected abstract void refreshMaterial(Material newMat);

	protected abstract void setTubeID(String localKey, Material newMat);

}
