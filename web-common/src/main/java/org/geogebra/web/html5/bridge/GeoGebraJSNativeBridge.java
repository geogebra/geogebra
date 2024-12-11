package org.geogebra.web.html5.bridge;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public final class GeoGebraJSNativeBridge {
	private GeoGebraJSNativeBridge() {
	}

	@JsProperty(name = "GeoGebraJSNativeBridge")
	public static native GeoGebraJSNativeBridge get();

	@JsMethod
	public native void share(String base64, String name, String fileType);

	@JsMethod
	public native void savePreference(String key, String value);

	@JsMethod
	public native void login();

	@JsMethod
	public native void openFromFileClickedNative();

	@JsMethod
	public native void callPlugin(String action, Object[] argument);

	@JsMethod
	public native void getCameraPictureNative();

	@JsMethod
	public native void listLocalFiles(int callbackId);

	@JsMethod
	public native void getMetaData(int materialId, int callbackId, int callbackParentId);

	@JsMethod
	public native void getBase64(String title, int callbackId);

	@JsMethod
	public native void saveFile(int materialId, String title, String base64,
			String metaData, int callbackId);

	@JsMethod
	public native void createFileFromTube(String title, String base64, String metaData);

	@JsMethod
	public native void updateFileFromTube(String title, String base64, String metaData);

	@JsMethod
	public native void openUrlInBrowser(String url);

	@JsMethod
	public native void rename(String oldKey, String newKey, String metaData, int callbackId);

	@JsMethod
	public native void overwriteMetaData(String key, String metaData, int callbackId);

	@JsMethod
	public native void deleteGgb(String key, int callbackId);

	@JsMethod
	public native void debug(String message);

	@JsMethod
	public native void open(int materialId, String title, String token);
}
