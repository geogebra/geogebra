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

package org.geogebra.web.full.move.googledrive.api;

import org.geogebra.common.util.InjectJsInterop;
import org.geogebra.gwtutil.JsConsumer;

import elemental2.core.JsArray;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = "google.picker", name = "PickerBuilder")
public class GooglePickerBuilder {

	public native GooglePickerBuilder addView(Object view);

	public native GooglePickerBuilder setOAuthToken(String token);

	public native GooglePickerBuilder setCallback(JsConsumer<PickerCallbackParam> data);

	public native GooglePicker build();

	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class PickerCallbackParam {
		@InjectJsInterop public String action;
		@InjectJsInterop public JsArray<GoogleDriveDocument> docs;
	}
}
