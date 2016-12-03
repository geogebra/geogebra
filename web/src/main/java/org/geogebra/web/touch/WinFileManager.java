package org.geogebra.web.touch;

import java.util.ArrayList;

import org.geogebra.common.move.ggtapi.models.JSONParserGGT;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.MaterialFilter;
import org.geogebra.common.move.ggtapi.models.SyncEvent;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.web.gui.browser.BrowseGUI;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.main.FileManager;
import org.geogebra.web.web.util.SaveCallback;


public class WinFileManager extends FileManager {
	private static final String META_PREFIX = "meta_";
	private static final String GGB_DIR = "GeoGebra";
	private static final String META_DIR = "meta";
	private static final String FILE_EXT = ".ggb";

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

	public void saveLoggedOut(AppW app) {
		((DialogManagerW) app.getDialogManager()).showSaveDialog();
	}

	@Override
	public void rename(String newTitle, Material mat, Runnable callback) {
		renameNative(FileManager.getFileKey(mat), newTitle, mat.getModified()
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

		final Material mat = WinFileManager.this.createMaterial("", modified);
		String meta = mat.toJson().toString();
		WinFileManager.this.doSave(base64, getApp().getLocalID(), getApp()
		        .getKernel().getConstruction().getTitle(), meta,
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
		nativeUploadUsersMaterials(new StringHandler() {

			@Override
			public void handle(String jsString) {
				JSONTokener tok = new JSONTokener(jsString);
				try {
				JSONObject jv = new JSONObject(tok);
				for (String key : jv.keySet()) {
					Material mat = JSONParserGGT.prototype.toMaterial(
							(JSONObject) jv.get(key));
					mat.setLocalID(FileManager.getIDFromKey(key));
					if (getApp().getLoginOperation().owns(mat)) {

						sync(mat, events);

					}

				}
				} catch (Exception e) {

				}
			}
		});

	};

	/**
	 * @param sh
	 */
	public native void nativeUploadUsersMaterials(StringHandler sh) /*-{
		var that = this;
		if ($wnd.android && $wnd.android.getFiles) {
			$wnd.android
					.getFiles(function(jsString) {
						sh.@org.geogebra.web.html5.main.StringHandler::handle(Ljava/lang/String;)(jsString);
					});
		}
	}-*/;


	private void addMaterials(String jsString) {
		JSONTokener tok = new JSONTokener(jsString);
		try {
			JSONObject jv = new JSONObject(tok);
			for (String key : jv.keySet()) {
				Material mat = JSONParserGGT.prototype
						.toMaterial((JSONObject) jv.get(key));
				mat.setLocalID(FileManager.getIDFromKey(key));
				this.addMaterial(mat);
			}
		} catch (Exception e) {

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
	
	public void export(final AppW app) {
		final String title1 = app.getExportTitle();
		app.getGgbApi().showTooltip(app.getLocalization().getMenu("Saving"));
		final StringHandler onFileDialogCancel = new StringHandler() {

			@Override
			public void handle(final String path) {
				app.getGgbApi().showTooltip("");
				((DialogManagerW) app.getDialogManager()).getSaveDialog()
						.hide();
				getApp().dispatchEvent(
						new Event(EventType.EXPORT, null, "[\"ggb\"]"));
			}
		};
		final StringHandler onFileDialogClosed = new StringHandler() {

			@Override
			public void handle(final String path) {
				onFileDialogCancel.handle(path);
			}
		};

		app.getGgbApi().getBase64(true, new StringHandler(){

			@Override
			public void handle(final String data) {
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
 StringHandler onSuccess,
			StringHandler onFailure)/*-{
		var that = this;
		if ($wnd.android && $wnd.android.callPlugin) {
			$wnd.android
					.callPlugin(
							'SaveDialog',
							[ data, title, 'ggb' ],
							function(path) {
								onSuccess.@org.geogebra.web.html5.main.StringHandler::handle(Ljava/lang/String;)(path);
							},
							function(path) {
								onFailure.@org.geogebra.web.html5.main.StringHandler::handle(Ljava/lang/String;)(path);
							});
		}
	}-*/;
	
	public native void exportImage(String url, String title)/*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			$wnd.android.callPlugin('SaveDialog', [
					url.substring(url.indexOf(',') + 1), title, 'png' ]);
		}
	}-*/;

	public boolean hasBase64(Material material) {
		return true;
	}

	public void showExportAsPictureDialog(String url, String filename, AppW app) {
		exportImage(url, filename);
		// TODO check if it really happened
		app.dispatchEvent(new Event(EventType.EXPORT, null, "[\"png\"]"));

	}

	public void refreshAutosaveTimestamp() {
		// TODO Auto-generated method stub

	}

}