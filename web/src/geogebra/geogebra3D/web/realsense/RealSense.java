package geogebra.geogebra3D.web.realsense;

import geogebra.common.main.App;
import geogebra.html5.js.ResourcesInjector;
import geogebra.html5.util.AsyncCallback;
import geogebra.html5.util.JSON;

import com.google.gwt.core.client.JavaScriptObject;

public class RealSense {

	public static void initIfSupported() {
		AsyncCallback clb = new AsyncCallback() {

			public void onSuccess(JavaScriptObject result) {
	           boolean isReady = JSON.getAsBoolean(result, "isReady");
	           if (isReady) {
	        	   App.debug("Hurray, Supported! " + JSON.get(result, "msg"));
	        	   ResourcesInjector.injectRealSenseResources();
	        	   
	           } else {
	        	   App.debug("Sadly, not Supported! " + JSON.get(result, "msg"));
	           }
            }
			
			
		};
		
		RealSenseInfo.isSupported(clb);
    }
	
	public static native void createInstance() /*-{
		$wnd.PXCMSenseManager_CreateInstance().then(function (result) {
		$wnd.sense = result;
		return $wnd.sense.EnableHand(onHandData);
	}).then(function (result) {
		@geogebra.html5.main.AppW::debug(Ljava/lang/String;)('Init started');
		return $wnd.sense.Init(onConnect);
	}).then(function (result) {
		return $wnd.sense.CreateHandConfiguration();
	}).then(function (result) {
		handConfiguration = result;
		return handConfiguration.EnableAllAlerts();
	}).then(function (result) {
		return handConfiguration.EnableAllGestures(false);
	 }).then(function (result) {
		return handConfiguration.ApplyChanges();
	}).then(function (result) {
		return $wnd.sense.QueryCaptureManager();
	}).then(function (result) {
		capture = result;
		return capture.QueryImageSize(capture.STREAM_TYPE_DEPTH);
	}).then(function (result) {
		imageSize = result.size;
		return $wnd.sense.StreamFrames();
	}).then(function (result) {
		@geogebra.html5.main.AppW::debug(Ljava/lang/String;)('Streaming ' + imageSize.width + 'x' + imageSize.height);
	}); //dont compiles because gwt believes it is a try catch block .catch(function (error) {
		//@geogebra.html5.main.AppW::debug(Ljava/lang/String;)('Init failed: ' + JSON.stringify(error));
		//});
	}-*/;
}
