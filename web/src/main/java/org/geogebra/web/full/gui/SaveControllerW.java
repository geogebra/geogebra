package org.geogebra.web.full.gui;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.main.SaveController;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.move.googledrive.operations.GoogleDriveOperationW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Class to handle material saving.
 * 
 * @author laszlo
 *
 */
public class SaveControllerW implements SaveController {

	private AppW app;
	private Localization loc;
	private MaterialType saveType;
	private SaveListener listener = null;
	private String fileName = "";
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

	@Override
	public void save(String name, MaterialVisibility visibility, SaveListener l) {
		if (l == null) {
			return;
		}

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
			if (app.getActiveMaterial() == null || isMacro()) {
				app.setActiveMaterial(new Material(0, saveType));
			}

			app.getActiveMaterial().setVisibility(visibility.getToken());
			uploadToGgt(app.getActiveMaterial().getVisibility());
		}
	}

	private void saveLocal() {
		ToolTipManagerW.sharedInstance().showBottomMessage(loc.getMenu("Saving"), false, app);
		if (!fileName.equals(app.getKernel().getConstruction().getTitle())) {
			app.setTubeId(null);
			app.setLocalID(-1);
		}
		app.getKernel().getConstruction().setTitle(fileName);
		app.getGgbApi().getBase64(true, listener.base64Callback());

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

		final AsyncOperation<String> handler = new AsyncOperation<String>() {
			@Override
			public void callback(String base64) {
				if (titleChanged && isWorksheet()) {
					Log.debug("SAVE filename changed");
					app.updateMaterialURL(0, null, null);
					doUploadToGgt(app.getTubeId(), visibility, base64,
							listener.initMaterialCB(base64, false));
				} else if (StringUtil.emptyOrZero(app.getTubeId()) || isMacro()) {
					Log.debug("SAVE had no Tube ID or tool is saved");
					doUploadToGgt(null, visibility, base64,
							listener.initMaterialCB(base64, false));
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

		listener.hide();
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
	 *            "P" / "O" / "S"
	 */
	void handleSync(final String base64, final String visibility) {
		app.getLoginOperation().getGeoGebraTubeAPI().getItem(app.getTubeId() + "",
				new MaterialCallback() {

					@Override
					public void onLoaded(final List<Material> parseResponse,
							ArrayList<Chapter> meta) {
						MaterialCallbackI materialCallback;
						if (parseResponse.size() == 1) {
							if (parseResponse.get(0).getModified() > app.getSyncStamp()) {
								Log.debug("SAVE MULTIPLE" + parseResponse.get(0).getModified() + ":"
										+ app.getSyncStamp());
								app.updateMaterialURL(0, null, null);
								materialCallback = listener.initMaterialCB(base64, true);
							} else {
								materialCallback = listener.initMaterialCB(base64, false);
							}
							doUploadToGgt(app.getTubeId(), visibility, base64,
									materialCallback);
						} else {
							// if the file was deleted meanwhile
							// (parseResponse.size() == 0)
							app.setTubeId(null);
							materialCallback = listener.initMaterialCB(base64, false);
							doUploadToGgt(app.getTubeId(), visibility, base64,
									materialCallback);
						}
					}

					@Override
					public void onError(final Throwable exception) {
						// TODO show correct message
						app.showError("Error");
					}
				});
	}

	/**
	 * does the upload of the actual opened file to GeoGebraTube
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
	void doUploadToGgt(String tubeID, String visibility, String base64,
			MaterialCallbackI materialCallback) {
		app.getLoginOperation().getGeoGebraTubeAPI().uploadMaterial(tubeID, visibility,
				fileName, base64, materialCallback, this.saveType);
	}

	/**
	 * @return true if the MaterialType is ggb
	 */
	@Override
	public boolean isWorksheet() {
		return saveType.equals(MaterialType.ggb) || saveType.equals(MaterialType.ggs);
	}

	/**
	 * @return true if the MaterialType is ggt
	 */
	@Override
	public boolean isMacro() {
		return saveType.equals(MaterialType.ggt);
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
}
