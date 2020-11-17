package org.geogebra.web.full.move.googledrive.api;

import org.geogebra.common.util.InjectJsInterop;
import org.geogebra.web.html5.util.JsConsumer;

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
