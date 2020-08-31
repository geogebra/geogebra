package org.geogebra.web.touch;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.main.MaterialsManager;
import org.geogebra.common.move.ggtapi.models.JSONParserGGT;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.MaterialFilter;
import org.geogebra.common.move.ggtapi.models.SyncEvent;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.ExternalAccess;
import org.geogebra.web.full.gui.browser.BrowseGUI;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.main.FileManager;
import org.geogebra.web.full.util.SaveCallback;
import org.geogebra.web.html5.main.AppW;

public class WinFileManager extends FileManager {

	public WinFileManager(final AppW app) {
		super(app);
	}

	@Override
	public void autoSave(int counter) {
		// not in touch either
	}

	@Override
	public String getAutosaveJSON() {
		return null;
	}

	@Override
	public void restoreAutoSavedFile(String json) {
		// not in touch either
	}

	@Override
	public void deleteAutoSavedFile() {
		// not in touch either
	}

	@Override
	public void saveLoggedOut(App app1) {
		((DialogManagerW) app1.getDialogManager()).showSaveDialog();
	}
	
	@Override
	public void rename(String newTitle, Material mat) {
		rename(newTitle, mat, null);
	}

	@Override
	public void rename(String newTitle, Material mat, Runnable callback) {
		renameNative(MaterialsManager.getFileKey(mat), newTitle,
				mat.getModified()
		        + "", callback);

	}

	private native void renameNative(String oldTitle, String newTitle,
	        String timestamp, Runnable callback) /*-{
		if ($wnd.android && $wnd.android.renameFile) {
			$wnd.android.renameFile(oldTitle, newTitle, timestamp, function() {
				callback.@java.lang.Runnable::run()();
			});
		}
	}-*/;

	@Override
	public void openMaterial(final Material mat) {
		getBase64(getFileKey(mat), new NativeSaveCallback() {

			@Override
			public void onSuccess(String fileID) {
				mat.setBase64(fileID);
				doOpen(mat);
			}

			@Override
			public void onFailure() {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void delete(Material mat, boolean permanent, Runnable onSuccess) {
		deleteNative(getFileKey(mat));
		removeFile(mat);
		((BrowseGUI) getApp().getGuiManager().getBrowseView())
		        .setMaterialsDefaultStyle();

	}

	private native void deleteNative(String title) /*-{
		if ($wnd.android && $wnd.android.deleteFile) {
			$wnd.android.deleteFile(title);
		}
	}-*/;

	@Override
	public void upload(final Material mat) {

		getBase64(getFileKey(mat), new NativeSaveCallback() {

			@Override
			public void onSuccess(String base64) {
				mat.setBase64(base64);
				doUpload(mat);
			}

			@Override
			public void onFailure() {
				// TODO Auto-generated method stub

			}
		});
	}

	void doOpen(Material mat) {
		super.openMaterial(mat);
	}

	void doUpload(Material mat) {
		super.upload(mat);
	}

	private native void getBase64(String fileKey, NativeSaveCallback nsc)/*-{
		if ($wnd.android && $wnd.android.getBase64) {
			$wnd.android
					.getBase64(
							fileKey,
							function(jsString) {
								nsc.@org.geogebra.web.touch.NativeSaveCallback::onSuccess(Ljava/lang/String;)(jsString);
							});
		}
	}-*/;

	@Override
	public void saveFile(String base64, long modified, final SaveCallback cb) {

		final Material mat = createMaterial("", modified);
		String meta = mat.toJson().toString();
		doSave(base64, getApp().getLocalID(),
				getApp().getKernel().getConstruction().getTitle(), meta,
				new NativeSaveCallback() {

					@Override
					public void onSuccess(String fileID) {
						getApp().setLocalID(Integer.parseInt(fileID));
						cb.onSaved(mat, true);
					}

					@Override
					public void onFailure() {
						cb.onError();

					}
				});
	}

	protected native void doSave(String base64, int id, String title,
	        String meta, NativeSaveCallback nsc) /*-{
		var that = this;
		if ($wnd.android && $wnd.android.saveFile) {
			$wnd.android
					.saveFile(
							base64,
							id,
							title,
							meta,
							function(jsString) {
								nsc.@org.geogebra.web.touch.NativeSaveCallback::onSuccess(Ljava/lang/String;)(jsString);
							},
							function(jsString) {
								nsc.@org.geogebra.web.touch.NativeSaveCallback::onFailure()();
							});
		}

	}-*/;

	@Override
	public void uploadUsersMaterials(final ArrayList<SyncEvent> events) {
		nativeUploadUsersMaterials(new AsyncOperation<String>() {

			@Override
			public void callback(String jsString) {
				JSONTokener tok = new JSONTokener(jsString);
				try {
				JSONObject jv = new JSONObject(tok);
				for (String key : jv.keySet()) {
					Material mat = JSONParserGGT.prototype.toMaterial(
							(JSONObject) jv.get(key));
						mat.setLocalID(MaterialsManager.getIDFromKey(key));
					if (getApp().getLoginOperation().owns(mat)) {

						sync(mat, events);

					}

				}
				} catch (Exception e) {
					// invalid JSON: ignore
				}
			}
		});
	}

	/**
	 * @param sh
	 *            handler that uploads the files
	 */
	public native void nativeUploadUsersMaterials(
			AsyncOperation<String> sh) /*-{
		var that = this;
		if ($wnd.android && $wnd.android.getFiles) {
			$wnd.android
					.getFiles(function(jsString) {
						sh.@org.geogebra.common.util.AsyncOperation::callback(*)(jsString);
					});
		}
	}-*/;

	@ExternalAccess
	private void addMaterials(String jsString) {
		JSONTokener tok = new JSONTokener(jsString);
		try {
			JSONObject jv = new JSONObject(tok);
			for (String key : jv.keySet()) {
				Material mat = JSONParserGGT.prototype
						.toMaterial((JSONObject) jv.get(key));
				mat.setLocalID(MaterialsManager.getIDFromKey(key));
				this.addMaterial(mat);
			}
		} catch (Exception e) {
			// invalid JSON: ignore
		}
	}

	@Override
	protected native void getFiles(MaterialFilter materialFilter) /*-{
		var that = this;
		if ($wnd.android && $wnd.android.getFiles) {
			$wnd.android
					.getFiles(function(jsString) {
						that.@org.geogebra.web.touch.WinFileManager::addMaterials(Ljava/lang/String;)(jsString);
					});
		}

	}-*/;

	@Override
	public void setTubeID(String localID, Material mat) {
		nativeUpdateMeta(localID, mat.toJson().toString());
	}

	private native void nativeUpdateMeta(String localID, String string) /*-{
		if ($wnd.android && $wnd.android.updateMeta) {
			$wnd.android.updateMeta(key, json);
		}
	}-*/;

	@Override
	protected void updateFile(String key, long modified, Material material) {
		material.setModified(modified);
		material.setSyncStamp(modified);
		String base64 = material.getBase64();
		material.setBase64("");
		nativeUpdateFile(material.getLocalID(), material.getTitle(), base64,
		        material.toJson().toString());
	}

	private native void nativeUpdateFile(int id, String title, String json,
	        String base64) /*-{
		if ($wnd.android && $wnd.android.updateFile) {
			$wnd.android.updateFile(id, title, json, base64);
		}
	}-*/;
	
	@Override
	public void export(final App app1) {
		final String title1 = app1.getExportTitle();
		app1.getGgbApi().showTooltip(app1.getLocalization().getMenu("Saving"));
		final AsyncOperation<String> onFileDialogCancel = new AsyncOperation<String>() {

			@Override
			public void callback(final String path) {
				app1.getGgbApi().showTooltip("");
				((DialogManagerW) app1.getDialogManager()).getSaveDialog(false,
						true).hide();
				getApp().dispatchEvent(
						new Event(EventType.EXPORT, null, "[\"ggb\"]"));
			}
		};
		final AsyncOperation<String> onFileDialogClosed = new AsyncOperation<String>() {

			@Override
			public void callback(final String path) {
				onFileDialogCancel.callback(path);
			}
		};

		((AppW) app1).getGgbApi().getBase64(true, new AsyncOperation<String>() {

			@Override
			public void callback(final String data) {
				saveDialog(data, title1, onFileDialogClosed,
						onFileDialogCancel);
			}
			});
	}

	/**
	 * @param data
	 *            base64
	 * @param title
	 *            title
	 * @param onSuccess
	 *            success handler
	 * @param onFailure
	 *            failure handler
	 */
	native void saveDialog(String data, String title,
			AsyncOperation<String> onSuccess,
			AsyncOperation<String> onFailure)/*-{
		var that = this;
		if ($wnd.android && $wnd.android.callPlugin) {
			$wnd.android
					.callPlugin(
							'SaveDialog',
							[ data, title, 'ggb' ],
							function(path) {
								onSuccess.@org.geogebra.common.util.AsyncOperation::callback(*)(path);
							},
							function(path) {
								onFailure.@org.geogebra.common.util.AsyncOperation::callback(*)(path);
							});
		}
	}-*/;

	@Override
	public boolean hasBase64(Material material) {
		return true;
	}

	@Override
	public void showExportAsPictureDialog(String url, String filename,
			String extension, String titleKey, App app1) {
		exportImage(url, filename, extension);
		// TODO check if it really happened
		app.dispatchEvent(
				new Event(EventType.EXPORT, null, "[\"" + extension + "\"]"));

	}

	@Override
	public void refreshAutosaveTimestamp() {
		// no autosave timestamp
	}

}