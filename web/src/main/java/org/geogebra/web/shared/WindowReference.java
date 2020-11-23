package org.geogebra.web.shared;

import org.geogebra.common.main.App;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LoginAttemptEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.shared.ggtapi.LoginOperationW;
import org.geogebra.web.shared.ggtapi.StaticFileUrls;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.FrameRequestCallback;
import elemental2.dom.HTMLHtmlElement;
import elemental2.dom.Window;

/**
 * @author gabor
 *	windowReference for GGT API
 */
public final class WindowReference implements EventRenderable {
	private static final int WIDTH = 900;
	private static final int HEIGHT = 500;
	/**
	 * The window object itself.
	 */
	Window wnd = null;
	/**
	 * To check that window is open or not
	 */
	int requestAnimationFrame;
	/**
	 * The instance of the opened window. We would like to have only one window
	 * opened in a given time
	 */
	static volatile WindowReference instance = null;

	/**
	 * Login operation
	 */
	static volatile LoginOperationW lOW;

	private final static Object lock = new Object();

	/**
	 * protected constructor as superclass of js object
	 */
	private WindowReference() {
		
	}
	
	/**
	 * Close the window.
	 */
	public void close() {
		if (wnd != null) {
			wnd.close();
		}
	}
	
	/**
	 * @param app
	 *            Application
	 * @param callback
	 *            callback URL param
	 * @return reference to this object
	 */
	public static WindowReference createSignInWindow(App app, String callback) {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new WindowReference();
					lOW = (LoginOperationW) app.getLoginOperation();
				}
			}
			instance.wnd = createWindowReference(
					app.getLocalization().getMenu("GeoGebraMaterials"),
					lOW.getLoginURL(app.getLocalization().getLanguage()),
					callback);
			lOW.getView().add(instance);
			instance.initClosedCheck();
		}
		
		return instance;
	}

	private void initClosedCheck() {
		requestAnimationFrame = DomGlobal.requestAnimationFrame(new FrameRequestCallback() {
			@Override
			public void onInvoke(double timestamp) {
				if (instance != null && instance.closed()) {
					if (lOW != null) {
						lOW.stayLoggedOut();
					}
					cleanWindowReferences();
				} else {
					DomGlobal.requestAnimationFrame(this);
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
	public boolean closed() {
		return wnd != null && wnd.closed;
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent
				&& !((LoginEvent) event).isSuccessful()) {
			return;
		}
		if (event instanceof LoginAttemptEvent) {
			return;
		}
		if (!this.closed()) {
			this.close();
			cleanWindowReferences();
		}
	}

	/**
	 * Remove all global state.
	 */
	void cleanWindowReferences() {
		DomGlobal.cancelAnimationFrame(requestAnimationFrame);
		synchronized (lock) {
			WindowReference.instance = null;
			lOW = null;
		}
	}

	private static Window createWindowReference(String name,
			String redirect, String callback) {
		HTMLHtmlElement documentElement = DomGlobal.document.documentElement;
		int left = (documentElement.clientWidth / 2) - (WIDTH / 2);
		int top = (documentElement.clientHeight / 2) - (HEIGHT / 2);
		String settings = "resizable," + "toolbar=no," + "location=no,"
				+ "statusbar=no, " + "titlebar=no, "
				+ "width=" + WIDTH + "," + "height=" + HEIGHT + "," + "left="
				+ left + ", " + "top=" + top;
		String url = StaticFileUrls.getOpenerUrl()
				+ "?redirect=" + Global.encodeURIComponent(redirect)
				+ "&callback=" + Global.encodeURIComponent(callback);
		return DomGlobal.window.open(url, name, settings);
	}

}
