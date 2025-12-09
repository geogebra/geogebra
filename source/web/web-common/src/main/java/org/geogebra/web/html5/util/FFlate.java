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

package org.geogebra.web.html5.util;

import elemental2.core.Uint8Array;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public final class FFlate {

	/** Callback for compression */
	@JsFunction
	public interface ZipCallback {
		/**
		 * @param err (optional) error
		 * @param data zipped file
		 */
		void call(Object err, Uint8Array data);
	}

	/** Callback for decompression */
	@JsFunction
	public interface UnzipCallback {
		/**
		 * @param err (optional) error
		 * @param data map {file name &#8594; file content}
		 */
		void call(Object err, JsPropertyMap<Uint8Array> data);
	}

	private FFlate() {
		// use FFlate.get() instead, may return null
	}

	@JsProperty(name = "fflate")
	public static native FFlate get();

	public native Uint8Array zipSync(JsPropertyMap<Object> fflatePrepared);

	public native JsPropertyMap<Uint8Array> unzipSync(Uint8Array uint8Array);

	public native void zip(JsPropertyMap<Object> fflatePrepared, ZipCallback callback);

	public native void unzip(Object bytes, UnzipCallback callback);

	public native Uint8Array strToU8(String str);

	public native String strFromU8(Uint8Array obj);

}
