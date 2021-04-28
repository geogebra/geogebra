package org.geogebra.web.tablet;

import java.util.TreeMap;

import org.geogebra.common.main.MaterialsManager;
import org.geogebra.common.move.ggtapi.models.JSONParserGGT;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialFilter;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.util.SaveCallback;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.touch.FileManagerT;

import com.google.gwt.core.client.Callback;

public class TabletFileManager extends FileManagerT {

	private final static int NO_CALLBACK = 0;
	private TreeMap<Integer, MyCallback> callbacks;
	private int callbacksCount = NO_CALLBACK;

	private abstract class MyCallback implements Callback<Object, Object> {
		private int id;

		protected MyCallback() {
			// protected
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	/**
	 * @param tabletApp application
	 */
	public TabletFileManager(AppW tabletApp) {
		super(tabletApp);
		init();
	}

	protected void init() {
		callbacks = new TreeMap<>();
		exportJavascriptMethods();
	}

	protected int addNewCallback(MyCallback callback) {
		callbacksCount++;
		callbacks.put(callbacksCount, callback);
		callback.setId(callbacksCount);
		return callbacksCount;
	}

	private void runCallback(int id, boolean success, Object result) {
		if (id != NO_CALLBACK) {
			MyCallback cb = callbacks.remove(id);
			if (success) {
				cb.onSuccess(result);
			} else {
				cb.onFailure(result);
			}
		}
	}

	@Override
	protected void getFiles(final MaterialFilter filter) {	
		final int callbackParent = addNewCallback(new MyCallback() {
			@Override
			public void onSuccess(Object resultParent) {
				int length = (Integer) resultParent;
				for (int i = 0; i < length; i++) {
					int callback = addNewCallback(new MyCallback() {
						@Override
						public void onSuccess(Object result) {
							try {
								String[] resultStrings = (String[]) result;
								String name = resultStrings[0];
								String data = resultStrings[1];
								Material mat = JSONParserGGT.prototype
										.toMaterial(new JSONObject(data));

								if (mat == null) {
									mat = new Material(0, MaterialType.ggb);
									mat.setTitle(getTitleFromKey(name));
								}

								mat.setLocalID(
										MaterialsManager.getIDFromKey(name));

								if (filter.check(mat)) {
									addMaterial(mat);
								}
							} catch (JSONException e) {
								e.printStackTrace();
								}
							}

						@Override
						public void onFailure(Object err) {
							// not needed
						}
					});
					getMetaDataNative(i, callback, getId());
				}
			}

			@Override
			public void onFailure(Object result) {
				// not needed
			}
		});
		listLocalFilesNative(callbackParent);
	}

	private native void listLocalFilesNative(int callback) /*-{
		if ($wnd.android) {
			$wnd.android.listLocalFiles(callback);
		}
	}-*/;

	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchListLocalFiles(int length, int callback) {
		runCallback(callback, true, length);
	}

	private native void getMetaDataNative(int i, int callback, int callbackParent) /*-{
		if ($wnd.android) {
			$wnd.android.getMetaData(i, callback, callbackParent);
		}
	}-*/;

	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchMetaData(String name, String data, int callback) {
		runCallback(callback, true, new String[] {name, data});
	}

	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchMetaDataError(int callback) {
		runCallback(callback, false, null);
	}

	@Override
	public void openMaterial(final Material material) {
		String fileName = getFileKey(material);
		int callback = addNewCallback(new MyCallback() {
			@Override
			public void onSuccess(Object result) {
				material.setBase64((String) result);
				doOpenMaterial(material);
			}

			@Override
			public void onFailure(Object result) {
				// not needed
			}
		});
		getBase64(fileName, callback);
	}

	private native void getBase64(String fileName, int callback) /*-{
		if ($wnd.android) {
			$wnd.android.getBase64(fileName, callback);
		}
	}-*/;

	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchBase64(String data, int callback) {
		runCallback(callback, true, data);
	}

	@Override
	public void saveFile(final String base64, final long modified,
			final SaveCallback cb) {
		Material material = createMaterial("", modified);
		material.setBase64("");
		final Material saveFileMaterial = material;
		int callback;
		if (cb != null) {
			callback = addNewCallback(new MyCallback() {
				@Override
				public void onSuccess(Object result) {
					saveFileMaterial.setLocalID((Integer) result);
					cb.onSaved(saveFileMaterial, true);
				}

				@Override
				public void onFailure(Object result) {
					cb.onError();
				}
			});
		} else {
			callback = NO_CALLBACK;
		}
		String cleanTitle = getTitleWithoutReservedCharacters(
				getApp().getKernel().getConstruction().getTitle());
		saveFileNative(getApp().getLocalID(), cleanTitle,
				base64, saveFileMaterial.toJson().toString(), callback);
	}

	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchSaveFileResult(int result, int cb) {
		runCallback(cb, result > 0, result);	
	}

	private native void saveFileNative(int id, String title, String base64,
			String metaDatas, int callback) /*-{
		if ($wnd.android) {
			$wnd.android.saveFile(id, title, base64, metaDatas, callback);
		}
	}-*/;

	@Override
	public void upload(final Material mat) {
		int callback = addNewCallback(new MyCallback() {
			@Override
			public void onSuccess(Object result) {
				mat.setBase64((String) result);
				doUpload(mat);
			}

			@Override
			public void onFailure(Object result) {
				// not needed
			}
		});
		getBase64(getFileKey(mat), callback);
	}

	@Override
	protected void updateFile(final String key, final long modified,
			final Material material) {	
		material.setModified(modified);
		if (key == null) {
			// save as a new local file
			String base64 = material.getBase64();
			material.setBase64("");
			createFileFromTubeNative(getTitleWithoutReservedCharacters(material.getTitle()), 
					base64, material.toJson().toString());
		} else {
			material.setLocalID(MaterialsManager.getIDFromKey(key));
			String newKey = MaterialsManager.createKeyString(
					material.getLocalID(), material.getTitle());
			if (key.equals(newKey)) {
				// re-save file and meta data
				String base64 = material.getBase64();
				material.setBase64("");
				updateFileFromTubeNative(key, base64, material.toJson().toString());
			} else {
				String newTitle = material.getTitle();
				material.setTitle(MaterialsManager.getTitleFromKey(key));
				material.setSyncStamp(material.getModified());
				// save and rename
				rename(newTitle, material);
			}
		}
	}

	private native void createFileFromTubeNative(String title, String base64,
			String metaDatas) /*-{
		if ($wnd.android) {
			$wnd.android.createFileFromTube(title, base64, metaDatas);
		}
	}-*/;

	private native void updateFileFromTubeNative(String title, String base64,
			String metaDatas) /*-{
		if ($wnd.android) {
			$wnd.android.updateFileFromTube(title, base64, metaDatas);
		}
	}-*/;

	@Override
	public void open(String url, String features) {
		openUrlInBrowser(url, features);
	}

	@Override
	public void open(String url) {
		openUrlInBrowser(url, "");
	}

	private native void openUrlInBrowser(String url, String features) /*-{
		if ($wnd.android) {
			$wnd.android.openUrlInBrowser(url, "_blank", features);
		}
	}-*/;

	@Override
	public void rename(final String newTitle, final Material mat,
			final Runnable callback) {
		final String newKey = MaterialsManager.createKeyString(mat.getLocalID(),
				newTitle);
		final String oldKey = getFileKey(mat);
		mat.setBase64("");
		mat.setTitle(newTitle);
		int callback1 = addNewCallback(new MyCallback() {			
			@Override
			public void onSuccess(Object result) {
				if (callback != null) {
					callback.run();
				}					
			}

			@Override
			public void onFailure(Object reason) {
				// not needed					
			}
		});
		renameNative(oldKey, newKey, mat.toJson().toString(), callback1);
	}

	private native void renameNative(String oldKey, String newKey,
			String metaData, int callback) /*-{
		if ($wnd.android) {
			$wnd.android.rename(oldKey, newKey, metaData, callback);
		}
	}-*/;

	/**
	 * this method is called through js (see exportJavascriptMethods())
	 * 
	 * @param callback
	 *            rename callback
	 */
	public void catchRename(int callback) {
		runCallback(callback, true, null);
	}

	@Override
	public void delete(final Material mat, boolean permanent,
			final Runnable onSuccess) {

		if (!permanent) {
			mat.setDeleted(true);
			mat.setBase64("");
			overwriteMetaDataNative(getFileKey(mat), mat.toJson().toString(), NO_CALLBACK);
			return;
		}

		int callback = addNewCallback(new MyCallback() {
			@Override
			public void onSuccess(Object result) {
				removeFile(mat);
				onSuccess.run();
			}

			@Override
			public void onFailure(Object result) {
				// not needed
			}
		});
		deleteNative(getFileKey(mat), callback);
	}

	private native void overwriteMetaDataNative(String key, String metaData, int callback)/*-{
		if ($wnd.android) {
			$wnd.android.overwriteMetaData(key, metaData, callback);
		}
	}-*/;

	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchOverwriteMetaData(int callback) {
		runCallback(callback, true, null);
	}

	private native void deleteNative(String key, int callback) /*-{
		if ($wnd.android) {
			$wnd.android.deleteGgb(key, callback);
		}
	}-*/;

	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchDeleteResult(String result, int callback) {
		runCallback(callback, result != null && !"0".equals(result), result);
	}

	@Override
	public void setTubeID(String localID, Material mat) {
		mat.setBase64("");
		overwriteMetaDataNative(localID, mat.toJson().toString(), NO_CALLBACK);
	}

	private native void exportJavascriptMethods() /*-{
		var that = this;
		$wnd.tabletFileManager_catchListLocalFiles = $entry(function(length,
				callback) {
			that.@org.geogebra.web.tablet.TabletFileManager::catchListLocalFiles(II)(length, callback);
		});
		$wnd.tabletFileManager_catchMetaData = $entry(function(name, data,
				callback) {
			that.@org.geogebra.web.tablet.TabletFileManager::catchMetaData(Ljava/lang/String;Ljava/lang/String;I)(name, data, callback);
		});
		$wnd.tabletFileManager_catchMetaDataError = $entry(function(callback) {
			that.@org.geogebra.web.tablet.TabletFileManager::catchMetaDataError(I)(callback);
		});
		$wnd.tabletFileManager_catchBase64 = $entry(function(data, callback) {
			that.@org.geogebra.web.tablet.TabletFileManager::catchBase64(Ljava/lang/String;I)(data, callback);
		});
		$wnd.tabletFileManager_catchSaveFileResult = $entry(function(result,
				callback) {
			that.@org.geogebra.web.tablet.TabletFileManager::catchSaveFileResult(II)(result, callback);
		});
		$wnd.tabletFileManager_catchDeleteResult = $entry(function(data,
				callback) {
			that.@org.geogebra.web.tablet.TabletFileManager::catchDeleteResult(Ljava/lang/String;I)(data, callback);
		});
		$wnd.tabletFileManager_catchRename = $entry(function(callback) {
			that.@org.geogebra.web.tablet.TabletFileManager::catchRename(I)(callback);
		});
		$wnd.tabletFileManager_catchOverwriteMetaData = $entry(function(
				callback) {
			that.@org.geogebra.web.tablet.TabletFileManager::catchOverwriteMetaData(I)(callback);
		});
	}-*/;

	private native void debugNative(String s) /*-{
		if ($wnd.android) {
			$wnd.android.debug(s);
		}
	}-*/;

	protected void debug(String s) {
		Log.debug(s);
		debugNative(s);
	}
}