package org.geogebra.web.geogebra3D.web.realsense;

import org.geogebra.common.main.App;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianController3DW;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.util.AsyncCallback;
import org.geogebra.web.html5.util.JSON;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author gabor
 * 
 *         Class for making RealSense api working
 *
 */
public class RealSense {

	protected static JavaScriptObject sense;
	protected static JavaScriptObject handConfiguration;
	protected static JavaScriptObject capture;
	protected static JavaScriptObject imageSize;
	protected static EuclidianController3DW controller;

	/**
	 * inits if supported
	 * 
	 * @param euclidianController3DW
	 */
	public static void initIfSupported(
	        final EuclidianController3DW euclidianController3DW) {
		AsyncCallback clb = new AsyncCallback() {

			public void onSuccess(JavaScriptObject result) {
				boolean isReady = JSON.getAsBoolean(result, "isReady");
				if (isReady) {
					App.debug("Hurray, Supported! " + JSON.get(result, "msg"));
					ResourcesInjector.injectRealSenseResources();
					RealSense.controller = euclidianController3DW;
				} else {
					App.debug("Sadly, not Supported! "
					        + JSON.get(result, "msg"));
				}
			}

		};

		RealSenseInfo.isSupported(clb);
	}

	/**
	 * creates instance
	 */
	public static native void createInstance() /*-{
		$wnd
				.PXCMSenseManager_CreateInstance()
				.then(
						function(result) {
							@org.geogebra.web.geogebra3D.web.realsense.RealSense::sense = result;
							return @org.geogebra.web.geogebra3D.web.realsense.RealSense::sense
									.EnableHand(function(mid, module, data) {
										@org.geogebra.web.geogebra3D.web.realsense.RealSense::onHandData(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(mid, module, data);
									});
						})
				.then(
						function(result) {
							@org.geogebra.web.html5.main.AppW::debug(Ljava/lang/String;)('Init started');
							return @org.geogebra.web.geogebra3D.web.realsense.RealSense::sense
									.Init(onConnect);
						})
				.then(
						function(result) {
							return @org.geogebra.web.geogebra3D.web.realsense.RealSense::sense
									.CreateHandConfiguration();
						})
				.then(
						function(result) {
							@org.geogebra.web.geogebra3D.web.realsense.RealSense::handConfiguration = result;
							return @org.geogebra.web.geogebra3D.web.realsense.RealSense::handConfiguration
									.EnableAllAlerts();
						})
				.then(
						function(result) {
							return @org.geogebra.web.geogebra3D.web.realsense.RealSense::handConfiguration
									.EnableAllGestures(false);
						})
				.then(
						function(result) {
							return @org.geogebra.web.geogebra3D.web.realsense.RealSense::handConfiguration
									.ApplyChanges();
						})
				.then(
						function(result) {
							return @org.geogebra.web.geogebra3D.web.realsense.RealSense::sense
									.QueryCaptureManager();
						})
				.then(
						function(result) {
							@org.geogebra.web.geogebra3D.web.realsense.RealSense::capture = result;
							return capture
									.QueryImageSize(capture.STREAM_TYPE_DEPTH);
						})
				.then(
						function(result) {
							@org.geogebra.web.geogebra3D.web.realsense.RealSense::imageSize = result.size;
							return @org.geogebra.web.geogebra3D.web.realsense.RealSense::sense
									.StreamFrames();
						})
				.then(
						function(result) {
							@org.geogebra.web.html5.main.AppW::debug(Ljava/lang/String;)('Streaming ' + @org.geogebra.web.geogebra3D.web.realsense.RealSense::imageSize.width + 'x' + @org.geogebra.web.geogebra3D.web.realsense.RealSense::imageSize.height);
						}); //dont compiles because gwt believes it is a try catch block .catch(function (error) {
		//@org.geogebra.web.html5.main.AppW::debug(Ljava/lang/String;)('Init failed: ' + JSON.stringify(error));
		//});
	}-*/;

	private native void onConnect(JavaScriptObject device, JavaScriptObject data) /*-{
	                                                                              if (data.connected == false) {
	                                                                              @org.geogebra.web.geogebra3D.web.realsense.RealSense::status(Ljava/lang/String;)('Alert: ' + JSON.stringify(data));
	                                                                              }
	                                                                              
	                                                                              }-*/;

	private static void status(String status) {
		App.debug(status);
	}

	private static native void onHandData(JavaScriptObject mid,
	        JavaScriptObject module, JavaScriptObject data) /*-{
		if (data.hands === undefined) {
			return;
		}

		var hand = data.hands[0];
		if (hand === undefined) {
			return;
		}

		var name;
		if (data.gestures === undefined) {
			name = "";
		} else {
			if (data.gestures[0] === undefined) {
				name = "";
			} else {
				name = data.gestures[0].name;
			}
		}

		var hv = {
			mx : hand.massCenterWorld.x,
			my : hand.massCenterWorld.y,
			mz : hand.massCenterWorld.z,
			ox : hand.palmOrientation.x,
			oy : hand.palmOrientation.y,
			oz : hand.palmOrientation.z,
			ow : hand.palmOrientation.w,
			gesture : name
		};

		@org.geogebra.web.geogebra3D.web.realsense.RealSense::processHandValues(IIIIIIILjava/lang/String;)(hv.mx, hv.my, hv.mz, hv.ox, hv.ox, hv.oy, hv.oz, hv.ow, hv.name);
	}-*/;

	/**
	 * @param mx
	 * @param my
	 * @param mz
	 * @param ox
	 * @param oy
	 * @param oz
	 * @param ow
	 * @param name
	 */
	protected static void processHandValues(int mx, int my, int mz, int ox,
	        int oy, int oz, int ow, String name) {
		controller.onHandValues(mx, my, mz, ox, oy, oz, ow, name);
	}

	/**
	 * closes realsense.
	 */
	public static native void realSenseClose() /*-{
		@org.geogebra.web.geogebra3D.web.realsense.RealSense::sense
				.Close()
				.then(
						function(result) {
							@org.geogebra.web.geogebra3D.web.realsense.RealSense::status(Ljava/lang/String;)('Stopped');
						});
	}-*/;
}
