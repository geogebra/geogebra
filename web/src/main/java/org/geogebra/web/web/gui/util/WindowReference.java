package org.geogebra.web.web.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LoginAttemptEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.html5.util.WindowW;
import org.geogebra.web.web.move.ggtapi.operations.BASEURL;
import org.geogebra.web.web.move.ggtapi.operations.LoginOperationW;

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
	static volatile WindowReference instance = null;

	static volatile LoginOperationW lOW;

	private static Object lock = new Object();

	/**
	 * protected constructor as superclass of js object
	 */
	private WindowReference() {
		
	}
	
	public native void close() /*-{
		var wnd = this.@org.geogebra.web.web.gui.util.WindowReference::wnd;
		if ($wnd.debug) {
			$wnd.debug("closing");
		}
		if (wnd) {
			wnd.close();
		}
	}-*/;
	
	/**
	 * @param app Application
	 * @return reference to this object
	 */
	public static WindowReference createSignInWindow(App app, String callback) {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new WindowReference();
					lOW = ((LoginOperationW) app.getLoginOperation());
				}
			}
			instance.wnd = createWindowReference(
					app.getLocalization().getMenu("GeoGebraMaterials"),
					lOW.getLoginURL(app.getLocalization().getLanguage()),
					callback, 900, 500);
					lOW.getView().add(instance);
					instance.initClosedCheck();
			}
		
		return instance;
	}
	
	private void initClosedCheck() {
	    requestAnimationFrame = AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
			
			public void execute(double timestamp) {
				if (instance != null && instance.closed()) {
					if(lOW != null){
				    	lOW.stayLoggedOut();
				    }
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
		var wnd = this.@org.geogebra.web.web.gui.util.WindowReference::wnd;
		if (wnd) {
			return wnd.closed;
		}
		return false;
	}-*/;

    public void renderEvent(BaseEvent event) {
    	if(event instanceof LoginEvent && !((LoginEvent)event).isSuccessful()){
    		return;
    	}
    	if(event instanceof LoginAttemptEvent){
    		return;
    	}
	    if (!this.closed()) {
	    	this.close();
	    	cleanWindowReferences();
	    }
    }

	void cleanWindowReferences() {
	    requestAnimationFrame.cancel();
		synchronized (lock) {
			WindowReference.instance = null;
			lOW = null;
		}
    }

	private static JavaScriptObject createWindowReference(String name,
			String redirect, String callback, int width, int height) {
		int left = (Window.getClientWidth() / 2) - (width / 2);
		int top = (Window.getClientHeight() / 2) - (height / 2);
		return WindowW.open(BASEURL.getOpenerUrl() +
				
						"?redirect=" + redirect +
				"&callback=" + callback,
						
						name,
						
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
	}

}
