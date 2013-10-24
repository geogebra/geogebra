package geogebra.html5.util;

import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.operations.OpenFromGGTOperation;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.move.ggtapi.operations.LoginOperationW;
import geogebra.web.main.AppW;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;

/**
 * @author gabor
 *	windowReference for GGT API
 */
public class WindowReference implements EventRenderable {
	
	/**
	 * The window object itself.
	 */
	JavaScriptObject wnd = null;
	/*
	 * To check that window is open or not
	 */
	AnimationHandle requestAnimationFrame;;
	/**
	 * The instance of the opened window. We would like to have only one window opened in a given time
	 */
	static WindowReference instance = null;

	/**
	 * protected constructor as superclass of js object
	 */
	private WindowReference() {
		
	}
	
	public native void close() /*-{
		var wnd = this.@geogebra.html5.util.WindowReference::wnd;
		if (wnd) {
			wnd.close();
		}
	}-*/;
	
	/**
	 * @param app Application
	 * @return reference to this object
	 */
	public static WindowReference createSignInWindow(App app) {
		if (instance == null) {
			instance = new WindowReference();
			int  width = 900;
			int height = 500;
			int left = (Window.getClientWidth() / 2) - (width / 2);
			int top = (Window.getClientHeight() / 2) - (height / 2);
			LoginOperationW lOW = ((LoginOperationW) app.getLoginOperation());
					instance.wnd = WindowW.open(lOW.getOpenerUrl() +
					"?redirect=" +
					lOW.getLoginURL(((AppW) app).getLocalization().getLanguage()) +
					"&callback=" +
					lOW.getCallbackUrl(),
					"GeoGebraTube" ,
						"resizable," +
						"toolbar=no," +
						"location=no," +
						"scrollbars=no, " + 
						"statusbar=no, " +
						"titlebar=no, " + 
						"width=" + width +"," +
						"height=" + height + "," +
						"left=" + left + ", " +
						"top=" + top);	
					lOW.getView().add(instance);
					instance.initClosedCheck();
			}
		
		return instance;
	}
	
	private void initClosedCheck() {
	    requestAnimationFrame = AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
			
			public void execute(double timestamp) {
				if (instance != null && instance.closed()) {
					cleanWindowReferences();
				} else {
					AnimationScheduler.get().requestAnimationFrame(this);
				}
			}
		});
    }

	/**
	 * @return the window instance wrapper
	 */
	public static WindowReference get() {
		return instance;
	}
	
	/**
	 * @return the closed state of the
	 */
	public native boolean closed() /*-{
		var wnd = this.@geogebra.html5.util.WindowReference::wnd;
		if (wnd) {
			return wnd.closed;
		}
		return false;
	}-*/;

    public void renderEvent(BaseEvent event) {
	    if (!this.closed()) {
	    	this.close();
	    	cleanWindowReferences();
	    }	    
    }

	private void cleanWindowReferences() {
	    requestAnimationFrame.cancel();
	    WindowReference.instance = null;
    }

	/**
	 * @param app Application
	 * @return windowReference for the opened window
	 */
	public static WindowReference createOpenFromGGTWidnow(App app) {
		if (instance == null) {
			((AppW) app).initOpenFromGGTEventFlow();
			instance = new WindowReference();
			int  width = 900;
			int height = 500;
			int left = (Window.getClientWidth() / 2) - (width / 2);
			int top = (Window.getClientHeight() / 2) - (height / 2);
			OpenFromGGTOperation oGGT = ((AppW) app).getOpenFromGGTOperation();
					instance.wnd = WindowW.open(oGGT.generateOpenFromGGTURL(OpenFromGGTOperation.APP_TYPE.WEB),
					"GeoGebraTube" ,
						"resizable," +
						"toolbar=no," +
						"location=no," +
						"scrollbars=no, " + 
						"statusbar=no, " +
						"titlebar=no, " + 
						"width=" + width +"," +
						"height=" + height + "," +
						"left=" + left + ", " +
						"top=" + top);	
					oGGT.getView().add(instance);
					instance.initClosedCheck();
			}
		
		return instance;
    }

}
