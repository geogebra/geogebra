package geogebra.geogebra3D.web.realsense;

import geogebra.common.main.App;
import geogebra.html5.js.ResourcesInjector;
import geogebra.html5.util.AsyncCallback;
import geogebra.html5.util.JSON;

import com.google.gwt.core.client.JavaScriptObject;

public class RealSense {
	
	private static JavaScriptObject sense;
	private static JavaScriptObject handConfiguration;
	private static JavaScriptObject capture;
	private static JavaScriptObject imageSize;

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
		var t = this;
		$wnd.PXCMSenseManager_CreateInstance().then(function (result) {
		t.@geogebra.geogebra3D.web.realsense.RealSense::sense = result;
		return t.@geogebra.geogebra3D.web.realsense.RealSense::sense.EnableHand(onHandData);
	}).then(function (result) {
		@geogebra.html5.main.AppW::debug(Ljava/lang/String;)('Init started');
		return t.@geogebra.geogebra3D.web.realsense.RealSense::sense.Init(onConnect);
	}).then(function (result) {
		return t.@geogebra.geogebra3D.web.realsense.RealSense::sense.CreateHandConfiguration();
	}).then(function (result) {
		t.@geogebra.geogebra3D.web.realsense.RealSense::handConfiguration = result;
		return t.@geogebra.geogebra3D.web.realsense.RealSense::handConfiguration.EnableAllAlerts();
	}).then(function (result) {
		return t.@geogebra.geogebra3D.web.realsense.RealSense::handConfiguration.EnableAllGestures(false);
	 }).then(function (result) {
		return t.@geogebra.geogebra3D.web.realsense.RealSense::handConfiguration.ApplyChanges();
	}).then(function (result) {
		return t.@geogebra.geogebra3D.web.realsense.RealSense::sense.QueryCaptureManager();
	}).then(function (result) {
		t.@geogebra.geogebra3D.web.realsense.RealSense::capture = result;
		return capture.QueryImageSize(capture.STREAM_TYPE_DEPTH);
	}).then(function (result) {
		t.@geogebra.geogebra3D.web.realsense.RealSense::imageSize = result.size;
		return t.@geogebra.geogebra3D.web.realsense.RealSense::sense.StreamFrames();
	}).then(function (result) {
		@geogebra.html5.main.AppW::debug(Ljava/lang/String;)('Streaming ' + imageSize.width + 'x' + imageSize.height);
	}); //dont compiles because gwt believes it is a try catch block .catch(function (error) {
		//@geogebra.html5.main.AppW::debug(Ljava/lang/String;)('Init failed: ' + JSON.stringify(error));
		//});
	}-*/;
	
	private native void onConnect(JavaScriptObject device, JavaScriptObject data) /*-{
		if (data.connected == false) {
			this.@geogebra.geogebra3D.web.realsense.RealSense::status(Ljava/lang/String;)('Alert: ' + JSON.stringify(data));
		}
	
	}-*/;
	
	private static void status(String status) {
		App.debug(status);
	}
}
