package org.geogebra.web.web;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.SilentProfiler;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.cas.giac.PNaCl;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationWeb;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.CustomElements;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.web.gui.app.GeoGebraAppFrame;
import org.geogebra.web.web.gui.applet.AppletFactory;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.laf.ExamLookAndFeel;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.gui.laf.OfficeLookAndFeel;
import org.geogebra.web.web.gui.laf.SmartLookAndFeel;
import org.geogebra.web.web.main.BrowserDevice;
import org.geogebra.web.web.main.GDevice;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author apa
 *
 */
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Web implements EntryPoint {



	/**
	 * set true if Google Api Js loaded
	 */
	public void onModuleLoad() {

		if (RootPanel.getBodyElement().getAttribute("data-param-laf") != null
				&& !"".equals(RootPanel.getBodyElement().getAttribute(
						"data-param-laf"))) {
			// loading touch, ignore.
			return;
		}
		Browser.checkFloat64();
		// use GeoGebraProfilerW if you want to profile, SilentProfiler for
		// production
		// GeoGebraProfiler.init(new GeoGebraProfilerW());
		GeoGebraProfiler.init(new SilentProfiler());

		GeoGebraProfiler.getInstance().profile();

		CustomElements.registerGeoGebraWebElement();
		exportGGBElementRenderer();

		// setLocaleToQueryParam();

		run();
		allowRerun();
		// just debug for now
		PNaCl.exportPNaCltoConsole();
		Web.registerSuperdevExceptionHandler();
	}

	private static void run() {

		// native preview handlers independent from app/applet
		// THIS IS THE SAME CODE AS IN Tablet.java!!!
		// maybe better than putting into both GeoGebraFrame / GeoGebraAppFrame
		// it it would even be better to find a common class and put there
		// although I'm not sure it's good to use AppW or something like that
		// for preloading, code block separation GWT cache JavaScript files...
		// edit: maybe put this at the end of this method in production builds?
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				switch (event.getTypeInt()) {
				// AFAIK, mouse events do not fire on touch devices,
				// and touch events do not fire on mouse devices,
				// so this will be okay (except laptops with touch
				// screens, but then also, the event will either be
				// mouse event or touch event, but not both, I think)
				case Event.ONTOUCHSTART:
					if (event.getNativeEvent() != null) {
						DrawEquationWeb.escEditingHoverTapWhenElsewhere(
								event.getNativeEvent(), true);
					}
					break;
				case Event.ONMOUSEDOWN:
					if (event.getNativeEvent() != null) {
						DrawEquationWeb.escEditingHoverTapWhenElsewhere(
								event.getNativeEvent(), false);
					}
					break;
				// this is an addition, only matters in Web.java, not on tablets
				case Event.ONKEYDOWN:
					if (event.getNativeEvent() != null) {
						if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
							// in case ENTER is pressed while the AppWapplet is
							// out of focus, then give the applet the focus
							// back! but which applet? it should have been
							// remembered!
							AppW.giveFocusBack();
						}
					}
				default:
					break;
				}
			}
		});

		if (!ArticleElement.checkAppNeeded()) {
			// we dont want to parse out of the box sometimes...
			if (!calledFromExtension()) {
				loadAppletAsync();
			} else {
				loadExtensionAsync();
			}
		} else {
			loadAppAsync();
		}

	}

	// TODO: what about global preview events?
	// these are an issue even if we register them elsewhere
	// maybe do not register them again in case of rerun?
	// this could be done easily now with a boolean parameter
	private native void allowRerun() /*-{
		$wnd.ggbRerun = function() {
			@org.geogebra.web.web.Web::run()();
		}
	}-*/;

	/**
	 * Registers handler for UnhandledExceptions that are wrapped by GWT by
	 * default
	 */
	public static void registerSuperdevExceptionHandler() {
		com.google.gwt.core.client.GWT
				.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void onUncaughtException(Throwable t) {
						Throwable cause = t;
						while (cause.getCause() != null) {
							cause = cause.getCause();
						}
						log(cause instanceof JavaScriptException
								&& ((JavaScriptException) cause).getThrown() != null ? ((JavaScriptException) cause)
								.getThrown() : cause);
					};

					public native void log(Object t) /*-{
		console && console.log && console.log(t);
	}-*/;

				});
	}

	private static void loadExtensionAsync() {
		// GWT.runAsync(new RunAsyncCallback() {

		// public void onSuccess() {
		ResourcesInjector.injectResources();

		exportArticleTagRenderer();
		// export other methods if needed
		// call the registered methods if any
		GGW_ext_webReady();
		// }

		// public void onFailure(Throwable reason) {
		// TODO Auto-generated method stub

		// }
		// });

	}

	public static void loadAppletAsync() {
		// GWT.runAsync(new RunAsyncCallback() {

		// public void onSuccess() {
		startGeoGebra(ArticleElement.getGeoGebraMobileTags());
		// }

		// ublic void onFailure(Throwable reason) {
		// TODO Auto-generated method stub

		// }
		// });
	}

	private static void loadAppAsync() {
		// GWT.runAsync(new RunAsyncCallback() {

		// public void onSuccess() {
		ResourcesInjector.injectResources();
		createGeoGebraAppFrame(new BrowserDevice());
		// }

		// public void onFailure(Throwable reason) {
		// Log.debug(reason);
		// }
		// });

	}

	/**
	 * create app frame
	 */
	protected static void createGeoGebraAppFrame(GDevice device) {
		new GeoGebraAppFrame(
				Web.getLAF(ArticleElement.getGeoGebraMobileTags()), device,
				(AppletFactory) GWT.create(AppletFactory.class));
	}

	native static void exportArticleTagRenderer() /*-{
		$wnd.GGW_ext.render = $entry(@org.geogebra.web.web.Web::renderArticleElement(Lcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;))
	}-*/;

	private native void exportGGBElementRenderer() /*-{
		$wnd.renderGGBElement = $entry(@org.geogebra.web.web.Web::renderArticleElement(Lcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;))
		@org.geogebra.web.html5.gui.GeoGebraFrame::renderGGBElementReady()();
		//CRITICAL: "window" below is intentional, the point is to redirect messages from window to $wnd
		window.addEventListener("message",function(event){$wnd.postMessage(event.data,"*");});
	}-*/;

	private native static boolean calledFromExtension() /*-{
		return (typeof $wnd.GGW_ext !== "undefined");
	}-*/;

	public static void renderArticleElement(Element el, JavaScriptObject clb) {

		GeoGebraFrameBoth.renderArticleElement(el,
				(AppletFactory) GWT.create(AppletFactory.class),
				getLAF(ArticleElement.getGeoGebraMobileTags()), clb);
	}

	/*
	 * This method should never be called. Only copyed to external javascript
	 * files, if we like to use GeoGebraWeb as an library, and call its methods
	 * depending on it is loaded or not.
	 */
	private native void copyThisJsIfYouLikeToUseGeoGebraWebAsExtension() /*-{
		//GGW_ext namespace must be a property of the global scope
		$wnd.GGW_ext = {
			startupFunctions : []
		};

		//register methods that will be called if web is loaded,
		//or if it is loaded, will be called immediately
		//GGW_ext.webReady("render",articleelement);
		GGW_ext.webReady = function(functionName, args) {
			if (typeof GGW_ext[functionName] === "function") {
				//web loaded
				this[functionName].apply(args);
			} else {
				this.startupFunctions.push([ functionName, args ]);
			}
		}
	}-*/;

	private static native void GGW_ext_webReady() /*-{
		var functions = null, i, l;
		if (typeof $wnd.GGW_ext === "object") {
			if ($wnd.GGW_ext.startupFunctions
					&& $wnd.GGW_ext.startupFunctions.length) {
				functions = $wnd.GGW_ext.startupFunctions;
				for (i = 0, l = functions.length; i < l; i++) {
					if (typeof $wnd.GGW_ext[functions[i][0]] === "function") {
						$wnd.GGW_ext[functions[i][0]](functions[i][1]);
					}
				}
			}
		}
	}-*/;

	static void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {

		org.geogebra.web.web.gui.applet.GeoGebraFrameBoth.main(
				geoGebraMobileTags,
				(AppletFactory) GWT.create(AppletFactory.class),
				getLAF(geoGebraMobileTags));

	}

	public static GLookAndFeel getLAF(
			ArrayList<ArticleElement> geoGebraMobileTags) {
		NodeList<Element> nodes = Dom
				.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		for (int i = 0; i < nodes.getLength(); i++) {
			if ("smart".equals(nodes.getItem(i).getAttribute("data-param-laf"))) {
				return new SmartLookAndFeel();
			}

			if ("office"
					.equals(nodes.getItem(i).getAttribute("data-param-laf"))) {
				return new OfficeLookAndFeel();
			}

			if ("exam".equals(nodes.getItem(i).getAttribute("data-param-laf"))) {
				return new ExamLookAndFeel();
			}
		}
		return new GLookAndFeel();

	}

}
