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
import org.geogebra.web.html5.bridge.GeoGebraJSNativeBridge;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.touch.FileManagerT;

import com.google.gwt.core.client.Callback;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType
public class TabletFileManager extends FileManagerT {

	private static TabletFileManager INSTANCE;
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
	@JsIgnore
	public TabletFileManager(AppW tabletApp) {
		super(tabletApp);
		init();
		setInstance();
	}

	protected void init() {
		callbacks = new TreeMap<>();
	}

	public static TabletFileManager getInstance() {
		return INSTANCE;
	}

	private void setInstance() {
		INSTANCE = this;
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

	private void listLocalFilesNative(int callback) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().listLocalFiles(callback);
		}
	}

	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchListLocalFiles(int length, int callback) {
		runCallback(callback, true, length);
	}

	private void getMetaDataNative(int i, int callback, int callbackParent) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().getMetaData(i, callback, callbackParent);
		}
	}

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

	@JsIgnore
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

	private void getBase64(String fileName, int callback) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().getBase64(fileName, callback);
		}
	}

	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchBase64(String data, int callback) {
		runCallback(callback, true, data);
	}

	@JsIgnore
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

	private void saveFileNative(int id, String title, String base64,
			String metaDatas, int callback) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().saveFile(id, title, base64, metaDatas, callback);
		}
	}

	@JsIgnore
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

	private void createFileFromTubeNative(String title, String base64, String metaDatas) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().createFileFromTube(title, base64, metaDatas);
		}
	}

	private void updateFileFromTubeNative(String title, String base64, String metaDatas) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().updateFileFromTube(title, base64, metaDatas);
		}
	}

	@JsIgnore
	@Override
	public void open(String url, String features) {
		openUrlInBrowser(url);
	}

	@JsIgnore
	@Override
	public void open(String url) {
		openUrlInBrowser(url);
	}

	private void openUrlInBrowser(String url) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().openUrlInBrowser(url);
		}
	}

	@JsIgnore
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

	private void renameNative(String oldKey, String newKey,
			String metaData, int callback) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().rename(oldKey, newKey, metaData, callback);
		}
	}

	/**
	 * this method is called through js (see exportJavascriptMethods())
	 *
	 * @param callback
	 *            rename callback
	 */
	public void catchRename(int callback) {
		runCallback(callback, true, null);
	}

	@JsIgnore
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

	private void overwriteMetaDataNative(String key, String metaData, int callback) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().overwriteMetaData(key, metaData, callback);
		}
	}

	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchOverwriteMetaData(int callback) {
		runCallback(callback, true, null);
	}

	private void deleteNative(String key, int callback) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().deleteGgb(key, callback);
		}
	}

	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchDeleteResult(String result, int callback) {
		runCallback(callback, result != null && !"0".equals(result), result);
	}

	@JsIgnore
	@Override
	public void setTubeID(String localID, Material mat) {
		mat.setBase64("");
		overwriteMetaDataNative(localID, mat.toJson().toString(), NO_CALLBACK);
	}

	protected void debug(String s) {
		Log.debug(s);
		debugNative(s);
	}

	private void debugNative(String s) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().debug(s);
		}
	}
}