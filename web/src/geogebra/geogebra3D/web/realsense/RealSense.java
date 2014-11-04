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
	        	   //TODO: continue here!
	           } else {
	        	   App.debug("Sadly, not Supported! " + JSON.get(result, "msg"));
	           }
            }
			
			
		};
		
		RealSenseInfo.isSupported(clb);
    }
}
