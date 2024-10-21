package org.geogebra.web.full.gui.laf;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.gwtutil.JsConsumer;

import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * LAF for offline chrome apps
 *
 */
public class ChromeLookAndFeel extends GLookAndFeel {

	// https://developer.chrome.com/docs/extensions/reference/api/storage#type-StorageArea
	@JsType(isNative = true)
	private interface ChromeStorage {

		void set(JsPropertyMap<String> val);

		void get(JsConsumer<JsPropertyMap<String>> callback);
	}

	@Override
	public boolean isOfflineExamSupported() {
		return true;
	}

	@Override
	public boolean hasLockedEnvironment() {
		return true;
	}

	@Override
	public boolean hasHeader() {
		return false;
	}

	@Override
	public boolean hasLoginButton() {
		return true;
	}

	@Override
	public String getClientId() {
		return GeoGebraConstants.CHROME_APP_CLIENT_ID;
	}

	@Override
	public boolean supportsGoogleDrive() {
		return false;
	}

	@Override
	public void storeLanguage(String lang) {
		Object local = getStorage();
		if (local != null) {
			Js.<ChromeStorage>uncheckedCast(local).set(JsPropertyMap.of("GeoGebraLangUI", lang));
		}
	}

	@Override
	public Promise<String> loadLanguage() {
		ChromeStorage local =  Js.uncheckedCast(getStorage());
		if (local != null) {
			return new Promise<>((resolve, reject) ->
					local.get(props -> resolve.onInvoke(props.get("GeoGebraLangUI")))
			);
		}
		return Promise.resolve((String) null);
	}

	private Object getStorage() {
		return Js.asPropertyMap(DomGlobal.window).nestedGet("chrome.storage.local");
	}
}
