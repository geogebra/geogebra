package geogebra.geogebra3D.web.realsense;

import geogebra.common.main.App;
import geogebra.html5.js.ResourcesInjector;
import geogebra.html5.util.AsyncCallback;
import geogebra.html5.util.JSON;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author gabor
 * 
 * Class for making RealSense api working
 *
 */
public class RealSense {
	
	private static JavaScriptObject sense;
	private static JavaScriptObject handConfiguration;
	private static JavaScriptObject capture;
	private static JavaScriptObject imageSize;
	private static JavaScriptObject handValues;

	/**
	 * inits if supported
	 */
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
	
	/**
	 * creates instance
	 */
	public static native void createInstance() /*-{
		$wnd.PXCMSenseManager_CreateInstance().then(function (result) {
		@geogebra.geogebra3D.web.realsense.RealSense::sense = result;
		return @geogebra.geogebra3D.web.realsense.RealSense::sense.EnableHand(function(mid, module, data) {
			@geogebra.geogebra3D.web.realsense.RealSense::onHandData(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(mid, module, data);
		});
	}).then(function (result) {
		@geogebra.html5.main.AppW::debug(Ljava/lang/String;)('Init started');
		return @geogebra.geogebra3D.web.realsense.RealSense::sense.Init(onConnect);
	}).then(function (result) {
		return @geogebra.geogebra3D.web.realsense.RealSense::sense.CreateHandConfiguration();
	}).then(function (result) {
		@geogebra.geogebra3D.web.realsense.RealSense::handConfiguration = result;
		return @geogebra.geogebra3D.web.realsense.RealSense::handConfiguration.EnableAllAlerts();
	}).then(function (result) {
		return @geogebra.geogebra3D.web.realsense.RealSense::handConfiguration.EnableAllGestures(false);
	 }).then(function (result) {
		return @geogebra.geogebra3D.web.realsense.RealSense::handConfiguration.ApplyChanges();
	}).then(function (result) {
		return @geogebra.geogebra3D.web.realsense.RealSense::sense.QueryCaptureManager();
	}).then(function (result) {
		@geogebra.geogebra3D.web.realsense.RealSense::capture = result;
		return capture.QueryImageSize(capture.STREAM_TYPE_DEPTH);
	}).then(function (result) {
		@geogebra.geogebra3D.web.realsense.RealSense::imageSize = result.size;
		return @geogebra.geogebra3D.web.realsense.RealSense::sense.StreamFrames();
	}).then(function (result) {
		@geogebra.html5.main.AppW::debug(Ljava/lang/String;)('Streaming ' + @geogebra.geogebra3D.web.realsense.RealSense::imageSize.width + 'x' + @geogebra.geogebra3D.web.realsense.RealSense::imageSize.height);
	}); //dont compiles because gwt believes it is a try catch block .catch(function (error) {
		//@geogebra.html5.main.AppW::debug(Ljava/lang/String;)('Init failed: ' + JSON.stringify(error));
		//});
	}-*/;
	
	private native void onConnect(JavaScriptObject device, JavaScriptObject data) /*-{
		if (data.connected == false) {
			@geogebra.geogebra3D.web.realsense.RealSense::status(Ljava/lang/String;)('Alert: ' + JSON.stringify(data));
		}
	
	}-*/;
	
	private static void status(String status) {
		App.debug(status);
	}
	
	private static native void onHandData(JavaScriptObject mid, JavaScriptObject module, JavaScriptObject data) /*-{
		if (data.hands === undefined) { 
			return;
		}
	
		var hand = data.hands[0];
		if (hand === undefined)  {
			return;
		}
	
		var name;
		if (data.gestures === undefined){
			name = "";
		}else{
			if (data.gestures[0] === undefined){
			name = "";
		}else{
			name = data.gestures[0].name;
		}
	}
	
	@geogebra.geogebra3D.web.realsense.RealSense::handValues = {
			mx : hand.massCenterWorld.x, my : hand.massCenterWorld.y, mz : hand.massCenterWorld.z, 
			ox : hand.palmOrientation.x, oy : hand.palmOrientation.y, oz : hand.palmOrientation.z, ow: hand.palmOrientation.w,
			gesture: name
		};
	}-*/;
	
	/**
	 * closes realsense.
	 */
	public static native void realSenseClose() /*-{
		@geogebra.geogebra3D.web.realsense.RealSense::sense.Close().then(function (result) {
			@geogebra.geogebra3D.web.realsense.RealSense::status(Ljava/lang/String;)('Stopped');
		});
	}-*/;
}
