package org.geogebra.web.tablet;

import java.util.ArrayList;
import java.util.TreeMap;

import org.geogebra.common.main.Feature;
import org.geogebra.common.main.MaterialsManager;
import org.geogebra.common.move.ggtapi.models.JSONParserGGT;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialFilter;
import org.geogebra.common.move.ggtapi.models.SyncEvent;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.touch.FileManagerT;
import org.geogebra.web.web.util.SaveCallback;

import com.google.gwt.core.client.Callback;

public class TabletFileManager extends FileManagerT {
	
	private enum ReadMetaDataMode { NONE, GET_FILES, UPLOAD_USERS_MATERIALS};
	private ReadMetaDataMode readMetaDataMode;
	
	private static int NO_CALLBACK = 0;
	private TreeMap<Integer, Callback<Object, Object>> callbacks;
	private int callbacksCount = NO_CALLBACK;

	public TabletFileManager(AppW tabletApp) {
		super(tabletApp);
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			callbacks = new TreeMap<Integer, Callback<Object, Object>>();
			exportJavascriptMethods();
			readMetaDataMode = ReadMetaDataMode.NONE;
		}
	}
	
	private int addNewCallback(Callback<Object, Object> callback){
		callbacksCount++;
		callbacks.put(callbacksCount, callback);
		return callbacksCount;
	}
	
	private void runCallback(int id, boolean success, Object result){
		if (id != NO_CALLBACK){
			Callback<Object, Object> cb = callbacks.remove(id);
			if (success){
				cb.onSuccess(result);
			}else{
				cb.onFailure(result);
			}
		}
	}
	
	@Override
	protected void getFiles(final MaterialFilter filter) {
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			getFilesFilter = filter;
			readMetaDataMode = ReadMetaDataMode.GET_FILES;
			listLocalFilesNative();
		}else{
			super.getFiles(filter);
		}
	}
	
	private MaterialFilter getFilesFilter = null;
	
	private native void listLocalFilesNative() /*-{
		if ($wnd.android) {
			$wnd.android.listLocalFiles();
		}
	}-*/;
	
	private int localFilesLength;
	
	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchListLocalFiles(int length) {
		debugNative("catchListLocalFiles: "+length+", mode: "+readMetaDataMode);
		if (length > 0){
			localFilesLength = length;
			if (readMetaDataMode == ReadMetaDataMode.UPLOAD_USERS_MATERIALS){
				setNotSyncedFileCount(localFilesLength, uploadUsersMaterialsEvents);
			}
			getMetaDatasNative();
		} else {
			localFilesLength = 0;
			stopCatchingMetaDatas();
			setNotSyncedFileCount(0, uploadUsersMaterialsEvents);
		}
	}
	
	final private void checkStopCatchingMetaDatas(){
		if (localFilesLength <= 0){
			stopCatchingMetaDatas();
		}
	}
	
	final private void stopCatchingMetaDatas(){
		debugNative("catching meta datas: stop -- mode: "+readMetaDataMode);
//		if (readMetaDataMode == ReadMetaDataMode.UPLOAD_USERS_MATERIALS){
//			ignoreNotSyncedFile(uploadUsersMaterialsEvents);
//		}
//		readMetaDataMode = ReadMetaDataMode.NONE;
	}
	
	private native void getMetaDatasNative() /*-{
		if ($wnd.android) {
			$wnd.android.getMetaDatas();
		}
	}-*/;
	
	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchMetaDatas(String name, String data) {
		localFilesLength--;
		debugNative("ok catching "+name+", still "+localFilesLength+" to catch ("+readMetaDataMode+" mode)");
		switch(readMetaDataMode){
		case GET_FILES:
			try {
				Material mat = JSONParserGGT.prototype.toMaterial(new JSONObject(data));

				if (mat == null) {
					mat = new Material(
							0,
							MaterialType.ggb);
					mat.setTitle(getTitleFromKey(name));
				}

				mat.setLocalID(MaterialsManager.getIDFromKey(name));

				if (getFilesFilter.check(mat)) {
					addMaterial(mat);
					Log.debug("add material: "+name+", id: "+mat.getLocalID());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				checkStopCatchingMetaDatas();
			}
			break;
		case UPLOAD_USERS_MATERIALS:
			try {
				debugNative("sync "+name+", still "+localFilesLength);
				Material mat = JSONParserGGT.prototype.toMaterial(new JSONObject(data));
				mat.setLocalID(MaterialsManager.getIDFromKey(name));
				sync(mat, uploadUsersMaterialsEvents);
			} catch (JSONException e) {
				ignoreNotSyncedFile(uploadUsersMaterialsEvents);
				e.printStackTrace();
			} finally {
				checkStopCatchingMetaDatas();
			}
			break;
		case NONE:
		default:
			// do nothing (should not happen)
			break;
		}
	}
	
	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchMetaDatasError() {
		localFilesLength--;
		debugNative("error catching meta data, still "+localFilesLength+" to catch ("+readMetaDataMode+" mode)");
		if (readMetaDataMode == ReadMetaDataMode.UPLOAD_USERS_MATERIALS){
			ignoreNotSyncedFile(uploadUsersMaterialsEvents);
		}
		checkStopCatchingMetaDatas();
	}
	
	
	@Override
    public void openMaterial(final Material material) {
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			String fileName = getFileKey(material);
			debug("openMaterial: "+fileName+", id: "+material.getLocalID());
			int callback = addNewCallback(new Callback<Object, Object>() {
				public void onSuccess(Object result){
					material.setBase64((String) result);
					doOpenMaterial(material);
				}
				public void onFailure(Object result){
					// not needed
				}
			});
			getBase64(fileName, callback);
		}else{
			super.openMaterial(material);
		}
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
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			Material material = createMaterial("", modified);
			material.setBase64("");
			final Material saveFileMaterial = material;
			int callback;
			if (cb != null){
				callback = addNewCallback(new Callback<Object, Object>() {
					public void onSuccess(Object result){
						saveFileMaterial.setLocalID((Integer) result);
						cb.onSaved(saveFileMaterial, true);
					}
					public void onFailure(Object result){
						cb.onError();
					}
				});
			}else{
				callback = NO_CALLBACK;
			}
			saveFileNative(getApp().getLocalID(), getTitleWithoutReservedCharacters(getApp()
			        .getKernel().getConstruction().getTitle()),base64, saveFileMaterial.toJson().toString(), callback);
		}else{
			super.saveFile(base64, modified, cb);
		}
	}
	
	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchSaveFileResult(int result, int cb) {
		runCallback(cb, result > 0, result);	
	}
			
	
	private native void saveFileNative(int id, String title, String base64, String metaDatas, int callback) /*-{
		if ($wnd.android) {
			$wnd.android.saveFile(id, title, base64, metaDatas, callback);
		}
	}-*/;
	
	
	private ArrayList<SyncEvent> uploadUsersMaterialsEvents;
	
	@Override
	public void uploadUsersMaterials(final ArrayList<SyncEvent> events) {
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			debugNative("uploadUsersMaterials");
			uploadUsersMaterialsEvents = events;
			readMetaDataMode = ReadMetaDataMode.UPLOAD_USERS_MATERIALS;
			listLocalFilesNative();
		} else {
			super.uploadUsersMaterials(events);
		}
		
	}
	
	
	@Override
	protected void updateFile(final String key, final long modified,
	        final Material material) {
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			debugNative("update file: "+material.getTitle());
			material.setModified(modified);
			if (key == null){
				debugNative("key is null");
				// save as a new local file
				String base64 = material.getBase64();
				material.setBase64("");
				createFileFromTubeNative(getTitleWithoutReservedCharacters(material.getTitle()), 
						base64, material.toJson().toString());
			} else {
		        material.setLocalID(MaterialsManager.getIDFromKey(key));
		        String newKey = MaterialsManager.createKeyString(material.getLocalID(), material.getTitle());
		        if (key.equals(newKey)) {
		        	debugNative("key == newKey");
		        	// re-save file and meta data
		        	String base64 = material.getBase64();
		        	material.setBase64("");
		        	updateFileFromTubeNative(key, base64, material.toJson().toString());
		        } else {
		        	String newTitle = material.getTitle();
		        	material.setTitle(MaterialsManager.getTitleFromKey(key));
		        	material.setSyncStamp(material.getModified());
		        	debugNative("key != newKey");
		        	// save and rename
		        	debugNative("rename: "+newTitle);
		        }
			}
		} else {
			super.updateFile(key, modified, material);
		}
	}
	
	private native void createFileFromTubeNative(String title, String base64, String metaDatas) /*-{
		if ($wnd.android) {
			$wnd.android.createFileFromTube(title, base64, metaDatas);
		}
	}-*/;
	
	private native void updateFileFromTubeNative(String title, String base64, String metaDatas) /*-{
		if ($wnd.android) {
			$wnd.android.updateFileFromTube(title, base64, metaDatas);
		}
	}-*/;
	
	
	
	@Override
	public void open(String url, String name, String features){
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			openUrlInBrowser(url, name, features);
		} else {
			super.open(url, name, features);
		}
	}
	
	@Override
	public void open(String url){
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			openUrlInBrowser(url, "", "");
		} else {
			super.open(url);
		}
	}
	
	private native void openUrlInBrowser(String url, String name, String features) /*-{
		if ($wnd.android) {
			$wnd.android.openUrlInBrowser(url, name, features);
		}
	}-*/;
	
	
	
	
	@Override
	public void rename(final String newTitle, final Material mat,
	        final Runnable callback) {
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			final String newKey = MaterialsManager.createKeyString(mat.getLocalID(),
					newTitle);
			final String oldKey = getFileKey(mat);
			mat.setBase64("");
			mat.setTitle(newTitle);
			renameNative(oldKey, newKey, mat.toJson().toString());
		} else {
			super.rename(newTitle, mat, callback);
		}		
	}
	
	private native void renameNative(String oldKey, String newKey, String metaData) /*-{
		if ($wnd.android) {
			$wnd.android.rename(oldKey, newKey, metaData);
		}
	}-*/;

	
	@Override
	public void delete(final Material mat, boolean permanent,
	        final Runnable onSuccess) {
		
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			if (!permanent) {
				mat.setDeleted(true);
				mat.setBase64("");
				overwriteMetaDataNative(getFileKey(mat), mat.toJson().toString());
				return;
			}

			int callback = addNewCallback(new Callback<Object, Object>() {
				public void onSuccess(Object result){
					removeFile(mat);
					onSuccess.run();
				}
				public void onFailure(Object result){
					// not needed
				}
			});
			deleteNative(getFileKey(mat), callback);
		}else{
			super.delete(mat, permanent, onSuccess);
		}

	}
	
	private native void overwriteMetaDataNative(String key, String metaData)/*-{
		if ($wnd.android) {
			$wnd.android.overwriteMetaData(key, metaData);
		}
	}-*/;
	
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
	
		
	
	private native void exportJavascriptMethods() /*-{
		var that = this;
		$wnd.tabletFileManager_catchListLocalFiles = $entry(function(length) {
			that.@org.geogebra.web.tablet.TabletFileManager::catchListLocalFiles(I)(length);
		});
		$wnd.tabletFileManager_catchMetaDatas = $entry(function(name, data) {
			that.@org.geogebra.web.tablet.TabletFileManager::catchMetaDatas(Ljava/lang/String;Ljava/lang/String;)(name,data);
		});
		$wnd.tabletFileManager_catchMetaDatasError = $entry(function() {
			that.@org.geogebra.web.tablet.TabletFileManager::catchMetaDatasError()();
		});
		$wnd.tabletFileManager_catchBase64 = $entry(function(data, callback) {
			that.@org.geogebra.web.tablet.TabletFileManager::catchBase64(Ljava/lang/String;I)(data, callback);
		});
		$wnd.tabletFileManager_catchSaveFileResult = $entry(function(result, callback) {
			that.@org.geogebra.web.tablet.TabletFileManager::catchSaveFileResult(II)(result, callback);
		});
		$wnd.tabletFileManager_catchDeleteResult = $entry(function(data, callback) {
			that.@org.geogebra.web.tablet.TabletFileManager::catchDeleteResult(Ljava/lang/String;I)(data, callback);
		});
	}-*/;
	

	private native void debugNative(String s) /*-{
		if ($wnd.android) {
			$wnd.android.debug(s);
		}
	}-*/;
	
	protected void debug(String s){
		Log.debug(s);
		debugNative(s);
	}
}