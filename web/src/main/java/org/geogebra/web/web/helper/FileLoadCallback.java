package org.geogebra.web.web.helper;

import org.geogebra.web.web.jso.JsUint8Array;

public interface FileLoadCallback {

	void onError(String errorMessage);

	void onSuccess(JsUint8Array zippedContent);

}
