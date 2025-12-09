/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	/**
	 * @param base64 base64 encoded content
	 * @param name name
	 * @param fileType file type (ggb, png, ...)
	 */
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

	/**
	 * @param materialId material ID
	 * @param callbackId callback ID
	 * @param callbackParentId parent callback ID
	 */
	@JsMethod
	public native void getMetaData(int materialId, int callbackId, int callbackParentId);

	@JsMethod
	public native void getBase64(String title, int callbackId);

	/**
	 * @param materialId material ID
	 * @param title title
	 * @param base64 content as base64
	 * @param metaData JSON metadata
	 * @param callbackId callback ID
	 */
	@JsMethod
	public native void saveFile(int materialId, String title, String base64,
			String metaData, int callbackId);

	/**
	 * @param title title
	 * @param base64 base64 encoded content
	 * @param metaData JSON metadata
	 */
	@JsMethod
	public native void createFileFromTube(String title, String base64, String metaData);

	/**
	 * @param title title
	 * @param base64 base64 encoded content
	 * @param metaData JSON metadata
	 */
	@JsMethod
	public native void updateFileFromTube(String title, String base64, String metaData);

	@JsMethod
	public native void openUrlInBrowser(String url);

	/**
	 * @param oldKey old key
	 * @param newKey new key
	 * @param metaData JSON metadata
	 * @param callbackId callback ID
	 */
	@JsMethod
	public native void rename(String oldKey, String newKey, String metaData, int callbackId);

	/**
	 * @param key key
	 * @param metaData JSON metadata
	 * @param callbackId callback ID
	 */
	@JsMethod
	public native void overwriteMetaData(String key, String metaData, int callbackId);

	/**
	 * @param key key
	 * @param callbackId callback ID
	 */
	@JsMethod
	public native void deleteGgb(String key, int callbackId);

	/**
	 * @param message debug message
	 */
	@JsMethod
	public native void debug(String message);

	/**
	 * @param materialId material ID
	 * @param title title
	 * @param token login token
	 */
	@JsMethod
	public native void open(int materialId, String title, String token);
}
