package geogebra.web.helper;

import geogebra.web.jso.JsUint8Array;

public interface FileLoadCallback {

	void onError(String errorMessage);

	void onSuccess(JsUint8Array zippedContent);

}
