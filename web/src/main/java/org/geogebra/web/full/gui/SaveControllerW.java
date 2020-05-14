package org.geogebra.web.full.gui;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.main.MaterialsManager;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.SaveController;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.TextObject;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.util.SaveDialogI;
import org.geogebra.web.full.main.FileManager;
import org.geogebra.web.full.move.googledrive.operations.GoogleDriveOperationW;
import org.geogebra.web.full.util.SaveCallback;
import org.geogebra.web.full.util.SaveCallback.SaveState;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class to handle material saving.
 * 
 * @author laszlo
 *
 */
public class SaveControllerW implements SaveController {

	private AppW app;
	private Localization loc;
	private MaterialType saveType = null;
	private SaveListener listener = null;
	private String fileName = "";
	private AsyncOperation<Boolean> runAfterSave = null;
	private AsyncOperation<Boolean> autoSaveCallback;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            The application.
	 */
	public SaveControllerW(AppW app) {
		this.app = app;
		loc = app.getLocalization();
	}

	/**
	 * @return see {@link AppW}
	 */
	public AppW getAppW() {
		return app;
	}

	/**
	 * @return file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return save listener
	 */
	public SaveListener getListener() {
		return listener;
	}

	@Override
	public void saveActiveMaterial(AsyncOperation<Boolean> autoSaveCB) {
		Material mat = app.getActiveMaterial();
		if (mat != null) {
			syncIdAndType(mat);
			this.autoSaveCallback = autoSaveCB;
			saveAs(mat.getTitle(), MaterialVisibility.value(mat.getVisibility()), null);
		}
	}

	@Override
	public void ensureTypeOtherThan(MaterialType type) {
		Material activeMaterial = app.getActiveMaterial();
		if (activeMaterial != null
				&& activeMaterial.getType() == type) {
			app.getKernel().getConstruction().setTitle(null);
			app.setActiveMaterial(null);
		}
	}

	@Override
	public void showDialogIfNeeded(AsyncOperation<Boolean> examCallback) {
		SaveDialogI saveDialog = ((DialogManagerW) app.getDialogManager()).getSaveDialog();
		showDialogIfNeeded(examCallback, !app.isSaved(), null);
		saveDialog.setDiscardMode();
	}

	/**
	 * @param runnable
	 *         callback gets true if save happened
	 * @param needed
	 *         whether to show the dialog
	 * @param anchor
	 *         UI element to be used for positioning the save dialog
	 */
	public void showDialogIfNeeded(final AsyncOperation<Boolean> runnable, boolean needed,
								   Widget anchor) {
		if (needed && !app.getLAF().isEmbedded()) {
			final Material oldActiveMaterial = app.getActiveMaterial();
			final String oldTitle = app.getKernel().getConstruction().getTitle();
			ensureTypeOtherThan(Material.MaterialType.ggsTemplate);
			setRunAfterSave(new AsyncOperation<Boolean>() {
				@Override
				public void callback(Boolean saved) {
					if (!saved) {
						app.setActiveMaterial(oldActiveMaterial);
						app.getKernel().getConstruction().setTitle(oldTitle);
					}
					runnable.callback(saved);
				}
			});
			((DialogManagerW) app.getDialogManager()).getSaveDialog().showAndPosition(anchor);
		} else {
			setRunAfterSave(null);
			runnable.callback(true);
		}
	}

	@Override
	public void saveAs(String name, MaterialVisibility visibility, SaveListener l) {
		this.listener = l;
		this.fileName = name;
		if (app.getFileManager().getFileProvider() == Provider.LOCAL) {
			app.getKernel().getConstruction().setTitle(name);
			app.getFileManager().export(app);
		} else if (app.isOffline() || !app.getLoginOperation().isLoggedIn()) {
			saveLocal();
		} else if (app.getFileManager().getFileProvider() == Provider.GOOGLE) {
			uploadToDrive();
		} else {
			Material activeMaterial = app.getActiveMaterial();
			if (activeMaterial == null || isMacro()) {
				activeMaterial = new Material(0, saveType);
				app.setActiveMaterial(activeMaterial);
			} else if (!app.getLoginOperation()
					.owns(activeMaterial)) {
				activeMaterial.setId(0);
				activeMaterial.setSharingKey(null);
			}

			activeMaterial.setVisibility(visibility.getToken());
			uploadToGgt(activeMaterial.getVisibility());
		}
	}

	private static String getTitleOnly(String key) {
		return key.substring(key.indexOf("_", key.indexOf("_") + 1) + 1);
	}

	private void syncIdAndType(Material mat) {
		getAppW().setTubeId(mat.getSharingKeyOrId());
		setSaveType(mat.getType());
	}

	/**
	 * Offline saving
	 */
	private void saveLocal() {
		ToolTipManagerW.sharedInstance().showBottomMessage(loc.getMenu("Saving"), false, app);
		if (!fileName.equals(app.getKernel().getConstruction().getTitle())) {
			app.setTubeId(null);
			app.setLocalID(-1);
		}
		app.getKernel().getConstruction().setTitle(fileName);
		app.getGgbApi().getBase64(true, newBase64Callback());
	}

	/**
	 * Handles the upload of the file and closes the dialog. If there are
	 * sync-problems with a file, a new one is generated on ggt.
	 */
	private void uploadToGgt(final String visibility) {
		final boolean titleChanged = !fileName
				.equals(app.getKernel().getConstruction().getTitle());
		if (this.saveType != MaterialType.ggt) {
			app.getKernel().getConstruction().setTitle(fileName);
		}
		Material mat = app.getActiveMaterial();
		if (!titleChanged && mat != null) {
			syncIdAndType(mat);
		}

		final AsyncOperation<String> handler = new AsyncOperation<String>() {
			@Override
			public void callback(String base64) {
				if (titleChanged && (isWorksheet() || savedAsTemplate())) {
					Log.debug("SAVE filename changed");
					getAppW().updateMaterialURL(0, null, null);
					doUploadToGgt(getAppW().getTubeId(), visibility, base64,
							newMaterialCB(base64, false));
				} else if (StringUtil.emptyOrZero(getAppW().getTubeId())
						|| isMacro()) {
					Log.debug("SAVE had no Tube ID or tool is saved");
					doUploadToGgt(null, visibility, base64,
							newMaterialCB(base64, false));
				} else {
					handleSync(base64, visibility);
				}
			}
		};

		ToolTipManagerW.sharedInstance().showBottomMessage(loc.getMenu("Saving"), false, app);

		if (saveType == MaterialType.ggt) {
			app.getGgbApi().getMacrosBase64(true, handler);
		} else {
			app.getGgbApi().getBase64(true, handler);
		}
		if (listener != null) {
			listener.hide();
		}
	}

	private void uploadToDrive() {
		ToolTipManagerW.sharedInstance().showBottomMessage(loc.getMenu("Saving"), false, app);
		app.getGoogleDriveOperation().afterLogin(new Runnable() {

			@Override
			public void run() {
				doUploadToDrive();
			}
		});
	}

	/**
	 * GoogleDrive upload
	 * 
	 */
	void doUploadToDrive() {
		String saveName = fileName;
		String prefix = saveType == MaterialType.ggb ? ".ggb" : ".ggt";
		if (!saveName.endsWith(prefix)) {
			app.getKernel().getConstruction().setTitle(saveName);
			saveName += prefix;
		} else {
			app.getKernel().getConstruction()
					.setTitle(saveName.substring(0, saveName.length() - prefix.length()));
		}
		JavaScriptObject callback = ((GoogleDriveOperationW) app.getGoogleDriveOperation())
				.getPutFileCallback(saveName, "GeoGebra", saveType == MaterialType.ggb);
		if (saveType == MaterialType.ggt) {
			app.getGgbApi().getMacrosBase64(true, callback);
		} else {
			app.getGgbApi().getBase64(true, callback);
		}
	}

	/**
	 * @param base64
	 *            material base64
	 * @param visibility
	 *            "P" - private / "O" - public / "S" - shared
	 */
	void handleSync(final String base64, final String visibility) {
		app.getLoginOperation().getGeoGebraTubeAPI().getItem(app.getTubeId() + "",
				new MaterialCallback() {

					@Override
					public void onLoaded(final List<Material> parseResponse,
							ArrayList<Chapter> meta) {
						MaterialCallbackI materialCallback;
						if (parseResponse.size() == 1) {
							if (parseResponse.get(0).getModified() > getAppW()
									.getSyncStamp()) {
								Log.debug("SAVE MULTIPLE" + parseResponse.get(0).getModified() + ":"
										+ getAppW().getSyncStamp());
								getAppW().updateMaterialURL(0, null, null);
								materialCallback = newMaterialCB(base64, true);
							} else {
								materialCallback = newMaterialCB(base64, false);
							}
							String key = parseResponse.get(0).getSharingKeyOrId();
							doUploadToGgt(key, visibility,
									base64,
									materialCallback);
						} else {
							// if the file was deleted meanwhile
							// (parseResponse.size() == 0)
							getAppW().setTubeId(null);
							materialCallback = newMaterialCB(base64, false);
							doUploadToGgt(getAppW().getTubeId(), visibility,
									base64,
									materialCallback);
						}
					}

					@Override
					public void onError(final Throwable exception) {
						getAppW().showError(Errors.SaveFileFailed);
					}
				});
	}

	/**
	 * Does the upload of the actual opened file to GeoGebraTube
	 * 
	 * @param tubeID
	 *            id in materials platform
	 * @param visibility
	 *            visibility string
	 * @param base64
	 *            material base64
	 * 
	 * @param materialCallback
	 *            {@link MaterialCallback}
	 */
	private void doUploadToGgt(String tubeID, String visibility, String base64,
			MaterialCallbackI materialCallback) {
		app.getLoginOperation().getGeoGebraTubeAPI().uploadMaterial(tubeID, visibility,
				fileName, base64, materialCallback, this.saveType);
	}

	@Override
	public boolean isWorksheet() {
		return MaterialType.ggb.equals(saveType) || MaterialType.ggs.equals(saveType);
	}

	@Override
	public boolean isMacro() {
		return MaterialType.ggt.equals(saveType);
	}

	/**
	 * @param app
	 *            used to get current sync stamp
	 * @return current time in seconds since epoch OR app sync stamp + 1 if
	 *         bigger (to avoid problems with system clock)
	 */
	public static long getCurrentTimestamp(AppW app) {
		return Math.max(System.currentTimeMillis() / 1000, app.getSyncStamp() + 1);
	}

	@Override
	public void setSaveType(MaterialType saveType) {
		this.saveType = saveType;
	}

	@Override
	public MaterialType getSaveType() {
		return saveType;
	}

	@Override
	public boolean savedAsTemplate() {
		return MaterialType.ggsTemplate.equals(getSaveType());
	}

	/**
	 * @param base64
	 *            material base64
	 * @param forked
	 *            whether this is a fork
	 * @return save callback
	 */
	MaterialCallbackI newMaterialCB(final String base64, final boolean forked) {
		return new MaterialCallback() {

			@Override
			public void onLoaded(final List<Material> parseResponse, ArrayList<Chapter> meta) {
				if (isWorksheet() || savedAsTemplate()) {
					if (parseResponse.size() == 1) {
						Material newMat = parseResponse.get(0);
						newMat.setThumbnailBase64(
								((EuclidianViewWInterface) getAppW()
										.getActiveEuclidianView())
										.getCanvasBase64WithTypeString());
						getAppW().getKernel().getConstruction()
								.setTitle(getFileName());

						// last synchronization is equal to last modified
						getAppW().setSyncStamp(newMat.getModified());

						newMat.setSyncStamp(newMat.getModified());

						getAppW().updateMaterialURL(newMat.getId(),
								newMat.getSharingKeyOrId(), getFileName());

						getAppW().setActiveMaterial(newMat);
						getAppW().setSyncStamp(newMat.getModified());
						saveLocalIfNeeded(newMat.getModified(),
								forked ? SaveState.FORKED : SaveState.OK);
						// if we got there via file => new, do the file =>new
						// now
						runAfterSaveCallback(true);
						runAutoSaveCallback();
					} else {
						resetCallback();
						saveLocalIfNeeded(
								SaveControllerW.getCurrentTimestamp(getAppW()),
								SaveState.ERROR);
					}
				} else {
					if (parseResponse.size() == 1) {
						SaveCallback.onSaved(getAppW(), SaveState.OK,
								isMacro());
						runAutoSaveCallback();
					} else {
						SaveCallback.onSaved(getAppW(), SaveState.ERROR,
								isMacro());
					}
				}
				if (getListener() != null) {
					getListener().hide();
				}
			}

			@Override
			public void onError(final Throwable exception) {
				Log.error("SAVE Error" + exception.getMessage());
				resetCallback();
				if (exception.getMessage().contains("auth")) {
					getAppW().getLoginOperation().performTokenLogin();
				}
				getAppW().getGuiManager().exportGGB(true);
				saveLocalIfNeeded(
						SaveControllerW.getCurrentTimestamp(getAppW()),
						SaveState.ERROR);
				if (getListener() != null) {
					getListener().hide();
				}
			}

			private void saveLocalIfNeeded(long modified, SaveState state) {
				if (isWorksheet() && (getAppW().getFileManager().shouldKeep(0)
						|| getAppW().has(Feature.LOCALSTORAGE_FILES)
						|| state == SaveState.ERROR)) {
					getAppW().getKernel().getConstruction()
							.setTitle(getFileName());
					((FileManager) getAppW().getFileManager()).saveFile(base64,
							modified, new SaveCallback(getAppW(), state));
				} else {
					SaveCallback.onSaved(getAppW(), state, false);
				}
			}
		};
	}

	private AsyncOperation<String> newBase64Callback() {
		return new AsyncOperation<String>() {

			@Override
			public void callback(String s) {
				((FileManager) getAppW().getFileManager()).saveFile(s,
						getCurrentTimestamp(getAppW()),
						new SaveCallback(getAppW(), SaveState.OK) {
							@Override
							public void onSaved(final Material mat, final boolean isLocal) {
								super.onSaved(mat, isLocal);
								runAfterSaveCallback(true);
							}
						});
				if (getListener() != null) {
					getListener().hide();
				}
			}
		};
	}

	@Override
	public void runAfterSaveCallback(boolean activeMaterial) {
		if (getRunAfterSave() != null) {
			getRunAfterSave().callback(activeMaterial);
			resetCallback();
		}
	}

	/**
	 * method to run auto save callback
	 */
	public void runAutoSaveCallback() {
		if (autoSaveCallback != null) {
			autoSaveCallback.callback(true);
			autoSaveCallback = null;
		}
	}

	/**
	 * resets the callback
	 */
	void resetCallback() {
		this.setRunAfterSave(null);
	}

	@Override
	public void dontSave() {
		if (isWorksheet()) {
			app.setSaved();
			// run only if material active/created
			runAfterSaveCallback(app.getActiveMaterial() != null);
		}
	}

	@Override
	public void cancel() {
		if (isWorksheet()) {
			// run only if material active/created
			runAfterSaveCallback(app.getActiveMaterial() != null);
		}
	}

	private AsyncOperation<Boolean> getRunAfterSave() {
		return runAfterSave;
	}

	@Override
	public void setRunAfterSave(AsyncOperation<Boolean> runAfterSave) {
		this.runAfterSave = runAfterSave;
	}

	@Override
	public boolean updateSaveTitle(TextObject title, String fallback) {
		String consTitle = app.getKernel().getConstruction().getTitle();
		if (!StringUtil.empty(consTitle) && !isMacro()) {
			if (consTitle.startsWith(MaterialsManager.FILE_PREFIX)) {
				consTitle = getTitleOnly(consTitle);
			}
			Material activeMaterial = app.getActiveMaterial();
			if (activeMaterial != null && !app.getLoginOperation()
					.owns(activeMaterial)) {
				consTitle = MaterialRestAPI.getCopyTitle(loc, consTitle);
				title.setText(consTitle);
				return true;
			}
			title.setText(consTitle);
		} else {
			title.setText(fallback);
			return true;
		}
		return false;
	}
}
