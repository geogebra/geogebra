package org.geogebra.web.geogebra3D.web.realsense;

import org.geogebra.web.html5.util.AsyncCallback;

public class RealSenseInfo {

	/**
	 * @param clb
	 *            callback
	 */
	public static native void isSupported(AsyncCallback clb) /*-{
		var result = {};
		if (typeof $wnd.RealSenseInfo !== 'function') {
			result.msg = "RealSenseInfo Library not loaded";
			result.isReady = false;
			clb.@org.geogebra.web.html5.util.AsyncCallback::onSuccess(Lcom/google/gwt/core/client/JavaScriptObject;)(result);

			return false;
		}

		$wnd
				.RealSenseInfo(
						[ 'hand' ],
						function(info) {
							if (info.IsReady == true) {
								result.msg = 'Platform supports Intel� RealSense� SDK feature';
								result.statusText = 'OK';
								result.isReady = true;
								clb.@org.geogebra.web.html5.util.AsyncCallback::onSuccess(Lcom/google/gwt/core/client/JavaScriptObject;)(result);
								return true;

							} else {
								result.statusText = 'Platform not supported: '
										+ info.responseText
								result.msg = 'Platform not supported: ';
								if (info.IsPlatformSupported != true) {
									result.msg = 'Intel� RealSense� 3D camera not found';
								} else if (info.IsBrowserSupported != true) {
									result.msg = 'Please update your browser to latest version';
								} else {
									result.msg = 'Please download and install the following update(s) before running sample:';
									for (i = 0; i < info.Updates.length; i++) {
										resut.mgs = result.msg + '<a href="'
												+ info.Updates[i].url + '">'
												+ info.Updates[i].href
												+ '</a><br>';
									}
								}
								result.isReady = false;
								clb.@org.geogebra.web.html5.util.AsyncCallback::onSuccess(Lcom/google/gwt/core/client/JavaScriptObject;)(result);
								return false;
							}

						});

	}-*/;

}
