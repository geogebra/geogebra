package org.geogebra.web.full.gui.bridge;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class GeoGebraJSNativeBridge {
	private GeoGebraJSNativeBridge() {
	}

	@JsProperty(name = "GeoGebraJSNativeBridge")
	public static native GeoGebraJSNativeBridge get();

	@JsProperty
	public native void share(String base64, String name, String fileType);

	@JsProperty
	public native void savePreference(String key, String value);

	@JsProperty
	public native void login();

	@JsProperty
	public native void openFromFileClickedNative();

	@JsProperty
	public native void callPlugin(String action, Object[] argument);

	@JsProperty
	public native void getCameraPictureNative();

	@JsProperty
	public native void listLocalFiles(int callbackId);

	@JsProperty
	public native void getMetaData(int materialId, int callbackId, int callbackParentId);

	@JsProperty
	public native void getBase64(String title, int callbackId);

	@JsProperty
	public native void saveFile(int materialId, String title, String base64,
			String metaDatas, int callbackId);

	@JsProperty
	public native void createFileFromTube(String title, String base64, String metaDatas);

	@JsProperty
	public native void updateFileFromTube(String title, String base64, String metaDatas);

	@JsProperty
	public native void openUrlInBrowser(String url);

	@JsProperty
	public native void rename(String oldKey, String newKey, String metaData, int callbackId);

	@JsProperty
	public native void overwriteMetaData(String key, String metaData, int callbackId);

	@JsProperty
	public native void deleteGgb(String key, int callbackId);

	@JsProperty
	public native void debug(String message);

	@JsProperty
	public native void open(int materialId, String title, String token);
}
